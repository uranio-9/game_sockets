package co.edu.uptc.server;

import co.edu.uptc.server.presenter.ServerRunner;

public class MainServer {
    public static void main(String[] args) {
        ServerRunner runner = new ServerRunner();
        runner.start();
    }
}
