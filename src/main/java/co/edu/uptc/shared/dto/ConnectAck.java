package co.edu.uptc.shared.dto;

/** S → C  –  { "type": "CONNECT_ACK", "accepted": boolean, "message": "String" } */
public class ConnectAck {
    private final String type = "CONNECT_ACK";
    private boolean accepted;
    private String  message;

    public ConnectAck() {}
    public ConnectAck(boolean accepted, String message) {
        this.accepted = accepted;
        this.message  = message;
    }

    public String  getType()     { return type; }
    public boolean isAccepted()  { return accepted; }
    public String  getMessage()  { return message; }
}
