package ext.MessageCenter.core;

import play.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ext.MessageCenter.Message.Message;

public class SocketEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf buf) throws Exception {
        Endpoint endpoint = new Endpoint(ctx);
        message.setEndpoint(endpoint);
        ByteBuf byteBuf;
        try {
            byteBuf = message.toBinary();
            if (message.getLength() == 0) {// 自动计算length
                MessageFactory.autoWriteLength(byteBuf);
            }
        } catch (Exception e) {
            Logger.error("消息编码失败", e);
            throw e;
        }
        buf.writeBytes(byteBuf);
    }

}
