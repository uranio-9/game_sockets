package co.edu.uptc.server.interfaces;

import co.edu.uptc.shared.pojo.Player;
import java.util.List;

public interface PresenterInterface extends ModelObserver {
    void setModel(ModelInterface model);
    void setView(ViewInterface view);

    // From Network to Model
    void onConnectReceived(String studentCode);
    void onMoveReceived(String studentCode, String direction);
    void onDisconnectReceived(String studentCode);

    // From View to Model
    void onStartGameClicked();
    void onFinishGameClicked();
    void startNetwork(int port);

    // From Presenter to Network
    void setBroadcaster(ServerBroadcasterInterface broadcaster);
}
