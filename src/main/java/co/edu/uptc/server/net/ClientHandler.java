package co.edu.uptc.server.net;

import co.edu.uptc.server.interfaces.PresenterInterface;
import co.edu.uptc.shared.dto.MoveRequest;
import co.edu.uptc.shared.util.JsonUtils;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final PresenterInterface presenter;
    private final NetServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String studentCode;

    public ClientHandler(Socket s, PresenterInterface p, NetServer ns) {
        this.socket = s;
        this.presenter = p;
        this.server = ns;
        initStreams();
    }

    private void initStreams() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (IOException ignored) {}
    }

    public void sendMessage(String json) {
        if (out != null) out.println(json);
    }

    public void disconnect() {
        try { if (socket != null && !socket.isClosed()) socket.close(); } 
        catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try {
            readLoop();
        } catch (IOException e) {
            // Drop
        } finally {
            closeHandler();
        }
    }

    private void readLoop() throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            processLine(line);
        }
    }

    private void processLine(String line) {
        String type = JsonUtils.getType(line);
        if ("CONNECT".equals(type) && studentCode == null) {
            handleConnect(line);
        } else if (studentCode != null) {
            routeMessage(type, line);
        }
    }

    private void handleConnect(String line) {
        studentCode = extractStudentCode(line);
        if (studentCode != null && !studentCode.isBlank()) {
            server.registerClient(studentCode, this);
            presenter.onConnectReceived(studentCode);
        }
    }

    private void routeMessage(String type, String line) {
        if ("MOVE".equals(type)) {
            MoveRequest req = JsonUtils.fromJson(line, MoveRequest.class);
            presenter.onMoveReceived(studentCode, req.getDirection());
        } else if ("DISCONNECT".equals(type)) {
            presenter.onDisconnectReceived(studentCode);
        }
    }

    private void closeHandler() {
        if (studentCode != null) {
            server.removeClient(studentCode);
            presenter.onDisconnectReceived(studentCode);
        }
        disconnect();
    }

    private String extractStudentCode(String json) {
        try {
            co.edu.uptc.shared.dto.ConnectMessage m = JsonUtils.fromJson(json, co.edu.uptc.shared.dto.ConnectMessage.class);
            return m.getStudentCode();
        } catch (Exception e) { return null; }
    }
}
