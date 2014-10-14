package system.vo;


public class UrlBeanVO {

	private Long id;// 前台node ID
	private String text;// 树的节点名称
	private Boolean leaf;// 是否叶子叶点
	private Boolean checked;
	private Long parentId;
	private int sort;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the leaf
	 */
	public Boolean getLeaf() {
		return leaf;
	}
	/**
	 * @param leaf the leaf to set
	 */
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	/**
	 * @return the checked
	 */
	public Boolean getChecked() {
		return checked;
	}
	/**
	 * @param checked the checked to set
	 */
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	/**
	 * @return the sort
	 */
	public int getSort() {
		return sort;
	}
	/**
	 * @param sort the sort to set
	 */
	public void setSort(int sort) {
		this.sort = sort;
	}
	/** 
	 * @return parentId 
	 */
	public Long getParentId() {
		return parentId;
	}
	/** 
	 * @param parentId 要设置的 parentId 
	 */
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	/* (非 Javadoc) 
	 * <p>Title: hashCode</p> 
	 * <p>Description: </p> 
	 * @return 
	 * @see java.lang.Object#hashCode() 
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return id.intValue();
	}
	/* (非 Javadoc) 
	 * <p>Title: equals</p> 
	 * <p>Description: </p> 
	 * @param obj
	 * @return 
	 * @see java.lang.Object#equals(java.lang.Object) 
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return ((UrlBeanVO)obj).getId() == id ;
	}
	
	
}
