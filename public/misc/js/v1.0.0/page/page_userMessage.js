/**
 * @description: 消息  --(系统 群组  圈)
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/msgSocket'
],function(inter, util, common, socket){
    common.initLogin();

    // 消息列表构造函数
    var MessageList = function(){
        this.init();
    };

    MessageList.prototype = {
        init : function(){
            var self = this;

            self.type = 0;
            
            util.tab($(".tab-menu-btn"), "current", "", function(i){
                if (i == self.type) return;
                self.type = i;
                self.getMessageCount();
            });
            if (util.location().type == "at") {
                $(".tab-menu-btn").eq(1).trigger("click");
                return;
            };
            if (util.location().type == "group") {
                $(".tab-menu-btn").eq(2).trigger("click");
                return
            };

            self.getMessageCount();
        },

        //初始化消息列表
        getMessageCount : function(){
            var self = this,
                msgType = 'system',
                listPanel = $('.tab-table');
	        switch(self.type){
		        case 0:
			        msgType = 'system';
			        break;
		        case 1:
			        msgType = 'friends';
			        break;
		        case 2:
			        msgType = 'group';
			        break;
	        }

            self.page = arguments[0] || 0;
            self.pageSize = 10;
            self._loading('show');//self.type ? 'system' : ''

            util.setAjax(
                inter.getApiUrl().getMessageListUrl,
                {'msgType': msgType, 'page': self.page, 'pageSize': self.pageSize},
                function (json) {
                    if (json.error) {
                        self._loading('hide');
                        self._loadFailed(listPanel, json.error, function () {
                            self.getMessageCount();
                        })
                    } else {
                        self.totalCount = json.totalRowCount;
                        self.list = json.list;
                        self.bindPagination();
                        util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {
                            socket.getDataForSelf();
                        }, null, 'GET')
                    }
                },
                function () {
                    self._loading('hide');
                    self._loadFailed(listPanel, '服务器繁忙，请稍后再试。', function () {  })
                },
                'GET'
            );
        },

        //翻页获取列表
        getMessageList : function(){
            var self = this,
                msgType = 'system',
                listPanel = $('.tab-table');
            switch (self.type){
                case 0 : msgType = 'system';break;
                case 1 : msgType = 'friends';break;
                case 2 : msgType = 'group';break;
                default :msgType = 'system';break;
            }
            if(!self.list){
                if(!self.loading){
                    self._loading('show');
                }

                util.setAjax(
                    inter.getApiUrl().getMessageListUrl,
                    {'msgType': msgType, 'page': self.page, 'pageSize': self.pageSize},
                    function (json) {
                        if (json.error) {
                            self._loading('hide');
                            self._loadFailed(listPanel, json.error, function () {})

                        } else {
                            self.totalCount = json.totalRowCount;
                            self.list = json.list;
                            self._render();
                            //如果没有未读信息，去除状态
                            var isReadNum = $(".J-tab-content").find("b").length;
                            if (isReadNum == 0) {
                                var $thisTab = $(".tab-menu-btn").eq(self.type);
                                $thisTab.find(".index-icon").remove();
                            }
                        }
                    },
                    function () {
                        self._loading('hide');
                        self._loadFailed(listPanel, '服务器繁忙，请稍后再试。', function () { })
                    },
                    'GET'
                );
            }else{
                self._render();
            }
        },
        _loading : function(){
            var self = this,
                tab = $('.J-tab-content'),
                loading = $('.tab-loading'),
                reCall = false;

            if(arguments[0] === 'show'){
                loading.show().find('span').css({
                    top : (tab.height() - loading.find('span').height())/2,
                    left : (tab.width() - loading.find('span').width())/2
                });
                reCall = true;
            }else if(arguments[0] === 'hide'){
                loading.hide();
                reCall = false;
            }
            self.loading = reCall;
        },
        _render : function(){
            var self = this,
                listBox = $('.tab-table');

            listBox.find('tr').remove();
            listBox.append(self.render(self.list));
            self.list = null;
            self._loading('hide');
            self.initListEvent();
            self.initDeleteMessage();
        },

        /**
         * 渲染列表
         */
        render : function(list){
            var self = this,
                listTpl = [
                    '<tr class="#{cls}" data-id="#{messageId}" data-uid="#{userId}" data-gid="#{groupId}">',
                        '<td class="message-status"><span class="#{status}"></span></td>',
                        '<td colspan="2" class="system-message-content left"><div class="clearfix">#{content}</div><div class="system-message-time">#{msgTime}</div></td>',
                        '<td class="operate">',
                            '#{right}<a href="javascript:" title="删除" class="btn-delete ml10"><em class="icon icon-delete"></em></a>',
                        '</td>',
                    '</tr>'
                ].join(''),
                returnStr = [],
                $thisTab = $(".tab-menu-btn").eq(self.type);

            if(list && list.length){
                $.each(list, function(i, n){
                    var data = n,
                        rightLink = '',
                        content = data.content;
                    data.show = true, //ignored a message only set false, default true
                    data.status = n.isRead ? 'status-original' : 'status-highlight';
                    data.msgTime = util.dateFormat(new Date(data.msgTime.replace(/-/g, '/')), 'yyyy-MM-dd hh:mm:ss');
                    switch (data.msgType){
                        case 'COMMENT': // 评价消息
                            data.content = data.senderName + ' 评价了你，内容：<span class="gray">'+ content +
	                            '</span><a href="'+ util.strFormat(inter.getApiUrl().commentReplyUrl, [data.refId]) +'" target="_blank">回复</a>';
                            break;
                        case 'REPLY': // 回复消息
                            data.content = data.senderName + ' 回复了你，内容：<span class="gray">'+ content +
	                            '</span><a href="'+ util.strFormat(inter.getApiUrl().commentReplyUrl, [data.refId]) +'" target="_blank">回复</a>';
                            break;
                        case 'TRANSFER_IN': // 转入消息
                            data.content = data.senderName + ' 给你转入了 <span class="green">'+ (data.currency == 2 ? '$' : '￥') + data.amount + '</span>';
                            break;
                        case 'TRANSFER_OUT': // 转出消息
                            data.content = '你的账户转出 <span class="red">'+ (data.currency == 2 ? '$' : '￥') + data.amount +
	                            '</span>，转给：'+ data.senderName;
                            break;
                        case 'ADD_FRIENDS': //收到邀请加圈消息
                            if(data.isHandler){
                                rightLink = '<span class="text-right dib">已同意</span>';
                            }else{
                                rightLink = '<a href="javascript:;" class="J-add-friend" title="同意"><em class="icon icon-right '+ data.isHandle +'"></em></a>';
                            }
                            data.right = rightLink;
                            data.userId = data.senderId;
                            data.content = '<a userId = "'+ data.senderId +'" class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
	                        data.senderName + '</a>邀请您加入Ta的圈。<br>' + content;
                            break;
                        case 'AGREE_FRIENDS': //同意加入圈
                            data.content = '<a userId = "'+ data.senderId +'" class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
                                data.senderName + '</a>已经同意加入您的圈。<br>';
                            break;
	                    case 'INVIT_GROUP_MEMBER':    //邀请群成员消息
                            if(data.isHandler){
                                rightLink = '<span class="text-right dib">已同意</span>';
                            }else{
                                rightLink = '<a href="javascript:;" class="J-add-group" title="同意"><em class="icon icon-right '+ data.isHandle +'"></em></a>';
                            }
                            data.right = rightLink;
                            data.userId = data.senderId;
                            data.content = '<a class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
                                data.senderName + '</a><span class="fl">邀请您加入群&nbsp;</span><a class="user-name" href="/group/detail/'+
                                data.groupId +'" target="_blank">"'+data.groupName+'"</a>';
		                    break;
	                    case 'AGREE_GROUP_INVIT':       //用户已同意群主的入群邀请
                            data.content = '<a userId = "'+ data.senderId +'" class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
                                data.senderName + '</a>已加入群&nbsp;"'+ data.groupName +'"';
		                    break;
	                    case 'REJECT_GROUP_INTVIT':     //拒绝加入群消息
                            data.content = '<a userId = "'+ data.senderId +'" class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
                                data.senderName + '</a>拒绝加入群&nbsp;"'+ data.groupName +'"';
		                    break;
	                    case 'REMOVE_GROUP_MEMBER':    //移除群成员消息
                            data.show = true ; // 不显示被移除的消息
                            data.content = '<span class="fl">您已被移出群&nbsp;</span><a class="user-name" href="/group/detail/'+ data.groupId +'" target="_blank">"' +
                                data.groupName + '"</a>';
		                    break;
	                    case 'QUIT_GROUP':              //退出群消息
                            data.content = '<a userId = "'+ data.senderId +'" class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
                                data.senderName + '</a>已退出群&nbsp;"' + data.groupName +'"';
		                    break;
	                    case 'APPLY_GROUP':      //申请加入群
                            if(data.isHandler){
                                rightLink = '<span class="text-right dib">已同意</span>';
                            }else{
                                rightLink = '<a href="javascript:" class="J-apply-group" title="同意"><em class="icon icon-right '+ data.isHandle +'"></em></a>';
                            }
                            data.right = rightLink;
                            data.userId = data.senderId;
                            data.content = '<a class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
                                data.senderName + '</a>申请加入群 "'+ data.groupName +'"<br>' + content;
		                    break;
                        case 'RESUME_FINISH': //海外简历翻译完毕
                            data.content = '<a class="user-name" href="javascript:;">嗨啰翻译中心</a><span class="fl"><span class="fl">已把您的英文海外简历翻译完成，</span>' +
                                '<a class="user-name" href="'+ content +'" target="_blank">立刻查看。</a><br>';
                            break;
                        case 'DISMISS_GROUP':  //解散群
                            data.content = '<a userId = "'+ data.senderId +'" class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
                                data.senderName + '</a>解散了群&nbsp;"'+ data.groupName +'"';
                            break;
                        case 'AGREE_GROUP_APPLY': //群主已同意用户的入群申请
                            data.content = '<a userId = "'+ data.senderId +'" class="user-name" href="/expert/detail/'+ data.senderId +'" target="_blank">' +
                                data.senderName + '</a>已同意您加入群&nbsp;"'+ data.groupName +'"';
                            break;
	                    default :
                            break;
                    }
                    if(!n.isRead){
                        data.content = '<b>'+ data.content +'</b>';
                        //取消tab上的红点消息提示
                        if ($thisTab.find(".index-icon").length > 0){
                            $thisTab.siblings(".tab-menu-btn").on("click", function(){
                                $thisTab.find(".index-icon").remove();
                            });
                        }
                    }

                    if(!!data.show){
                        returnStr.push(util.template(listTpl, data));
                    }
                });
            }else{
                returnStr.push('<tr><td align="center">还没有任何消息</td></tr>');
            }

            return returnStr.join('');
        },
        /**
         * 获取数据失败
         */
        _loadFailed : function(panel, errMsg, call){
            var errorTpl = $('<tr><td><div class="load-failed">'+ (errMsg || '列表加载失败') + '<a href="">重新加载</a></div></td></tr>');

            panel.html(errorTpl);
            errorTpl.find('a').on('click', function(e){
                errorTpl.remove();
                call && call();
                e.preventDefault();
            })
        },
        bindPagination : function(){
            var self = this;
            $('.page').pagination(self.totalCount, {
                prev_text: '上一页',
                next_text: '下一页',
                link_to: 'javascript:',
                items_per_page: self.pageSize,
                num_display_entries: 6,
                current_page: self.page,
                num_edge_entries: 2,
                //prev_show_always: false,
                //next_show_always: false,
                callback: function(page_id, jq){
                    self.page = page_id;
                    self.getMessageList();
                }
            })
        },
        initDeleteMessage : function(){
            var self = this,
                deleteBtn = $(".btn-delete"),
                addFriend = $('.J-add-friend'),
                addGroup = $('.J-add-group'),
                applyGroup = $('.J-apply-group');
            /**
             * 同意加入圈
             */
            addFriend.on('click',function(){
                var $this = $(this),
	                $tr = $this.closest('tr'),
                    mid = $tr.attr('data-id'),
                    uid = $tr.attr('data-uid');
				$tr.find('.message-status .status-highlight').attr('class','status-original');

                util.setAjax(
                    inter.getApiUrl().addFriend,
                    {
                        friendId : uid,
                        messageId : mid
                    },
                    function(json){
                        $this.replaceWith('<span class="text-right dib">已同意</span>');
                        $this.off('click');
                    },
                    function(){
                        $.alert('服务器繁忙，请稍后再试。');
                    },
                    'GET'
                );

            });
            /**
             * 同意加入群主的群 (群主邀请成员进入群)
             */
            addGroup.on('click',function(){
                var $this = $(this),
                    $tr = $this.closest('tr'),
                    id = $tr.attr('data-id'),
                    gid = $tr.attr('data-gid'),
                    uid = $tr.attr('data-uid'),
                    tips = $.tips('loading');

                $tr.find('.message-status .status-highlight').attr('class','status-original')
                util.setAjax(
                    inter.getApiUrl().groupAgreeInvite,
                    {
                        groupId : gid,
                        messageId : id
                    },
                    function(json){
                        tips.close();
                        if(json.status == 0){
                            $.alert(json.error);
                        }else{
                            $this.replaceWith('<span class="text-right dib">已同意</span>');
                        }
                    },
                    function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试。');
                    },
                    'GET'
                );

            });
            /**
             * 同意成员加入群 (成员申请入群)
             */
            applyGroup.on('click',function(){
                var $this = $(this),
                    $tr = $this.closest('tr'),
                    id = $tr.attr('data-id'),
                    gid = $tr.attr('data-gid'),
                    uid = $tr.attr('data-uid'),
                    tips = $.tips('loading');

                $tr.find('.message-status .status-highlight').attr('class','status-original')

                util.setAjax(
                    inter.getApiUrl().groupAgreeApply,
                    {
                        userId : uid,
                        groupId : gid,
                        messageId : id
                    },
                    function(json){
                        tips.close();
                        if(json.status == 0){
                            $.alert(json.error);
                        }else{
                            $this.replaceWith('<span class="text-right dib">已同意</span>');
                        }

                    },
                    function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试。');
                    },
                    'GET'
                );

            });

            deleteBtn.on("click", function(){
                var $this = $(this);
                $.confirm('你确定要删除此条消息吗？', function(){
                    self.handleDeleteOK($this);
                });
            });
        },
        initListEvent : function(){
            var self = this,
                listItem = $(".tab-table tr");

            listItem.hover(function(){
                $(this).addClass('odd');
            }, function(){
                $(this).removeClass('odd');
            });
        },
        handleDeleteOK : function($this){
            var self = this,
                $tr = $this.closest('tr'),
                items = $tr.siblings('tr'),
                id = $tr.attr('data-id');
            //删除一行
            var tips = $.tips('loading');
            util.setAjax(inter.getApiUrl().deleteMessageUrl, {id: parseInt(id)}, function(json){
                tips.close();
                if(json.status === '200'){
                    if(!items.length && self.page >0){
                        self.page--;
                    }
                    self.getMessageCount(self.page);
                }else{
                    $.alert(json.message);
                }
            }, function(){
                tips.close();
                $.alert('服务器繁忙，请稍后再试。');
            });

        }
    };
    new MessageList();
});