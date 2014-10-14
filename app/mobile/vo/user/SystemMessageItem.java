package mobile.vo.user;

import com.fasterxml.jackson.databind.JsonNode;
import ext.msg.model.Message;
import ext.msg.model.Message.MsgType;
import mobile.vo.MobileVO;
import models.Group;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import utils.Assets;
import utils.HelomeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ShenTeng
 * @ClassName: SystemMessage
 * @Description: 系统消息
 * @date 2014-4-30 上午10:29:28
 */
public class SystemMessageItem implements MobileVO {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 消息id
     */
    private Long messageId;
    /**
     * 发送者id
     */
    private String senderId;
    /**
     * 发送者
     */
    private String senderName;
    /**
     * 接受者id
     */
    private String recevierId;
    /**
     * 接受者
     */
    private String recevierName;
    /**
     * 关联id *
     */
    private Long refId;
    /**
     * 内容 *
     */
    private String content;
    /**
     * 消息类型 *
     */
    private MsgType msgType;
    /**
     * 消息时间
     */
    private String msgTime;
    /**
     * 已读还是未读
     */
    private Boolean isRead;
    /**
     * 金额 *
     */
    private String amount;
    /**
     * 货币() *
     */
    private Integer currency;
    /**
     * 评论等级 *
     */
    private Integer level;
    /**
     * 是否处理，仅用于好友消息
     */
    private Boolean isHandle;

    private Long groupId;

    private String groupName;

    /**
     * 群组类型<br>
     * NORMAL：普通群组，TRANSLATE：翻译群组， MULTICOMMUNICATE：多人会话
     */
    private String groupType;

    /**
     * 群组头像
     */
    private String groupAvatar;

    private Long hostsId;

    private String hostsName;

    private SystemMessageItem() {
    }

    public static List<SystemMessageItem> createList(List<Message> msgList) {
        List<SystemMessageItem> voList = new ArrayList<SystemMessageItem>();

        List<Long> groupIdList = new ArrayList<Long>();
        for (Message msg : msgList) {
            SystemMessageItem sysMsg = new SystemMessageItem();
            sysMsg.setMessageId(msg.id);
            sysMsg.setSenderId(msg.senderOnly);
            sysMsg.setSenderName(msg.senderName);
            sysMsg.setRecevierId(msg.consumeOnly);
            sysMsg.setRecevierName(msg.consumeName);
            sysMsg.setMsgType(msg.msgType);
            sysMsg.setMsgTime(msg.msgTime == null ? "" : dateFormat.format(msg.msgTime));
            sysMsg.setIsRead(msg.isRead);
            sysMsg.setIsHandle(msg.isHandler);

            if (StringUtils.isNotBlank(msg.content)) {
                JsonNode content = Json.parse(msg.content);
                sysMsg.setRefId(HelomeUtil.defaultIfEqual(content.findPath("refId").asLong(-1L), -1L, null));
                sysMsg.setLevel(HelomeUtil.defaultIfEqual(content.findPath("level").asInt(-1), -1, null));

                Double amount = HelomeUtil.defaultIfEqual(content.findPath("amount").asDouble(-1d), -1d, null);
                if (null != amount) {
                    sysMsg.setAmount(String.format("%.2f", amount).toString());
                }

                sysMsg.setCurrency(HelomeUtil.defaultIfEqual(content.findPath("currency").asInt(-1), -1, null));
                if (content.hasNonNull("content")) {
                    sysMsg.setContent(content.findPath("content").asText());
                }

                sysMsg.setGroupId(HelomeUtil.defaultIfEqual(content.findPath("groupId").asLong(-1L), -1L, null));
                if (null != sysMsg.getGroupId()) {
                    groupIdList.add(sysMsg.getGroupId());
                }

                if (content.hasNonNull("groupName")) {
                    sysMsg.setGroupName(content.findPath("groupName").asText());
                }

                if (content.hasNonNull("type")) {
                    sysMsg.setGroupType(content.findPath("type").asText());
                }

                if (content.hasNonNull("headUrl")) {
                    String headUrl = content.findPath("headUrl").asText();
                    if (StringUtils.isNotBlank(headUrl)) {
                        sysMsg.setGroupAvatar(Assets.at(headUrl));
                    } else if (Group.Type.NORMAL.toString().equals(sysMsg.getGroupType())) {
                        sysMsg.setGroupAvatar(Assets.getDefaultGroupHeadUrl(false));
                    }
                }

                sysMsg.setHostsId(HelomeUtil.defaultIfEqual(content.findPath("hostsId").asLong(-1L), -1L, null));
                if (content.hasNonNull("hostsName")) {
                    sysMsg.setHostsName(content.findPath("hostsName").asText());
                }
            }

            voList.add(sysMsg);
        }

        return voList;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecevierId() {
        return recevierId;
    }

    public void setRecevierId(String recevierId) {
        this.recevierId = recevierId;
    }

    public String getRecevierName() {
        return recevierName;
    }

    public void setRecevierName(String recevierName) {
        this.recevierName = recevierName;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(Boolean isHandle) {
        this.isHandle = isHandle;
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

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public Long getHostsId() {
        return hostsId;
    }

    public void setHostsId(Long hostsId) {
        this.hostsId = hostsId;
    }

    public String getHostsName() {
        return hostsName;
    }

    public void setHostsName(String hostsName) {
        this.hostsName = hostsName;
    }

}
