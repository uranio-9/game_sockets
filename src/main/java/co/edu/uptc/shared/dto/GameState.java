package co.edu.uptc.shared.dto;

import java.util.List;

/**
 * S → All  –  { "type": "GAME_STATE",
 *               "players": [{ "studentCode": "String", "role": "String",
 *                             "x": int, "y": int }] }
 */
public class GameState {
    private final String type = "GAME_STATE";
    private List<PlayerState> players;

    public GameState() {}
    public GameState(List<PlayerState> players) { this.players = players; }

    public String            getType()    { return type; }
    public List<PlayerState> getPlayers() { return players; }
    public void setPlayers(List<PlayerState> players) { this.players = players; }
}
