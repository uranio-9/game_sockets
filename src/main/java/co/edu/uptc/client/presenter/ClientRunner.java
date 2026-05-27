package co.edu.uptc.client.presenter;

import co.edu.uptc.client.config.ClientConstants;
import co.edu.uptc.client.net.NetClient;
import co.edu.uptc.client.view.ClientFrame;
import co.edu.uptc.shared.dto.ConnectMessage;

import javax.swing.*;
import java.io.IOException;

public class ClientRunner {

    public void start() {
        // ── Prompt for connection details ─────────────────────────────────────
        JTextField fldCode = new JTextField("student01", 14);
        JTextField fldHost = new JTextField(ClientConstants.DEFAULT_HOST, 14);
        JTextField fldPort = new JTextField(String.valueOf(ClientConstants.DEFAULT_PORT), 6);

        JPanel panel = new JPanel(new java.awt.GridLayout(3, 2, 6, 6));
        panel.add(new JLabel("Código de estudiante:")); panel.add(fldCode);
        panel.add(new JLabel("IP / Host del servidor:")); panel.add(fldHost);
        panel.add(new JLabel("Puerto:"));                panel.add(fldPort);

        int result = JOptionPane.showConfirmDialog(
                null, panel, "Conectar al servidor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) System.exit(0);

        String studentCode = fldCode.getText().trim();
        String host        = fldHost.getText().trim();
        int    port        = ClientConstants.DEFAULT_PORT;

        try {
            port = Integer.parseInt(fldPort.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Puerto inválido; usando " + ClientConstants.DEFAULT_PORT, "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        if (studentCode.isEmpty() || host.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Código y host son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // ── Build & wire components ───────────────────────────────────────────
        ClientPresenter presenter = new ClientPresenter();
        ClientFrame     view      = new ClientFrame(studentCode);
        NetClient       net       = new NetClient(presenter);

        presenter.setView(view);
        presenter.setNetClient(net);
        presenter.setStudentCode(studentCode);
        view.setPresenter(presenter);

        // ── Connect ───────────────────────────────────────────────────────────
        final int finalPort = port;
        try {
            net.connect(host, finalPort);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a " + host + ":" + finalPort + "\n" + e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // ── Send flat CONNECT handshake ───────────────────────────────────────
        // ConnectRequest is now the flat ConnectMessage DTO
        net.sendMessage(new ConnectMessage(studentCode));

        // ── Show window ───────────────────────────────────────────────────────
        SwingUtilities.invokeLater(view::start);
    }
}
