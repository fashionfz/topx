package ext.MessageCenter.Message.group;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;
import ext.MessageCenter.utils.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * 创建(翻译)群的消息
 *
 * User: Rocs Zhang
 */
public class GroupNewMessage implements Message {

    private final static Logger log = LoggerFactory.getLogger(GroupNewMessage.class);
    public static final short CODE = 301;

    private Long groupId;

    private Long masterId;

    private String groupName;

    private Endpoint endpoint;

    private int length;

    private String messageId;

   
    
    public GroupNewMessage(Long groupId, Long masterId, String groupName) {
		super();
		this.groupId = groupId;
		this.masterId = masterId;
		this.groupName = groupName;
		this.length  = 19 + Utils.getUTF8StringLength(groupName);
	}
    
    public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Long getMasterId() {
        return masterId;
    }

    public String getGroupName() {
        return groupName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public short getCode() {
        return CODE;
    }

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public void setLength(int length) {
        this.length = length;
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
    	length =16+ 19 + Utils.getUTF8StringLength(groupName);

        ByteBuf buffer = MessageFactory.createByteBuf(this,endpoint.getContext());
    	buffer.writeBytes(Utils.getIdAsByte(messageId));
        buffer.writeLong(masterId);
        buffer.writeLong(groupId);
        byte[] groupNameBytes = groupName.getBytes(CharsetUtil.UTF_8);
        buffer.writeByte(groupNameBytes.length);
        buffer.writeBytes(groupNameBytes);
        return buffer;
    }

    @Override
    public void fromBinary(ByteBuf buffer) {
    	byte[] messageIdBytes = new byte[16];
		buffer.readBytes(messageIdBytes);
		messageId = Utils.convertBytesToUUID(messageIdBytes);
        masterId = buffer.readLong();
        groupId = buffer.readLong();
        short groupNameLen = buffer.readUnsignedByte();
        byte[] groupNameBytes = new byte[groupNameLen];
        buffer.readBytes(groupNameBytes);
        groupName = new String(groupNameBytes, CharsetUtil.UTF_8);
    }

    @Override
    public String toJson() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", CODE);
        map.put("masterId", masterId);
        map.put("groupId", groupId);
        map.put("groupName", groupName);
        map.put("messageId", messageId);
        String str = null;
        try {
            str = JsonUtils.stringify(map);
        } catch (JsonProcessingException e) {
            log.error("创建(翻译)群消息{}异常：",CODE ,e);
        }
        return str;
    }

    @Override
    public void fromMap(Map<String, JsonNode> map) {
        masterId = map.get("masterId").asLong();
        groupId = map.get("groupId").asLong();
        groupName = map.get("groupName").asText();
        messageId= map.get("messageId").asText();
        length = 19 +16+ Utils.getUTF8StringLength(groupName);
    }
}
