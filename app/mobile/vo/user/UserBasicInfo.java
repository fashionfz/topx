package mobile.vo.user;

import mobile.vo.MobileVO;
import models.User;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * 
 * 
 * @ClassName: UserBasicInfo
 * @Description: 用户基本信息
 * @date 2014-4-4 下午5:16:08
 * @author ShenTeng
 * 
 */
public class UserBasicInfo implements MobileVO {

    /** 用户Id */
    private Long userId;

    /** 用户姓名 **/
    private String name;

    /** 190x190头像地址 */
    private String avatar_190;

    /** 70x70头像地址 */
    private String avatar_70;

    /** 22x22头像地址 */
    private String avatar_22;

    private UserBasicInfo() {
    };

    public static UserBasicInfo create(User user) {
        if (null == user) {
            return null;
        }

        UserBasicInfo basic = new UserBasicInfo();
        basic.setUserId(user.id);
        basic.setName(user.getName());
        basic.setAvatar_190(user.getAvatar(190));
        basic.setAvatar_70(user.getAvatar(70));
        basic.setAvatar_22(user.getAvatar(22));

        return basic;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar_190() {
        return avatar_190;
    }

    public void setAvatar_190(String avatar_190) {
        this.avatar_190 = avatar_190;
    }

    public String getAvatar_70() {
        return avatar_70;
    }

    public void setAvatar_70(String avatar_70) {
        this.avatar_70 = avatar_70;
    }

    public String getAvatar_22() {
        return avatar_22;
    }

    public void setAvatar_22(String avatar_22) {
        this.avatar_22 = avatar_22;
    }

}
