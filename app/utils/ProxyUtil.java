/**
 * Project Name:topx
 * @Title: ProxyUtil.java
 * @Package com.helome.monitor.utils
 * @Description: TODO
 * Copyright: Copyright (c) 2014 All Rights Reserved. 
 * Company:helome.com
 * 
 * @author bin.deng@helome.com
 * @date 2014年3月28日 上午10:03:20
 * @version V1.0
 */
package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @ClassName: ProxyUtil
 * @Description: 动态代理工具类，反射获取类的相关字段，
 * 以及反射调用类的setter或getter方法
 * @author bin.deng
 * @date 2014年3月28日 上午10:03:20
 *
 */
public class ProxyUtil {
	protected static final Logger logger = LoggerFactory.getLogger(ProxyUtil.class);
	
	private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 
	  * getter()
	  *根据对象属性字符串调用对应的getter方法返回值
	  * @param obj 对象
	  * @param att 属性字符串
	  * @return
	  * @throws SecurityException
	  * @throws NoSuchMethodException
	  * @throws IllegalArgumentException
	  * @throws IllegalAccessException
	  * @throws InvocationTargetException
	 */
	public static Object getter(Object obj, String att) 
			throws SecurityException, NoSuchMethodException, 
			IllegalArgumentException, IllegalAccessException, InvocationTargetException { 
		try{
			Method method = obj.getClass().getMethod("get" + firstLetterToUpper(att)); 
			return method.invoke(obj); 
		}catch(Exception e){
			return null;
		}
	} 
	
	
	public static Object isser(Object obj, String att) 
			throws SecurityException, NoSuchMethodException, 
			IllegalArgumentException, IllegalAccessException, InvocationTargetException { 
		try{
			Method method = obj.getClass().getMethod("is" + firstLetterToUpper(att)); 
			return method.invoke(obj); 
		}catch(Exception e){
			return null;
		}
	} 
	/**
	 * 
	  * firstLetterToUpper()
	  *首字母大写
	  * @param str 字符串
	  * @return 
	 */
	protected static String firstLetterToUpper(String str) { 
		char[] array = str.toCharArray(); 
		array[0] -= 32; 
		return String.valueOf(array); 
	} 
	
	/**
	 * 
	 * @param obj
	 * @param att
	 * @param value
	 * @param type
	 */
	public static void setter(Object obj, String att, Object value,
			Class<?> type) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
		try {
			att =firstLetterToUpper(att);
			Method method = obj.getClass().getMethod("set" + att, type);
			if (type == String.class)
				method.invoke(obj, toString(value));
			else if (type == Integer.class || type == int.class)
				method.invoke(obj, toInteger(value));
			else if (type == double.class || type == Double.class)
				method.invoke(obj, toDouble(value));
			else if (type == char.class || type == Character.class)
				method.invoke(obj, toCharacter(value));
			else if (type == long.class || type == Long.class)
				method.invoke(obj, toLong(value));
			else if (type == float.class || type == Float.class)
				method.invoke(obj, toFloat(value));
			else if (type == byte.class || type == Byte.class)
				method.invoke(obj, toByte(value));
			else if (type == boolean.class || type == Boolean.class)
				method.invoke(obj, toBoolean(value));
			else if (type == short.class || type == Short.class)
				method.invoke(obj, toShort(value));
			else if (type == java.util.Date.class) {
				method.invoke(obj, df.parse(String.valueOf(value)));
			} else if (type == java.sql.Timestamp.class) {
                method.invoke(obj ,
                        new Timestamp(
                                df.parse(String.valueOf(value)).getTime()));
            } else
				method.invoke(obj, value);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}	
	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static String toString(Object object) {
		if (object == null) {
		    return "";}
		else if (object instanceof Date) {
			return df.format(object);
		} else if (object instanceof Timestamp) {
            return df.format(object);
        }
		else
			return object.toString();
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Integer toInteger(Object object) {
		String str = toString(object);
		if ("".equals(str))
			return 0;
		else
			return Integer.parseInt(str);
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Double toDouble(Object object) {
		String str = toString(object);
		if ("".equals(str))
			return 0.0;
		else
			return Double.parseDouble(str);
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Float toFloat(Object object) {
		String str = toString(object);
		if ("".equals(str))
			return 0.0f;
		else
			return Float.parseFloat(str);
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Long toLong(Object object) {
		String str = toString(object);
		if ("".equals(str))
			return 0l;
		else
			return Long.parseLong(str);
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Boolean toBoolean(Object object) {
		String str = toString(object);
		if ("".equals(str))
			return true;
		else
			return Boolean.parseBoolean(str);
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Short toShort(Object object) {
		String str = toString(object);
		if ("".equals(str))
			return 0;
		else
			return Short.parseShort(str);
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Byte toByte(Object object) {
		String str = toString(object);
		if ("".equals(str))
			return 0;
		else
			return Byte.parseByte(str);
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	protected static Character toCharacter(Object object) {
		if (object == null)
			return '\u0beb';
		else
			return (Character) object;
	}	
	
	
	
	public static Field[] getFieldtoObject(Object obj){
		Field[] fields = obj.getClass().getDeclaredFields();
		Field[] parent = obj.getClass().getSuperclass().getDeclaredFields();
		Field[] result = new Field[fields.length+parent.length];
		System.arraycopy(fields,0,result, 0, fields.length);
		System.arraycopy(parent,0,result, fields.length, parent.length);
		return result;
	}
	
	
	public static Object getObject(String name){
		try {
			return Class.forName(name).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Field[] getFields(String name){
		try {
			Field[] fields = Class.forName(name).getDeclaredFields();
			Field[] child = Class.forName(name).getSuperclass().getDeclaredFields();
			Field[] result = new Field[fields.length+child.length];
			System.arraycopy(fields,0,result, 0, fields.length);
			System.arraycopy(child,0,result, fields.length, child.length);
			return result;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
