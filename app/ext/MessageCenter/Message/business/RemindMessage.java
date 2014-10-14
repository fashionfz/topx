package ext.MessageCenter.Message.business;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;
import ext.MessageCenter.utils.Utils;

/**
 * 
 * @description 评论消息
 * @author beyond.zhang   
 */
public class RemindMessage implements Message{
	
	public static final short CODE = 281;
	
	private String messageId;

	private Long senderId;

	private Long receiverId;

	private Endpoint endpoint;

	private int length;
	
	private String senderName;
	
	private String receiverName;
	
	private String content;
	
	private Long date;
	
	private int type;
	
	
	
	public String getMessageId() {
		return messageId;
	}


	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}


	public RemindMessage() {
	}


	public RemindMessage(String messageId,Long senderId, Long receiverId, String senderName,
			String receiverName, String content, Long date,int type) {
		super();
		this.messageId = messageId;
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.content = content;
		this.date = date;
		this.type = type;
		this.length =16 + 4+ 2 + 8 + 8 + 6 + 3 * 1+1
				+ Utils.getUTF8StringLength(senderName)
				+ Utils.getUTF8StringLength(receiverName)
				+ Utils.getUTF8StringLength(content);
	}

	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	

	public Long getDate() {
		return date;
	}


	public void setDate(Long date) {
		this.date = date;
	}


	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	@Override
	public short getCode() {
		return RemindMessage.CODE;
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


	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
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
		this.length =16+4+ 1+8+1+Utils.getUTF8StringLength(senderName)+8+1
				+Utils.getUTF8StringLength(receiverName)+6+2+Utils.getUTF8StringLength(content);
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		buffer.writeBytes(Utils.getIdAsByte(messageId));
		buffer.writeLong(senderId);
		byte[] dateByte = Utils.unsigned48ToBytes(date);
		byte[] senderNameBytes = senderName.getBytes(CharsetUtil.UTF_8);
		byte[] consumeNameByte = receiverName.getBytes(CharsetUtil.UTF_8);
		byte[] contentByte = content.getBytes(CharsetUtil.UTF_8);
		buffer.writeByte(senderNameBytes.length);
		buffer.writeBytes(senderNameBytes);
		buffer.writeLong(receiverId);
		buffer.writeByte(consumeNameByte.length);
		buffer.writeBytes(consumeNameByte);
		buffer.writeInt(type);
		buffer.writeBytes(dateByte);
		buffer.writeShort(contentByte.length);
		buffer.writeBytes(contentByte);

		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("messageId", messageId);
		map.put("senderId", senderId);
		map.put("receiverId", receiverId);
		map.put("senderName", senderName);
		map.put("receiverName", receiverName);
		map.put("content", content);
		map.put("date", date);
		map.put("type", type);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		/*	logger.error("评论消息异常："+e);*/
		}
		return str;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		byte[] messageIdBytes = new byte[16];
		buffer.readBytes(messageIdBytes);
		messageId = Utils.convertBytesToUUID(messageIdBytes);
		senderId = buffer.readLong();
		short senderNameLen = buffer.readUnsignedByte();
		byte[] senderNameBytes = new byte[senderNameLen];
		buffer.readBytes(senderNameBytes);
		senderName = new String(senderNameBytes, CharsetUtil.UTF_8);
		receiverId = buffer.readLong();
		short consumeNameLen = buffer.readUnsignedByte();
		byte[] consumeNameBytes = new byte[consumeNameLen];
		buffer.readBytes(consumeNameBytes);
		receiverName = new String(consumeNameBytes, CharsetUtil.UTF_8);
		type = buffer.readInt();
		byte[] bytes = new byte[6];
		buffer.readBytes(bytes);
		date = Utils.from48Unsigned(bytes);
	
		//date = new Date().getTime();
		int contentLen = buffer.readUnsignedShort();
		byte[] contentBytes = new byte[contentLen];
		buffer.readBytes(contentBytes);
		content = new String(contentBytes, CharsetUtil.UTF_8);
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		messageId = map.get("messageId").asText();
		senderId = map.get("senderId").asLong();
		receiverId = map.get("receiverId").asLong();
		senderName = map.get("senderName").asText();
		receiverName = map.get("receiverName").asText();
		content = map.get("content").asText();
		date = map.get("date").asLong();
		type = map.get("type").asInt();
		this.length =4+ 2+8+1+Utils.getUTF8StringLength(senderName)+8+1
				+Utils.getUTF8StringLength(receiverName)+6+2+Utils.getUTF8StringLength(content);
	}
}
