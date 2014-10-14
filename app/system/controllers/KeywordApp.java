/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年1月6日
 */
package system.controllers;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import play.cache.Cache;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import system.models.Keyword;
import system.vo.KeyWordVO;
import system.vo.ext.ExtForm;
import utils.ExcelUtil;

import com.fasterxml.jackson.databind.JsonNode;
import common.Constants;

import ext.paycenter.PCClient;
import ext.paycenter.PayCenterException;

/**
 * @ClassName: KeywordApp
 * @Description: 
 * @date 2014年1月6日 下午1:39:05
 * @author RenYouchao
 * 
 */
public class KeywordApp extends Controller{
	
	@Transactional
	public static Result update() {
		Cache.remove(Constants.CACHE_KEYWORDS);
		DynamicForm requestData = Form.form().bindFromRequest();
		String id = requestData.get("id");
		String words = requestData.get("words");
		List<Keyword> ks = JPA.em().createQuery("from Keyword k").getResultList();
		Keyword kw = null;
		if(CollectionUtils.isNotEmpty(ks)){
		    kw = ks.get(0);
			kw.words = words;
			JPA.em().merge(kw);
		}else{
			kw = new Keyword(null,words);
			JPA.em().persist(kw);
		}
		ExtForm extForm = new ExtForm();
		extForm.setSuccess(true);
		extForm.setMsg("updated");
		extForm.setData(kw);
		return ok(play.libs.Json.toJson(extForm));
	}
	
	
	
	@Transactional(readOnly = true)
	public static Result get() {
		@SuppressWarnings("unchecked")
		List<Keyword> ks = JPA.em().createQuery("from Keyword k").getResultList();
		if(CollectionUtils.isNotEmpty(ks)){
		    Keyword kw = ks.get(0);
		    ExtForm extForm = new ExtForm();
			extForm.setSuccess(true);
			extForm.setMsg("get");
			extForm.setData(kw);
			return ok(play.libs.Json.toJson(extForm));
		}else{
			ExtForm extForm = new ExtForm();
			extForm.setSuccess(true);
			extForm.setMsg("get");
			return ok(play.libs.Json.toJson(extForm));
		}
	}
	
	/**
	 * 
	 * @Title: keyword 
	 * @Description: 关键字统计查询
	 * @return
	 */
	public static Result keyword(){
		String start = request().getQueryString("start");
		String type = request().getQueryString("type");
		String startDate = request().getQueryString("startDate");
		String endDate = request().getQueryString("endDate");
		String searchText = request().getQueryString("searchText");
		String result = getKeyWordCount(searchText,type,startDate,endDate,start);
		return ok(result);
	}
	/**
	 * 
	 * @Title: getKeyWordCount 
	 * @Description: 关键字统计查询接口
	 * @param searchText 关键字
	 * @param type 日期类型
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @param start 当前页
	 * @return
	 */
    private static String getKeyWordCount(String searchText,String type,String startDate,String endDate,String start) {
        String result = null;
        try {
            result = PCClient.postKeyWordCountBySystem("/count","searchText",searchText,"type",type,"startDate",startDate,"endDate",endDate,"start",start);
        } catch (PayCenterException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 
     * @Title: keyWordExcel 
     * @Description: 搜索关键字统计excel导出
     * @param type 统计日期类型
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @param searchText 关键字
     * @return
     */
    public static Result keyWordExcel(String type,String startDate,String endDate,String searchText){
    	String result = getKeyWordCount(searchText,type,startDate,endDate,"-1");
    	JsonNode json = play.libs.Json.parse(result);
    	json=json.findPath("data");
    	Iterator<JsonNode> iterator = json.elements();
    	List<KeyWordVO> list = new ArrayList<KeyWordVO>();
    	while(iterator.hasNext()){
    		json = iterator.next();
    		KeyWordVO vo = new KeyWordVO();
    		vo.setWord(json.findPath("word").textValue());
    		vo.setSearchNum(json.findPath("searchNum").asInt());
    		list.add(vo);
    	}
    	try{
    		Long time = System.currentTimeMillis();
    		play.mvc.Http.Response response = response();
    		response.setContentType("application/x-msdownload;");  
            response.setHeader("Content-disposition", "attachment; filename="  
                     +  java.net.URLEncoder.encode("关键字统计_"+getExcelName(type,startDate,endDate)+".xls", "UTF-8"));
	    	ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    	ExcelUtil.export(KeyWordVO.class, list,os);
	    	byte[] excel = os.toByteArray();
	    	os.close();
	    	response.setHeader("Content-Length", String.valueOf(excel.length)); 
	    	return ok(excel);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return ok();
    }
    
    public static Result getConfig(){
        String result = null;
        try {
            result = PCClient.getKeyWordCountBySystem("/get_config");
        } catch (PayCenterException e) {
            e.printStackTrace();
        }
        return ok(play.libs.Json.parse(result));
    }
    
    public static Result saveKeyWordConfig(){
		DynamicForm requestData = Form.form().bindFromRequest();
		String id = requestData.get("id");
		String value = requestData.get("value");
        String result = null;
        try {
            result = PCClient.getKeyWordCountBySystem("/save_config","id",id,"value",value);
        } catch (PayCenterException e) {
            e.printStackTrace();
        }
        return ok(play.libs.Json.parse(result));
    }
    
    public static String getExcelName(String type,String startDate,String endDate){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	if("".equals(type)){
    		cal.add(Calendar.DAY_OF_MONTH, -1);
    		return df.format(cal.getTime());
    	}
    	else if("0".equals(type)){
    		cal.add(Calendar.WEEK_OF_YEAR, -1);
    		return cal.get(Calendar.YEAR)+"年-"+cal.get(Calendar.WEEK_OF_YEAR)+"周";
    	}
    	else if("1".equals(type)){
    		return cal.get(Calendar.YEAR)+"年-"+cal.get(Calendar.MONTH)+"月";
    	}
    	else if("2".equals(type)){
    		return startDate+"-"+endDate;
    	}
    	return "";
    }
}
