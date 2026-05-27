package co.edu.uptc.shared.dto;

/** C → S  –  { "type": "DISCONNECT", "studentCode": "String" } */
public class DisconnectMessage {
    private final String type = "DISCONNECT";
    private String studentCode;

    public DisconnectMessage() {}
    public DisconnectMessage(String studentCode) { this.studentCode = studentCode; }

    public String getType()        { return type; }
    public String getStudentCode() { return studentCode; }
}
