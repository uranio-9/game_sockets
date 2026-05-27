package co.edu.uptc.server.model;

import co.edu.uptc.server.config.Constants;
import co.edu.uptc.server.interfaces.ModelInterface;
import co.edu.uptc.server.interfaces.PresenterInterface;
import co.edu.uptc.shared.dto.*;
import co.edu.uptc.shared.pojo.Player;
import co.edu.uptc.shared.pojo.Position;
import co.edu.uptc.shared.pojo.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Physics engine and authority for all game rules.
 * Does NOT know about Swing, sockets, or network details.
 */
public class GameModel implements ModelInterface, Runnable {

    private PresenterInterface presenter;

    private final ConcurrentLinkedQueue<MoveCommand>    moveQueue     = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, Player>     players       = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long>       lastMoveTimes = new ConcurrentHashMap<>();

    private int     globalGoals   = 0;
    private volatile boolean isGameStarted = false;
    private volatile boolean running       = true;

    private List<Position> attackerSpawns;
    private List<Position> defenderSpawns;

    // ── Inner command type ────────────────────────────────────────────────────
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
        for (int y = 0; y < Constants.GAME_AREA_HEIGHT; y++)
            attackerSpawns.add(new Position(Constants.GAME_AREA_WIDTH - 1, y));

        defenderSpawns = new ArrayList<>();
        for (int y = 0; y < Constants.GAME_AREA_HEIGHT; y++)
            defenderSpawns.add(new Position(3, y));
    }

    // ── ModelInterface ────────────────────────────────────────────────────────

    @Override public void setPresenter(PresenterInterface p) { this.presenter = p; }

    @Override
    public void processConnect(String studentCode) {
        if (players.containsKey(studentCode)) {
            // Reconnect: send ACK and re-assign existing role
            Player existing = players.get(studentCode);
            send(studentCode, new ConnectAck(true, "Reconnected as " + existing.getRole()));
            send(studentCode, new RoleAssign(
                    existing.getRole().name(),
                    existing.getPosition().getX(),
                    existing.getPosition().getY()));
            return;
        }

        // Assign role balancing
        long attackers = players.values().stream().filter(p -> p.getRole() == Role.ATTACKER).count();
        long defenders = players.values().stream().filter(p -> p.getRole() == Role.DEFENDER).count();
        Role role = attackers <= defenders ? Role.ATTACKER : Role.DEFENDER;

        Position spawn = findAvailableSpawn(role);
        Player player  = new Player(studentCode, spawn, role);
        players.put(studentCode, player);

        send(studentCode, new ConnectAck(true, "Connected as " + role));
        send(studentCode, new RoleAssign(role.name(), spawn.getX(), spawn.getY()));

        presenter.logEvent("Player " + studentCode + " connected as " + role);
        broadcastGameState();
    }

    @Override
    public void processDisconnect(String studentCode) {
        players.remove(studentCode);
        lastMoveTimes.remove(studentCode);
        presenter.logEvent("Player " + studentCode + " disconnected.");
        broadcastGameState();
    }

    @Override
    public void processMove(String studentCode, String direction) {
        if (!isGameStarted) return;
        long now  = System.currentTimeMillis();
        long last = lastMoveTimes.getOrDefault(studentCode, 0L);
        if (now - last < Constants.COOLDOWN_MS) return; // anti-spam: silent drop
        lastMoveTimes.put(studentCode, now);
        moveQueue.offer(new MoveCommand(studentCode, direction));
    }

    @Override
    public void startGame() {
        if (players.isEmpty()) { presenter.logEvent("Cannot start: no players."); return; }
        isGameStarted = true;
        presenter.logEvent("Game started.");
        broadcast(new GameStart(1, Constants.GAME_AREA_WIDTH, Constants.GAME_AREA_HEIGHT, Constants.COURT_SIDE));
        broadcastGameState();
    }

    @Override
    public void finishGame() {
        isGameStarted = false;
        running       = false;
        presenter.logEvent("Game finished by operator.");
        broadcast(new GameEnd("SERVER_DECISION"));
        if (presenter.getBroadcaster() != null) presenter.getBroadcaster().disconnectAll();
    }

    // ── Physics loop ──────────────────────────────────────────────────────────

    @Override
    public void run() {
        while (running) {
            MoveCommand cmd = moveQueue.poll();
            if (cmd != null && isGameStarted) {
                executeMove(cmd);
            } else {
                try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    private void executeMove(MoveCommand cmd) {
        Player player = players.get(cmd.studentCode);
        if (player == null || player.getScore().getTotal() >= Constants.POINTS_TO_WIN) return;

        int[] delta = directionToDelta(cmd.direction);
        if (delta == null) return;

        int newX = player.getPosition().getX() + delta[0];
        int newY = player.getPosition().getY() + delta[1];

        // Bounds
        if (newX < 0 || newX >= Constants.GAME_AREA_WIDTH || newY < 0 || newY >= Constants.GAME_AREA_HEIGHT) return;

        // Anti-camper zone: DEFENDER cannot enter x <= 1
        if (player.getRole() == Role.DEFENDER && newX <= 1) return;

        // Collision detection
        Player occupant = getPlayerAt(newX, newY);
        if (occupant != null) {
            if (occupant.getRole() == player.getRole()) return; // wall: same role rejected silently
            // Block mechanic
            Player attacker = player.getRole() == Role.ATTACKER ? player : occupant;
            Player defender = player.getRole() == Role.DEFENDER ? player : occupant;
            handleBlock(attacker, defender);
            return;
        }

        // Goal mechanic: x == 0, row in [5..9]
        if (player.getRole() == Role.ATTACKER && newX == 0 && newY >= 5 && newY <= 9) {
            handleGoal(player);
            return;
        }

        // Normal move
        player.getPosition().setX(newX);
        player.getPosition().setY(newY);
        broadcastGameState();
    }

    // ── Game-event handlers ───────────────────────────────────────────────────

    private void handleBlock(Player attacker, Player defender) {
        presenter.logEvent("BLOCK: " + defender.getStudentCode() + " stopped " + attacker.getStudentCode());

        broadcast(new BlockEvent(defender.getStudentCode(), attacker.getStudentCode()));

        defender.getScore().increment();
        send(defender.getStudentCode(),
             new ScoreUpdate(defender.getStudentCode(),
                             defender.getScore().getTotal(),
                             defender.getRole().name()));

        attacker.setPosition(findAvailableSpawn(Role.ATTACKER));

        checkRoleChange(defender);
        checkWinCondition(defender);
        broadcastGameState();
    }

    private void handleGoal(Player attacker) {
        globalGoals++;
        attacker.getScore().increment();
        presenter.logEvent("GOAL: " + attacker.getStudentCode() + " scored! Total goals: " + globalGoals);

        send(attacker.getStudentCode(),
             new ScoreUpdate(attacker.getStudentCode(),
                             attacker.getScore().getTotal(),
                             attacker.getRole().name()));

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

        presenter.logEvent("ROLE_CHANGE: " + player.getStudentCode() + " → " + newRole);
        broadcast(new RoleChange(player.getStudentCode(), newRole.name(),
                                 newPos.getX(), newPos.getY()));
    }

    private void checkWinCondition(Player player) {
        if (player.getScore().getTotal() < Constants.POINTS_TO_WIN) return;

        presenter.logEvent("PLAYER_DONE: " + player.getStudentCode());
        broadcast(new PlayerDone(player.getStudentCode())); // broadcast per protocol

        boolean allDone = players.values().stream()
                .allMatch(p -> p.getScore().getTotal() >= Constants.POINTS_TO_WIN);
        if (allDone && isGameStarted) {
            isGameStarted = false;
            presenter.logEvent("ALL_DONE — game ending.");
            broadcast(new GameEnd("ALL_DONE"));
            if (presenter.getBroadcaster() != null) presenter.getBroadcaster().disconnectAll();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
        // Update the operator view (keeps globalGoals for the side panel)
        presenter.updateGameState(list);

        // Build flat PlayerState list for network broadcast
        List<PlayerState> states = new ArrayList<>();
        for (Player p : list)
            states.add(new PlayerState(p.getStudentCode(), p.getRole().name(),
                                       p.getPosition().getX(), p.getPosition().getY()));
        broadcast(new GameState(states));
    }

    private void broadcast(Object dto) {
        if (presenter != null && presenter.getBroadcaster() != null)
            presenter.getBroadcaster().broadcast(dto);
    }

    private void send(String code, Object dto) {
        if (presenter != null && presenter.getBroadcaster() != null)
            presenter.getBroadcaster().sendTo(code, dto);
    }
}
