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
        byte[] rawResponseHeader;
        byte[] rawResponsePayload;

        try {
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            HttpHeader header = new HttpHeader();
            header.parse(reader);

            if (!header.getMethod().equals("GET")) {
                throw new HttpStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED, header.getMethod() + " invalid here");
            }

            HttpFileLoader loader = new HttpFileLoader(header);
            rawResponsePayload = loader.read();

            HttpHeader responseHeader = new HttpHeader();
            responseHeader.setField("Content-type", loader.getMime());
            rawResponseHeader = responseHeader.buildResponse((responseStatus));
        } catch (IOException e) {
            System.out.println("Reading from client failed:");
            e.printStackTrace();
            HttpStatusCode errorCode = new HttpStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR, "Error receiving client data");
            rawResponseHeader = new HttpHeader().buildResponse(errorCode);
            rawResponsePayload = new HttpErrorPage(errorCode).buildPage();
        } catch (HttpStatusCode e) {
            System.out.println("HTTP error " + String.valueOf(e.getStatusCode()) + " - \"" + e.getMessage() + "\" generated:");
            e.printStackTrace();
            HttpHeader responseHeader = new HttpHeader();
            if (e.getStatusCode() == HttpStatusCode.UNAUTHORIZED) {
                responseHeader.setField("WWW-Authenticate", "Basic realm=\"System Administrator\"");
            }
            rawResponseHeader = responseHeader.buildResponse(e);
            rawResponsePayload = new HttpErrorPage(e).buildPage();
        }
        safeWrite(connection, rawResponseHeader);
        safeWrite(connection, rawResponsePayload);
        try {
            connection.close();
        } catch (IOException e) {
            System.out.println("IO error closing socket:");
            e.printStackTrace();
        }
    }

    private void safeWrite(Socket connection, byte[] data) {
        try {
            OutputStream stream = connection.getOutputStream();
            new DataOutputStream(stream).write(data);
        } catch (IOException e) {
            System.out.println("Writing to client failed:");
            System.out.println(e.toString());
        }
    }
}
