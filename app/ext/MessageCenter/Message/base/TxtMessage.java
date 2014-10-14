package ext.MessageCenter.Message.base;

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
 * @description 文本消息
 * @author beyond.zhang   
 */
public class TxtMessage implements Message {

	public static final short CODE = 3;

	private Long senderId;

	private Long receiverId;
	
	private String data;

	private Endpoint endpoint;

	private int length;
	
	public String senderName;
	
	public String receiverName;
	
   private String messageId;
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public TxtMessage() {
		super();
	}
	

	public TxtMessage(Long senderId, Long receiverId, String data,
			String senderName, String receiverName) {
		super();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.data = data;
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.messageId = UUID.randomUUID().toString().toUpperCase();
		this.length =16+2+ 8 + 8 +data.getBytes().length+ senderName.getBytes().length+receiverName.getBytes().length +4;
	}


	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}


	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	@Override
	public short getCode() {
		return TxtMessage.CODE;
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
		System.out.println("from "+endpoint.getContext().channel().remoteAddress()+" : "+toString());

	}

	@Override
	public ByteBuf toBinary() {
		byte[] senderNameBytes = senderName.getBytes(CharsetUtil.UTF_8);
		byte[] consumeNameByte = receiverName.getBytes(CharsetUtil.UTF_8);
		byte[] bytes = data.getBytes(CharsetUtil.UTF_8);
		this.length = 2+ 16+8+1+senderNameBytes.length+8+1+consumeNameByte.length+2+bytes.length;
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		buffer.writeBytes(Utils.getIdAsByte(messageId));
		buffer.writeLong(senderId);
		
		buffer.writeByte(senderNameBytes.length);
		buffer.writeBytes(senderNameBytes);
		buffer.writeLong(receiverId);
		buffer.writeByte(consumeNameByte.length);
		buffer.writeBytes(consumeNameByte);
		buffer.writeShort(bytes.length);
		buffer.writeBytes(bytes);
		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("senderId", senderId);
		map.put("receiverId", receiverId);
		map.put("data", data);
		map.put("senderName", senderName);
		map.put("receiverName", receiverName);
		map.put("messageId", messageId);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
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
		int len = buffer.readUnsignedShort();
		byte[] bytes = new byte[len];
		buffer.readBytes(bytes);
		data = new String(bytes, CharsetUtil.UTF_8);
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		senderId = map.get("senderId").asLong();
		receiverId = map.get("receiverId").asLong();
		data = map.get("data").asText();
		senderName = map.get("senderName").asText();
		receiverName = map.get("receiverName").asText();
		length = 22 + Utils.getUTF8StringLength(data)
				+ Utils.getUTF8StringLength(senderName)
				+ +Utils.getUTF8StringLength(receiverName);
	}

	
}
