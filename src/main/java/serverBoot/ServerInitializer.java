package serverBoot;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.CharsetUtil;


/**
 * Created by W on 08.08.2014.
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

//adding "trafficHandler" for traffic computing per chanel
        p.addLast("trafficHandler", new ChannelTrafficShapingHandler(0, 0, 100));
        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("encoder", new HttpResponseEncoder());
        p.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
//      adding "readTimeoutHandler" to close connection after 70 seconds without client activity
//      After 70 seconds ReadTimeoutException will be thrown and handled in "ServerHandler" in "exceptionCaught" method
        p.addLast("readTimeoutHandler", new ReadTimeoutHandler(70));
        p.addLast("handler", new ServerHandler());
    }
}
