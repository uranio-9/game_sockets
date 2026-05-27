package co.edu.uptc.client.view;

import co.edu.uptc.client.config.ClientConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Personal-info panel.
 *
 * PRIVACY CONSTRAINT: Only the local player's role and total score are shown.
 * No data about other players is ever displayed here.
 */
public class PersonalInfoPanel extends JPanel {

    private final JLabel lblRole;
    private final JLabel lblScore;

    public PersonalInfoPanel() {
        setLayout(new GridLayout(2, 1, 6, 6));
        setBorder(new EmptyBorder(10, 14, 10, 14));
        setBackground(new Color(30, 30, 45));

        Font big = new Font("SansSerif", Font.BOLD, 16);

        lblRole  = createLabel("Role: —", big);
        lblScore = createLabel("Score: 0", big);

        add(lblRole);
        add(lblScore);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(Color.WHITE);
        l.setOpaque(true);
        l.setBackground(new Color(40, 40, 60));
        l.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 110), 1));
        return l;
    }

    /**
     * Update display. Must be called from the EDT (guaranteed by Presenter).
     *
     * @param role       "ATTACKER" or "DEFENDER"
     * @param totalScore the local player's accumulated total
     */
    public void update(String role, int totalScore) {
        Color roleColor = "ATTACKER".equals(role)
                ? ClientConstants.COLOR_ATTACKER.darker()
                : ClientConstants.COLOR_DEFENDER.darker();

        lblRole.setText("Role: " + role);
        lblRole.setForeground(roleColor);
        lblScore.setText("Score: " + totalScore);
    }
}
