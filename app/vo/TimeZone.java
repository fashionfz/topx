/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2013年10月30日
 */

package vo;

import utils.HelomeUtil;
import utils.MD5Util;

/**
 * @author ZhouChun
 * @ClassName: TimeZone
 * @Description: 时区
 * @date 13-11-14 下午5:24
 */
public class TimeZone implements  Comparable{

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 偏移量
     */
    private int offset;

    /**
     * 唯一标识
     */
    private String uid;

    public String getUid() {
        if(HelomeUtil.isEmpty(uid)) return createUid();
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String createUid() {
        return MD5Util.MD5(getDisplayName()+getOffset());
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean equalsOffset(Object offset) {
        return String.valueOf(this.offset).equals(offset);
    }

    public boolean equalsUid(Object uid) {
        return getUid().equals(uid);
    }

    public String toString() {
        return "offset:" + getOffset() + "#displayname:" + getDisplayName();
    }

    public String toYaml() {
        StringBuilder sb = new StringBuilder("- !!vo.TimeZone");
        sb.append("\n");
        sb.append("  uid: ").append(getUid());
        sb.append("\n");
        sb.append("  offset: ").append(getOffset());
        sb.append("\n");
        sb.append("  displayName: ").append(getDisplayName());
        return sb.toString();
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof TimeZone) {
            TimeZone target = (TimeZone)o;
            if(target.getOffset() < getOffset()) return -1;
            if(target.getOffset() > getOffset()) return 1;
        }
        return 0;
    }
}
