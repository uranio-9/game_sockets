package co.edu.uptc.shared.dto;

/** S → All  –  { "type": "PLAYER_DONE", "studentCode": "String" } */
public class PlayerDone {
    private final String type = "PLAYER_DONE";
    private String studentCode;

    public PlayerDone() {}
    public PlayerDone(String studentCode) { this.studentCode = studentCode; }

    public String getType()        { return type; }
    public String getStudentCode() { return studentCode; }
}
