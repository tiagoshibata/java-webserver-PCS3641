package org.pcs3641;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Dictionary;

public class HttpFileLoader {
    private String root;
    private String name;

    HttpFileLoader(HttpHeader header) throws HttpStatusCode {
        String name = header.getPage();
        if (name.endsWith("/")) {
            name += "index.html";
        }
        this.name = name;

        if (name.contains("secure")) {

        }

        String virtualHost = header.getField("Host", "default").split(":", 2)[0];  // ignore port
        Dictionary<String, String> virtualHosts = HttpServer.getConfigSection("VirtualHosts");
        root = virtualHosts.get(virtualHost);
        if (root == null) {
            root = virtualHosts.get("default");
            if (root == null) {
                throw new HttpStatusCode(HttpStatusCode.BAD_REQUEST, "Invalid virtual host and no default host configured");
            }
        }
    }

    public byte[] read() throws HttpStatusCode {
        File file = new File(root, name);
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
