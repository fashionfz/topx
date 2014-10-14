package vo;

import utils.Assets;
import models.Attach;

/**
 * 附件VO
 */
public class AttachmentVO {

	/** Attach的id */
	private Long attachId;

	/** 附件的路径 */
	private String path;

	/** 附件的路径（可访问的全路径） */
	private String pathsource;

	/** 附件文件的后缀 */
	private String suffix;
	
	/** 附件的名称 */
	private String fileName;

	public Long getAttachId() {
		return attachId;
	}

	public void setAttachId(Long attachId) {
		this.attachId = attachId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPathsource() {
		return pathsource;
	}

	public void setPathsource(String pathsource) {
		this.pathsource = pathsource;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void convert(Attach attach) {
		this.attachId = attach.id;
		this.path = attach.path;
		this.pathsource = Assets.at(attach.path);
		this.suffix = attach.suffix;
		this.fileName = attach.fileName;
	}

}
