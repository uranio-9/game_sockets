package co.edu.uptc.server.interfaces;

import co.edu.uptc.shared.pojo.Player;
import java.util.List;

public interface PresenterInterface {
    void setModel(ModelInterface model);
    void setView(ViewInterface view);

    // Net → Presenter
    void handleClientMessage(String studentCode, String messageJson);
    void handleClientDisconnect(String studentCode);

    // View → Presenter
    void onStartGameClicked();
    void onFinishGameClicked();

    // Model → Presenter → View
    void updateGameState(List<Player> players);
    void logEvent(String message);

    // Broadcaster access
    ServerBroadcasterInterface getBroadcaster();
    void setBroadcaster(ServerBroadcasterInterface broadcaster);
}
