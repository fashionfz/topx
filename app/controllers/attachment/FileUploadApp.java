/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package controllers.attachment;

import java.io.File;
import java.util.List;

import play.Play;
import play.mvc.Http;
import play.mvc.Result;
import utils.HelomeUtil;
import controllers.base.ObjectNodeResult;

/**
 * @author ZhouChun
 * @ClassName: FileUploadApp
 * @Description: 文件上传
 * @date 13-11-7 上午9:55
 */
public class FileUploadApp extends AttachmentApp {

    /**
     * 文件上传，包含表单数据
     * @return
     */
    public static Result multipartUpload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFile = body.getFile("file");
        ObjectNodeResult result = new ObjectNodeResult();
        if(uploadFile != null) {

            if(uploadCheck(result, uploadFile)) {
                String path = save(uploadFile);
                result.getObjectNode().put("path", path);
            }

        } else {
            result.error("没有找到上传的文件file");
        }

        return ok(result.getObjectNode());
    }

    /**
     * 直接上传文件
     * Direct file upload
     * @return
     */
    public static Result directFileUpload() {
        return TODO;
    }


    /**
     * 保存文件
     * @param filePart
     * @return
     */
    private static String save(Http.MultipartFormData.FilePart filePart) {

        String uploadpath = getUploadPath();

        File path = new File(uploadpath);
        if(!path.exists()) {
            path.mkdirs();
        }

        String newFileName = getNewFileName(filePart);

        uploadpath += newFileName;

        File target = new File(uploadpath);
        File source = filePart.getFile();

        copy(source, target);

        return "/assets/uploadfile/" + newFileName;
    }

    private static String getNewFileName(Http.MultipartFormData.FilePart filePart) {
        String filename = filePart.getFilename();
        String suffix = getSuffix(filename);
        String newFileName = HelomeUtil.uuid() + suffix;
        return newFileName;
    }
    /**
     * 校验上传的文件是否合法
     * @param result
     * @param filePart
     * @return
     */
    private static boolean uploadCheck(ObjectNodeResult result,Http.MultipartFormData.FilePart filePart) {

        String filename = filePart.getFilename();

        String suffix = getSuffix(filename);
        if(suffix == null) {
            result.error("错误的文件类型");
            return false;
        }

        if(!checkFileTypes(suffix)) {
            result.error("不允许上传的文件类型");
            return false;
        }

        File file = filePart.getFile();
        long fileSize = getFileSize(file);
        if(fileSize <= 0) {
            result.error("上传文件不允许为空");
            return false;
        }

        fileSize = fileSize / 1024;

        long size = Play.application().configuration().getLong(UPLOAD_SIZE);

        if(fileSize > size) {
            result.error("超过文件大小限制[" + size + "kb]");
            return false;
        }

        return true;
    }

    /**
     * 验证上传文件的类型
     * @param suffix
     */
    private static boolean checkFileTypes(String suffix) {
        List suffixList = Play.application().configuration().getList(UPLOAD_SUFFIX);
        if(HelomeUtil.isEmpty(suffixList)) return true;
        return suffixList.contains(suffix);
    }



    /**
     * 获取文件后缀名
     * @param filename
     * @return
     */
    private static String getSuffix(String filename) {
        String s = HelomeUtil.trim(filename).toLowerCase();
        int index = s.lastIndexOf(".");
        if(index == -1) {
            return null;
        }else {
            return s.substring(index);
        }
    }

}
