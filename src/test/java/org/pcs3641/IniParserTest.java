package org.pcs3641;

import org.junit.Test;

import java.util.Dictionary;

import static org.junit.Assert.*;

public class IniParserTest {
    @Test
    public void parse() throws Exception {
        IniParser parser = new IniParser();
        parser.parse("[VirtualHosts]\n" +
                "public.pcs3641=/srv/http\r\n" +
                "private.pcs3641=/srv/private\n" +
                "; comment\n" +
                "[Users]\n" +
                "admin=admin_password");
        Dictionary<String, String> virtualHosts = parser.getSection("VirtualHosts");
        Dictionary<String, String> users = parser.getSection("Users");

        assertEquals("/srv/http", virtualHosts.get("public.pcs3641"));
        assertEquals("/srv/private", virtualHosts.get("private.pcs3641"));
        assertEquals("admin_password", users.get("admin"));
    }
}