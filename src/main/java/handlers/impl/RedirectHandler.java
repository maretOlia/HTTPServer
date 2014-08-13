package handlers.impl;

import handlers.annotations.Mapping;
import handlers.api.URIHandler;
import io.netty.handler.codec.http.HttpRequest;

/**
 * Created by W on 10.08.2014.
 */
@Mapping(uri = "/redirect_page")
public class RedirectHandler implements URIHandler {
    @Override
    public void handle(HttpRequest request, StringBuilder builder) {
        builder.append("Page was redirected");
    }
}
