package serverBoot;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;
import statistic.Statistic;

import java.util.Calendar;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by W on 19.08.2014.
 */
public class DelayedResponse implements Runnable {
    ChannelHandlerContext ctx;
    StringBuilder builder;

    public DelayedResponse(ChannelHandlerContext ctx, StringBuilder builder) {
        this.ctx = ctx;
        this.builder = builder;
    }

    @Override
    public void run() {

        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, OK, Unpooled.copiedBuffer(builder.toString(), CharsetUtil.UTF_8));

        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        response.headers().set(DATE, Calendar.getInstance().getTime());

        ctx.write(response);
        ctx.flush();
    }
}
