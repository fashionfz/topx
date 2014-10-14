package ext.search;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Expert;
import models.Gender;
import models.Group;
import models.Service;
import models.SkillTag;
import models.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import play.Logger;
import play.Logger.ALogger;
import play.libs.F.Promise;
import play.libs.WS.Response;
import utils.Assets;
import utils.RegexUtils;
import utils.WSUtil;
import vo.ServiceListVO;
import vo.expertpage.SPage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Constants;
import common.jackjson.JackJsonUtil;
import ext.config.ConfigFactory;

@SuppressWarnings("unused")
public class STransformer {

	static ALogger logger = Logger.of(STransformer.class);


	private Service service;

	private String fulltext;

	private String inf;
	
	private String gf;
	
	private String country;
	
	private Gender gender;
	
	private Long userId;
	
	private String userName;
	
	private String headUrl;
	
	private String job;
	
	private String cf;

	private Boolean isFilter = false;

	private String perPageParam;

	private String order;

	private String orderType;

	public int getPageIndex() {
		if (StringUtils.isNotBlank(this.perPageParam)) {
			String[] perPageArr = this.perPageParam.split("\\|");
			for (String str : perPageArr) {
				if (!RegexUtils.isNumeric(str))
					return 0;
			}
			int bigPageIndex = (Integer.parseInt(perPageArr[0]) - 1) * Constants.HOME_EXPERT_PER_NUM;
			int allPageIndex = bigPageIndex + (Integer.parseInt(perPageArr[1]) - 1);
			return allPageIndex;
		} else {
			return 0;
		}
	}

	public STransformer() {
		super();
	}

	public STransformer(Service service) {
		super();
		this.service = service;
	}

	public STransformer(String fulltext, String perPageParam, String inf, 
			String cf,String gf, String o, String ot) {
		super();
		this.fulltext = fulltext;
		this.perPageParam = perPageParam;
		if (StringUtils.isNotBlank(inf) 
				|| StringUtils.isNotBlank(cf) 
				|| StringUtils.isNotBlank(gf)
				|| StringUtils.isNotBlank(o))
			this.isFilter = true;
		this.inf = inf;
		this.cf = cf;
		this.gf = gf;
		this.order = o;
		this.orderType = ot;
	}

