/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年5月15日
 */
package system.controllers;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import system.models.Config;
import system.vo.ext.ExtForm;

import com.fasterxml.jackson.databind.JsonNode;

import ext.config.ConfigFactory;

/**
 * @ClassName: ConfigApp
 * @Description: 配置管理的controller
 * @date 2014年5月15日 上午10:33:33
 * @author RenYouchao
 * 
 */
public class ConfigApp extends Controller{
	
	@Transactional(readOnly = true)
	public static Result list() {
		Config config = new Config();
		return ok(play.libs.Json.toJson(config.findConfigAll()));
	}
	
	@Transactional
	public static Result create(){
		JsonNode json = request().body().asJson();
		String property = json.findPath("property").asText();
		String value = json.findPath("value").asText();
		String introduce = json.findPath("introduce").asText();
		Config config = new Config(property, value, introduce);
        config.saveOrUpdate();
        return ok(play.libs.Json.toJson(config));
	}
	
	
	@Transactional
	public static Result syn(){
		ConfigFactory.pushMemory();
		ExtForm extForm = new ExtForm();
		extForm.setSuccess(true);
        return ok(play.libs.Json.toJson(extForm));
	}
	
	@Transactional
	public static Result update(){
		JsonNode json = request().body().asJson();
		String property = json.findPath("property").asText();
		String value = json.findPath("value").asText();
		String introduce = json.findPath("introduce").asText();
		Long id = json.findPath("id").asLong();
        Config config = new Config();
        config = config.findById(id);
        config.property = property;
        config.value = value;
        config.introduce = introduce;
        config.saveOrUpdate();
        return ok(play.libs.Json.toJson(config));
	}
	
	@Transactional
	public static Result delete(){
		JsonNode json = request().body().asJson();
		Long id = json.findPath("id").asLong();
        Config config = new Config();
        config.deleteById(id);
        return ok(play.libs.Json.toJson(config));
	}

}
