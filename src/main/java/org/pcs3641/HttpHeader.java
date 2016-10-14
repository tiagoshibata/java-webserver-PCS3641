package org.pcs3641;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class HttpHeader {
    private String method;
    private String page;
    private String version;
    private Hashtable<String, String> fields = new Hashtable<>();

    public void parse(Reader rawReader) throws HttpStatusCode {
        BufferedReader reader = new BufferedReader(rawReader);

        parseStatusLine(readLine(reader));

        for (String line = readLine(reader); !line.isEmpty(); line = readLine(reader)) {
            parseField(line);
        }
    }

    public byte[] buildResponse(HttpStatusCode status) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.0 " + String.valueOf(status.getStatusCode()) + " " + status.getMessage() + "\r\n");

        Iterator<Map.Entry<String, String>> it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            response.append(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        response.append("\r\n");
        return response.toString().getBytes();
    }

    public String getMethod() {
        return method;
    }

    public String getPage() {
        return page;
    }

    public String getVersion() {
        return version;
    }
    
    public String getStatusLine() {
        return method + " " + page + " HTTP/" + version;
    }

    public Dictionary<String, String> getFields() {
        return fields;
    }

    public String getField(String key) {
        return fields.get(key.toLowerCase());
    }

    public String getField(String key, String defaultValue) {
        String value = fields.get(key.toLowerCase());
        return value != null ? value : defaultValue;
    }

    public void setField(String key, String value) {
        fields.put(key, value);
    }

    private String readLine(BufferedReader reader) throws HttpStatusCode {
        try {
            String line = reader.readLine();
            return (line == null) ? "" : line;
        } catch (IOException e) {
            throw new HttpStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR, "Error receiving client data");
        }
    }

    private void parseStatusLine(String line) throws HttpStatusCode {
        String[] fields = line.split(" ");

        if (fields.length < 3 || !fields[2].startsWith("HTTP/")) {
            throw new HttpStatusCode(HttpStatusCode.NOT_IMPLEMENTED, "Only HTTP headers are supported");
        }

        method = fields[0].toUpperCase();
        page = fields[1];
        version = fields[2].substring(5);

        if (!method.equals("GET") && !method.equals("POST")) {
            throw new HttpStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED, "Method " + method + " unknown");
        }
    }

    private void parseField(String line) throws HttpStatusCode {
        String[] keyValue = line.split(":", 2);

        if (keyValue.length != 2) {
            throw new HttpStatusCode(HttpStatusCode.BAD_REQUEST, "Malformed header (missing colon)");
        }

        fields.put(keyValue[0].toLowerCase(), keyValue[1].replaceAll("^\\s+", ""));
    }
}
