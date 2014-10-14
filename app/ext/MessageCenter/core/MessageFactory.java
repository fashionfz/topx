package ext.MessageCenter.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.UnsupportedMessageTypeException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.Message.base.AbNormalOfflineMessage;
import ext.MessageCenter.Message.base.BinaryMessage;
import ext.MessageCenter.Message.base.CacheBinaryMessage;
import ext.MessageCenter.Message.base.HeartbeatMessage;
import ext.MessageCenter.Message.base.NormalOfflineMessage;
import ext.MessageCenter.Message.base.ReportMessage;
import ext.MessageCenter.Message.base.ResponseMessage;
import ext.MessageCenter.Message.base.TxtMessage;
import ext.MessageCenter.Message.business.AcceptConsultEndMessage;
import ext.MessageCenter.Message.business.ChatMessage;
import ext.MessageCenter.Message.business.CommentMessage;
import ext.MessageCenter.Message.business.ConsultAcceptMessage;
import ext.MessageCenter.Message.business.ConsultEndMessage;
import ext.MessageCenter.Message.business.ConsultRejectMessage;
import ext.MessageCenter.Message.business.ConsultTimeAcpMessage;
import ext.MessageCenter.Message.business.ConsultTimeMessage;
import ext.MessageCenter.Message.business.ConsultTimeRejMessage;
import ext.MessageCenter.Message.business.LackBalanceMessage;
import ext.MessageCenter.Message.business.RejectConsultEndMessage;
import ext.MessageCenter.Message.business.ReplyMessage;
import ext.MessageCenter.Message.business.ReserveMessage;
import ext.MessageCenter.Message.business.TransferMessage;
import ext.MessageCenter.Message.webrtc.AcceptAudioMessage;
import ext.MessageCenter.Message.webrtc.AcceptVideoMessage;
import ext.MessageCenter.Message.webrtc.AnswerMessage;
import ext.MessageCenter.Message.webrtc.AudioTimeoutMessage;
import ext.MessageCenter.Message.webrtc.CloseCameraMessage;
import ext.MessageCenter.Message.webrtc.CloseMicMessage;
import ext.MessageCenter.Message.webrtc.CreateSessionFailMessage;
import ext.MessageCenter.Message.webrtc.CreateSessionMessage;
import ext.MessageCenter.Message.webrtc.CreateSessionSuccessMessage;
import ext.MessageCenter.Message.webrtc.InviteAudioMessage;
import ext.MessageCenter.Message.webrtc.InviteVideoMessage;
import ext.MessageCenter.Message.webrtc.NetworkSpeedMessage;
import ext.MessageCenter.Message.webrtc.OfferMessage;
import ext.MessageCenter.Message.webrtc.OpenCameraMessage;
import ext.MessageCenter.Message.webrtc.OpenMicMessage;
import ext.MessageCenter.Message.webrtc.SendCandidateMessage;
import ext.MessageCenter.Message.webrtc.UserDropMessage;
import ext.MessageCenter.Message.webrtc.VideoTimeoutMessage;
import ext.MessageCenter.utils.JsonUtils;

public class MessageFactory {
	public static final Map<Short, Class<?>> SUPPORED_CODES = new HashMap<Short, Class<?>>();

	public static final byte IDENTITY = 77;
	public static final int MIN_LENGTH = 7;
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static final int MAX_DATA_LENGTH = 65535;

