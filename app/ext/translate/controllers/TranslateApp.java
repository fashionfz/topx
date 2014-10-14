/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年5月29日
 */
package ext.translate.controllers;

import com.fasterxml.jackson.databind.JsonNode;

import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import ext.translate.Translate;
import ext.translate.TranslateVO;

/**
 * @ClassName: TranslateApp
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014年5月29日 下午2:51:39
 * @author RenYouchao
 * 
 */
public class TranslateApp extends Controller {
	
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result autots() {
		JsonNode json = request().body().asJson();
		String q = json.findPath("q").asText();
		Translate tran = new Translate(q);
		TranslateVO tv = tran.translateAdapt();
		return ok(play.libs.Json.toJson(tv));
	}
	
	

}
