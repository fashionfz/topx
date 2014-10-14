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
 * @description 异常下线消息
 * @author beyond.zhang   
 */
public class AbNormalOfflineMessage implements Message {

	public static final short CODE = 21;

	private Endpoint endpoint;

	private int length;


	public AbNormalOfflineMessage(){
		this.length = 2;
	}

	@Override
	public short getCode() {
		return AbNormalOfflineMessage.CODE;
	}

	@Override
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int getLength() {
		return length;
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
		return buffer;
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
	public void fromBinary(ByteBuf buffer) {
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
	}

}
