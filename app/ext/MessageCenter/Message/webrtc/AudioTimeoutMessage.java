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
 * @description 音频会话超时
 * @author beyond.zhang   
 */
public class AudioTimeoutMessage implements Message{

	public static final short CODE = 223;
	
private Long userId;
	
	private String userName;
	
	private Long inviterId;
	
	private String inviterName;
	
	private long  sessionId;
	
	private Long date;
	
	private Endpoint endpoint;

	private int length;
	
	public AudioTimeoutMessage() {
		super();
	}

	
	



	public AudioTimeoutMessage(Long userId, String userName,
			Long inviterId, String inviterName, int sessionId, Long date) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.inviterId = inviterId;
		this.inviterName = inviterName;
		this.sessionId = sessionId;
		this.date = date;
		this.length = 2+8 + userName.getBytes().length+ 8 + inviterName.getBytes().length + 4+6 +2*1;

	}






	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getInviterId() {
		return inviterId;
	}

	public void setInviterId(Long inviterId) {
		this.inviterId = inviterId;
	}

	public String getInviterName() {
		return inviterName;
	}

	public void setInviterName(String inviterName) {
		this.inviterName = inviterName;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
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
		byte[] dateByte = Utils.unsigned48ToBytes(date);
		byte[] userNameBytes = userName.getBytes(CharsetUtil.UTF_8);
		byte[] inviterNameBytes = inviterName.getBytes(CharsetUtil.UTF_8);
		buffer.writeLong(userId);
		buffer.writeByte(userNameBytes.length);
		buffer.writeBytes(userNameBytes);
		buffer.writeLong(inviterId);
		buffer.writeByte(inviterNameBytes.length);
		buffer.writeBytes(inviterNameBytes);
		buffer.writeBytes(dateByte);
		buffer.writeInt(new Long(sessionId).intValue());
		
		return buffer;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		userId = buffer.readLong();
		short userNameLen = buffer.readUnsignedByte();
		byte[] userNameBytes = new byte[userNameLen];
		buffer.readBytes(userNameBytes);
		userName = new String(userNameBytes, CharsetUtil.UTF_8);
		inviterId = buffer.readLong();
		short inviterNameLen = buffer.readUnsignedByte();
		byte[] inviterNameBytes = new byte[inviterNameLen];
		buffer.readBytes(inviterNameBytes);
		inviterName = new String(inviterNameBytes, CharsetUtil.UTF_8);
		byte[] bytes = new byte[6];
		buffer.readBytes(bytes);
		date = Utils.from48Unsigned(bytes);
		sessionId = buffer.readUnsignedInt();

		
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("userId", userId);
		map.put("inviterId", inviterId);
		map.put("date", date);
		map.put("userName", userName);
		map.put("inviterName", inviterName);
		map.put("sessionId", sessionId);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
		}
		return str;
	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		userId = map.get("userId").asLong();
		inviterId = map.get("inviterId").asLong();
		date = map.get("date").asLong();
		userName = map.get("userName").asText();
		inviterName = map.get("inviterName").asText();
		sessionId = map.get("sessionId").asInt();
		this.length = 2 + 8 + 8 + 6 + 4 + 2 * 1
				+ Utils.getUTF8StringLength(userName)
				+ Utils.getUTF8StringLength(inviterName);
	}
	
	
}