	static {
		SUPPORED_CODES.put(ReportMessage.CODE, ReportMessage.class);
		SUPPORED_CODES.put(TxtMessage.CODE, TxtMessage.class);
		SUPPORED_CODES.put(ResponseMessage.CODE, ResponseMessage.class);
		SUPPORED_CODES.put(BinaryMessage.CODE, BinaryMessage.class);

		SUPPORED_CODES.put(CommentMessage.CODE, CommentMessage.class);
		SUPPORED_CODES.put(ConsultAcceptMessage.CODE,ConsultAcceptMessage.class);
		SUPPORED_CODES.put(ChatMessage.CODE, ChatMessage.class);
		SUPPORED_CODES.put(ConsultRejectMessage.CODE,ConsultRejectMessage.class);

		SUPPORED_CODES.put(ReplyMessage.CODE, ReplyMessage.class);
		SUPPORED_CODES.put(ReserveMessage.CODE, ReserveMessage.class);
		SUPPORED_CODES.put(TransferMessage.CODE, TransferMessage.class);

		SUPPORED_CODES.put(RejectConsultEndMessage.CODE,RejectConsultEndMessage.class);
		SUPPORED_CODES.put(AcceptConsultEndMessage.CODE,AcceptConsultEndMessage.class);
		SUPPORED_CODES.put(ConsultEndMessage.CODE, ConsultEndMessage.class);

		SUPPORED_CODES.put(ConsultTimeMessage.CODE, ConsultTimeMessage.class);
		SUPPORED_CODES.put(ConsultTimeAcpMessage.CODE,ConsultTimeAcpMessage.class);
		SUPPORED_CODES.put(ConsultTimeRejMessage.CODE,ConsultTimeRejMessage.class);
		
		SUPPORED_CODES.put(AcceptAudioMessage.CODE,AcceptAudioMessage.class);
		SUPPORED_CODES.put(AcceptVideoMessage.CODE,AcceptVideoMessage.class);
		SUPPORED_CODES.put(AudioTimeoutMessage.CODE,AudioTimeoutMessage.class);
		SUPPORED_CODES.put(InviteAudioMessage.CODE,InviteAudioMessage.class);
		SUPPORED_CODES.put(InviteVideoMessage.CODE,InviteVideoMessage.class);
		SUPPORED_CODES.put(VideoTimeoutMessage.CODE,VideoTimeoutMessage.class);
		
		SUPPORED_CODES.put(CreateSessionFailMessage.CODE,CreateSessionFailMessage.class);
		SUPPORED_CODES.put(CreateSessionMessage.CODE,CreateSessionMessage.class);
		SUPPORED_CODES.put(CreateSessionSuccessMessage.CODE,CreateSessionSuccessMessage.class);
		SUPPORED_CODES.put(AnswerMessage.CODE,AnswerMessage.class);
		SUPPORED_CODES.put(OfferMessage.CODE,OfferMessage.class);
		
		SUPPORED_CODES.put(CloseCameraMessage.CODE,CloseCameraMessage.class);
		SUPPORED_CODES.put(CloseMicMessage.CODE,CloseMicMessage.class);
		SUPPORED_CODES.put(NetworkSpeedMessage.CODE,NetworkSpeedMessage.class);
		SUPPORED_CODES.put(OpenCameraMessage.CODE,OpenCameraMessage.class);
		SUPPORED_CODES.put(OpenMicMessage.CODE,OpenMicMessage.class);
		
		SUPPORED_CODES.put(SendCandidateMessage.CODE,SendCandidateMessage.class);
		SUPPORED_CODES.put(UserDropMessage.CODE,UserDropMessage.class);
		
		SUPPORED_CODES.put(AbNormalOfflineMessage.CODE,AbNormalOfflineMessage.class);
		SUPPORED_CODES.put(NormalOfflineMessage.CODE,NormalOfflineMessage.class);
		
		SUPPORED_CODES.put(CacheBinaryMessage.CODE,CacheBinaryMessage.class);
		SUPPORED_CODES.put(LackBalanceMessage.CODE,LackBalanceMessage.class);
		SUPPORED_CODES.put(HeartbeatMessage.CODE,HeartbeatMessage.class);
		

	}

