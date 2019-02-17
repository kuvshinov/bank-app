package com.kuvshinov.bank.services;

import java.util.Optional;

/**
 * Parse incoming request.
 *
 * @author Sergei Kuvshinov.
 */
public interface RequestParserService {

    /**
     * Parse request body.
     *
     * @param body - String representation of request body.
     * @param tClass - class object that you expect.
     * @param <T> - object that you expect.
     * @return instance of T.
     * @throws com.kuvshinov.http.server.exceptions.ValidationException parsing was failed.
     */
    <T> T parseBody(String body, Class<T> tClass);

    /**
     * Parse id from requested path.
     *
     * @param path - path from request.
     * @return <code>Optional.empty()</code> if id doesn't exists and {@link Optional} with id otherwise.
     * @throws com.kuvshinov.http.server.exceptions.ValidationException if path is not valid.
     */
    Optional<Integer> parseId(String path);
}
