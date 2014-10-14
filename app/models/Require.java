package models;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.hibernate.annotations.Where;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import vo.RequireListVO;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;

import common.Constants;
import controllers.base.ObjectNodeResult;
import ext.search.RTransformer;
import ext.search.SearchHttpClient;

/**
 * @ClassName: Service
 * @Description: 需求实体类
 */
@Entity
@Table(name = "tb_require")
public class Require {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/** 需求创建人 **/
	@Column(name = "ownerId", insertable = false, updatable = false)
	public Long ownerId;
	/** 需求创建人 **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	public User owner;
	@Column(name = "industryId", insertable = false, updatable = false)
	public Long industryId;
	/** 服务行业 **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "industryId")
	public SkillTag industry;
	/** 标题 **/
	@Column(length = 4000)
	public String title;
	/** 说明 **/
	@Column(length = 4000)
	public String info;
	/** 需求附件 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "attach_id")
	@OrderBy("seq ASC, id DESC")
    @Where(clause = "attachType='require'")
	public Set<AttachOfRequire> caseAttachs = new HashSet<AttachOfRequire>();
	/** 预算 **/
	public Double budget;
	/** 标签 **/
	@Column(length = 4000)
	public String tags;
	/** 发布日期 **/
	public Date createDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public SkillTag getIndustry() {
		return industry;
	}