	public static Message create(ByteBuf buffer) {
		if (buffer.readableBytes() < MIN_LENGTH) {
			return null;
		} else {
			buffer.markReaderIndex();
			if (buffer.readByte() == IDENTITY) {
				int len = buffer.readInt();
				
				if (buffer.readableBytes() < len) {
					buffer.resetReaderIndex();
					return null;
				} else {
					short code = buffer.readShort();
					if (SUPPORED_CODES.containsKey(code)) {
						Message message = null;
						try {
							message = (Message) SUPPORED_CODES.get(code)
									.newInstance();
						} catch (InstantiationException e) {
							throw new IllegalArgumentException(
									"new instance error");
						} catch (IllegalAccessException e) {
							throw new IllegalArgumentException(
									"new instance error");
						}
						message.setLength(len);
						message.fromBinary(buffer);
						return message;
					} else {
						throw new UnsupportedMessageTypeException(
								"Not supported by Me!");
					}
				}
			} else {
				throw new UnsupportedMessageTypeException(
						"Not supported by Me!");
			}
		}
	}

	public static Message create(String str) {
		Map<String, JsonNode> map = null;
		try {
			map = JsonUtils.parse(str);
		} catch (IOException e) {
			throw new UnsupportedMessageTypeException("Not supported by Me!");
		}
		if (!map.containsKey("code")) {
			throw new UnsupportedMessageTypeException("Not supported by Me!");
		} else {
			short code = 0;
			try {
				code = (short) map.get("code").asInt();
			} catch (Exception e) {
				throw new UnsupportedMessageTypeException(
						"Not supported by Me!");
			}
			if (SUPPORED_CODES.containsKey(code)) {
				Message message = null;
				try {
					message = (Message) SUPPORED_CODES.get(code).newInstance();
				} catch (InstantiationException e) {
					throw new IllegalArgumentException("new instance error");
				} catch (IllegalAccessException e) {
					throw new IllegalArgumentException("new instance error");
				}
				message.fromMap(map);
				return message;
			} else {
				throw new UnsupportedMessageTypeException(
						"Not supported by Me!");
			}
		}
	}

	public static ByteBuf createByteBuf(Message msg, ChannelHandlerContext ctx) {
		ByteBuf buffer = ctx.alloc().buffer(msg.getLength() + 5);
		buffer.writeByte(IDENTITY);
		buffer.writeInt(msg.getLength());
		buffer.writeShort(msg.getCode());
		return buffer;
	}
	
	public static void autoWriteLength(ByteBuf buffer) {
	    buffer.setInt(1, buffer.writerIndex() - 5);
	}

	public static Message create(int code) throws InstantiationException,
			IllegalAccessException {
		return (Message) SUPPORED_CODES.get(code).newInstance();
	}

	// 创建二进制消息
	public static Message createBinaryMessage(Long senderId, Long receiverId,
			byte[] data, String senderName, String receiverName,
			String fileName, byte finish) throws Exception {
		return new BinaryMessage(senderId, receiverId, data, senderName,
				receiverName, fileName, finish);
	}

	// 创建评论消息
	public static Message createCommentMessage(Long senderId, Long receiverId,
			String senderName, String receiverName, String content, Long date) {
		return new CommentMessage(senderId, receiverId, senderName,
				receiverName, content, date);
	}

	// 创建回复消息
	public static Message createReplyMessage(Long senderId, Long receiverId,
			Long date, String senderName, String receiverName, String content) {
		return new ReplyMessage(senderId, receiverId, date, senderName,
				receiverName, content);
	}

	// 创建上报消息
	public static Message createReportMessage(Long id, String token) {
		return new ReportMessage(id, token);
	}

	// 创建预约消息
	public static Message createReserveMessage(Long senderId, Long receiverId,
			Long date, String senderName, String receiverName, Long fromDate,
			Long toDate) {
		return new ReserveMessage(senderId, receiverId, date, senderName,
				receiverName, fromDate, toDate);
	}

	// 创建转账消息
	public static Message createTransferMessage(Long senderId, Long receiverId,
			long date, String senderName, String receiverName, double amount,
			byte currency) {
		return new TransferMessage(senderId, receiverId, date, senderName,
				receiverName, amount, currency);
	}

