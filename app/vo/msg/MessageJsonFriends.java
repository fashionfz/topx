package vo.msg;


/**
 * @ClassName: MessageJsonFriends
 * @Description: 好友消息的Json数据类
 * @date 2014年4月16日 下午3:39:25
 * @author RenYouchao
 * 
 */
public class MessageJsonFriends {
	
	public String content;
	

	/**
	 * @param hostedId
	 * @param hostedName
	 * @param content
	 */
	public MessageJsonFriends(String content) {
		super();
		this.content = content;
	}

	
}
