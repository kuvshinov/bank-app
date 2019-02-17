package com.kuvshinov.http.server;

public enum  HttpStatus {

    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad request"),
    NOT_FOUND(404, "Not found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

    private int code;

    private String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%d %s", code, message);
    }
}
