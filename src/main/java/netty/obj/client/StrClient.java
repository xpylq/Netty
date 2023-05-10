package netty.obj.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

/**
 * Created by hzyouzhihao on 2016/9/12.
 */
public class StrClient {

    private String host = "127.0.0.1";

    private int port = 8080;
    public StrClient() {
    }
    public StrClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void run() throws Exception {
        NioEventLoopGroup work = new NioEventLoopGroup();
        ChannelFuture f = null;
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(work);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MessageEncoder());
                }
            });
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            f = bootstrap.connect(host, port).sync();
            while (true) {
                Scanner in = new Scanner(System.in);
                String content = in.nextLine();
                f.channel().writeAndFlush(content);
            }
        } finally {
            if (f != null) {
                f.channel().closeFuture().sync();
                work.shutdownGracefully();
            }
        }
    }
    public static void main(String[] args) throws Exception {
        new StrClient().run();
    }
}
