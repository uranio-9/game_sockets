package co.edu.uptc.shared.dto;

/**
 * S → C  –  { "type": "ROLE_ASSIGN", "role": "String",
 *             "position": { "x": int, "y": int } }
 */
public class RoleAssign {
    private final String type = "ROLE_ASSIGN";
    private String   role;
    private Position position;

    public RoleAssign() {}
    public RoleAssign(String role, int x, int y) {
        this.role     = role;
        this.position = new Position(x, y);
    }

    public String   getType()     { return type; }
    public String   getRole()     { return role; }
    public Position getPosition() { return position; }

    public static class Position {
        private int x, y;
        public Position() {}
        public Position(int x, int y) { this.x = x; this.y = y; }
        public int getX() { return x; }
        public int getY() { return y; }
    }
}
