package co.edu.uptc.shared.pojo;

public class Player {
    private String studentCode;
    private Position position;
    private Role role;
    private Score score;

    public Player(String studentCode, Position position, Role role) {
        this.studentCode = studentCode;
        this.position = position;
        this.role = role;
        this.score = new Score();
    }

    public String getStudentCode() {
        return studentCode;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Score getScore() {
        return score;
    }
}
