package ext.MessageCenter.Message;

import io.netty.buffer.ByteBuf;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import ext.MessageCenter.core.Endpoint;


public interface Message {

	short getCode();
	
	int getLength();
	
	void setLength(int length);
	
	void setEndpoint(Endpoint endpoint);
	
	void onReceived();

	ByteBuf toBinary();
	
	void fromBinary(ByteBuf buffer);

	String toJson();
	
	void fromMap(Map<String, JsonNode> map);
	
}
