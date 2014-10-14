package system.vo;

import models.SkillTag;
import models.SkillTag.TagType;

/**
 * @ClassName: SkillTagListVo
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2013年12月23日 下午5:16:02
 * @author RenYouchao
 * 
 */
public class SkillTagListVo {

	/**标示**/
	private Long id;
	/**标签名称 **/
	private String tagName;
	
	private String tagNameEn;
	
	private Long industryId;
	/**行业分类**/
	private String industryName;
	/**标签填写数 **/
	private Long hits;
	/**标签顺序 **/
	private Integer seq;
	/**标签类型 **/
	private TagType tagType;
	/** 是否是儿子节点 **/
	private Boolean leaf;
	/** 层级数 **/
	private Integer level;
	
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
	public Boolean getLeaf() {
		return leaf;
	}
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
	/**
	 * @return the industryId
	 */
	public Long getIndustryId() {
		return industryId;
	}
	/**
	 * @param industryId the industryId to set
	 */
	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}
	/**
	 * @return the tagNameEn
	 */
	public String getTagNameEn() {
		return tagNameEn;
	}
	/**
	 * @param tagNameEn the tagNameEn to set
	 */
	public void setTagNameEn(String tagNameEn) {
		this.tagNameEn = tagNameEn;
	}

}
