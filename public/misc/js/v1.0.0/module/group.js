/**
 * @description: 群组操作模块
 * @author:
 * @update: tintao.li@helome.com
 */
define('module/group', [
    'common/util',
    'common/interface',
    'module/cookie',
    'module/imTips'
], function(util, inter, cookie, imTips){

    var curId = parseInt(cookie.get('_u_id')) || 0;
    /**
     * 加入普通群组
     */
    var applyJoin = function(applyId, applyContent){
        util.setAjax(
            inter.getApiUrl().applyJoinGroup,
            {groupId: applyId, content: applyContent},
            function(json){
                if (json.status) {}
            },
            function(json){
                $.alert(json.error);
            },
            "GET");
    };
    /**
     * 改变按钮到群聊状态
     */
    var changeBtnToChat = function($btn){
        var groupId = $btn.attr("group-id"),
            groupName = $btn.attr("group-name"),
            groupIslock = $btn.attr("group-islock");

        var $concactBtn = $('<span class="btn-default btn-green btn-lg btn-contact" group-id="' + groupId + '" group-name="' + groupName + '" group-islock="' + groupIslock + '"><i class="index-icon icon-btn-chat"></i>群 聊</span>');
        $btn.after($concactBtn);
        $btn.remove();

        $concactBtn.on("click", function(){
            bindChatBtn($(this));
        });
    };

    /**
     * 绑定事件，打开webIM群聊
     */
    var bindChatBtn = function($this){
        var self = this,
            curId = parseInt(cookie.get('_u_id')) || 0,
            chatId = $this.attr('group-id')|| 0,
            isOpenChat = $.parseJSON(cookie.get('isOpenChat'+curId)) || {},
            $item = $this.closest(".search-block"),
            groupData = {
                groupId: chatId,
                groupName: $item.find('.name').text(),
                groupAvatar: $item.find('.search-head img').attr('src'),
                groupType: 'normal'
            };

        imTips.fireChat(groupData);
    };

    /**
     * 绑定加入群组按钮
     */
    var bindJoinEvent = function($btn){

        if (!curId) {
            location.href = '/login?msg=1&referer=' + encodeURIComponent(location.href);
            return;
        }
        
        var $this = $btn,
            gId = parseInt($this.attr("group-id")),
            uId = cookie.get("_u_id"),
            isLock = $this.attr("group-islock"),
            groupName = $this.attr("group-name");

        var tips = $.tips('loading');

        isLock = isLock =='true' || isLock == '1';
        if (isLock) {

            var tpl = [
                '<div class="clearfix">',
                    '<div class="dialog-tit clearfix">',
                        '<span class="span-left">申请加入</span>',
                    '</div>',
                    '<div class="apply-block">',
                        '<h3>该群需要身份验证才能加入，请联系该群管理员</h3>',
                        '<p class="pt20">验证信息：</p>',
                        '<textarea id="apply-content" class="textarea mt10"></textarea>',
                        '<div class="tips pt10">内容限制250个字符，还可以输入<b>250</b>个字符</div>',
                    '</div>',
                '</div>'
            ].join('');

            $.dialog({
                title: '申请加入',
                content: tpl,
                lock: true,
                ok: function(){
                    var $applyTextArea = $("#apply-content"),
                        applyContent = $.trim($applyTextArea.val());

                    applyJoin(gId, applyContent);
                },
                initialize: function(){
                    tips.close();
                    util.wordsCount('#apply-content', '.apply-block .tips b', 250);
                },
                cancel: function(){}
            });

        }else{

            util.setAjax(
                inter.getApiUrl().addMembers,
                {groupId: gId, userId: uId},
                function(json){
                    tips.close();
                    if (json.status) {
                        $.dialog({
                            id: gId,
                            fixed: true,
                            lock: true,
                            content:['<div class="dialog-panel">',
                                        '<div class="dialog-tit clearfix">',
                                            '<span class="span-left">申请加入</span>',
                                        '</div>',
                                        '<table class="dialog-table">',
                                            '<tr>',
                                                '<td class="d-alert-content">',
                                                    '您已经成功加入了“<a>'+ groupName +'</a>”',
                                                '</td>',
                                            '</tr>',
                                        '</table>',
                                    '</div>'].join(""),
                            okValue:'关闭',
                            ok: function () {
                                changeBtnToChat($this);
                            }
                        });
                    }
                },
                function(json){
                    tips.close();
                    $.alert(json.error);
                },
                "POST");
        }

    };

    return {
    	bindJoinEvent: bindJoinEvent,
        bindChatBtn: bindChatBtn
    }
});