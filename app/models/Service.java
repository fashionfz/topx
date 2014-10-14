/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年8月14日
 */
package models;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
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
import vo.ServiceListVO;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;
import common.Constants;

import controllers.base.ObjectNodeResult;
import ext.search.STransformer;
import ext.search.SearchHttpClient;

/**
 * @ClassName: Service
 * @Description: 服务实体类
 * @date 2014年8月14日 下午1:49:48
 * @author RenYouchao
 * 
 */
@Entity
@Table(name = "tb_service")
public class Service {
	


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/** 服务创建人 **/
	@Column(name = "ownerId", insertable = false, updatable = false)
	public Long ownerId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	public User owner;
	@Column(name = "industryId", insertable = false, updatable = false)
	public Long industryId;
	/** 服务行业 **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "industryId")
	public SkillTag industry;
	/** 服务标题 **/
	@Column(length = 4000)
	public String title;
	/** 服务说明 **/
	@Column(length = 4000)
	public String info;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="attach_id")
	@OrderBy("seq ASC, id DESC")
    @Where(clause = "attachType='service'")
	public Set<AttachOfService> caseAttachs = new HashSet<AttachOfService>();
	/** 价格 **/
	public Double price;
	/** 标签 **/
	@Column(length = 4000)
	public String tags;
	/**服务封面默认图片**/
	public String coverUrl;
	/** 发布日期 **/
	public Date createDate;
	/** 评分（总分） */
	public Float score = 0.0f;
	/** 平均分数 **/
	public Float averageScore = 0.0f;
	/** 评论数 */
	public Long commentNum = 0l;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOwnerId() {
        return ownerId;
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

	public Set<AttachOfService> getCaseAttachs() {
		return caseAttachs;
	}

	public void setCaseAttachs(Set<AttachOfService> caseAttachs) {
		this.caseAttachs = caseAttachs;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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
	
	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public Float getAverageScore() {
		return averageScore;
	}

	public void setAverageScore(Float averageScore) {
		this.averageScore = averageScore;
	}

	public Long getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(Long commentNum) {
		this.commentNum = commentNum;
	}

	/**
	 * 新增服务
	 */
	public static Service saveService(Service service) {
		JPA.em().persist(service);
		return service;
	}
	
	/**
	 * 新增服务
	 * @return
	 */
	public Service saveService() {
		JPA.em().persist(this);
		return this;
	}
	
	/**
	 * 保存或更新服务信息
	 */
	public static ObjectNodeResult saveOrUpdateByJson(User currentUser,JsonNode json){
		JPA.em().clear();
		ObjectNodeResult result = new ObjectNodeResult();
//		String[] notUseStrs = { "helome", "嗨啰" };
		
		Service service = new Service();
		Service dbService = null; // 从数据库中查询出来的Service对象
		// id
		if (json.has("id")) {
			Long id = json.findPath("id").asLong();
			if (id != null) {
				dbService = Service.queryServiceById(id);
				if (dbService != null) {
					if (dbService.getOwner() != null && dbService.getOwner().getId() - currentUser.getId() != 0) {
						return result.error("你不是该服务的创建者，修改失败", "-301");
					}
					service = dbService;
				}
			}
		}
		// title
		if (json.has("title")) {
			String title = json.findPath("title").asText();
			if (StringUtils.isBlank(title)) {
				return result.error("标题不能为空", "800001");
			}
//			for (String item : notUseStrs) {
//				if (StringUtils.contains(title, item)) {
//					return result.error("标题不能包含'" + item +"'字符", "800002");
//				}
//			}
			if (StringUtils.isNotBlank(title)) {
				title = common.SensitiveWordsFilter.doFilter(title.trim());
				title = common.ReplaceWordsFilter.doFilter(title);
			}
			service.setTitle(title);
		}
		// info
		if (json.hasNonNull("info")) {
			String info = json.findPath("info").asText();
			if (info.length() > 500) {
				return result.error("服务说明最多500个字符", "800003");
			}
			info = common.SensitiveWordsFilter.doFilter(info.trim());
			info = common.ReplaceWordsFilter.doFilter(info);
			service.setInfo(info);
		}
		// price
		if (json.has("price")) {
			DecimalFormat df = new DecimalFormat("###.00");
			Double price = null;
			String priceStr = json.findPath("price").asText();
			if (StringUtils.isNotBlank(priceStr)) {
				try {
					price = Double.parseDouble(df.format(json.findPath("price").asDouble()));
				} catch (Exception e) {
					if (Logger.isErrorEnabled()) {
						Logger.error("价格转换异常。price：" + price, e);
					}
				}
			}
			service.setPrice(price);
		}
		// industry
		if (json.has("industry")) {
			Long industryId =json.findPath("industry").asLong();
			SkillTag sk = SkillTag.getTagById(industryId);
			service.setIndustry(sk);
			if (sk != null) {
				service.setIndustryId(sk.getId());
			}
		}
		
		// tags
		if (json.has("tags") && json.get("tags").isArray()) {
			String tags = json.findPath("tags").toString();
			if (StringUtils.isNotBlank(tags)) {
				tags = common.SensitiveWordsFilter.doFilter(tags);
				tags = common.ReplaceWordsFilter.doFilter(tags);
			}
			service.setTags(tags);
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
				service.getCaseAttachs().clear();
				List<AttachOfService> attachList = new AttachOfService().queryAttachListByIdsAndCreateUserId(attachIdList, currentUser.getId());
				if (Logger.isInfoEnabled()) {
					Logger.info("service的id：" + service.getId());
				} 
				// 设置seq
				if (CollectionUtils.isNotEmpty(attachList)) {
					for (int i = 0; i < attachList.size(); ++i) {
						attachList.get(i).seq = (long) i;
						if (Logger.isInfoEnabled()) {
					    	Logger.info("attach的id：" + attachList.get(i).id);
					    }
					}
					service.coverUrl = attachList.get(0).path;
				}
				
				Set<AttachOfService> attachSet = new HashSet<AttachOfService>(attachList);
				service.setCaseAttachs(attachSet);
			}
		}
		
		if (service.getId() == null || dbService == null) {
			// 服务创建人
			service.setOwner(currentUser);
			service.setOwnerId(currentUser.id);
			// 服务发布日期
			service.setCreateDate(new Date());
		}
		
		service.saveOrUpdate();
		
		result.put("serviceId", service.getId());
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
		final Service thiz = this;
		final Expert expert = Expert.getExpertByUserId(thiz.ownerId);
		Akka.system().scheduler().scheduleOnce(Duration.create(100, TimeUnit.MILLISECONDS), new Runnable() {
			public void run() {
				STransformer tf = new STransformer(thiz);
				tf.setCountry(expert.country);
				tf.setGender(expert.getGenderWithDefault());
				tf.setUserId(expert.userId);
				if (StringUtils.isNotBlank(expert.headUrl))
					tf.setHeadUrl(User.getAvatarFileRelativePath(expert.userId,70));
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
	public static Service queryServiceById(Long serviceId) {
		List<Service> serviceList = JPA.em().createQuery("from Service s left join fetch s.owner where s.id = :id", Service.class)
				.setParameter("id", serviceId).getResultList();
		if (CollectionUtils.isNotEmpty(serviceList)) {
			Service service =  serviceList.get(0);
			return service;
		}
		return null;
	}
	
	/**
	 * 根据id删除
	 * @param serviceId
	 */
	public static void deleteById(final Long serviceId) {
		JPA.em().createQuery("delete from Service where id = :id")
				.setParameter("id", serviceId).executeUpdate();
		Akka.system().scheduler().scheduleOnce(Duration.create(100, TimeUnit.MILLISECONDS), new Runnable() {
			public void run() {
				 STransformer.tranDeleteNVP(serviceId);;
			}
		}, Akka.system().dispatcher());
	}
	
	public static Page<ServiceListVO> queryServiceByPage(int page, int pageSize,Long categoryId){
		
		Long count = (Long) JPA.em()
				.createQuery("select count(s.id) from Service   s where s.industryId = :categoryId")
				.setParameter("categoryId", categoryId)
				.getSingleResult();
		
		List<Service> data = JPA.em()
				.createQuery("from Service s left join fetch s.owner o left join fetch o.experts where s.industryId = :categoryId order by s.createDate desc ", Service.class)
				.setParameter("categoryId", categoryId)
				.setFirstResult((page - 1) * pageSize)
				.setMaxResults(pageSize)
				.getResultList();
		List<ServiceListVO> slvos = new ArrayList<ServiceListVO>();
		for (Service service : data){
			ServiceListVO slv = new ServiceListVO();
			slv.setId(service.id);
			if (service.getOwner() != null && service.getOwner().experts != null){
				Expert expert = service.getOwner().getExperts().iterator().next();
				slv.setCountry(expert.country);
				slv.setCountryUrl(Constants.countryPicKV.get(expert.country));
				slv.setJob(expert.job);
				slv.setGender(expert.gender);
				slv.setOwnerUserName(service.owner.userName);
				if (StringUtils.isNotBlank(expert.headUrl))
					slv.setHeadUrl(User.getAvatarFileRelativePath(service.owner.id,70));
			}
			slv.setCoverUrl(service.coverUrl);
			slv.setOwnerUserId(service.owner.id);
			slv.setTitle(service.title);
			slv.setInfo(service.info);
			slv.setScore(String.valueOf(service.score));
			slv.setCommentNum(service.commentNum);
			if (service.price == null)
				slv.setPrice("");
			else
				slv.setPrice(Constants.dformat.format(service.price));
			slvos.add(slv);
		}
		return new Page<ServiceListVO>(Constants.SUCESS, count, slvos);
	}
	
	
	public static Page<Service> queryServiceByPage(int page, int pageSize, Long userId, String searchText, 
	        Long industryId, String skillTag, boolean isFetchUser, boolean isFetchUserExpert) {
		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		queryHql.append("from Service s ");
		countHql.append("select count(s.id) from Service s where 1=1 ");
		
		if (isFetchUser) {
		    queryHql.append(" left join fetch s.owner o  ");
		}
		if (isFetchUser && isFetchUserExpert) {
		    queryHql.append(" left join fetch o.experts ");
		}
		queryHql.append(" where 1=1 ");
		
		if (null != userId) {
		    queryHql.append(" and s.owner.id = :userId ");
		    countHql.append(" and s.owner.id = :userId ");
		    paramMap.put("userId", userId);
		}
		if (StringUtils.isNotBlank(searchText)) {
		    queryHql.append(" and (s.industry.tagName like :searchTextLike or s.title like :searchTextLike or s.price like :searchTextLike)");
		    countHql.append(" and (s.industry.tagName like :searchTextLike or s.title like :searchTextLike or s.price like :searchTextLike)");
		    paramMap.put("searchTextLike", "%" + searchText.trim() + "%");
		}
		if (null != industryId) {
		    queryHql.append(" and s.industry.id = :industryId ");
		    countHql.append(" and s.industry.id = :industryId ");
		    paramMap.put("industryId", industryId);
		    
		}
	    if (StringUtils.isNotBlank(skillTag)) {
	        queryHql.append(" and s.tags like :skillTag ");
	        countHql.append(" and s.tags like :skillTag ");
            paramMap.put("skillTag", "%" + skillTag + "%");
        }
		queryHql.append(" order by s.createDate desc");

		TypedQuery<Service> listQuery = JPA.em().createQuery(queryHql.toString(), Service.class);
		TypedQuery<Long> countQuery = JPA.em().createQuery(countHql.toString(), Long.class);

		for (Entry<String, Object> e : paramMap.entrySet()) {
			listQuery.setParameter(e.getKey(), e.getValue());
			countQuery.setParameter(e.getKey(), e.getValue());
		}

		List<Service> data = listQuery.setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
		Long count = countQuery.getSingleResult();

		return new Page<Service>(Constants.SUCESS, count, data);
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	
	public static Page<Service> queryServiceByPage(int page, int pageSize, User user) {
		if (user == null) {
			throw new IllegalArgumentException("传入的user对象不能为空。");
		}

		StringBuffer queryHql = new StringBuffer();
		StringBuffer countHql = new StringBuffer();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		queryHql.append("from Service s where s.owner.id = :userId");
		countHql.append("select count(s.id) from Service s where s.owner.id = :userId");
		paramMap.put("userId", user.getId());

		queryHql.append(" order by s.createDate desc");

		TypedQuery<Service> listQuery = JPA.em().createQuery(queryHql.toString(), Service.class);
		TypedQuery<Long> countQuery = JPA.em().createQuery(countHql.toString(), Long.class);

		for (Entry<String, Object> e : paramMap.entrySet()) {
			listQuery.setParameter(e.getKey(), e.getValue());
			countQuery.setParameter(e.getKey(), e.getValue());
		}

		List<Service> data = listQuery.setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
		Long count = countQuery.getSingleResult();

		return new Page<Service>(Constants.SUCESS, count, data);
	}
	
	
	/**
	 * 计算平均分 <br/>
	 * 提高正确性
	 * @param score
	 */
	public void computeAverageScore(Long toCommentUserId,Long serviceId) {
		this.score = new Float(Comment.getTotalLevelBytoCommentServiceId(toCommentUserId,serviceId,ServiceComment.class));
		this.commentNum = Comment.getTotalCountBytoCommentServiceId(toCommentUserId,serviceId,ServiceComment.class);
		DecimalFormat df = new DecimalFormat("###.0");
		if (commentNum != null && commentNum > 0)
			this.averageScore = Float.parseFloat(df.format((float) this.score / (float) this.commentNum));
	}
	
}
