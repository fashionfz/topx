/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-4-22
 */
package mobile.vo;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 *
 * @ClassName: MobileVO
 * @Description: 移动端VO接口
 * @date 2014-4-22 下午5:32:57
 * @author ShenTeng
 * 
 */
public interface MobileVO {

    JsonNode toJson();
    
}
