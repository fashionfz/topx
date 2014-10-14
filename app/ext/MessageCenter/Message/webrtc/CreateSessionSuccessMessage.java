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
 * @description 创建会话成功消息
 * @author beyond.zhang   
 */
public class CreateSessionSuccessMessage implements Message{

	public static final short CODE = 201;

	private Endpoint endpoint;

	private int length;
	
	public long sessionId;

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	@Override
	public short getCode() {
		return CreateSessionSuccessMessage.CODE;
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
		buffer.writeInt(new Long(sessionId).intValue());
		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("sessionId", sessionId);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		return str;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		sessionId = buffer.readUnsignedInt();
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		sessionId = map.get("sessionId").asInt();
	}

}
