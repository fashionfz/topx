package system.models.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import models.Expert;
import models.Gender;
import models.User;
import models.User.ResumeStatus;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import play.db.jpa.JPA;
import system.vo.Page;
import system.vo.UserGridVO;
import ext.search.Transformer;

public class UserService {

    public static Page<UserGridVO> queryUserGridPage(UserGridSearchCondition c) {
        List<UserGridVO> result = new ArrayList<>();

        Integer start = c.start;
        Integer limit = c.limit;
        if (null == start) {
            start = 0;
        }
        if (null == limit) {
            limit = 20;
        }

        StringBuilder queryQL = new StringBuilder("from User u left join fetch u.experts e");
        StringBuilder countQL = new StringBuilder("select count(u.id) from User u left join u.experts e");
        if (c.inId != null) {
            queryQL.append(" left join fetch e.inTags inTags");
            countQL.append(" left join e.inTags inTags");
        }

        Map<String, Object> paramMap = new HashMap<>();
        queryQL.append(" where 1 = 1");
        countQL.append(" where 1 = 1");

        if (StringUtils.isNotBlank(c.searchText)) {
            queryQL.append(" and (u.userName like :searchTextLike or u.email like :searchTextLike or u.phoneNumber like :searchTextLike");
            countQL.append(" and (u.userName like :searchTextLike or u.email like :searchTextLike or u.phoneNumber like :searchTextLike");
            paramMap.put("searchTextLike", "%" + c.searchText + "%");

            if (NumberUtils.isDigits(c.searchText)) {
                queryQL.append(" or u.id = :searchTextDigit");
                countQL.append(" or u.id = :searchTextDigit");
                paramMap.put("searchTextDigit", NumberUtils.toLong(c.searchText));
            }
            queryQL.append(")");
            countQL.append(")");
        }
        if (StringUtils.isNotBlank(c.country)) {
            queryQL.append(" and e.country = :country");
            countQL.append(" and e.country = :country");
            paramMap.put("country", c.country);
        }
        if (null != c.gender) {
            queryQL.append(" and u.gender = :gender");
            countQL.append(" and u.gender = :gender");
            paramMap.put("gender", c.gender);
        }
        if (null != c.isComplain) {
            queryQL.append(" and u.isComplain = :isComplain");
            countQL.append(" and u.isComplain = :isComplain");
            paramMap.put("isComplain", c.isComplain);
        }
        if (null != c.isEnable) {
            queryQL.append(" and u.isEnable = :isEnable");
            countQL.append(" and u.isEnable = :isEnable");
            paramMap.put("isEnable", c.isEnable);
        }
        //注册日期查询
        if(null != c.startDate){
        	queryQL.append(" and u.registerDate > :startDate");
        	countQL.append(" and u.registerDate > :startDate");
        	paramMap.put("startDate", c.startDate);
        }
        if(null != c.endDate){
        	queryQL.append(" and u.registerDate < :endDate");
        	countQL.append(" and u.registerDate < :endDate");
        	paramMap.put("endDate", c.endDate);
        }
        if (null != c.isTop) {
            queryQL.append(" and (e.isTop = :isTop");
            countQL.append(" and (e.isTop = :isTop");
            paramMap.put("isTop", c.isTop);

            if (!c.isTop) {
                queryQL.append(" or e.isTop is null");
                countQL.append(" or e.isTop is null");
            } else {
            	queryQL.append(" and e.topIndustry is not null");
                countQL.append(" and e.topIndustry is not null");
            }

            queryQL.append(")");
            countQL.append(")");
        }
        if (null != c.inId) {
            queryQL.append(" and inTags.id = :inId");
            countQL.append(" and inTags.id = :inId");
            paramMap.put("inId", c.inId);
        }
		if (StringUtils.isNotEmpty(c.getUserType())) {
			if(StringUtils.equals("0", c.getUserType())) { // 嗨啰在线客服
				queryQL.append(" and e.skillsTags like :skillsTags");
	            countQL.append(" and e.skillsTags like :skillsTags");
	            paramMap.put("skillsTags", "%"+"\"嗨啰在线客服\""+"%");
			} else if(StringUtils.equals("1", c.getUserType())) { // 嗨啰在线翻译
				queryQL.append(" and e.skillsTags like :skillsTags");
	            countQL.append(" and e.skillsTags like :skillsTags");
	            paramMap.put("skillsTags", "%"+"\"嗨啰在线翻译\""+"%");
			} else if(StringUtils.equals("2", c.getUserType())) { // 海外简历未发布
				queryQL.append(" and (u.resumeStatus = :resumeStatus or u.resumeStatus is null) and u.id<1000000");
	            countQL.append(" and (u.resumeStatus = :resumeStatus or u.resumeStatus is null) and u.id<1000000");
	            paramMap.put("resumeStatus", ResumeStatus.UNPUBLISHED);
			} else if(StringUtils.equals("3", c.getUserType())) { // 海外简历已发布(翻译中)
				queryQL.append(" and u.resumeStatus = :resumeStatus");
	            countQL.append(" and u.resumeStatus = :resumeStatus");
	            paramMap.put("resumeStatus", ResumeStatus.PUBLISHED);
			} else if(StringUtils.equals("4", c.getUserType())) { // 海外简历已翻译
				queryQL.append(" and u.resumeStatus = :resumeStatus");
	            countQL.append(" and u.resumeStatus = :resumeStatus");
	            paramMap.put("resumeStatus", ResumeStatus.TRANSLATED);
			} else if(StringUtils.equals("5", c.getUserType())) { // 中文版用户
				queryQL.append(" and u.id<1000000");
	            countQL.append(" and u.id<1000000");
			} else if(StringUtils.equals("6", c.getUserType())) { // 国际版用户
				queryQL.append(" and u.id>=1000000");
	            countQL.append(" and u.id>=1000000");
			}
        }

        boolean isSort = false;
        if (StringUtils.isNotBlank(c.sortProperty) && null != c.isDesc) {
            isSort = true;
        }
        if (!isSort && null != c.inId) {
            c.sortProperty = "e.isTop";
            c.isDesc = true;
            isSort = true;
        }
        if (isSort) {
            queryQL.append(" order by " + c.sortProperty + (c.isDesc ? " DESC" : " ASC"));
        } else {
            queryQL.append(" order by u.registerDate DESC");
        }

        Query dataQuery = JPA.em().createQuery(queryQL.toString());
        Query countQuery = JPA.em().createQuery(countQL.toString());

        if (MapUtils.isNotEmpty(paramMap)) {
            for (Map.Entry<String, Object> e : paramMap.entrySet()) {
                dataQuery.setParameter(e.getKey(), e.getValue());
                countQuery.setParameter(e.getKey(), e.getValue());
            }
        }

        @SuppressWarnings("unchecked")
        List<User> resultList = dataQuery.setFirstResult(start).setMaxResults(limit).getResultList();
        if (CollectionUtils.isNotEmpty(resultList))
            for (User user : resultList) {
            	// 这里是解决根据行业条件进行查询的时候，查询出的用户的行业没有展示完整的问题。
            	if (null != c.inId) {
            		List<models.SkillTag> skillTags = models.SkillTag.listCategoriesOfExpert(user.getId());
            		java.util.Set<models.SkillTag> skillTagSet = new java.util.HashSet<models.SkillTag>();
					if (CollectionUtils.isNotEmpty(skillTags)) {
						for (models.SkillTag s : skillTags) {
							skillTagSet.add(s);
                		}
						Expert expert = CollectionUtils.isEmpty(user.experts) ? null : user.getExperts().iterator().next();
						expert.inTags = skillTagSet;
            		}
                }
                UserGridVO vo = UserGridVO.initByUser(user);
                result.add(vo);
            }

        Long count = (Long) countQuery.getSingleResult();

        Page<UserGridVO> page = new Page<>();
        page.setTotal(count);
        page.setData(result);

        return page;
    }

