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
 * @description 关闭麦克风消息
 * @author beyond.zhang   
 */
public class CloseMicMessage implements Message{

	public static final short CODE = 251;

	private Long senderId;

	private Long receiverId;
	
	private Long date;

	private Endpoint endpoint;

	private int length;
	
	public String senderName;
	
	public String receiverName;
	
	private long sessionId;
	

	public CloseMicMessage() {
		super();
	}

	public CloseMicMessage(Long senderId, Long receiverId, Long date,
			String senderName, String receiverName, int sessionId) {
		super();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.date = date;
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.sessionId = sessionId;
		this.length = 2+8+ 8 +6 +2*1 + 4 + senderName.getBytes().length+receiverName.getBytes().length;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public Endpoint getEndpoint() {
		return endpoint;
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
		return CloseMicMessage.CODE;
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
		byte[] senderNameBytes = senderName.getBytes(CharsetUtil.UTF_8);
		byte[] consumeNameByte = receiverName.getBytes(CharsetUtil.UTF_8);
		buffer.writeByte(senderNameBytes.length);
		buffer.writeBytes(senderNameBytes);
		buffer.writeLong(receiverId);
		buffer.writeByte(consumeNameByte.length);
		buffer.writeBytes(consumeNameByte);
		buffer.writeBytes(dateByte);
		buffer.writeInt(new Long(sessionId).intValue());
		

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
		sessionId = buffer.readUnsignedInt();
		

	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		senderId = map.get("senderId").asLong();
		receiverId = map.get("receiverId").asLong();
		date = map.get("date").asLong();
		senderName = map.get("senderName").asText();
		receiverName = map.get("receiverName").asText();
		sessionId = map.get("sessionId").asInt();
		this.length = 2 + 8 + 8 + 6 + 4 + 2 * 1
				+ Utils.getUTF8StringLength(senderName)
				+ Utils.getUTF8StringLength(receiverName);
	}
}
