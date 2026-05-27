package co.edu.uptc.shared.dto;

/**
 * S → All  –  { "type": "GAME_START", "speed": int,
 *               "gameArea": { "width": int, "height": int },
 *               "courtSide": "String" }
 */
public class GameStart {
    private final String type = "GAME_START";
    private int      speed;
    private GameArea gameArea;
    private String   courtSide;

    public GameStart() {}
    public GameStart(int speed, int width, int height, String courtSide) {
        this.speed     = speed;
        this.gameArea  = new GameArea(width, height);
        this.courtSide = courtSide;
    }

    public String   getType()      { return type; }
    public int      getSpeed()     { return speed; }
    public GameArea getGameArea()  { return gameArea; }
    public String   getCourtSide() { return courtSide; }

    // ── Nested class ──────────────────────────────────────────────────────────
    public static class GameArea {
        private int width;
        private int height;

        public GameArea() {}
        public GameArea(int width, int height) {
            this.width  = width;
            this.height = height;
        }
        public int getWidth()  { return width; }
        public int getHeight() { return height; }
    }
}
