package co.edu.uptc.server.net;

import co.edu.uptc.server.interfaces.PresenterInterface;
import co.edu.uptc.shared.util.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * One thread per connected client.
 * Reads lines with blocking {@code in.readLine()} and delegates each
 * raw JSON string to the Presenter — no game logic here.
 */
public class ClientHandler implements Runnable {

    private final Socket             socket;
    private final PresenterInterface presenter;
    private final NetServer          server;

    private PrintWriter   out;
    private BufferedReader in;
    private String        studentCode;

    public ClientHandler(Socket socket, PresenterInterface presenter, NetServer server) {
        this.socket    = socket;
        this.presenter = presenter;
        this.server    = server;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStudentCode() { return studentCode; }

    public void sendMessage(String json) {
        if (out != null) out.println(json);
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String type = JsonUtils.getType(line);
                if ("CONNECT".equals(type) && studentCode == null) {
                    // Extract studentCode from flat JSON
                    studentCode = extractStudentCode(line);
                    if (studentCode != null && !studentCode.isBlank()) {
                        server.registerClient(studentCode, this);
                        presenter.handleClientMessage(studentCode, line);
                    }
                } else if (studentCode != null) {
                    presenter.handleClientMessage(studentCode, line);
                }
            }
        } catch (IOException e) {
            // connection dropped
        } finally {
            if (studentCode != null) {
                server.removeClient(studentCode);
                presenter.handleClientDisconnect(studentCode);
            }
            disconnect();
        }
    }

    /** Read "studentCode" from a flat JSON string without a data wrapper. */
    private String extractStudentCode(String json) {
        try {
            co.edu.uptc.shared.dto.ConnectMessage msg =
                    JsonUtils.fromJson(json, co.edu.uptc.shared.dto.ConnectMessage.class);
            return msg.getStudentCode();
        } catch (Exception e) {
            return null;
        }
    }
}
