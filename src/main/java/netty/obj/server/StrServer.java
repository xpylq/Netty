package netty.obj.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 服务端增加编解码器，
 * 1. 服务端接收到客户端发送的二进制数组，
 * 2. 通过MessageDecoder解码器，将二进制数组解码成Message实体对象，再进行后续处理
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
