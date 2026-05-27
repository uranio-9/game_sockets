package co.edu.uptc.shared.dto;

/**
 * One entry in the GAME_STATE players array.
 * { "studentCode": "String", "role": "String", "x": int, "y": int }
 */
public class PlayerState {
    private String studentCode;
    private String role;
    private int    x;
    private int    y;

    public PlayerState() {}
    public PlayerState(String studentCode, String role, int x, int y) {
        this.studentCode = studentCode;
        this.role = role;
        this.x = x;
        this.y = y;
    }

    public String getStudentCode() { return studentCode; }
    public String getRole()        { return role; }
    public int    getX()           { return x; }
    public int    getY()           { return y; }
}
