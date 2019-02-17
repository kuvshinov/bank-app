package com.kuvshinov.http.server;

import com.kuvshinov.http.server.exceptions.NotFoundException;
import com.kuvshinov.http.server.exceptions.ValidationException;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Dispatch incoming requests to contexts.
 *
 * @author Sergei Kuvshinov
 */
class RequestDispatcher {

    private final Map<String, RequestHandler> requestHandlers = new HashMap<>();

    RequestDispatcher(Map<String, RequestHandler> requestHandlers) {
        this.requestHandlers.putAll(requestHandlers);
    }

    /**
     *
     * @param request
     * @return
     */
    ResponseEntity dispatch(HttpRequest request) {
        String path = request.getPath();
        String context = Optional.ofNullable(findContext(path))
                .orElseThrow(() -> new NotFoundException(String.format("Path %s doesn't exists", path)));
        RequestHandler handler = requestHandlers.get(context);
        switch (request.getHttpMethod()) {
            case POST:
                return handler.doPost(request);
            case PUT:
                return handler.doPut(request);
            case DELETE:
                return handler.doDelete(request);

        }
        return handler.doGet(request);
    }

    private String findContext(String path) {
        if (path == null || !path.startsWith("/")) {
            throw new ValidationException("Requested path must be started with '/'");
        }

        if (requestHandlers.containsKey(path)) {
            return path;
        }

        String parent = Paths.get(path).getParent().toString();
        if (requestHandlers.containsKey(parent)) {
            return parent;
        }

        return null;
    }
}
