package vo.msg;

import ext.msg.model.Message.MsgType;

/**
 * @ClassName: MessageJson
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014年4月16日 下午3:39:25
 * @author RenYouchao
 * 
 */
public class MessageJson {

    /** 消息id */
    public Long messageId;
    /** 发送者id */
    public String senderId;
    /** 发送者 */
    public String senderName;
    /** 接受者id */
    public String recevierId;
    /** 接受者 */
    public String recevierName;
    /** 关联id **/
    public Long refId;
    /** 内容 **/
    public String content;
    /** 消息类型 **/
    public MsgType msgType;
    /** 消息时间 */
    public String msgTime;
    /** 已读还是未读 */
    public Boolean isRead;
    /** 金额 **/
    public String amount;
    /** 货币 **/
    public Integer currency;
    /** 群组ID**/
    public Long groupId;
    
    public String groupName;
	/**请描述**/
	public Boolean isHandler; 
	/**群主id**/
	public Long hostsId;
	/**群主姓名**/
	public String hostsName;
	
	
	
	public MessageJson(){
		
	}

    /**
     * 请描述
     * 
     * @param senderId
     * @param senderName
     * @param recevierId
     * @param recevierName
     * @param refId
     * @param content
     * @param msgType
     * @param msgTime
     * @param isRead
     */
    public MessageJson(Long messageId, String senderId, String senderName, String recevierId, String recevierName,
            MsgType msgType, String msgTime, Boolean isRead) {
        super();
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.recevierId = recevierId;
        this.recevierName = recevierName;
        this.msgType = msgType;
        this.msgTime = msgTime;
        this.isRead = isRead;
    }

}
