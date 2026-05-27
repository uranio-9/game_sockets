package co.edu.uptc.client.view;

import co.edu.uptc.client.interfaces.PresenterInterface;
import co.edu.uptc.client.interfaces.ViewInterface;
import co.edu.uptc.shared.dto.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Main client window.
 *
 * PASSIVE MVP: captures arrow-key presses and delegates to the Presenter.
 * Never modifies any game-state variable.
 */
public class ClientFrame extends JFrame implements ViewInterface {

    private PresenterInterface presenter;

    private final ClientGamePanel  gamePanel;
    private final PersonalInfoPanel infoPanel;

    public ClientFrame(String studentCode) {
        super("Game Client — " + studentCode);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(0, 0));

        gamePanel = new ClientGamePanel();
        infoPanel = new PersonalInfoPanel();

        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

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

        pack();
        setLocationRelativeTo(null);
    }

    @Override public void setPresenter(PresenterInterface p) { this.presenter = p; }

    @Override
    public void start() {
        setFocusable(true);
        requestFocusInWindow();
        setVisible(true);
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
        String msg = "ALL_DONE".equals(reason)
                ? "¡Todos los jugadores han terminado! La partida ha concluido."
                : "El servidor ha finalizado la partida.";
        JOptionPane.showMessageDialog(this, msg, "Partida Finalizada", JOptionPane.INFORMATION_MESSAGE);
        closeAndExit();
    }

    @Override
    public void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(this,
                "Error de conexión:\n" + message, "Error de Red", JOptionPane.ERROR_MESSAGE);
        closeAndExit();
    }

    private void closeAndExit() { dispose(); System.exit(0); }
}
