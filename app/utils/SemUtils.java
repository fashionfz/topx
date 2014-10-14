/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年2月14日
 */
package utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @ClassName: SemUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014年2月14日 下午3:40:49
 * @author RenYouchao
 * 
 */
public class SemUtils {
	
	static ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
	
	public static ScriptEngine getEngine(){
		return engine;
	}

}
