package ext.MessageCenter.core;

import io.netty.channel.ChannelHandlerContext;

public class Endpoint {
	protected Long id;
	protected ChannelHandlerContext context;

	public Endpoint(ChannelHandlerContext context){
		this.context = context;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChannelHandlerContext getContext() {
		return context;
	}

	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}

}
