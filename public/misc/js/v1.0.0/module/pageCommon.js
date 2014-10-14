/**
 * @description: 一级页面公共模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/pageCommon', [
    'common/interface',
    'common/util',
    'module/msgSocket',
    'module/placeholder',
    'module/fixed',
    'module/goTop',
    'module/msgSocket',
    'module/cookie',
    'module/searchType'
], function(inter, util, socket, placeholder, fixed, gotop, socket, cookie, searchType){
    /* 初始化 反馈建议 和回顶部*/
    gotop.init();

        /**
         * header部分的搜索框
         */
    var $searchTop = $(".header-search"),
        $searchTopBtn = $searchTop.find(".search-type-btn");

    var userData = {
        btnClass: "search-type-btn",
        text: "user",
        searchUrl: "/expertsearch",
        searchTip: "搜索服务者"
    }

    var groupData = {
        btnClass: "search-type-btn search-type-btn-group",
        text: "group",
        searchUrl: "/searchgroup",
        searchTip: "搜索群组"
    }

    var serviceData = {
        btnClass: "search-type-btn search-type-btn-service",
        text: "service",
        searchUrl: "/services/search",
        searchTip: "搜索服务"
    }

    var requireData = {
        btnClass: "search-type-btn search-type-btn-require",
        text: "require",
        searchUrl: "/require/search",
        searchTip: "搜索需求"
    }

    var searchData = {
        userData: userData,
        groupData: groupData,
        serviceData: serviceData,
        requireData: requireData
    }

    var sts = new searchType.SearchTypeSwitch($searchTop, $searchTopBtn, searchData);

    return {
        /**
         * 初始化登录注册模块
         */
        initLogin : function(callback){

            var self = this;

            self.browserDetect();
            self.devCenter();
            //self.searchTop();
            self.msgPanel();
            placeholder.init();

            //this.setTimezone();
            if($('.J-login-mark').length>0){
                //poll.init();
                socket.init();
            }
            util.imgLoadError();
        },

        /**
         * 设置客户端时区偏移量
         */
        setTimezone: function() {
            var offset = new Date( ).getTimezoneOffset();
            $.get('/timezone?offset=' + offset);
        },

        /**
         * 初始化咨询提示框
         */
        initConsult : function(){
            var tpl = [
                '<div class="dialog-panel">',
                    '<div class="dialog-tit clearfix">',
                        '<span class="span-left">咨询请求</span>',
                    '</div>',
                    '<table class="dialog-table">',
                        '<tr>',
                            '<td><div class="icon-alert"></div></td>',
                            '<td class="d-alert-content">收到 <a href="#{uid}" style="font-size: 16px; font-weight: bold; color: #4083a9;">#{userName}</a> 的视频咨询请求，5秒后自动接听</td>',
                        '</tr>',
                    '</table>',
                '</div>'].join(''),
                okConsult = null;

            $.dialog({
                content: util.template(tpl, {uid: 1, userName: '马立三'}),
                okValue: '立即开始',
                fixed: true,
                ok: function(){
                    window.location.href = '/consult/begin?id=1';
                },
                cancelValue: '拒绝咨询',
                cancel: function(){
                    var warp = $(this.dom.wrap[0]),
                        close = $(this.dom.close[0]);

                    warp.css({
                        bottom: 'auto',
                        right: 'auto'
                    });
                    this.fixed = false;
                    this._reset();
                    close.css({
                        right: -10,
                        top: -10
                    });
                    clearTimeout(okConsult);
                },
                initialize: function(){
                    var warp = $(this.dom.wrap[0]),
                        close = $(this.dom.close[0]);
                    warp.css({
                        left: 'auto',
                        top: 'auto',
                        bottom: 10,
                        right: 10
                    });
                    close.css({
                        right: 10,
                        top: 10
                    });
                    okConsult = setTimeout(function(){
                        window.location.href = '/consult/begin?id=1';
                    }, 10000)
                }
            });

        },

        /**
         * 顶部登录后头像下拉菜单效果
         */
        devCenter : function(){
            // var center = $(".J-login-mark"),
            //     btn = center.find(".home"),
            //     panel = center.find(".home-panel");

            // btn.hover(function(){
            //     panel.stop().slideToggle({duration: 200, queue: false});
            // }, function(){
            //     panel.stop().slideToggle({duration: 200, queue: false});
            // });

            var center = $(".J-login-mark"),
                btn = center.find(".home"),
                panel = center.find(".home-panel"),
                height = panel.height();

            btn.hover(function(){
                panel.show();
                panel.height(0);
                panel.stop().animate({
                    height: height
                }, 200);

            }, function(){
                panel.stop().animate({
                    height: 0
                }, 200, function(){
                    panel.hide();
                });
                                
            })
        },

        /**
         * 非chrome浏览器提示下载
         */
        browserDetect: function(){
            /*var userAgent = navigator.userAgent,
                isChrome = userAgent.indexOf("Chrome") > -1;
            if (!isChrome) {
                $(".browser-tips").show();
            }*/
            var tpl = [
                '<div class="browser-tips">',
                    '<div class="browser-tips-inner clearfix">',
                        '<div class="index-icon index-icon-i"></div>',
                        '<p>helome 建议您下载最新版 Google Chrome 浏览器，以获得最佳浏览效果，否则将无法实时向对方发起视频交流</p>',
                        '<a href="http://www.google.cn/intl/zh-CN/chrome/browser/" target="_blank">立即下载</a>',
                    '</div>',
                '</div>'
            ].join('');
            if(!(window.PeerConnection || window.webkitPeerConnection00 || window.webkitRTCPeerConnection || window.mozRTCPeerConnection) || !window.WebSocket){
                $('body').prepend(tpl);
            }
        },

        fixedBanner : function(){
            if(!$('.menuFixed').length){
                fixed.init({
                    target : $('#header'),
                    afterNoFixed : function(menuPanel, clone){
                        menuPanel.css('position', 'relative');
                    },
                    afterFixed : function(menuPanel, clone){
                        menuPanel.css({'position': 'fixed', width: '100%', top: 0, 'z-index': 1900});
                        clone.show();
                    }
                });
            }
        },

        msgPanel: function(){

            var currentIndex = 0,   //当前index
                systemMsgIdArr = [],    //系统消息数组
                isPanelOpen = false,    //面板是否打开
                currentSystemMsgLen = 0,   //当前系统消息长度
                currentAtMsgLen = 0,       //当前朋友圈消息长度
                currentGroupMsgLen = 0;    //当前群组消息长度

            var $tabBtn = $(".msg-header-block"),
                $tabBtnSystem = $tabBtn.eq(0),
                $tabBtnFriend = $tabBtn.eq(1),
                $tabBtnGroup = $tabBtn.eq(2),
                $sysContent = $("#msg-system-content"),
                $atContent = $("#msg-at-content"),
                $groupContent = $("#msg-group-content"),
                dotHTML = '<div class="index-icon index-icon-msg-noread"></div>',
                loadingHTML = '<div class="msg-loading"><span class="loader"></span></div>';

            $("#J-header-msg").on("click", function(e){

                $(".msg").toggle();
                if (!$(".msg").is(":hidden")) {
                    isPanelOpen = true;
                    $(".msg-content").append(loadingHTML);
                    getMsg(function(){
                        dotHandle();
                        $(".msg-loading").remove();
                    });
                }else{
                    //将面板收拢
                    clearContent();
                    resetPanel();
                    isPanelOpen = false;
                    systemMsgIdArr = [];
                };
                e.stopPropagation();
                
            });

            //点击外部将面板收拢
            $(document).on("click", function(){
                if (!$(".msg").is(":hidden")) {
                    //将面板收拢
                    clearContent();
                    resetPanel();
                    isPanelOpen = false;
                    systemMsgIdArr = [];
                    $(".msg").toggle();
                };
            });

            //组织点击msg事件
            $(".msg").on("click", function(e){
                e.stopPropagation();
            });

            //绑定tab事件
            $tabBtn.on("click", function(e){
                var index = $(this).index();
                currentIndex = index;
                
                //显示隐藏红点
                $(this).siblings(".msg-header-block").find(".icon").removeClass("active");
                $(this).find(".icon").addClass("active");
                $tabBtn.eq(currentIndex).find(".index-icon").remove();
                
                //显示隐藏对应的面板
                $(".msg-box").find(".msg-main").eq(currentIndex).removeClass("hide").siblings(".msg-main").addClass("hide");

                //原始的index相等和如果当前index，返回
                if (currentIndex == index) return;
                
                getMsg();
                e.stopPropagation();
            });

            //清空消息面板内容
            var clearContent = function(){
                $(".msg-content").html("");
            }

            //重置面板
            var resetPanel = function(){
                $tabBtn.find(".icon").removeClass("active");
                $tabBtn.eq(0).find(".icon").addClass("active");
                $(".msg-main").addClass("hide");
                $sysContent.parent(".msg-main").removeClass("hide");
            }

            var appendSysNoneMsg = function(){
                $sysContent.append("<div class='no-msg'>你会在这里收到系统消息。</div>");
            }

            var appendAtNoneMsg = function(){
                $atContent.append("<div class='no-msg'>你会在这里收到好友圈消息。</div>");
            }

            var appendGroupNoneMsg = function(){
                $groupContent.append("<div class='no-msg'>你会在这里收到群组消息。</div>");
            }

            var render = function(arr){

                var msgHtml =  ['<div class="msg-item #{isFixHight}" data-id="#{messageId}" data-gid="#{groupId}" data-uid="#{userId}">',
                                    '<div class="msg-item-inner">#{content}</div>',
                                    '#{addBtn}',
                                '</div>'].join("");

                var systemTmp = "",
                    atTmp = "",
                    groupTmp = "";
                    
                if(arr && arr.length){
                    $.each(arr, function(i, o){
                        var tmp = "",
                            content = o.content,
                            type = 0,
                            isFixHight = "",
                            addBtn = '<div class="msg-item-btnbox"><div class="icon icon-delete btn-delete"></div></div>'; //0 系统消息，1 圈子, 2 群组

                        switch (o.msgType){
                            case 'COMMENT': // 评价消息
                                tmp = '<a href="/expert/detail/'+ o.senderId +'" class="mr5" target="_blank">'+ o.senderName +'</a>评论了你：' + content + 
                                    '<a href="'+ util.strFormat(inter.getApiUrl().commentReplyUrl, [o.refId]) +'" class="ml5" target="_blank">回复</a>';
                                break;
                            case 'REPLY': // 回复消息
                                tmp = '<a href="/expert/detail/'+ o.senderId +'" class="mr5" target="_blank">'+ o.senderName + '</a>回复了你：'+ content +
                                    '<a href="'+ util.strFormat(inter.getApiUrl().commentReplyUrl, [o.refId]) +'" class="ml5" target="_blank">回复</a>';
                                break;
                            case 'TRANSFER_IN': // 转入消息
                                tmp = '<a href="/expert/detail/'+ o.senderId +'" class="mr5" target="_blank">'+ o.senderName + '</a>给你转入了 <span class="green">'+ (o.currency == 2 ? '$' : '￥') + o.amount + '</span>';
                                break;
                            case 'TRANSFER_OUT': // 转出消息
                                tmp = '你的账户转出 <span class="red">'+ (o.currency == 2 ? '$' : '￥') + o.amount +
                                    '</span>，转给：<a href="" class="ml5">'+ o.senderName +'</a>';
                                break;
                            case 'RESUME_FINISH': //海外简历翻译完毕
                                tmp = '<a class="mr5" href="javascript:;">嗨啰翻译中心</a>已把您的英文海外简历翻译完成。' +
                                    '<a class="ml5" href="'+ content +'" target="_blank">立刻查看。</a>';
                                break;
                            case 'ADD_FRIENDS': //收到邀请加圈消息
                                tmp = '<a userId = "'+ o.senderId +'" class="mr5" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                o.senderName + '</a>邀请您加入Ta的圈：' + content;
                                type = 1;
                                addBtn = '<div class="msg-item-btnbox btn-double"><div class="icon icon-right J-add-friend"></div><div class="icon icon-delete btn-delete"></div></div>';
                                break;
                            case 'AGREE_FRIENDS': //同意加入圈
                                tmp = '<a userId = "'+ o.senderId +'" class="mr5" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                    o.senderName + '</a>已经同意加入您的圈。';
                                type = 1;
                                break;
                            case 'INVIT_GROUP_MEMBER':      //邀请群成员消息
                                tmp = '<a class="mr5" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                    o.senderName + '</a>邀请您加入群<a class="ml5" href="/group/detail/'+
                                    o.groupId +'" target="_blank">'+o.groupName+'</a>';
                                    addBtn = '<div class="msg-item-btnbox btn-double"><div class="icon icon-right J-add-group"></div><div class="icon icon-delete btn-delete"></div></div>';
                                type = 2;
                                break;
                            case 'AGREE_GROUP_INVIT':       //用户已同意群主的入群邀请
                                tmp = '<a userId = "'+ o.senderId +'" class="mr5" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                    o.senderName + '</a>已加入群<a class="ml5" href="/group/detail/'+
                                    o.groupId +'" target="_blank">'+o.groupName+'</a>';
                                type = 2;
                                break;
                            case 'REJECT_GROUP_INTVIT':     //拒绝加入群消息
                                tmp = '<a userId = "'+ o.senderId +'" class="mr5" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                    o.senderName + '</a>拒绝加入群<a class="ml5" href="/group/detail/'+
                                    o.groupId +'" target="_blank">'+o.groupName+'</a>';
                                type = 2;
                                break;
                            case 'REMOVE_GROUP_MEMBER':    //移除群成员消息
                                tmp = '您已被移出群<a class="ml5" href="/group/detail/'+ o.groupId +'" target="_blank">' +
                                    o.groupName + '</a>';
                                type = 2;
                                break;
                            case 'QUIT_GROUP':              //退出群消息
                                tmp = '<a userId = "'+ o.senderId +'" class="mr5" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                    o.senderName + '</a>已退出群<a class="ml5" href="/group/detail/'+
                                    o.groupId +'" target="_blank">'+o.groupName+'</a>';
                                type = 2;
                                break;
                            case 'APPLY_GROUP':             //申请加入群
                                tmp = '<a class="mr5" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                    o.senderName + '</a>申请加入群<a class="ml5" href="/group/detail/'+
                                    o.groupId +'" target="_blank">'+o.groupName+'</a>：' + content;
                                    addBtn = '<div class="msg-item-btnbox btn-double"><div class="icon icon-right J-apply-group"></div><div class="icon icon-delete btn-delete"></div></div>';
                                type = 2;
                                break;
                            case 'DISMISS_GROUP':  //解散群
                                tmp = '<a userId = "'+ o.senderId +'" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                    o.senderName + '</a>解散了群<a class="ml5" href="/group/detail/'+
                                    o.groupId +'" target="_blank">'+o.groupName+'</a>';
                                type = 2;
                                break;
                            case 'AGREE_GROUP_APPLY': //群主已同意用户的入群申请
                                tmp = '<a userId = "'+ o.senderId +'" href="/expert/detail/'+ o.senderId +'" target="_blank">' +
                                    o.senderName + '</a>已同意您加入群<a class="ml5" href="/group/detail/'+
                                    o.groupId +'" target="_blank">'+o.groupName+'</a>';
                                type = 2;
                                break;
                            default :
                                break;
                        }

                        if (!o.isRead) {
                            tmp = "<b>" + tmp + "</b>";
                        }

                        //分类判定
                        switch(type){
                            case 0:
                                systemMsgIdArr.push(o.messageId);
                                break;
                            case 1:
                                isFixHight = "msg-item-fix";
                                if (o.isHandler) {
                                    return true;
                                    //addBtn = '<div class="msg-item-btnbox">已处理<div class="icon icon-delete btn-delete btn-double"></div></div>';
                                }
                                break;
                            case 2:
                                
                                isFixHight = "msg-item-fix";
                                if (o.isHandler) {
                                    return true;
                                    //addBtn = '<div class="msg-item-btnbox">已处理<div class="icon icon-delete btn-delete btn-double"></div></div>';
                                }
                                break;
                            default:
                                break;
                        }

                        //数据集合
                        var data = {
                            content: tmp,
                            isFixHight: isFixHight,
                            addBtn: addBtn,
                            messageId: o.messageId,
                            userId: o.senderId,
                            groupId: o.groupId
                        }

                        var html = util.template(msgHtml, data);
                        switch(type){
                            case 0:
                                systemTmp = systemTmp + html;
                                break;
                            case 1:
                                atTmp = atTmp + html;
                                break;
                            case 2:
                                groupTmp = groupTmp + html;
                                break;
                            default:
                                break;
                        }

                    });
                }

                //append数据
                $sysContent.append(systemTmp);
                $atContent.append(atTmp);
                $groupContent.append(groupTmp);
                
                //空数据时候的设置
                currentSystemMsgLen = $sysContent.find(".msg-item").length;
                currentAtMsgLen = $atContent.find(".msg-item").length;
                currentGroupMsgLen = $groupContent.find(".msg-item").length;

                if (currentSystemMsgLen == 0) {
                    appendSysNoneMsg();
                };

                if (currentAtMsgLen == 0) {
                    appendAtNoneMsg();
                };

                if (currentGroupMsgLen == 0){
                    appendGroupNoneMsg();
                };

                //去除当前dot
                $tabBtn.eq(currentIndex).find(".index-icon").remove();
            }

            //将所有消息标记为已读
            var markRead = function(arr){
                
                util.setAjax(
                    inter.getApiUrl().markUnreadMsg,
                    { messageId: arr },
                    function(json){
                        console.log("clean system message");
                        socket.getDataForSelf();
                    },
                    function(){
                        $.alert('服务器繁忙，请稍后再试。');
                    },
                    'POST'
                );
            }

            //处理红点
            var dotHandle = function(){

                if (currentAtMsgLen > 0) {
                    $tabBtnFriend.append(dotHTML);
                };

                if (currentGroupMsgLen > 0) {
                    $tabBtnGroup.append(dotHTML);
                };

                if (currentSystemMsgLen == 0 && currentAtMsgLen > 0) {
                    $tabBtnFriend.trigger("click");
                };

                if (currentSystemMsgLen == 0 && currentAtMsgLen == 0 && currentGroupMsgLen > 0) {
                    $tabBtnGroup.trigger("click");
                };

                if (currentSystemMsgLen == 0 && currentAtMsgLen == 0 && currentGroupMsgLen == 0) {
                    $tabBtnSystem.trigger("click");
                };
                //去除当前dot
                $tabBtn.eq(currentIndex).find(".index-icon").remove();
                
            }

            //移除添加按钮
            var removeAddBtn = function(btn){
                btn.parent(".msg-item-btnbox").removeClass("btn-double");
                btn.replaceWith('已同意');
            }

            //绑定按钮事件
            var bindBtn = function(){
                var self = this,
                    deleteBtn = $(".msg-box").find(".btn-delete"),
                    addFriend = $('.J-add-friend'),
                    addGroup = $('.J-add-group'),
                    applyGroup = $('.J-apply-group');
                /**
                 * 同意加入圈
                 */
                addFriend.on('click',function(){
                    var $this = $(this),
                        $item = $this.closest('.msg-item'),
                        msgid = $item.attr('data-id'),
                        uid = $item.attr('data-uid'),
                        $msgContent = $this.closest(".msg-content");

                    var $load = $(loadingHTML);
                    $msgContent.append($load);

                    util.setAjax(
                        inter.getApiUrl().addFriend,
                        {
                            friendId : uid,
                            messageId : msgid
                        },
                        function(json){
                            if (json.status) {
                                removeAddBtn($this);
                            }else{
                                $.alert(json.error);
                            };

                            markRead([msgid]);
                            $load.remove();
                            //刷新tips数字
                            util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {
                                socket.getDataForSelf();
                            }, null, 'GET');

                        },
                        function(){
                            $.alert('服务器繁忙，请稍后再试。');
                            $load.remove();
                        },
                        'GET'
                    );

                });

                /**
                 * 同意加入群 (包含2种消息：  群主同意成员进入群 / 成员接受群主的入群邀请)
                 */
                addGroup.on('click',function(){
                    var $this = $(this),
                        $item = $this.closest('.msg-item'),
                        id = $item.attr('data-id'),
                        gid = $item.attr('data-gid'),
                        uid = $item.attr('data-uid'),
                        $msgContent = $this.closest(".msg-content");

                    var $load = $(loadingHTML);
                    $msgContent.append($load);

                    util.setAjax(
                        inter.getApiUrl().groupAgreeInvite,
                        {
                            userId : uid,
                            groupId : gid,
                            messageId : id
                        },
                        function(json){
                            if (json.status) {
                                removeAddBtn($this);
                            }else{
                                $.alert(json.error);
                            };
                            
                            markRead([id]);
                            $load.remove();

                            util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {
                                socket.getDataForSelf();
                            }, null, 'GET');
                        },
                        function(){
                            $.alert('服务器繁忙，请稍后再试。');
                            $load.remove();
                        },
                        'GET'
                    );

                });

                /**
                 * 同意成员加入群 (成员申请入群)
                 */
                applyGroup.on('click',function(){
                    var $this = $(this),
                        $item = $this.closest('.msg-item'),
                        id = $item.attr('data-id'),
                        gid = $item.attr('data-gid'),
                        uid = $item.attr('data-uid'),
                        $msgContent = $this.closest(".msg-content");

                    var $load = $(loadingHTML);
                    $msgContent.append($load);

                    util.setAjax(
                        inter.getApiUrl().groupAgreeApply,
                        {
                            userId : uid,
                            groupId : gid,
                            messageId : id
                        },
                        function(json){
                            if (json.status) {
                                removeAddBtn($this);
                            }else{  
                                $.alert(json.error);
                            };
                            
                            markRead([id]);
                            $load.remove();

                            util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {
                                socket.getDataForSelf();
                            }, null, 'GET');
                        },
                        function(){
                            $.alert('服务器繁忙，请稍后再试。');
                            $load.remove();
                        },
                        'GET'
                    );


                });

                /**
                 * 删除一条消息
                 */
                deleteBtn.on("click", function(){
                    var $this = $(this),
                        $item = $this.closest('.msg-item'),
                        id = $item.attr("data-id"),
                        $msgContent = $this.closest(".msg-content");

                    var $load = $(loadingHTML);
                    $msgContent.append($load);
                    //删除一行

                    util.setAjax(inter.getApiUrl().deleteMessageUrl, {id: parseInt(id)}, function(json){
                        if(json.status === '200'){
                            $item.remove();
                            $load.remove();

                            //删除一条记录以后，如果是最后一条，将显示没有数据的提示
                            switch(currentIndex){
                                case 0:
                                    currentSystemMsgLen--;
                                    if (currentSystemMsgLen == 0) {
                                        appendSysNoneMsg();
                                    };
                                    break;
                                case 1:
                                    currentAtMsgLen--;
                                    if (currentAtMsgLen == 0) {
                                        appendAtNoneMsg();
                                    };
                                    break;
                                case 2:
                                    currentGroupMsgLen--;
                                    if (currentGroupMsgLen == 0) {
                                        appendGroupNoneMsg();
                                    };
                                    break;
                                default:
                            }

                            util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {
                                socket.getDataForSelf();
                            }, null, 'GET');
                        }else{
                            $.alert(json.message);
                        }
                    }, function(){
                        $.alert('服务器繁忙，请稍后再试。');
                        $load.remove();
                    });
                });
            }

            var getMsg = function(callback){

                util.setAjax(
                    inter.getApiUrl().getMsgUnread,
                    {},
                    function(json){
                        var dataArr = json;
                        clearContent();
                        render(dataArr);
                        //将普通的系统消息标记已读
                        if (currentIndex == 0 && currentSystemMsgLen > 0) {
                            markRead(systemMsgIdArr);
                            util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {
                                socket.getDataForSelf();
                            }, null, 'GET');
                        };
                        bindBtn();
                        if (callback) { callback()};
                    },
                    function(json){
                        util.trace("Failed to fetch data, Develope by Young Foo" + json.error, "green");
                        //$.alert(json.error);
                    }, 'GET');

            }

        },

        searchTypeInstance: sts,
        searchData: searchData
    }
});