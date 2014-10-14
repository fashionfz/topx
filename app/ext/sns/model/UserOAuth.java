package ext.sns.model;

import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import models.User;

/**
 * 
 * 
 * 
 * @ClassName: UserOAuth
 * @Description: 用户第三方OAuth鉴权信息
 * @date 2013-11-25 上午10:55:43
 * @author ShenTeng
 * 
 */
@javax.persistence.Entity
@Table(name = "tb_user_oauth", uniqueConstraints = { @UniqueConstraint(columnNames = { "provider", "userid" }) })
public class UserOAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    /**
     * 提供商名字
     */
    public String provider;

    /**
     * 关联用户
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid")
    public User user;

    /**
     * 授权凭证
     */
    public String token;

    /**
     * 刷新授权的凭证
     */
    public String refreshToken;

    /**
     * 过期时间(单位:秒)
     */
    public Long expiredIn;

    /**
     * 授权用户在第三方系统的Id，该Id可能用于调用第三方的其他接口
     */
    public String openId;

    /**
     * 用户昵称
     */
    public String nickname;

    /**
     * 头像URL
     */
    public String avatarUrl;

    /**
     * 创建时间
     */
    public Date createTime = new Date();

    /**
     * 刷新时间
     */
    public Date refreshTime = new Date();

    /**
     * token是否有效
     * 
     * @return true - token有效， false - token无效
     */
    public boolean isValid() {
        Long now = System.currentTimeMillis();
        Long elapseInSecond = (now - refreshTime.getTime()) / 1000;

        return elapseInSecond < expiredIn;
    }

    /**
     * 判断是否关联了用户
     * 
     * @return
     */
    public boolean isBindUser() {
        return user != null;
    }

    @Override
    public String toString() {
        return "UserOAuth [id=" + id + ", provider=" + provider + ", user=" + user + ", token=" + token
                + ", refreshToken=" + refreshToken + ", expiredIn=" + expiredIn + ", openId=" + openId + ", nickname="
                + nickname + ", avatarUrl=" + avatarUrl + ", createTime=" + createTime + ", refreshTime=" + refreshTime
                + "]";
    }

}
