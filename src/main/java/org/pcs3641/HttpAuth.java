package org.pcs3641;

import java.util.Base64;

public class HttpAuth {
    private String authorization;

    public HttpAuth(HttpHeader header) {
        authorization = header.getField("Authorization");
    }

    public void enforce() throws HttpStatusCode {
        if (authorization == null || !"Basic".equalsIgnoreCase(authorization.substring(0, 5))) {
            throw new HttpStatusCode(HttpStatusCode.UNAUTHORIZED, "Invalid login");
        }

        String encodedUserPass = authorization.substring(6).replaceAll("^\\s+", "");
        String[] userPass = new String(Base64.getDecoder().decode(encodedUserPass)).split(":", 2);
        if (userPass.length != 2) {
            throw new HttpStatusCode(HttpStatusCode.UNAUTHORIZED, "Can't decode authentication string");
        }

        String pass = HttpServer.getConfigSection("Users").get(userPass[0]);
        if (pass == null || !pass.equals(userPass[1])) {
            throw new HttpStatusCode(HttpStatusCode.UNAUTHORIZED, "Invalid login");
        }
    }
}
