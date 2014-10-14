package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @ClassName: AttachSuggestion
 * @Description: 建议模块对应的附件类
 * <br/> Hibernate继承映射 方式
 */
@Entity
@DiscriminatorValue("suggestion")
public class AttachOfSuggestion extends Attach {
	
	public AttachOfSuggestion() {
	}

	public AttachOfSuggestion(String fileName, String path, String suffix,
			BigDecimal size, Date createDate, Long createUserId,
			String createUserName, Boolean isDelete) {
		this.fileName = fileName;
		this.path = path;
		this.suffix = suffix;
		this.size = size;
		this.createDate = createDate;
		this.createUserId = createUserId;
		this.createUserName = createUserName;
		this.isDelete = isDelete;
	}
	
}
