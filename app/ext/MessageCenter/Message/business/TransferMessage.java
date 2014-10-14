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
 * @description 转账消息
 * @author beyond.zhang   
 */
public class TransferMessage implements Message{

	public static final short CODE = 110;
	
	private String messageId;

	private Long senderId;

	private Long receiverId;
	
	private Long date;

	private Endpoint endpoint;

	private int length;
	
	public String senderName;
	
	public String receiverName;
	
	private double amount;
	
	private byte currency;
	
	
	
	public String getMessageId() {
		return messageId;
	}






	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}






	public TransferMessage() {
		super();
	}
	
	




	public TransferMessage(Long senderId, Long receiverId, Long date,
			String senderName, String receiverName, double amount, byte currency) {
		super();
		this.messageId = UUID.randomUUID().toString();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.date = date;
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.amount = amount;
		this.currency = currency;
		this.length = 16+2+8+8+6+senderName.getBytes().length+receiverName.getBytes().length+8+1+2*1;

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


	public double getAmount() {
		return amount;
	}



	public void setAmount(double amount) {
		this.amount = amount;
	}



	public byte getCurrency() {
		return currency;
	}

	public void setCurrency(byte currency) {
		this.currency = currency;
	}

	@Override
	public void onReceived() {
	}


	@Override
	public ByteBuf toBinary() {
		this.length =16 + 2 + 8 + 8 + 6 + 8 + 1 + 2 
				+ Utils.getUTF8StringLength(senderName)
				+ Utils.getUTF8StringLength(receiverName);
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		byte[] dateByte = Utils.unsigned48ToBytes(date);
		byte[] senderNameBytes = senderName.getBytes(CharsetUtil.UTF_8);
		byte[] consumeNameByte = receiverName.getBytes(CharsetUtil.UTF_8);
		buffer.writeBytes(Utils.getIdAsByte(messageId));
		buffer.writeLong(senderId);
		buffer.writeByte(senderNameBytes.length);
		buffer.writeBytes(senderNameBytes);
		buffer.writeLong(receiverId);
		buffer.writeByte(consumeNameByte.length);
		buffer.writeBytes(consumeNameByte);
		buffer.writeBytes(dateByte);
		buffer.writeDouble(amount);
		buffer.writeByte(currency);

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
		map.put("amount", amount);
		map.put("currency", currency);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
			/*logger.error("TransferMessage 消息异常："+e);*/
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
		date = Utils.from48Unsigned(bytes);
	//	date = new Date().getTime();
		amount = buffer.readDouble();
		currency = buffer.readByte();
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		messageId = map.get("messageId").asText();
		senderId = map.get("senderId").asLong();
		receiverId = map.get("receiverId").asLong();
		date = map.get("date").asLong();
		senderName = map.get("senderName").asText();
		receiverName = map.get("receiverName").asText();
		amount = map.get("amount").asDouble();
		currency = (byte) map.get("currency").asInt();
		this.length =16 + 2 + 8 + 8 + 6 + 8 + 1 + 2 
				+ Utils.getUTF8StringLength(senderName)
				+ Utils.getUTF8StringLength(receiverName);
	}





}
