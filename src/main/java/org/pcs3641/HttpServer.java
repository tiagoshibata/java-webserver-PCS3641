package org.pcs3641;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Dictionary;

public class HttpServer {
    private static IniParser config;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            server.run();
        } catch (IOException e) {
            System.out.println("Exception occurred: " + e.toString());
        }
    }

    public static Dictionary<String, String> getConfigSection(String section) {
        if (config == null) {
            try {
                config = new IniParser(Paths.get("/etc/pcs3641_http.ini"));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Can't read config file");
            }
        }
        return config.getSection(section);
    }

    public void run() throws IOException {
        System.out.println("PCS3641 HTTP Server starting");
        ServerSocket serverSocket = new ServerSocket(8080);

        for (;;) {
            Socket connection = serverSocket.accept();
            Runnable runnable = new HttpConnection(connection);
            new Thread(runnable).start();
        }
    }
}
