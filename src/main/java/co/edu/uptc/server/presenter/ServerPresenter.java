package co.edu.uptc.server.presenter;

import co.edu.uptc.server.interfaces.ModelInterface;
import co.edu.uptc.server.interfaces.PresenterInterface;
import co.edu.uptc.server.interfaces.ServerBroadcasterInterface;
import co.edu.uptc.server.interfaces.ViewInterface;
import co.edu.uptc.shared.pojo.Player;

import javax.swing.SwingUtilities;
import java.util.List;

public class ServerPresenter implements PresenterInterface {

    private ModelInterface model;
    private ViewInterface view;
    private ServerBroadcasterInterface broadcaster;

    @Override public void setModel(ModelInterface m) { this.model = m; this.model.setObserver(this); }
    @Override public void setView(ViewInterface v) { this.view = v; }
    @Override public void setBroadcaster(ServerBroadcasterInterface b) { this.broadcaster = b; }

    @Override public void startNetwork(int port) {
        co.edu.uptc.server.net.NetServer s = new co.edu.uptc.server.net.NetServer(port, this);
        this.broadcaster = s;
        try { s.startServer(); } catch (Exception e) { onLogEvent("Net Err"); }
    }

    @Override public void onConnectReceived(String studentCode) { model.processConnect(studentCode); }
    @Override public void onMoveReceived(String code, String dir) { model.processMove(code, dir); }
    @Override public void onDisconnectReceived(String studentCode) { model.processDisconnect(studentCode); }

    @Override public void onStartGameClicked() { model.startGame(); }
    @Override public void onFinishGameClicked() { model.finishGame(); }

    @Override public void onGameStateUpdated(List<Player> p, int g) { SwingUtilities.invokeLater(() -> view.updateBoard(p, g)); }
    @Override public void onLogEvent(String message) { System.out.println("[SERVER] " + message); }
    @Override public void onBroadcast(Object dto) { broadcaster.broadcast(dto); }
    @Override public void onSendTo(String code, Object dto) { broadcaster.sendTo(code, dto); }
    @Override public void onDisconnectAll() { broadcaster.disconnectAll(); }
}
