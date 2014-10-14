/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年7月25日
 */
package vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import models.Expert;
import models.Group;
import models.GroupMember;
import models.User;
import play.mvc.Http.Context;
import vo.page.Page;

/**
 *
 *
 * @ClassName: GroupDetail
 * @Description: 群组详细信息
 * @date 2014年7月25日 下午4:17:31
 * @author ShenTeng
 * 
 */
public class GroupDetail {

    private GroupVO group;

    private ExpertDetail groupOwner;

    private List<GroupMemberVO> groupMemberList;
    
    public GroupVO getGroup() {
        return group;
    }

    /**
     * 从PO转换为VO，群成员取出指定个数
     * 
     * @param g Group PO
     * @param num 群成员个数
     * @param isIncludeOwner群成员是否包含群主
     */
    public void convert(Group g, Integer num, boolean isIncludeOwner) {
        group = new GroupVO();
        group.convert(g);

        // 检查当前用户是否加入群组
        User user = User.getFromSession(Context.current().session());
        if (null != user) {
            Map<Long, Boolean> joinGroupMap = GroupMember.checkJoinGroup(user.getId(), Arrays.asList(g.getId()));
            group.setIsJoin(joinGroupMap.get(g.getId()));
        }

        // 生成groupOwner
        groupOwner = new ExpertDetail();
		if (g.getOwner() != null) {
        	groupOwner.convert(Expert.getExpertByUserId(g.getOwner().getUserId()));
        }

        // 生成groupMember集合
        groupMemberList = new ArrayList<GroupMemberVO>();
        Long excludeMemberId = null;
        if (!isIncludeOwner && g.getOwner() != null) {
            excludeMemberId = g.getOwner().getId();
        }
        Page<GroupMember> queryPageByGroupId = GroupMember.queryPageByGroupId(1, 10, g.getId(), excludeMemberId);

        for (GroupMember gm : queryPageByGroupId.getList()) {
            GroupMemberVO groupMemberVO = new GroupMemberVO();
            groupMemberVO.convertWithDetailInfo(gm);
            groupMemberList.add(groupMemberVO);
        }

    }

    public void setGroup(GroupVO group) {
        this.group = group;
    }

    public ExpertDetail getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(ExpertDetail groupOwner) {
        this.groupOwner = groupOwner;
    }

    public List<GroupMemberVO> getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(List<GroupMemberVO> groupMemberList) {
        this.groupMemberList = groupMemberList;
    }
}