    public static class UserGridSearchCondition {
        private String searchText;
        private Boolean isEnable;
        private Boolean isOnline;
        private Boolean isComplain;
        private Boolean isTop;
        private Gender gender;
        private String country;
        /**注册起始日期*/
        private Date startDate;
        /**注册结束日期*/
        private Date endDate;
        /**
         * 行业Id
         */
        private Long inId;
        /**
         * 用户类型<br/>
         * ""：所有的用户，"0"：嗨啰在线客服，"1"：嗨啰在线翻译
         */
        private String userType;

        private Integer start;
        private Integer limit;

        private String sortProperty;
        private Boolean isDesc;

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

        public String getSearchText() {
            return searchText;
        }

        public void setSearchText(String searchText) {
            this.searchText = searchText;
        }

        public Boolean getIsEnable() {
            return isEnable;
        }

        public void setIsEnable(Boolean isEnable) {
            this.isEnable = isEnable;
        }

        public Boolean getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(Boolean isOnline) {
            this.isOnline = isOnline;
        }

        public Boolean getIsComplain() {
            return isComplain;
        }

        public void setIsComplain(Boolean isComplain) {
            this.isComplain = isComplain;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
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

        public Boolean getIsTop() {
            return isTop;
        }

        public void setIsTop(Boolean isTop) {
            this.isTop = isTop;
        }

        public Long getInId() {
            return inId;
        }

        public void setInId(Long inId) {
            this.inId = inId;
        }

		public String getUserType() {
			return userType;
		}

		public void setUserType(String userType) {
			this.userType = userType;
		}

		/**
		 * @return the startDate
		 */
		public Date getStartDate() {
			return startDate;
		}

		/**
		 * @param startDate the startDate to set
		 */
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		/**
		 * @return the endDate
		 */
		public Date getEndDate() {
			return endDate;
		}

		/**
		 * @param endDate the endDate to set
		 */
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		

    }

