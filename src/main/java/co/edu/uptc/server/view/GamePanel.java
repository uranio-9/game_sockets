package co.edu.uptc.server.view;

import co.edu.uptc.server.config.Constants;
import co.edu.uptc.shared.pojo.Player;
import co.edu.uptc.shared.pojo.Role;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private List<Player> players;

    public GamePanel() {
        this.players = new ArrayList<>();
        setPreferredSize(new Dimension(Constants.GAME_AREA_WIDTH * Constants.CELL_SIZE, Constants.GAME_AREA_HEIGHT * Constants.CELL_SIZE));
        setBackground(Color.BLACK);
    }

    public void updatePlayers(List<Player> players) {
        this.players = players;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawGoal(g);
        drawPlayers(g);
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < Constants.GAME_AREA_WIDTH; i++) {
            for (int j = 0; j < Constants.GAME_AREA_HEIGHT; j++) {
                g.drawRect(i * Constants.CELL_SIZE, j * Constants.CELL_SIZE, Constants.CELL_SIZE, Constants.CELL_SIZE);
            }
        }
    }

    private void drawGoal(Graphics g) {
        g.setColor(Color.YELLOW);
        int h = (9 - 5 + 1) * Constants.CELL_SIZE;
        g.fillRect(0, 5 * Constants.CELL_SIZE, Constants.CELL_SIZE, h);
    }

    private void drawPlayers(Graphics g) {
        for (Player p : players) drawPlayer(g, p);
    }

    private void drawPlayer(Graphics g, Player p) {
        Color c = p.getRole() == Role.ATTACKER ? Constants.COLOR_ATTACKER : Constants.COLOR_DEFENDER;
        g.setColor(c);
        int px = p.getPosition().getX() * Constants.CELL_SIZE;
        int py = p.getPosition().getY() * Constants.CELL_SIZE;
        g.fillRect(px, py, Constants.PLAYER_SIZE, Constants.PLAYER_SIZE);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(p.getStudentCode(), px + 2, py + 25);
    }
}
