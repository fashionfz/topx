/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月22日
 */
package common;

import static play.Play.application;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import models.service.reminder.Item;
import controllers.base.ObjectNodeResult;

/**
 *
 *
 * @ClassName: Constants
 * @Description: 常量类
 * @date 2013年10月22日 上午11:06:29
 * @author RenYouchao
 * 
 */
public class Constants {
	
	
	public static SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	public static SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHssmm");
	
	
	public static DecimalFormat dformat = new DecimalFormat("#0.0");
	
	public static Map<String, String> countryPicKV = new HashMap<String, String>();
	//public static final String fLAG_PRE = "topx/assets/misc/images/flag/";
	
	static {
		/*countryPicKV.put("中国", "flag-186");
		countryPicKV.put("美国", "145.gif");
		countryPicKV.put("日本", "020.gif");
		countryPicKV.put("加拿大", "176.gif");
		countryPicKV.put("英国", "217.gif");*/
		countryPicKV.put("阿布哈兹", "flag-001");
		countryPicKV.put("阿迪格", "flag-002");
		countryPicKV.put("阿富汗", "flag-003");
		countryPicKV.put("阿扎尔", "flag-004");
		countryPicKV.put("阿尔巴尼亚", "flag-006");
		countryPicKV.put("阿尔及利亚", "flag-008");
		countryPicKV.put("加拿大", "flag-009");
		countryPicKV.put("佛得角", "flag-010");
		countryPicKV.put("开曼群岛", "flag-011");
		countryPicKV.put("中非", "flag-012");
		countryPicKV.put("乍得", "flag-013");
		countryPicKV.put("车臣", "flag-015");
		countryPicKV.put("智利", "flag-016");
		countryPicKV.put("格鲁吉亚", "flag-017");
		countryPicKV.put("德国", "flag-018");
		countryPicKV.put("加纳", "flag-019");
		countryPicKV.put("希腊", "flag-021");
		countryPicKV.put("格陵兰岛", "flag-022");
		countryPicKV.put("格林纳达", "flag-023");
		countryPicKV.put("老挝", "flag-026");
		countryPicKV.put("拉脱维亚", "flag-027");
		countryPicKV.put("黎巴嫩", "flag-028");
		countryPicKV.put("莱索托", "flag-029");
		countryPicKV.put("利比里亚", "flag-030");
		countryPicKV.put("利比亚", "flag-031");
		countryPicKV.put("列支敦士登", "flag-032");
		countryPicKV.put("挪威", "flag-033");
		countryPicKV.put("阿曼", "flag-034");
		countryPicKV.put("巴基斯坦", "flag-035");
		countryPicKV.put("帕劳", "flag-036");
		countryPicKV.put("巴勒斯坦", "flag-037");
		countryPicKV.put("巴拿马", "flag-038");
		countryPicKV.put("巴布亚新几内亚", "flag-039");
		countryPicKV.put("巴拉圭", "flag-040");
		countryPicKV.put("XXX", "flag-041");
		countryPicKV.put("西班牙", "flag-042");
		countryPicKV.put("斯里兰卡", "flag-043");
		countryPicKV.put("苏丹", "flag-044");
		countryPicKV.put("苏里南", "flag-045");
		countryPicKV.put("斯威士兰", "flag-046");
		countryPicKV.put("瑞典", "flag-047");
		countryPicKV.put("瑞士", "flag-048");
		countryPicKV.put("阿尔泰共和国", "flag-049");
		countryPicKV.put("XXXX", "flag-050");
		countryPicKV.put("安道尔", "flag-051");
		countryPicKV.put("安哥拉", "flag-052");
		countryPicKV.put("XXXX", "flag-053");
		countryPicKV.put("XXXX", "flag-054");
		countryPicKV.put("安提瓜和巴布达", "flag-055");
		countryPicKV.put("阿根廷", "flag-056");
		countryPicKV.put("中国", "flag-057");
		countryPicKV.put("XXXX", "flag-058");
		countryPicKV.put("楚瓦什", "flag-059");
		countryPicKV.put("哥伦比亚", "flag-060");
		countryPicKV.put("科摩罗", "flag-061");
		countryPicKV.put("刚果（金）", "flag-062");
		countryPicKV.put("刚果（布）", "flag-063");
		countryPicKV.put("库克群岛", "flag-064");
		countryPicKV.put("危地马拉", "flag-065");
		countryPicKV.put("XXXX", "flag-066");
		countryPicKV.put("几内亚比绍", "flag-067");
		countryPicKV.put("几内亚", "flag-068");
		countryPicKV.put("圭亚那", "flag-069");
		countryPicKV.put("海地", "flag-070");
		countryPicKV.put("XXXX", "flag-071");
		countryPicKV.put("洪都拉", "flag-072");
		countryPicKV.put("立陶宛", "flag-073");
		countryPicKV.put("XXXX", "flag-074");
		countryPicKV.put("卢森堡", "flag-075");
		countryPicKV.put("XXXX", "flag-076");
		countryPicKV.put("马其顿", "flag-077");
		countryPicKV.put("马达加斯加", "flag-078");
		countryPicKV.put("马拉维", "flag-079");
		countryPicKV.put("马来西亚", "flag-080");
		countryPicKV.put("秘鲁", "flag-081");
		countryPicKV.put("菲律宾", "flag-082");
		countryPicKV.put("XXXX", "flag-083");
		countryPicKV.put("波兰", "flag-084");
		countryPicKV.put("葡萄牙", "flag-085");
		countryPicKV.put("波多黎各", "flag-086");
		countryPicKV.put("卡塔尔", "flag-087");
		countryPicKV.put("波黑", "flag-088");
		countryPicKV.put("叙利亚", "flag-089");
		countryPicKV.put("XXXX", "flag-090");
		countryPicKV.put("塔吉克斯坦", "flag-091");
		countryPicKV.put("坦桑尼亚", "flag-092");
		countryPicKV.put("鞑靼斯坦", "flag-093");
		countryPicKV.put("泰国", "flag-094");
		countryPicKV.put("XXXX", "flag-095");
		countryPicKV.put("多戈", "flag-096");
		countryPicKV.put("亚美尼亚", "flag-097");
		countryPicKV.put("XXXX", "flag-098");
		countryPicKV.put("澳大利亚", "flag-099");
		countryPicKV.put("奥地利", "flag-100");
		countryPicKV.put("阿塞拜疆", "flag-101");
		countryPicKV.put("巴哈马", "flag-102");
		countryPicKV.put("巴林", "flag-103");
		countryPicKV.put("孟加拉国", "flag-104");
		countryPicKV.put("哥斯达黎加", "flag-105");
		countryPicKV.put("科特迪瓦", "flag-106");
		countryPicKV.put("XXXX", "flag-107");
		countryPicKV.put("克罗地亚", "flag-108");
		countryPicKV.put("古巴", "flag-109");
		countryPicKV.put("塞浦路斯", "flag-110");
		countryPicKV.put("捷克", "flag-111");
		countryPicKV.put("达吉斯坦", "flag-112");
		countryPicKV.put("XXXX", "flag-113");
		countryPicKV.put("匈牙利", "flag-114");
		countryPicKV.put("冰岛", "flag-115");
		countryPicKV.put("印度", "flag-116");
		countryPicKV.put("印度尼西亚", "flag-117");
		countryPicKV.put("印古什", "flag-118");
		countryPicKV.put("马绍尔群岛", "flag-119");
		countryPicKV.put("毛里塔尼亚", "flag-120");
		countryPicKV.put("毛里求斯", "flag-121");
		countryPicKV.put("墨西哥", "flag-122");
		countryPicKV.put("罗马尼亚", "flag-123");
		countryPicKV.put("俄罗斯", "flag-124");
		countryPicKV.put("卢旺达", "flag-125");
		countryPicKV.put("XXXX", "flag-126");
		countryPicKV.put("XXXX", "flag-127");
		countryPicKV.put("XXXX", "flag-128");
		countryPicKV.put("圣基茨和尼维斯", "flag-129");
		countryPicKV.put("圣卢西亚", "flag-130");
		countryPicKV.put("突尼斯", "flag-131");
		countryPicKV.put("土耳其", "flag-132");
		countryPicKV.put("XXXX", "flag-133");
		countryPicKV.put("托克劳群岛", "flag-134");
		countryPicKV.put("汤加", "flag-135");
		countryPicKV.put("XXXX", "flag-136");
		countryPicKV.put("伊朗", "flag-137");
		countryPicKV.put("伊拉克", "flag-138");
		countryPicKV.put("马尔代夫", "flag-139");
		countryPicKV.put("马里", "flag-140");
		countryPicKV.put("马耳他", "flag-141");
		countryPicKV.put("XXXX", "flag-142");
		countryPicKV.put("特立尼达和多巴哥", "flag-143");
		countryPicKV.put("特立", "flag-143");
		countryPicKV.put("XXXX", "flag-144");
		countryPicKV.put("巴巴多斯", "flag-145");
		countryPicKV.put("XXXX", "flag-146");
		countryPicKV.put("白俄罗斯", "flag-147");
		countryPicKV.put("比利时", "flag-148");
		countryPicKV.put("伯利兹", "flag-149");
		countryPicKV.put("贝宁", "flag-150");
		countryPicKV.put("XXXX", "flag-151");
		countryPicKV.put("不丹", "flag-152");
		countryPicKV.put("丹麦", "flag-153");
		countryPicKV.put("吉布提", "flag-154");
		countryPicKV.put("多米尼克", "flag-155");
		countryPicKV.put("多米尼加", "flag-156");
		countryPicKV.put("东帝汶", "flag-157");
		countryPicKV.put("XXXX", "flag-158");
		countryPicKV.put("厄瓜多尔", "flag-159");
		countryPicKV.put("埃及", "flag-160");
		countryPicKV.put("爱尔兰", "flag-161");
		countryPicKV.put("XXXX", "flag-162");
		countryPicKV.put("以色列", "flag-163");
		countryPicKV.put("意大利", "flag-164");
		countryPicKV.put("牙买加", "flag-165");
		countryPicKV.put("XXXX", "flag-166");
		countryPicKV.put("日本", "flag-167");
		countryPicKV.put("XXXX", "flag-168");
		countryPicKV.put("密克罗尼西亚", "flag-169"); 
		countryPicKV.put("摩尔多瓦", "flag-170");
		countryPicKV.put("摩纳哥", "flag-171");
		countryPicKV.put("蒙古国", "flag-172");
		countryPicKV.put("黑山", "flag-173");
		countryPicKV.put("XXXX", "flag-174");
		countryPicKV.put("XXXX", "flag-175");
		countryPicKV.put("摩洛哥", "flag-176");
		countryPicKV.put("XXXX", "flag-177");
		countryPicKV.put("XXXX", "flag-178");
		countryPicKV.put("XXXX", "flag-179");
		countryPicKV.put("XXXX", "flag-180");
		countryPicKV.put("萨摩亚", "flag-181");
		countryPicKV.put("圣马力诺", "flag-182");
		countryPicKV.put("圣多美和普林西比", "flag-183");
		countryPicKV.put("XXXX", "flag-184");
		countryPicKV.put("土库曼斯坦", "flag-185");
		countryPicKV.put("XXXX", "flag-186");
		countryPicKV.put("XXXX", "flag-187");
		countryPicKV.put("图瓦卢", "flag-188"); 
		countryPicKV.put("XXXX", "flag-189");
		countryPicKV.put("乌干达", "flag-190");
		countryPicKV.put("乌克兰", "flag-191");
		countryPicKV.put("阿联酋", "flag-192");
		countryPicKV.put("XXXX", "flag-193");
		countryPicKV.put("玻利维亚", "flag-194");
		countryPicKV.put("XXXX", "flag-195");
		countryPicKV.put("波黑", "flag-196");
		countryPicKV.put("博茨瓦纳", "flag-194");
		countryPicKV.put("巴西", "flag-198");
		countryPicKV.put("XXXX", "flag-199");
		countryPicKV.put("XXXX", "flag-200");
		countryPicKV.put("萨尔瓦多", "flag-201");
		countryPicKV.put("英格兰", "flag-202");
		countryPicKV.put("赤道几内亚", "flag-203");
		countryPicKV.put("厄立特里亚", "flag-204");
		countryPicKV.put("爱沙尼亚", "flag-205");
		countryPicKV.put("埃塞俄比亚", "flag-206");
		countryPicKV.put("XXXX", "flag-207");
		countryPicKV.put("XXXX", "flag-208");
		countryPicKV.put("约旦", "flag-209");
		countryPicKV.put("XXXX", "flag-210");
		countryPicKV.put("XXXX", "flag-211");
		countryPicKV.put("XXXX", "flag-212");
		countryPicKV.put("XXXX", "flag-213");
		countryPicKV.put("XXXX", "flag-214");
		countryPicKV.put("哈萨克斯坦", "flag-215");
		countryPicKV.put("肯尼亚", "flag-216");
		countryPicKV.put("莫桑比克", "flag-217");
		countryPicKV.put("缅甸", "flag-218");
		countryPicKV.put("XXXX", "flag-219");
		countryPicKV.put("纳米比亚", "flag-220");
		countryPicKV.put("瑙鲁", "flag-221");
		countryPicKV.put("尼泊尔", "flag-222");
		countryPicKV.put("XXXX", "flag-223");
		countryPicKV.put("荷兰", "flag-224");
		countryPicKV.put("沙特阿拉伯", "flag-225");
		countryPicKV.put("XXXX", "flag-226");
		countryPicKV.put("塞内加尔", "flag-227");
		countryPicKV.put("塞尔维亚", "flag-228");
		countryPicKV.put("塞舌尔", "flag-229");
		countryPicKV.put("塞拉利昂", "flag-230");
		countryPicKV.put("锡金", "flag-231");
		countryPicKV.put("新加坡", "flag-232");
		countryPicKV.put("英国", "flag-233");
		countryPicKV.put("美国", "flag-234");
		countryPicKV.put("乌拉圭", "flag-235");
		countryPicKV.put("乌兹别克斯坦", "flag-236");
		countryPicKV.put("瓦努阿图", "flag-237");
		countryPicKV.put("梵蒂冈", "flag-238");
		countryPicKV.put("委内瑞拉", "flag-239");
		countryPicKV.put("越南", "flag-240");
		countryPicKV.put("XXXX", "flag-241");
		countryPicKV.put("XXXX", "flag-242");
		countryPicKV.put("保加利亚", "flag-243");
		countryPicKV.put("布基纳法索", "flag-244");
		countryPicKV.put("布隆迪", "flag-245");
		countryPicKV.put("XXXX", "flag-246");
		countryPicKV.put("柬埔寨", "flag-247");
		countryPicKV.put("喀麦隆", "flag-248");
		countryPicKV.put("XXXX", "flag-249");
		countryPicKV.put("斐济", "flag-250"); 
		countryPicKV.put("芬兰", "flag-251");
		countryPicKV.put("法国", "flag-252");
		countryPicKV.put("XXXX", "flag-253");
		countryPicKV.put("XXXX", "flag-254");
		countryPicKV.put("加蓬", "flag-255");
		countryPicKV.put("冈比亚", "flag-256");
		countryPicKV.put("XXXX", "flag-257");
		countryPicKV.put("基里巴斯", "flag-258"); 
		countryPicKV.put("XXXX", "flag-259");
		countryPicKV.put("朝鲜", "flag-260");
		countryPicKV.put("韩国", "flag-261");
		countryPicKV.put("科索沃", "flag-262");
		countryPicKV.put("科威特", "flag-263");
		countryPicKV.put("吉尔吉斯斯坦", "flag-264");
		countryPicKV.put("新西兰", "flag-265");
		countryPicKV.put("尼加拉瓜", "flag-266");
		countryPicKV.put("尼日尔", "flag-267");
		countryPicKV.put("尼日利亚", "flag-268");
		countryPicKV.put("纽埃", "flag-269"); 
		countryPicKV.put("XXXX", "flag-270");
		countryPicKV.put("XXXX", "flag-271");
		countryPicKV.put("XXXX", "flag-272");
		countryPicKV.put("斯洛伐克", "flag-273");
		countryPicKV.put("斯洛文尼亚", "flag-274");
		countryPicKV.put("所罗门群岛", "flag-275");
		countryPicKV.put("索马里", "flag-276");
		countryPicKV.put("索马里兰", "flag-277");
		countryPicKV.put("南非", "flag-278");
		countryPicKV.put("XXXX", "flag-279");
		countryPicKV.put("南奥塞梯", "flag-280");
		countryPicKV.put("XXXX", "flag-281");
		countryPicKV.put("XXXX", "flag-282");
		countryPicKV.put("威尔士", "flag-283");
		countryPicKV.put("西撒哈拉", "flag-284");
		countryPicKV.put("也门", "flag-285");
		countryPicKV.put("赞比亚", "flag-286");
		countryPicKV.put("津巴布韦", "flag-287");
	}
	
	
	public static final String PIC_PREFIX = "";
	
