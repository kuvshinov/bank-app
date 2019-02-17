package com.kuvshinov.http.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Simple REST server.
 *
 * @author Sergei Kuvshinov
 */
public class Server {

    private static final String HOST = "127.0.0.1";

    private final int port;
    private final RequestDispatcher requestDispatcher;
    private final ObjectMapper objectMapper;

    Server(int port, Map<String, RequestHandler> handlers) {
        Objects.requireNonNull(handlers);
        this.port = port;
        this.requestDispatcher = new RequestDispatcher(handlers);
        this.objectMapper = new ObjectMapper();
    }

    void start() {
        System.out.println("Starting server on port " + port);
        InetSocketAddress address = new InetSocketAddress(HOST, port);
        try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {
            if (socketChannel.isOpen()) {
                socketChannel.configureBlocking(true);
                socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 1024 * 4);
                socketChannel.bind(address);
                System.out.println("Server was started. Waiting for connections...");
                while (true) {
                    SocketChannel channel = socketChannel.accept();
                    HttpProcessor httpHandler = new HttpProcessor(channel, requestDispatcher, objectMapper);
                    httpHandler.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerBootstrap bootstrap() {
        return new ServerBootstrap();
    }

    public static class ServerBootstrap {
        private Map<String, RequestHandler> handlers = new HashMap<>();
        private int port;

        public ServerBootstrap port(int port) {
            this.port = port;
            return this;
        }

        public ServerBootstrap addRequestHandler(String path, RequestHandler handler) {
            handlers.put(path, handler);
            return this;
        }

        public void start() {
            new Server(port, handlers).start();
        }
    }
}
