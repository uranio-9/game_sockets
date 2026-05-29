package co.edu.uptc.client.model;

import co.edu.uptc.client.interfaces.ModelInterface;
import co.edu.uptc.client.interfaces.ModelObserver;
import co.edu.uptc.shared.dto.GameState;

public class ClientModel implements ModelInterface {
    private ModelObserver observer;
    private String studentCode;
    private String role = "—";
    private int score = 0;

    @Override
    public void setObserver(ModelObserver o) { this.observer = o; }

    @Override
    public void setStudentCode(String code) { this.studentCode = code; }

    @Override
    public void updateRole(String r) {
        this.role = r;
        if (observer != null) observer.onPersonalInfoUpdated(role, score);
    }

    @Override
    public void updateScore(int s) {
        this.score = s;
        if (observer != null) observer.onPersonalInfoUpdated(role, score);
    }

    @Override
    public void updateState(GameState state) {
        findAndSetRole(state);
        if (observer != null) {
            observer.onBoardUpdated(state);
            observer.onPersonalInfoUpdated(role, score);
        }
    }

    private void findAndSetRole(GameState state) {
        if (state.getPlayers() == null || studentCode == null) return;
        state.getPlayers().stream()
             .filter(p -> studentCode.equals(p.getStudentCode()))
             .findFirst()
             .ifPresent(p -> this.role = p.getRole());
    }

    @Override
    public void endGame(String reason) {
        if (observer != null) observer.onGameEnd(reason);
    }

    @Override
    public void triggerError(String error) {
        if (observer != null) observer.onError(error);
    }
}
