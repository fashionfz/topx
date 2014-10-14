package ext.MessageCenter.Message.base;

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
 * @description 心跳包
 * @author beyond.zhang   
 */
public class HeartbeatMessage implements Message{
	public static final short CODE = 2;
	
	private int length;
	
	private Endpoint endpoint;
	
	public HeartbeatMessage() {
		this.length = 2;
	}

	@Override
	public short getCode() {
		return 2;
	}

	@Override
	public int getLength() {
		return 2;
	}

	@Override
	public void setLength(int length) {
		
	}


	public Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public void onReceived() {
		
	}

	@Override
	public ByteBuf toBinary() {
		ByteBuf buffer = MessageFactory.createByteBuf(this, endpoint.getContext());
		return buffer;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		return str;
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		
	}

	

}
