package serverBoot;

import io.netty.channel.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import static io.netty.channel.ChannelOption.*;

/**
 * Created by W on 08.08.2014.
 */
public class HTTPServer {
    private final int port;

    public HTTPServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
//      creating event loops with default settings
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());


            Channel ch = b.bind(port).sync().channel();

            ch.closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}



