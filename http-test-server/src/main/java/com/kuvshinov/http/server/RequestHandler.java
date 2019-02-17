package com.kuvshinov.http.server;

/**
 * Handle incoming request for specific HTTP method.
 *
 * @author Sergei Kuvshinov
 */
public interface RequestHandler {

    /**
     * Handle GET request.
     *
     * @param request
     * @return
     */
    default ResponseEntity<?> doGet(HttpRequest request) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle POST request.
     *
     * @param request
     * @return
     */
    default ResponseEntity<?> doPost(HttpRequest request) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle PUT request.
     *
     * @param request
     * @return
     */
    default ResponseEntity<?> doPut(HttpRequest request) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Handle DELETE request.
     * @param request
     * @return
     */
    default ResponseEntity<?> doDelete(HttpRequest request) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

}
