/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-1-7
 */
package controllers.attachment;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.Attach;
import models.AttachOfFeedback;
import models.AttachOfIndustry;
import models.AttachOfRequire;
import models.AttachOfService;
import models.AttachOfSuggestion;
import models.User;

import org.apache.commons.lang3.StringUtils;

import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import utils.Assets;
import utils.HelomeUtil;
import controllers.base.ObjectNodeResult;
import ext.config.ConfigFactory;

/**
 * 附件上传
 * @ClassName: AttachUploadApp
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-1-7 上午11:19:25
 * @author YangXuelin
 * 
 */
public class AttachUploadApp extends AttachmentApp {
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	//用户投诉上传凭证图像保存路径
	public static final String PROOF_UPLOAD_PATH = getUploadPath();
	
	//反馈附件相对路径
	public static final String FEEDBACK_UPLOAD_RELATIVE_PATH = "topx/uploadfile/attachment/feedback/";
	//建议附件相对路径
	public static final String SUGGESTION_UPLOAD_RELATIVE_PATH = "topx/uploadfile/attachment/suggestion/";
	//服务附件相对路径
	public static final String SERVICE_UPLOAD_RELATIVE_PATH = "topx/uploadfile/attachment/service/";
	//需求附件相对路径
	public static final String REQUIRE_UPLOAD_RELATIVE_PATH = "topx/uploadfile/attachment/require/";
	//需求附件相对路径
	public static final String INDUSTRY_UPLOAD_RELATIVE_PATH = "topx/uploadfile/attachment/industry/";
	
	//用户投诉上传凭证图像张数
	public static final int PROOF_UPLOAD_COUNT = 5;
	
	//用户投诉上传凭证图像类型
	public static final String[] PROOF_UPLOAD_FILETYPE = {".jpg", ".jpeg", ".gif", ".png", ".bmp"};
	//需求上传附件类型
	public static final String[] REQUIRE_UPLOAD_FILETYPE = {".jpg", ".jpeg", ".gif", ".png", ".bmp", ".xls", ".xlsx", ".doc", ".docx"};
	
	//上传凭证大小,单位：kb
	public static final long PROOF_UPLOAD_FILESIZE = 2048;
	//上传凭证URL
	public static final String PROOF_UPLOAD_URL = ConfigFactory.getString(UPLOAD_URL);

    /**
     * 直接上传文件
     * Direct file upload
     * @param type
     *  feedback：反馈， suggestion：建议，service：服务，require：需求，industry：行业
     * @return
     */
	@Transactional
    public static Result fileUpload(String type) {
    	User currentUser = User.getFromSession(session());
    	Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFile = body.getFile("certificate");
    	ObjectNodeResult result = new ObjectNodeResult();
    	if(uploadFile != null) {
    		if(uploadCheck(result, uploadFile,type)) {
    			result = save(uploadFile.getFile(), uploadFile.getFilename(), result, currentUser, type);
    		}
    	} else {
    		result.error("没有找到上传的文件file");
    	}
    	return ok(result.getObjectNode().toString());
    }


    /**
     * 保存文件
     * @param filePart
     * @return
     */
    public static ObjectNodeResult save(File sourceFile, String fileName, ObjectNodeResult result, User currentUser, 
            String attachType) {
    	StringBuffer sb = new StringBuffer(PROOF_UPLOAD_PATH);
    	String date = dateFormat.format(new java.util.Date());
    	sb.append("attachment");
    	
    	if (StringUtils.equals("suggestion", attachType)) {
    		sb.append(File.separator).append("suggestion");
    	} else if (StringUtils.equals("service", attachType)) {
    		sb.append(File.separator).append("service");
    	} else if (StringUtils.equals("require", attachType)) {
    		sb.append(File.separator).append("require");
    	}else if(StringUtils.equals("feedback", attachType)) {	
    		sb.append(File.separator).append("feedback");
		} else {
    		sb.append(File.separator).append("industry");
    	}
    	sb.append(File.separator).append(date);
    	
    	//目录
        File path = new File(sb.toString());
        if(!path.exists()) {
            path.mkdirs();
        }
        //再次判断目录是否存在，可能出现未连接cdn问题
        if(!path.exists()) {
        	String str = "上传凭证失败！";
			if (StringUtils.equals("service", attachType)) {
				str = "上传服务案例失败！";
			} else if (StringUtils.equals("require", attachType)) {
				str = "上传需求附件失败！";
			}
        	return result.error(str);
        }
        
        String newFileName = getNewFileName(fileName);
        String uploadPath = sb.append(File.separator).append(newFileName).toString();
        File target = new File(uploadPath);
        BigDecimal sizeTemp = BigDecimal.valueOf(sourceFile.length());
        String suffixTemp = getSuffix(fileName);
        move(sourceFile, target);
        
        String relativePath = FEEDBACK_UPLOAD_RELATIVE_PATH;
		if (StringUtils.equals("suggestion", attachType)) {
        	relativePath = SUGGESTION_UPLOAD_RELATIVE_PATH;
		} else if (StringUtils.equals("service", attachType)) {
        	relativePath = SERVICE_UPLOAD_RELATIVE_PATH;
		} else if (StringUtils.equals("require", attachType)) {
        	relativePath = REQUIRE_UPLOAD_RELATIVE_PATH;
        } else if(StringUtils.equals("industry", attachType)) {
        	relativePath = INDUSTRY_UPLOAD_RELATIVE_PATH;
        }
        String relPath = new StringBuffer(relativePath).append(date).append("/").append(newFileName).toString();
       
        result.getObjectNode().put("path", relPath);
        result.getObjectNode().put("pathsource", Assets.at(relPath));
        // 附件表写记录
        String pathTemp = relPath;
        Attach attach = new AttachOfFeedback(fileName,pathTemp,suffixTemp,sizeTemp,new Date(),currentUser == null ? null : currentUser.getId(),currentUser == null ? "" : currentUser.getName(),false);
        if (StringUtils.equals("suggestion", attachType)) {
        	attach = new AttachOfSuggestion(fileName,pathTemp,suffixTemp,sizeTemp,new Date(),currentUser == null ? null : currentUser.getId(),currentUser == null ? "" : currentUser.getName(),false);
        } else if (StringUtils.equals("service", attachType)) {
        	attach = new AttachOfService(fileName,pathTemp,suffixTemp,sizeTemp,new Date(),currentUser == null ? null : currentUser.getId(),currentUser == null ? "" : currentUser.getName(),false);
        } else if (StringUtils.equals("require", attachType)) {
        	attach = new AttachOfRequire(fileName,pathTemp,suffixTemp,sizeTemp,new Date(),currentUser == null ? null : currentUser.getId(),currentUser == null ? "" : currentUser.getName(),false);
        } else if (StringUtils.equals("industry", attachType)) {
        	attach = new AttachOfIndustry(fileName,pathTemp,suffixTemp,sizeTemp,new Date(),currentUser == null ? null : currentUser.getId(),currentUser == null ? "" : currentUser.getName(),false);
        }
        attach.saveOrUpdate();
        result.getObjectNode().put("attachId", attach.id);
        result.getObjectNode().put("fileName", fileName);
        return result;
    }

