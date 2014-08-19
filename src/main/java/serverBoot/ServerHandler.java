package serverBoot;

import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import statistic.Logger;
import statistic.Statistic;
import handlers.annotations.Mapping;
import handlers.api.URIHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.lang.annotation.Annotation;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * ServerHandler represents main logic of application with
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequest request;
    private StringBuilder builder = new StringBuilder();
    private String redirectURL;
    private Map<String, URIHandler> allHandlers = new HashMap<String, URIHandler>();
    private CopyOnWriteArraySet<Integer> connections = new CopyOnWriteArraySet<Integer>();


    public ServerHandler() {
        if (allHandlers.isEmpty()) {
//          retrieving all existing URL-handlers from package and adding to storage
            Set<Class<?>> urlHandlers = HandlersLoader.loadHandlers();
            for (Class clazz : urlHandlers) {
                Annotation annotation = clazz.getAnnotation(Mapping.class);
                Mapping mappingAnnotation = (Mapping) annotation;
                String uri = mappingAnnotation.uri();
                try {
                    allHandlers.put(uri, (URIHandler) clazz.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
//      getting IP
        Channel channel = ctx.channel();
        SocketAddress clientIP = channel.remoteAddress();
        String[] parts = clientIP.toString().split("/{1}|:{1}");
        String ip = parts[1];
//      checking new connection
        int connectionID;
        connectionID = channel.hashCode();
        //if it is a new connection  - then increment connections counter
        if (connections.add(connectionID)) {
            Statistic.incrementOpenedConnections();
        }

        if (message instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) message;
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
//          retrieving user URL
            String userRequest = queryStringDecoder.path();
//          checking "redirect" request
            Map<String, List<String>> parameters = queryStringDecoder.parameters();
            boolean contains = parameters.containsKey("url");
//          checking if there is a handler for this request
            if (contains == true && (allHandlers.containsKey(parameters.get("url").get(0)))) {
                redirectURL = parameters.get("url").get(0);
//              refreshing redirect statistics
                Statistic.countRedirectRequests(redirectURL);
//              refresh logs
                refreshLogs(ctx, ip, userRequest);
//              redirect client
                writeResponseOnRedirect(ctx);
//              refresh statistic
                Statistic.refreshStatistic(ip, userRequest);
                return;
            }
//          cleaning buffer
            builder.setLength(0);
//          Looking for handler on this request
            URIHandler newHandler = allHandlers.get(userRequest);
            if (newHandler != null) {
                newHandler.handle(builder);

//              refresh statistic
                Statistic.refreshStatistic(ip, userRequest);
//              refresh logs
                refreshLogs(ctx, ip, userRequest);

//              checking if this request need a delay
                if (userRequest.equals("/hello")) {
//              retrieving EventLoop
                    EventLoop eventExecutors = channel.eventLoop();
//              creating non-blocking delay with 10 seconds
                    eventExecutors.schedule(new DelayedResponse(ctx, builder), 10, SECONDS);
                    return;
                }
//              preparing and writing response
                writeResponseOnSuccess(ctx);

            } else {
                builder.append("This web-page is not available");
//              preparing and writing response on bad request
                writeResponseOnFailure(ctx);
                return;
            }
        }
    }

    private boolean writeResponseOnFailure(ChannelHandlerContext ctx) {
        FullHttpResponse response;
        if (!request.getDecoderResult().isSuccess()) {
            response = new DefaultFullHttpResponse(
                    HTTP_1_1, BAD_REQUEST, Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));
        } else {
            response = new DefaultFullHttpResponse(
                    HTTP_1_1, NOT_FOUND, Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));
        }
        return processResponse(response, ctx);
    }

    private boolean writeResponseOnRedirect(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                (TEMPORARY_REDIRECT));
        return processResponse(response, ctx);
    }


    private boolean writeResponseOnSuccess(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1,
                (request.getDecoderResult().isSuccess() ? OK : BAD_REQUEST),
                Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));
        return processResponse(response, ctx);
    }

    private boolean processResponse(FullHttpResponse response, ChannelHandlerContext ctx) {
        if (isKeepAlive(request)) {
            if (response.getStatus().equals(TEMPORARY_REDIRECT)) {
                response.headers().set(LOCATION, redirectURL);
            } else {
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            }
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            response.headers().set(DATE, Calendar.getInstance().getTime());
        }

        ctx.write(response);
        ctx.flush();
        return isKeepAlive(request);
    }

    public void refreshLogs(ChannelHandlerContext ctx, String ip, String userRequest) {
//      getting "ChannelHandler" instance from "pipeline"
        ChannelHandler handler = ctx.pipeline().get("trafficHandler");
        ChannelTrafficShapingHandler trafficHandler = (ChannelTrafficShapingHandler) handler;
//      getting TrafficCounter instance
        TrafficCounter trafficCounter = trafficHandler.trafficCounter();
//      turning of computing for retrieving results
        trafficCounter.stop();
//      creating object for storing logs
        Logger logger = new Logger(ip, userRequest, trafficCounter.lastTime(), trafficCounter.lastWrittenBytes(), trafficCounter.lastReadBytes());
//      add request statistic to the cache (limited storage on 16 last requests)
        Statistic.addRequestToStatistic(logger);
//      turning on computing
        trafficCounter.start();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        final int connectionID = ctx.channel().hashCode();
//        adding "ChannelFuture to do post-closing cleaning
        ctx.close().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
//              decrementing connection counter and removing connection ID from storage of opened connections
                Statistic.decrementOpenedConnections();
                connections.remove(connectionID);
            }
        });
    }
}

