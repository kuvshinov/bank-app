package com.kuvshinov.bank.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuvshinov.bank.services.RequestParserService;
import com.kuvshinov.http.server.exceptions.ValidationException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link RequestParserService}.
 *
 * @author Sergei Kuvshinov
 */
public class RequestParserServiceImpl implements RequestParserService {

    private static final String PARSE_ERROR_MESSAGE = "Cannot parse request body";

    private final ObjectMapper objectMapper;

    public RequestParserServiceImpl(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper);
        this.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T parseBody(String body, Class<T> tClass) {
        try {
            return objectMapper.readValue(body, tClass);
        } catch (IOException e) {
            throw new ValidationException(PARSE_ERROR_MESSAGE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Integer> parseId(String path) {
        String[] pathVariables = path.substring(1).split("/");
        if (pathVariables.length < 2)
            return Optional.empty();


        try {
            return Optional.of(Integer.valueOf(pathVariables[1]));
        } catch (NumberFormatException e) {
            throw new ValidationException("Id should be an integer value");
        }
    }
}
