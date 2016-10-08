package org.pcs3641;

public class HttpStatusCode extends Exception {
    public final static int CONTINUE = 100;
    public final static int OK = 200;
    public final static int BAD_REQUEST = 400;
    public final static int UNAUTHORIZED = 401;
    public final static int FORBIDDEN = 403;
    public final static int NOT_FOUND = 404;
    public final static int METHOD_NOT_ALLOWED = 405;
    public final static int INTERNAL_SERVER_ERROR = 500;
    public final static int NOT_IMPLEMENTED = 501;

    private int code;

    public HttpStatusCode(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getStatusCode() {
        return code;
    }
}
