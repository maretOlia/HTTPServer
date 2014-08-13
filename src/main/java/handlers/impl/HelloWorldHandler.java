package handlers.impl;

import handlers.annotations.Mapping;
import handlers.api.URIHandler;
import io.netty.handler.codec.http.HttpRequest;


/**
 * Created by W on 08.08.2014.
 */
@Mapping(uri = "/hello")
public class HelloWorldHandler implements URIHandler {


    @Override
    public void handle(HttpRequest request, StringBuilder builder) {

        builder.append("Hello World");
//      10 seconds delay
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}