package co.edu.uptc.shared.dto;

/**
 * S → All  –  { "type": "ROLE_CHANGE", "studentCode": "String",
 *               "newRole": "String",
 *               "newPosition": { "x": int, "y": int } }
 */
public class RoleChange {
    private final String type = "ROLE_CHANGE";
    private String   studentCode;
    private String   newRole;
    private Position newPosition;

    public RoleChange() {}
    public RoleChange(String studentCode, String newRole, int x, int y) {
        this.studentCode = studentCode;
        this.newRole     = newRole;
        this.newPosition = new Position(x, y);
    }

    public String   getType()        { return type; }
    public String   getStudentCode() { return studentCode; }
    public String   getNewRole()     { return newRole; }
    public Position getNewPosition() { return newPosition; }

    public static class Position {
        private int x, y;
        public Position() {}
        public Position(int x, int y) { this.x = x; this.y = y; }
        public int getX() { return x; }
        public int getY() { return y; }
    }
}
