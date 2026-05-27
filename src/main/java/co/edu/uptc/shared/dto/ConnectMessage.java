package co.edu.uptc.shared.dto;

/** C → S  –  { "type": "CONNECT", "studentCode": "String" } */
public class ConnectMessage {
    private final String type = "CONNECT";
    private String studentCode;

    public ConnectMessage() {}
    public ConnectMessage(String studentCode) { this.studentCode = studentCode; }

    public String getType()        { return type; }
    public String getStudentCode() { return studentCode; }
}
