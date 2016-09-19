package netty.demo2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Calendar;

/**
 * Created by hzyouzhihao on 2016/9/10.
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        Calendar calendar = Calendar.getInstance();
        byteBuf.writeInt(calendar.get(Calendar.HOUR_OF_DAY));
        final ChannelFuture channelFuture = ctx.writeAndFlush(byteBuf);
        //因为写入的动作是异步进行的,这里需要关闭和客户端的连接,因此需要添加写入完成事件,等待写入操作完成后再关闭连接
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (channelFuture == future) {
                    System.out.println("关闭channel");
                    ctx.close();
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
