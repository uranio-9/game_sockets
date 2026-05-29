package co.edu.uptc.client.presenter;

import co.edu.uptc.client.interfaces.*;
import co.edu.uptc.shared.dto.*;
import javax.swing.SwingUtilities;

public class ClientPresenter implements PresenterInterface {
    private ViewInterface view;
    private NetClientInterface netClient;
    private ModelInterface model;
    private String studentCode;

    @Override public void setModel(ModelInterface m) { this.model = m; this.model.setObserver(this); }
    @Override public void setView(ViewInterface v) { this.view = v; }
    @Override public void setNetClient(NetClientInterface n) { this.netClient = n; }
    @Override public void setStudentCode(String code) { 
        this.studentCode = code; 
        if (model != null) this.model.setStudentCode(code); 
    }

    @Override public void onKeyUp() { sendMove("UP"); }
    @Override public void onKeyDown() { sendMove("DOWN"); }
    @Override public void onKeyLeft() { sendMove("LEFT"); }
    @Override public void onKeyRight() { sendMove("RIGHT"); }

    @Override public void connectToServer(String host, int port, String code) {
        setStudentCode(code);
        this.netClient = new co.edu.uptc.client.net.NetClient(this);
        try { 
            netClient.connect(host, port);
            netClient.sendMessage(new ConnectMessage(code));
        } catch (Exception e) { onError("Connection failed"); }
    }

    private void sendMove(String direction) {
        if (netClient != null) netClient.sendMessage(new MoveRequest(studentCode, direction));
    }

    @Override public void onConnectAck(ConnectAck ack) { System.out.println("[C] ACK: " + ack.getMessage()); }
    @Override public void onGameStart(GameStart gs) { System.out.println("[C] START: " + gs.getCourtSide()); }
    @Override public void onRoleAssign(RoleAssign ra) { model.updateRole(ra.getRole()); }
    @Override public void onGameState(GameState state) { model.updateState(state); }
    
    @Override public void onScoreUpdate(ScoreUpdate su) { 
        if (studentCode.equals(su.getStudentCode())) model.updateScore(su.getScore());
    }
    
    @Override public void onRoleChange(RoleChange rc) {
        if (studentCode.equals(rc.getStudentCode())) model.updateRole(rc.getNewRole());
    }
    
    @Override public void onBlockEvent(BlockEvent be) { System.out.println("[C] BLOCK"); }
    @Override public void onPlayerDone(PlayerDone pd) { System.out.println("[C] DONE"); }
    
    @Override public void onGameEndMsg(GameEnd ge) {
        netClient.disconnect();
        model.endGame(ge != null ? ge.getReason() : "SERVER_DECISION");
    }
    
    @Override public void onNetworkErrorMsg(String error) { model.triggerError(error); }

    @Override public void onPersonalInfoUpdated(String role, int score) { 
        SwingUtilities.invokeLater(() -> view.updatePersonalInfo(role, score)); 
    }
    @Override public void onBoardUpdated(GameState state) { 
        SwingUtilities.invokeLater(() -> view.updateBoard(state)); 
    }
    @Override public void onGameEnd(String reason) { 
        SwingUtilities.invokeLater(() -> view.showGameEnd(reason)); 
    }
    @Override public void onError(String msg) { 
        SwingUtilities.invokeLater(() -> view.showErrorAndExit(msg)); 
    }
}
