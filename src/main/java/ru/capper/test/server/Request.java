package ru.capper.test.server;

import io.netty.handler.codec.http.FullHttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * The Request class provides convenience helpers to the underyling
 * HTTP Request.
 */
public class Request {
    private final FullHttpRequest request;
    private Map<String, List<String>> query;

    public Map<String, List<String>> getQuery() {
        return query;
    }

    public void setQuery(Map<String, List<String>> query) {
        this.query = query;
    }

    /**
     * Creates a new Request.
     *
     * @param request The Netty HTTP request.
     */
    public Request(final FullHttpRequest request) {
        this.request = request;
    }


    /**
     * Returns the body of the request.
     *
     * @return The request body.
     */
    public String body() {
        return request.content().toString(StandardCharsets.UTF_8);
    }

}
