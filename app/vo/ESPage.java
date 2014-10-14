package vo;

import java.util.List;


public class ESPage<T> {
	
	private Long pageSize;
	
	private Long pageIndex;
	
	private List<T> list;



	/**
	 * @return the pageSize
	 */
	public Long getPageSize() {
		return pageSize;
	}

	/**
	 * @return the pageIndex
	 */
	public Long getPageIndex() {
		return pageIndex;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(Long pageIndex) {
		this.pageIndex = pageIndex;
	}

	/**
	 * @return the list
	 */
	public List<T> getList() {
		return list;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(List<T> list) {
		this.list = list;
	}

	public ESPage(Long pageSize, Long pageIndex, List<T> list) {
		super();
		this.pageSize = pageSize;
		this.pageIndex = pageIndex;
		this.list = list;
	}





}
