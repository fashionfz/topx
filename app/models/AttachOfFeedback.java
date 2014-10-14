package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @ClassName: AttachFeedback
 * @Description: 反馈模块对应的附件类
 * <br/> Hibernate继承映射 方式
 */
@Entity
@DiscriminatorValue("feedback")
public class AttachOfFeedback extends Attach {
	
	public AttachOfFeedback() {
	}

	public AttachOfFeedback(String fileName, String path, String suffix,
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
