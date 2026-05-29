package co.edu.uptc.server.view;

import co.edu.uptc.server.interfaces.PresenterInterface;
import co.edu.uptc.server.interfaces.ViewInterface;
import co.edu.uptc.shared.pojo.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ServerFrame extends JFrame implements ViewInterface {
    private PresenterInterface presenter;
    private GamePanel gamePanel;
    private SidePanel sidePanel;

    public ServerFrame() {
        setTitle("Game Server Operator View");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gamePanel = new GamePanel();
        sidePanel = new SidePanel();

        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null); // Center on screen
    }

    @Override
    public void setPresenter(PresenterInterface presenter) {
        this.presenter = presenter;
        sidePanel.getBtnStart().addActionListener(e -> presenter.onStartGameClicked());
        sidePanel.getBtnFinish().addActionListener(e -> presenter.onFinishGameClicked());
    }

    @Override
    public void start() {
        int port = promptPort();
        presenter.startNetwork(port);
        setVisible(true);
    }

    private int promptPort() {
        String in = JOptionPane.showInputDialog(this, "Puerto del servidor:", "8080");
        try { return Integer.parseInt(in.trim()); } 
        catch (Exception e) { return 8080; }
    }

    @Override
    public void updateBoard(List<Player> players, int globalGoals) {
        gamePanel.updatePlayers(players);
        sidePanel.updateState(players, globalGoals);
    }
}
