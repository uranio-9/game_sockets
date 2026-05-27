package co.edu.uptc.server.presenter;

import co.edu.uptc.server.config.Constants;
import co.edu.uptc.server.model.GameModel;
import co.edu.uptc.server.net.NetServer;
import co.edu.uptc.server.view.ServerFrame;

import javax.swing.*;
import java.io.IOException;

public class ServerRunner {
    public void start() {
        // Setup dependencies
        GameModel model = new GameModel();
        ServerFrame view = new ServerFrame();
        ServerPresenter presenter = new ServerPresenter();

        // Inject
        presenter.setModel(model);
        presenter.setView(view);

        model.setPresenter(presenter);
        view.setPresenter(presenter);

        // Prompt for port
        int port = Constants.DEFAULT_PORT;
        String portInput = JOptionPane.showInputDialog(
                null,
                "Ingrese el puerto del servidor:",
                String.valueOf(port)
        );

        if (portInput != null && !portInput.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Puerto inválido, usando default: " + port);
            }
        } else {
            System.out.println("Operación cancelada o puerto vacío, usando default: " + port);
        }

        NetServer server = new NetServer(port, presenter);
        presenter.setBroadcaster(server);

        try {
            server.startServer();
            SwingUtilities.invokeLater(() -> {
                view.start();
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al iniciar servidor en puerto " + port + ": " + e.getMessage());
            System.exit(1);
        }
    }
}