	public SPage<ServiceListVO> pageFromJson(String json, int pageSize,User me) {
		List<ServiceListVO> elvos = new ArrayList<ServiceListVO>();
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			node = node.path("data");
			int maxCount = node.path("maxCount").asInt();
			int nowPage = node.path("nowPage").asInt();
			if (maxCount > 0) {
				elvos = jsonToExpertList(node.path("results"),me);
			}
			return new SPage<ServiceListVO>(elvos, maxCount, nowPage, pageSize);
		} else {
			logger.error("搜索平台群组搜索异常：" + json);
			return new SPage<ServiceListVO>(elvos, 0, 0, pageSize);
		}
	}

	private List<ServiceListVO> jsonToExpertList(JsonNode node,User me) {
		Iterator<JsonNode> resultsIter = node.iterator();
		List<ServiceListVO> elvos = new ArrayList<ServiceListVO>();
		while (resultsIter.hasNext()) {
			JsonNode jsonNode = resultsIter.next();
			elvos.add(fieldJson(jsonNode.path("fieldList").iterator(),me));
		}
		return elvos;
	}

	private ServiceListVO fieldJson(Iterator<JsonNode> iterContext,User me) {
		ServiceListVO glvo = new ServiceListVO();
		while (iterContext.hasNext()) {
			JsonNode nameNode = iterContext.next();
			String fieldname = nameNode.path("fieldname").asText();
			if (fieldname.equals("id")) {
				glvo.setId(nameNode.path("context").asLong());
			} else if (fieldname.equals("industryId")) {
				glvo.setIndustryId(nameNode.path("context").asLong());
				Map<Long, String> cts = (Map<Long, String>) play.cache.Cache.get(Constants.CACHE_INDUSTRY);
				if (cts == null) {
					cts = SkillTag.getCategoryTagMap();
					play.cache.Cache.set(Constants.CACHE_INDUSTRY, cts);
				}
				glvo.setIndustryName(cts.get(nameNode.path("context").asLong()));
			} else if (fieldname.equals("ownerUserId")) {
				glvo.setOwnerUserId(nameNode.path("context").asLong());
				// 用户头像
				//glvo.setHeadUrl(Assets.at(User.getAvatarFileRelativePath(nameNode.path("context").asLong(),70)));
			} else if (fieldname.equals("ownerUserName")) {
				glvo.setOwnerUserName(nameNode.path("context").asText());
			} else if (fieldname.equals("gender")) {
				if (nameNode.path("context").asInt() == Gender.WOMAN.ordinal())
					glvo.setGender(Gender.WOMAN);
				else
					glvo.setGender(Gender.MAN);
			} else if (fieldname.equals("country")) {
				String country = RegexUtils.replaceFont(nameNode.path("context").asText());
				glvo.setCountry(country);
				glvo.setCountryUrl(Constants.countryPicKV.get(country));
			} else if (fieldname.equals("title")) {
				glvo.setTitle(nameNode.path("context").asText());
			} else if (fieldname.equals("job")) {
				glvo.setJob(nameNode.path("context").asText());
			} else if (fieldname.equals("price")) {
				glvo.setPrice(Constants.dformat.format((nameNode.path("context").asDouble())));
			} else if (fieldname.equals("info")) {
				glvo.setInfo(nameNode.path("context").asText());
			} else if (fieldname.equals("tags")) {
				String skillsTags = nameNode.path("context").asText();
				glvo.setTags(SkillTag.makeSTagVos(skillsTags));
			} else if (fieldname.equals("createDate")) {
				try {
					Date date = Constants.timeStampFormat.parse(nameNode.path("context").asText());
					glvo.setCreateDate(Constants.dateformat.format(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (fieldname.equals("coverUrl")) {
				glvo.setCoverUrl(nameNode.path("context").asText());
			} else if (fieldname.equals("headUrl")) {
				glvo.setHeadUrl(nameNode.path("context").asText());
			} else if (fieldname.equals("commentNum")) {
				glvo.setCommentNum((nameNode.path("context").asLong()));
			} else if (fieldname.equals("ownerUserId")) {
				glvo.setOwnerUserId(nameNode.path("context").asLong());
			} else if (fieldname.equals("ownerUserName")) {
				glvo.setOwnerUserName(nameNode.path("context").asText());
			} else if (fieldname.equals("averageScore")) {
				glvo.setAverageScore(nameNode.path("context").asText());
			}
		}
		
		return glvo;
	}

	public List<NameValuePair> tranInputsNVP() {
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("what", "meservice" + ConfigFactory.getString("search.client.whatsuffix")));
		nvps.add(new BasicNameValuePair("id", String.valueOf(service.id)));
		nvps.add(new BasicNameValuePair("industryId", String.valueOf(service.industryId)));
		nvps.add(new BasicNameValuePair("country", this.country));
		nvps.add(new BasicNameValuePair("title", service.title));
		nvps.add(new BasicNameValuePair("info", service.info));
		nvps.add(new BasicNameValuePair("gender", Integer.toString(this.gender.ordinal())));
		nvps.add(new BasicNameValuePair("tags", service.tags));
		nvps.add(new BasicNameValuePair("price", String.valueOf(service.price)));
		nvps.add(new BasicNameValuePair("score", String.valueOf(service.score)));
		if (service.commentNum > 0)
			nvps.add(new BasicNameValuePair("averageScore", String.valueOf(service.score/service.commentNum)));
		else
			nvps.add(new BasicNameValuePair("averageScore", "0"));
		nvps.add(new BasicNameValuePair("coverUrl", service.coverUrl == null ? "" : service.coverUrl));
		nvps.add(new BasicNameValuePair("commentNum", String.valueOf(service.commentNum)));
		nvps.add(new BasicNameValuePair("ownerUserId", String.valueOf(this.userId)));
		nvps.add(new BasicNameValuePair("ownerUserName", this.userName));
		nvps.add(new BasicNameValuePair("headUrl", this.headUrl));
		nvps.add(new BasicNameValuePair("job", this.job));
		nvps.add(new BasicNameValuePair("createDate", service.createDate == null ? "" : Constants.timeStampFormat.format(service.createDate)));
		return nvps;
	}

	public static void tranDeleteNVP(Long id) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("what", "meservice" + ConfigFactory.getString("search.client.whatsuffix"));
		paramMap.put("indexFieldValue", String.valueOf(id));
		String body = WSUtil.postFormURLEncoded(ConfigFactory.getString("search.client.url") + "/index/deleteDocument", paramMap).get(30000).getBody();
		logger.debug("paramMap:"+ paramMap + "accessTokenResponse:" + body);
	}

	public List<NameValuePair> tranAdSearchNVP(Integer pageSize) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("what", "meservice" + ConfigFactory.getString("search.client.whatsuffix")));
		StringBuffer condition = new StringBuffer();
		if (StringUtils.isNotBlank(this.fulltext)) {
			String regex = "[\\+,\\-,\\&,\\|,\\!,\\(,\\),\\{,\\},\\[,\\],\\^,\",\\~,\\*,\\?,\\:]";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(this.fulltext);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String name = matcher.group(0);
				matcher.appendReplacement(sb, "\\\\" + name);
			}
			matcher.appendTail(sb);
			String words = sb.toString().replaceAll("\\s+", "+");
			condition.append("(title:" + words + " OR ");
			condition.append("info:" + words + " OR ");
			condition.append("tags:" + words + ") ");
		}

		if (StringUtils.isNotBlank(this.fulltext) && StringUtils.isNotBlank(this.inf)) {
			condition.append(" AND industryId:" + this.inf);
		} else if (StringUtils.isBlank(this.fulltext) && StringUtils.isNotBlank(this.inf)) {
			condition.append("industryId:" + this.inf);
		}
		if ((StringUtils.isNotBlank(this.inf) || StringUtils.isNotBlank(this.fulltext)) 
				&& StringUtils.isNotBlank(this.cf)) {
			condition.append(" AND country:" + this.cf);
		} else if (StringUtils.isBlank(this.fulltext) && StringUtils.isNotBlank(this.cf)) {
			condition.append("groupPrivId:" + this.cf);
		}
		
		if (((StringUtils.isNotBlank(this.inf) 
				|| StringUtils.isNotBlank(this.fulltext) 
				|| StringUtils.isNotBlank(this.cf)) 
				&& StringUtils.isNotBlank(this.gf))) {
			condition.append(" AND gender:" + this.gf);
		} else if (StringUtils.isBlank(this.fulltext) && StringUtils.isNotBlank(this.gf)) {
			condition.append("gender:" + this.gf);
		}
		String condStr = condition.toString();
		nvps.add(new BasicNameValuePair("conditions", condStr));
		if (StringUtils.isNotBlank(this.order) && StringUtils.isNotBlank(this.orderType))
			nvps.add(new BasicNameValuePair("orderConditions", "[{\"fieldname\":\"" + this.order + "\",\"ordertype\":\"" + this.orderType
					+ "\"}]"));
		nvps.add(new BasicNameValuePair("highlighting", "{'hl.simple.pre':'<span class=\\'red\\'>', 'hl.simple.post':'</span>','hl.fragsize':'500'}"));
		nvps.add(new BasicNameValuePair("pageSize", Integer.toString(pageSize)));
		nvps.add(new BasicNameValuePair("pageNum", Integer.toString(this.getPageIndex())));
		return nvps;
	}

	public Boolean getIsFilter() {
		return isFilter;
	}

	public void setIsFilter(Boolean isFilter) {
		this.isFilter = isFilter;
	}

	/**
	 * @param perPageParam
	 *            the perPageParam to set
	 */
	public void setPerPageParam(String perPageParam) {
		this.perPageParam = perPageParam;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param job the job to set
	 */
	public void setJob(String job) {
		this.job = job;
	}

	/**
	 * @return the headUrl
	 */
	public String getHeadUrl() {
		return headUrl;
	}

	/**
	 * @param headUrl the headUrl to set
	 */
	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}
}
