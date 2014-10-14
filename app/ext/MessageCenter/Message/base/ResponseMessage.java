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
 * @description 返回消息
 * @author beyond.zhang   
 */
public class ResponseMessage implements Message {
	public static final short CODE = 0;

	private int state;

	private Endpoint endpoint;

	public ResponseMessage() {
		super();
	}
	
	public ResponseMessage(int state) {
		super();
		this.state = state;
	}


	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	@Override
	public short getCode() {
		return CODE;
	}

	@Override
	public int getLength() {
		return 4;
	}

	@Override
	public void setLength(int length) {

	}

	@Override
	public void onReceived() {

	}

	@Override
	public ByteBuf toBinary() {
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		buffer.writeShort(state);
		return buffer;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		 state =  buffer.readUnsignedShort();
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("state", state);
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
