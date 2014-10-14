/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年4月8日
 */
package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: RegexUtils
 * @Description: 正则表达式工具类
 * @date 2014年4月8日 上午10:13:38
 * @author RenYouchao
 * 
 */
public class RegexUtils {
	
	static String regex = "<[^>].*?>";

	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if( !isNum.matches() )
		{
			return false;
		}
		return true;
	}
	
	public static String replaceFont(String str){
		Pattern pat = Pattern.compile(regex);
		Matcher mat=pat.matcher(str);
		String s = mat.replaceAll(""); 
		return s;
	}

}
