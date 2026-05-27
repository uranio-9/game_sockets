package co.edu.uptc.client.config;

import java.awt.Color;

public class ClientConstants {
    public static final int CELL_SIZE       = 40;
    public static final int PLAYER_SIZE     = 40;

    public static final int GAME_AREA_WIDTH  = 25;
    public static final int GAME_AREA_HEIGHT = 15;

    public static final String COURT_SIDE   = "LEFT";
    public static final int COURT_WIDTH     = 1;
    public static final int COURT_HEIGHT    = 5;  // rows 5-9 inclusive

    public static final int DEFAULT_PORT    = 8080;
    public static final String DEFAULT_HOST = "localhost";

    public static final Color COLOR_ATTACKER = Color.GREEN;
    public static final Color COLOR_DEFENDER = Color.RED;

    // Goal area row bounds (inclusive)
    public static final int GOAL_ROW_MIN = 5;
    public static final int GOAL_ROW_MAX = 9;

    private ClientConstants() {}
}
