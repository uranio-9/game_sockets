package co.edu.uptc.server.interfaces;

import co.edu.uptc.shared.pojo.Player;
import java.util.List;

public interface ViewInterface {
    void setPresenter(PresenterInterface presenter);
    void start();
    void updateBoard(List<Player> players, int globalGoals);
}