	public void setIndustry(SkillTag industry) {
		this.industry = industry;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Set<AttachOfRequire> getCaseAttachs() {
		return caseAttachs;
	}

	public void setCaseAttachs(Set<AttachOfRequire> caseAttachs) {
		this.caseAttachs = caseAttachs;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * 新增需求
	 */
	public static Require saveRequire(Require require) {
		JPA.em().persist(require);
		return require;
	}

	/**
	 * 新增需求
	 */
	public Require saveRequire() {
		JPA.em().persist(this);
		return this;
	}

	public static ObjectNodeResult saveOrUpdateByJson(User currentUser, JsonNode json) {
		JPA.em().clear();
		ObjectNodeResult result = new ObjectNodeResult();
		// String[] notUseStrs = { "helome", "嗨啰" };

		Require require = new Require();
		Require dbRequire = null; // 从数据库查询出来的Require对象
		// id
		if (json.has("id")) {
			Long id = json.findPath("id").asLong();
			if (id != null) {
				dbRequire = Require.queryRequireById(id);
				if (dbRequire != null) {
					if (dbRequire.getOwner() != null && dbRequire.getOwner().getId() - currentUser.getId() != 0) {
						return result.error("你不是该需求的创建者，修改失败", "-301");
					}

					require = dbRequire;
				}
			}
		}
		// title
		if (json.has("title")) {
			String title = json.findPath("title").asText();
			if (StringUtils.isBlank(title)) {
				return result.error("需求标题不能为空", "700001");
			}
			// for (String item : notUseStrs) {
			// if (StringUtils.contains(title, item)) {
			// return result.error("需求标题不能包含'" + item +"'字符", "700002");
			// }
			// }
			if (StringUtils.isNotBlank(title)) {
				title = common.SensitiveWordsFilter.doFilter(title.trim());
				title = common.ReplaceWordsFilter.doFilter(title);
			}
			require.setTitle(title);
		}
		// info
		if (json.hasNonNull("info")) {
			String info = json.findPath("info").asText();
			if (info.length() > 500) {
				return result.error("需求说明最多500个字符", "700003");
			}
			info = common.SensitiveWordsFilter.doFilter(info.trim());
			info = common.ReplaceWordsFilter.doFilter(info);
			require.setInfo(info);
		}
		// budget
		if (json.has("budget")) {
			DecimalFormat df = new DecimalFormat("###.00");
			Double budget = null;
			String budgetStr = json.findPath("budget").asText();
			if (StringUtils.isNotBlank(budgetStr)) {
				try {
					budget = Double.parseDouble(df.format(json.findPath("budget").asDouble()));
				} catch (Exception e) {
					if (Logger.isErrorEnabled()) {
						Logger.error("预算的值转换异常。budget：" + budget, e);
					}
				}
			}
			require.setBudget(budget);
		}
		// industry
		if (json.has("industry")) {
			Long industryId = json.findPath("industry").asLong();
			SkillTag sk = SkillTag.getTagById(industryId);
			require.setIndustry(sk);
			if (sk != null) {
				require.setIndustryId(sk.getId());
			}
		}
		// tags
		if (json.has("tags") && json.get("tags").isArray()) {
			String tags = json.findPath("tags").toString();
			if (StringUtils.isNotBlank(tags)) {
				tags = common.SensitiveWordsFilter.doFilter(tags);
				tags = common.ReplaceWordsFilter.doFilter(tags);
			}
			require.setTags(tags);
		}
		// caseAttachs
		if (json.has("attachs")) {
			Iterator<JsonNode> attachs = json.findPath("attachs").elements();
			List<Long> attachIdList = new ArrayList<Long>();
			while (attachs.hasNext()) {
				Long attachId = attachs.next().findValue("attachId").asLong();
				attachIdList.add(attachId);
			}
			if (CollectionUtils.isNotEmpty(attachIdList)) {
				require.getCaseAttachs().clear();
				List<AttachOfRequire> attachList = new AttachOfRequire().queryAttachListByIdsAndCreateUserId(attachIdList,
						currentUser.getId());
				if (Logger.isInfoEnabled()) {
					Logger.info("require的id：" + require.getId());
				}
				// 设置seq
				if (CollectionUtils.isNotEmpty(attachList)) {
					for (int i = 0; i < attachList.size(); ++i) {
						attachList.get(i).seq = (long) i;
						if (Logger.isInfoEnabled()) {
							Logger.info("attach的id：" + attachList.get(i).id);
						}
					}
				}

				Set<AttachOfRequire> attachSet = new HashSet<AttachOfRequire>(attachList);
				require.setCaseAttachs(attachSet);
			}
		}

		if (require.getId() == null || dbRequire == null) {
			// 需求创建人
			require.setOwner(currentUser);
			require.setOwnerId(currentUser.id);
			// 需求发布日期
			require.setCreateDate(new Date());
		}

		require.saveOrUpdate();

		result.put("requireId", require.getId());
		return result;
	}

	/**
	 * 保存或更新当前对象
	 */
	public void saveOrUpdate() {
		if (id != null) {
			JPA.em().merge(this);
		} else {
			JPA.em().persist(this);
		}
		final Require thiz = this;
		final Expert expert = Expert.getExpertByUserId(this.ownerId);
		Akka.system().scheduler().scheduleOnce(Duration.create(100, TimeUnit.MILLISECONDS), new Runnable() {
			public void run() {
				RTransformer tf = new RTransformer(thiz);
				tf.setCountry(expert.country);
				tf.setGender(expert.getGenderWithDefault());
				tf.setUserId(expert.userId);
				if (StringUtils.isNotBlank(expert.headUrl))
					tf.setHeadUrl(User.getAvatarFileRelativePath(expert.userId, 70));
				else
					tf.setHeadUrl("");
				tf.setUserName(expert.userName);
				tf.setJob(expert.job);
				SearchHttpClient.createOrUpdateDocument(tf.tranInputsNVP());
			}
		}, Akka.system().dispatcher());
	}

	/**
	 * 根据id查询
	 */
	public static Require queryRequireById(Long requireId) {
		List<Require> requireList = JPA.em().createQuery("from Require where id = :id", Require.class).setParameter("id", requireId)
				.getResultList();
		if (CollectionUtils.isNotEmpty(requireList)) {
			Require require = requireList.get(0);
			return require;
		}
		return null;
	}

	/**
	 * 根据id删除
	 */
	public static void deleteById(final Long requireId) {
		JPA.em().createQuery("delete from Require where id = :id").setParameter("id", requireId).executeUpdate();
		Akka.system().scheduler().scheduleOnce(Duration.create(100, TimeUnit.MILLISECONDS), new Runnable() {
			public void run() {
				RTransformer.tranDeleteNVP(requireId);
				;
			}
		}, Akka.system().dispatcher());
	}

	public static Page<Require> queryRequireByPage(int page, int pageSize, Long userId, String searchText, Long industryId,
			String skillTag, boolean isFetchUser, boolean isFetchUserExpert) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		queryHql.append("from Require r ");
		countHql.append("select count(r.id) from Require r where 1=1 ");

		if (isFetchUser) {
			queryHql.append(" left join fetch r.owner o  ");
		}
		if (isFetchUser && isFetchUserExpert) {
			queryHql.append(" left join fetch o.experts ");
		}
		queryHql.append(" where 1=1 ");

		if (null != userId) {
			queryHql.append(" and r.owner.id = :userId ");
			countHql.append(" and r.owner.id = :userId ");
			paramMap.put("userId", userId);
		}
		if (StringUtils.isNotBlank(searchText)) {
			queryHql.append(" and (r.industry.tagName like :searchTextLike or r.title like :searchTextLike or r.budget like :searchTextLike)");
			countHql.append(" and (r.industry.tagName like :searchTextLike or r.title like :searchTextLike or r.budget like :searchTextLike)");
			paramMap.put("searchTextLike", "%" + searchText.trim() + "%");
		}
		if (null != industryId) {
			queryHql.append(" and r.industry.id = :industryId ");
			countHql.append(" and r.industry.id = :industryId ");
			paramMap.put("industryId", industryId);

		}
		if (StringUtils.isNotBlank(skillTag)) {
			queryHql.append(" and r.tags like :skillTag ");
			countHql.append(" and r.tags like :skillTag ");
			paramMap.put("skillTag", "%" + skillTag + "%");
		}
		queryHql.append(" order by r.createDate desc");

		TypedQuery<Require> listQuery = JPA.em().createQuery(queryHql.toString(), Require.class);
		TypedQuery<Long> countQuery = JPA.em().createQuery(countHql.toString(), Long.class);

		for (Entry<String, Object> e : paramMap.entrySet()) {
			listQuery.setParameter(e.getKey(), e.getValue());
			countQuery.setParameter(e.getKey(), e.getValue());
		}

		List<Require> data = listQuery.setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
		Long count = countQuery.getSingleResult();

		return new Page<Require>(Constants.SUCESS, count, data);
	}

	public static Page<Require> queryRequireByPage(int page, int pageSize, User user) {
		if (user == null) {
			throw new IllegalArgumentException("传入的user对象不能为空。");
		}

		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		queryHql.append("from Require r where r.owner.id = :userId");
		countHql.append("select count(r.id) from Require r where r.owner.id = :userId");
		paramMap.put("userId", user.getId());

		queryHql.append(" order by r.createDate desc");

		TypedQuery<Require> listQuery = JPA.em().createQuery(queryHql.toString(), Require.class);
		TypedQuery<Long> countQuery = JPA.em().createQuery(countHql.toString(), Long.class);

		for (Entry<String, Object> e : paramMap.entrySet()) {
			listQuery.setParameter(e.getKey(), e.getValue());
			countQuery.setParameter(e.getKey(), e.getValue());
		}

		List<Require> data = listQuery.setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
		Long count = countQuery.getSingleResult();

		return new Page<Require>(Constants.SUCESS, count, data);
	}

	public static Page<RequireListVO> queryRequireByPage(int page, int pageSize, Long categoryId) {
		Long count = (Long) JPA.em().createQuery("select count(r.id) from Require  r where r.industryId = :categoryId")
				.setParameter("categoryId", categoryId).getSingleResult();

		List<Require> data = JPA
				.em()
				.createQuery(
						"from Require r left join fetch r.owner o left join fetch o.experts where r.industryId = :categoryId order by r.createDate desc ",
						Require.class).setParameter("categoryId", categoryId).setFirstResult((page - 1) * pageSize).setMaxResults(pageSize)
				.getResultList();
		List<RequireListVO> slvos = new ArrayList<RequireListVO>();
		for (Require require : data) {
			RequireListVO slv = new RequireListVO();
			slv.setId(require.id);
			if (require.getOwner() != null && require.getOwner().experts != null) {
				Expert expert = require.getOwner().getExperts().iterator().next();
				slv.setCountry(expert.country);
				slv.setCountryUrl(Constants.countryPicKV.get(expert.country));
				slv.setJob(expert.job);
				slv.setGender(expert.gender);
				if (StringUtils.isNotBlank(expert.headUrl))
					slv.setHeadUrl(User.getAvatarFileRelativePath(require.owner.id, 70));
				slv.setOwnerUserName(require.owner.userName);
				slv.setOwnerUserId(require.owner.id);
			}
			slv.setTitle(require.title);
			slv.setInfo(require.info);
			slv.setCreateDate(Constants.dateformat.format(require.createDate));
			if (require.budget == null)
				slv.setBudget("");
			else
				slv.setBudget(Constants.dformat.format(require.budget));
			slvos.add(slv);
		}
		return new Page<RequireListVO>(Constants.SUCESS, count, slvos);
	}

	/**
	 * @return the ownerId
	 */
	public Long getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId
	 *            the ownerId to set
	 */
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

}
