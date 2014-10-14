package vo;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import utils.SemUtils;

/**
 * @ClassName: STagVo
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2013年12月27日 上午10:10:27
 * @author RenYouchao
 * 
 */
public class STagVo {
	
	ScriptEngine engine = SemUtils.getEngine();
	
	private Long sid;
	
	private String tag;
	
	private String noMarkedTag;
	
	private Boolean isMarked = false;

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return the noMarkedTag
	 */
	public String getNoMarkedTag() {
		String noMared = null;
		try {
			noMared = engine.eval("encodeURIComponent(\"" + noMarkedTag + "\")").toString();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return noMared;
	}
	
	public String getNoEncodeTag() {
		return noMarkedTag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @param noMarkedTag the noMarkedTag to set
	 */
	public void setNoMarkedTag(String noMarkedTag) {
		this.noMarkedTag = noMarkedTag;
	}

	/**
	 * @return the isMarked
	 */
	public Boolean getIsMarked() {
		return isMarked;
	}

	/**
	 * @param isMarked the isMarked to set
	 */
	public void setIsMarked(Boolean isMarked) {
		this.isMarked = isMarked;
	}

	/**
	 * @return the sid
	 */
	public Long getSid() {
		return sid;
	}

	/**
	 * @param sid the sid to set
	 */
	public void setSid(Long sid) {
		this.sid = sid;
	}
}
