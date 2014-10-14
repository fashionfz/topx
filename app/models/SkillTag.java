/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月21日
 */
package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import system.vo.Page;
import system.vo.SkillTagListVo;
import vo.STagVo;
import vo.TopCate;
import vo.TopExpert;

import common.Constants;
import common.jackjson.JackJsonUtil;

/**
 * @ClassName: SkillTag
 * @Description: 技能标签 domain
 * @date 2013年10月21日 下午6:58:56
 * @author RenYouchao
 * 
 */
@Entity
@Table(name = "tb_skill_tag")
public class SkillTag implements java.io.Serializable {

	private static final long serialVersionUID = -6901610535613865789L;
	/** 主键自增 **/
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	/** 标签名称 **/
	public String tagName;
	/** 标签英文名 **/
	public String tagNameEn;
	/** 标签拼音 **/
	public String tagNamePY;
	/** 一级行业类型 **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "industryId", insertable = false, updatable = false)
	public SkillTag industry;
	@Column(name = "industryId")
	public Long industryId;
	/** 标签填写数 **/
	public Long hits;
	/** 标签顺序 **/
	public Integer seq;
	/** 标签类型 */
	@Enumerated(EnumType.ORDINAL)
	@Column(length = 1, nullable = false)
	public TagType tagType;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="attach_id")
	public Set<AttachOfIndustry> attachs = new HashSet<AttachOfIndustry>();
	
	
	/** 一级行业类型 **/
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "industryId")
	public Set<SkillTag> industrys;

	/**
	 * @ClassName: TagType
	 * @Description: 行业分类还是标签
	 * @date 2013年12月23日 上午10:23:22
	 * @author RenYouchao
	 */
	public enum TagType {
		CATEGORY, TAG
	}

	public static void getIndExpertById(Long expertId) {
		List<SkillTag> skillTags = JPA.em().createQuery("from SkillTag s left join fetch s.experts e where e.id=:id")
				.setParameter("name", expertId).getResultList();
	}

	public static List<SkillTag> getCategoryTag(boolean isFetchAttachs) {
	    String jpql = "from SkillTag s ";
	    if (isFetchAttachs) {
	        jpql += " left join fetch s.attachs ";
	    }
	    jpql += " where (s.tagType=:tagType ) order by s.seq asc";
		return JPA.em().createQuery(jpql)
				.setParameter("tagType", SkillTag.TagType.CATEGORY).setMaxResults(150).getResultList();
	}

	public static ListOrderedMap getCategoryTagMap() {
		ListOrderedMap ctmap = new ListOrderedMap();
		List<SkillTag> list = JPA.em().createQuery("from SkillTag s where s.tagType=:tagType  order by s.seq asc")
				.setParameter("tagType", SkillTag.TagType.CATEGORY).setMaxResults(150).getResultList();
		for (SkillTag st : list) {
			ctmap.put(st.id, st.tagName);
		}
		return ctmap;
	}

	public static List<SkillTag> getCategoryTag(List<Long> ss) {
		if (CollectionUtils.isNotEmpty(ss)) {
			return JPA.em().createQuery("from SkillTag s where s.id in(:ss)").setParameter("ss", ss).getResultList();
		} else {
			return new ArrayList<SkillTag>();
		}
	}

	public static ListOrderedMap getCacheCategory() {
		ListOrderedMap cts = (ListOrderedMap) play.cache.Cache.get(Constants.CACHE_INDUSTRY);
		if (cts == null) {
			cts = SkillTag.getCategoryTagMap();
			play.cache.Cache.set(Constants.CACHE_INDUSTRY, cts);
		}
		return cts;
	}

	public static List<STagVo> makeSTagVos(String skillsTags) {
		List<STagVo> stvs = new ArrayList<STagVo>();
		if (StringUtils.isNotBlank(skillsTags)) {
			List<String> list = null;
			try {
				list = JackJsonUtil.getMapperInstance(false).readValue(skillsTags, List.class);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.debug(e.getMessage());
			}
			if (CollectionUtils.isNotEmpty(list)) {
				for (String tagName : list) {
					STagVo sTagVo = new STagVo();
					sTagVo.setTag(tagName);
					if (tagName.indexOf("<span class='red'>") != -1) {
						sTagVo.setNoMarkedTag(tagName.replaceAll("<span class='red'>", "").replaceAll("</span>", ""));
						sTagVo.setIsMarked(true);
					} else {
						sTagVo.setNoMarkedTag(tagName);
					}
					stvs.add(sTagVo);
				}
			}
		}
		return stvs;
	}

	public static void deleteExpertIn(SkillTag skillTag) {
		JPA.em().createNativeQuery("delete from tb_expert_in where inId =:inId").setParameter("inId", skillTag.id).executeUpdate();
	}

	public static void deleteTagAndInByIn(SkillTag skillTag) {
		JPA.em().createNativeQuery("delete from tb_skill_tag where (industryId =:inId or id =:inId)").setParameter("inId", skillTag.id)
				.executeUpdate();
	}

	public static List<SkillTag> getAll(Long i, int seq, String type) {
		if (type.equals("all"))
			return JPA.em().createQuery("from SkillTag s where (s.industryId = :industryId or s.tagType = 0) order by s.tagType,s.seq asc")
					.setParameter("industryId", i).setFirstResult((seq - 1) * (40 + 19)).setMaxResults(40 + 19).getResultList();
		else
			return JPA.em().createQuery("from SkillTag s where s.industryId = :industryId and s.tagType = 1 order by s.seq asc")
					.setParameter("industryId", i).setFirstResult((seq - 1) * 40).setMaxResults(40).getResultList();
	}

	public static List<SkillTag> getTagAll(Long i) {
		if (i == null)
			return JPA.em().createQuery("from SkillTag s  order by s.seq asc").setMaxResults(150).getResultList();
		else
			return JPA.em().createQuery("from SkillTag s where (s.tagType=:tagType and s.industryId = :industryId) order by s.seq asc")
					.setParameter("tagType", SkillTag.TagType.TAG).setParameter("industryId", i).setMaxResults(150).getResultList();
	}

	public static void deleteById(Long id) {
		JPA.em().createQuery("delete from SkillTag where id=" + id).executeUpdate();
	}

	public static List<SkillTag> query(String tagName, int max) {
		return (List<SkillTag>) JPA.em().createQuery("from SkillTag s where s.tagName like :k").setParameter("k", "%" + tagName + "%")
				.setMaxResults(max).getResultList();
	}
	
	
	public static void delete(SkillTag tag){
		JPA.em().remove(tag);
	}

	public static void saveOrUpdate(SkillTag skilltag) {
		if (skilltag.id == null) {
			JPA.em().persist(skilltag);
		} else {
			JPA.em().merge(skilltag);
		}
	}

	public static Integer getMaxSeq(String cateId) {
		List<Integer> seqs = null;
		if (StringUtils.isBlank(cateId)) {
			seqs = (List<Integer>) JPA.em().createQuery("select max(s.seq) from SkillTag s where s.tagType=:tagType")
					.setParameter("tagType", SkillTag.TagType.CATEGORY).getResultList();
		} else {
			seqs = (List<Integer>) JPA.em().createQuery("select max(s.seq) from SkillTag s where s.industryId=:id")
					.setParameter("id", Long.parseLong(cateId)).getResultList();
		}
		if (CollectionUtils.isNotEmpty(seqs) && seqs.get(0) != null)
			return seqs.get(0);
		else
			return 0;

	}

	public static SkillTag getTagById(Long id) {
		List<SkillTag> resultList = JPA.em().createQuery("from SkillTag s where s.id=:id", SkillTag.class).setParameter("id", id)
				.getResultList();
		return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
	}

	/**
	 * 查询该服务标签下的子节点
	 * 
	 * @param id
	 *            服务标签id
	 * @return 该服务标签的子节点的集合，没有则返回空
	 */
	public static List<SkillTag> getSubTagsById(Long id) {
		List<SkillTag> skillTagList = (List<SkillTag>) JPA.em()
				.createQuery("from SkillTag s where s.industryId=:id and s.id<>:id order by s.seq").setParameter("id", id).getResultList();
		if (skillTagList != null && skillTagList.size() > 0) {
			return skillTagList;
		}
		return null;
	}

	public static Page<SkillTagListVo> listTagByCate(Long cateId, Integer page, Integer start, Integer limit,String searchText) {
		String hql1="select count(s) from SkillTag s where s.industryId=? and s.id<>?";
		if(searchText!=null&&!"".equals(searchText)){
			hql1+=" and (s.tagName like ? or s.tagNameEn like ?)";
		}
		Query query1 = JPA.em().createQuery(hql1);
		query1.setParameter(1, cateId);
		query1.setParameter(2, cateId);
		if(searchText!=null&&!"".equals(searchText)){
			query1.setParameter(3, "%"+searchText+"%");
			query1.setParameter(4, "%"+searchText+"%");
		}
		Long total = (Long) query1.getSingleResult();
		
		
		String hql2="from SkillTag s left join fetch s.industry where s.industryId=? and s.id<>?";
		if(searchText!=null&&!"".equals(searchText)){
			hql2+=" and (s.tagName like ? or s.tagNameEn like ?)";
		}
		hql2+=" order by s.seq asc";
		
		Query query2 = JPA.em().createQuery(hql2);
		query2.setParameter(1, cateId);
		query2.setParameter(2, cateId);
		if(searchText!=null&&!"".equals(searchText)){
			query2.setParameter(3, "%"+searchText+"%");
			query2.setParameter(4, "%"+searchText+"%");
		}
		
		List<SkillTag> skillTags = query2.setFirstResult(start).setMaxResults(limit).getResultList();
		Page<SkillTagListVo> p = new Page<SkillTagListVo>();
		p.setTotal(total);

		List<SkillTagListVo> sklvs = new ArrayList<SkillTagListVo>();
		for (SkillTag skillTag : skillTags) {
			SkillTagListVo sklv = new SkillTagListVo();
			sklv.setId(skillTag.id);
			if (skillTag.industry != null) {
				sklv.setIndustryId(skillTag.industry.id);
				sklv.setIndustryName(skillTag.industry.tagName);
			}
			sklv.setHits(skillTag.hits);
			sklv.setTagNameEn(skillTag.tagNameEn);
			sklv.setTagName(skillTag.tagName);
			sklv.setSeq(skillTag.seq);
			sklv.setTagType(skillTag.tagType);
			sklvs.add(sklv);
		}
		p.setData(sklvs);
		return p;
	}

	public static List<SkillTag> listCategories(int limit) {
		return JPA.em().createQuery("from SkillTag s where s.tagType=:tagType order by s.seq asc")
				.setParameter("tagType", SkillTag.TagType.CATEGORY).setMaxResults(limit).getResultList();
	}

	/**
	 * 查询某个专家所在的行业
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 */
	public static List<SkillTag> listCategoriesOfExpert(Long userId) {
		return (List<SkillTag>) JPA.em().createQuery("select e.inTags from Expert e where e.userId=:userId").setParameter("userId", userId)
				.getResultList();
	}

	/**
	 * 查询所有用户对应的国家名的集合，根据用户数量降序
	 * 
	 * @return 国家名的list集合
	 */
	public static List<String> listCountrysOfAllUser() {
		List<String> countryList = new ArrayList<String>();
		List<Object[]> list = JPA
				.em()
				.createQuery(
						"select distinct(e.country),count(e.id) from Expert e where e.country is not null group by e.country order by count(e.id) desc,e.country")
				.getResultList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Object[] objs = (Object[]) list.get(i);
				countryList.add((String) objs[0]);
			}
		}

		return countryList;
	}

	public static Boolean isExist(String tagName, Long id) {
		List<SkillTag> st = null;
		if (id == null)
			st = JPA.em().createQuery("from SkillTag s where s.tagName=:tagName and s.tagType=1").setParameter("tagName", tagName)
					.getResultList();
		else
			st = JPA.em().createQuery("from SkillTag s where s.tagName=:tagName and s.tagType=1 and s.id <>" + id)
					.setParameter("tagName", tagName).getResultList();
		if (CollectionUtils.isEmpty(st)) {
			return false;
		} else {
			return true;
		}
	}

	public static List<TopCate> getTopCateWithCache() {
		List<TopCate> cates = (List<TopCate>) Cache.get(Constants.CACHE_EXPERT_TOPS);
		if (cates == null) {
			HashMap<Long, Integer> expertMap = new HashMap<Long, Integer>();
			cates = new ArrayList<TopCate>();
			List<SkillTag> tags = SkillTag.listCategories(8);
			for (SkillTag st : tags) {
				TopCate topCate = new TopCate();
				topCate.setId(st.id);
				topCate.setTagName(st.tagName);
				cates.add(topCate);
			}
			TopExpert.queryTopExpertList(cates);
			Cache.set(Constants.CACHE_EXPERT_TOPS, cates, 24 * 60 * 60);
		}
		return cates;
	}

	public static List<String> getCountryNameWithCache() {
		List<String> countryNameList = (List<String>) Cache.get(Constants.CACHE_COUNTRY);
		if (countryNameList == null) {
			countryNameList = listCountrysOfAllUser();
			Cache.set(Constants.CACHE_COUNTRY, countryNameList, 24 * 60 * 60);
		}
		return countryNameList;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagNameEn() {
		return tagNameEn;
	}

	public void setTagNameEn(String tagNameEn) {
		this.tagNameEn = tagNameEn;
	}

	public String getTagNamePY() {
		return tagNamePY;
	}

	public void setTagNamePY(String tagNamePY) {
		this.tagNamePY = tagNamePY;
	}

	public SkillTag getIndustry() {
		return industry;
	}

	public void setIndustry(SkillTag industry) {
		this.industry = industry;
	}

	public Long getIndustryId() {
		return industryId;
	}

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public Long getHits() {
		return hits;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public TagType getTagType() {
		return tagType;
	}


    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }

	public Set<AttachOfIndustry> getAttachs() {
		return attachs;
	}

	public void setAttachs(Set<AttachOfIndustry> attachs) {
		this.attachs = attachs;
	}

	/** 
	 * @return industrys 
	 */
	public Set<SkillTag> getIndustrys() {
		return industrys;
	}

	/** 
	 * @param industrys 要设置的 industrys 
	 */
	public void setIndustrys(Set<SkillTag> industrys) {
		this.industrys = industrys;
	}
    
}
