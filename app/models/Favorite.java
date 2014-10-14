package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;

import play.db.jpa.JPA;

/**
 * @ClassName: Favorite
 * @Description: 专家收藏信息
 */
@Entity
@Table(name = "tb_favorite")
public class Favorite {

    /**
     * 主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    /**
     * 用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    public User user;

    /**
     * 专家
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expertId")
    public Expert expert;
    
	public Favorite() {
		
	}
    
	public Favorite(User user, Expert expert) {
		this.user = user;
		this.expert = expert;
	}

	/**
     * 新增收藏
     * @param favorite
     */
    public static Favorite saveFavorite(Favorite favorite) {
        JPA.em().persist(favorite);
        return favorite;
    }
    
    /**
     * 新增收藏
     * @param user
     * @param expert
     */
    public static void addFavorite(User user,Expert expert){
		if (user == null || expert == null) {
    		throw new IllegalArgumentException("传入的user对象或者expert对象不能为空。");
    	}
    	JPA.em().persist(new Favorite(user,expert));
    }
    
    
    /**
     * 查询某个用户收藏的所有的专家
     * @param user
     * @return
     */
	public static List<Expert> queryFavoriteExpert(Long userId) {
		if (userId == null) {
			throw new IllegalArgumentException("传入的userId不能为空。");
		}
		List<Expert> expertList = JPA.em().createQuery("select f.expert from Favorite f where f.user.id=:userId")
				.setParameter("userId", userId).getResultList();
		return expertList;
	}
	
	/**
	 * 获取某页面的数据
	 * @param page
	 * @param pageSize
	 * @param user
	 * @return
	 */
	public static List<Expert> queryExpertOfCurrentPage(int page,int pageSize,User user){
		if (user == null) {
			throw new IllegalArgumentException("传入的user不能为空。");
		}
		List<Expert> data = JPA.em().createQuery("select f.expert from Favorite f where f.user.id=:userId order by f.expert.id asc")
				.setParameter("userId", user.getId())
				.setFirstResult(page * pageSize).setMaxResults(pageSize).getResultList();
		return data;
	}
	
	/**
	 * 获取用户收藏的专家总数
	 * @param user
	 * @return
	 */
	public static Long queryTotalCount(User user){
		if (user == null) {
			throw new IllegalArgumentException("传入的user不能为空。");
		}
		return (Long)JPA.em().createQuery("select count(f.expert) from Favorite f where f.user.id=:userId")
		.setParameter("userId", user.getId()).getSingleResult();
	}

    /**
     * 根据id删除
     */
    public static void deleteById(Long id) {
    	JPA.em().createQuery("delete from Favorite where id=:id").setParameter("id", id).executeUpdate();
    }
    
    /**
     * 根据userId和expertId删除
     * @param userId 用户id
     * @param expertId 专家id
     */
    public static void deleteByUserIdAndExpertId(Long userId,Long expertId){
    	JPA.em().createQuery("delete from Favorite where user.id=:userId and expert.id=:expertId")
    			.setParameter("userId", userId).setParameter("expertId", expertId).executeUpdate();
    }
    

    /**
     * 判断该用户是否已经收藏该专家
     * @param userId 用户id
     * @param expertId 专家id
     * @return true：已收藏，false：未收藏
     */
    public static Boolean isFavorited(Long userId,Long expertId){
    	List<Favorite> list = JPA.em().createQuery("from Favorite where user.id=:userId and expert.id=:expertId")
    			.setParameter("userId", userId).setParameter("expertId", expertId).getResultList();
		if (CollectionUtils.isEmpty(list)) {
    		return false;
    	}
		return true;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Expert getExpert() {
		return expert;
	}

	public void setExpert(Expert expert) {
		this.expert = expert;
	}
    
}
