package co.edu.uptc.client.view;

import co.edu.uptc.client.config.ClientConstants;
import co.edu.uptc.shared.dto.PlayerState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the 25×15 game grid.
 *
 * OPTIMISED REPAINT: updatePlayers() computes dirty rectangles (old + new
 * bounding box per player) and calls repaint(x, y, width, height) only on
 * the affected cells.  The static background is never touched.
 */
public class ClientGamePanel extends JPanel {

    private List<PlayerState> players = new ArrayList<>();

    public ClientGamePanel() {
        int w = ClientConstants.GAME_AREA_WIDTH  * ClientConstants.CELL_SIZE;
        int h = ClientConstants.GAME_AREA_HEIGHT * ClientConstants.CELL_SIZE;
        setPreferredSize(new Dimension(w, h));
        setBackground(new Color(20, 20, 30));
        setOpaque(true);
        setDoubleBuffered(true);
    }

    /**
     * Called from the EDT (guaranteed by Presenter via invokeLater).
     * Computes dirty rectangles and triggers partial repaints.
     */
    public void updatePlayers(List<PlayerState> newPlayers) {
        List<Rectangle> dirty = new ArrayList<>();
        for (PlayerState old : this.players) dirty.add(cellRect(old.getX(), old.getY()));
        for (PlayerState np  : newPlayers)   dirty.add(cellRect(np.getX(),  np.getY()));

        this.players = new ArrayList<>(newPlayers);
        for (Rectangle r : dirty) repaint(r.x, r.y, r.width, r.height);
    }

    private Rectangle cellRect(int col, int row) {
        return new Rectangle(
                col * ClientConstants.CELL_SIZE,
                row * ClientConstants.CELL_SIZE,
                ClientConstants.CELL_SIZE,
                ClientConstants.CELL_SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGrid(g2);
        drawGoal(g2);
        drawPlayers(g2);
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(new Color(50, 50, 65));
        int W = ClientConstants.GAME_AREA_WIDTH, H = ClientConstants.GAME_AREA_HEIGHT;
        int C = ClientConstants.CELL_SIZE;
        for (int col = 0; col < W; col++)
            for (int row = 0; row < H; row++)
                g.drawRect(col * C, row * C, C, C);
    }

    private void drawGoal(Graphics2D g) {
        int C  = ClientConstants.CELL_SIZE;
        int y0 = ClientConstants.GOAL_ROW_MIN * C;
        int h  = (ClientConstants.GOAL_ROW_MAX - ClientConstants.GOAL_ROW_MIN + 1) * C;
        g.setColor(new Color(255, 215, 0, 180));
        g.fillRect(0, y0, C, h);
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(0, y0, C, h);
    }

    private void drawPlayers(Graphics2D g) {
        int C = ClientConstants.CELL_SIZE;
        int P = ClientConstants.PLAYER_SIZE;

        for (PlayerState p : players) {
            Color base = "ATTACKER".equals(p.getRole())
                    ? ClientConstants.COLOR_ATTACKER
                    : ClientConstants.COLOR_DEFENDER;

            int px = p.getX() * C;
            int py = p.getY() * C;

            g.setColor(base);
            g.fillRect(px, py, P, P);
            g.setColor(base.darker());
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(px, py, P, P);

            // Student code label
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 9));
            FontMetrics fm = g.getFontMetrics();
            String code = p.getStudentCode();
            while (fm.stringWidth(code) > C - 4 && code.length() > 1)
                code = code.substring(0, code.length() - 1);
            int tx = px + (C - fm.stringWidth(code)) / 2;
            int ty = py + (C + fm.getAscent()) / 2 - 2;
            g.drawString(code, tx, ty);
        }
    }
}
