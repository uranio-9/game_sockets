package co.edu.uptc.client.interfaces;

import java.io.IOException;

public interface NetClientInterface {

    /** Open a TCP connection to the server. */
    void connect(String host, int port) throws IOException;

    /** Serialize {@code dto} to JSON and write it to the socket. */
    void sendMessage(Object dto);

    /** Close streams and socket cleanly. */
    void disconnect();
}
