package ext.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import play.Logger;
import play.Logger.ALogger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.jackjson.JackJsonUtil;
import ext.config.ConfigFactory;

public class SearchHttpClient {

	static ALogger logger = Logger.of(SearchHttpClient.class);


	public static Boolean createOrUpdateDocument(List<NameValuePair> nvps) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);// 连接时间
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		String url = ConfigFactory.getString("search.client.url") + "/index/createOrUpdateDocument";
		HttpPost httpost = new HttpPost(url);
		logger.debug("L in LogUrl:" + url);
		logger.debug("L in Log:" + nvps);
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = null;
		HttpResponse response = null;
		InputStream in = null;
		try {
			response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity = new BufferedHttpEntity(entity);
				in = entity.getContent();
				byte[] read = new byte[1024];
				byte[] all = new byte[0];
				int num;
				while ((num = in.read(read)) > 0) {
					byte[] temp = new byte[all.length + num];
					System.arraycopy(all, 0, temp, 0, all.length);
					System.arraycopy(read, 0, temp, all.length, num);
					all = temp;
				}
				result = new String(all, "UTF-8");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			httpost.abort();
		}
		logger.debug("L out Log:" + result);
		ObjectMapper mapper = JackJsonUtil.getMapperInstance(false);
		JsonNode node = null;
		try {
			node = mapper.readTree(result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (node != null && node.path("responsecode").asText().equals("_200")) {
			return true;
		} else {
			logger.error("搜索平台建立索引异常：" + result);
			return false;
		}
	}

	public static String advancedQuery(List<NameValuePair> nvps) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);// 连接时间
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		String url = ConfigFactory.getString("search.client.url") + "/search/query";
		HttpPost httpost = new HttpPost(url);
		logger.debug("AQ in LogUrl:" + url);
		logger.debug("AQ in Log:" + nvps);
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String result = null;
		HttpResponse response = null;
		InputStream in = null;
		try {
			response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				entity = new BufferedHttpEntity(entity);
				in = entity.getContent();
				byte[] read = new byte[1024];
				byte[] all = new byte[0];
				int num;
				while ((num = in.read(read)) > 0) {
					byte[] temp = new byte[all.length + num];
					System.arraycopy(all, 0, temp, 0, all.length);
					System.arraycopy(read, 0, temp, all.length, num);
					all = temp;
				}
				result = new String(all, "UTF-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			httpost.abort();
		}
		logger.debug("AQ out Log:" + result);
		return result;
	}
}