	public static final String MC_WEBSOCKET_HOST = "mc.websocket.host";
	
	public static final String MC_WEBSOCKET_PORT = "mc.websocket.port";
	
	public static final String CONFIG_MESSAGE_IP_LOCAL_KEY = "message.ip.local";
	
	public static final int HOME_EXPERT_PAGE_SIZE = 10;
	
	public static final int HOME_EXPERT_PER_NUM = 3;
	
	public static final int HOME_SKILL_TAG_SIZE = 7;
	
	public static final String SEARCH_FILTER_KEY = "country|all,serveState|all,expenses|all,gender|all";
	
	public static final int SUCESS = ObjectNodeResult.STATUS_SUCCESS;
	/**
	 * 交易号长度， 默认12位
	 */
	public static final int MERCHANDISE_NUMBER_LENGTH = 12;
	/**
	 * 定金收取比例
	 */
	public static final double EARNEST_MONEY_RATE = 0.05;
	
	/**
	 * 咨询后多少天内未评价，系统进行默认好评
	 */
	public static final int DEFAULT_COMMENT_DAYS = 8;
	
	/**
	 * 咨询后多少小时内未评价，系统进行默认好评
	 */
	public static final int DEFAULT_COMMENT_HOURS = 1;
	
	/**
	 * 分享到第三方平台的最大字符限制
	 */
	public static final int SHARE_MAX_CHARE_LENGTH = 135;
	
