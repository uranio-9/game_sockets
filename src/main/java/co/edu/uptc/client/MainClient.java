package co.edu.uptc.client;

import co.edu.uptc.client.presenter.ClientRunner;

/**
 * Application entry point.
 * Single responsibility: instantiate ClientRunner and call start().
 */
public class MainClient {
    public static void main(String[] args) {
        new ClientRunner().start();
    }
}
