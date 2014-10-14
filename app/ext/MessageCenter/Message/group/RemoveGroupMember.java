package ext.MessageCenter.Message.group;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * @description 移除群成员消息
 * @author beyond.zhang   
 * @update 2014-6-3 下午2:43:05
 */
public class RemoveGroupMember implements Message {
	private final static Logger logger = LoggerFactory
			.getLogger(RemoveGroupMember.class);

	public static final short CODE = 303;

	private Long groupId;

	private Long memberId;

	private int length;

	private Endpoint endpoint;
	
	public RemoveGroupMember(Long groupId, Long memberId) {
		super();
		this.groupId = groupId;
		this.memberId = memberId;
		this.length = 2+8+8;
	}

	@Override
	public short getCode() {
		return RemoveGroupMember.CODE;
	}

	@Override
	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int getLength() {
		return length;
	}


	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
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
		buffer.writeLong(groupId);
		buffer.writeLong(memberId);
		return buffer;
	}
	@Override
	public void fromBinary(ByteBuf buffer) {
		groupId = buffer.readLong();
		memberId = buffer.readLong();
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("groupId", groupId);
		map.put("memberId", memberId);
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
		memberId = map.get("memberId").asLong();
		this.length = 2 + 8 + 8 ;
	}


}
