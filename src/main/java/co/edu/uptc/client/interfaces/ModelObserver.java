package co.edu.uptc.client.interfaces;

import co.edu.uptc.shared.dto.GameState;

public interface ModelObserver {
    void onPersonalInfoUpdated(String role, int score);
    void onBoardUpdated(GameState state);
    void onGameEnd(String reason);
    void onError(String message);
}
