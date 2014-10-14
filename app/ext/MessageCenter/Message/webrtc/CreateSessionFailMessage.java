package ext.MessageCenter.Message.webrtc;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;
/**
 * 
 * @description 创建会话失败消息
 * @author beyond.zhang   
 */
public class CreateSessionFailMessage implements Message{

	public static final short CODE = 202;

	private Endpoint endpoint;

	private int length;
	@Override
	public short getCode() {
		return CreateSessionFailMessage.CODE;
	}

	@Override
	public void setLength(int length) {
		this.length = length;
	}
	
	@Override
	public int getLength() {
		return length;
	}
	

	@Override
	public void setEndpoint(Endpoint Endpoint) {
		this.endpoint = Endpoint;
	}

	@Override
	public void onReceived() {
		/*Endpoint fromEndpoint = Context.getEndpoint(userId);
		if(fromEndpoint == null || fromEndpoint != endpoint){
			endpoint.getContext().close();
		}else{
			if(toEndpoint != null){
				if(toEndpoint instanceof SocketEndpoint){
					ChannelFuture future = toEndpoint.getContext().channel().writeAndFlush(toBinary());
				}else{
					ChannelFuture future = toEndpoint.getContext().channel().writeAndFlush(new TextWebSocketFrame(toJson()));
				}
			}
		}*/
	}

	@Override
	public ByteBuf toBinary() {
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		return str;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
	}

}
