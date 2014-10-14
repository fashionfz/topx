package ext.resume;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.multipart.RequestEntity;

import common.jackjson.JackJsonUtil;
import ext.config.ConfigFactory;
import play.Logger;
import play.Logger.ALogger;

/**
 * 海外简历客户端
 * @version 1.0
 */
public class ResumeHttpClient {
	
	static ALogger logger = Logger.of(ResumeHttpClient.class);
	
	/**
     * 请求超时时间，默认20000ms
     */
	public static final int REQUEST_TIMEOUT = 20 * 1000;
	
	public static final int SOCKET_BUFFER_SIZE = 8192;
	
	/**
	 * 海外简历访问路径
	 * /public/data.do
	 */
	
	private static String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>";
	
	private static String getXMLStr(String xmlBody) {
		StringBuffer sb = new StringBuffer();
		sb.append(XML_HEADER);
		sb.append(xmlBody);
		return sb.toString();
	}
	
	public static String addTaskForChinese(String xmlStr) throws UnsupportedEncodingException {
		String xmlData = getXMLStr(xmlStr);
		logger.debug("addTaskForChinese in Log:" + xmlStr);
		long beginMillis = System.currentTimeMillis();
		String result = sendXMLDataByPost(ConfigFactory.getString("resume.client.helomeusUrl") + "public/data.do", xmlData);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "addTaskForChinese", ConfigFactory.getString("resume.client.helomeusUrl") + "public/data.do");
		logger.debug("addTaskForChinese out Log:" + result);
		return result;
	}
	
	public static String updateTaskForChinese(String xmlStr) throws UnsupportedEncodingException {
		String xmlData = getXMLStr(xmlStr);
		logger.debug("updateTaskForChinese in Log:" + xmlStr);
		long beginMillis = System.currentTimeMillis();
		String result = sendXMLDataByPost(ConfigFactory.getString("resume.client.helomeusUrl") + "public/data.do", xmlData);
		long endMillis = System.currentTimeMillis();
		long callTime = endMillis - beginMillis;
		callTimeLog(callTime, "updateTaskForChinese", ConfigFactory.getString("resume.client.helomeusUrl") + "public/data.do");
		logger.debug("updateTaskForChinese out Log:" + result);
		return result;
	}
	
	
	public static String sendXMLDataByPost(String url,String xmlData) throws UnsupportedEncodingException{
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIMEOUT);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, REQUEST_TIMEOUT);
		client.getParams().setParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, SOCKET_BUFFER_SIZE);
		
		HttpPost post = new HttpPost(url);
		StringEntity strEntity = new StringEntity(xmlData,"UTF-8");
		post.setEntity(strEntity);
		post.setHeader("Content-Type","text/xml;charset=UTF-8");
		String result = null;
		HttpResponse response = null;
		InputStream in = null;
		// 发送请求并等待响应
		try {
			response = client.execute(post);
			// 如果状态码为200
			if (response.getStatusLine().getStatusCode() == 200) {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			post.abort();
		}
		return result;
	}
	
	/**
	 * 调用接口耗时写日志
	 * @param callTime
	 * @param method
	 * @param url
	 */
	private static void callTimeLog(long callTime, String method, String url) {
        StringBuilder log = new StringBuilder();
        log.append("调用方法=").append(method);
        log.append(",知识库系统接口URL=").append(url);
        log.append(",耗时=").append(callTime).append("ms");
        logger.info(log.toString());
    }

}
