/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年5月29日
 */
package ext.translate;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.Logger.ALogger;
import play.libs.WS;
import play.libs.F.Function;

/**
 * @ClassName: translate
 * @Description: 翻译实体类
 * @date 2014年5月29日 上午9:56:15
 * @author RenYouchao
 * 
 */
public class Translate {
	
	private static ALogger LOGGER = Logger.of(Translate.class);

	private static String CLIENT_ID_BAIDU = "dELNc28FKzVHFgR9dGz2r8tZ";

	private static String BAIDU_API = "http://openapi.baidu.com/public/2.0/bmt/translate";
	
	private static String YOUDAO_API = "http://fanyi.youdao.com/openapi.do";
	
	private static String YOUDAO_KEY_FROM = "helome";
	
	private static String YOUDAO_KEY = "1220932392";

	private String from = "auto";

	private String to = "auto";

	private String q;

	private List<String> qs;

	/**
	 * 请描述
	 * 
	 * @param q
	 */
	public Translate(String q) {
		super();
		this.q = q;
		if (StringUtils.isNotBlank(this.q)){
			String[] qsArr = this.q.split("\\n");
			this.qs = Arrays.asList(qsArr);
		}
	}

	private String translateByBaiDu() {
		StringBuffer postsb = new StringBuffer();
		postsb.append("client_id=").append(CLIENT_ID_BAIDU).append("&from=").append(from).append("&to=").append(to).append("&q=").append(q);
		return WS.url(BAIDU_API).post(postsb.toString()).map(new Function<WS.Response, String>() {
			public String apply(WS.Response response) throws UnsupportedEncodingException {
				return response.getBody();
			}
		}).get(20000);
	}
	
	private TranslateVO translateJsonYouDao() {
		TranslateVO tv = new TranslateVO();
		for (String s : qs){
			String responseJson	 = WS.url(YOUDAO_API).setQueryParameter("keyfrom", YOUDAO_KEY_FROM)
						        .setQueryParameter("key", YOUDAO_KEY)
						        .setQueryParameter("type", "data")
		                        .setQueryParameter("doctype", "json")
		                        .setQueryParameter("version", "1.1")
		                        .setQueryParameter("q", s).get().get(20000).getBody();
			LOGGER.debug(responseJson);
			JsonNode jsonNode = play.libs.Json.parse(responseJson);
			if (jsonNode.findPath("errorCode") != null 	&& StringUtils.isNotBlank(jsonNode.findPath("errorCode").asText()) 
													   	&& jsonNode.findPath("errorCode").asText().equals("0")){
				SrcDst srcdst = new SrcDst();
				if (jsonNode.findValue("translation").elements().hasNext())
					srcdst.dst = jsonNode.findValue("translation").elements().next().asText();
				srcdst.src = jsonNode.findValue("query").asText();
				tv.results.add(srcdst);
			} else {
				tv.errorCode = "888";
				tv.errorMsg = "百度翻译与有道翻译均用完或以失效";
			}
		}
		return tv;
	}
	
	public TranslateVO translateAdapt(){
		TranslateVO tlv = this.translateJsonBaidu();
		if (StringUtils.isNotBlank(tlv.errorCode)){
			tlv = this.translateJsonYouDao();
		}
		return tlv;
	}
	
	public TranslateVO translateJsonBaidu(){
		String responseJson = this.translateByBaiDu();
		LOGGER.debug(responseJson);
		JsonNode jsonNode = play.libs.Json.parse(responseJson);
		if (jsonNode.findPath("error_code") != null && StringUtils.isNotBlank(jsonNode.findPath("error_code").asText())){
			TranslateVO tv = new TranslateVO();
			tv.errorCode = jsonNode.findPath("error_code").asText();
			tv.errorMsg = jsonNode.findPath("error_msg").asText();
			return tv;
		} else {
			TranslateVO tv = new TranslateVO();
			List<JsonNode> jns = jsonNode.findValues("trans_result");
			tv.from = jsonNode.findPath("from").asText();
			tv.to = jsonNode.findPath("to").asText();
			for (JsonNode jn :jns){
				SrcDst srcdst = new SrcDst();
				srcdst.dst = jn.findValue("dst").asText();
				srcdst.src = jn.findValue("src").asText();
				tv.results.add(srcdst);
			}
			return tv;
		}
	}
	
	
	

	public static void main(String[] args) {
		Translate translate = new Translate("哈哈");
		System.out.println(translate.translateAdapt());
	}

}