	/**
	 * 缓存前缀
	 */
	public static final String CACHE_EXPERT_TOPS = "tops";
	
	public static final String CACHE_COUNTRY = "countrys";
	
	public static final String CACHE_KEYWORDS = "kw";
	
	public static final String FONT_REGEX = "<[^>].*?>";
	
	
	
	public static final String CACHE_INDUSTRY = "skilltag.industry";
	
	/**
	 * 敏感字符
	 */
	public static final String CACHE_SENSITIVE_WORDS = "sensitive_words";
	
	public static final String CACHE_CAPTCHA = "cp";
	
	public static final String CACHE_MSG_NEWCOUNT = new String("msg.newcount.");
	/**登录token-uid缓存前缀**/
	public static final String CACHE_LOGIN_TOKEN_UID = "loginSessionToken_";
	/**登录用户缓存前缀**/
	public static final String CACHE_LOGIN_USER = "userCache_";
	/**手机验证码**/
	public static final String CACHE_PHONE_VERIFY_CODE = "phoneVerifyCode_";
	/**预约提醒记录**/
	public static final String CACHE_BOOKING_REMIND_REC = "bkRmRec_";
	/**移动端行业图片**/
	public static final String CACHE_INDUSTRY_IMG = "industryImg_";
	/**移动端首页图片**/
	public static final String CACHE_INTRO_IMG = "introImg_";
	/**根据邮箱修改手机号**/
	public static final String CACHE_CHANGE_PHONE_BY_EMAIL_KEY_CU = "cgPhoneByEmailCU_";
	public static final String CACHE_CHANGE_PHONE_BY_EMAIL_KEY_UC = "cgPhoneByEmailUC_";
	/**UserOAuth ID*/
	public static final String CACHE_USEROAUTH_ID = "userOAuthId_";
	
