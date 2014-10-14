/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-3-11
 */
package mobile.service.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import mobile.service.result.ServiceVOResult;
import mobile.util.MobileUtil;
import mobile.vo.result.CommonVO;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Logger.ALogger;
import scalax.io.support.FileUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.attachment.AttachUploadApp;
import controllers.attachment.AttachmentApp;
import controllers.base.ObjectNodeResult;

/**
 * 
 * 
 * @ClassName: FileUploadService
 * @Description: 文件相关的操作
 * @date 2014-3-11 下午2:37:23
 * @author ShenTeng
 * 
 */
public class FileService {

    private static final ALogger LOGGER = Logger.of(FileService.class);

    /**
     * 文件拷贝
     * 
     * @param sourceFile 源路径
     * @param destFile 目标路径
     */
    public static void copyFile(String sourceFile, String destFile) {
        File dest = new File(destFile);
        if (!dest.exists()) {// 目标文件对应的目录不存在，创建新的目录
            dest.getParentFile().mkdirs();
        }

        try (FileInputStream in = new FileInputStream(sourceFile);
                FileOutputStream out = new FileOutputStream(destFile)) {
            FileUtils.copy(in, out);
        } catch (Exception e) {
            LOGGER.error("fail to copyFile. sourceFile = " + sourceFile + " ,destFile = " + destFile, e);
        }
    }

    /**
     * 上传附件
     * 
     * @param sourceFile 文件，必须
     * @param fileName 文件名，必须
     * @param attachType 文件类型枚举，按业务划分
     * @return CommonVO - relPath：String，相对路径 ；url：String，下载路径；attachId：attachId
     */
    public static ServiceVOResult<CommonVO> uploadAttatch(File sourceFile, String fileName, AttatchType attachType) {
        String suffix = FileService.getFileNameSuffix(fileName);
        if (null == suffix) {
            return ServiceVOResult.error("100005", "非法的文件名");
        }

        if (!AttachUploadApp.checkFileTypes("." + suffix, attachType.getAttachType())) {
            return ServiceVOResult.error("108", "文件上传失败，不允许的文件类型");
        }

        ObjectNodeResult nodeResult = new ObjectNodeResult();
        AttachUploadApp.save(sourceFile, fileName, nodeResult, MobileUtil.getCurrentUser(), attachType.getAttachType());
        if (!nodeResult.isSuccess()) {
            return ServiceVOResult.error("108", "文件上传失败");
        } else {
            ObjectNode objectNode = nodeResult.getObjectNode();
            ServiceVOResult<CommonVO> result = ServiceVOResult.success();
            CommonVO vo = CommonVO.create();
            vo.set("relPath", objectNode.path("path").asText());
            vo.set("url", objectNode.path("pathsource").asText());
            vo.set("attachId", objectNode.path("attachId").asLong());
            result.setVo(vo);

            return result;
        }
    }

    public static enum AttatchType {
        SERVICE("service"), REQUIRE("require");

        private String attachType;

        AttatchType(String attachType) {
            this.attachType = attachType;
        }

        public String getAttachType() {
            return attachType;
        }

    }

    /**
     * 获取文件名后缀，如果文件名不符合规范，返回null
     * 
     * @param filename 文件名，全小写
     * @return
     */
    public static String getFileNameSuffix(String filename) {
        if (StringUtils.isBlank(filename)) {
            return null;
        }

        int indexOf = filename.indexOf('.');

        return indexOf < 0 ? null : filename.substring(indexOf + 1).toLowerCase();
    }

    /**
     * 获取绝对文件上传路径。路径结尾包括“/”符号。
     */
    public static String getAbsUploadPath() {
        return AttachmentApp.getUploadPath();
    }

    /**
     * 获取相对文件上传路径。路径结尾包括“/”符号。
     */
    public static String getRelUploadPath() {
        return "topx/uploadfile/";
    }

    /**
     * 获取移动端绝对文件上传路径。路径结尾包括“/”符号。
     */
    public static String getMobileAbsUploadPath() {
        return AttachmentApp.getUploadPath() + "mobile" + File.separator;
    }

    /**
     * 获取移动端相对文件上传路径。路径结尾包括“/”符号。
     */
    public static String getMobileRelUploadPath() {
        return "topx/uploadfile/mobile/";
    }

}
