package netty.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by hzyouzhihao on 2016/9/7.
 */
public class DiscardServer {

    private int port = 8080;
    public DiscardServer() {
    }
    public DiscardServer(int port) {
        this.port = port;
    }
    public void run() throws Exception {
        /**
         * NioEventLoopGroup 可以看作一个多线程的用于处理IO操作的线程池
         * Netty提供了大量不同的EventLoopGroup实现，来针对不同类型的传输
         * 一般server端有两个EventLoopGroup
         * 1. boosGroup: 负责接收新的连接，并将新的连接注册给对应的workGroup
         * 2. workGroup: 负责处理连接后续的消息通信
         * 关于EventLoopGroup中包含多少个线程池，并且线程和channel的对应关系是怎么样的，需要参考每一个具体的EventLoopGroup实现类
         */
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // ServerBootstrap 是一个帮助类，用于快速配置、组装并启动一个netty server
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            // ChannelInitializer 是一个特殊的handler.当一个新的channel建立后，用于初始化这个新的channel。
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DiscardServerHandler());
                }
            });
            // 配置NioServerSocketChannel，这个channel用于接收和创建新的channel
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            // 配置被NioServerSocketChannel创建出来的新的channel
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 指定端口号，并启动服务
            ChannelFuture f = bootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        new DiscardServer().run();
    }
}
