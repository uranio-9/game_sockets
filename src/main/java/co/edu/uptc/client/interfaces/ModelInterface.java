package co.edu.uptc.client.interfaces;

import co.edu.uptc.shared.dto.GameState;

public interface ModelInterface {
    void setObserver(ModelObserver observer);
    void setStudentCode(String code);
    void updateRole(String role);
    void updateScore(int score);
    void updateState(GameState state);
    void endGame(String reason);
    void triggerError(String error);
}
