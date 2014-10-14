package ext.search;

import static play.Play.application;

import java.io.IOException;
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
import models.SkillTag;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import play.Logger;
import play.Logger.ALogger;
import utils.RegexUtils;
import utils.WSUtil;
import vo.ExpertListVO;
import vo.expertpage.EPage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Constants;
import common.jackjson.JackJsonUtil;
import ext.config.ConfigFactory;

@SuppressWarnings("unused")
public class Transformer {
	
	static ALogger logger = Logger.of(Transformer.class);


	private Expert expert;

	private String fulltext;

	private String countryFilter;

	private String expensesFilter;

	private String serveStateFilter;

	private String genderFilter;

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

	public Transformer() {
		super();
	}

	public Transformer(Expert expert) {
		super();
		this.expert = expert;
	}

	public Transformer(String fulltext, String perPageParam, String cf, String ssf, String ef, String gf, String o, String ot) {
		super();
		this.fulltext = fulltext;
		this.perPageParam = perPageParam;
		if (StringUtils.isNotBlank(cf) || StringUtils.isNotBlank(ssf) || StringUtils.isNotBlank(ef) || StringUtils.isNotBlank(gf)
				|| StringUtils.isNotBlank(o))
			this.isFilter = true;
		this.countryFilter = cf;
		this.serveStateFilter = ssf;
		this.expensesFilter = ef;
		this.genderFilter = gf;
		this.order = o;
		this.orderType = ot;
	}

