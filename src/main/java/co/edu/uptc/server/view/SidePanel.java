package co.edu.uptc.server.view;

import co.edu.uptc.shared.pojo.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SidePanel extends JPanel {
    private JButton btnStart;
    private JButton btnFinish;
    private DefaultListModel<String> listModel;
    private JList<String> playerList;
    private JLabel lblGlobalGoals;

    public SidePanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        btnStart = new JButton("Iniciar Partida");
        btnFinish = new JButton("Finalizar Partida");
        controlPanel.add(btnStart);
        controlPanel.add(btnFinish);
        add(controlPanel, BorderLayout.NORTH);

        lblGlobalGoals = new JLabel("Goles Totales: 0", SwingConstants.CENTER);
        lblGlobalGoals.setFont(new Font("Arial", Font.BOLD, 14));
        lblGlobalGoals.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        listModel = new DefaultListModel<>();
        playerList = new JList<>(listModel);
        playerList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value.toString().contains("ATTACKER")) {
                    c.setForeground(Color.GREEN.darker());
                } else {
                    c.setForeground(Color.RED);
                }
                return c;
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(lblGlobalGoals, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(playerList), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void updateState(List<Player> players, int globalGoals) {
        lblGlobalGoals.setText("Goles Totales: " + globalGoals);
        listModel.clear();
        for (Player p : players) {
            listModel.addElement(p.getStudentCode() + " - " + p.getRole() + " (Pts: " + p.getScore().getTotal() + ")");
        }
    }

    public JButton getBtnStart() {
        return btnStart;
    }

    public JButton getBtnFinish() {
        return btnFinish;
    }
}
