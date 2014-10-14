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

import models.Group;
import models.SkillTag;
import models.User;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import play.Logger;
import play.Logger.ALogger;
import utils.RegexUtils;
import utils.WSUtil;
import vo.GroupListVO;
import vo.expertpage.GPage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.Constants;
import common.jackjson.JackJsonUtil;
import ext.config.ConfigFactory;

@SuppressWarnings("unused")
public class GTransformer {

	static ALogger logger = Logger.of(GTransformer.class);

	private Group group;

	private String fulltext;

	private String gpf;

	private String inf;

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

	public GTransformer() {
		super();
	}

	public GTransformer(Group group) {
		super();
		this.group = group;
	}

	public GTransformer(String fulltext, String perPageParam, String inf, String gpf, String o, String ot) {
		super();
		this.fulltext = fulltext;
		this.perPageParam = perPageParam;
		if (StringUtils.isNotBlank(inf) || StringUtils.isNotBlank(gpf) || StringUtils.isNotBlank(o))
			this.isFilter = true;
		this.inf = inf;
		this.gpf = gpf;
		this.order = o;
		this.orderType = ot;
	}

	public GPage<GroupListVO> pageFromJson(String json, int pageSize,User me) {
		List<GroupListVO> elvos = new ArrayList<GroupListVO>();
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
			return new GPage<GroupListVO>(elvos, maxCount, nowPage, pageSize);
		} else {
			logger.error("搜索平台群组搜索异常：" + json);
			return new GPage<GroupListVO>(elvos, 0, 0, pageSize);
		}
	}

	private List<GroupListVO> jsonToExpertList(JsonNode node,User me) {
		Iterator<JsonNode> resultsIter = node.iterator();
		List<GroupListVO> elvos = new ArrayList<GroupListVO>();
		while (resultsIter.hasNext()) {
			JsonNode jsonNode = resultsIter.next();
			elvos.add(fieldJson(jsonNode.path("fieldList").iterator(),me));
		}
		return elvos;
	}

	private GroupListVO fieldJson(Iterator<JsonNode> iterContext,User me) {
		GroupListVO glvo = new GroupListVO();
		while (iterContext.hasNext()) {
			JsonNode nameNode = iterContext.next();
			String fieldname = nameNode.path("fieldname").asText();
			if (fieldname.equals("id")) {
				glvo.setId(nameNode.path("context").asLong());
				if (me != null){
					List<Long> joins = Group.queryJoinedGroupIdByUserId(me.id);
					if(joins.contains(glvo.getId()))
						glvo.setIsJoin(true);
				}
			} else if (fieldname.equals("industryId")) {
				glvo.setIndustryId(nameNode.path("context").asLong());
				Map<Long, String> cts = (Map<Long, String>) play.cache.Cache.get(Constants.CACHE_INDUSTRY);
				if (cts == null) {
					cts = SkillTag.getCategoryTagMap();
					play.cache.Cache.set(Constants.CACHE_INDUSTRY, cts);
				}
				glvo.setIndustryName(cts.get(nameNode.path("context").asLong()));
			} else if (fieldname.equals("groupPrivId")) {
				glvo.setGroupPrivId(nameNode.path("context").asLong());
			} else if (fieldname.equals("groupName")) {
				glvo.setGroupName(nameNode.path("context").asText());
			} else if (fieldname.equals("groupInfo")) {
				glvo.setGroupInfo(nameNode.path("context").asText());
			} else if (fieldname.equals("tags")) {
				String skillsTags = nameNode.path("context").asText();
				glvo.setTags(SkillTag.makeSTagVos(skillsTags));
			} else if (fieldname.equals("createDate")) {
				Date date = null;
				try {
					date = Constants.timeStampFormat.parse(nameNode.path("context").asText());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				glvo.setCreateDate(Constants.dateformat.format(date));
			} else if (fieldname.equals("headUrl")) {
				glvo.setHeadUrl(nameNode.path("context").asText());
			} else if (fieldname.equals("countMem")) {
				glvo.setCountMem((nameNode.path("context").asLong()));
			} else if (fieldname.equals("ownerId")) {
				glvo.setOwnerId(nameNode.path("context").asLong());
			} else if (fieldname.equals("ownerName")) {
				glvo.setOwnerName(nameNode.path("context").asText());
			}
		}
		return glvo;
	}

	public List<NameValuePair> tranInputsNVP() {
		ObjectMapper objectMapper = JackJsonUtil.getMapperInstance(false);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("what", "megroup" + ConfigFactory.getString("search.client.whatsuffix")));
		nvps.add(new BasicNameValuePair("id", String.valueOf(group.id)));
		nvps.add(new BasicNameValuePair("industryId", String.valueOf(group.industryId)));
		nvps.add(new BasicNameValuePair("groupPrivId", String.valueOf(group.groupPriv.ordinal())));
		nvps.add(new BasicNameValuePair("groupName", group.groupName));
		nvps.add(new BasicNameValuePair("groupInfo", group.groupInfo));
		nvps.add(new BasicNameValuePair("tags", group.tags));
		nvps.add(new BasicNameValuePair("headUrl", group.headUrl == null ? "" : group.headUrl));
		nvps.add(new BasicNameValuePair("countMem", String.valueOf(group.countMem)));
		nvps.add(new BasicNameValuePair("ownerId", String.valueOf(group.owner.id)));
		nvps.add(new BasicNameValuePair("ownerName", group.owner.userName));
		nvps.add(new BasicNameValuePair("createDate", group.createDate == null ? "" : Constants.timeStampFormat.format(group.createDate)));
		return nvps;
	}

	public static void tranDeleteNVP(Long id) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("what", "megroup" + ConfigFactory.getString("search.client.whatsuffix"));
		paramMap.put("indexFieldValue", String.valueOf(id));
		String body = WSUtil.postFormURLEncoded(ConfigFactory.getString("search.client.url") + "/index/deleteDocument", paramMap).get(30000).getBody();
		logger.debug("paramMap:"+ paramMap + "accessTokenResponse:" + body);
	}

	public List<NameValuePair> tranAdSearchNVP(Integer pageSize) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("what", "megroup" + ConfigFactory.getString("search.client.whatsuffix")));
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
			condition.append("(groupName:" + words + " OR ");
			condition.append("groupInfo:" + words + " OR ");
			condition.append("tags:" + words + ") ");
		}

		if (StringUtils.isNotBlank(this.fulltext) && StringUtils.isNotBlank(this.inf)) {
			condition.append(" AND industryId:" + this.inf);
		} else if (StringUtils.isBlank(this.fulltext) && StringUtils.isNotBlank(this.inf)) {
			condition.append("industryId:" + this.inf);
		}
		if ((StringUtils.isNotBlank(this.inf) || StringUtils.isNotBlank(this.fulltext)) && StringUtils.isNotBlank(this.gpf)) {
			condition.append(" AND groupPrivId:" + this.gpf);
		} else if (StringUtils.isBlank(this.fulltext) && StringUtils.isNotBlank(this.gpf)) {
			condition.append("groupPrivId:" + this.gpf);
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
}
