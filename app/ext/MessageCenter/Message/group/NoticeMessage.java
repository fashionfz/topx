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

public class NoticeMessage implements Message{
	public static final short CODE = 307;
	
	private final static Logger logger = LoggerFactory.getLogger(NoticeMessage.class);
	
	private String messageId;
	
	private int state;
	
	private String message;
	
	private Long groupId;
	
	private String groupName;
	
    private Long operationId;
	
	private String operationName;
	
	private Long sendTime;
	
	private Endpoint endpoint;
	
	private int length;
	
	private int type;
	
	public NoticeMessage(){}

	public NoticeMessage(String messageId,int state, String message, Long groupId,
			String groupName, Long operationId, String operationName,int type) {
		super();
		this.messageId = messageId;
		this.state = state;
		this.message = message;
		this.groupId = groupId;
		this.groupName = groupName;
		this.operationId = operationId;
		this.operationName = operationName;
		this.type = type;
		this.length = 16 + 2+ 2 +2 +Utils.getUTF8StringLength(message)+8+1+Utils.getUTF8StringLength(groupName)+8+1+Utils.getUTF8StringLength(operationName)+4;

	}
	
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessageId() {
		return messageId;
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
	
	

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	@Override
	public void onReceived() {
	
		
	}

	@Override
	public ByteBuf toBinary() {
		this.length = 16 + 2+ 2 +2 +message.getBytes(CharsetUtil.UTF_8).length+8+1+groupName.getBytes(CharsetUtil.UTF_8).length+8+1+operationName.getBytes(CharsetUtil.UTF_8).length+4;
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		buffer.writeBytes(Utils.getIdAsByte(messageId));
		buffer.writeInt(type);
		buffer.writeShort(state);
		buffer.writeShort(message.getBytes(CharsetUtil.UTF_8).length);
		buffer.writeBytes(message.getBytes(CharsetUtil.UTF_8));
		buffer.writeLong(groupId);
		buffer.writeByte(groupName.getBytes(CharsetUtil.UTF_8).length);
		buffer.writeBytes(groupName.getBytes(CharsetUtil.UTF_8));
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
	   groupName =new String(groupNamebytes, CharsetUtil.UTF_8);
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
		map.put("groupName", groupName);
		map.put("senderId", operationId);
		map.put("senderName", operationName);
		map.put("sendTime", new Date().getTime());
		map.put("type", type);
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
		groupName = map.get("groupName").asText();
		operationId = map.get("operationId").asLong();
		operationName = map.get("operationName").asText();
		type = map.get("type").asInt();
		this.length =16 + 2+ 2 +2 +message.getBytes().length+8+1+groupName.getBytes().length+8+1+operationName.getBytes().length+4;
	}



}
