package netty.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
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
            //这里的write不会直接将数据传输，需要调用flush方法，另外如果write完后，对应的obj会被release,这个是在write方法内部进行release的
            //因此不需要在finally里再次release
            ctx.write(msg);
            ctx.flush();
        } finally {
            //ReferenceCountUtil.release(msg);
            //这里也可以改成
            //in.release();
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
