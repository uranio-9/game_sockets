package co.edu.uptc.server.model;

import co.edu.uptc.server.config.Constants;
import co.edu.uptc.server.interfaces.ModelInterface;
import co.edu.uptc.server.interfaces.ModelObserver;
import co.edu.uptc.shared.dto.*;
import co.edu.uptc.shared.pojo.Player;
import co.edu.uptc.shared.pojo.Position;
import co.edu.uptc.shared.pojo.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameModel implements ModelInterface, Runnable {

    private ModelObserver observer;
    private final ConcurrentLinkedQueue<MoveCommand> moveQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastMoveTimes = new ConcurrentHashMap<>();

    private int globalGoals = 0;
    private volatile boolean isGameStarted = false;
    private volatile boolean running = true;
    private List<Position> attackerSpawns;
    private List<Position> defenderSpawns;

    private static class MoveCommand {
        final String studentCode;
        final String direction;
        MoveCommand(String code, String dir) { this.studentCode = code; this.direction = dir; }
    }

    public GameModel() {
        initSpawns();
        new Thread(this, "Game-Physics-Thread").start();
    }

    private void initSpawns() {
        attackerSpawns = new ArrayList<>();
        for (int y = 0; y < Constants.GAME_AREA_HEIGHT; y++) attackerSpawns.add(new Position(Constants.GAME_AREA_WIDTH - 1, y));
        defenderSpawns = new ArrayList<>();
        for (int y = 0; y < Constants.GAME_AREA_HEIGHT; y++) defenderSpawns.add(new Position(3, y));
    }

    @Override public void setObserver(ModelObserver o) { this.observer = o; }

    @Override
    public void processConnect(String studentCode) {
        if (players.containsKey(studentCode)) {
            reconnectPlayer(studentCode);
            return;
        }
        addNewPlayer(studentCode);
    }

    private void reconnectPlayer(String studentCode) {
        Player existing = players.get(studentCode);
        send(studentCode, new ConnectAck(true, "Reconnected as " + existing.getRole()));
        send(studentCode, new RoleAssign(existing.getRole().name(), existing.getPosition().getX(), existing.getPosition().getY()));
    }

    private void addNewPlayer(String studentCode) {
        Role role = determineRole();
        Position spawn = findAvailableSpawn(role);
        Player player  = new Player(studentCode, spawn, role);
        players.put(studentCode, player);
        send(studentCode, new ConnectAck(true, "Connected as " + role));
        send(studentCode, new RoleAssign(role.name(), spawn.getX(), spawn.getY()));
        notifyConnect(studentCode, role);
    }

    private void notifyConnect(String studentCode, Role role) {
        if (observer != null) observer.onLogEvent("Player " + studentCode + " connected as " + role);
        broadcastGameState();
    }

    private Role determineRole() {
        long attackers = players.values().stream().filter(p -> p.getRole() == Role.ATTACKER).count();
        long defenders = players.values().stream().filter(p -> p.getRole() == Role.DEFENDER).count();
        return attackers <= defenders ? Role.ATTACKER : Role.DEFENDER;
    }

    @Override
    public void processDisconnect(String studentCode) {
        players.remove(studentCode);
        lastMoveTimes.remove(studentCode);
        if (observer != null) observer.onLogEvent("Player " + studentCode + " disconnected.");
        broadcastGameState();
    }

    @Override
    public void processMove(String studentCode, String direction) {
        if (!isGameStarted) return;
        long now  = System.currentTimeMillis();
        long last = lastMoveTimes.getOrDefault(studentCode, 0L);
        if (now - last < Constants.COOLDOWN_MS) return;
        lastMoveTimes.put(studentCode, now);
        moveQueue.offer(new MoveCommand(studentCode, direction));
    }

    @Override
    public void startGame() {
        if (players.isEmpty()) { 
            if (observer != null) observer.onLogEvent("Cannot start: no players."); 
            return; 
        }
        isGameStarted = true;
        if (observer != null) observer.onLogEvent("Game started.");
        broadcast(new GameStart(1, Constants.GAME_AREA_WIDTH, Constants.GAME_AREA_HEIGHT, Constants.COURT_SIDE));
        broadcastGameState();
    }

    @Override
    public void finishGame() {
        isGameStarted = false;
        running       = false;
        if (observer != null) observer.onLogEvent("Game finished by operator.");
        broadcast(new GameEnd("SERVER_DECISION"));
        if (observer != null) observer.onDisconnectAll();
    }

    @Override
    public void run() {
        while (running) {
            MoveCommand cmd = moveQueue.poll();
            if (cmd != null && isGameStarted) {
                executeMove(cmd);
            } else {
                sleepLoop();
            }
        }
    }

    private void sleepLoop() {
        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void executeMove(MoveCommand cmd) {
        Player player = players.get(cmd.studentCode);
        if (!canPlayerMove(player)) return;
        int[] delta = directionToDelta(cmd.direction);
        if (delta == null) return;
        int newX = player.getPosition().getX() + delta[0];
        int newY = player.getPosition().getY() + delta[1];
        processNewPosition(player, newX, newY);
    }

    private boolean canPlayerMove(Player player) {
        return player != null && player.getScore().getTotal() < Constants.POINTS_TO_WIN;
    }

    private void processNewPosition(Player player, int newX, int newY) {
        if (isOutOfBounds(newX, newY)) return;
        if (isAntiCamperZone(player, newX)) return;
        Player occupant = getPlayerAt(newX, newY);
        if (occupant != null) {
            handleCollision(player, occupant);
            return;
        }
        if (isGoalPosition(player, newX, newY)) {
            handleGoal(player);
            return;
        }
        moveTo(player, newX, newY);
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || x >= Constants.GAME_AREA_WIDTH || y < 0 || y >= Constants.GAME_AREA_HEIGHT;
    }

    private boolean isAntiCamperZone(Player player, int x) {
        return player.getRole() == Role.DEFENDER && x <= 1;
    }

    private void handleCollision(Player player, Player occupant) {
        if (occupant.getRole() == player.getRole()) return;
        Player attacker = player.getRole() == Role.ATTACKER ? player : occupant;
        Player defender = player.getRole() == Role.DEFENDER ? player : occupant;
        handleBlock(attacker, defender);
    }

    private boolean isGoalPosition(Player player, int x, int y) {
        return player.getRole() == Role.ATTACKER && x == 0 && y >= 5 && y <= 9;
    }

    private void moveTo(Player player, int x, int y) {
        player.getPosition().setX(x);
        player.getPosition().setY(y);
        broadcastGameState();
    }

    private void handleBlock(Player attacker, Player defender) {
        if (observer != null) observer.onLogEvent("BLOCK: " + defender.getStudentCode() + " stopped " + attacker.getStudentCode());
        broadcast(new BlockEvent(defender.getStudentCode(), attacker.getStudentCode()));
        defender.getScore().increment();
        send(defender.getStudentCode(), new ScoreUpdate(defender.getStudentCode(), defender.getScore().getTotal(), defender.getRole().name()));
        attacker.setPosition(findAvailableSpawn(Role.ATTACKER));
        checkRoleChange(defender);
        checkWinCondition(defender);
        broadcastGameState();
    }

    private void handleGoal(Player attacker) {
        globalGoals++;
        attacker.getScore().increment();
        if (observer != null) observer.onLogEvent("GOAL: " + attacker.getStudentCode() + " scored!");
        send(attacker.getStudentCode(), new ScoreUpdate(attacker.getStudentCode(), attacker.getScore().getTotal(), attacker.getRole().name()));
        attacker.setPosition(findAvailableSpawn(Role.ATTACKER));
        checkRoleChange(attacker);
        checkWinCondition(attacker);
        broadcastGameState();
    }

    private void checkRoleChange(Player player) {
        if (player.getScore().getPartial() < Constants.ROLE_CHANGE_THRESHOLD) return;
        player.getScore().resetPartial();
        Role newRole = player.getRole() == Role.ATTACKER ? Role.DEFENDER : Role.ATTACKER;
        player.setRole(newRole);
        Position newPos = findAvailableSpawn(newRole);
        player.setPosition(newPos);
        if (observer != null) observer.onLogEvent("ROLE_CHANGE: " + player.getStudentCode() + " → " + newRole);
        broadcast(new RoleChange(player.getStudentCode(), newRole.name(), newPos.getX(), newPos.getY()));
    }

    private void checkWinCondition(Player player) {
        if (player.getScore().getTotal() < Constants.POINTS_TO_WIN) return;
        if (observer != null) observer.onLogEvent("PLAYER_DONE: " + player.getStudentCode());
        broadcast(new PlayerDone(player.getStudentCode()));
        checkAllDone();
    }

    private void checkAllDone() {
        boolean allDone = players.values().stream().allMatch(p -> p.getScore().getTotal() >= Constants.POINTS_TO_WIN);
        if (allDone && isGameStarted) {
            isGameStarted = false;
            if (observer != null) observer.onLogEvent("ALL_DONE — game ending.");
            broadcast(new GameEnd("ALL_DONE"));
            if (observer != null) observer.onDisconnectAll();
        }
    }

    private int[] directionToDelta(String direction) {
        return switch (direction) {
            case "UP"    -> new int[]{ 0, -1};
            case "DOWN"  -> new int[]{ 0,  1};
            case "LEFT"  -> new int[]{-1,  0};
            case "RIGHT" -> new int[]{ 1,  0};
            default      -> null;
        };
    }

    private Player getPlayerAt(int x, int y) {
        for (Player p : players.values())
            if (p.getPosition().getX() == x && p.getPosition().getY() == y) return p;
        return null;
    }

    private Position findAvailableSpawn(Role role) {
        List<Position> spawns = role == Role.ATTACKER ? attackerSpawns : defenderSpawns;
        for (Position s : spawns)
            if (getPlayerAt(s.getX(), s.getY()) == null)
                return new Position(s.getX(), s.getY());
        return new Position(spawns.get(0).getX(), spawns.get(0).getY());
    }

    private void broadcastGameState() {
        List<Player> list = new ArrayList<>(players.values());
        if (observer != null) observer.onGameStateUpdated(list, globalGoals);
        List<PlayerState> states = new ArrayList<>();
        for (Player p : list) states.add(new PlayerState(p.getStudentCode(), p.getRole().name(), p.getPosition().getX(), p.getPosition().getY()));
        broadcast(new GameState(states));
    }

    private void broadcast(Object dto) {
        if (observer != null) observer.onBroadcast(dto);
    }

    private void send(String code, Object dto) {
        if (observer != null) observer.onSendTo(code, dto);
    }
}
