package system.controllers;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import models.Feedback;
import models.Feedback.FeedbackStatus;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Result;
import utils.DateUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 系统日志
 * @ClassName: SysLogApp
 * @Description: 
 * @date 2014-5-4
 */
public class SysLogApp extends Controller {
	
	/**
	 * 日志文件夹主目录名
	 */
	public static final String LOGDIRECTORYHOME = "logback";
	
	/**
	 * 系统日志文件列表
	 */
	@Transactional(readOnly = true)
	public static Result list() {
		int start = NumberUtils.toInt(request().getQueryString("start"), 0);
		int pageSize = NumberUtils.toInt(request().getQueryString("limit"), 20);
		
		// 获取${application.home}/logback路径
		String logPath = System.getProperty("user.dir") + File.separator + LOGDIRECTORYHOME;
		if (Logger.isInfoEnabled()) {
			Logger.info("日志文件所在的主目录： --> " + logPath);
		}
		List<File> fileList = new ArrayList<File>();
		if (StringUtils.isNotBlank(logPath)) {
			fileList = listFile(new File(logPath));
		}
		
		ObjectNode result = Json.newObject();
		result.put("total", fileList.size());
		if (CollectionUtils.isEmpty(fileList)) {
			result.putPOJO("data", null);
			return ok(result);
		}
		// 文件夹降序排序
		sortFileList(fileList);
		
		List<ObjectNode> data = null;
		data = new ArrayList<ObjectNode>(fileList.size());
		for (File f : fileList) {
			ObjectNode node = Json.newObject();
			node.put("id", f.getPath());
			node.put("filename", f.getName());
			node.put("fileType", f.isDirectory() ? "文件夹" : "文本文档");
			node.put("fileSize", "");
			List<File> childFilelist = new ArrayList<File>();
			if (StringUtils.isNotBlank(f.getPath())) {
				childFilelist = listChildFile(f);
			}
			if (CollectionUtils.isNotEmpty(childFilelist)) {
				List<ObjectNode> nodes = new ArrayList<ObjectNode>(childFilelist.size());
				Iterator<File> iter = childFilelist.iterator();
				while (iter.hasNext()) {
					File subFile = (File) iter.next();
					ObjectNode n = Json.newObject();
					n.put("parentName", f.getName());
					n.put("childFileName", subFile.getName());
					n.put("childFilePath", subFile.getPath());
					n.put("childCreateTime", DateUtils.format(new java.util.Date(subFile.lastModified())));
					n.put("childFileType", subFile.isDirectory() ? "文件夹" : "文本文档");
					n.put("childFileSize", (subFile.length()/1024)+" KB");
					nodes.add(n);
				}
				node.putPOJO("files", nodes);
			} else {
				node.putPOJO("files", null);
			}
			node.put("content", f.isDirectory());
			node.put("createTime", DateUtils.format(new java.util.Date(f.lastModified())));
			data.add(node);
		}
		// 内部分页效果
		if (CollectionUtils.isNotEmpty(data) && data.size() > pageSize) {
			data = data.subList(start,(start + pageSize) <= data.size() ? (start + pageSize) : data.size());
		}
		
		result.putPOJO("data", data);
		return ok(result);
	}
	
	/**
	 * 文件List排序，降序排序
	 * @param fileList
	 */
	private static void sortFileList(List<File> fileList) {
		if (CollectionUtils.isNotEmpty(fileList)) {
			Collections.sort(fileList, new Comparator<File>(){
				public int compare(File o1, File o2) {
					if (o1 != null && o2 != null) {
						return (int) (o2.lastModified() - o1.lastModified());
					}
					return 0;
				}
			});
		}
	}
	
	/**
	 * 获取父目录下的所有的子目录
	 * @return File的list集合
	 */
	public static List<File> listFile(File file) {
		// 判断传入对象是否为一个文件夹
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("传入的参数不是文件夹");
		}
		File[] list = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		return new ArrayList<File>(Arrays.asList(list));
	}
    
    /**
     * 获取目录下的所有的文件
     * @param file
     * @return
     */
	public static List<File> listChildFile(File file) {
		// 判断传入对象是否为一个文件夹
		if (!file.isDirectory()) {
			throw new IllegalArgumentException("传入的参数不是文件夹");
		}
		File[] list = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isFile();
			}
		});
		return new ArrayList<File>(Arrays.asList(list));
	}
    
	
	/**
	 * 更新投诉处理状态
	 * @return
	 */
	@Transactional
	public static Result modifyStatus() {
		DynamicForm requestData = Form.form().bindFromRequest();
		Long id = NumberUtils.createLong(requestData.get("feedbackId"));
		Integer state = NumberUtils.createInteger(requestData.get("handleState"));
		ObjectNode result = Json.newObject();
		if(id == null || state == null) {
			result.put("success", false);
			return ok(result);
		}
		Feedback fb = Feedback.getFeedback(id);
		if(fb == null) {
			result.put("success", false);
			return ok(result);
		}
		FeedbackStatus fbs = FeedbackStatus.getStatusByOrdinal(state.intValue());
		fb.status = fbs;
		fb.merge();
		result.put("success", true);
		return ok(result);
	}
	
	/**
	 * 文件下载
	 * @param fileName 文件所在的目录名称
	 * @param subFileName 文件名
	 * @return
	 */
	public static Result download(String fileName, String subFileName) {
		String filePath = System.getProperty("user.dir") + File.separator + LOGDIRECTORYHOME + File.separator + fileName + File.separator + subFileName;
		play.mvc.Http.Response response = response();

		// filePath是指欲下载的文件的路径。
		File file = new File(filePath);
		// 取得文件名。
		String filename = file.getName();
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("UTF-8"),"ISO8859_1"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response.setHeader("Content-Length", "" + file.length());
		response.setContentType("application/octet-stream");

		byte[] buffer = {};
		try {
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(filePath));
			buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
		} catch (IOException e) {
			if (Logger.isErrorEnabled()) {
				Logger.error("下载文件" + filePath + "出错。", e);
			}
		}

		return ok(buffer);
	}
		

}
