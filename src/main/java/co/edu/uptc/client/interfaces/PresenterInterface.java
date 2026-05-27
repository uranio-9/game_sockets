package co.edu.uptc.client.interfaces;

public interface PresenterInterface {

    void setView(ViewInterface view);
    void setNetClient(NetClientInterface netClient);
    void setStudentCode(String code);

    // ── View → Presenter (keyboard) ──────────────────────────────────────────
    void onKeyUp();
    void onKeyDown();
    void onKeyLeft();
    void onKeyRight();

    // ── Net → Presenter (raw JSON from server) ────────────────────────────────
    void onRawMessageReceived(String json);
    void onNetworkError(Exception e);
}
