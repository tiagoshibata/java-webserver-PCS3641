package org.pcs3641;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            server.run();
        } catch (IOException e) {
            System.out.println("Exception occurred: " + e.toString());
        }
    }

    public void run() throws IOException {
        System.out.println("PCS3641 HTTP Server starting");
        ServerSocket serverSocket = new ServerSocket(8090);

        for (;;) {
            Socket connection = serverSocket.accept();
            Runnable runnable = new HttpConnection(connection);
            new Thread(runnable).start();
        }
    }
}
