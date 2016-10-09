package org.pcs3641;

import java.net.Socket;

public abstract class WebConnection implements Runnable {
    private Socket connection;

    abstract protected void handleConnection(Socket connection);

    WebConnection(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        handleConnection(connection);
    }
}
