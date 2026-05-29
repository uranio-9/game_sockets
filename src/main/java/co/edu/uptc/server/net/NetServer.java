package co.edu.uptc.server.net;

import co.edu.uptc.server.interfaces.PresenterInterface;
import co.edu.uptc.server.interfaces.ServerBroadcasterInterface;
import co.edu.uptc.shared.util.JsonUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class NetServer implements ServerBroadcasterInterface, Runnable {
    private ServerSocket serverSocket;
    private PresenterInterface presenter;
    private int port;
    private volatile boolean running;
    private ConcurrentHashMap<String, ClientHandler> clients;

    public NetServer(int port, PresenterInterface presenter) {
        this.port = port;
        this.presenter = presenter;
        this.clients = new ConcurrentHashMap<>();
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        new Thread(this, "Acceptor-Thread").start();
        System.out.println("[SERVER-NET] Server started on port " + port);
    }

    public void stopServer() {
        running = false;
        disconnectAll();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[SERVER-NET] Server stopped.");
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, presenter, this);
                new Thread(handler).start();
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void registerClient(String studentCode, ClientHandler handler) {
        clients.put(studentCode, handler);
        System.out.println("[SERVER-NET] Client registered: " + studentCode);
    }

    public void removeClient(String studentCode) {
        clients.remove(studentCode);
        System.out.println("[SERVER-NET] Client removed: " + studentCode);
    }

    @Override
    public void broadcast(Object message) {
        String json = JsonUtils.toJson(message);
        for (ClientHandler handler : clients.values()) {
            handler.sendMessage(json);
        }
    }

    @Override
    public void sendTo(String studentCode, Object message) {
        ClientHandler handler = clients.get(studentCode);
        if (handler != null) {
            handler.sendMessage(JsonUtils.toJson(message));
        }
    }

    @Override
    public void disconnectAll() {
        for (ClientHandler handler : clients.values()) {
            handler.disconnect();
        }
        clients.clear();
    }
}
