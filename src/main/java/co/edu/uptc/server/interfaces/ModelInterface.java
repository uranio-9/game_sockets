package co.edu.uptc.server.interfaces;

public interface ModelInterface {
    void setObserver(ModelObserver observer);
    void processMove(String studentCode, String direction);
    void processConnect(String studentCode);
    void processDisconnect(String studentCode);
    void startGame();
    void finishGame();
}
