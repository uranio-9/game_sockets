package co.edu.uptc.client.presenter;

import co.edu.uptc.client.interfaces.NetClientInterface;
import co.edu.uptc.client.interfaces.PresenterInterface;
import co.edu.uptc.client.interfaces.ViewInterface;
import co.edu.uptc.shared.dto.*;
import co.edu.uptc.shared.util.JsonUtils;

import javax.swing.SwingUtilities;

/**
 * Central mediator for the client application.
 *
 * Server → Client flow (reader thread):
 *   NetClient calls onRawMessageReceived(json).
 *   Presenter reads the "type" field, deserializes the correct flat DTO,
 *   and schedules view updates via SwingUtilities.invokeLater().
 *
 * Client → Server flow (EDT):
 *   View calls onKeyXxx().
 *   Presenter builds a flat MoveRequest and sends via NetClient.
 */
public class ClientPresenter implements PresenterInterface {

    private ViewInterface      view;
    private NetClientInterface netClient;
    private String             studentCode;

    // Local shadow for privacy-restricted personal info panel
    private String localRole       = "—";
    private int    localTotalScore = 0;

    // ── Wiring ────────────────────────────────────────────────────────────────

    @Override public void setView(ViewInterface v)              { this.view = v; }
    @Override public void setNetClient(NetClientInterface n)    { this.netClient = n; }
    @Override public void setStudentCode(String code)          { this.studentCode = code; }

    // ── View → Presenter (keyboard input) ────────────────────────────────────

    @Override public void onKeyUp()    { sendMove("UP");    }
    @Override public void onKeyDown()  { sendMove("DOWN");  }
    @Override public void onKeyLeft()  { sendMove("LEFT");  }
    @Override public void onKeyRight() { sendMove("RIGHT"); }

    private void sendMove(String direction) {
        if (netClient == null) return;
        netClient.sendMessage(new MoveRequest(studentCode, direction));
    }

    // ── Net → Presenter (raw JSON from server) ────────────────────────────────

    @Override
    public void onRawMessageReceived(String json) {
        try {
            String type = JsonUtils.getType(json);
            if (type == null) return;

            switch (type) {
                case "CONNECT_ACK" -> {
                    ConnectAck ack = JsonUtils.fromJson(json, ConnectAck.class);
                    System.out.println("[Client] CONNECT_ACK: " + ack.getMessage());
                }
                case "GAME_START" -> {
                    GameStart gs = JsonUtils.fromJson(json, GameStart.class);
                    System.out.println("[Client] GAME_START – area "
                            + gs.getGameArea().getWidth() + "x" + gs.getGameArea().getHeight()
                            + ", court=" + gs.getCourtSide());
                }
                case "ROLE_ASSIGN" -> {
                    RoleAssign ra = JsonUtils.fromJson(json, RoleAssign.class);
                    localRole = ra.getRole();
                    final String role  = localRole;
                    final int    score = localTotalScore;
                    SwingUtilities.invokeLater(() -> view.updatePersonalInfo(role, score));
                }
                case "GAME_STATE" -> {
                    GameState state = JsonUtils.fromJson(json, GameState.class);
                    // Find own position to keep local info in sync
                    if (state.getPlayers() != null) {
                        state.getPlayers().stream()
                                .filter(p -> studentCode.equals(p.getStudentCode()))
                                .findFirst()
                                .ifPresent(p -> localRole = p.getRole());
                    }
                    final String role  = localRole;
                    final int    score = localTotalScore;
                    SwingUtilities.invokeLater(() -> {
                        view.updateBoard(state);
                        view.updatePersonalInfo(role, score);
                    });
                }
                case "SCORE_UPDATE" -> {
                    ScoreUpdate su = JsonUtils.fromJson(json, ScoreUpdate.class);
                    if (studentCode.equals(su.getStudentCode())) {
                        localTotalScore = su.getScore();
                        localRole       = su.getRole();
                        final String role  = localRole;
                        final int    score = localTotalScore;
                        SwingUtilities.invokeLater(() -> view.updatePersonalInfo(role, score));
                    }
                }
                case "ROLE_CHANGE" -> {
                    RoleChange rc = JsonUtils.fromJson(json, RoleChange.class);
                    if (studentCode.equals(rc.getStudentCode())) {
                        localRole = rc.getNewRole();
                        final String role  = localRole;
                        final int    score = localTotalScore;
                        SwingUtilities.invokeLater(() -> view.updatePersonalInfo(role, score));
                    }
                }
                case "BLOCK" -> {
                    // No UI change needed; only positional update comes via GAME_STATE
                    BlockEvent be = JsonUtils.fromJson(json, BlockEvent.class);
                    System.out.println("[Client] BLOCK – defender=" + be.getDefenderCode()
                            + " attacker=" + be.getAttackerCode());
                }
                case "PLAYER_DONE" -> {
                    PlayerDone pd = JsonUtils.fromJson(json, PlayerDone.class);
                    System.out.println("[Client] PLAYER_DONE – " + pd.getStudentCode());
                }
                case "GAME_END" -> {
                    GameEnd end = JsonUtils.fromJson(json, GameEnd.class);
                    netClient.disconnect();
                    SwingUtilities.invokeLater(() ->
                            view.showGameEnd(end != null ? end.getReason() : "SERVER_DECISION"));
                }
                default -> System.out.println("[Client] Unknown type: " + type);
            }
        } catch (Exception e) {
            System.err.println("[Client] Error processing message: " + e.getMessage());
        }
    }

    @Override
    public void onNetworkError(Exception e) {
        System.err.println("[Client] Network error: " + e.getMessage());
        SwingUtilities.invokeLater(() -> view.showErrorAndExit(e.getMessage()));
    }
}
