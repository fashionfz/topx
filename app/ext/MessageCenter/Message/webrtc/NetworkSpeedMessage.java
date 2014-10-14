package ext.MessageCenter.Message.webrtc;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;
/**
 * 
 * @description 网速监控消息
 * @author beyond.zhang   
 */
public class NetworkSpeedMessage implements Message{

	public static final short CODE = 260;

	private Long senderId;

	private Long receiverId;

	private Endpoint endpoint;

	private int length;
	
	private long key;


	public NetworkSpeedMessage() {
		super();
	}
	
	public NetworkSpeedMessage(Long senderId, Long receiverId, int key) {
		super();
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.key = key;
		this.length = 2+8+8+4;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	@Override
	public short getCode() {
		return NetworkSpeedMessage.CODE;
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
		buffer.writeLong(receiverId);
		buffer.writeInt(new Long(key).intValue());

		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("senderId", senderId);
		map.put("receiverId", receiverId);
		map.put("key", key);
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
		receiverId = buffer.readLong();
		key = buffer.readUnsignedInt();

	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		senderId = map.get("senderId").asLong();
		receiverId = map.get("receiverId").asLong();
		key = map.get("key").asInt();
		this.length = 2 + 8 + 8 + 4;
	}


}
