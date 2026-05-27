package co.edu.uptc.server.config;

import java.awt.Color;

public class Constants {
    public static final int CELL_SIZE = 40;
    public static final int PLAYER_SIZE = 40;
    
    public static final int GAME_AREA_WIDTH = 25;
    public static final int GAME_AREA_HEIGHT = 15;
    
    public static final String COURT_SIDE = "LEFT";
    public static final int COURT_WIDTH = 1;
    public static final int COURT_HEIGHT = 5;
    
    public static final int DEFAULT_PORT = 8080;
    
    public static final Color COLOR_ATTACKER = Color.GREEN;
    public static final Color COLOR_DEFENDER = Color.RED;
    
    // Non-UI constants
    public static final long COOLDOWN_MS = 500;
    public static final int POINTS_TO_WIN = 10;
    public static final int ROLE_CHANGE_THRESHOLD = 3;
    
    private Constants() {
        // Hide constructor
    }
}
