package org.pcs3641;

import java.net.Socket;

public class HttpConnection extends WebConnection {

    HttpConnection(Socket connection) {
        super(connection);
    }

    protected void handleConnection(Socket connection) {

    }
}
