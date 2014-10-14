package ext.MessageCenter.Message.base;

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
 * @description 上报消息
 * @author beyond.zhang   
 */
public class ReportMessage implements Message {

	public static final short CODE = 1;

	private Long id;

	private String token;

	private Endpoint endpoint;
	
	private int length;
	
	private byte type;
	
	public static final byte WEBSOCKET = 0;
	
	public static final byte WEBRTC = 1;
	
	public static final byte SOCKET = 2;
	
	public ReportMessage() {
		super();
	}
	
	public ReportMessage(Long id, String token) {
		super();
		this.id = id;
		this.token = token;
        this.length =2+ 8 + token.getBytes().length+1;
	}

	public ReportMessage(Long id, String token, byte type) {
		super();
		this.id = id;
		this.token = token;
		this.type = type;
	    this.length =2+ 8 + token.getBytes().length+1+1;
	}

	@Override
	public short getCode() {
		return ReportMessage.CODE;
	}
	
	@Override
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int getLength() {
		return length;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void onReceived() {
		
	}

	@Override
	public ByteBuf toBinary() {
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		buffer.writeLong(id);
		byte[] bytes = token.getBytes(CharsetUtil.UTF_8);
		buffer.writeByte(bytes.length);
		buffer.writeBytes(bytes);
		buffer.writeByte(type);
		return buffer;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		id = buffer.readLong();
		short len = buffer.readUnsignedByte();
		byte[] bytes = new byte[len];
		buffer.readBytes(bytes);
		token = new String(bytes, CharsetUtil.UTF_8);
		type = buffer.readByte();
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("id", id);
		map.put("token", token);
		map.put("type", type);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		return str;
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		id = map.get("id").asLong();
		token = map.get("token").asText();
		type = (byte)map.get("type").intValue();
		this.length = 2 + 8 + 1 +1+ Utils.getUTF8StringLength(token);
	}

	public boolean isWebRTC() {
		return type == WEBRTC;
	}

}
