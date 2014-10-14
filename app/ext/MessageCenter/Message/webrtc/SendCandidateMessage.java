package ext.MessageCenter.Message.webrtc;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ext.MessageCenter.Message.Message;
import ext.MessageCenter.core.Endpoint;
import ext.MessageCenter.core.MessageFactory;
import ext.MessageCenter.utils.JsonUtils;
import ext.MessageCenter.utils.Utils;
/**
 * 
 * @description 发送Candidate
 * @author beyond.zhang   
 */
public class SendCandidateMessage implements Message{

	 public static final short CODE = 232;
		
		private Long senderId;
		
		private Long receiverId;
		
		private int  sessionId;
		
		private ObjectNode candidate;
		
		private Endpoint endpoint;

		private int length;
		
		

		public SendCandidateMessage() {
			super();
		}
		
		

		public SendCandidateMessage(Long senderId, Long receiverId,
				int sessionId, String candidate) {
			super();
		
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


		public int getSessionId() {
			return sessionId;
		}

		public void setSessionId(int sessionId) {
			this.sessionId = sessionId;
		}

		public ObjectNode getCandidate() {
			return candidate;
		}



		public void setCandidate(ObjectNode candidate) {
			this.candidate = candidate;
		}



		public Endpoint getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(Endpoint endpoint) {
			this.endpoint = endpoint;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public short getCode() {
			return CODE;
		}

		@Override
		public void onReceived() {
			
		}

		@Override
		public ByteBuf toBinary() {
			ByteBuf buffer = MessageFactory.createByteBuf(this,
					endpoint.getContext());
//			byte[] candidateBytes = candidate.getBytes(CharsetUtil.UTF_8);
			buffer.writeLong(senderId);
			buffer.writeLong(receiverId);
			buffer.writeInt(sessionId);
//			buffer.writeByte(candidateBytes.length);
//			buffer.writeBytes(candidateBytes);
			return buffer;
		}

		@Override
		public void fromBinary(ByteBuf buffer) {
			senderId = buffer.readLong();
			receiverId = buffer.readLong();
			sessionId = buffer.readInt();

			short candidateLength = buffer.readShort();
			byte[] candidatebytes = new byte[candidateLength];
			buffer.readBytes(candidatebytes);
//			candidate = new String(candidatebytes, CharsetUtil.UTF_8);
		}

		@Override
		public String toJson() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", CODE);
			map.put("senderId", senderId);
			map.put("receiverId", receiverId);
			map.put("sessionId", sessionId);
			map.put("candidate", candidate);
			String str = null;
			try {
				str = JsonUtils.stringify(map);
			} catch (JsonProcessingException e) {
			}
			return str;
		}

		@Override
		public void fromMap(Map<String, JsonNode> map) {
			senderId = map.get("senderId").asLong();
			receiverId = map.get("receiverId").asLong();
			sessionId = map.get("sessionId").asInt();
			candidate = (ObjectNode)map.get("candidate");
			this.length = 2 + 8 + 8 + 4 + 1 * 1
					+ Utils.getUTF8StringLength(candidate.toString());
		}

}
