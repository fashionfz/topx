package ext.MessageCenter.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;

import java.util.List;

import ext.MessageCenter.Message.Message;

public class SocketDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf,
			List<Object> result) throws Exception {
		Message message = null;
		try{
			message = MessageFactory.create(buf);
		}catch(UnsupportedMessageTypeException e){
			ctx.close();
			return;
		}
		if(message != null){
			message.setEndpoint(new Endpoint(ctx));
			result.add(message);
		}
	}


}
