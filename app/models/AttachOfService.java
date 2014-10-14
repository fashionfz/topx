package models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @ClassName: AttachService
 * @Description: 服务模块对应的附件类
 * <br/> Hibernate继承映射 方式
 */
@Entity
@DiscriminatorValue("service")
public class AttachOfService extends Attach {
	
	public AttachOfService() {
	}
	
	public AttachOfService(String fileName, String path, String suffix,
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
