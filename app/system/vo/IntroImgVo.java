/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-6-27
 */
package system.vo;

import java.util.ArrayList;
import java.util.List;

import mobile.model.IntroImg;
import utils.Assets;

/**
 * 
 * 
 * @ClassName: IntroImgVo
 * @Description: 移动端轮播图
 * @date 2014-6-27 下午5:07:53
 * @author ShenTeng
 * 
 */
public class IntroImgVo {
    private Long id;

    private String imgUrl;

    private String uri;

    private Integer seq = 0;

    private String device;

    public static IntroImgVo create(IntroImg introImg) {
        IntroImgVo vo = new IntroImgVo();
        vo.setDevice(introImg.getDevice());
        vo.setId(introImg.getId());
        vo.setImgUrl(Assets.at(introImg.getImgUrl()));
        vo.setSeq(introImg.getSeq());
        vo.setUri(introImg.getUri());

        return vo;
    }

    public static List<IntroImgVo> createList(List<IntroImg> list) {
        List<IntroImgVo> voList = new ArrayList<>(list.size());
        for (IntroImg img : list) {
            voList.add(create(img));
        }
        return voList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

}
