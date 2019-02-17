package com.kuvshinov.http.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuvshinov.http.server.exceptions.HttpVersionNotSupportedException;
import com.kuvshinov.http.server.exceptions.NotFoundException;
import com.kuvshinov.http.server.exceptions.NotImplementedException;
import com.kuvshinov.http.server.exceptions.ValidationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Optional;

/**
 * Process incoming request.
 *
 * @author Sergei Kuvshinov
 */
class HttpProcessor extends Thread {

    private static final String PROTOCOL = "HTTP/1.1";

    private final SocketChannel socketChannel;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final RequestDispatcher requestDispatcher;
    private final ObjectMapper objectMapper;

    HttpProcessor(SocketChannel socketChannel, RequestDispatcher requestDispatcher, ObjectMapper objectMapper) throws IOException {
        Objects.requireNonNull(socketChannel);
        Objects.requireNonNull(requestDispatcher);
        this.socketChannel = socketChannel;
        this.requestDispatcher = requestDispatcher;
        this.objectMapper = objectMapper;
        this.reader = new BufferedReader(new InputStreamReader(socketChannel.socket().getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socketChannel.socket().getOutputStream()));
    }

    @Override
    public void run() {
        try {
            System.out.println("Handle request from " + socketChannel.getRemoteAddress());
            //read request
            HttpRequest httpRequest = parseRequest();
            //handle request
            ResponseEntity response = requestDispatcher.dispatch(httpRequest);
            //write response
            writeResponse(response);
        } catch (HttpVersionNotSupportedException e) {
            e.printStackTrace();
            writeResponse(createError(HttpStatus.VERSION_NOT_SUPPORTED, e.getMessage()));
        } catch (NotImplementedException e) {
            e.printStackTrace();
            writeResponse(createError(HttpStatus.NOT_IMPLEMENTED, e.getMessage()));
        } catch (NotFoundException e) {
            e.printStackTrace();
            writeResponse(createError(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (ValidationException e) {
            e.printStackTrace();
            writeResponse(createError(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            writeResponse(createError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        } finally {
            close();
        }
    }

    private void close() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpRequest parseRequest() throws IOException, NotImplementedException {
        HttpRequest request = new HttpRequest();
        HttpMethod method;
        String[] startingLine = reader.readLine().split(" ");
        //validate starting line
        if (startingLine.length != 3) {
            throw new ValidationException("It's not HTTP/1.1 request");
        }

        try {
            method = HttpMethod.valueOf(startingLine[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotImplementedException(String.format("Method %s not supported", startingLine[0]));
        }
        request.setHttpMethod(method);

        String path, query = null;
        int queryIndex = startingLine[1].lastIndexOf('?');
        if (queryIndex != -1) {
            query = startingLine[1].substring(queryIndex + 1);
            path = startingLine[1].substring(0, queryIndex);
        } else {
            path = startingLine[1];
        }
        request.setPath(path);
        request.setQuery(Optional.ofNullable(query).orElse(""));

        //read headers
        String nextHeader = reader.readLine();
        while (nextHeader != null && !nextHeader.isEmpty()) {
            String[] header = nextHeader.split(": ");
            request.addHeader(header[0], header[1]);
            nextHeader = reader.readLine();
        }

        //read message body
        int contentSize = 0;
        String headerContentLength = request.getHeader("content-length");
        if (headerContentLength != null) {
            contentSize = Integer.parseInt(headerContentLength);
        }
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentSize; i++) {
            body.append((char) reader.read());
        }
        request.setBody(body.toString());
        return request;
    }

    private void writeResponse(ResponseEntity entity) {
        StringBuilder response = new StringBuilder();
        //add starting line [protocol] [status_code] [message]
        response.append(PROTOCOL)
                .append(" ")
                .append(entity.getStatus().toString())
                .append("\r\n");
        //add headers
        response.append("Content-type: application/json\r\n");
        response.append("Connection: close\r\n");
        //write response body
        try {
            if (entity.getBody() != null) {
                String body = objectMapper.writeValueAsString(entity.getBody());
                response.append("content-length: ")
                        .append(body.length())
                        .append("\r\n\r\n")
                        .append(body);
            }
            response.append("\r\n");
            writer.write(response.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ResponseEntity<ErrorDto> createError(HttpStatus status, String message) {
        ErrorDto errorDto = new ErrorDto(message);
        ResponseEntity<ErrorDto> responseEntity = new ResponseEntity<>(status);
        responseEntity.setBody(errorDto);
        return responseEntity;
    }
}
