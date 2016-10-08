package org.pcs3641;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Dictionary;
import java.util.Hashtable;

public class HttpHeader {
    private String type;
    private String page;
    private String version;
    private Hashtable<String, String> fields;

    public void parse(Reader rawReader) throws HttpParseException {
        BufferedReader reader = new BufferedReader(rawReader);

        parseRequestType(readLine(reader));

        fields = new Hashtable<>();
        for (String line = readLine(reader); !line.isEmpty(); line = readLine(reader)) {
            parseField(line);
        }
    }

    public String getType() {
        return type;
    }

    public String getPage() {
        return page;
    }

    public String getVersion() {
        return version;
    }

    public Dictionary<String, String> getFields() {
        return fields;
    }

    public String getField(String key) {
        return fields.get(key.toLowerCase());
    }

    private String readLine(BufferedReader reader) throws HttpParseException {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new HttpParseException("IOException: " + e.toString());
        }
    }

    private void parseRequestType(String line) throws HttpParseException {
        String[] fields = line.split(" ");

        if (fields.length < 3 || !fields[2].startsWith("HTTP/")) {
            throw new HttpParseException("Header is not HTTP");
        }

        type = fields[0];
        page = fields[1];
        version = fields[2].substring(5);
    }

    private void parseField(String line) throws HttpParseException {
        String[] keyValue = line.split(":", 2);

        if (keyValue.length != 2) {
            throw new HttpParseException("Header field without colon");
        }

        fields.put(keyValue[0].toLowerCase(), keyValue[1].replaceAll("^\\s+", ""));
    }
}
