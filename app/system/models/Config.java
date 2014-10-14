package system.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import ext.config.ConfigFactory;
import play.db.jpa.JPA;


@Entity
@Table(name = "tb_config")
public class Config {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String property;
	
	@Column(length = 4000)
	public String value;
	
	public String introduce;

	/**
	 * 请描述
	 * @param property
	 * @param value
	 */
	public Config(String property, String value, String introduce) {
		super();
		this.property = property;
		this.value = value;
		this.introduce = introduce;
	}
	
	
	public Config(String property, String value) {
		super();
		this.property = property;
		this.value = value;
	}
	
	public Config(){
		super();
	}
	
	public void saveOrUpdate(){
		if (this.id == null){
			JPA.em().persist(this);
		} else {
			JPA.em().merge(this);
		}
		ConfigFactory.setString(this.property, this.value);
	}
	/**
	 * @return
	 */
	public List<Config> findConfigAll(){
		return (List<Config>)JPA.em().createQuery("from Config order by id desc").getResultList();
	}
	
	public Config findConfigByKey(String key){
		List<Config> configs = JPA.em().createQuery("from Config where property = :property").setParameter("property", key).getResultList();
		if (CollectionUtils.isNotEmpty(configs)){
			return configs.get(0);
		} else {
			return new Config();
		}
	}
	
	public Config findById(Long id){
		return (Config)JPA.em().find(Config.class, id);
	}
	
	public void deleteById(Long id){
		Config config = this.findById(id);
	    ConfigFactory.deleteString(config.property);
		JPA.em().remove(config);
	}
	
}
