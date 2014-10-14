package common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符替换
 * @version 1.0
 */
public class ReplaceWordsFilter {
	
	private static Map<String,String> replaceWordsMap = new HashMap<String,String>();

	static { // html字符编码
		replaceWordsMap.put("<", "&lt;");
		replaceWordsMap.put(">", "&gt;");
	}
	
	public static String doFilter(String str) {
		if (StringUtils.isNotEmpty(str)) {
			Set<String> keySet = replaceWordsMap.keySet();
			if (CollectionUtils.isNotEmpty(keySet)) {
				for (String key : keySet) {
					String value = replaceWordsMap.get(key);
					str = str.replace(key, value);
				}
			}
		}
		return str;
	}
	
	
}
