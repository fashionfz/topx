package ext.MessageCenter.Message.business;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.util.Date;
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
 * @description 回复消息
 * @author beyond.zhang   
 */
public class ReplyMessage implements Message{

	public static final short CODE = 101;
	
	private String messageId;

	private Long senderId;

	private Long receiverId;
	
	private Long date;

	private Endpoint endpoint;

	private int length;
	
	public String senderName;
	
	public String receiverName;
	
	public String content;
	
	public ReplyMessage() {
		super();
	}
	




	public ReplyMessage(Long senderId, Long receiverId, Long date,
			String senderName, String receiverName, String content) {
		super();
		this.messageId = UUID.randomUUID().toString();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.date = date;
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.content = content;
		this.length =16 + 2 + 8 + 8 + 6 + 3 * 1+1
				+ Utils.getUTF8StringLength(senderName)
				+ Utils.getUTF8StringLength(receiverName)
				+ Utils.getUTF8StringLength(content);

	}





	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}


	public Long getDate() {
		return date;
	}


	public void setDate(Long date) {
		this.date = date;
	}





	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	@Override
	public short getCode() {
		return CODE;
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
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		buffer.writeBytes(Utils.getIdAsByte(messageId));
		buffer.writeLong(senderId);
		byte[] dateByte = Utils.unsigned48ToBytes(date);
		byte[] senderNameBytes = senderName.getBytes(CharsetUtil.UTF_8);
		byte[] consumeNameByte = receiverName.getBytes(CharsetUtil.UTF_8);
		byte[] contentBytes = content.getBytes(CharsetUtil.UTF_8);
		buffer.writeByte(senderNameBytes.length);
		buffer.writeBytes(senderNameBytes);
		buffer.writeLong(receiverId);
		buffer.writeByte(consumeNameByte.length);
		buffer.writeBytes(consumeNameByte);
		buffer.writeBytes(dateByte);
		buffer.writeShort(contentBytes.length);
		buffer.writeBytes(contentBytes);

		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("messageId", messageId);
		map.put("senderId", senderId);
		map.put("receiverId", receiverId);
		map.put("date", date);
		map.put("senderName", senderName);
		map.put("receiverName", receiverName);
		map.put("content", content);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		/*	logger.error("回复消息异常："+e);*/
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
		byte[] bytes = new byte[6];
		buffer.readBytes(bytes);
	//	date = Utils.from48Unsigned(bytes);
		date = new Date().getTime();
		int contentLen = buffer.readUnsignedShort();
		byte[] contentBytes = new byte[contentLen];
		buffer.readBytes(contentBytes);
		content = new String(contentBytes, CharsetUtil.UTF_8);
		this.length =  16+2+8+1+Utils.getUTF8StringLength(senderName)+8+1+Utils.getUTF8StringLength(receiverName)+6+2 +Utils.getUTF8StringLength(content);


	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		messageId = map.get("messageId").asText();
		senderId = map.get("senderId").asLong();
		receiverId = map.get("receiverId").asLong();
		date = map.get("date").asLong();
		senderName = map.get("senderName").asText();
		receiverName = map.get("receiverName").asText();
		content = map.get("content").asText();
		this.length =  16+2+8+1+Utils.getUTF8StringLength(senderName)+8+1+Utils.getUTF8StringLength(receiverName)+6+2 +Utils.getUTF8StringLength(content);
	}



}
