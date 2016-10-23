package org.pcs3641;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpConnection extends WebConnection {
	private DataOutputStream log;
	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");

    HttpConnection(Socket connection) {
        super(connection);
        FileOutputStream logFile;
        try {
               logFile = new FileOutputStream("/var/log/pcs3641_http/access.log", true);
               log = new DataOutputStream(logFile);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open 'log.txt' file:");
            System.out.println(e.toString());
            log = null;
        }

    }

    protected void handleConnection(Socket connection) {
        HttpStatusCode responseStatus = new HttpStatusCode(HttpStatusCode.OK, "OK");
        byte[] rawResponseHeader;
        byte[] rawResponsePayload;
        HttpHeader header = new HttpHeader();

        try {
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
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
            responseStatus = errorCode;
            rawResponseHeader = new HttpHeader().buildResponse(errorCode);
            rawResponsePayload = new HttpErrorPage(errorCode).buildPage();
        } catch (HttpStatusCode e) {
            System.out.println("HTTP error " + String.valueOf(e.getStatusCode()) + " - \"" + e.getMessage() + "\" generated:");
            e.printStackTrace();
            HttpHeader responseHeader = new HttpHeader();
            if (e.getStatusCode() == HttpStatusCode.UNAUTHORIZED) {
                responseHeader.setField("WWW-Authenticate", "Basic realm=\"System Administrator\"");
            }
            responseStatus = e;
            rawResponseHeader = responseHeader.buildResponse(e);
            rawResponsePayload = new HttpErrorPage(e).buildPage();
        }
        safeWrite(connection, rawResponseHeader);
        safeWrite(connection, rawResponsePayload);
        logConnection(connection, header, responseStatus, rawResponsePayload);
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

    private void logConnection(Socket connection, HttpHeader requestHeader, HttpStatusCode responseStatus, byte[] rawResponsePayload) {
    	try {
    		String ip = connection.getInetAddress().getHostAddress();
    		String host = requestHeader.getField("host", "unknown");
        	String date = dateFormat.format(new Date());
        	String request = requestHeader.getStatusLine();
        	String status = Integer.toString(responseStatus.getStatusCode());
        	String payload = Integer.toString(rawResponsePayload.length);
    		log.writeBytes(ip + " " + host + " - [" + date + "]\n\"" + request + "\" " + status + " " + payload + "\n\n");
    	} catch (IOException e) {
    		System.out.println("Unable to write to log:");
            System.out.println(e.toString());
    	}
    }
}
