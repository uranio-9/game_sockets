package co.edu.uptc.client.net;

import co.edu.uptc.client.interfaces.NetClientInterface;
import co.edu.uptc.client.interfaces.PresenterInterface;
import co.edu.uptc.shared.util.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Manages the TCP connection.
 * The reader thread calls {@code presenter.onRawMessageReceived(json)}
 * passing the raw JSON string — the Presenter owns all dispatch logic.
 */
public class NetClient implements NetClientInterface, Runnable {

    private final PresenterInterface presenter;

    private Socket        socket;
    private PrintWriter   out;
    private BufferedReader in;
    private volatile boolean running;

    public NetClient(PresenterInterface presenter) {
        this.presenter = presenter;
    }

    @Override
    public void connect(String host, int port) throws IOException {
        socket  = new Socket(host, port);
        out     = new PrintWriter(socket.getOutputStream(), true);
        in      = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        running = true;
        new Thread(this, "Client-Listener-Thread").start();
    }

    @Override
    public void sendMessage(Object dto) {
        if (out != null) out.println(JsonUtils.toJson(dto));
    }

    @Override
    public void disconnect() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                presenter.onRawMessageReceived(line);
            }
        } catch (IOException e) {
            if (running) presenter.onNetworkError(e);
        } finally {
            disconnect();
        }
    }
}
