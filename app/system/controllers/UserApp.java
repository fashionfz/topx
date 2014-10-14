/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013-12-24
 */
package system.controllers;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import models.Expert;
import models.Gender;
import models.SkillTag;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import play.Logger;
import play.Logger.ALogger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import system.models.service.TopService;
import system.models.service.UserService;
import system.models.service.UserService.UserGridSearchCondition;
import system.vo.Page;
import system.vo.UserGridVO;
import system.vo.ext.ExtForm;
import utils.ExcelUtil;
import utils.HelomeUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import common.jackjson.JackJsonUtil;

/**
 * 
 * 
 * @ClassName: UserApp
 * @Description: 用户管理App
 * @date 2013-12-24 上午11:40:23
 * @author ShenTeng
 * 
 */
public class UserApp extends Controller {

    private static final ALogger LOGGER = Logger.of(UserApp.class);
    

    @Transactional(readOnly = true)
    public static Result list() {
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String startDatestr = StringUtils.defaultIfBlank(request().getQueryString("startDate"), null);
    	String endDatestr = StringUtils.defaultIfBlank(request().getQueryString("endDate"), null);
    	Date startDate = null;
    	Date endDate = null;
		try {
			if(startDatestr!=null){
				startDate = df.parse(startDatestr+" 00:00:00");
			}
			if(endDatestr!=null){
				endDate = df.parse(endDatestr+" 23:59:59");
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

        Integer start = NumberUtils.toInt(request().getQueryString("start"), 0);
        String searchText = StringUtils.defaultIfBlank(request().getQueryString("searchText"), null);

        String isEnableStr = defaultIfNotContain(request().getQueryString("isEnable"), new String[] { "0", "1" }, null);
        String isComplainStr = defaultIfNotContain(request().getQueryString("isComplain"), new String[] { "0", "1" },
                null);
        String isOnlineStr = defaultIfNotContain(request().getQueryString("isOnline"), new String[] { "0", "1" }, null);
        String isTopStr = defaultIfNotContain(request().getQueryString("isTop"), new String[] { "0", "1" }, null);
        String genderStr = defaultIfNotContain(request().getQueryString("gender"), new String[] { "0", "1" }, null);
        String country = StringUtils.defaultIfBlank(request().getQueryString("country"), null);
        String inIdStr = StringUtils.defaultIfBlank(request().getQueryString("inId"), null);
        String userTypeStr = defaultIfNotContain(request().getQueryString("userType"), new String[] { "0","1","2","3","4","5","6" }, null);
        
		if (StringUtils.equals(inIdStr, "全部行业")) {
			inIdStr = "-1";
		}
        String sortStr = request().getQueryString("sort");
        String sortProperty = null;
        Boolean isDesc = null;
        if (StringUtils.isNotBlank(sortStr)) {
            try {
                JsonNode sortJsonArray = Json.parse(sortStr);
                if (sortJsonArray.isArray() && null != sortJsonArray.get(0)) {
                    JsonNode sortJsonNode = sortJsonArray.get(0);
                    if (sortJsonNode.hasNonNull("property") && sortJsonNode.hasNonNull("direction")) {
                        String property = sortJsonNode.get("property").asText();
                        if ("tradeNum".equals(property)) {
                            sortProperty = "e.dealNum";
                        } else if ("averageScore".equals(property)) {
                            sortProperty = "e.averageScore";
                        }
                        isDesc = "DESC".equals(sortJsonNode.get("direction").asText());
                    }
                }
            } catch (RuntimeException e) {
                LOGGER.debug("failed to parse JSON. JSON: " + sortStr);
            }
        }

        UserGridSearchCondition c = new UserGridSearchCondition();
        c.setCountry(country);
        c.setGender(null == genderStr ? null : Gender.getByOrdinal(Integer.valueOf(genderStr)));
        c.setIsComplain(BooleanUtils.toBooleanObject(isComplainStr, "1", "0", null));
        c.setIsDesc(isDesc);
        c.setIsEnable(BooleanUtils.toBooleanObject(isEnableStr, "1", "0", null));
        c.setIsOnline(BooleanUtils.toBooleanObject(isOnlineStr, "1", "0", null));
        c.setLimit(20);
        c.setSearchText(searchText);
        c.setSortProperty(sortProperty);
        c.setStart(start);
        c.setIsTop(BooleanUtils.toBooleanObject(isTopStr, "1", "0", null));
        c.setUserType(userTypeStr);
        c.setStartDate(startDate);
        c.setEndDate(endDate);
        
        Long inId = HelomeUtil.toLong(inIdStr, null);
        c.setInId(inId == -1 ? null : inId);
        Page<UserGridVO> page = UserService.queryUserGridPage(c);

        return ok(page.toJson());
    }

    @Transactional(readOnly = true)
    public static Result getCountry() {

        List<String> countryList = UserService.getCountry();

        ObjectNode json = Json.newObject();
        ArrayNode data = json.arrayNode();

        ObjectNode itemAll = Json.newObject();
        itemAll.put("name", "全部国家");
        itemAll.put("value", "");
        data.add(itemAll);

        for (String country : countryList) {
            ObjectNode item = Json.newObject();
            item.put("name", country);
            item.put("value", country);
            data.add(item);
        }

        json.put("data", data);
        return ok(json);
    }

    @Transactional(readOnly = false)
    public static Result saveUserState() {
        DynamicForm requestData = Form.form().bindFromRequest();

        Long userId = NumberUtils.createLong(requestData.get("userId"));
        String isEnableStr = defaultIfNotContain(requestData.get("disableState"), new String[] { "0", "1" }, null);
        String isComplainStr = defaultIfNotContain(requestData.get("complainState"), new String[] { "0", "1" }, null);
        String isOnlineStr = defaultIfNotContain(requestData.get("onlineState"), new String[] { "0", "1" }, null);

        isComplainStr = "0";
        isOnlineStr = "0";
        Boolean isEnable = BooleanUtils.toBooleanObject(isEnableStr, "1", "0", null);
        Boolean isComplain = BooleanUtils.toBooleanObject(isComplainStr, "1", "0", null);
        Boolean isOnline = BooleanUtils.toBooleanObject(isOnlineStr, "1", "0", null);
        String onlineServiceStr = requestData.get("onlineService"); // 嗨啰在线客服
        String onlineTranslationStr = requestData.get("onlineTranslation"); // 嗨啰在线翻译
        
        boolean isOnlineService = Boolean.FALSE;
        boolean isOnlineTranslation = Boolean.FALSE;
		if (StringUtils.equals("1", onlineServiceStr)) {
        	isOnlineService = Boolean.TRUE;
		}
		if (StringUtils.equals("1", onlineTranslationStr)) {
        	isOnlineTranslation = Boolean.TRUE;
        }

        ObjectNode result = Json.newObject();

        if (null == userId || null == isEnable || null == isOnline || null == isComplain) {
            result.put("success", false);
        } else {
			try {
				UserService.saveUserState(userId, isEnable, isComplain, isOnline);
				ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
				Expert expert = Expert.getExpertByUserId(userId);
				List<String> skillsTagsList = new ArrayList<String>();
				try {
					if (expert !=null && StringUtils.isNotEmpty(expert.skillsTags)) {
						if (expert.skillsTags != null) {
							skillsTagsList = objectMapper.readValue(expert.skillsTags, List.class);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					skillsTagsList = new ArrayList<String>();
				}
				String ss1 = "嗨啰在线客服";
				if(!isOnlineService) {
					if (expert != null && expert.skillsTags != null && expert.skillsTags.contains("\"" + ss1 + "\"")) {
						Iterator<String> ite = skillsTagsList.iterator();
						while (ite.hasNext()) {
							String item = ite.next();
							if (StringUtils.equals(item, ss1)) {
								ite.remove();
							}
						}
					}
				} else {
					if (expert != null && expert.skillsTags != null && !expert.skillsTags.contains("\"" + ss1 + "\"")) {
						skillsTagsList.add(ss1);
					} else if(expert != null && expert.skillsTags == null){
						skillsTagsList.add(ss1);
					}
				}
				String ss2 = "嗨啰在线翻译";
				if(!isOnlineTranslation) {
					if (expert != null && expert.skillsTags != null && expert.skillsTags.contains("\"" + ss2 + "\"")) {
						Iterator<String> ite = skillsTagsList.iterator();
						while (ite.hasNext()) {
							String item = ite.next();
							if (StringUtils.equals(item, ss2)) {
								ite.remove();
							}
						}
					}
				} else {
					if(expert != null && expert.skillsTags != null && !expert.skillsTags.contains("\"" + ss2 + "\"")) {
						skillsTagsList.add(ss2);
					} else if(expert != null && expert.skillsTags == null){
						skillsTagsList.add(ss2);
					}
				}
				expert.skillsTags = objectMapper.writeValueAsString(skillsTagsList);
		        if (isEnable == true) {
		        	expert.saveOrUpate();
		        }
				result.put("success", true);
			} catch (Exception e) {
				ExtForm extForm = new ExtForm();
				extForm.setSuccess(false);
				extForm.setMsg("保存失败。"+e.getMessage());
				if(Logger.isErrorEnabled()){
					Logger.error("后台编辑用户账号信息保存出错。", e);
				}
				return ok(play.libs.Json.toJson(extForm));
			}
        }

        return ok(result);
    }

    @Transactional(readOnly = false)
    public static Result top() {
    	DynamicForm requestData = Form.form().bindFromRequest();

        Long userId = NumberUtils.createLong(requestData.get("userId"));
        Long industryId = NumberUtils.createLong(requestData.get("industryId"));
    	

        ObjectNode result = Json.newObject();

		if (null == userId || null == industryId) {
            result.put("success", false);
        } else {
        	Expert expert = Expert.findByUserId(userId);
            if (null == expert) {
                result.put("success", false);
                result.put("errorCode", 1);
                result.put("errorMsg", "用户没有设置个人信息，不能置顶。");
            } else {
				if (expert.isTop != null && expert.isTop && expert.topIndustry != null) {
            		result.put("success", false);
	                result.put("errorCode", 1);
	                result.put("errorMsg", "该用户已经置顶过！");
	                return ok(result);
            	}
            	
            	List<SkillTag> skillTagList = SkillTag.listCategoriesOfExpert(userId);
				if (CollectionUtils.isEmpty(skillTagList)) {
					result.put("success", false);
	                result.put("errorCode", 1);
	                result.put("errorMsg", "该用户还没有选择任何行业，置顶失败。");
	                return ok(result);
            	} 
				for (SkillTag st : skillTagList) {
					Long skillTagId = st.id;
					if (skillTagId-industryId==0) {
						TopService.topExpert(userId,industryId);
//		                Cache.remove(Constants.CACHE_EXPERT_TOPS); // 清除缓存
		                result.put("success", true);
		                
		                break;
					}
				}
				if (result.get("success") == null) {
					result.put("success", false);
	                result.put("errorCode", 1);
	                result.put("errorMsg", "该用户还没有选择该行业，置顶失败。");
				}
            }
        }
        return ok(result);
    }

    @Transactional(readOnly = false)
    public static Result untop() {
        DynamicForm requestData = Form.form().bindFromRequest();
        Long userId = NumberUtils.createLong(requestData.get("userId"));

        ObjectNode result = Json.newObject();

        if (null == userId) {
            result.put("success", false);
        } else {
            TopService.unTopExpert(userId);
//            Cache.remove(Constants.CACHE_EXPERT_TOPS); // 清除缓存
            result.put("success", true);
        }
        return ok(result);
    }

    private static String defaultIfNotContain(String src, String[] contain, String defaultStr) {
        return ArrayUtils.contains(contain, src) ? src : defaultStr;
    }
    
    /**
     * 用户所在的行业
     * @param userId 用户id
     * @return
     */
    @Transactional(readOnly = true)
	public static Result queryUserInd(Long userId) {
		List<SkillTag> tagList = new ArrayList<SkillTag>();
		
		List<SkillTag> tags = SkillTag.listCategoriesOfExpert(userId);
		for (SkillTag skillTag : tags) {
			skillTag.industry = null;
		}
		tagList.addAll(0, tags);
		
		return ok(play.libs.Json.toJson(tagList));
	}
    /**
     * 用户信息导出excel
      * downloadExcel
     */
    @Transactional
	public static Result downloadExcel(String searchText,String isEnable,
			String gender,String country,String isTop,String inId,
			String userType,String startDate,String endDate) {
    	
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date start = null;
    	Date end = null;
		try {
			if(startDate!=null && !"null".equals(startDate)){
				start = df.parse(startDate+" 00:00:00");
			}
			if(endDate!=null && !"null".equals(endDate)){
				end = df.parse(endDate+" 23:59:59");
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
        
		if (StringUtils.equals(inId, "全部行业")) {
			inId = "-1";
		}
        UserGridSearchCondition c = new UserGridSearchCondition();
        c.setCountry(country);
        c.setGender("".equals(gender) ? null : Gender.getByOrdinal(Integer.valueOf(gender)));
        c.setIsComplain(null);
        c.setIsDesc(null);
        c.setIsEnable(BooleanUtils.toBooleanObject("".equals(isEnable) ? null : isEnable, "1", "0", null));
        c.setIsOnline(null);
        c.setLimit(20);
        c.setSearchText(searchText);
        c.setSortProperty(null);
        c.setIsTop(BooleanUtils.toBooleanObject("".equals(isTop) ? null : isTop, "1", "0", null));
        c.setUserType(userType);
        c.setStartDate(start);
        c.setEndDate(end);
        
        Long inIds = HelomeUtil.toLong(inId, null);
        c.setInId(inIds == -1 ? null : inIds);
        List<UserGridVO> list = UserService.queryUserGridExcel(c);
    	
    	
    	
    	try{
    		Long time = System.currentTimeMillis();
    		play.mvc.Http.Response response = response();
    		response.setContentType("application/x-msdownload;");  
            response.setHeader("Content-disposition", "attachment; filename="  
                     +  java.net.URLEncoder.encode("用户信息_"+time+".xls", "UTF-8"));
	    	ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    	ExcelUtil.export(UserGridVO.class, list,os);
	    	byte[] excel = os.toByteArray();
	    	os.close();
	    	response.setHeader("Content-Length", String.valueOf(excel.length)); 
	    	return ok(excel);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return ok();
	}

}
