package netty.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by hzyouzhihao on 2016/9/7.
 */
@Slf4j
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            String content = in.toString(CharsetUtil.UTF_8);
            log.info("discard 服务端接收到的数据: {}", content);
            //这里将客户端的信息再返回给客户端
            // ctx.write()方法，不会直接将消息发送出去，而是保存到内部缓存结构中
            // 另外需要注意的是，当调用ctx.write()方法时，netty内部会自动将写入的msg对象release了，因此无需显示release
            ctx.write(msg);
            // 只有当调用ctx.flush()方法的时候，才会真正的将所有缓存的需要发送的内容发送出去
            ctx.flush();
        } finally {
            // ReferenceCountUtil.release(msg);
            // ReferenceCountUtil.release(msg)也可以用下面的in.release()代替
            // in.release();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 遇到IO异常或handler抛出异常时触发
        // 通常在这里会打印日志，并且关闭channel。也可以给客户端回复一个错误码
        cause.printStackTrace();
        ctx.channel();
    }
}
