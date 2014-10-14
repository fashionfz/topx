package ext.MessageCenter.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ext.MessageCenter.Message.Message;

public class SocketHandler extends SimpleChannelInboundHandler<Message> {
	
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message message)
			throws Exception {	
		message.onReceived();
		
	}
	
}