	/**保持活动消息**/
	public static final String MSG_KEEP_LIVE = "message.keep.live";
	/**轮询检查是否活跃消息**/
	public static final String MSG_KEEP_CHECK = "message.keep.check";
	/**新数量查看消息**/
	public static final String MSG_COUNT_NEW = "message.count.new";
	/**实时咨询消息**/
	public static final String MSG_CONSULT_NOW = "message.consult.now";
	/**清除实时咨询消息**/
	public static final String MSG_CONSULT_CLEAR = "message.consult.clear";
	
	public static final String IP_LOCAL = application().configuration().getString(Constants.CONFIG_MESSAGE_IP_LOCAL_KEY);
	
	public static final String default_head = "misc/images/dev-head-default1.png";
	
	public static final Item[] safetyReminderItems = new Item[] { Item.CHANGE_PASSWORD, Item.CHANGE_PHONE_NUM,
        Item.DIFFERENT_PLACE_LOGIN };
	
	public static final Item[] bookingReminderItems = new Item[] { Item.BOOKING_REMIND};
	
	/**个人作品图最大个数**/
	public static final int PORTFOLIO_PhOTO_MAX_NUM = 20;
	
	
	/**
	 * mobile APK升级版本信息（包含version,versionName,应用下载地址）
	 */
	public static final String APK_VERSION_INFO = "mobile.apk.version";
	
	/**
	 * 记住我cookie
	 */
	public static final String COOKIE_REMEMBER_ME = "_rm";
	
	/**
	 * 用户信息相关cookie
	 */
	public static final String COOKIE_USERINFO_ID = "_u_id";
	public static final String COOKIE_USERINFO_NAME = "_u_nm";
	public static final String COOKIE_USERINFO_TOKEN = "_u_tk";

}
