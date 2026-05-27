package co.edu.uptc.server.interfaces;

public interface ModelInterface {
    void setPresenter(PresenterInterface presenter);

    /**
     * direction: "UP" | "DOWN" | "LEFT" | "RIGHT"
     */
    void processMove(String studentCode, String direction);
    void processConnect(String studentCode);
    void processDisconnect(String studentCode);
    void startGame();
    void finishGame();
}
