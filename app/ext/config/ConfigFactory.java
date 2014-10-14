/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年5月12日
 */
package ext.config;

import static play.Play.application;

import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import play.cache.Cache;
import system.models.Config;
import common.jackjson.JackJsonUtil;

/**
 * @ClassName: ConfigFactory
 * @Description: 配置文件的管理工厂类
 * @date 2014年5月12日 下午4:18:45
 * @author RenYouchao
 */
public class ConfigFactory {

	/**
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		String value = (String) Cache.get(key);
		if (value != null)
			return value;
		else
			return application().configuration().getString(key); 
	}

	public static Boolean getBoolean(String key) {
	    String value = (String) Cache.get(key);
	    if (value != null)
	        return new Boolean(value);
	    else
	        return application().configuration().getBoolean(key); 
	}
	
	public static Integer getInt(String key) {
		String value = (String) Cache.get(key);
		if (value != null)
			return new Integer(value);
		else
			return application().configuration().getInt(key); 
	}

	public static Long getLong(String key) {
		String value = (String) Cache.get(key);
		if (value != null)
			return new Long(value);
		else
			return application().configuration().getLong(key); 
	}

	public static List<String> getList(String key) {
		String value = (String) Cache.get(key);
		if (value != null)
			try {
				return JackJsonUtil.getMapperInstance(false).readValue(value, List.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			return application().configuration().getStringList(key);
		return null; 
	}

	/**
	 * @param key
	 */
	public static void deleteString(String key) {
		Cache.remove(key);
	}
	
	public static void pushMemory(){
		Config config = new Config();
		List<Config> configs = config.findConfigAll();
		for (Config cf:configs){
			setString(cf.property,cf.value);
		}
	}

	/**
	 * @param key
	 */
	public static void setString(String key, String value) {
		Cache.set(key, value);
	}

}
