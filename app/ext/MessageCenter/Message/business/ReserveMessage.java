package ext.MessageCenter.Message.business;

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
 * @description 预约消息
 * @author beyond.zhang   
 */
public class ReserveMessage implements Message{

	public static final short CODE = 120;

	private Long senderId;

	private Long receiverId;
	
	private Long date;

	private Endpoint endpoint;

	private int length;
	
	private String senderName;
	
	private String receiverName;
	
    private Long fromDate;
	
	private Long toDate;
	
	public ReserveMessage() {
		super();
	}
	


	public ReserveMessage(Long senderId, Long receiverId, Long date,
			String senderName, String receiverName, Long fromDate, Long toDate) {
		super();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.date = date;
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.length = 2+8+8+6+senderName.getBytes().length+receiverName.getBytes().length+6+6+2*1;

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



	public Long getFromDate() {
		return fromDate;
	}



	public void setFromDate(Long fromDate) {
		this.fromDate = fromDate;
	}



	public Long getToDate() {
		return toDate;
	}



	public void setToDate(Long toDate) {
		this.toDate = toDate;
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
		buffer.writeLong(senderId);
		byte[] dateByte = Utils.unsigned48ToBytes(date);
		byte[] fromDatebytes = Utils.unsigned48ToBytes(fromDate);
		byte[] toDatebytes = Utils.unsigned48ToBytes(toDate);
		byte[] senderNameBytes = senderName.getBytes(CharsetUtil.UTF_8);
		byte[] consumeNameByte = receiverName.getBytes(CharsetUtil.UTF_8);
		buffer.writeByte(senderNameBytes.length);
		buffer.writeBytes(senderNameBytes);
		buffer.writeLong(receiverId);
		buffer.writeByte(consumeNameByte.length);
		buffer.writeBytes(consumeNameByte);
		buffer.writeBytes(dateByte);
		buffer.writeBytes(fromDatebytes);
		buffer.writeBytes(toDatebytes);

		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("senderId", senderId);
		map.put("receiverId", receiverId);
		map.put("date", date);
		map.put("senderName", senderName);
		map.put("receiverName", receiverName);
		map.put("fromDate", fromDate);
		map.put("toDate", toDate);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		return str;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
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
		byte[] dateBytes = new byte[6];
		buffer.readBytes(dateBytes);
		date = Utils.from48Unsigned(dateBytes);
		byte[] fromdateBytes = new byte[6];
		buffer.readBytes(fromdateBytes);
		fromDate = Utils.from48Unsigned(fromdateBytes);
		byte[] todateBytes = new byte[6];
		buffer.readBytes(todateBytes);
		toDate = Utils.from48Unsigned(todateBytes);

	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		senderId = map.get("senderId").asLong();
		receiverId = map.get("receiverId").asLong();
		date = map.get("date").asLong();
		senderName = map.get("senderName").asText();
		receiverName = map.get("receiverName").asText();
		fromDate = map.get("fromDate").asLong();
		toDate = map.get("toDate").asLong();
		this.length = 2 + 8 + 8 + 6 * 3 + 2 * 1
				+ Utils.getUTF8StringLength(senderName)
				+ Utils.getUTF8StringLength(receiverName);
	}

}
