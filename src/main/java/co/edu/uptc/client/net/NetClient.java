package co.edu.uptc.client.net;

import co.edu.uptc.client.interfaces.NetClientInterface;
import co.edu.uptc.client.interfaces.PresenterInterface;
import co.edu.uptc.shared.dto.*;
import co.edu.uptc.shared.util.JsonUtils;

import java.io.*;
import java.net.Socket;

public class NetClient implements NetClientInterface, Runnable {
    private final PresenterInterface presenter;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running;

    public NetClient(PresenterInterface p) { this.presenter = p; }

    @Override
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        running = true;
        new Thread(this, "Client-Listener").start();
    }

    @Override
    public void sendMessage(Object dto) { if (out != null) out.println(JsonUtils.toJson(dto)); }

    @Override
    public void disconnect() {
        running = false;
        try { if (socket != null && !socket.isClosed()) socket.close(); } 
        catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try {
            readLoop();
        } catch (IOException e) {
            if (running) presenter.onNetworkErrorMsg(e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void readLoop() throws IOException {
        String line;
        while (running && (line = in.readLine()) != null) {
            processLine(line);
        }
    }

    private void processLine(String json) {
        String type = JsonUtils.getType(json);
        if (type == null) return;
        routeA(type, json);
        routeB(type, json);
    }

    private void routeA(String type, String json) {
        if ("CONNECT_ACK".equals(type)) presenter.onConnectAck(JsonUtils.fromJson(json, ConnectAck.class));
        else if ("GAME_START".equals(type)) presenter.onGameStart(JsonUtils.fromJson(json, GameStart.class));
        else if ("ROLE_ASSIGN".equals(type)) presenter.onRoleAssign(JsonUtils.fromJson(json, RoleAssign.class));
        else if ("GAME_STATE".equals(type)) presenter.onGameState(JsonUtils.fromJson(json, GameState.class));
        else if ("SCORE_UPDATE".equals(type)) presenter.onScoreUpdate(JsonUtils.fromJson(json, ScoreUpdate.class));
    }

    private void routeB(String type, String json) {
        if ("ROLE_CHANGE".equals(type)) presenter.onRoleChange(JsonUtils.fromJson(json, RoleChange.class));
        else if ("BLOCK".equals(type)) presenter.onBlockEvent(JsonUtils.fromJson(json, BlockEvent.class));
        else if ("PLAYER_DONE".equals(type)) presenter.onPlayerDone(JsonUtils.fromJson(json, PlayerDone.class));
        else if ("GAME_END".equals(type)) presenter.onGameEndMsg(JsonUtils.fromJson(json, GameEnd.class));
    }
}
