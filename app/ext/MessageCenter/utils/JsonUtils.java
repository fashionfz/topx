package ext.MessageCenter.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String stringify(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}

	public static Map<String, JsonNode> parse(String str)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(str, new TypeReference<Map<String, JsonNode>>() {
		});
	}

}
