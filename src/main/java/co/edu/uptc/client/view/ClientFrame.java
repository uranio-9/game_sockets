package co.edu.uptc.client.view;

import co.edu.uptc.client.config.ClientConstants;
import co.edu.uptc.client.interfaces.PresenterInterface;
import co.edu.uptc.client.interfaces.ViewInterface;
import co.edu.uptc.shared.dto.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientFrame extends JFrame implements ViewInterface {
    private PresenterInterface presenter;
    private final ClientGamePanel gamePanel;
    private final PersonalInfoPanel infoPanel;

    public ClientFrame() {
        super("Game Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(0, 0));
        gamePanel = new ClientGamePanel();
        infoPanel = new PersonalInfoPanel();
        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
        setupKeyListener();
        pack();
        setLocationRelativeTo(null);
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (presenter == null) return;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP    -> presenter.onKeyUp();
                    case KeyEvent.VK_DOWN  -> presenter.onKeyDown();
                    case KeyEvent.VK_LEFT  -> presenter.onKeyLeft();
                    case KeyEvent.VK_RIGHT -> presenter.onKeyRight();
                }
            }
        });
    }

    @Override public void setPresenter(PresenterInterface p) { this.presenter = p; }

    @Override
    public void start() {
        promptAndConnect();
        setFocusable(true);
        requestFocusInWindow();
        setVisible(true);
    }

    private void promptAndConnect() {
        JTextField fldCode = new JTextField("student01", 14);
        JTextField fldHost = new JTextField(ClientConstants.DEFAULT_HOST, 14);
        JTextField fldPort = new JTextField(String.valueOf(ClientConstants.DEFAULT_PORT), 6);

        JPanel p = new JPanel(new java.awt.GridLayout(3, 2, 6, 6));
        p.add(new JLabel("Code:")); p.add(fldCode);
        p.add(new JLabel("Host:")); p.add(fldHost);
        p.add(new JLabel("Port:")); p.add(fldPort);

        if (JOptionPane.showConfirmDialog(null, p, "Connect", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) System.exit(0);

        int port = ClientConstants.DEFAULT_PORT;
        try { port = Integer.parseInt(fldPort.getText().trim()); } catch (Exception ignored) {}
        
        setTitle("Game Client — " + fldCode.getText().trim());
        presenter.connectToServer(fldHost.getText().trim(), port, fldCode.getText().trim());
    }

    @Override
    public void updateBoard(GameState state) {
        if (state != null && state.getPlayers() != null)
            gamePanel.updatePlayers(state.getPlayers());
    }

    @Override
    public void updatePersonalInfo(String role, int totalScore) {
        infoPanel.update(role, totalScore);
    }

    @Override
    public void showGameEnd(String reason) {
        JOptionPane.showMessageDialog(this, "Game ended: " + reason);
        closeAndExit();
    }

    @Override
    public void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(this, "Network Error:\n" + message, "Error", JOptionPane.ERROR_MESSAGE);
        closeAndExit();
    }

    private void closeAndExit() { dispose(); System.exit(0); }
}
