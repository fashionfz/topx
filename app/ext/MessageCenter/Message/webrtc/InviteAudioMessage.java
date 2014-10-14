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
 * @description 邀请对方音频会话消息
 * @author beyond.zhang   
 */
public class InviteAudioMessage implements Message{

	 public static final short CODE = 220;
		

private Long userId;
	
	private String userName;
	

	private Long inviteeId;

	private String inviteeName;
	
	private long  sessionId;
	
	private Long date;
	
	private Endpoint endpoint;

	private int length;
	
	public InviteAudioMessage() {
		super();
	}

	public InviteAudioMessage(Long userId, String userName,
			Long inviterId, String inviterName, int sessionId, Long date) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.inviteeId = inviterId;
		this.inviteeName = inviterName;
		this.sessionId = sessionId;
		this.date = date;
		this.length =2+ 8 + userName.getBytes().length+ 8 + inviterName.getBytes().length + 4+6 +2*1;

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

	public Long getInviteeId() {
		return inviteeId;
	}

	public void setInviteeId(Long inviteeId) {
		this.inviteeId = inviteeId;
	}

	public String getInviteeName() {
		return inviteeName;
	}

	public void setInviteeName(String inviteeName) {
		this.inviteeName = inviteeName;
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
		byte[] inviteeNameBytes = inviteeName.getBytes(CharsetUtil.UTF_8);
		buffer.writeLong(userId);
		buffer.writeByte(userNameBytes.length);
		buffer.writeBytes(userNameBytes);
		buffer.writeLong(inviteeId);
		buffer.writeByte(inviteeNameBytes.length);
		buffer.writeBytes(inviteeNameBytes);
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
		inviteeId = buffer.readLong();
		short inviteeNameLen = buffer.readUnsignedByte();
		byte[] inviteeNameBytes = new byte[inviteeNameLen];
		buffer.readBytes(inviteeNameBytes);
		inviteeName = new String(inviteeNameBytes, CharsetUtil.UTF_8);
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
		map.put("inviteeId", inviteeId);
		map.put("date", date);
		map.put("userName", userName);
		map.put("inviteeName", inviteeName);
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
		inviteeId = map.get("inviteeId").asLong();
		date = map.get("date").asLong();
		userName = map.get("userName").asText();
		inviteeName = map.get("inviteeName").asText();
		sessionId = map.get("sessionId").intValue();
		this.length = 2 + 8 + 8 + 6 + 4 + 2 * 1
				+ Utils.getUTF8StringLength(userName)
				+ Utils.getUTF8StringLength(inviteeName);
	}
	
}
