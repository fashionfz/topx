package utils;


import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.Logger.ALogger;
import play.libs.F.Function;
import play.libs.WS;
import ext.config.ConfigFactory;


public class MessageUtil {
	
	public static String url = "http://sms.hcsdsms.com:8080/smsservice/smsservice.asmx";
	public static String lkUrl = "http://mb345.com:999/ws";
	
	public static String LKUCORPID = ConfigFactory.getString("message.lkucorpID");
	public static String LKUPWD = ConfigFactory.getString("message.lkupwd");
	public static String UCORPID = ConfigFactory.getString("message.ucorpID");
	public static String UPWD = ConfigFactory.getString("message.upwd");
	public static int TIMEOUT = ConfigFactory.getInt("message.timeout");
	private static final ALogger LOGGER = Logger.of(MessageUtil.class);
	private static Map<String,String> BizTypeMap = new HashMap<String,String>();
	
	static{
		BizTypeMap.put("134", "28");
		BizTypeMap.put("135", "28");
		BizTypeMap.put("136", "28");
		BizTypeMap.put("137", "28");
		BizTypeMap.put("138", "28");
		BizTypeMap.put("139", "28");
		BizTypeMap.put("147", "28");
		BizTypeMap.put("150", "28");
		BizTypeMap.put("151", "28");
		BizTypeMap.put("152", "28");
		BizTypeMap.put("157", "28");
		BizTypeMap.put("158", "28");
		BizTypeMap.put("159", "28");
		BizTypeMap.put("182", "28");
		BizTypeMap.put("187", "28");
		BizTypeMap.put("188", "28");
		BizTypeMap.put("133", "16");
		BizTypeMap.put("153", "16");
		BizTypeMap.put("180", "16");
		BizTypeMap.put("181", "16");
		BizTypeMap.put("189", "16");
		BizTypeMap.put("130", "33");
		BizTypeMap.put("131", "33");
		BizTypeMap.put("132", "33");
		BizTypeMap.put("155", "33");
		BizTypeMap.put("156", "33");
		BizTypeMap.put("186", "33");
		BizTypeMap.put("185", "33");
		BizTypeMap.put("145", "33");
	}

	/**
     * 发送群发短信 post请求。
     * 
     * @param Mobile 发送手机号码,多个号码间由半角分号隔开,最多1000个;
     * @param Content 发送内容;
     * @return int 等于0表示提交成功;
     */
	public static int batchSend(String mobile,String content){
		
		if(mobile==null||"".equals(mobile)||content==null||"".equals(content)){
			throw new IllegalArgumentException("输入参数不正确");
		}
		if(mobile != null && mobile.length()!=11){
			throw new IllegalArgumentException("输入手机号码不正确");
		}
	
			int result = lkbatchSend(mobile,content);
			if(result<0){
				result = hcbatchSend(mobile,content);
			}
		return result;
	} 
	/**
     * 发送群发短信 post请求。
     * 
     * @param Mobile 发送手机号码,多个号码间由半角分号隔开,最多1000个;
     * @param Content 发送内容;
     * @return int 等于0表示提交成功;
     */
	public static int hcbatchSend(String mobile,String content){
		
		if(mobile==null&&"".equals(mobile)&&content==null&&"".equals(content)){
			throw new IllegalArgumentException("输入参数不正确");
		}
		
		try{
			
		String currentUrl = url+"/SendEx";
		
		
		StringBuffer postsb = new StringBuffer();
		String bizType = "16";
			postsb.append("UserId=").append(UCORPID).append("&Password=").append(UPWD)
			.append("&DestNumber=").append(mobile).append("&MsgContent=")
			.append(content).append("&BizType=").append(bizType)
			.append("&SendTime=")
			.append("&SubNumber=&BatchSendID=&WapURL=");
			int result =  WS.url(currentUrl).setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
				.post(postsb.toString())
				.map(new Function<WS.Response, Integer>() {
					public Integer apply(WS.Response response) {
						String returnCode = response.getBody().split("<Result>")[1].split("</Result>")[0];
						if(Integer.valueOf(returnCode)!=0){
							LOGGER.error("发送群发短信出错，错误代码为 "+returnCode);
						}
						return Integer.valueOf(returnCode);
					}
				}).get(TIMEOUT);
		
		return result;
		}catch(Exception e){
			e.printStackTrace();
			return -1000;
		}
		
	} 
	
	/**
     * lk发送群发短信 post请求。
     * 
     * @param Mobile 发送手机号码,多个号码间由半角分号隔开,最多1000个;
     * @param Content 发送内容;
     * @return int 大于等于0表示提交成功;
     */
	public static int lkbatchSend(String mobile,String content){
		
		if(mobile==null||"".equals(mobile)||content==null||"".equals(content)){
			throw new IllegalArgumentException("输入参数不正确");
		}
		
		try{
			
		String currentUrl = lkUrl+"/BatchSend.aspx";
		
		StringBuffer postsb = new StringBuffer();
		
		postsb.append("CorpID=").append(LKUCORPID).append("&Pwd=").append(LKUPWD)
		.append("&Mobile=").append(mobile).append("&Content=")
		.append(content);
		
		return WS.url(currentUrl).setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
				.post(postsb.toString())
				.map(new Function<WS.Response, Integer>() {
					public Integer apply(WS.Response response) {
						return Integer.valueOf(response.getBody());
					}
				}).get(TIMEOUT);
		
		
		}catch(Exception e){
			e.printStackTrace();
			return -1000;
		}
		
	} 
	
}
