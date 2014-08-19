package handlers.impl;

import handlers.annotations.Mapping;
import handlers.api.URIHandler;


/**
 * Created by W on 08.08.2014.
 */
@Mapping(uri = "/hello")
public class HelloWorldHandler implements URIHandler {


    @Override
    public void handle(StringBuilder builder) {
        builder.append("Hello World");
    }
}