package netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by hzyouzhihao on 2016/9/7.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            String content=in.toString(CharsetUtil.UTF_8);
            System.out.println(content);
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
        cause.printStackTrace();
        ctx.channel();
    }
}
