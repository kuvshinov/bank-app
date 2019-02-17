package com.kuvshinov.http.server;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private HttpMethod httpMethod;
    private String path;
    private String query;
    private Map<String, String> headers = new HashMap<>();
    private String body;


    HttpRequest() {}

    void addHeader(String header, String value) {
        headers.put(header, value);
    }

    void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    void setBody(String body) {
        this.body = body;
    }

    void setPath(String path) {
        this.path = path;
    }

    void setQuery(String query) {
        this.query = query;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public String getBody() {
        return body;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

}
