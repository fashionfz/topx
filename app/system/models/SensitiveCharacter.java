package system.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName: Keyword
 * @Description: 关键字实体类
 */
@Entity
@Table(name = "tb_sensitive_character")
public class SensitiveCharacter {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String words;

	public SensitiveCharacter() {
		super();
	}

	public SensitiveCharacter(Long id, String words) {
		super();
		this.id = id;
		this.words = words;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

}
