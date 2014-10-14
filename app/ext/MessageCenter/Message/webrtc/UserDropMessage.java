package ext.MessageCenter.Message.webrtc;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;
import ext.MessageCenter.utils.Utils;
/**
 * 
 * @description 用户掉线消息
 * @author beyond.zhang   
 */
public class UserDropMessage implements Message{

	public static final short CODE = 105;

	private Long userId;

	
	private Endpoint endpoint;

	private int length;
	
	public String userName;

	public UserDropMessage() {
		super();
	}

	
	
	public UserDropMessage(Long userId, String userName) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.length =2+ 8 +1*1 +userName.getBytes().length;
	}



	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public short getCode() {
		return UserDropMessage.CODE;
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
		/*Endpoint fromEndpoint = Context.getEndpoint(senderId);
		Endpoint toEndpoint = Context.getEndpoint(receiverId);
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
		buffer.writeLong(userId);
		byte[] userNameBytes = userName.getBytes(CharsetUtil.UTF_8);
		buffer.writeByte(userNameBytes.length);
		buffer.writeBytes(userNameBytes);

		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("userId", userId);
		map.put("userName", userName);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		return str;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		userId = buffer.readLong();
		short userNameLen = buffer.readUnsignedByte();
		byte[] userNameBytes = new byte[userNameLen];
		buffer.readBytes(userNameBytes);
		userName = new String(userNameBytes, CharsetUtil.UTF_8);

	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		userId = map.get("userId").asLong();
		userName = map.get("userName").asText();
		this.length = 2 + 8 + 1 * 1 + Utils.getUTF8StringLength(userName);
	}


}
