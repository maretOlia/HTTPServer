package handlers.api;

import io.netty.handler.codec.http.HttpRequest;

/**
 * URIHandler is the base interface for all URL-handlers
 */
public interface URIHandler {

    void handle(HttpRequest request, StringBuilder builder);
}
