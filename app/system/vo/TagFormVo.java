package system.vo;

/**
 * @ClassName: TagFormVo
 * @Description: TagForm的VO类
 * @date 2013年12月23日 下午12:25:02
 * @author RenYouchao
 * 
 */
public class TagFormVo {
	
	private Long id;
	
	private String tagName;
	
	private String tagNameEn;
	
	private Long parentId;
	
	private String parentTagName;
	
	private Long industryId;
	
	private Long hits;
	
	private Integer seq;
	
	private Integer tagType;
	
	private Boolean leaf;

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

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getParentTagName() {
		return parentTagName;
	}

	public void setParentTagName(String parentTagName) {
		this.parentTagName = parentTagName;
	}

	public Long getHits() {
		return hits;
	}

	public void setHits(Long hits) {
		this.hits = hits;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public Integer getTagType() {
		return tagType;
	}

	public void setTagType(Integer tagType) {
		this.tagType = tagType;
	}

	public Long getIndustryId() {
		return industryId;
	}

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
