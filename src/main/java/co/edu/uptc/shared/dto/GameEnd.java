package co.edu.uptc.shared.dto;

/** S → All  –  { "type": "GAME_END", "reason": "String" } */
public class GameEnd {
    private final String type = "GAME_END";
    private String reason;

    public GameEnd() {}
    public GameEnd(String reason) { this.reason = reason; }

    public String getType()   { return type; }
    public String getReason() { return reason; }
}
