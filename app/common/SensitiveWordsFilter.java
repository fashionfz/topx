package common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.db.jpa.JPA;

/**
 * 敏感字符过滤
 * @version 1.0
 */
public class SensitiveWordsFilter {
	
	private static Set<String> sensitiveWordsSet = null;
	public static final String TARGET_WORDS = "**";
	public static final int CACHE_TIME = 24 * 60 * 60;
	
	public static Set<String> obtainSensitiveWords(){
//		Cache.remove(Constants.CACHE_SENSITIVE_WORDS); // 清除缓存
		Set<String> wordsSet = (Set<String>) Cache.get(Constants.CACHE_SENSITIVE_WORDS);
		if (wordsSet == null) {
			wordsSet = getSensitiveWordSet();
			Cache.set(Constants.CACHE_SENSITIVE_WORDS, wordsSet,CACHE_TIME);
		}
		return wordsSet;
	}
	
	private static Set<String> getSensitiveWordSet(){
		List<String> wordsList = (List<String>)JPA.em().createQuery("select s.words from SensitiveCharacter s").getResultList();
		String words = null;
		Set<String> wordSet = new HashSet<String>();
		if (CollectionUtils.isNotEmpty(wordsList)) {
			words = wordsList.get(0);
			String[] ss = words.split("\\s+|,");
			for (String s : ss) {
				if (StringUtils.isNotEmpty(s)) {
					wordSet.add(s.trim());
				}
			}
		}
		return wordSet;
	}
	
	public static String doFilter(String str) {
		sensitiveWordsSet = obtainSensitiveWords();
		if (CollectionUtils.isNotEmpty(sensitiveWordsSet)) {
			Iterator<String> ite = sensitiveWordsSet.iterator();
			while (ite.hasNext()) {
				String item = ite.next();
				if (StringUtils.isNotEmpty(item)) {
					str = str.replace(item, TARGET_WORDS);
				}
			}
		}
		return str;
	}

}
