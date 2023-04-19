package netty.time.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Calendar;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hzyouzhihao on 2016/9/10.
 */
@Slf4j
public class TimeServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 当和客户端的连接建立完毕后触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 获取当前的ByteBufAllocator对象，并指定分配8字节的ByteBuf对象，用于写入服务器当前时间戳
        // 关于ByteBuf对象:
        // 1. 包含读和写两个指针:
        //  * 写指针: 只有在写操作的时候，该指针才会增加
        //  * 读指针: 只有在读操作的时候，该指针才会增加
        // 2. 和java.nio.ByteBuffer比较，java.nio.ByteBuffer没有清晰的两个指针，从而无法清晰的标记当前缓存内容的头和尾，
        // 需要自己调用flip()方法，操作起来会相对复杂，且容易出错
        // 其他注意事项:
        // 使用nc当客户端，无法准确解析long（出现乱码），因此要将时间戳转成字符串再写入:
        // ByteBuf byteBuf = ctx.alloc().buffer();
        // byteBuf.writeBytes(String.valueOf(System.currentTimeMillis()).getBytes());
        long time = System.currentTimeMillis();
        log.info("time server: {}", time);
        ByteBuf byteBuf = ctx.alloc().buffer(8);
        // 发送给客户端当前服务端的时间戳
        byteBuf.writeLong(time);
        // ctx.writeAndFlush()或ctx.write()为异步方法，会返回一个ChannelFuture对象
        // ChannelFuture对象代表一个IO操作，但是这个操作可能还未执行，因为在Netty中所有操作都是异步的
        final ChannelFuture channelFuture = ctx.writeAndFlush(byteBuf);
        // 因为写入的动作是异步进行的,这里需要关闭和客户端的连接,因此需要添加写入完成事件,等待写入操作完成后再关闭连接
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (channelFuture == future) {
                    System.out.println("关闭channel");
                    // 这里注意，ctx.close()也是一个异步方法
                    ctx.close();
                }
            }
        });

        // 也可以直接使用内置ChannelFutureListener
        // channelFuture.addListener(ChannelFutureListener.CLOSE);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
