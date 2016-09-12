package netty.demo3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by hzyouzhihao on 2016/9/12.
 */
public class StrServer {

    private int port = 8080;
    public StrServer() {
    }
    public StrServer(int port) {
        this.port = port;
    }
    public void run() throws Exception {
        EventLoopGroup boos = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrapServer = new ServerBootstrap();
            bootstrapServer.group(boos, work);
            bootstrapServer.channel(NioServerSocketChannel.class);
            bootstrapServer.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MessageDecoder(), new ServerHandler());
                }
            });
            bootstrapServer.option(ChannelOption.SO_BACKLOG, 128);
            bootstrapServer.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = bootstrapServer.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            boos.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        new StrServer().run();
    }
}
