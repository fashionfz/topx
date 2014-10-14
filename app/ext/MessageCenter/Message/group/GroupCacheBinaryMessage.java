package ext.MessageCenter.Message.group;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

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
import ext.MessageCenter.utils.Utils;
/**
 * 
 * @description 缓存流消息
 * @author beyond.zhang   
 */
public class GroupCacheBinaryMessage implements Message {

	public static final short CODE = 310;
	private final static Logger logger = LoggerFactory.getLogger(GroupCacheBinaryMessage.class);
	
	private String messageId;

	private Long senderId;

	private Long groupId;
	
	private byte[] data;

	private Endpoint endpoint;

	private int length;
	
	private String senderName;
		
	private String groupName;
	
	private String fileName;
	
	private byte finish;
	
	private int packageNo;
	
	private Long dateTime;
	
    public GroupCacheBinaryMessage(String messageId, Long senderId, Long groupId, byte[] data, String senderName,
            String groupName, String fileName, byte finish, int packageNo, Long dateTime) {
        super();
        this.messageId = messageId;
        this.senderId = senderId;
        this.groupId = groupId;
        this.data = data;
        this.senderName = senderName;
        this.groupName = groupName;
        this.fileName = fileName;
        this.finish = finish;
        this.packageNo = packageNo;
        this.dateTime = dateTime;
    }

    public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Long getDateTime() {
		return dateTime;
	}

	public void setDateTime(Long dateTime) {
		this.dateTime = dateTime;
	}

	public int getPackageNo() {
		return packageNo;
	}

	public void setPackageNo(int packageNo) {
		this.packageNo = packageNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}


	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}


	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	@Override
	public short getCode() {
		return GroupCacheBinaryMessage.CODE;
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


	public byte getFinish() {
		return finish;
	}

	public void setFinish(byte finish) {
		this.finish = finish;
	}

	@Override
	public void onReceived() {
	}

	@Override
	public ByteBuf toBinary() {
		ByteBuf buffer = MessageFactory.createByteBuf(this, endpoint.getContext());
		byte[] fileNameByte = fileName.getBytes(CharsetUtil.UTF_8);
		buffer.writeBytes(Utils.getIdAsByte(messageId));
		buffer.writeLong(senderId);
		byte[] senderNameBytes = senderName.getBytes(CharsetUtil.UTF_8);
		byte[] groupNameByte = groupName.getBytes(CharsetUtil.UTF_8);
		buffer.writeByte(senderNameBytes.length);
		buffer.writeBytes(senderNameBytes);
		buffer.writeLong(groupId);
		buffer.writeByte(groupNameByte.length);
		buffer.writeBytes(groupNameByte);
		buffer.writeByte(fileNameByte.length);
		buffer.writeBytes(fileNameByte);
		buffer.writeInt(packageNo);
		buffer.writeLong(dateTime);
		buffer.writeByte(finish);
		buffer.writeBytes(Utils.unsigned16ToBytes(data.length));
		buffer.writeBytes(data);
		return buffer;
	}

	@Override
	public String toJson() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", CODE);
		map.put("senderId", senderId);
		map.put("groupId", groupId);
		map.put("data", data);
		map.put("senderName", senderName);
		map.put("groupName", groupName);
		map.put("fileName", fileName);   
		map.put("DateTime", dateTime);   
		map.put("finish", finish);
		map.put("messageId", messageId);
		String str = null;
		try {
			str = JsonUtils.stringify(map);
		} catch (JsonProcessingException e) {
			logger.error("文件传输异常："+e);
		}
		return str;
	}

	@Override
	public void fromBinary(ByteBuf buffer) {
		byte[] messageIdBytes = new byte[16];
		buffer.readBytes(messageIdBytes);
		messageId = Utils.convertBytesToUUID(messageIdBytes);
		senderId = buffer.readLong();
		byte senderNameLen = buffer.readByte();
		byte[] senderNameBytes = new byte[senderNameLen];
		buffer.readBytes(senderNameBytes);
		senderName = new String(senderNameBytes,CharsetUtil.UTF_8);
		groupId = buffer.readLong();
		short consumeNameLen = buffer.readUnsignedByte();
		byte[] consumeNameBytes = new byte[consumeNameLen];
		buffer.readBytes(consumeNameBytes);
		groupName = new String(consumeNameBytes,CharsetUtil.UTF_8);
		short fileNameLen = buffer.readUnsignedByte();
		byte[] fileNameBytes = new byte[fileNameLen];
		buffer.readBytes(fileNameBytes);
		fileName = new String(fileNameBytes,CharsetUtil.UTF_8);
		packageNo = buffer.readInt();
		dateTime = buffer.readLong();
		finish = buffer.readByte();
		
		byte[] dataLenBytes = new byte[2];
		buffer.readBytes(dataLenBytes);
		int dataLen = Utils.from16Unsigned(dataLenBytes);
		byte[] dataBytes = new byte[dataLen];
		buffer.readBytes(dataBytes);
		data = dataBytes;
		this.length = 2+16+8+1+8+1+1+1+2+4+8+data.length+Utils.getUTF8StringLength(senderName)+Utils.getUTF8StringLength(groupName)+Utils.getUTF8StringLength(fileName);

	}

	@Override
	public void fromMap(Map<String, JsonNode> map) {
		senderId = map.get("senderId").asLong();
		groupId = map.get("groupId").asLong();
		try {
			data = map.get("data").textValue().getBytes("iso8859-1");
		} catch (Exception e) {
			logger.error("文件传输异常："+e);
		}
		messageId = map.get("messageId").asText();
		senderName = map.get("senderName").asText();
		groupName = map.get("groupName").asText();
		fileName = map.get("fileName").asText();
		dateTime = map.get("dateTime").asLong();
		packageNo = map.get("packageNo").asInt();
		finish = Byte.valueOf(String.valueOf(map.get("finish")));
		length = 2+16+8+1+8+1+1+1+2+4+8+data.length+Utils.getUTF8StringLength(senderName)+Utils.getUTF8StringLength(groupName)+Utils.getUTF8StringLength(fileName);

	}
	
	public static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }
        byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }


}
