package org.pcs3641;

public class HttpErrorPage {
    private HttpStatusCode code;

    public HttpErrorPage(HttpStatusCode code) {
        this.code = code;
    }

    public byte[] buildPage() {
        String errorNum = String.valueOf(code.getStatusCode());
        return ("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" +
                "<html lang=\"en\"><head>" +
                    "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
                    "<title>" + errorNum + " ERROR</title>" +
                "</head>" +
                "<body>" +
                    "<h1>An error occurred:</h1><p>" + errorNum + " - " + code.getMessage() + "</p>" +
                "</body>" +
                "</html>").getBytes();
    }
}
