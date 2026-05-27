package co.edu.uptc.shared.dto;

/** S → C  –  { "type": "SCORE_UPDATE", "studentCode": "String", "score": int, "role": "String" } */
public class ScoreUpdate {
    private final String type = "SCORE_UPDATE";
    private String studentCode;
    private int    score;
    private String role;

    public ScoreUpdate() {}
    public ScoreUpdate(String studentCode, int score, String role) {
        this.studentCode = studentCode;
        this.score       = score;
        this.role        = role;
    }

    public String getType()        { return type; }
    public String getStudentCode() { return studentCode; }
    public int    getScore()       { return score; }
    public String getRole()        { return role; }
}
