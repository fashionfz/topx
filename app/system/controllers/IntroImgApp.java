/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-27
 */
package system.controllers;

import java.util.List;

import mobile.model.IntroImg;
import mobile.service.MobileService;
import mobile.service.core.IntroImgService;

import org.apache.commons.lang3.StringUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import system.vo.IntroImgVo;
import system.vo.Page;
import system.vo.ext.ExtForm;
import utils.HelomeUtil;

/**
 * 
 * 
 * @ClassName: IntroImgApp
 * @Description: 移动端首页介绍图片
 * @date 2014-6-27 下午2:30:55
 * @author ShenTeng
 * 
 */
public class IntroImgApp extends Controller {

    @Transactional
    public static Result list() {
        String device = StringUtils.defaultIfBlank(request().getQueryString("device"), "android");
        List<IntroImgVo> list = IntroImgVo.createList(IntroImgService.getByFrom(device));

        Page<IntroImgVo> page = new Page<>((long) list.size(), list);
        return ok(page.toJson());
    }

    @Transactional
    public static Result delete(Long id) {
        ExtForm extForm = new ExtForm();
        extForm.setSuccess(true);

        IntroImg introImg = IntroImgService.getById(id);
        if (null == introImg) {
            extForm.setSuccess(false);
            extForm.setMsg("该条记录已经被删除，请刷新页面");
            return ok(Json.toJson(extForm));
        }
        IntroImgService.delete(introImg);
        MobileService.removeIntroImgCache(introImg.getDevice());

        return ok(Json.toJson(extForm));
    }

    @Transactional
    public static Result saveOrUpdate() {
        // 获取参数
        DynamicForm requestData = Form.form().bindFromRequest();
        Long id = HelomeUtil.toLong(requestData.get("id"), null);
        String device = HelomeUtil.defaultIfNotContain(requestData.get("device"), new String[] { "android", "iphone" },
                null);
        Integer seq = HelomeUtil.toInteger(requestData.get("seq"), null);
        String uri = requestData.get("uri");
        MultipartFormData body = request().body().asMultipartFormData();
        FilePart imgFile = body.getFile("imgFile");
        // 参数校验
        ExtForm extForm = new ExtForm();
        extForm.setSuccess(true);
        if (null == id) {
            if (null == imgFile) {
                extForm.setSuccess(false);
                extForm.setMsg("请选择上传图片");
                return ok(Json.toJson(extForm));
            }
            if (StringUtils.isBlank(device) || null == seq) {
                extForm.setSuccess(false);
                extForm.setMsg("请完整填写表单");
                return ok(Json.toJson(extForm));
            }
        } else {
            if (StringUtils.isBlank(device) || null == seq) {
                extForm.setSuccess(false);
                extForm.setMsg("请完整填写表单");
                return ok(Json.toJson(extForm));
            }
        }

        // 保存或更新
        if (null == id) { // 保存
            String filename = imgFile.getFilename();
            String imgUrl = IntroImgService.uploadImg(imgFile.getFile(),
                    filename.substring(filename.lastIndexOf('.') + 1));

            IntroImg introImg = new IntroImg();
            introImg.setDevice(device);
            introImg.setImgUrl(imgUrl);
            introImg.setSeq(seq);
            introImg.setUri(uri);
            IntroImgService.saveOrUpdate(introImg);
        } else {// 更新
            IntroImg introImg = IntroImgService.getById(id);
            if (null == introImg) {
                extForm.setSuccess(false);
                extForm.setMsg("id错误，请刷新页面重新提交");
                return ok(Json.toJson(extForm));
            }
            if (null != imgFile) {
                IntroImgService.deleteImg(introImg.getImgUrl());
                String filename = imgFile.getFilename();
                String imgUrl = IntroImgService.uploadImg(imgFile.getFile(),
                        filename.substring(filename.lastIndexOf('.') + 1));
                introImg.setImgUrl(imgUrl);
            }
            introImg.setDevice(device);
            introImg.setSeq(seq);
            introImg.setUri(uri);
            IntroImgService.saveOrUpdate(introImg);
        }
        MobileService.removeIntroImgCache(device);

        return ok(Json.toJson(extForm));
    }

}
