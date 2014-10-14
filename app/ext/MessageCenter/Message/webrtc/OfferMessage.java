package ext.MessageCenter.Message.webrtc;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

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
 * @description 发送offer消息
 * @author beyond.zhang   
 */
public class OfferMessage implements Message{

	 public static final short CODE = 230;
		
	 private Long senderId;
		
		private Long receiverId;
		
		private int  sessionId;
		
		private ObjectNode sdp;
		
		private String agent;
		
		private Endpoint endpoint;

		private int length;

		
		
		public OfferMessage() {
			super();
		}

		
		
		public OfferMessage(Long senderId, Long receiverId, int sessionId,
				String sdp, String agent) {

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



		public ObjectNode getSdp() {
			return sdp;
		}



		public void setSdp(ObjectNode sdp) {
			this.sdp = sdp;
		}



		public String getAgent() {
			return agent;
		}

		public void setAgent(String agent) {
			this.agent = agent;
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
//			byte[] sdpBytes = sdp.getBytes(CharsetUtil.UTF_8);
			byte[] agentBytes = agent.getBytes(CharsetUtil.UTF_8);
			buffer.writeLong(senderId);
			buffer.writeLong(receiverId);
			buffer.writeInt(sessionId);
//			buffer.writeShort(sdpBytes.length);
//			buffer.writeBytes(sdpBytes);
			buffer.writeByte(agentBytes.length);
			buffer.writeBytes(agentBytes);
			return buffer;
		}

		@Override
		public void fromBinary(ByteBuf buffer) {
			senderId = buffer.readLong();
			receiverId = buffer.readLong();
			sessionId = buffer.readInt();
			short sdpLength = buffer.readShort();
			byte[] sdpytes = new byte[sdpLength];
			buffer.readBytes(sdpytes);
//			sdp = new String(sdpytes, CharsetUtil.UTF_8);

			byte agentLength = buffer.readByte();
			byte[] agentbytes = new byte[agentLength];
			buffer.readBytes(agentbytes);
			agent = new String(agentbytes, CharsetUtil.UTF_8);
		}

		@Override
		public String toJson() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", CODE);
			map.put("senderId", senderId);
			map.put("receiverId", receiverId);
			map.put("sessionId", sessionId);
			map.put("sdp", sdp);
			map.put("agent", agent);
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
			sdp = (ObjectNode) map.get("sdp");
			agent = map.get("agent").asText();
			this.length = 2 + 8 + 8 + 4 + 2 * 1 + Utils.getUTF8StringLength(sdp.toString())
					+ Utils.getUTF8StringLength(agent);
		}


		
		


}
