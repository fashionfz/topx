package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import models.SkillTag;
import models.User;
import models.service.ForgetPasswordService;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import system.models.Keyword;
import utils.HelomeUtil;
import utils.TimeZoneUtils;

import views.html.user.login;
import views.html.user.register;
import views.html.user.regsuccess;
import views.html.user.forgetpwd;
import views.html.user.forgetpwdSendsuccess;
import views.html.user.resetpwd;
import views.html.user.resetsuccess;
import views.html.user.resetpwdValidFailure;
import views.html.user.loginskip;

import views.html.aboutus;
import views.html.aboutme2me;
import views.html.groupAgreement;
import views.html.frequentlyQuestions;
import views.html.index;
import views.html.legalnotice;
import views.html.regagreement;
import views.html.serviceagreement;
import views.html.tradeprocess;
import views.html.feedback;
import views.html.wap;
import views.html.phone;
import views.html.qrcodeTransfer;
import vo.TopCate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import common.Constants;

import ext.usercenter.UserAuthService;

public class Application extends Controller {
	
	/**
	 * 热门标签 最多获取10个
	 */
	private static final int KWMAXCOUNT = 10;

	@Transactional(readOnly = true)
	public static Result index() {
		User user = User.getFromSession(session());
		List<TopCate> cates = SkillTag.getTopCateWithCache();
//		List<Keyword> keywordList = Keyword.queryKeywordList();
		// 获取热门标签 最多获取10个
		List<String> keywords = new ArrayList<String>();
		String[] arys = ExpertApp.queryKeywords();
		if (arys.length > 0) {
			if (arys.length > KWMAXCOUNT) {
				for (int i = 0; i < KWMAXCOUNT; i++) {
					if (StringUtils.isNotBlank(arys[i])) {
						keywords.add(arys[i].trim());
					}
				}
			} else {
				for (int i = 0; i < arys.length; i++) {
					if (StringUtils.isNotBlank(arys[i])) {
						keywords.add(arys[i].trim());
					}
				}
			}
		}
		
        return ok(index.render(cates, user,keywords));
	}

	/**
	 * 注册页面
	 * 
	 * @return
	 */
	public static Result register() {
		return ok(register.render());
	}

    /**
     * 邀请注册页面
     *
     * @return
     */
    public static Result invitedRegister(String uid) {
        return ok(register.render());
    }

    /**
     * 关于我们页面
     *
     * @return
     */
    public static Result aboutus() {
        return ok(aboutus.render());
    }
    /**
     * 关于me2me
     *
     * @return
     */
    public static Result aboutMeToMe() {
        return ok(aboutme2me.render());
    }
    /**
     * 反馈建议
     *
     * @return
     */
    public static Result feedback() {
        return ok(feedback.render());
    }

    /**
     * 手机端下载页面
     * @return
     */
    public static Result wap() {
        return ok(wap.render());
    }

    /**
     * 客户端下载页面
     * @return
     */
    public static Result phone() {
        return ok(phone.render());
    }

    /**
     * 手机端下载中转页面
     * @return
     */
    public static Result qrcodeTransfer() {
        return ok(qrcodeTransfer.render());
    }

    /**
     * 常见问题页面
     *
     * @return
     */
    public static Result frequentlyQuestions() {
        return ok(frequentlyQuestions.render());
    }
    
	 /**
     * 交易流程页面
     *
     * @return
     */
    public static Result tradeprocess() {
        return ok(tradeprocess.render());
    }

    /**
     * 法律声明页面
     *
     * @return
     */
    public static Result legalnotice() {
        return ok(legalnotice.render());
    }

    /**
     * 注册协议页面
     *
     * @return
     */
    public static Result regAgreement() {
        return ok(regagreement.render());
    }

    /**
     * 充值、提现及转账服务协议页面
     *
     * @return
     */
    public static Result serviceAgreement() {
        return ok(serviceagreement.render());
    }

	/**
	 * 注册成功页面
	 * 
	 * @return
	 */
	public static Result regsuccess() {
		return ok(regsuccess.render(User.getFromSession(session())));
	}

	/**
	 * 登录页面
	 * 
	 * @return
	 */
	public static Result login() {
		if (UserAuthService.isLogin(session())) {
			return redirect(routes.Application.index());
		}
		String msg = "";
		if (UserAuthService.isKicked(session())) {
			msg = "你的帐号在别处登录了！如非本人操作，请注意帐号安全。";
			UserAuthService.removeKickedFlag(session());
		} else if ("1".equals(request().getQueryString("msg"))) {
			msg = "该操作需要登录，请先登录！";
		}
		return ok(login.render(msg));
	}
	
	/**
	 * 登陆禁用页面
	 * 
	 * @return
	 */
	public static Result loginskip() {
		return ok(loginskip.render());
	}

	/**
	 * 找回密码页面
	 * 
	 * @return
	 */
	public static Result forgetpwd() {
		return ok(forgetpwd.render());
	}

	/**
	 * 找回密码邮件发送成功页面
	 * 
	 * @return
	 */
	public static Result sendsuccess() {
		return ok(forgetpwdSendsuccess.render());
	}

	/**
	 * 重置密码页面
	 * 
	 * @return
	 */
	@Transactional
	public static Result resetpwd(String email, String code) {
		// 判断重置密码的链接的加密串是否有效，是否合法
		ObjectNode result = ForgetPasswordService.isLinkValid(email,code);
		if ("false".equals(result.get("result").asText())) {
            // 跳转到校验不通过的页面
            return ok(resetpwdValidFailure.render(email,result.get("error")==null?"对不起，该链接是无效的链接！":result.get("error").asText())); 
        }
		return ok(resetpwd.render(email, code));
	}
	
	/**
	 * 重置密码校验失败
	 * @return
	 */
	public static Result resetpwdValidFailure(String email, String code){
		return ok(resetpwdValidFailure.render(email, code));
	}

	/**
	 * 重置密码成功页面
	 * 
	 * @return
	 */
	public static Result resetsuccess() {
		return ok(resetsuccess.render());
	}

	/**
	 * 获得客户端的时区偏移量存到session中
	 * 
	 * @return
	 */
	public static Result setTimeZoneOffset2Session() {
		String timeZoneOffsetStr = request().getQueryString("offset");
		Integer timeZoneOffset = HelomeUtil.toInteger(timeZoneOffsetStr, null);
		if (null != timeZoneOffset) {
			TimeZoneUtils.setTimeZoneOffset2Session(session(), -timeZoneOffset);
		}
		return ok(timeZoneOffsetStr);
	}

    /**
    * 群组服务协议页面
    *
    * @return
    */
    public static Result groupAgreement() {
    return ok(groupAgreement.render());
    }

}
