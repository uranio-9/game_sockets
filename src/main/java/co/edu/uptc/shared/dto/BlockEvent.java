package co.edu.uptc.shared.dto;

/** S → All  –  { "type": "BLOCK", "defenderCode": "String", "attackerCode": "String" } */
public class BlockEvent {
    private final String type = "BLOCK";
    private String defenderCode;
    private String attackerCode;

    public BlockEvent() {}
    public BlockEvent(String defenderCode, String attackerCode) {
        this.defenderCode = defenderCode;
        this.attackerCode = attackerCode;
    }

    public String getType()         { return type; }
    public String getDefenderCode() { return defenderCode; }
    public String getAttackerCode() { return attackerCode; }
}
