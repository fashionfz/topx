package system.vo;


public class MainMenuBeanVO {

	private String id;// 前台node ID
	private String text;// 树的节点名称
	private Boolean leaf;// 是否叶子叶点
	private String tabxtype;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
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
	 * @return the tabxtype
	 */
	public String getTabxtype() {
		return tabxtype;
	}
	/**
	 * @param tabxtype the tabxtype to set
	 */
	public void setTabxtype(String tabxtype) {
		this.tabxtype = tabxtype;
	}

}
