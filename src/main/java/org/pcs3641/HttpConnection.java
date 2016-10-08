package org.pcs3641;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnection extends WebConnection {

    HttpConnection(Socket connection) {
        super(connection);
    }

    protected void handleConnection(Socket connection) {
        HttpStatusCode responseStatus = new HttpStatusCode(HttpStatusCode.OK, "OK");
        byte[] payload = null;

        try {
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            HttpHeader header = new HttpHeader();
            header.parse(reader);

            if (!header.getMethod().equals("GET")) {
                throw new HttpStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED, header.getMethod() + " invalid here");
            }

            HttpFileLoader loader = new HttpFileLoader(header);
            payload = loader.read();
        } catch (IOException e) {
            System.out.println("Reading from client failed:");
            e.printStackTrace();
            responseStatus = new HttpStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR, "Error receiving client data");
        } catch (HttpStatusCode e) {
            System.out.println("HTTP error " + String.valueOf(e.getStatusCode()) + " - \"" + e.getMessage() + "\" generated:");
            e.printStackTrace();
            responseStatus = e;
        }

        byte[] responseHeader = new HttpHeader().buildResponse(responseStatus);
        if (responseStatus.getStatusCode() == HttpStatusCode.OK) {
            safeWrite(connection, responseHeader);
            safeWrite(connection, payload);
        } else {
            safeWrite(connection, responseHeader);
        }
        try {
            connection.close();
        } catch (IOException e) {
            System.out.println("IO error closing socket:");
            e.printStackTrace();
        }
    }

    private void safeWrite(Socket connection, byte[] data) {
        try {
            System.out.printf("Length %d\n", data.length);
            OutputStream stream = connection.getOutputStream();
            System.out.println(stream != null);
            new DataOutputStream(stream).write(data);
        } catch (IOException e) {
            System.out.println("Writing to client failed:");
            System.out.println(e.toString());
        }
    }
}
