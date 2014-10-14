package models.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import models.Expert;
import models.OverseasResume;
import models.OverseasResume.Status;
import models.User;
import models.User.ResumeStatus;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import play.Logger;
import play.db.jpa.JPA;
import scalax.io.support.FileUtils;
import system.vo.Page;
import system.vo.ResumeGridVO;
import utils.Assets;
import utils.HelomeUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.jackjson.JackJsonUtil;
import controllers.attachment.AttachmentApp;
import controllers.base.ObjectNodeResult;
import controllers.user.UserAvatarApp;
import exception.AvatarException;
import ext.config.ConfigFactory;
import ext.resume.ResumeHttpClient;

/**
 * 提供海外简历相关的服务
 */
public class OverseasResumeService {
	
	public final static String WEBSITE = ConfigFactory.getString("resume.client.clientWebSite");
	/**
	 * 创建或更新OverseasResume
	 * @param json
	 * @return
	 * @throws AvatarException 
	 * @throws IOException 
	 */
	public static ObjectNodeResult createOrUpdateOverseasResume(JsonNode json) {
		ObjectNodeResult result = new ObjectNodeResult();
		
		OverseasResume overseasResume = new OverseasResume();
		// userId
		if (!json.has("userId")) {
			return result.error("Parameter[userId] can't be empty.", "502"); // Parameter[userId] can't be empty.  --- 参数userId不能为空
		}
		if (json.has("userId")) {
			Long userId = json.findPath("userId").asLong();
			if (userId < 0) { // 不能小于0
				return result.error("The value of parameter[userId] must greater than zero.", "503"); // The value of parameter[userId] must greater than zero. --- userId的值必须大于0
			}
			// 判断id段属于国际组的id段。
			if (userId - 1000000 < 0) { // 一百万以上
				return result.error("The value of parameter[userId] must more than 1 million.", "504"); // The value of parameter[userId] must more than 1 million. --- userId的值不属于国际组的id段
			}
			OverseasResume or = OverseasResume.queryOverseasResumeByUserId(userId);
			if (or != null && (or.getStatus() != Status.INVALIDATE || or.getStatus() != Status.TRANSLATED)) { // 找到并且其status不是作废或者已翻译就更新
				overseasResume = or;
			}
			overseasResume.setUserId(userId);
		}
		// email
		if (json.has("email")) {
			String email = json.findPath("email").asText();
			overseasResume.setEmail(email);
		}
		// avatarUrl
		if (json.has("avatarUrl")) {
			String avatarUrl = json.findPath("avatarUrl").asText();
			overseasResume.setAvatarUrl(avatarUrl);
		}
		// source
//		if (json.has("source")) {
//			String source = json.findPath("source").asText();
//			overseasResume.setSource(source);
//		}
		overseasResume.setSource("helome.us");
		// sourceResume
		if (json.has("sourceResume")) {
			String sourceResume = json.findPath("sourceResume").asText();
			overseasResume.setSourceResume(sourceResume);
		}
		// sourceResumeUrl
		if (json.has("sourceResumeUrl")) {
			String sourceResumeUrl = json.findPath("sourceResumeUrl").asText();
			overseasResume.setSourceResumeUrl(sourceResumeUrl);
		}
		
		//未翻译
		overseasResume.setStatus(Status.UNTRANSLATED);
		// 接收时间
		overseasResume.setCreateDate(new Date());
		User user = User.findById(overseasResume.getUserId());
		Expert expert = new Expert();
		if (user == null) {
			// 写数据到tb_user表
			if (Logger.isInfoEnabled()) {
				Logger.info("国际版发布海外简历，写用户表  ---> " + "id:" + overseasResume.getUserId() + ",email:" + overseasResume.getEmail());
			}
			JPA.em().createNativeQuery("insert into tb_user(id,email,userName,isEnable,isComplain) values (:id,:email,:userName,:isEnable,:isComplain)")
				.setParameter("id", overseasResume.getUserId()).setParameter("email", overseasResume.getEmail()).setParameter("userName", overseasResume.getSourceResume()).setParameter("isEnable", 1).setParameter("isComplain", 0)
				.executeUpdate();
			if (Logger.isInfoEnabled()) {
				Logger.info("写数据操作成功。");
			}
		} else {
			if (Logger.isInfoEnabled()) {
				Logger.info("国际版发布海外简历，更新用户表数据  ---> " + "id:" + overseasResume.getUserId() + ",email:" + overseasResume.getEmail());
			}
			user.setEmail(overseasResume.getEmail());
			user.userName = overseasResume.getSourceResume();
			JPA.em().merge(user);
			if (Logger.isInfoEnabled()) {
				Logger.info("更新数据操作成功。");
			}
		}
		
		// 设置Expert的userid为User的id并保存。
		Expert dbExpert = Expert.findByUserId(overseasResume.getUserId());
		if (dbExpert == null) {
			JPA.em().createNativeQuery("insert into tb_expert(dealNum,expenses,userid,userName) values (:dealNum,:expenses,:userid,:userName)")
				.setParameter("dealNum", 0L).setParameter("expenses", 0L).setParameter("userid", overseasResume.getUserId()).setParameter("userName", overseasResume.getSourceResume())
				.executeUpdate();
		}
		
		overseasResume.saveOrUpdate();
		
		// 保存头像
		HttpURLConnection conn = null;
		InputStream in = null;
		FileOutputStream out = null;
		try {
			if (Logger.isInfoEnabled()) {
				Logger.info("国际版发布海外简历，用户头像访问地址： " + overseasResume.getAvatarUrl());
			}
			URL u = new URL(overseasResume.getAvatarUrl());
			conn = (HttpURLConnection) u.openConnection();
			in = conn.getInputStream();
			Long currentTimestamp = System.currentTimeMillis(); // 时间戳，用于解决同名文件上传的情况
			StringBuffer sb = new StringBuffer(AttachmentApp.getUploadPath());
	    	sb.append(File.separator).append("resumeTemp").append(File.separator).append(overseasResume.getId()).append(File.separator).append(currentTimestamp);
	    	File path = new File(sb.toString());
			if (!path.exists()) {
				boolean results = path.mkdirs();
				if (!results) {
					if (Logger.isErrorEnabled()) {
						Logger.error("海外简历任务发布接口 - 服务端图片路径创建失败，请检查权限或配置。保存路径：" + sb.toString());
					}
					return result.error("Server-side image path creation fails, please check the permissions or configuration.The path：" + sb.toString(), "505"); //  --- 服务端图片路径创建失败，请检查权限或配置。保存路径：
				}
			}
			File target = new File(sb.toString() + File.separator + "resumeAvatar" + ".jpg");
			out = new FileOutputStream(target);
			FileUtils.copy(in,out);
			
			// 更新用户头像
			Long userId = overseasResume.getUserId();
			User targetUser = User.findById(userId);
			UserAvatarApp.save(target,result,targetUser);
		} catch (Exception e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("海外简历任务发布接口 - 图片保存失败。传入的json：" + json);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
		
		return result;
	}
	
	/**
	 * 发布任务
	 * @param user 当前用户
	 * @throws UnsupportedEncodingException 
	 */
	public static ObjectNodeResult addTaskForChinese(User user,String remark) throws UnsupportedEncodingException {
		ObjectNodeResult result = new ObjectNodeResult();
		if (remark.contains("<") || remark.contains(">")) {
			result = result.error("特殊要求不能包含 < 或 > 字符。", "-9999");
			return result;
		}
		if(remark.length()>250) {
			result = result.error("特殊要求最大250个字符。", "-9999");
			return result;
		}
		String url = "hex_manage_addTaskForChineseFunction";
		String webSite = "http://www.helome.com/";
		if (StringUtils.isNotBlank(WEBSITE)) {
			webSite = WEBSITE;
		}
		String avatar = getAvatar(190, user);
		if (StringUtils.contains(avatar, "?")) {
			avatar = StringUtils.substring(avatar, 0, StringUtils.indexOf(avatar, "?"));
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("<requests><global><resultType>json</resultType></global>");
		sb.append("<request>");
		sb.append("<funcId>").append(url).append("</funcId>");
		
		sb.append("<provider_id>").append(user.getId()).append("</provider_id>"); // 用户ID
		sb.append("<language>").append("English").append("</language>"); // 需要翻译成的语言，默认为English 可以不传
		sb.append("<resume_name>").append(user.getName()).append("</resume_name>"); // 简历名
		sb.append("<profile_photo>").append(avatar).append("</profile_photo>"); // 头像地址全路径
		sb.append("<provider_url>").append(webSite).append("expert/detail/").append(user.getId()).append("</provider_url>"); // 简历地址全路径
		sb.append("<email>").append(user.getEmail()).append("</email>"); // 登录邮箱
		sb.append("<remark><![CDATA[").append(remark).append("]]></remark>"); // 客户留言（特殊要求）
		
		sb.append("</request>");
		sb.append("</requests>");
		
		if (Logger.isInfoEnabled()) {
			Logger.info("海外简历 - 发布任务，传入的xml ---> " + sb.toString());
		}
		String responseJson = ResumeHttpClient.addTaskForChinese(sb.toString());
		if (Logger.isInfoEnabled()) {
			Logger.info("海外简历 - 发布任务，返回的json ---> " + responseJson);
		}
		parseResultFromAddTaskForChineseJson(result,responseJson,user); //解析结果
		return result;
	}
	
	/**
	 * 用户头像，没有路径返回空串
	 */
	private static String getAvatar(int size,User user) {
        if (!HelomeUtil.isEmpty(user.avatar)) {
            int indexOf = StringUtils.indexOf(user.avatar, "?");
            String ayata = "?t=0";
            if (indexOf > -1) {
                ayata = StringUtils.substring(user.avatar, indexOf);
            }
            return Assets.at(User.getAvatarFileRelativePath(user.getId(), size) + ayata);
        }
        return "";
    }
	
	/**
	 * 修改翻译任务的状态
	 * @param user
	 * @param remark
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static ObjectNodeResult updateTaskForChinese(OverseasResume or,Status status,ObjectNodeResult result) throws UnsupportedEncodingException {
		String url = "hex_manage_updateTaskForChineseFunction";
		StringBuffer sb = new StringBuffer();
		sb.append("<requests><global><resultType>json</resultType></global>");
		sb.append("<request>");
		sb.append("<funcId>").append(url).append("</funcId>");
		
		sb.append("<provider_id>").append(or.getUserId()).append("</provider_id>"); // 用户ID
		sb.append("<language>").append("Chinese").append("</language>"); // 需要翻译成的语言，默认为English 可以不传
		if (status == Status.TRANSLATED) {
			sb.append("<state>").append("2").append("</state>"); // 状态，可以不传，默认为2    状态 1 未翻译   2 已完成  3 处理中  4 废弃
		} else if (status == Status.INVALIDATE) {
			sb.append("<state>").append("4").append("</state>"); // 状态，可以不传，默认为2    状态 1 未翻译   2 已完成  3 处理中  4 废弃
		}
		
		sb.append("</request>");
		sb.append("</requests>");
		if (Logger.isInfoEnabled()) {
			Logger.info("海外简历 - 修改翻译任务的状态，传入的xml ---> " + sb.toString());
		}
		String responseJson = ResumeHttpClient.updateTaskForChinese(sb.toString());
		if (Logger.isInfoEnabled()) {
			Logger.info("海外简历 - 修改翻译任务的状态，返回的json ---> " + responseJson);
		}
		parseResultFromUpdateTaskForChineseJson(result,responseJson,or,status);
		return result;
	}
	
	/**
	 * 对调用发布海外简历的接口的结果进行解析
	 * @param result
	 * @param json
	 */
	public static void parseResultFromAddTaskForChineseJson(ObjectNodeResult result,String json,User user) {
		JsonNode node = null;
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		if (StringUtils.isNotBlank(json)) {
			try {
				node = mapper.readTree(json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (node != null) {
				JsonNode globalNode = node.path("global");
				if (globalNode != null) {
					String flag = globalNode.path("flag").asText();
					if (StringUtils.equals("-1", flag)) {
						node = node.path("responses");
						Iterator<JsonNode> ite = node.elements();
						while (ite.hasNext()) {
							node = ite.next();
							String errorCode = node.path("flag").asText();
							String message = node.path("message").asText();
							result = result.error(message, errorCode);
						}
					} else if (StringUtils.equals("1", flag)) { // 操作成功返回
						// 成功后要做的所有的后续操作...
						if (Logger.isInfoEnabled()) {
							Logger.info("接口调用成功，更新User的resumeStatus的值为PUBLISHED。");
						}
//						user.setIsForbidAddResumeTask(Boolean.TRUE); // 是否禁止发布海外简历 --> 是
//						user.setIsResumePublished(Boolean.TRUE);	 // 是否已发布海外简历  --> 是
						user.setResumeStatus(ResumeStatus.PUBLISHED); // 已发布
						User.merge(user); // 更新信息，刷新缓存
						if (Logger.isInfoEnabled()) {
							Logger.info("更新User的resumeStatus的值成功。");
						}
						result = new ObjectNodeResult();
					}
				}
			} else {
				result = result.error("接口返回的json解析失败。", "-10001");
			}
		} else {
			result = result.error("请求失败，请检查配置。", "-10000");
		}
		
	}
	
	/**
	 * 对调用发布海外简历的接口的结果进行解析
	 * @param result
	 * @param json
	 */
	public static void parseResultFromUpdateTaskForChineseJson(ObjectNodeResult result,String json,OverseasResume or,Status status) {
		JsonNode node = null;
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		if (StringUtils.isNotBlank(json)) {
			try {
				node = mapper.readTree(json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (node != null) {
				JsonNode globalNode = node.path("global");
				if (globalNode != null) {
					String flag = globalNode.path("flag").asText();
					if (StringUtils.equals("-1", flag)) {
						node = node.path("responses");
						Iterator<JsonNode> ite = node.elements();
						while (ite.hasNext()) {
							node = ite.next();
							String errorCode = node.path("flag").asText();
							String message = node.path("message").asText();
							result = result.error(message, errorCode);
						}
					} else if (StringUtils.equals("1", flag)) { // 操作成功返回
						// 成功后要做的所有的后续操作...
						if (Logger.isInfoEnabled()) {
							Logger.info("接口调用成功，更新OverseasResume的status的值为true。");
						}
						or.setStatus(status);
						if (status == Status.TRANSLATED) {
							or.setTranslateDate(new Date());
						}
						or.saveOrUpdate();
						if (Logger.isInfoEnabled()) {
							Logger.info("更新OverseasResume的status的值成功。");
						}
						result = new ObjectNodeResult();
					}
				}
			} else {
				result = result.error("接口返回的json解析失败。", "-10001");
			}
		} else {
			result = result.error("请求失败，请检查配置。或者服务端是否能联通。", "-10000");
		}
		
	}
	
	/**
	 * 根据条件查询ResumeGridVO的Page数据
	 */
	public static Page<ResumeGridVO> queryResumeGridPage(ResumeGridSearchCondition c){
		Integer start = c.start;
		Integer limit = c.limit;
		if(null == start){
			start = 0;
		}
		if(null == limit){
			limit = 20;
		}
		
		StringBuffer queryQL = new StringBuffer("from OverseasResume r where 1=1");
		StringBuffer countQL = new StringBuffer("select count(r.id) from OverseasResume r where 1=1");
		Map<String, Object> paramMap = new HashMap<>();
		if (StringUtils.isNotBlank(c.searchText)) {
			queryQL.append(" and (r.email like :searchTextLike or r.sourceResume like :searchTextLike");
			countQL.append(" and (r.email like :searchTextLike or r.sourceResume like :searchTextLike");
			paramMap.put("searchTextLike", "%" + c.searchText + "%");
			if (NumberUtils.isDigits(c.searchText)) {
				queryQL.append(" or r.userId = :searchTextDigit");
				countQL.append(" or r.userId = :searchTextDigit");
				paramMap.put("searchTextDigit", NumberUtils.toLong(c.searchText));
			}
			queryQL.append(")");
            countQL.append(")");
		}
		if(c.getStatus()!=null){
			queryQL.append(" and r.status = :status");
			countQL.append(" and r.status = :status");
			paramMap.put("status", c.getStatus());
		}
		
		boolean isSort = false;
		if (StringUtils.isNotBlank(c.sortProperty) && null != c.isDesc) {
			isSort = true;
		}
		if (isSort) {
			queryQL.append(" order by " + c.sortProperty + (c.isDesc ? " DESC" : " ASC"));
		} else {
			queryQL.append(" order by r.createDate DESC,r.translateDate DESC");
		}
		Query dataQuery = JPA.em().createQuery(queryQL.toString(),OverseasResume.class);
		Query countQuery = JPA.em().createQuery(countQL.toString(),Long.class);
		
		if (MapUtils.isNotEmpty(paramMap)) {
			for (Map.Entry<String, Object> e : paramMap.entrySet()) {
				dataQuery.setParameter(e.getKey(), e.getValue());
				countQuery.setParameter(e.getKey(), e.getValue());
			}
		}
		List<OverseasResume> resultList = dataQuery.setFirstResult(start).setMaxResults(limit).getResultList();
		List<ResumeGridVO> resumeGridVOList = new ArrayList<ResumeGridVO>();
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (OverseasResume or : resultList) {
				ResumeGridVO vo = new ResumeGridVO();
				vo.convert(or);
				resumeGridVOList.add(vo);
			}
		}
		Long count = (Long) countQuery.getSingleResult();
		Page<ResumeGridVO> page = new Page<ResumeGridVO>();
		page.setTotal(count);
		page.setData(resumeGridVOList);
		
		return page;
	}
	
	
	/**
	 * 修改简历状态
	 * @param id
	 * @param status
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static ObjectNodeResult changResumeStatus(Long id, Status status) throws UnsupportedEncodingException {
		ObjectNodeResult result = new ObjectNodeResult();
		OverseasResume or = OverseasResume.queryById(id);
		if (or == null) {
			result = result.error("该简历已被删除。", "-100012");
			return result;
		}
		result = OverseasResumeService.updateTaskForChinese(or, status, result);
		return result;
	}
	
	public static class ResumeGridSearchCondition {
		private String searchText;
		private Status status; // 状态

		private Integer start;
		private Integer limit;

		private String sortProperty;
		private Boolean isDesc;

		public String getSearchText() {
			return searchText;
		}

		public void setSearchText(String searchText) {
			this.searchText = searchText;
		}

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public Integer getStart() {
			return start;
		}

		public void setStart(Integer start) {
			this.start = start;
		}

		public Integer getLimit() {
			return limit;
		}

		public void setLimit(Integer limit) {
			this.limit = limit;
		}

		public String getSortProperty() {
			return sortProperty;
		}

		public void setSortProperty(String sortProperty) {
			this.sortProperty = sortProperty;
		}

		public Boolean getIsDesc() {
			return isDesc;
		}

		public void setIsDesc(Boolean isDesc) {
			this.isDesc = isDesc;
		}
	}

}
