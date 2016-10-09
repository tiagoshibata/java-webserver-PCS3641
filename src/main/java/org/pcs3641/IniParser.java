package org.pcs3641;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Dictionary;
import java.util.Hashtable;

public class IniParser {
    private Hashtable<String, Hashtable<String, String>> sections;

    public IniParser() {}

    public IniParser(Path path) throws IOException {
        String data = new String(Files.readAllBytes(path));
        parse(data);
    }

    public void parse(String data) {
        String lines[] = data.split("\\r?\\n");
        String sectionName = "";
        Hashtable<String, String> section = new Hashtable<>();
        sections = new Hashtable<>();
        for (String line : lines) {
            line = line.replaceAll("^\\s+", "");
            if (line.length() == 0 || line.charAt(0) == ';') {
                continue;
            }

            if (line.charAt(0) == '[') {
                sections.put(sectionName, section);
                // start section
                sectionName = line.substring(1, line.indexOf("]"));
                section = new Hashtable<>();
            } else {
                String[] keyValue = line.split("=", 2);
                if (keyValue.length != 2) {
                    throw new RuntimeException("Malformed INI file: key/value line missing = symbol");
                }
                section.put(keyValue[0], keyValue[1]);
            }
        }
        sections.put(sectionName, section);
    }

    Dictionary<String, String> getSection(String name) {
        Dictionary<String, String> section = sections.get(name);
        return section != null ? section : new Hashtable<>();
    }
}