	public EPage<ExpertListVO> pageFromJson(String json, User currUser, int pageSize) {
		List<ExpertListVO> elvos = new ArrayList<ExpertListVO>();
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
			if (maxCount > 0){
				elvos = jsonToExpertList(node.path("results"), currUser);
//				// 更新当前用户收藏信息
//				updateFavoriteOfExpertListVOList(elvos,currUser);
			}
			return new EPage<ExpertListVO>(elvos, maxCount, nowPage, pageSize);
		} else {
			logger.error("搜索平台搜索异常："+json);
			return new EPage<ExpertListVO>(elvos, 0, 0, pageSize);
		}
	}
	
	/**
	 * 更新用户的收藏信息
	 * @param elvos
	 * @param currentUser
	 * @deprecated
	 */
	private void updateFavoriteOfExpertListVOList(List<ExpertListVO> elvos,User currentUser) {
		if (currentUser != null) {
			List<Expert> favoriteExpertList = models.service.FavoriteService.listFavorite(currentUser);
			if (CollectionUtils.isNotEmpty(favoriteExpertList)) {
				for (ExpertListVO vo : elvos) {
					for (int i = 0; i < favoriteExpertList.size(); i++) {
						User user = favoriteExpertList.get(i).user;
						if (vo.getUserId() - user.getId() == 0) {
							vo.setIsFavorite(Boolean.TRUE);
							break;
						}
					}
				}
			}
		}
	}

	private List<ExpertListVO> jsonToExpertList(JsonNode node, User currUser) {
		Iterator<JsonNode> resultsIter = node.iterator();
		List<ExpertListVO> elvos = new ArrayList<ExpertListVO>();
		while (resultsIter.hasNext()) {
			JsonNode jsonNode = resultsIter.next();
			elvos.add(fieldJson(jsonNode.path("fieldList").iterator(), currUser));
		}
		return elvos;
	}

	private ExpertListVO fieldJson(Iterator<JsonNode> iterContext, User currUser) {
		ExpertListVO elvo = new ExpertListVO();
		while (iterContext.hasNext()) {
			JsonNode nameNode = iterContext.next();
			String fieldname = nameNode.path("fieldname").asText();
			if (fieldname.equals("id")) {
				elvo.setId(nameNode.path("context").asLong());
			} else if (fieldname.equals("country")) {
				String country = RegexUtils.replaceFont(nameNode.path("context").asText());
				elvo.setCountry(country);
				elvo.setCountryUrl(Constants.countryPicKV.get(country));
			} else if (fieldname.equals("job")) {
				elvo.setJob(nameNode.path("context").asText());
			} else if (fieldname.equals("personalInfo")) {
				elvo.setPersonalInfo(nameNode.path("context").asText());
			} else if (fieldname.equals("skillsTags")) {
				String skillsTags = nameNode.path("context").asText();
				elvo.setSkillsTags(SkillTag.makeSTagVos(skillsTags));
			} else if (fieldname.equals("userName")) {
				elvo.setUserName(nameNode.path("context").asText());
			} else if (fieldname.equals("userId")) {
				Long userId = nameNode.path("context").asLong();
				elvo.setUserId(userId);
				if (currUser != null && currUser.id.equals(elvo.getUserId()))
					elvo.setIsSelf(true);
			} else if (fieldname.equals("joinDate")) {
				Date date = new Date(nameNode.path("context").asLong());
				elvo.setJoinDate(date);
			} else if (fieldname.equals("gender")) {
				if (nameNode.path("context").asInt() == Gender.WOMAN.ordinal()){
					elvo.setGender(Gender.WOMAN);
				} else {
					elvo.setGender(Gender.MAN);
				}
			} else if (fieldname.equals("headUrl")) {
				elvo.setHeadUrl(nameNode.path("context").asText());
			} else if (fieldname.equals("commentNum")) {
				elvo.setCommentNum((nameNode.path("context").asLong()));
			} else if (fieldname.equals("averageScore")) {
				elvo.setAverageScore((Float.valueOf(nameNode.path("context").asText())));
			} else if (fieldname.equals("payType") && StringUtils.isNotBlank(nameNode.path("context").asText())) {
				if (nameNode.path("context").asText().indexOf(Expert.PayType.NEGOTIABLE.name()) != -1)
					elvo.setPayType(Expert.PayType.NEGOTIABLE);
				else if (nameNode.path("context").asText().indexOf(Expert.PayType.TIMEBILL.name()) != -1)
					elvo.setPayType(Expert.PayType.TIMEBILL);
			}
		}
		return elvo;
	}

	public List<NameValuePair> tranInputsNVP() {
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("what", "me" + ConfigFactory.getString("search.client.whatsuffix")));
		nvps.add(new BasicNameValuePair("id", String.valueOf(expert.id)));
		nvps.add(new BasicNameValuePair("userId", String.valueOf(expert.userId == null ? "" : expert.userId)));
		nvps.add(new BasicNameValuePair("country", expert.country == null ? "" : expert.country));
		nvps.add(new BasicNameValuePair("educationExp", expert.combineEducationExp(objectMapper)));
		nvps.add(new BasicNameValuePair("jobExp", expert.combinejobExp(objectMapper)));
		nvps.add(new BasicNameValuePair("job", expert.job == null ? "" : expert.job));
		nvps.add(new BasicNameValuePair("personalInfo", expert.personalInfo == null ? "" : expert.personalInfo));
		nvps.add(new BasicNameValuePair("skillsTags", expert.skillsTags));
		nvps.add(new BasicNameValuePair("userName", expert.userName == null ? "" : expert.userName));
		nvps.add(new BasicNameValuePair("expenses", Float.toString(expert.expenses)));
		if (expert.joinDate != null)
			nvps.add(new BasicNameValuePair("joinDate", expert.joinDate == null ? "" : Constants.timeStampFormat.format(expert.joinDate)));
		nvps.add(new BasicNameValuePair("gender", Integer.toString(expert.getGenderWithDefault().ordinal())));
		nvps.add(new BasicNameValuePair("headUrl", expert.headUrl == null ? "" : expert.headUrl));
		nvps.add(new BasicNameValuePair("commentNum", expert.commentNum == null ? "0" : expert.commentNum.toString()));
		nvps.add(new BasicNameValuePair("averageScore", Float.toString(expert.averageScore == null ? 0.0f : expert.averageScore)));
		nvps.add(new BasicNameValuePair("dealNum", expert.dealNum.toString()));
		nvps.add(new BasicNameValuePair("ipLocal", Constants.IP_LOCAL));
		if (expert.payType != null)
			nvps.add(new BasicNameValuePair("payType", expert.payType.name()));
		else
			nvps.add(new BasicNameValuePair("payType", Expert.PayType.TIMEBILL.name()));
		return nvps;
	}

	public List<NameValuePair> tranMustTagNVP() {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("what", "me" + ConfigFactory.getString("search.client.whatsuffix")));
		String condition = new String("skillsTags:\"\\\"" + this.fulltext + "\\\"\"  ");
		nvps.add(new BasicNameValuePair("conditions", condition));
		nvps.add(new BasicNameValuePair("highlighting", "{'hl.simple.pre':'<span class=\\'red\\'>', 'hl.simple.post':'</span>','hl.fragsize':'500'}"));
		nvps.add(new BasicNameValuePair("pageSize", Integer.toString(10)));
		nvps.add(new BasicNameValuePair("pageNum", Integer.toString(this.getPageIndex())));
		return nvps;
	}

	public static void tranDeleteNVP(Long userId) {
		Expert expert = Expert.getExpertByUserId(userId);
		if (expert != null) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("what", "me" + ConfigFactory.getString("search.client.whatsuffix"));
			paramMap.put("indexFieldValue", expert.id.toString());
			String body = WSUtil.postFormURLEncoded(ConfigFactory.getString("search.client.url") + "/index/deleteDocument", paramMap).get(30000).getBody();
			logger.debug("paramMap:"+ paramMap + "accessTokenResponse:" + body);	
		}
	}

	public List<NameValuePair> tranAdSearchNVP(Integer pageSize) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("what", "me" + ConfigFactory.getString("search.client.whatsuffix")));
		StringBuffer condition = new StringBuffer();
		if (StringUtils.isNotBlank(this.fulltext)) {
			String regex = "[\\+,\\-,\\&,\\|,\\!,\\(,\\),\\{,\\},\\[,\\],\\^,\",\\~,\\*,\\?,\\:]";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(this.fulltext);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				String name = matcher.group(0);// 键名
				matcher.appendReplacement(sb, "\\\\" + name);
			}
			matcher.appendTail(sb);
			String words = sb.toString().replaceAll("\\s+", "+");
			condition.append("(userName:" + words + " OR ");
			condition.append("skillsTags:" + words + " OR ");
			condition.append("job:" + words + " OR ");
			condition.append("personalInfo:" + words + " OR ");
			condition.append("jobExp:" + words + " OR ");
			condition.append("educationExp:" + words + ") ");
			// TODO 需要实现的精确查找
			// condition.append("skillsTags:\"\\\"Java\\\"\"  ");
		}
		if (StringUtils.isNotBlank(this.countryFilter)) {
			condition.append(" AND country:\"" + this.countryFilter + "\"  ");
		}
		if (StringUtils.isNotBlank(this.expensesFilter)) {
			String ef = this.expensesFilter;
			if (ef.equals("0")) {
				condition.append(" AND payType:\"TIMEBILL\"");
			} else if (ef.equals("1")) {
				condition.append(" AND payType:\"NEGOTIABLE\"");
			}
		}
		if (StringUtils.isNotBlank(this.genderFilter)) {
			condition.append(" AND gender:" + this.genderFilter);
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
	 * @param serveStateFilter
	 *            the serveStateFilter to set
	 */
	public void setServeStateFilter(String serveStateFilter) {
		this.serveStateFilter = serveStateFilter;
	}

	/**
	 * @param perPageParam
	 *            the perPageParam to set
	 */
	public void setPerPageParam(String perPageParam) {
		this.perPageParam = perPageParam;
	}
}
