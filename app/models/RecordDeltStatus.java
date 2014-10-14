/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014-03-27
 */
package models;

/**
 *
 *
 * @ClassName: RecordDeltStatus
 * @Description: 记录的状态（未删除，用户删除，专家删除，两边同时删除）
 * @author zhouxin
 * 
 */
public enum RecordDeltStatus {
	NORMAL, USER_DELETED,EXPERT_DELETED,ALL_DELETED
}
