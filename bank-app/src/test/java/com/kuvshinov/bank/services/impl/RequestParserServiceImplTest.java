package com.kuvshinov.bank.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuvshinov.http.server.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RequestParserServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RequestParserServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    class ParseBodyTest {

        @Test
        void shouldParseBody() throws IOException {
            String body = "{}";
            Object o = new Object();
            when(objectMapper.readValue(body, Object.class))
                    .thenReturn(o);

            Object result = service.parseBody(body, Object.class);

            assertEquals(o, result);
        }

        @Test
        void shouldThrowExceptionIfCannotParseBody() throws IOException {
            String body = "{}";
            when(objectMapper.readValue(body, Object.class))
                    .thenThrow(IOException.class);

            assertThrows(ValidationException.class, () -> service.parseBody(body, Object.class));
        }
    }

    @Nested
    class ParseIdTest {

        @Test
        void shouldParseId() {
            String path = "/some/1";

            Optional<Integer> result = service.parseId(path);

            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(1, result.get().intValue());
        }

        @Test
        void shouldReturnEmptyIfIdNotPresent() {
            String path = "/some";

            Optional<Integer> result = service.parseId(path);

            assertNotNull(result);
            assertFalse(result.isPresent());
        }

        @Test
        void shouldThrowExceptionIfIdNotValid() {
            String path = "/some/blabla";

            assertThrows(ValidationException.class, () -> service.parseId(path));
        }
    }
}