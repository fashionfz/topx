/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;

import play.Logger;
import play.Play;
import scalax.io.support.FileUtils;
import utils.HelomeUtil;
import controllers.base.BaseApp;
import ext.config.ConfigFactory;

/**
 * @author ZhouChun
 * @ClassName: AttachmentApp
 * @Description: 附件管理
 * @date 13-11-7 上午9:57
 */
public class AttachmentApp extends BaseApp{

    public static final String UPLOAD_SUFFIX = "upload.suffix";
    public static final String UPLOAD_PATH = "upload.path";
    public static final String UPLOAD_SIZE = "upload.size";
    public static final String UPLOAD_URL = "upload.url";
    public static final String EXPERT_LANGUAGE = "language";
    public static List getUploadSuffix() {
        List suffixList = ConfigFactory.getList(UPLOAD_SUFFIX);
        if(suffixList == null || suffixList.size() == 0) suffixList = Collections.emptyList();
        return suffixList;
    }

     
    public static List<String> getLanguage() {
        List languageList = ConfigFactory.getList(EXPERT_LANGUAGE);
        if(languageList == null || languageList.size() == 0) languageList = Collections.emptyList();
        return languageList;
    }
    
    public static String getUploadPath() {
        String uploadpath = ConfigFactory.getString(UPLOAD_PATH);
        if(HelomeUtil.isEmpty(uploadpath)) {
            uploadpath = System.getProperty("user.dir") + "\\public\\uploadfile\\";
        }
        return uploadpath;
    }

    public static long getUploadSize() {
        long size = ConfigFactory.getLong(UPLOAD_SIZE);
        return size;
    }

    public static long getFileSize(File file) {
        if(file.exists()) {
            try {
                FileInputStream in = new FileInputStream(file);
                long size = in.available();
                in.close();
                return size;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static void copy(File source, File target) {
        try {
            FileInputStream in = new FileInputStream(source);
            FileOutputStream out = new FileOutputStream(target);
            FileUtils.copy(in, out);
            in.close();
            out.close();
        }  catch (Exception e) {
            Logger.error("attachmentApp copy error", e);
        }
    }

    public static void move(File source, File target) {
        try {
            FileInputStream in = new FileInputStream(source);
            FileOutputStream out = new FileOutputStream(target);
            FileUtils.copy(in, out);
            in.close();
            out.close();
            source.delete();
        }  catch (Exception e) {
            Logger.error("attachmentApp move error", e);
        }
    }

}
