package netty.time.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 实现一个基于服务端回复时间的客户端
 * 1. 连接服务端
 * 2. 接收和处理服务端回复的时间戳，转化成人类可读的字符串
 * Created by hzyouzhihao on 2016/9/10.
 */
public class TimeClient {

    private int port = 8080;

    private String host = "127.0.0.1";

    public TimeClient() {
    }

    public TimeClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        // 创建客户端和创建服务端最大的不同点是，使用了不同的Bootstrap和Channel实现
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // Bootstrap类似于ServerBootstrap，不同点在于创建的是非服务端的channel
            Bootstrap bootstrap = new Bootstrap();
            // 这里指定了一个EventLoopGroup，同时用于boos和worker
            // 通常客户端不需要单独指定一个EventLoopGroup用作boos
            bootstrap.group(workGroup);
            // 指定客户端专用channel
            bootstrap.channel(NioSocketChannel.class);
            // 初始化channel，不考虑二进制流分包的问题
            // initChannel1(bootstrap);
            // 初始化channel，处理数据分包问题
            initChannel2(bootstrap);
            // 这里不像服务端需要配置childOption，因为客户端只有NioSocketChannel。
            // 而服务端是先有父的NioServerSocketChannel(通过option方法配置)，后有子的NioSocketChannel（通过childOption方法配置）
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
        }
    }

    /**
     * 初始化channel，添加handler（不考虑数据分包问题）
     */
    public void initChannel1(Bootstrap bootstrap) {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TimeClientHandler());
            }
        });
    }

    /**
     * 初始化channel，添加handler（处理数据分包问题）
     */
    public void initChannel2(Bootstrap bootstrap) {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new TimeDecoder(), new TimeClientHandler());
            }
        });
    }

    public static void main(String[] args) throws Exception {
        new TimeClient().run();
    }
}
