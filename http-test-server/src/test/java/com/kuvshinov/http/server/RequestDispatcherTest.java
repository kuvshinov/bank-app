package com.kuvshinov.http.server;

import com.kuvshinov.http.server.exceptions.NotFoundException;
import com.kuvshinov.http.server.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestDispatcherTest {

    @Mock
    private HttpRequest request;
    @Mock
    private RequestHandler rootHandler;
    @Mock
    private RequestHandler endpointHandler;

    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Map<String, RequestHandler> paths = new HashMap<>();
        paths.put("/", rootHandler);
        paths.put("/some", endpointHandler);
        dispatcher = new RequestDispatcher(paths);
    }

    @ParameterizedTest(name = "[{index}] Should call {0} /some for path {1}")
    @CsvSource({
            "GET, /some",   "POST, /some",   "PUT, /some",   "DELETE, /some",
            "GET, /some/1", "POST, /some/1", "PUT, /some/1", "DELETE, /some/1",
    })
    void shouldCallEndpoint(HttpMethod method, String path) {
        when(request.getPath()).thenReturn(path);
        when(request.getHttpMethod()).thenReturn(method);

        dispatcher.dispatch(request);

        switch(method) {
            case GET:
                verify(endpointHandler, times(1)).doGet(request); break;
            case POST:
                verify(endpointHandler, times(1)).doPost(request); break;
            case PUT:
                verify(endpointHandler, times(1)).doPut(request); break;
            case DELETE:
                verify(endpointHandler, times(1)).doDelete(request);
        }
    }

    @ParameterizedTest(name = "[{index}] Should call {0} / for path {1}")
    @CsvSource({
            "GET, /",      "POST, /",      "PUT, /",      "DELETE, /",
            "GET, /other", "POST, /other", "PUT, /other", "DELETE, /other",
    })
    void shouldCallRootContext(HttpMethod method, String path) {
        when(request.getPath()).thenReturn(path);
        when(request.getHttpMethod()).thenReturn(method);

        dispatcher.dispatch(request);

        switch(method) {
            case GET:
                verify(rootHandler, times(1)).doGet(request); break;
            case POST:
                verify(rootHandler, times(1)).doPost(request); break;
            case PUT:
                verify(rootHandler, times(1)).doPut(request); break;
            case DELETE:
                verify(rootHandler, times(1)).doDelete(request);
        }
    }

    @Test
    void shouldThrowExceptionIfParentPathNotFound() {
        String path = "/another/1";
        when(request.getPath()).thenReturn(path);

        assertThrows(NotFoundException.class, () -> dispatcher.dispatch(request));
    }

    @Test
    void shouldThrowExceptionIfPathNotValid() {
        String path = "invalid";
        when(request.getPath()).thenReturn(path);

        assertThrows(ValidationException.class, () -> dispatcher.dispatch(request));
    }
}