package ext.MessageCenter.Message.group;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;
/**
 * 
 * @description 删除群组消息
 * @author beyond.zhang   
 * @update 2014-5-28 下午2:01:11
 */
public class DeleteGroupMessage implements Message{
	
	private final static Logger logger = LoggerFactory
			.getLogger(DeleteGroupMessage.class);
	
	public static final short CODE = 304;
	private Long groupId; 
	private int length ;
	private Endpoint endpoint;
	
	
	public DeleteGroupMessage(Long groupId) {
		super();
		this.groupId = groupId;
		this.length = 2+8;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = 10;
	}
	public Endpoint getEndpoint() {
		return endpoint;
	}
	public short getCode() {
		return CODE;
	}
	@Override
	public void onReceived() {
	}
	@Override
	public ByteBuf toBinary() {
		ByteBuf buffer = MessageFactory.createByteBuf(this,
				endpoint.getContext());
		buffer.writeLong(groupId);
		return buffer;
	}
	@Override
	public void fromBinary(ByteBuf buffer) {
		groupId = buffer.readLong();
		
	}
	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("groupId", groupId);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		return str;
	}
	@Override
	public void fromMap(Map<String, JsonNode> map) {
		groupId = map.get("groupId").asLong();
		this.length = 2+8;
	}
	@Override
	public void setEndpoint(Endpoint endpoint) {
		this.endpoint =endpoint;
		
	}
	
	
}
