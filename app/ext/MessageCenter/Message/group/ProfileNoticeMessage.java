package ext.MessageCenter.Message.group;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;
import ext.MessageCenter.utils.Utils;

public class ProfileNoticeMessage implements Message{
	public static final short CODE = 317;
	
	private final static Logger logger = LoggerFactory.getLogger(ProfileNoticeMessage.class);
	
	private String messageId;
	
	private int state;
	
	private String message;
	
	private Long groupId;
	
	private String groupNewName;
	
    private Long operationId;
	
	private String operationName;
	
	private String avatarUrl;
	
	private Long sendTime;
	
	private Endpoint endpoint;
	
	private int length;
	
	private int type;
	

	public String getMessageId() {
		return messageId;
	}



	public int getType() {
		return type;
	}



	public void setType(int type) {
		this.type = type;
	}



	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}



	public Long getSendTime() {
		return sendTime;
	}

	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}



	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getGroupNewName() {
		return groupNewName;
	}



	public void setGroupNewName(String groupNewName) {
		this.groupNewName = groupNewName;
	}



	public String getAvatarUrl() {
		return avatarUrl;
	}



	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}



	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getOperationId() {
		return operationId;
	}

	public void setOperationId(Long operationId) {
		this.operationId = operationId;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}
	public void setPoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public short getCode() {
		return CODE;
	}

	public ProfileNoticeMessage(String messageId,int state, String message, Long groupId,
			String groupNewName, String avatarUrl,Long operationId, String operationName,int type) {
		super();
		this.messageId = messageId;
		this.state = state;
		this.message = message;
		this.groupId = groupId;
		this.groupNewName = groupNewName;
		this.operationId = operationId;
		this.operationName = operationName;
		this.type = type;
		this.avatarUrl = avatarUrl;
		this.length = 16 + 2+ 2 +2 +Utils.getUTF8StringLength(message)+8+1+Utils.getUTF8StringLength(groupNewName)+8+1+Utils.getUTF8StringLength(operationName)+4+1+Utils.getUTF8StringLength(avatarUrl);

	}
	
	@Override
	public void onReceived() {
   
    }

	@Override
	public ByteBuf toBinary() {
		this.length = 16 + 2+ 2 +2 +message.getBytes(CharsetUtil.UTF_8).length+8+1+groupNewName.getBytes(CharsetUtil.UTF_8).length+8+1+operationName.getBytes(CharsetUtil.UTF_8).length+4+1+avatarUrl.getBytes(CharsetUtil.UTF_8).length;
		System.out.println("22222222222222222   "+length);
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		buffer.writeBytes(Utils.getIdAsByte(messageId));
		buffer.writeInt(type);
		buffer.writeShort(state);
		buffer.writeShort(message.getBytes(CharsetUtil.UTF_8).length);
		buffer.writeBytes(message.getBytes(CharsetUtil.UTF_8));
		buffer.writeLong(groupId);
		buffer.writeByte(groupNewName.getBytes(CharsetUtil.UTF_8).length);
		buffer.writeBytes(groupNewName.getBytes(CharsetUtil.UTF_8));
		buffer.writeByte(avatarUrl.getBytes(CharsetUtil.UTF_8).length);
		buffer.writeBytes(avatarUrl.getBytes(CharsetUtil.UTF_8));
		buffer.writeLong(operationId);
		buffer.writeByte(operationName.getBytes(CharsetUtil.UTF_8).length);
		buffer.writeBytes(operationName.getBytes(CharsetUtil.UTF_8));
		
		return buffer;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		byte[] messageIdBytes = new byte[16];
        buffer.readBytes(messageIdBytes);
        messageId = Utils.convertBytesToUUID(messageIdBytes);
 	    type = buffer.readInt();
		state =  buffer.readUnsignedShort();
	    int	msgLength =  buffer.readUnsignedShort();
	    byte[] msgbytes = new byte[msgLength];
	    buffer.readBytes(msgbytes);
		message = new String(msgbytes, CharsetUtil.UTF_8);
	    groupId = buffer.readLong();
	   int groupNameLength =  buffer.readUnsignedByte();
	   byte[] groupNamebytes = new byte[groupNameLength];
	   buffer.readBytes(groupNamebytes);
	   groupNewName =new String(groupNamebytes, CharsetUtil.UTF_8);
	   int avatarUrlLength = buffer.readUnsignedByte();
	   byte[] avatarUrlByte = new byte[avatarUrlLength];
	   avatarUrl =new String(avatarUrlByte, CharsetUtil.UTF_8);
	   operationId = buffer.readLong();
	   int operationNameLength =  buffer.readUnsignedByte();
	   byte[] operationNamebytes = new byte[operationNameLength];
	   operationName =new String(operationNamebytes, CharsetUtil.UTF_8);
	   
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("messageId", messageId);
		map.put("state", state);
		map.put("message", message);
		map.put("groupId", groupId);
		map.put("groupNewName", groupNewName);
		map.put("senderId", operationId);
		map.put("senderName", operationName);
		map.put("sendTime", new Date().getTime());
		map.put("type", type);
		map.put("avatarUrl", avatarUrl);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
			logger.error("返回消息异常："+e);
		}
		return str;
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		messageId = map.get("messageId").asText();
		state = map.get("state").asInt();
		message = map.get("message").asText();
		groupId = map.get("groupId").asLong();
		groupNewName = map.get("groupNewName").asText();
		avatarUrl = map.get("avatarUrl").asText();
		operationId = map.get("operationId").asLong();
		operationName = map.get("operationName").asText();
		type = map.get("type").asInt();
		this.length =16 + 2+ 2 +2 +message.getBytes().length+8+1+groupNewName.getBytes().length+8+1+operationName.getBytes().length+4;
	}

	
}
