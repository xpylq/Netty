package netty.demo3;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Created by hzyouzhihao on 2016/9/12.
 * 解码器
 */
public class MessageDecoder extends ReplayingDecoder<Void> {

    private boolean isReadLength = false;

    private int length;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Message message = null;
        if (!isReadLength) {
            length = in.readInt();
            isReadLength = true;
            checkpoint();
        }
        if (isReadLength) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            message = initContent(bytes);
            isReadLength = false;
            checkpoint();
        }
        out.add(message);
    }
    private Message initContent(byte[] bytes) {
        Message message = new Message();
        message.setContent(new String(bytes));
        message.setLength(bytes.length);
        return message;
    }
}