	// 创建文本消息
	public static Message createTxtMessage(Long senderId, Long receiverId,
			String data, String senderName, String receiverName) {
		return new TxtMessage(senderId, receiverId, data, senderName,
				receiverName);
	}

	// 创建接收咨询消息
	public static Message createConsultAcceptMessage(Long senderId,
			Long receiverId, Long date, String senderName, String receiverName,
			Long consultId) {
		return new ConsultAcceptMessage(senderId, receiverId, date, senderName,
				receiverName, consultId);
	}

	// 创建聊天消息
	public static Message createChatMessage(Long senderId, Long receiverId,
			Long date, String senderName, String receiverName) {
		return new ChatMessage(senderId, receiverId, date, senderName,receiverName);
	}

	// 创建拒绝聊天消息
	public static Message createChatRejectMessage(Long senderId,
			Long receiverId, Long date, String senderName, String receiverName) {
		return new ConsultRejectMessage(senderId, receiverId, date, senderName,receiverName);
	}

	// 创建拒绝结束视频消息
	public static Message createRejectConsultEndMessage(Long senderId,
			Long receiverId, Long date, String senderName, String receiverName,
			Long consultId) {
		return new RejectConsultEndMessage(senderId, receiverId, date,
				senderName, receiverName, consultId);
	}

	// 创建接收结束视频消息
	public static Message createAcceptConsultEndMessage(Long senderId,
			Long receiverId, Long date, String senderName, String receiverName,
			Long consultId) {
		return new AcceptConsultEndMessage(senderId, receiverId, date,
				senderName, receiverName, consultId);
	}

	// 创建结束视频消息
	public static Message createConsultEndMessage(Long senderId,
			Long receiverId, Long date, String senderName, String receiverName,
			Long consultId) {
		return new ConsultEndMessage(senderId, receiverId, date, senderName,
				receiverName, consultId);
	}

	//创建咨询计时消息
	public static Message createConsultTimeMessage(Long senderId,
			Long receiverId, Long date, String senderName, String receiverName) {
		return new ConsultTimeMessage(senderId, receiverId, date, senderName,
				receiverName);
	}

	//创建接收咨询计时消息
	public static Message createConsultTimeAcpMessage(Long senderId,
			Long receiverId, Long date, String senderName, String receiverName,
			Long consultId) {
		return new ConsultTimeAcpMessage(senderId, receiverId, date,
				senderName, receiverName, consultId);
	}

	//创建拒绝咨询计时消息
	public static Message createConsultTimeRejMessage(Long senderId,
			Long receiverId, Long date, String senderName, String receiverName,
			Long consultId) {
		return new ConsultTimeRejMessage(senderId, receiverId, date,
				senderName, receiverName, consultId);
	}
	// 创建接受音频会话消息
	public static Message createAcceptAudioSessionRequestMessage(Long userId, String userName,
			Long inviterId, String inviterName, int sessionId, Long date){
		return new AcceptAudioMessage(userId,userName,inviterId,inviterName,sessionId,date);
	}
	
	// 创建接受视频会话消息
		public static Message createAcceptVideoSessionRequestMessage(Long userId, String userName,
				Long inviterId, String inviterName, int sessionId, Long date){
			return new AcceptVideoMessage(userId,userName,inviterId,inviterName,sessionId,date);
		}
		
		// 创建音频会话超时
		public static Message createAudioSessionRequestTimeoutMessage(Long userId, String userName,
				Long inviterId, String inviterName, int sessionId, Long date){
			return new AudioTimeoutMessage(userId,userName,inviterId,inviterName,sessionId,date);
		}
		
		// 创建邀请对方音频会话消息
		public static Message createInviteEachJionAudioSessiomMessage(Long userId, String userName,
				Long inviterId, String inviterName, int sessionId, Long date){
			return new InviteAudioMessage(userId,userName,inviterId,inviterName,sessionId,date);
		}
		
