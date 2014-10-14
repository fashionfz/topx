package vo.msg;

import ext.msg.model.Message.MsgType;

/**
 * @ClassName: MessageJson
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014年4月16日 下午3:39:25
 * @author RenYouchao
 * 
 */
public class MessageJsonTS {
	
	public String  amount;
	
	public String currency;

	/**
	 * 请描述
	 * @param senderUserId
	 * @param recevierUserId
	 * @param senderUserName
	 * @param recevierUserName
	 * @param refId
	 * @param content
	 * @param msgType
	 * @param amount
	 * @param currency
	 */
	public MessageJsonTS(String amount, String currency) {
		super();
		this.amount = amount;
		this.currency = currency;
	}
	



	


	
	

}
