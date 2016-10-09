package org.pcs3641;

import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.*;

public class HttpHeaderTest {

    private HttpHeader parse(String text) throws HttpStatusCode {
        StringReader reader = new StringReader(text);
        HttpHeader header = new HttpHeader();
        header.parse(reader);
        return header;
    }

    @Test
    public void raisesOnInvalidHeader() throws HttpStatusCode {
        try {
            parse("GET / HTTP/1.0\r\nNo separator in this line\r\n");
        } catch (HttpStatusCode e) {
            assertEquals("Malformed header (missing colon)", e.getMessage());
        }
    }

    @Test
    public void raisesOnNonHttp() throws HttpStatusCode {
        try {
            parse("Name (some.host:yourlogin): anonymous\r\n");
            fail("Should have thrown exception");
        } catch (HttpStatusCode e) {
            assertEquals("Only HTTP headers are supported", e.getMessage());
        }
    }


    @Test
    public void raisesOnUnknownMethod() {
        try {
            parse("EAT /page.html HTTP/1.0\r\n");
            fail("Should have thrown exception");
        } catch (HttpStatusCode e) {
            assertEquals("Method EAT unknown", e.getMessage());
        }
    }

    @Test
    public void parsesFields() throws HttpStatusCode {
        HttpHeader header = parse("GET /page.html HTTP/1.0\r\n" +
        "User-Agent: Mozilla/5.0\r\n" +
        "Host: some.host.com\r\n" +
        "Accept: image/gif, image/x-bitmap, image/jpeg, */*\r\n\r\n");
        assertEquals("GET", header.getMethod());
        assertEquals("/page.html", header.getPage());
        assertEquals("1.0", header.getVersion());
        for (String key : new String[] {"user-agent", "host", "accept"}) {
            assert header.getFields().get(key) != null;
        }
        assertEquals("Mozilla/5.0", header.getField("User-Agent"));
        assertEquals("some.host.com", header.getField("Host"));
        assertEquals("image/gif, image/x-bitmap, image/jpeg, */*", header.getField("Accept"));
    }

    @Test
    public void handlesCaseInsensitive() throws HttpStatusCode {
        HttpHeader header = parse("GET /page.html HTTP/1.0\r\n" +
                "User-Agent: Mozilla/5.0\r\n\r\n");
        assertEquals("Mozilla/5.0", header.getField("User-Agent"));
        assertEquals("Mozilla/5.0", header.getField("user-agent"));
        assertEquals("Mozilla/5.0", header.getField("USER-AGENT"));
    }
}