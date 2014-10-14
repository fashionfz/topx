package ext.MessageCenter.Message.group;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.util.DateUtil;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;

/**
 * @description 群文本消息
 * @author beyond.zhang
 */
public class GroupTxtMessage implements Message {
	private final static Logger logger = LoggerFactory
			.getLogger(GroupTxtMessage.class);

	public static final short CODE = 305;
	
	private Endpoint endpoint;
	
	private String messageId;
	
	private Long senderId;

	private String senderName;
	
	private Long groupId;
	
	private int type;
	
	private String data;
	
	private int length;

	public static final short ORIGINAL = 1;     /** 原文 **/ 
	public static final short TRANSLATION = 2;  /** 译文 **/ 
	
	
	public String getMessageId() {
		return messageId;
	}

	public GroupTxtMessage(String messageId, Long senderId, String senderName,
			Long groupId, int type, String data) {
		super();
		this.messageId = messageId;
		this.senderId = senderId;
		this.senderName = senderName;
		this.groupId = groupId;
		this.type = type;
		this.data = data;
		this.length = 2+36 +8 +1+senderName.getBytes().length+8+2+2+data.getBytes().length;

	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	@Override
	public short getCode() {
		return GroupTxtMessage.CODE;
	}

	@Override
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int getLength() {
		return length;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}


	@Override
	public void setEndpoint(Endpoint Endpoint) {
		this.endpoint = Endpoint;
	}

	@Override
	public void onReceived() {
		
	}




	@Override
	public ByteBuf toBinary() {
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		byte[] senderNames = senderName.getBytes();
		byte[] datas = data.getBytes();
		buffer.writeBytes(messageId.getBytes());
		buffer.writeLong(senderId);
		buffer.writeByte(senderNames.length);
		buffer.writeBytes(senderNames);
		buffer.writeLong(groupId);
		buffer.writeShort(type);
		buffer.writeShort(datas.length);
		buffer.writeBytes(datas);
		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("messageId", messageId);
		map.put("senderId", senderId);
		map.put("senderName", senderName);
		map.put("groupId", groupId);
		map.put("type", type);
		map.put("data", data);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
			logger.error("文本消息异常：" + e);
		}
		return str;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		byte[] messageIdBytes = new byte[36];
		buffer.readBytes(messageIdBytes);
		messageId = new String(messageIdBytes,CharsetUtil.UTF_8);
		senderId = buffer.readLong();
		short senderNameLength=buffer.readUnsignedByte();
		byte[] senderNameBytes = new byte[senderNameLength];
		buffer.readBytes(senderNameBytes);
		senderName = new String(senderNameBytes,CharsetUtil.UTF_8);
		groupId =  buffer.readLong();
		type = buffer.readUnsignedShort();
		int dataLength = buffer.readUnsignedShort();
		byte[] dataBytes = new byte[dataLength];
		buffer.readBytes(dataBytes);
		data = new String(dataBytes,CharsetUtil.UTF_8);
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		messageId = map.get("messageId").asText();
		senderId = map.get("senderId").asLong();
		senderName = map.get("senderName").asText();
		groupId = map.get("groupId").asLong();
		type = map.get("type").asInt();
		data = map.get("data").asText();
		this.length = 2+36 +8 +1+senderName.getBytes().length+8+2+2+data.getBytes().length;
		
	}

}