    public static List<String> getCountry() {
        String sql = "select country from tb_expert where country is not null group by country";
        @SuppressWarnings("unchecked")
        List<String> resultList = JPA.em().createNativeQuery(sql).getResultList();

        return resultList;
    }

    public static void saveUserState(Long userId, Boolean isEnable, Boolean isComplain, Boolean isOnline) {
        if (null == userId || null == isEnable || null == isOnline || null == isComplain) {
            throw new IllegalArgumentException("参数不能为null");
        }
        if (isEnable == false) {
            Transformer.tranDeleteNVP(userId);
        }

        JPA.em()
                .createQuery(
                        "update User u set u.isEnable = :isEnable,"
                                + " isComplain = :isComplain  where u.id = :userId")
                .setParameter("isEnable", isEnable).setParameter("isComplain", isComplain).setParameter("userId", userId).executeUpdate();
    }
    
    
    
    public static List<UserGridVO> queryUserGridExcel(UserGridSearchCondition c) {
    	List<UserGridVO> result = new ArrayList<>();
        StringBuilder queryQL = new StringBuilder("from User u left join fetch u.experts e");
        if (c.inId != null) {
            queryQL.append(" left join fetch e.inTags inTags");
        }

        Map<String, Object> paramMap = new HashMap<>();
        queryQL.append(" where 1 = 1");

        if (StringUtils.isNotBlank(c.searchText)) {
            queryQL.append(" and (u.userName like :searchTextLike or u.email like :searchTextLike or u.phoneNumber like :searchTextLike");
            paramMap.put("searchTextLike", "%" + c.searchText + "%");

            if (NumberUtils.isDigits(c.searchText)) {
                queryQL.append(" or u.id = :searchTextDigit");
                paramMap.put("searchTextDigit", NumberUtils.toLong(c.searchText));
            }
            queryQL.append(")");
        }
        if (StringUtils.isNotBlank(c.country)) {
            queryQL.append(" and e.country = :country");
            paramMap.put("country", c.country);
        }
        if (null != c.gender) {
            queryQL.append(" and u.gender = :gender");
            paramMap.put("gender", c.gender);
        }
        if (null != c.isComplain) {
            queryQL.append(" and u.isComplain = :isComplain");
            paramMap.put("isComplain", c.isComplain);
        }
        if (null != c.isEnable) {
            queryQL.append(" and u.isEnable = :isEnable");
            paramMap.put("isEnable", c.isEnable);
        }
        //注册日期查询
        if(null != c.startDate){
        	queryQL.append(" and u.registerDate > :startDate");
        	paramMap.put("startDate", c.startDate);
        }
        if(null != c.endDate){
        	queryQL.append(" and u.registerDate < :endDate");
        	paramMap.put("endDate", c.endDate);
        }
        if (null != c.isTop) {
            queryQL.append(" and (e.isTop = :isTop");
            paramMap.put("isTop", c.isTop);

            if (!c.isTop) {
                queryQL.append(" or e.isTop is null");
            } else {
            	queryQL.append(" and e.topIndustry is not null");
            }

            queryQL.append(")");
        }
        if (null != c.inId) {
            queryQL.append(" and inTags.id = :inId");
            paramMap.put("inId", c.inId);
        }
		if (StringUtils.isNotEmpty(c.getUserType())) {
			if(StringUtils.equals("0", c.getUserType())) { // 嗨啰在线客服
				queryQL.append(" and e.skillsTags like :skillsTags");
	            paramMap.put("skillsTags", "%"+"\"嗨啰在线客服\""+"%");
			} else if(StringUtils.equals("1", c.getUserType())) { // 嗨啰在线翻译
				queryQL.append(" and e.skillsTags like :skillsTags");
	            paramMap.put("skillsTags", "%"+"\"嗨啰在线翻译\""+"%");
			} else if(StringUtils.equals("2", c.getUserType())) { // 海外简历未发布
				queryQL.append(" and (u.resumeStatus = :resumeStatus or u.resumeStatus is null) and u.id<1000000");
	            paramMap.put("resumeStatus", ResumeStatus.UNPUBLISHED);
			} else if(StringUtils.equals("3", c.getUserType())) { // 海外简历已发布(翻译中)
				queryQL.append(" and u.resumeStatus = :resumeStatus");
	            paramMap.put("resumeStatus", ResumeStatus.PUBLISHED);
			} else if(StringUtils.equals("4", c.getUserType())) { // 海外简历已翻译
				queryQL.append(" and u.resumeStatus = :resumeStatus");
	            paramMap.put("resumeStatus", ResumeStatus.TRANSLATED);
			} else if(StringUtils.equals("5", c.getUserType())) { // 中文版用户
				queryQL.append(" and u.id<1000000");
			} else if(StringUtils.equals("6", c.getUserType())) { // 国际版用户
				queryQL.append(" and u.id>=1000000");
			}
        }

        boolean isSort = false;
        if (StringUtils.isNotBlank(c.sortProperty) && null != c.isDesc) {
            isSort = true;
        }
        if (!isSort && null != c.inId) {
            c.sortProperty = "e.isTop";
            c.isDesc = true;
            isSort = true;
        }
        if (isSort) {
            queryQL.append(" order by " + c.sortProperty + (c.isDesc ? " DESC" : " ASC"));
        } else {
            queryQL.append(" order by u.registerDate DESC");
        }

        Query dataQuery = JPA.em().createQuery(queryQL.toString());

        if (MapUtils.isNotEmpty(paramMap)) {
            for (Map.Entry<String, Object> e : paramMap.entrySet()) {
                dataQuery.setParameter(e.getKey(), e.getValue());
            }
        }
        @SuppressWarnings("unchecked")
        List<User> resultList = dataQuery.getResultList();
        if (CollectionUtils.isNotEmpty(resultList))
            for (User user : resultList) {
            	// 这里是解决根据行业条件进行查询的时候，查询出的用户的行业没有展示完整的问题。
            	if (null != c.inId) {
            		List<models.SkillTag> skillTags = models.SkillTag.listCategoriesOfExpert(user.getId());
            		java.util.Set<models.SkillTag> skillTagSet = new java.util.HashSet<models.SkillTag>();
					if (CollectionUtils.isNotEmpty(skillTags)) {
						for (models.SkillTag s : skillTags) {
							skillTagSet.add(s);
                		}
						Expert expert = CollectionUtils.isEmpty(user.experts) ? null : user.getExperts().iterator().next();
						expert.inTags = skillTagSet;
            		}
                }
                UserGridVO vo = UserGridVO.initByUser(user);
                result.add(vo);
            }
        return result;
    }

}
