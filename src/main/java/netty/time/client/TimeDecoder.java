package netty.time.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * 专门用于处理TCP或UDP协议下，流数据会被分包的问题:
 * 一条完整的消息发送后，如何保证消息被完整接收后再进行后续逻辑处理
 * <p/>
 * 这里的TimeDecoder结合这个Demo的模拟场景，就是在等待完整的接收到2字节的消息后，再进行业务逻辑的处理
 * <p/>
 * 关于{@link io.netty.handler.codec.ByteToMessageDecoder}:
 * <ul>
 *     <li>
 *         ByteToMessageDecoder是Netty提供的实现了{@link io.netty.channel.ChannelInboundHandler}接口的子类，专门用于处理流数据消息碎片化的问题
 *     </li>
 *     <li>
 *         ByteToMessageDecoder内部维持一个可累加的二进制缓存，每次接收二进制数据都会累加到该缓存中，并且调用decode()方法
 *     </li>
 * </ul>
 * 其他注意事项:
 * <ul>
 *     <li>可以尝试使用{@link io.netty.handler.codec.ReplayingDecoder}来代替{@link io.netty.handler.codec.ByteToMessageDecoder}来实现下面的解码逻辑</li>
 * </ul>
 * @ProjectName: Netty <br/>
 * @Date: 2023/4/21 15:10 <br/>
 * @Author: youzhihao
 */

@Slf4j
public class TimeDecoder extends ByteToMessageDecoder {


    /**
     * decode方法会被持续调用，直到缓存中没有任何二进制数据
     * decode方法每次只能最多解析一条消息
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        log.info("TimeDecoder: 尝试解码数据");
        // 判断内部缓存中是否有2个字节的消息
        if (in.readableBytes() < 8) {
            log.info("TimeDecoder: 当前接收到数据小于2字节，等待后续数据");
            return; // (3)
        }
        // 如果包含2个字节消息，则2个字节的消息读取出来，然后交给后续的handler处理
        log.info("TimeDecoder: 接收到数据超过2个字节，开始读取并处理2字节数据");
        out.add(in.readBytes(8)); // (4)
    }
}
