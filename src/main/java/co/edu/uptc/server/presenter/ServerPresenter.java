package co.edu.uptc.server.presenter;

import co.edu.uptc.server.interfaces.ModelInterface;
import co.edu.uptc.server.interfaces.PresenterInterface;
import co.edu.uptc.server.interfaces.ServerBroadcasterInterface;
import co.edu.uptc.server.interfaces.ViewInterface;
import co.edu.uptc.shared.dto.MoveRequest;
import co.edu.uptc.shared.pojo.Player;
import co.edu.uptc.shared.util.JsonUtils;

import javax.swing.SwingUtilities;
import java.util.List;

public class ServerPresenter implements PresenterInterface {

    private ModelInterface            model;
    private ViewInterface             view;
    private ServerBroadcasterInterface broadcaster;

    // ── Wiring ────────────────────────────────────────────────────────────────

    @Override public void setModel(ModelInterface m)                    { this.model = m; }
    @Override public void setView(ViewInterface v)                      { this.view  = v; }
    @Override public void setBroadcaster(ServerBroadcasterInterface b)  { this.broadcaster = b; }
    @Override public ServerBroadcasterInterface getBroadcaster()        { return broadcaster; }

    // ── Net → Presenter ───────────────────────────────────────────────────────

    @Override
    public void handleClientMessage(String studentCode, String messageJson) {
        try {
            String type = JsonUtils.getType(messageJson);
            if (type == null) return;

            switch (type) {
                case "CONNECT"    -> model.processConnect(studentCode);
                case "MOVE"       -> {
                    MoveRequest req = JsonUtils.fromJson(messageJson, MoveRequest.class);
                    model.processMove(studentCode, req.getDirection());
                }
                case "DISCONNECT" -> model.processDisconnect(studentCode);
                default           -> logEvent("Unknown message type '" + type + "' from " + studentCode);
            }
        } catch (Exception e) {
            logEvent("Error handling message from " + studentCode + ": " + e.getMessage());
        }
    }

    @Override
    public void handleClientDisconnect(String studentCode) {
        model.processDisconnect(studentCode);
    }

    // ── View → Presenter ─────────────────────────────────────────────────────

    @Override public void onStartGameClicked()  { model.startGame(); }
    @Override public void onFinishGameClicked() { model.finishGame(); }

    // ── Model → Presenter → View ──────────────────────────────────────────────

    @Override
    public void updateGameState(List<Player> players) {
        // The operator panel still shows global goals; we track it in the
        // player list by counting scores (kept simple: use 0 as placeholder).
        SwingUtilities.invokeLater(() -> view.updateBoard(players, 0));
    }

    @Override
    public void logEvent(String message) {
        System.out.println("[SERVER] " + message);
    }
}
