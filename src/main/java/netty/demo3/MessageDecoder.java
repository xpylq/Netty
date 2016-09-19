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

    /**
     * 解码器不需要进行release,已经帮使用者进行释放了
     * 工作原理则是如果读取出现异常,则等待更多消息传入,再次从头读取消息
     * 加上checkpoint,增加效率,防止重复处理已读内容
     * @author youzhihao
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Message message = null;
        //先读本次消息的长度信息
        if (!isReadLength) {
            length = in.readInt();
            //读取完毕后,记录读取长度标志
            isReadLength = true;
            //记录读取位置,抛出异常后下次从该为止重新读取信息
            checkpoint();
        }
        //读取本次消息的内容
        if (isReadLength) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            //转成实体对象
            message = initContent(bytes);
            //读取完毕后,记录读取内容标志
            isReadLength = false;
            //记录读取位置,抛出异常后下次从该为止重新读取信息
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
