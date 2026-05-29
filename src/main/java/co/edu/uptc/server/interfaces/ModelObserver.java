package co.edu.uptc.server.interfaces;

import co.edu.uptc.shared.pojo.Player;
import java.util.List;

public interface ModelObserver {
    void onGameStateUpdated(List<Player> players, int globalGoals);
    void onLogEvent(String message);
    void onBroadcast(Object dto);
    void onSendTo(String code, Object dto);
    void onDisconnectAll();
}
