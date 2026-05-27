package co.edu.uptc.server.interfaces;

public interface ServerBroadcasterInterface {
    void broadcast(Object message);
    void sendTo(String studentCode, Object message);
    void disconnectAll();
}
