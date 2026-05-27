package co.edu.uptc.client.interfaces;

import co.edu.uptc.shared.dto.GameState;

public interface ViewInterface {

    void setPresenter(PresenterInterface presenter);
    void start();

    /**
     * Repaint the board.  Called from EDT (via invokeLater in Presenter).
     * Receives the new flat GameState (List of PlayerState).
     */
    void updateBoard(GameState state);

    /** Refresh the personal-info strip. Called from EDT. */
    void updatePersonalInfo(String role, int totalScore);

    /** Modal dialog + clean exit. Called from EDT. */
    void showGameEnd(String reason);

    /** Error dialog + clean exit. Called from EDT. */
    void showErrorAndExit(String message);
}
