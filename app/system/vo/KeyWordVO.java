package system.vo;

import utils.ExcelField;

public class KeyWordVO implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ExcelField(lableName="关键字")
	private String word;
	@ExcelField(lableName="统计次数")
	private int searchNum;
	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}
	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}
	/**
	 * @return the searchNum
	 */
	public int getSearchNum() {
		return searchNum;
	}
	/**
	 * @param searchNum the searchNum to set
	 */
	public void setSearchNum(int searchNum) {
		this.searchNum = searchNum;
	}
	
}
