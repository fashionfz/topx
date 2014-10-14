package ext.MessageCenter.Message.base;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.Utils;

/**
 * @author beyond.zhang
 * @description 用户上线通知客服消息
 */
public class UserOnlineMessage implements Message {
    private final static Logger logger = LoggerFactory
            .getLogger(UserOnlineMessage.class);

    public static final short CODE = 33;

    private Long userId;

    private Long customServiceId;

    private String data;

    private Endpoint endpoint;

    private int length;

    public String userName;

    public String customServiceName;

    private String messageId;

    public UserOnlineMessage() {
        super();
    }

    public UserOnlineMessage(Long userId, Long customServiceId, String data, String userName, String customServiceName) {
        this.messageId = UUID.randomUUID().toString();
        this.userId = userId;
        this.customServiceId = customServiceId;
        this.data = data;
        this.userName = userName;
        this.customServiceName = customServiceName;
        this.length = 2 + 16 + 8 + 1 + userName.getBytes(CharsetUtil.UTF_8).length +
                8 + 1 + customServiceName.getBytes(CharsetUtil.UTF_8).length + 2 + data.getBytes(CharsetUtil.UTF_8).length + 8;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public short getCode() {
        return UserOnlineMessage.CODE;
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

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCustomServiceId() {
		return customServiceId;
	}

	public void setCustomServiceId(Long customServiceId) {
		this.customServiceId = customServiceId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCustomServiceName() {
		return customServiceName;
	}

	public void setCustomServiceName(String customServiceName) {
		this.customServiceName = customServiceName;
	}

	@Override
    public void onReceived() {
    }


    public ByteBuf toBinary() {
    	 byte[] userNameBytes = userName.getBytes(CharsetUtil.UTF_8);
         byte[] customServiceNameByte = customServiceName.getBytes(CharsetUtil.UTF_8);
         byte[] bytes = data.getBytes(CharsetUtil.UTF_8);
         this.length = 2 + 16 + 8 + 1 + userNameBytes.length + 8 + 1 + customServiceNameByte.length + 2 + bytes.length ;
         ByteBuf buffer = MessageFactory.createByteBuf(this,
                 endpoint.getContext());
         buffer.writeBytes(Utils.getIdAsByte(messageId));
         buffer.writeLong(userId);
         buffer.writeByte(userNameBytes.length);
         buffer.writeBytes(userNameBytes);
         buffer.writeLong(customServiceId);
         buffer.writeByte(customServiceNameByte.length);
         buffer.writeBytes(customServiceNameByte);
         buffer.writeShort(bytes.length);
         buffer.writeBytes(bytes);
         return buffer;
    }

    public String toJson() {
        String str = null;
      
        return str;
    }

    @Override
    public void fromBinary(ByteBuf buffer) {
        byte[] messageIdBytes = new byte[16];
        buffer.readBytes(messageIdBytes);
        messageId = Utils.convertBytesToUUID(messageIdBytes);
        userId = buffer.readLong();
        short userNameLen = buffer.readUnsignedByte();
        byte[] userNameBytes = new byte[userNameLen];
        buffer.readBytes(userNameBytes);
        userName = new String(userNameBytes, CharsetUtil.UTF_8);
        customServiceId = buffer.readLong();
        short customServiceLen = buffer.readUnsignedByte();
        byte[] customServiceBytes = new byte[customServiceLen];
        buffer.readBytes(customServiceBytes);
        customServiceName = new String(customServiceBytes, CharsetUtil.UTF_8);
        int len = buffer.readUnsignedShort();
        byte[] bytes = new byte[len];
        buffer.readBytes(bytes);
        data = new String(bytes, CharsetUtil.UTF_8);
        length = 30 + Utils.getUTF8StringLength(data)
                + Utils.getUTF8StringLength(userName)
                + +Utils.getUTF8StringLength(customServiceName);
    }

    @Override
    public void fromMap(Map<String, JsonNode> map) {
        userId = map.get("userId").asLong();
        customServiceId = map.get("customServiceId").asLong();
        data = map.get("data").asText();
        userName = map.get("userName").asText();
        customServiceName = map.get("customServiceName").asText();
        messageId = map.get("messageId").asText();
        length = 30 + Utils.getUTF8StringLength(data)
                + Utils.getUTF8StringLength(userName)
                + +Utils.getUTF8StringLength(customServiceName);
    }

}
