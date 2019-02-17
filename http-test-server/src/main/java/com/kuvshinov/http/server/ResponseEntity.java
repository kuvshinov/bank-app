package com.kuvshinov.http.server;

public class ResponseEntity<T> {

    private final HttpStatus status;
    private T body;

    public ResponseEntity(HttpStatus status) {
        this.status = status;
    }

    public ResponseEntity(HttpStatus status, T body) {
        this.status = status;
        this.body = body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static ResponseEntity ok() {
        return new ResponseEntity(HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(HttpStatus.OK, body);
    }

    public static ResponseEntity notFound() {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity internalError() {
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
