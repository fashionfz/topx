package common.jackjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;

public class JackJsonUtil {
	
	private static ObjectMapper mapper;

	/**
	 * @param createNew
	 *            是否创建一个新的Mapper
	 * @return
	 */
	public static synchronized ObjectMapper getMapperInstance(boolean createNew) {
		if (createNew) {
			return new ObjectMapper();
		} else if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}

    public static synchronized  ObjectMapper getMapperInstance() {
        return getMapperInstance(false);
    }

    public static String writeValueAsString(Object object) {
        try {
            return getMapperInstance().writeValueAsString(object);
        } catch (Exception e) {
            Logger.error(e.getLocalizedMessage());
            return "{}";
        }
    }
}
