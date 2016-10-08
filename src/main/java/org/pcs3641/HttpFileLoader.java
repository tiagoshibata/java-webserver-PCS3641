package org.pcs3641;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class HttpFileLoader {
    private static final String ROOT = "/srv/http";
    private String name;
    private byte[] data;

    HttpFileLoader(HttpHeader header) {
        String name = header.getPage();
        if (name.endsWith("/")) {
            name += "index.html";
        }
        this.name = name;
    }

    public byte[] read() throws HttpStatusCode {
        File file = new File(ROOT, name);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new HttpStatusCode(HttpStatusCode.NOT_FOUND, name + " not found");
        }
    }

    public String getMime() {
        if (name.endsWith(".html") || name.endsWith(".htm"))
            return "text/html";
        if (name.endsWith(".txt") || name.endsWith(".java"))
            return "text/plain";
        if (name.endsWith(".gif"))
            return "image/gif";
        if (name.endsWith(".class"))
            return "application/octet-stream";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg"))
            return "image/jpeg";
        try {
            return Files.probeContentType(FileSystems.getDefault().getPath(name));
        } catch (IOException e) {
            return "text/plain";
        }
    }
}
