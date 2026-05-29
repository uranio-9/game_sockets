package co.edu.uptc.client.interfaces;

import co.edu.uptc.shared.dto.*;

public interface PresenterInterface extends ModelObserver {
    void setModel(ModelInterface model);
    void setView(ViewInterface view);
    void setNetClient(NetClientInterface netClient);
    void setStudentCode(String code);

    // From View
    void onKeyUp();
    void onKeyDown();
    void onKeyLeft();
    void onKeyRight();
    void connectToServer(String host, int port, String code);

    // From NetClient
    void onConnectAck(ConnectAck ack);
    void onGameStart(GameStart gs);
    void onRoleAssign(RoleAssign ra);
    void onGameState(GameState state);
    void onScoreUpdate(ScoreUpdate su);
    void onRoleChange(RoleChange rc);
    void onBlockEvent(BlockEvent be);
    void onPlayerDone(PlayerDone pd);
    void onGameEndMsg(GameEnd ge);
    void onNetworkErrorMsg(String error);
}
