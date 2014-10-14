package ext.MessageCenter.core;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import play.Logger;
import play.Logger.ALogger;
import ext.MessageCenter.Message.Message;
import ext.MessageCenter.thread.PushMsg;
import ext.config.ConfigFactory;

public class ChannelFactory {
	//private static final String host = "172.16.2.227";
	    private static ALogger LOGGER = Logger.of(ChannelFactory.class);
	    private static String host = ConfigFactory.getString("mc.socket.host");
	    private static Integer port = ConfigFactory.getInt("mc.socket.port");
		private static EventLoopGroup group;
		private static Bootstrap b;
		private static Channel ch;
		private static ExecutorService service;
		private static BlockingQueue<Message> msg = new LinkedBlockingQueue<Message>();
		static{
				 try {
					group = new NioEventLoopGroup();
					service = Executors.newFixedThreadPool(3);
					 b = new Bootstrap();
					 b.group(group).channel(NioSocketChannel.class).handler(new SocketInitializer());
					 LOGGER.info("连接到消息中心{}：{}",host,port);
					 ch = b.connect(host, port).sync().channel();
				} catch (Exception e) {
					LOGGER.error("connect message center:"+e);
				}
		}

		private ChannelFactory() {
		}

		public static synchronized Channel getChannel()  {
		  try {
			if(ch!=null){
					if(!ch.isOpen()){
								ch = b.connect(host, port).sync().channel();
					}
				}else{
						ch = b.connect(host, port).sync().channel();
				}
			} catch (Exception e) {
				getChannel();
				 LOGGER.error("连接到消息中心异常：",e);
			}
		  LOGGER.info("连接到消息中心{}：{}",host,port);
			return ch;
		}
		public static EventLoopGroup getEventLoopGroup()  {
			return group;
		}

		public static synchronized void sendMessage(Message message){
			try {
				ChannelFactory.getChannel().writeAndFlush(message);
			} catch (Exception e) {
				LOGGER.error("push message exception:",e);
			}
		}
		public static void stopSendMsg() {
			service.shutdown();
		}
		
		public static synchronized Channel reConnChannel()  {
			if(!ch.isOpen()){
				 try {
					ch = b.connect(host, port).sync().channel();
				} catch (Exception e) {
					LOGGER.error("connect message center:",e);
					reConnChannel();
				}
			}
			return ch;
		}
}
