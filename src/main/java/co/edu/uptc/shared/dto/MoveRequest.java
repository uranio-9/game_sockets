package co.edu.uptc.shared.dto;

/** C → S  –  { "type": "MOVE", "studentCode": "String", "direction": "String" }
 *  direction values: "UP" | "DOWN" | "LEFT" | "RIGHT"
 */
public class MoveRequest {
    private final String type = "MOVE";
    private String studentCode;
    private String direction;

    public MoveRequest() {}
    public MoveRequest(String studentCode, String direction) {
        this.studentCode = studentCode;
        this.direction   = direction;
    }

    public String getType()        { return type; }
    public String getStudentCode() { return studentCode; }
    public String getDirection()   { return direction; }
}
