package ext.MessageCenter.Message.business;

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
 * @description 图片消息
 * @author beyond.zhang
 */
public class PictureMessage implements Message {
    public static final short CODE = 14;

    private final static Logger logger = LoggerFactory.getLogger(PictureMessage.class);

    private Long senderId;

    private Long receiverId;

    private byte[] data;

    private Endpoint endpoint;

    private int length;

    private String senderName;

    private String receiverName;

    private String fileName;

    private byte finish;

    private int packageNo;

    private Long dateTime;

    private String messageId;

    public PictureMessage(Long senderId, Long receiverId, byte[] data, String senderName, String receiverName,
            String fileName, byte finish, int packageNo, Long dateTime, String messageId) {
        super();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.data = data;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.fileName = fileName;
        this.finish = finish;
        this.packageNo = packageNo;
        this.dateTime = dateTime;
        this.messageId = messageId;
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

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public short getCode() {
        return PictureMessage.CODE;
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
        byte[] consumeNameByte = receiverName.getBytes(CharsetUtil.UTF_8);
        buffer.writeByte(senderNameBytes.length);
        buffer.writeBytes(senderNameBytes);
        buffer.writeLong(receiverId);
        buffer.writeByte(consumeNameByte.length);
        buffer.writeBytes(consumeNameByte);
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
        map.put("receiverId", receiverId);
        map.put("data", data);
        map.put("senderName", senderName);
        map.put("receiverName", receiverName);
        map.put("fileName", fileName);
        map.put("DateTime", dateTime);
        map.put("finish", finish);
        map.put("messageId", messageId);
        String str = null;
        try {
            str = JsonUtils.stringify(map);
        } catch (JsonProcessingException e) {
            logger.error("图片传输异常：" + e);
        }
        return str;
    }

    @Override
    public void fromBinary(ByteBuf buffer) {
    }

    @Override
    public void fromMap(Map<String, JsonNode> map) {
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
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }
}
