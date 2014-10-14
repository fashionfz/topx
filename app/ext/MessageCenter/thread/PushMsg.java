package ext.MessageCenter.thread;

import java.util.concurrent.BlockingQueue;

import play.Logger;
import play.Logger.ALogger;
import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.ChannelFactory;
/**
 * 
 * @description 推送消息任务
 * @author beyond.zhang   
 */
public class PushMsg implements Runnable{
    private static ALogger LOGGER = Logger.of(PushMsg.class);
	private BlockingQueue<Message> queue; 

	public PushMsg(BlockingQueue<Message> queue) {
		super();
		this.queue = queue;
	}

	@Override
	public void run() {
		while(!queue.isEmpty()){
			try {
				ChannelFactory.getChannel().writeAndFlush(queue.take());
			} catch (Exception e) {
				LOGGER.error("发送消息任务异常：", e);
			}
		}
		
	}

}