    /**
     * 获得新文件名
     * 
     * @param filename
     * @return
     */
    private static String getNewFileName(String filename) {
        String suffix = getSuffix(filename);
        return HelomeUtil.uuid() + suffix;
    }
    
    /**
     * 校验上传的文件是否合法
     * @param result
     * @param filePart
     * @return
     */
    public static boolean uploadCheck(ObjectNodeResult result, Http.MultipartFormData.FilePart filePart) {
        String filename = filePart.getFilename();
        String suffix = getSuffix(filename);
        if(suffix == null) {
            result.error("错误的文件类型！");
            return false;
        }
        if(!checkFileTypes(suffix)) {
            result.error("不允许上传的文件类型！");
            return false;
        }

        long fileSize = getFileSize(filePart.getFile());
        if(fileSize <= 0) {
            result.error("上传文件不允许为空！");
            return false;
        }
        //计算文件大小kb
        fileSize = fileSize / 1024;
        if(fileSize > PROOF_UPLOAD_FILESIZE) {
            result.error("超过文件大小限制[" + PROOF_UPLOAD_FILESIZE + "kb]");
            return false;
        }
        return true;
    }

    /**
     * 校验上传的文件是否合法
     * @param result
     * @param filePart
     * @return
     */
    public static boolean uploadCheck(ObjectNodeResult result, Http.MultipartFormData.FilePart filePart,String attachType) {
        String filename = filePart.getFilename();
        String suffix = getSuffix(filename);
        if(suffix == null) {
            result.error("错误的文件类型！");
            return false;
        }
        if(!checkFileTypes(suffix,attachType)) {
            result.error("不允许上传的文件类型！");
            return false;
        }

        long fileSize = getFileSize(filePart.getFile());
        if(fileSize <= 0) {
            result.error("上传文件不允许为空！");
            return false;
        }
        //计算文件大小kb
        fileSize = fileSize / 1024;
        if(fileSize > PROOF_UPLOAD_FILESIZE) {
            result.error("超过文件大小限制[" + PROOF_UPLOAD_FILESIZE + "kb]");
            return false;
        }
        return true;
    }
    
    /**
     * 验证上传文件的类型
     * @param suffix
     */
    private static boolean checkFileTypes(String suffix) {
    	for(String type : PROOF_UPLOAD_FILETYPE) {
    		if(type.equals(suffix)) {
    			return true;
    		}
    	}
        return false;
    }
    
    /**
     * 验证上传文件的类型
     * @param suffix
     */
    public static boolean checkFileTypes(String suffix,String attachType) {
    	String[] fileTypes = PROOF_UPLOAD_FILETYPE;
		if (StringUtils.equals("require", attachType)) {
			fileTypes = REQUIRE_UPLOAD_FILETYPE;
		}
    	for(String type : fileTypes) {
    		if(type.equals(suffix)) {
    			return true;
    		}
    	}
        return false;
    }

    /**
     * 获取文件后缀名
     * @param filename
     * @return
     */
    public static String getSuffix(String filename) {
        String s = HelomeUtil.trim(filename).toLowerCase();
        int index = s.lastIndexOf(".");
        if(index == -1) {
            return null;
        } else {
            return s.substring(index);
        }
    }
	
}
