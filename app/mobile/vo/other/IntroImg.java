/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-27
 */
package mobile.vo.other;

import java.io.Serializable;

import play.libs.Json;

import utils.Assets;

import com.fasterxml.jackson.databind.JsonNode;

import mobile.vo.MobileVO;

/**
 * 
 * 
 * @ClassName: IntroImg
 * @Description: 轮播图
 * @date 2014-6-27 下午6:08:29
 * @author ShenTeng
 * 
 */
public class IntroImg implements MobileVO, Serializable {

    private static final long serialVersionUID = -6323850499280819273L;

    private String imgUrl;

    private String uri;

    public static IntroImg create(mobile.model.IntroImg img) {
        IntroImg vo = new IntroImg();
        vo.setImgUrl(Assets.at(img.getImgUrl()));
        vo.setUri(img.getUri());
        return vo;
    }

    @Override
    public JsonNode toJson() {
        return Json.toJson(this);
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