		// 创建邀请对方视频会话消息
		public static Message createInviteEachJionVideoSessiomMessage(Long userId, String userName,
				Long inviterId, String inviterName, int sessionId, Long date){
			return new InviteVideoMessage(userId,userName,inviterId,inviterName,sessionId,date);
		}
		
		// 创建视频请求超时消息
		public static Message createVideoSessionRequestTimeoutMessage(Long userId, String userName,
				Long inviterId, String inviterName, int sessionId, Long date){
			return new VideoTimeoutMessage(userId,userName,inviterId,inviterName,sessionId,date);
		}
		
		
		public static Message createOfferMessage(Long senderId, Long receiverId, int sessionId,
				String sdp, String agent){
			return new OfferMessage(senderId,receiverId,sessionId,sdp,agent);
		}
		
		public static Message createAnswerMessage(Long senderId, Long receiverId, int sessionId,
				String sdp, String agent){
			return new AnswerMessage(senderId,receiverId,sessionId,sdp,agent);
		}
        // 关闭摄像头消息
		public static Message createCloseCameraMessage(Long senderId, Long receiverId, Long date,
				String senderName, String receiverName, int sessionId){
			return new CloseCameraMessage(senderId,receiverId,date,senderName,receiverName,sessionId);
		}
		
		// 关闭麦克风消息
		public static Message createCloseMicMessage(Long senderId, Long receiverId, Long date,
				String senderName, String receiverName, int sessionId){
			return new CloseMicMessage(senderId,receiverId,date,senderName,receiverName,sessionId);
		}
		
		
		public static Message createNetworkSpeedMessage(Long senderId, Long receiverId, int key){
			return new NetworkSpeedMessage(senderId,receiverId,key);
		}
		// 打开摄像头消息
		public static Message createOpenCameraMessage(Long senderId, Long receiverId, Long date,
				String senderName, String receiverName, int sessionId){
			return new OpenCameraMessage(senderId,receiverId,date,senderName,receiverName,sessionId);
		}
		
		// 打开麦克风消息
		public static Message createOpenMicMessage(Long senderId, Long receiverId, Long date,
				String senderName, String receiverName, int sessionId){
			return new OpenMicMessage(senderId,receiverId,date,senderName,receiverName,sessionId);
		}
		// 拒绝音频会话消息
	/*	public static Message createRejectAudioEndMessage(Long userId, String userName, Long inviterId,
				String inviterName, int sessionId, Long date){
			return new RejectAudioEndMessage(userId,userName,inviterId,inviterName,sessionId,date);
		}
		// 拒绝视频会话消息
	    public 	static Message createRejectVideoEndMessage(Long userId, String userName, Long inviterId,
	    		String inviterName, int sessionId, Long date){
	    	return new RejectVideoEndMessage(userId,userName,inviterId,inviterName,sessionId,date);
	    }*/
		
		public static Message createSendCandidateMessage(Long senderId, Long receiverId,
				int sessionId, String candidate){
			return new SendCandidateMessage(senderId,receiverId,sessionId,candidate);
		}
		// 创建用户掉线消息
		public static Message createUserDropMessage(Long userId, String userName){
			return new UserDropMessage(userId,userName);
		}
		// 异常下线消息
		public static Message createAbNormalOfflineMessage(){
			return new AbNormalOfflineMessage();
		}
		// 正常下线消息
		public static Message createNormalOfflineMessage(Long userId, String userName){
			return new NormalOfflineMessage(userId,userName);
		}
		// 余额不足提醒消息
		public static Message createLackBalanceMessage(Long senderId, Long receiverId, Long date,
				String senderName, String receiverName, Long consultId){
			return new LackBalanceMessage(senderId,receiverId,date,senderName,receiverName,consultId);
		}
		
}
