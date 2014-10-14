/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-25
 */
package mobile.service.core;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.util.CollectionUtils;

import mobile.model.IntroImg;
import play.db.jpa.JPA;

/**
 * 
 * 
 * @ClassName: IntroImgService
 * @Description: 移动端首屏介绍图片服务
 * @date 2014-6-25 上午11:04:43
 * @author ShenTeng
 * 
 */
public class IntroImgService {

    /**
     * 根据设备来源获取IntroImg集合
     * 
     * @param device 设备
     * @return IntroImg集合
     */
    public static List<IntroImg> getByFrom(String device) {
        List<IntroImg> resultList = JPA.em()
                .createQuery("from IntroImg where device = :device order by seq desc,id desc", IntroImg.class)
                .setParameter("device", device).getResultList();
        return resultList;
    }

    /**
     * 根据id获取IntroImg对象
     * 
     * @param id IntroImg id
     * @return IntroImg对象
     */
    public static IntroImg getById(Long id) {
        List<IntroImg> resultList = JPA.em().createQuery("from IntroImg where id = :id", IntroImg.class)
                .setParameter("id", id).getResultList();
        return CollectionUtils.isEmpty(resultList) ? null : resultList.get(0);
    }

    /**
     * 保存或更新IntroImg实体
     * 
     * @param introImg IntroImg实体
     */
    public static void saveOrUpdate(IntroImg introImg) {
        if (null != introImg) {
            JPA.em().merge(introImg);
        }
    }

    /**
     * 保存或更新IntroImg实体,并上传Img
     * 
     * @param tmpFile 临时上传文件，上传完成后需要删除
     * @return 保存img的相对路径
     */
    public static String uploadImg(File tmpFile, String suffix) {
        UUID.randomUUID().toString().replace("-", "");
        String absBasePath = FileService.getMobileAbsUploadPath() + "intro/";
        // String relBasePath = FileService.getMobileRelUploadPath() + "intro/";
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
        String absImgPath = absBasePath + fileName;
        String relImgPath = FileService.getMobileRelUploadPath() + "intro/" + fileName;

        File absBaseFile = new File(absBasePath);
        if (!absBaseFile.exists()) {
            absBaseFile.mkdirs();
        }

        FileService.copyFile(tmpFile.getAbsolutePath(), absImgPath);

        return relImgPath;
    }

    /**
     * 删除上传的图片
     * 
     * @param relImgPath 图片相对路径
     */
    public static void deleteImg(String relImgPath) {
        String absImgPath = FileService.getMobileAbsUploadPath() + "intro/"
                + relImgPath.substring(relImgPath.lastIndexOf("/") + 1);
        File absImgFile = new File(absImgPath);
        if (absImgFile.exists()) {
            absImgFile.delete();
        }
    }

    /**
     * 删除IntroImg 并删除对应的图片
     * 
     * @param introImg 待删除的IntroImg对象
     */
    public static void delete(IntroImg introImg) {
        deleteImg(introImg.getImgUrl());
        JPA.em().remove(introImg);
    }

}
