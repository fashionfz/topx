/**
 * @description: 聊天模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 **/
define('module/chat', [
    'common/interface',
    'common/util',
    'module/cookie',
    'module/uuid',
    'module/webIM',
    'module/placeholder',
    'module/pinyin',
    'module/cursorControl',
    'module/clipboard',
    'module/emotion',
    'module/msgLS'
], function (inter, util, cookie, uuid, webIM, placeholder, pinyin, cursorControl, clipboard, emotionM, msgLS) {
    var youList = null,
        cursor = null,
        linkmanApi = null,
        chatContentTpl = [
            '<div id="chatWrap" class="clearfix">',
                '<div id="chatTitleDown" class="none">',
                    '<p class="notice none"><span class="chat-icon i-red"></span><span class="Js-content"></span><span class="chat-icon chat-btn-no none"></span></p>',
                    '<p class="invite-video none">',
                        '<span class="chat-icon i-red"></span><strong class="user-name"></strong>的视频邀请&nbsp;<span class="keep-time">(60)</span>',
                        '<span class="chat-icon chat-btn-ok"></span><span class="chat-icon chat-btn-no"></span>',
                    '</p>',
                    '<p class="invite-voice none">',
                        '<span class="chat-icon i-red"></span><strong class="user-name"></strong>的语音邀请&nbsp;<span  class="keep-time">(60)</span>',
                        '<span class="chat-icon chat-btn-ok"></span><span class="chat-icon chat-btn-no"></span>',
                    '</p>',
                '</div>',
                '<div id="contentLeft" class="fl">',
                    '<div class="userTypeTab clearfix">',
                        '<div class="home-num home-num-trade none" title="#{msgTip}">',
                            '<div class="index-icon left"></div>',
                            '<div class="center"><span class="home-num-txt">#{msgNum}</span></div>',
                            '<div class="index-icon right"></div>',
                        '</div>',
                        '<a class="current" href="javascript:">会话</a>',
                        '<a class="lnl" href="javascript:">圈</a>',
                    '</div>',
                    '<div class="chatSearch clearfix">',
                        '<input class="txt fl searchInput" type="text" placeholder="搜索用户">',
                        '<input id="newGroupBtn" class="chat-icon im-create-discussion fl" type="button" value="" title="创建多人会话">',
                    '</div>',
                    '<ul class="user-list">',
                        '<li class="loading"><em></em></li>',
                    '</ul>',
                '</div>',
                '<div id="contentTitle" class="fl">',
                    '<div class="cr-tools">',
                        '<div class="setting-panel none">',
                            '<em class="chat-icon tips"></em>',
                            '<ul class="setting-list">',
                                '<li><em></em>机器翻译</li>',
                                //'<li><em></em>自动翻译</li>',
                            '</ul>',
                        '</div>',
                        '<a id="translateSetting" href="javascript:">',
                            '<em class="chat-icon setting"></em>',
                        '</a>',
                        '<a id="chatMenuMini" href="javascript:"><em class="chat-icon mini"></em></a>',
                    '</div>',
                '</div>',
                '<div id="contentRight" class="fl">',
                    '<div class="loading"><em></em></div>',
                '</div>',
            '</div>'
        ].join(''),
        chatPanelTpl = [
            '<div id="chatTitle">',
                '<div class="tl-user-panel none">',
                    '<div class="pending none"></div>',
                    '<div class="userTypeTab">嗨啰翻译中心</div>',
                    '<div class="chatSearch clearfix">',
                        '<input class="txt fl searchInput" type="text" placeholder="搜索嗨啰官方翻译者">',
                    '</div>',
                    '<ul class="user-list">',
                        '<li class="loading"><em></em></li>',
                    '</ul>',
                '</div>',
                '<strong class="user-name" title="#{name}">#{name}</strong>',
                '<span class="chat-icon i-voice" title="开始语音通话"></span>',
                '<span class="chat-icon i-video" title="开始视频通话"></span>',
                '<span class="chat-icon i-translate" title="邀请翻译者"></span> ',
            '</div>',
            '<div id="chatContent"><div class="msg-box"></div></div>',
            '<div id="chatUtil"></div>'
        ].join(''),
        utilToolsTpl = [
            '<div class="util-tools clearfix">',
                '<span class="tools-item util-emotion">',
                    '<label class="item-label"><em class="chat-icon emotion-select-icon"></em>表情</label>',
                '</span>',
                '<span class="tools-item util-file">',
                    '<label for="fileUpload" class="item-label"><em class="chat-icon file-input-icon"></em>文件</label>',
                    '<input type="file" class="file-input" id="fileUpload">',
                '</span>',
            '</div>'
        ].join(''),
        utilChatInputTpl = [
            '<div class="util-text">',
                '<div id="chatEditPanel" class="chat-input" contenteditable="true"></div>',
                '<textarea id="chatInput" placeholder="发送内容"></textarea>',
                '<span class="chatTips red"></span>',
                '<input id="chatBtn" class="btn-xs btn-default  btn-green" type="button" value="   发送    ">',
            '</div>'
        ].join(''),
        groupChatPanelTpl = [
            '<div id="chatTitle" data-gid="#{id}" data-type="#{type}">',
                '<div class="group-manage-panel none">',
                    '<em class="chat-icon tips"></em>',
                    '<ul class="user-list">',
                        '<li><a href="/user/group/editInfo?gid=#{id}"><em class="chat-icon i-group-editName"></em>修改群资料</a></li>',
                        '<li><a href="/user/group/memberList?gid=#{id}"><em class="chat-icon i-group-member"></em>管理群成员</a></li>',
                        '<li><a class="btn-group-remove" href="javascript:"><em class="chat-icon i-group-remove"></em>删除群组</a></li>',
                    '</ul>',
                '</div>',
                '<div class="edit-group-panel none">',
                    '<em class="chat-icon tips"></em>',
                    '<div class="edit-group-box">',
                        '<div class="loading none"></div>',
                        '<input id="groupName" class="txt" type="text" value="" placeholder="请输入群组/会话名称" maxlength="50">',
                        '<button class="btn-default btn-lg btn-green btn-editIM-groupName"><em class="chat-icon i-chat-right"></em></button>',
                    '</div>',
                '</div>',
                '<div class="group-user-panel none">',
                    '<em class="chat-icon tips"></em>',
                    '<ul class="user-list">',
                        '<li class="loading"><em></em></li>',
                    '</ul>',
                '</div>',
                '<div class="at-user-panel none">',
                    '<div class="pending none"></div>',
                    '<div class="userTypeTab">',
                        '圈',
                        /*'<a class="current" href="javascript:">圈</a>',
                        '<a class="lnl" href="javascript:"></a>',*/
                    '</div>',
                    '<div class="chatSearch clearfix">',
                        '<input class="txt fl searchInput" type="text" placeholder="搜索圈好友">',
                    '</div>',
                    '<ul class="user-list">',
                        '<li class="loading"><em></em></li>',
                    '</ul>',
                    '<div class="sel-user-btns">',
                        '<span class="chat-icon chat-btn-ok" title="确认邀请"></span>',
                        '<span class="chat-icon chat-btn-no" title="关闭"></span>',
                    '</div>',
                '</div>',
                '<strong class="user-name" title="#{name}">#{name}</strong>',
                '<span class="i-look" title="查看其它成员">#{member}</span>',
                '<span class="chat-icon i-group-invitation" title="邀请"></span>',
                '<span class="chat-icon i-group-editName" title="修改名称"></span>',
                '<span class="chat-icon i-group-manage none" title="管理"></span>',
                '<span class="chat-icon i-group-out" title="退出"></span>',
            '</div>',
            '<div id="chatContent"><div class="msg-box"></div></div>',
            '<div id="chatUtil"></div>'
        ].join(''),
        linkmanTpl = [
            '<li class="user-common clearfix" data-uid="#{userId}">',
                '<div class="user-head fl"><img src="#{avatar}"><span class="chat-icon user-head-i"></span></div>',
                '<div class="user-name fl" title="#{userName}">#{userName}</div>',
            '</li>'
        ].join(''),
        atFriendsTpl = [
            '<li class="user-common clearfix" data-uid="#{userId}">',
                '<div class="sel-user chat-icon sel-base fl"><em class="#{cls}"></em></div>',
                '<div class="user-head fl"><img src="#{avatar}"><span class="chat-icon user-head-i"></span></div>',
                '<div class="user-name at-user-name fl">#{userName}</div>',
            "</li>"
        ].join(""),
        groupMemberTpl = [
            '<li class="user-common clearfix" data-uid="#{userId}">',
                '<div class="user-head fl"><img src="#{avatar}"><span class="chat-icon user-head-i"></span></div>',
                '<div class="user-name fl"><span title="#{userName}">#{userName}</span></div>',
                '<div class="chat-icon user-icon #{iconCls} fl" title="#{title}"></div>',
            '</li>'
        ].join(''),
        groupTpl = [
            '<li class="user-common clearfix" data-gid="#{groupId}">',
                '<div class="user-head fl"><img src="#{groupAvatar}"></div>',
                '<div class="user-name fl" title="#{groupName}">#{groupName}</div>',
            '</li>'
        ].join(''),
        userNone = '<li class="user-none"><em class="chat-icon no-user"></em><br>#{text}</li>',
        notWebSocketTemp = [
            '<div class="socket-err" style="',
            'position: absolute;',
            'z-index:999;',
            'margin-top: -31px;',
            'height: 30px;',
            'line-height: 30px;',
            'text-align: center;',
            'width: 390px;',
            'background: #FFFFD8;',
            'color: #666;',
            '">建立通信失败，请查看<a>问题详情</a></div>'
        ].join(''),
        translatorTpl = [
            '<li class="user-common clearfix" data-uid="#{userId}">',
                '<div class="tl-user-sel none"><span class="chat-icon chat-btn-ok" title="选择该翻译者"></span></div>',
                '<div class="user-head fl"><img src="#{headUrl}"><span class="chat-icon user-head-i"></span></div>',
                '<div class="tl-user-info fl">',
                    '<div class="user-name">#{userName}</div>',
                    '<div class="info-comment clearfix">',
                        '<span class="icon icon-star-base fl" title="平均分：#{averageScore}分">',
                            '<span class="icon icon-star-on" style="width:#{commentWidth}px"></span>',
                        '</span>',
                        '<span class="star-num fl">(#{commentCount})</span>',
                    '</div>',
                '</div>',
            '</li>'
        ].join(''),
        translatorNone = '<li class="user-none"><em class="chat-icon no-user"></em><br>#{text}</li>',
        emotionPanelTpl = [
            '<div class="emotion-panel">',
                '<em class="chat-icon icon-bottom-tips"></em>',
                '<ul class="emotion-list clearfix"></ul>',
            '</div>'
        ].join('');

    return {
        /**
         * 初始化webIM
         */
        init: function () {
            this.curUid = parseInt(cookie.get('_u_id')) || 0;
            this.curToken = cookie.get('_u_tk');
            this.setData();
            this.openChat();
            window.isLoadChat = true;
            window.chatAct = this;
            var formatChat = cookie.get('formatChat');
            if(!formatChat){
                msgLS.formatChat();
                cookie.set('formatChat', 1, new Date(new Date().getTime()+30*24*60*60*1000));
            }
        },
        /**
         * 设置面板数据
         */
        setData : function(){
            var self = this,
                mark = $('.J-login-mark'),
                meHead = mark.find('.img-full').prop('src'),
                meName = decodeURIComponent(cookie.get('_u_nm')).replace(/\+/g, ' ');

            window.me = {
                'headUrl': meHead || util.getAvatar(self.curUid),
                'name': meName,
                'id': self.curUid,
                'token': self.curToken
            };
        },
        /**
         * 获取最近联系人数据
         */
        getLinkmanData : function(call){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                loadTpl = '<li class="loading"><em></em></li>';

            listPanel.html(loadTpl);
            linkmanApi && linkmanApi.abort();
            linkmanApi = util.setAjax(inter.getApiUrl().getChatRecUrl, {'msgType': 'no_system', 'page': 0, 'pageSize': 2000}, function(json){
                if(json.error){
                    self.loadFailed(listPanel, function(){
                        self.getLinkmanData(call);
                    })
                }else{
                    window.linkmanList = json.list;
                    call && call(json.list);
                }
            },function(){
                self.loadFailed(listPanel, function(){
                    self.getLinkmanData(call);
                })
            }, 'GET');
        },
        /**
         * 获取收藏夹数据
         */
        getFavoritesData : function(call){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                loadTpl = '<li class="loading"><em></em></li>';

            listPanel.html(loadTpl);
            util.setAjax(inter.getApiUrl().queryFavorite, {'page': 0, 'pageSize': 2000}, function(json){
                if(json.error){
                    self.loadFailed(listPanel, function(){
                        self.getFavoritesData(call);
                    })
                }else{
                    window.favoritesList = json.list;
                    call && call(json.list);
                }
            },function(){
                self.loadFailed(listPanel, function(){
                    self.getFavoritesData(call);
                })
            }, 'GET');
        },
        /**
         * 获取圈子数据
         */
        getAtData : function(call){
            var self = this,
                listPanel = $('#contentLeft .user-list');

            util.setAjax(inter.getApiUrl().queryFriends, {'page': 0, 'pageSize': 2000}, function(json){
                if(json.error){
                    self.loadFailed(listPanel, function(){
                        self.getAtData(call);
                    });
                }else{
                    window.atList = json.list;
                    call && call(json.list);
                }
            },function(){
                self.loadFailed(listPanel, function(){
                    self.getAtData(call);
                });
            }, 'GET');
        },
        /**
         * 获取最近联系人失败
         */
        loadFailed : function(listObj, call){
            var errorTpl = $('<li class="load-failed">列表加载失败 <a href="javascript:">重新加载</a></li>');

            listObj.html(errorTpl);
            errorTpl.find('a').on('click', function(e){
                call && call();
                e.preventDefault();
            })
        },
        /**
         * 渲染联系人列表
         */
        renderLinkman : function(call){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                isOpenChat = $.parseJSON(cookie.get('isOpenChat'+self.curUid)) || {},
                listStr = [],
                unreadList = [];

            self.getLinkmanData(function(list){
                if(!$.isEmptyObject(isOpenChat)){
                    var newList = [],
                        hasOpenChat = false;
                    newList.push(isOpenChat);
                    if(list && list.length){
                        $.each(list, function(i, n){
                            if(n.userId == isOpenChat.userId || (n.groupId && isOpenChat.groupId && n.groupId == isOpenChat.groupId)){
                                hasOpenChat = true;
                            }
                        });
                        !hasOpenChat && list.unshift(isOpenChat);
                    }else{
                        list = newList;
                    }
                }
                if(list && list.length){
                    youList = list;
                    $.each(youList, function(i, n){
                        if(n.groupId){
                            switch(n.groupType){
                                case 'multicommunicate':
                                    n.groupAvatar = ued_conf.root + 'images/session.png';
                                    break;
                                case 'normal':
                                    n.groupAvatar = n.groupAvatar || (ued_conf.root + 'images/group.png');
                                    break;
                                case 'translate':
                                    n.groupAvatar = ued_conf.root + 'images/translate.png';
                                    break;
                                default:
                                    n.groupAvatar = ued_conf.root + 'images/translate.png';
                                    break;
                            }
                            listStr.push(util.template(groupTpl, {
                                groupId: n.groupId,
                                groupName: n.groupName || '翻译群',
                                groupAvatar: n.groupAvatar
                            }));
                        }else{
                            listStr.push(util.template(linkmanTpl, n));
                        }
                        if(n.msgNum){
                            if(($.isEmptyObject(isOpenChat) && i > 0) || (!$.isEmptyObject(isOpenChat) && n.userId != isOpenChat.userId) || (!$.isEmptyObject(isOpenChat) && n.groupId != isOpenChat.groupId)){
                                unreadList.push(n);
                            }
                        }
                    });

                    listPanel.html(listStr.join(''));
                    self.setCurChat($.isEmptyObject(isOpenChat) ? youList[0] : isOpenChat);
                    self.bindLinkmanEvent();
                    if(unreadList.length){
                        self.setTipChat(unreadList);
                    }
                }else{
                    listPanel.html(util.template(userNone, {text: '暂无会话记录'}));
                    self.setEmptyChatPanel();
                }
                call && call(list);
            });
        },
        /**
         * 渲染收藏夹列表
         */
        renderFavorites : function(){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                listStr = [];

            self.getFavoritesData(function(list){
                if(list && list.length){
                    youList = list;
                    $.each(youList, function(i, n){
                        n.avatar = n.headUrl;
                        listStr.push(util.template(linkmanTpl, n));
                    });
                    listPanel.html(listStr.join(''));
                    util.imgLoadError(listPanel.find('.user-head img'));
                    self.bindLinkmanEvent();
                }else{
                    listPanel.html(util.template(userNone, {text: '暂无记录'}));
                }
            });
        },
        /**
         * 渲染圈子列表
         */
        renderAt : function(call){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                loadTpl = '<li class="loading"><em></em></li>',
                listStr = [];

            listPanel.html(loadTpl);
            self.getAtData(function(list){
                if(list && list.length){
                    youList = list;
                    $.each(youList, function(i, n){
                        n.avatar = n.headUrl;
                        listStr.push(util.template(linkmanTpl, n));
                    });
                    listPanel.html(listStr.join(''));
                    util.imgLoadError(listPanel.find('.user-head img'));
                    self.bindLinkmanEvent();
                }else{
                    listPanel.html(util.template(userNone, {text: '暂无记录'}));
                }
                call && call(list);
            }, function(){

            });
        },
        /**
         * 渲染圈子列表用于创建群组
         */
        renderAtByNewGroup : function(call){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                selBtnTpl = [
                    '<div class="sel-user-btns">',
                        '<span class="chat-icon chat-btn-ok" title="确认"></span>',
                        '<span class="chat-icon chat-btn-no" title="关闭"></span>',
                    '</div>'
                ].join(''),
                loadTpl = '<li class="loading"><em></em></li>',
                listStr = [];

            listPanel.html(loadTpl);
            self.getAtData(function(list){
                if(list && list.length){
                    youList = list;
                    $.each(youList, function(i, n){
                        n.avatar = n.headUrl;
                        listStr.push(util.template(atFriendsTpl, n));
                    });
                    listPanel.html(listStr.join(''));
                    listPanel.height(341).after(selBtnTpl);
                    util.imgLoadError(listPanel.find('.user-head img'));
                    self.bindNewGroupEvent();
                }else{
                    listPanel.html(util.template(userNone, {text: '暂无记录'}));
                }
                call && call(list);
            });
        },
        /**
         * 绑定邀请圈好友入群事件
         */
        bindInviteAtJoinEvent : function(){
            var self = this,
                $chatTitle = $('#chatTitle'),
                atUserPanel = $('.at-user-panel'),
                listPanel = atUserPanel.find('.user-list'),
                selBtnsPanel = atUserPanel.find('.sel-user-btns');

            //绑定 搜索联系人event
            $chatTitle.find('.at-user-panel .chatSearch .searchInput').on('keyup', function (e) {
                self.searchTranslator($(this));
            });
            //选择被邀请的用户
            listPanel.find('li:not(.user-none)').off('click').on('click', function(){
                var $this = $(this),
                    uid = $this.attr('data-uid'),
                    checkBox = $this.find('.sel-user em'),
                    newData = self.inviteAt || [];

                if(!checkBox.hasClass('disabled')){
                    if(checkBox.hasClass('selected')){
                        checkBox.removeAttr('class');
                        newData.splice($.inArray(uid, newData), 1);
                    }else{
                        checkBox.attr('class', 'chat-icon selected');
                        newData.push(uid);
                    }
                    self.inviteAt = newData;
                }
            });
            //确认选择并发起邀请
            selBtnsPanel.find('.chat-btn-ok').off('click').on('click', function(){
                var pending = atUserPanel.find('.pending'),
                    errTips = null;

                pending.removeClass('none').height(atUserPanel.height());
                if(self.inviteAt && self.inviteAt.length){
                    $.each(self.inviteAt, function(i, n){
                        if(group && group.member){
                            $.each(group.member, function(x, y){
                                if(y.userId == n){
                                    self.inviteAt.splice($.inArray(n, self.inviteAt), 1);
                                    return false;
                                }
                            })
                        }
                    });
                    util.setAjax(inter.getApiUrl().inviteJoinGroup, {groupId: group.id, userIds: self.inviteAt, content: ''}, function(json){
                        if(json.error){
                            self._pendingTips(pending, json.error);
                            errTips = pending.find('.err-tips');
                            errTips.css('padding-top', (atUserPanel.height()-errTips.height())/2);
                        }else{
                            self._pendingTips(pending, '邀请成功！');
                            errTips = pending.find('.err-tips');
                            errTips.css('padding-top', (atUserPanel.height()-errTips.height())/2);
                            self.inviteAt = null;
                            setTimeout(function(){
                                atUserPanel.addClass('none');
                            }, 3000)
                        }
                    }, function(){
                        self._pendingTips(pending, '服务器繁忙，请稍后再试。');
                        errTips.css('padding-top', (atUserPanel.height()-errTips.height())/2);
                    })
                }else{
                    self._pendingTips(pending, '请邀请至少一位圈中联系人。');
                    errTips = pending.find('.err-tips');
                    errTips.css('padding-top', (atUserPanel.height()-errTips.height())/2);
                }
            });
            //取消选择
            selBtnsPanel.find('.chat-btn-no').off('click').on('click', function(){
                var $this = $(this);
                self.inviteAt = null;
                atUserPanel.addClass('none');
            })
        },
        /**
         * 绑定新建群选择邀请成员事件
         */
        bindNewGroupEvent : function(){
            var self = this,
                contentLeft = $('#contentLeft'),
                listPanel = contentLeft.find('.user-list'),
                selBtnsPanel = contentLeft.find('.sel-user-btns');
            //选择被邀请的用户
            listPanel.find('li:not(.user-none)').off('click').on('click', function(){
                var $this = $(this),
                    uid = $this.attr('data-uid'),
                    checkBox = $this.find('.sel-user em'),
                    checkBoxCls = checkBox.prop('class') || '',
                    newData = self.newGroupMember || [];

                if(checkBoxCls.indexOf('selected') > -1){
                    checkBox.removeAttr('class');
                    newData.splice($.inArray(uid, newData), 1);
                }else{
                    checkBox.attr('class', 'chat-icon selected');
                    newData.push(uid);
                }
                self.newGroupMember = newData;
            });
            //确认选择并创建群
            selBtnsPanel.find('.chat-btn-ok').off('click').on('click', function(){
                var loadingTpl = $('<div class="loadAddDiscussion"><div class="tips"></div></div>'),
                    loading = $('.loadAddDiscussion');
                if(self.newGroupMember && self.newGroupMember.length){
                    if(!loading.length){
                        contentLeft.prepend(loadingTpl);
                        loading = loadingTpl;
                    }else{
                        loading.removeClass('none');
                    }
                    util.setAjax(inter.getApiUrl().addMultiCommunicate, {userIds: self.newGroupMember}, function(json){
                        if(json.error){
                            loading.find('.tips').html(json.error);
                            setTimeout(function(){
                                loading.addClass('none').removeClass('no-load-gif');
                            }, 3000)
                        }else{
                            setTimeout(function(){
                                self.tabsType(0, null, function(){
                                    self.setCurChat(json);
                                });
                            }, 500)
                            loading.addClass('none');
                            self.newGroupMember = null;
                        }
                    }, function(){
                        loading.find('.tips').html('服务器繁忙，请稍后再试。');
                        setTimeout(function(){
                            loading.addClass('none').removeClass('no-load-gif');
                        }, 3000)
                    })
                }else{
                    $.alert('请邀请至少一位用户。');
                }
            });
            //取消选择并恢复样式
            selBtnsPanel.find('.chat-btn-no').off('click').on('click', function(){
                var $this = $(this);
                self.newGroupMember = null;
                $this.closest('.sel-user-btns').remove();
                listPanel.find('.sel-user').remove();
                listPanel.find('.user-name').css('max-width', 108);
                listPanel.height(381);
                self.bindLinkmanEvent();
            })
        },
        /**
         * 绑定切换联系人事件
         */
        bindLinkmanEvent : function(){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                chatContent = $('#contentRight');
            listPanel.find('li:not(.user-none)').off('click').on('click', function(){
                var $this = $(this),
                    uid = $this.attr('data-uid'),
                    gid = $this.attr('data-gid');

                if(youList && youList.length){
                    $.each(youList, function(i, n){
                        if((gid && gid == n.groupId) || (!n.groupId && n.userId && n.userId == uid) ){
                            self.setCurChat(n, 1);
                            return false;
                        }
                    });
                }else{
                    chatContent.html('<div class="load-failed">聊天数据加载失败，请<a href="javascript:location.reload();">刷新页面</a>再试</div>');
                }
            });
            listPanel.off('mousewheel').on('mousewheel', function(e, d){
                var t = $(this);
                if (d > 0 && t.scrollTop() === 0) {
                    e.preventDefault();
                }else {
                    if (d < 0 && (t.scrollTop() == t.get(0).scrollHeight - t.innerHeight())) {
                        e.preventDefault();
                    }
                }
            });
        },
        /**
         * 添加联系人
         */
        setLinkman : function (obj, call) {
            var self = this,
                listPanel = null,
                //tabsPanel = $('#contentLeft .userTypeTab a'),
                tpl = $(util.template(linkmanTpl, obj)),
                //isOpenChat = $.parseJSON(cookie.get('isOpenChat'+self.curUid)) || {},
                chatContent = $('#contentRight');

            if(obj.groupId){
                switch(obj.groupType){
                    case 'multicommunicate':
                        obj.groupAvatar = ued_conf.root + 'images/session.png';
                        break;
                    case 'translate':
                        obj.groupAvatar = ued_conf.root + 'images/translate.png';
                        break;
                    case 'normal':
                        obj.groupAvatar = (obj.groupAvatar+'?_r='+(+new Date())) || (ued_conf.root + 'images/group.png');
                        break;
                    default :
                        obj.groupAvatar = ued_conf.root + 'images/translate.png';
                        break;
                }
                tpl = $(util.template(groupTpl, obj));
            }
            self.tabsType(0, null, function(){
                listPanel = $('#contentLeft .user-list');
                if(youList){
                    var curItem = listPanel.find('li.user-highlight'),
                        hasItem = obj.groupId ? listPanel.find('li[data-gid="'+ obj.groupId +'"]') : listPanel.find('li[data-uid="'+ obj.userId +'"]'),
                        curGid = curItem.attr('data-gid'),
                        curUid = curItem.attr('data-gid'),
                        hasRender = false;
                    $.each(youList, function(i, n){
                        if((curUid && n.userId == curUid && obj.userId == curUid) || (n.groupId && obj.groupId && curGid && n.groupId == curGid && obj.groupId == curGid)){
                            hasRender = true;
                            return false;
                        }
                    });
                    if(hasRender){
                        /*window.group = null;
                        window.you = null;
                        self.setCurChat(obj);*/
                        return;
                    }
                    youList.unshift(obj);
                }else{
                    var newDataList = [];
                    newDataList.push(obj);
                    youList = newDataList;
                }
                if(!hasItem.length){
                    if(listPanel.find('.user-none').length && listPanel.find('.user-none').css('display') != 'none'){
                        listPanel.html(tpl);
                        //self.setCurChat(obj);
                    }else{
                        listPanel.prepend(tpl);
                    }
                    util.imgLoadError(tpl.find('img'));
                    tpl.off('click').on('click', function(){
                        if(youList && youList.length){
                            self.setCurChat(obj);
                        }else{
                            chatContent.html('<div class="load-failed">聊天数据加载失败，请<a href="javascript:location.reload();">刷新页面</a>再试</div>');
                        }
                    });
                }
                call && call(tpl);
            });
        },
        /**
         * 移除联系人
         */
        removeLinkman : function (obj) {
            var self = this,
                contentLeft = $('#contentLeft'),
                listPanel = contentLeft.find('.user-list'),
                tabsPanel = contentLeft.find('.userTypeTab a'),
                chatContent = $('#contentRight'),
                newYouData = [],
                removeItem = null,
                nextItem = null,
                nextJson = {};

            self.tabsType(0, null, function(){
                if(obj.groupId){
                    removeItem = listPanel.find('li[data-gid="'+ obj.groupId +'"]');
                }else{
                    removeItem = listPanel.find('li[data-uid="'+ obj.userId +'"]');
                }
                if(removeItem.hasClass('user-highlight')){
                    nextItem = removeItem.next();
                    if(!nextItem.length){
                        nextItem = removeItem.prev();
                    }
                    removeItem.remove();
                    if(nextItem.length){
                        var itemUid = nextItem.attr('data-uid'),
                            itemGid = nextItem.attr('data-gid');

                        $.each(youList, function(i, n){
                            if((itemGid && n.groupId && n.groupId == itemGid) || (itemUid && n.userId && n.userId == itemUid)){
                                nextJson = n;
                                return false;
                            }
                        });
                        self.setCurChat(nextJson);
                    }else{
                        listPanel.html(util.template(userNone, {text: '暂无会话记录'}));
                        self.setEmptyChatPanel();
                    }
                }else{
                    removeItem.remove();
                }
                if(youList){
                    $.each(youList, function(i, n){
                        if((obj.groupId && n.groupId && n.groupId == obj.groupId) || (!obj.groupId && n.userId && n.userId == obj.userId)){

                        }else{
                            newYouData.push(n);
                        }
                    });
                    youList = newYouData;
                }
            });
        },
        /**
         * 搜索联系人
         */
        searchLinkman : function ($this) {
            var searchNum = 0,
                keyword = $this.val(),
                listPanel = $this.closest('.chatSearch').siblings('.user-list'),
                listItem = listPanel.find('li'),
                listNone = listPanel.find('.user-none'),
                listName = listPanel.find('.user-name');

            listNone.hide();
            if ( !keyword || ('' == keyword) ) {
                if(listItem.length == 1 && listItem.hasClass('user-none')){
                    listNone.show();
                }
                listItem.removeClass('none');
                return;
            }

            listName.each(function () {
                var that = $(this),
                    li = that.closest("li"),
                    nameItem = that.text();

                //if (nameItem.search(new RegExp(keyword, 'i')) === -1) {
                if(nameItem.indexOf(keyword) > -1 || pinyin.getJP(nameItem).indexOf(keyword) > -1 || pinyin.getQP(nameItem).indexOf(keyword) > -1 || pinyin.getHP(nameItem).indexOf(keyword) > -1){
                    li.removeClass('none');
                    searchNum++
                } else {
                    li.addClass('none');
                }
            });
            //未搜到联系人
            if(searchNum === 0){
                if( !listNone.length){
                    listPanel.append(util.template(userNone, {text: '无搜索结果'}));
                }else{
                    listNone.show();
                }
            }
        },
        /**
         * 设置当前聊天对象
         */
        setCurChat : function(json, k){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                curItem = listPanel.find('li.user-highlight'),
                itemUid = curItem.attr('data-uid'),
                itemGid = curItem.attr('data-gid');
            if((itemGid && itemGid == json.groupId) || (itemUid && !json.groupId && itemUid == json.userId)){

            }else{
                if(window.fileUploading){
                    $.confirm('文件正在上传中，切换聊天对象会导致上传失败，确认是否切换？', function(){
                        self._setCurChatHelper(json, k);
                    });
                }else{
                    self._setCurChatHelper(json, k);
                }
            }
        },
        /**
         * 设置当前聊天对象辅助方法
         */
        _setCurChatHelper : function(json, k){
            var self = this,
                saveData = {},
                listPanel = $('#contentLeft .user-list'),
                curChatPanel = listPanel.find('li[data-uid="'+ json.userId +'"]'),
                curGroupPanel = listPanel.find('li[data-gid="'+ json.groupId +'"]'),
                tips = curChatPanel.find('.user-msg-tips');

            if(json.groupId){
                window.group = {
                    'type': json.groupType,
                    'name': json.groupName,
                    'id': json.groupId
                };
                window.you = null;
                saveData.groupId = json.groupId;
                saveData.groupType = json.groupType;
                saveData.groupName = json.groupName;
                if(json.groupType == 'normal'){
                    saveData.groupAvatar = json.groupAvatar;
                    window.group.avatar = json.groupAvatar;
                }
            }else{
                window.you = {
                    'headUrl': json.avatar,
                    'name': json.userName,
                    'id': json.userId
                };
                window.group = null;
                saveData.userId = json.userId;
                saveData.avatar = json.headUrl || json.avatar;
                saveData.userName = json.userName;
            }
            json.closeChatWindow = false;
            cookie.set('isOpenChat'+self.curUid, JSON.stringify(saveData));
            if(!curChatPanel.length && !curGroupPanel.length){
                self.setLinkman(json, function(tpl){
                    self._setCurChatPanel(json, tpl, k);
                });
            }else if(curGroupPanel.length){
                self._setCurChatPanel(json, curGroupPanel, k);
            }else{
                self._setCurChatPanel(json, curChatPanel, k);
            }
        },
        /**
         * 设置当前聊天窗口
         */
        _setCurChatPanel : function(json, curChatPanel, k){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                tips = curChatPanel.find('.user-msg-tips');

            if(curChatPanel.prop('class').indexOf('user-highlight') == -1){
                listPanel.find('.user-highlight').prop('class', 'user-common');
                curChatPanel.prop('class', 'user-highlight').find('.user-msg-icon').remove();
                if(tips.length){
                    tips.stop();
                }
                !k && listPanel.scrollTop(listPanel.find('li').index(curChatPanel)*curChatPanel.outerHeight());
                if(json.groupId){
                    if(json.groupAvatar){
                        curChatPanel.find('.user-head img').prop('src', json.groupAvatar);
                    }
                    self.initGroupPanel();
                }else{
                    self.initChatPanel();
                }
            }
            /***通过联系人列表，切换im后 设置视频状态***/
            var videoStatus = cookie.get('videoStatus');
            if(videoStatus == 0){
                webIM.changeVideo(false);
            }else if(videoStatus == 1){
                webIM.changeVideo(true);
            }
        },
        /**
         * 设置有未读消息联系人
         */
        setTipChat : function(uList){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                itemPanel = null;

            $.each(uList, function(i, n){
                itemPanel = listPanel.find('li[data-uid="'+ n.userId +'"]');
                if(n.groupId){
                    itemPanel = listPanel.find('li[data-gid="'+ n.groupId +'"]');
                }
                if(!itemPanel.length){
                    self.setLinkman(n, function(tpl){
                        self._setTipChatPanel(tpl);
                    });
                }
                self._setTipChatPanel(itemPanel);
            })
        },
        /**
         * 设置有未读消息联系人辅助方法
         */
        _setTipChatPanel : function(itemPanel){
            var self = this;
            if(itemPanel.hasClass('user-common')){
                itemPanel.prop('class', 'user-msg');
                if(!itemPanel.find('.user-msg-tips').length){
                    itemPanel.append('<div class="user-msg-tips"><em class="icon msg-icon" title="有新消息"></em></div>');
                }
            }
            self.setTipAnimation(itemPanel.find('.user-msg-tips'));
        },
        /**
         * 设置动画
         */
        setTipAnimation : function(obj){
            var self = this,
                alpha = parseInt(obj.css('opacity')) || 0;

            obj.stop().animate({opacity: (alpha ? 0 : 1)}, 'slow', 'swing', function(){
                self.setTipAnimation(obj);
            })
        },
        /**
         * 无联系人时设置聊天窗口
         */
        setEmptyChatPanel : function(){
            var self = this,
                chatContent = $('#contentRight');

            chatContent.find('.loading').remove();
            if(!chatContent.find('.emptyChatPanel').length){
                chatContent.html('<div class="emptyChatPanel">Sorry! 还没有收到任何聊天消息！</div>');
            }
            cookie.set('isOpenChat'+self.curUid, '');
        },
        /**
         * 初始化聊天框
         */
        initChatPanel : function(){
            var self = this,
                chatContent = $('#contentRight');

            $('#translateSetting').removeClass('none');
            chatContent.find('.loading').remove();
            chatContent.html(util.template(chatPanelTpl, you));
            self.initTools(); // 初始化工具栏
            self.initChatInput(); // 初始化信息发送框
            var data ={};
            data.userId = me.id;
            data.userName = me.name;
            data.date = +new Date();
            data.sessionId = 0;
            self.bindTitleEvent(data);
            $('#fileUpload').on('change', function(){
                webIM.sendFile(this);
            });
            webIM.init();
            chatContent.find('#chatContent').on('mousewheel', function(e, d){
                var t = $(this);
                if (d > 0 && t.scrollTop() === 0) {
                    e.preventDefault();
                }else {
                    if (d < 0 && (t.scrollTop() == t.get(0).scrollHeight - t.innerHeight())) {
                        e.preventDefault();
                    }
                }
            });
            var $chatTitle = $('#chatTitle');
            //选择翻译者
            $chatTitle.find('.i-translate').on( 'click', function (e) {
                var $this = $(this),
                    tlPanel = $chatTitle.find('.tl-user-panel');

                if(tlPanel.hasClass('none')){
                    self.renderTranslator();
                    tlPanel.css({left: $this.position().left - (tlPanel.width()/2 - $this.width()/2)});
                    tlPanel.removeClass('none');
                }else{
                    tlPanel.addClass('none');
                }
                e.preventDefault();
                e.stopPropagation();
            });
            $(document).on('click', function(e){
                var target = $(e.target),
                    cls = target.prop('class') || '';
                if(cls.indexOf('tl-user-panel')==-1){
                    if(!target.closest('.tl-user-panel').length){
                        $chatTitle.find('.tl-user-panel').addClass('none');
                    }
                }
            });
            //绑定 搜索联系人event
            $chatTitle.find('.tl-user-panel .chatSearch .searchInput').on('keyup', function (e) {
                self.searchTranslator($(this));
            });

            // 上报打开聊天窗口消息
            socket.sendData(JSON.stringify({
                "code": 105,
                "messageId" : uuid.get(),
                "date": +new Date(),
                "senderId": me.id,
                "senderName": me.name,
                "receiverId": you.id,
                "receiverName": you.name
            }));

            // 记录 视频音频 邀请的状态；并在打开聊天窗口时加载相关提示 7-3-2014 by zhiqiang
            self.holdNotice();
        },
        /**
         * 初始化群组聊天
         */
        initGroupPanel : function(){
            var self = this,
                chatLeft = $('#contentLeft'),
                chatContent = $('#contentRight'),
                chatContentBox = $('#chatContent');

            $('#translateSetting').addClass('none');
            chatContent.find('.loading').remove();
            chatContent.html(util.template(groupChatPanelTpl, {
                type: group.type,
                id: group.id,
                name: group.name,
                member: '...'
            }));
            var data ={};
            data.userId = me.id;
            data.userName = me.name;
            data.date = +new Date();
            data.sessionId = 0;
            self.initTools(); // 初始化工具栏
            self.initChatInput(); // 初始化信息发送框
            self.bindTitleEvent(data);
            $('#fileUpload').on('change', function(){
                webIM.sendFile(this);
            });
            webIM.initGroupChat();
            chatContent.find('#chatContent').on('mousewheel', function(e, d){
                var t = $(this);
                if (d > 0 && t.scrollTop() === 0) {
                    e.preventDefault();
                }else {
                    if (d < 0 && (t.scrollTop() == t.get(0).scrollHeight - t.innerHeight())) {
                        e.preventDefault();
                    }
                }
            });
            var $chatTitle = $('#chatTitle');
            // 查看其它群成员
            $chatTitle.find('.i-look').off('click').on( 'click', function (e) {
                var $this = $(this),
                    groupPanel = $chatTitle.find('.group-user-panel'),
                    atCls = $chatTitle.find('.at-user-panel'),
                    editCls = $chatTitle.find('.edit-group-panel');

                if(groupPanel.hasClass('none')){
                    self.renderGroupMember();
                    groupPanel.css({left: $this.position().left - 94}).removeClass('none');
                }else{
                    groupPanel.addClass('none');
                }
                if(!atCls.hasClass('none')){
                    $chatTitle.find('.at-user-panel').addClass('none');
                }
                if(!editCls.hasClass('none')){
                    $chatTitle.find('.edit-group-panel').addClass('none');
                }
                e.preventDefault();
                e.stopPropagation();
            });
            if(group.type == 'translate'){
                $chatTitle.find('.i-group-invitation').addClass('none');
            }else if(group.type == 'normal'){
                self.selfRole(function(role){
                    if( role == 'OWNER'){
                        $chatTitle.find('.i-group-manage').removeClass('none');
                        $chatTitle.find('.i-group-out').addClass('none');
                    }
                });
                $chatTitle.children('.i-group-editName').addClass('none');
            }else if(group.type == 'multicommunicate'){
                //$chatTitle.find('.i-group-remove').addClass('none');
            }
            // 邀请圈好友
            $chatTitle.find('.i-group-invitation').off('click').on( 'click', function (e) {
                var $this = $(this),
                    atUserPanel = $chatTitle.find('.at-user-panel'),
                    listPanel = atUserPanel.find('.user-list'),
                    listStr = [],
                    groupCls = $chatTitle.find('.group-user-panel'),
                    editCls = $chatTitle.find('.edit-group-panel'),
                    isExists = false;

                if(atUserPanel.hasClass('none')){
                    atUserPanel.removeClass('none');
                    self.getAtData(function(list){
                        if(list && list.length){
                            $.each(list, function(i, n){
                                isExists = false;
                                if(group && group.member){
                                    $.each(group.member, function(x, y){
                                        if(y.userId == n.userId){
                                            isExists = true;
                                            return false;
                                        }
                                    })
                                }
                                n.avatar = n.headUrl;
                                if(isExists){
                                    n.cls = 'chat-icon selected disabled';
                                }
                                listStr.push(util.template(atFriendsTpl, n));
                            });
                            if(listStr.length){
                                listPanel.html(listStr.join(''));
                                util.imgLoadError(listPanel.find('.user-head img'));
                            }else{
                                listPanel.html(util.template(userNone, {text: '暂无记录'}));
                            }
                        }else{
                            listPanel.html(util.template(userNone, {text: '暂无记录'}));
                        }
                        self.bindInviteAtJoinEvent();
                    });
                }else{
                    atUserPanel.addClass('none');
                }
                if(!groupCls.hasClass('none')){
                    $chatTitle.find('.group-user-panel').addClass('none');
                }
                if(!editCls.hasClass('none')){
                    $chatTitle.find('.edit-group-panel').addClass('none');
                }
                e.preventDefault();
                e.stopPropagation();
            });
            // 管理群
            $chatTitle.find('.i-group-manage').on( 'click', function (e) {
                var $this = $(this),
                    managePanel = $chatTitle.find('.group-manage-panel'),
                    groupCls = $chatTitle.find('.group-user-panel');

                if(managePanel.hasClass('none')){
                    managePanel.css({left: $this.position().left - 42}).removeClass('none');
                }else{
                    managePanel.addClass('none');
                }
                if(!groupCls.hasClass('none')){
                    $chatTitle.find('.group-user-panel').addClass('none');
                }

                e.preventDefault();
                e.stopPropagation();
            });
            // 修改群名称
            $chatTitle.find('.i-group-editName').on( 'click', function (e) {
                var $this = $(this),
                    editPanel = $chatTitle.find('.edit-group-panel'),
                    atCls = $chatTitle.find('.at-user-panel'),
                    groupCls = $chatTitle.find('.group-user-panel'),
                    editBtn = editPanel.find('.btn-editIM-groupName'),
                    editInput = editPanel.find('#groupName');

                if(group && group.type == 'normal'){
                    window.location.href = '/user/group/editInfo?gid=' + group.id;
                    return;
                }
                if(!groupCls.hasClass('none')){
                    $chatTitle.find('.group-user-panel').addClass('none');
                }
                if(!atCls.hasClass('none')){
                    $chatTitle.find('.at-user-panel').addClass('none');
                }
                if(editPanel.hasClass('none')){
                    editPanel.removeClass('none');
                    editPanel.find('.tips').css('left', $this.position().left + $this.width()/2-4);
                    editInput.val(group.name);
                    editBtn.off('click').on('click', function(){
                        var groupName = editInput.val(),
                            editLoading = editPanel.find('.loading');
                        editLoading.removeClass('none');
                        self._editGroupName(groupName, function(){
                            if(groupName.length){
                                if(group){
                                    group.name = groupName;
                                }
                                if(youList){
                                    $.each(youList, function(i, n){
                                        if(n.groupId == group.id){
                                            n.groupName = groupName;
                                            youList[i] = n;
                                            return false;
                                        }
                                    })
                                }
                                $chatTitle.find('.user-name').text(groupName).prop('title', groupName);
                                chatLeft.find('.user-highlight .user-name').text(groupName).prop('title', groupName);
                            }
                            editLoading.addClass('none');
                            editPanel.addClass('none');
                        }, function(){
                            editLoading.addClass('none');
                        });
                    });
                    editInput.off('keydown').on('keydown', function(e){
                        if(e.keyCode == 13){
                            var groupName = $(this).val(),
                                editLoading = editPanel.find('.loading');

                            editLoading.removeClass('none');
                            self._editGroupName(groupName, function(){
                                if(groupName.length){
                                    $chatTitle.find('.user-name').text(groupName).prop('title', groupName);
                                    chatLeft.find('.user-highlight .user-name').text(groupName).prop('title', groupName);
                                }
                                editLoading.addClass('none');
                                editPanel.addClass('none');
                            });
                        }
                    })
                }else{
                    editPanel.addClass('none');
                }

                e.preventDefault();
                e.stopPropagation();
            });
            // 退出群
            $chatTitle.find('.i-group-out').off('click').on( 'click', function (e) {
                var $this = $(this),
                    groupPanel = $chatTitle.find('.group-user-panel');

                $.confirm(
                    '<strong class="f14">你确定要退出该群组吗？</strong>' +
                        '<p class="f12">退出后将无法再查看该群组消息记录，也不再收取该群组消息记录。</p>',
                    function(){
                        var tips = $.tips('loading'),
                            groupData = group || {};
                        util.setAjax(
                            util.strFormat(inter.getApiUrl().exitGroup, [groupData.id]),
                            {},
                            function (json) {
                                tips.close();
                                if(json.status){
                                    msgLS.removeGroup(groupData.id);
                                    self.removeLinkman({
                                        groupId: groupData.id,
                                        groupName: groupData.name,
                                        groupType: groupData.type,
                                        groupAvatar: groupData.avatar
                                    });
                                }else{
                                    $.alert(json.error);
                                }
                            },
                            function () {
                                tips.close();
                                $.alert('服务器繁忙，请稍后再试。');
                            },
                            'GET'
                        );
                    },
                    null,
                    {
                        title:'<strong>退出群组</strong>',
                        icon: false
                    }
                );
                e.preventDefault();
            });
            // 删除群
            $chatTitle.find('.btn-group-remove').off('click').on( 'click', function (e) {
                var $this = $(this);

                $.confirm(
                    '<strong class="f14">你确定要删除该群组吗？</strong>' +
                        '<p class="f12">将不再收取群组消息记录，群组详情页将被删除，且不可恢复！</p>',
                    function(){
                        var tips = $.tips('loading'),
                            groupData = group || {};
                        util.setAjax(
                            util.strFormat(inter.getApiUrl().deleteGroup, [groupData.id]),
                            {},
                            function (json) {
                                tips.close();
                                if(json.status){
                                    msgLS.removeGroup(groupData.id);
                                    self.removeLinkman({
                                        groupId: groupData.id,
                                        groupName: groupData.name,
                                        groupType: groupData.type,
                                        groupAvatar: groupData.avatar
                                    });
                                }else{
                                    $.alert(json.error);
                                }
                            },
                            function () {
                                tips.close();
                                $.alert('服务器繁忙，请稍后再试。');
                            },
                            'GET'
                        );
                    },
                    null,
                    {
                        title:'<strong>删除群组</strong>',
                        icon: false
                    }
                );
                e.preventDefault();
            });
            $(document).on('click', function(e){
                var target = $(e.target),
                    cls = target.prop('class') || '';
                if(!target.hasClass('group-user-panel')){//隐藏群成员层
                    if(!target.closest('.group-user-panel').length){
                        $chatTitle.find('.group-user-panel').addClass('none');
                    }
                }
                if(!target.hasClass('at-user-panel')){//隐藏邀请圈好友入群层
                    if(!target.closest('.at-user-panel').length){
                        $chatTitle.find('.at-user-panel').addClass('none');
                    }
                }
                if(!target.hasClass('edit-group-panel')){//编辑群名称层
                    if(!target.closest('.edit-group-panel').length){
                        $chatTitle.find('.edit-group-panel').addClass('none');
                    }
                }
                if(!target.hasClass('group-manage-panel')){//管理群层
                    if(!target.closest('.group-manage-panel').length){
                        $chatTitle.find('.group-manage-panel').addClass('none');
                    }
                }
            });
        },
        /**
         * 编辑多人会话群组名称
         */
        _editGroupName : function(groupName, call, errCall){
            var self = this,
                isOpenChat = $.parseJSON(cookie.get('isOpenChat'+self.curUid)) || {};

            if(groupName.length){
                util.setAjax(inter.getApiUrl().updateMultiCommunicate, {groupId: group.id, groupName: groupName}, function(json){
                    if(json.error){
                        $.alert(json.error);
                        errCall && errCall();
                    }else{
                        isOpenChat.groupName = groupName;
                        cookie.set('isOpenChat'+self.curUid, JSON.stringify(isOpenChat));
                        call && call();
                    }
                }, function(){
                    $.alert('服务器繁忙，请稍后再试。');
                });
            }else{
                call && call();
            }
        },
        /**
         * 判断当前登录用户的角色
         */
        selfRole : function(call){
            var self = this,
                role = 'MEMBER';

            if(group && group.member){
                $.each(group.member, function(i, n){
                    if(n.userId == self.curUid){
                        role = n.role;
                        return false;
                    }
                });
                call && call(role);
            }else{
                self.getGroupMemberData(function(data){
                    $.each(data, function(i, n){
                        if(n.userId == self.curUid){
                            role = n.role;
                            return false;
                        }
                    });
                    call && call(role);
                })
            }
        },
        /**
         * 绑定聊天窗口顶部按钮事件
         */
        bindTitleEvent : function(data){
            var self = this,
                $chatTitleDown = $('#chatTitleDown'),
                $inviteVideo = $chatTitleDown.find('.invite-video'),
                $inviteVoice = $chatTitleDown.find('.invite-voice'),
                $notice = $chatTitleDown.find('.notice');
            //绑定接受/拒绝 视频
            $inviteVideo.find('.chat-btn-ok')
                .off('click')
                .on('click',function(){
                    var _210 = cookie.get('210');

                    if(_210 == 'ok'){
                        webIM.notice();
                        webIM.changeVideo(false);
                        socket.chatTipAnimate('', 0);
                        util.clearCountdown('chatTipAnimate');
                    }else{
                        var socketStatus = socket.socket.readyState == 1;
                        if(socketStatus){
                            data.inviterId = cookie.get('senderId');
                            data.inviterName = cookie.get('senderName');

                            data.code = 211;
                            data.messageId = uuid.get();
                            socket.sendData(JSON.stringify(data));
                            webIM.openVideo(1);

                            webIM.notice('', '', '',null,true);
                            webIM.changeVideo(false,true);
                            socket.chatTipAnimate('', 0);
                            util.clearCountdown('chatTipAnimate');

                            cookie.set('210','ok');
                        }else{
                            webIM.notice('notice','','通讯终断，请稍后重试!',4,false);
                        }

                    }


                });
            $inviteVideo.find('.chat-btn-no')
                .off('click')
                .on('click',function(){
                    var _210 = cookie.get('210');

                    if(_210 == 'no'){
                        //其他页面已经拒绝
                        webIM.notice();
                        webIM.changeVideo(true);
                        socket.chatTipAnimate('', 0);
                        util.clearCountdown('chatTipAnimate');

                    }else{
                        data.code = 212;
                        data.messageId = uuid.get();

                        data.inviterId = cookie.get('senderId');
                        data.inviterName = cookie.get('senderName');

                        socket.sendData(JSON.stringify(data));
                        webIM.notice('', '', '', 0, true);
                        webIM.changeVideo(true, true);
                        socket.chatTipAnimate('', 0);
                        util.clearCountdown('chatTipAnimate');

                        cookie.set('210','no');
                    }

                });
            //绑定接受/拒绝 语音
            $inviteVoice.find('.chat-btn-ok')
                .off('click')
                .on('click',function(){
                    var _220 = cookie.get('220');
                    if(_220 == 'ok'){
                        webIM.notice('');
                        webIM.changeVideo(false);
                        socket.chatTipAnimate('', 0);
                        util.clearCountdown('chatTipAnimate');

                    }else{
                        var socketStatus = socket.socket.readyState == 1;
                        if(socketStatus){
                            data.code = 221;
                            data.messageId = uuid.get();
                            data.inviterId = cookie.get('senderId');
                            data.inviterName = cookie.get('senderName');

                            socket.sendData(JSON.stringify(data));
                            webIM.openVideo(3);
                            webIM.notice('',data.inviterName,'',null,true);
                            webIM.changeVideo(false,true);
                            socket.chatTipAnimate('', 0);
                            util.clearCountdown('chatTipAnimate');

                            cookie.set('220','ok');

                        }else{
                            webIM.notice('notice','','通讯终断，请稍后重试!',4,false);
                        }
                    }

                });
            $inviteVoice.find('.chat-btn-no')
                .off('click')
                .on('click',function(){
                    var _220 = cookie.get('220');
                    if(_220 == 'no'){

                        webIM.notice();
                        webIM.changeVideo(true);
                        socket.chatTipAnimate('', 0);
                        util.clearCountdown('chatTipAnimate');

                    }else{
                        data.code = 222;
                        data.messageId = uuid.get();
                        data.inviterId = cookie.get('senderId');
                        data.inviterName = cookie.get('senderName');
                        socket.sendData(JSON.stringify(data));
                        webIM.notice();
                        webIM.changeVideo(true);
                        socket.chatTipAnimate('', 0);
                        util.clearCountdown('chatTipAnimate');

                        cookie.set('220','no');
                    }

                });


            /* 主动挂断*/
            $notice.find('.chat-btn-no')
                .off('click')
                .on('click',function(){

                    socket.sendData(JSON.stringify({
                        'code':260,
                        'messageId' : uuid.get(),
                        'senderId':me.id,
                        'senderName':me.name,
                        'receiverId': cookie.get('inviteeId'),
                        'receiverName': cookie.get('inviteeName'),
                        'sessionId':0,
                        'date': +new Date()
                    }));

                    webIM.notice();
                    webIM.changeVideo(true);
                });
        },
        /**
         * 初始化工具栏
         */
        initTools : function(){
            var self = this,
                chatUtil = $('#chatUtil'),
                tools = $(utilToolsTpl);

            chatUtil.append(tools);
            // 插入表情
            tools.find('.util-emotion').on('click', function(e){
                var $this = $(this),
                    emotionPanel = $('.emotion-panel'),
                    chatEmotionPanel = $('.chat-input'),
                    emotionItem = null,
                    emotionItemTpl = '<li><img src="#{src}" data-reg="#{reg}" title="#{title}"></li>',
                    panelTpl = $(emotionPanelTpl);
                if(!emotionPanel.length){
                    $this.prepend(panelTpl);
                    emotionPanel = panelTpl;
                    emotionM.renderEmotion(emotionItemTpl, function(emotionList){
                        emotionItem = $(emotionList.join(''));
                        emotionPanel.find('.emotion-list').html(emotionItem);
                        emotionItem.on('click', function(e){
                            var $item = $(this),
                                $itemImg = $item.find('img');
                            cursor.insertText($item.html());
                            self._syncChatInput();
                            emotionPanel.hide();
                            e.preventDefault();
                            e.stopPropagation();
                        });
                    });
                }else{
                    emotionPanel.show();
                }
                //chatEmotionPanel.focus();
                e.preventDefault();
                e.stopPropagation();
                $(document).one('click', function(){
                    emotionPanel.hide();
                })
            });
            // 发送文件
            tools.find('.util-file').on('click', function(){

            });
        },
        /**
         * 初始化聊天信息发送框
         */
        initChatInput : function(){
            var self = this,
                chatUtil = $('#chatUtil'),
                chatInput = $(utilChatInputTpl);

            chatUtil.append(chatInput);
            cursor = cursorControl.init(chatInput.find('.chat-input'));
            chatInput.find('.chat-input').on($.browser.ie || $.browser.opera ? 'keydown' : 'paste', function (e) {
                var $this = $(this);
                self._syncChatInput();
                //ie下beforepaste在点击右键时也会触发，所以用监控键盘才处理
                if (($.browser.ie || $.browser.opera) && ((!e.ctrlKey && !e.metaKey) || e.keyCode != '86')) {
                    return;
                }
                clipboard.getData(function (div) {
                    var copyText = $(div).val();
                    copyText = copyText.replace(/\u200B/ig,'');
                    copyText = copyText.replace(/\ufeff/ig,'');
                    cursor.insertText(copyText);
                });
            }).on('keypress', function(e){
                    self._syncChatInput();
                    /*if(e.keyCode == 13){
                        cursor.insertText('<br>');
                        e.preventDefault();
                    }*/
                }).on('keyup', function(e){
                    self._syncChatInput();
                });//.focus();
            self.sendMsg();

        },
        /**
         * 同步消息编辑框和输入框
         */
        _syncChatInput : function(){
            var self = this,
                chatUtil = $('#chatUtil'),
                txt = chatUtil.find('.chat-input').html(),
                panel = $('<p></p>').append(txt),
                img = panel.find('img'),
                regList = [],
                spaceReg = /\u200b|\ufeff/ig;

            $.each(img, function(i, n){
                regList.push('[!' + $(n).attr('data-reg') + ']');
            });
            $.each(regList, function(i, n){
                txt = txt.replace(/<img\s([^>]{0,})>/i, n);
            });
            if(txt){
                txt = txt.replace(/<div><\/div>/ig,'');
                txt = txt.replace(/<p><\/p>/ig,'');
                txt = txt.replace(/<[div]+><br><\/div>/ig,'<br>');
                txt = txt.replace(/<[div]+>/ig, '').replace(/<\/div>/ig,'<br>');
                txt = txt.replace(/<[p]+><br><\/p>/ig,'<br>');
                txt = txt.replace(/<[p]+>/ig, '').replace(/<\/p>/ig,'<br>');
                txt = txt.replace(/<br type="_moz">/ig, '');
                txt = txt.replace(/<br>$/ig, '');
                txt = txt.replace(/<[br]+>/ig, '\n');
                txt = txt.replace(/<[^>]+>/ig, '');
                txt = txt.replace(spaceReg, '');
            }
            chatUtil.find('#chatInput').val(txt);
        },
        /**
         * 发送消息
         */
        sendMsg : function () {
            var self = this,
                $sendMsg = $('#chatBtn'),
                $inputMsg = $('#chatInput'),
                chatEditPanel = $('#chatEditPanel');
            chatEditPanel.on('keydown', function (event) {
                if (event.ctrlKey && event.keyCode == 13) {
                    self._syncChatInput();
                    webIM.send($inputMsg.val());
                    return false;
                }
            });
            $sendMsg.on('click', function(){
                self._syncChatInput();
                webIM.send($inputMsg.val());
            }).poshytip({
                    className: 'tip-yellow',
                    content : 'Ctrl+Enter快捷发送',
                    alignTo: 'target',
                    alignX: 'center',
                    showAniDuration: 0,
                    offsetY: 7
                });
        },
        /**
         * 获取群成员数据
         */
        getGroupMemberData : function(call, data){
            var self = this,
                tlPanel = $('#chatTitle .group-user-panel'),
                listPanel = tlPanel.find('.user-list');
            if(!group && data){
                window.group = {
                    type : data.groupType || 'translate',
                    name : data.groupName,
                    id : data.groupId
                }
            }
            webIM._getGroupMember(function(json){
                if(group){
                    group.merber = json;
                }
                util.trace('获取群数据：');
                util.trace(group);
                call && call(json);
            },function(data){
                self.loadFailed(listPanel, function(){
                    self.getGroupMemberData(call);
                })
            });
        },
        /**
         * 渲染群成员列表
         */
        renderGroupMember : function(){
            var self = this,
                tlPanel = $('#chatTitle .group-user-panel'),
                listPanel = tlPanel.find('.user-list'),
                loadTpl = '<li class="loading"><em></em></li>',
                listStr = [];

            listPanel.html(loadTpl);
            self.getGroupMemberData(function(list){
                if(list && list.length){
                    $.each(list, function(i, n){
                        //if(group.type != 'translate' || n.userId != me.id){
                        if(n.userId != me.id){
                            n.avatar = util.getAvatar(n.userId);
                            switch (n.role){
                                case 'TRANSLATE':
                                    n.title = '翻译者';
                                    n.iconCls = 'i-group-translator';
                                    break;
                                case 'OWNER':
                                    n.title = '群主';
                                    n.iconCls = 'i-group-manager';
                                    break;
                                default :
                                    break;
                            }
                            listStr.push(util.template(groupMemberTpl, n));
                        }
                    });
                    if(listStr.length){
                        listPanel.html(listStr.join(''));
                        util.imgLoadError(listPanel.find('.user-head img'));
                    }else{
                        listPanel.html(util.template(translatorNone, {text: '暂无记录'}));
                    }
                }else{
                    listPanel.html(util.template(translatorNone, {text: '暂无记录'}));
                }
            });
        },
        /**
         * 获取翻译者数据
         */
        getTranslatorData : function(call){
            var self = this,
                listPanel = $('#chatTitle .tl-user-panel .user-list'),
                loadTpl = '<li class="loading"><em></em></li>';

            listPanel.html(loadTpl);
            util.setAjax(inter.getApiUrl().getTranslatorUrl, {'msgType': 'no_system', 'page': 0, 'pageSize': 2000}, function(json){
                if(json.error){
                    self.loadFailed(listPanel, function(){
                        self.getTranslatorData(call);
                    })
                }else{
                    call && call(json);
                }
            },function(){
                self.loadFailed(listPanel, function(){
                    self.getTranslatorData(call);
                })
            }, 'GET');
        },
        /**
         * 渲染翻译者列表
         */
        renderTranslator : function(){
            var self = this,
                tlPanel = $('#chatTitle .tl-user-panel'),
                listPanel = tlPanel.find('.user-list'),
                listStr = [],
                noneDataTpl = util.template(translatorNone, {text: '暂无记录'});

            self.getTranslatorData(function(list){
                if(list && list.length){
                    $.each(list, function(i, n){
                        n.commentWidth = n.averageScore/5*73;
                        n.averageScore = util.toFixed(n.averageScore, 1) || '0';
                        n.commentCount = n.commentCount || '0';
                        listStr.push(util.template(translatorTpl, n));
                    });
                    listPanel.html(listStr.length ? listStr.join('') : noneDataTpl);
                    util.imgLoadError(listPanel.find('.user-head img'));
                    self.bindTranslatorEvent();
                }else{
                    listPanel.html(noneDataTpl);
                }
            });
        },
        /**
         * 绑定切换翻译者事件
         */
        bindTranslatorEvent : function(){
            var self = this,
                tpPanel = $('#chatTitle .tl-user-panel'),
                listPanel = tpPanel.find('.user-list'),
                /*okSelBtn = tpPanel.find('.chat-btn-ok'),
                noSelBtn = tpPanel.find('.chat-btn-no'),*/
                selTl = $('#selTl');

            listPanel.find('li:not(.user-none)').hover(function(){
                $(this).find('.tl-user-sel').removeClass('none');
            },function(){
                $(this).find('.tl-user-sel').addClass('none');
            }).off('click').on('click', function(){
                var $this = $(this),
                    pending = tpPanel.find('.pending'),
                    tlUid = $this.attr('data-uid');

                if(tlUid){
                    pending.removeClass('none');
                    if(tlUid == me.id || (you && tlUid == you.id)){
                        self._pendingTips(pending, '不能邀请自己或者对方为翻译者。');
                    }else{
                        self.addTranslateGroup(tlUid, function(json){
                            pending.addClass('none');
                            tpPanel.addClass('none');
                            tpPanel.find('.searchInput').val('');
                            listPanel.find('.sel-user em').removeAttr('class');
                            self.setCurChat(json);
                        }, function(msg){
                            self._pendingTips(pending, '创建群组失败，错误信息：<br>' + msg);
                        });
                    }
                }else{
                    self._pendingTips(pending, '请选择翻译者。');
                }
            });

            // 选择翻译者
            /*listPanel.find('li:not(.user-none)').off('click').on('click', function(){
                var $this = $(this),
                    uid = $this.attr('data-uid'),
                    checkBox = $this.find('.sel-user em'),
                    checkBoxCls = checkBox.prop('class') || '';

                if(checkBoxCls.indexOf('selected') > -1){
                    checkBox.removeAttr('class');
                    uid = 0;
                }else{
                    listPanel.find('.sel-user em').removeAttr('class');
                    checkBox.attr('class', 'chat-icon selected');
                }
                selTl.val(uid);
            });*/
            // 关闭选择
            /*noSelBtn.off('click').on('click', function(){
                tpPanel.addClass('none');
                tpPanel.find('.searchInput').val('');
                listPanel.find('.sel-user em').removeAttr('class');
                selTl.val(0);
            });*/
            // 确认选择
            /*okSelBtn.off('click').on('click', function(){
                var tlUid = parseInt(selTl.val()) || 0,
                    pending = tpPanel.find('.pending');

                if(tlUid){
                    pending.removeClass('none');
                    self.addTranslateGroup(tlUid, function(json){
                        pending.addClass('none');
                        tpPanel.addClass('none');
                        tpPanel.find('.searchInput').val('');
                        listPanel.find('.sel-user em').removeAttr('class');
                        selTl.val(0);
                        self.setCurChat(json);
                    }, function(msg){
                        pending.addClass('no-load-gif').html('<div class="err-tips red">创建群组失败，错误信息：<br>' + msg + '</div>');
                        setTimeout(function(){
                            pending.addClass('none').removeClass('no-load-gif');
                        }, 3000)

                    });
                }else{
                    pending.removeClass('none').addClass('no-load-gif').html('<div class="err-tips red">请选择翻译者。</div>');
                    setTimeout(function(){
                        pending.addClass('none').removeClass('no-load-gif');
                    }, 3000)
                }
            })*/
        },
        /**
         * 加载错误提醒
         */
        _pendingTips : function(pending, txt){
            pending.addClass('no-load-gif').html('<div class="err-tips red">' + txt + '</div>');
            setTimeout(function(){
                pending.addClass('none').removeClass('no-load-gif');
            }, 3000)
        },
        /**
         * 创建翻译者模式群组
         */
        addTranslateGroup : function(tlUid, sucCall, errCall){
            util.setAjax(util.strFormat(inter.getApiUrl().addGroupByTranslate, [you.id, tlUid]), {}, function(json){
                if(json.error){
                    errCall && errCall(json.error);
                }else{
                    sucCall && sucCall(json);
                }
            }, function(){
                errCall && errCall('服务器繁忙。');
            }, 'GET')
        },
        /**
         * 搜索翻译者
         */
        searchTranslator : function ($this) {
            var searchNum = 0,
                keyword = $this.val(),
                listPanel = $this.closest('.chatSearch').siblings('.user-list'),
                listItem = listPanel.find('li'),
                listNone = listPanel.find('.user-none'),
                listName = listPanel.find('.user-name');

            listNone.hide();
            if ( !keyword || ('' == keyword) ) {
                listItem.removeClass('none');
                return;
            }

            listName.each(function () {
                var that = $(this),
                    li = that.closest("li"),
                    nameItem = that.text();

                //if (nameItem.search(new RegExp(keyword, 'i')) === -1) {
                if(nameItem.indexOf(keyword) > -1 || pinyin.getJP(nameItem).indexOf(keyword) > -1 || pinyin.getQP(nameItem).indexOf(keyword) > -1 || pinyin.getHP(nameItem).indexOf(keyword) > -1){
                    li.removeClass('none');
                    searchNum++
                } else {
                    li.addClass('none');
                }
            });
            //未搜到联系人
            if(searchNum === 0){
                if( !listNone.length){
                    listPanel.append(util.template(translatorNone, {text: '无搜索结果'}));
                }else{
                    listNone.show();
                }
            }
        },
        /**
         * 收到消息写入聊天窗口
         */
        setChatMsg : function(data){
            var self = this,
                chatBox = $('#chatBox'),
                chatTitle = $('#chatTitle'),
                chatWrap = chatBox.find('#chatWrap'),
                listPanel = $('#contentLeft .user-list'),
                curOpenItem = listPanel.find('.user-highlight'),
                curGroupId = curOpenItem.attr('data-gid'),
                curUserId = curOpenItem.attr('data-uid'),
                curItem = null,
                isOpenChat = $.parseJSON(cookie.get('isOpenChat'+self.curUid)) || {};

            if( chatBox.length && chatWrap.length ){
                var isClear = !!(chatWrap.css('display') != 'none');
                if(data.groupId && data.groupId == curGroupId){
                    switch (data.code){
                        case 307:
                            webIM.getMsg(data, isClear);
                            break;
                        case 305:
                            webIM.getMsg(data, isClear);
                            break;
                        case 311:
                            webIM.getFile(data, isClear);
                            break;
                        case 313:
                            webIM.getVoice(data, isClear);
                            break;
                        case 315:
                            webIM.getFile(data, isClear);
                            break;
                        case 317:
                            curItem = listPanel.find('li[data-gid="'+ data.groupId +'"]');
                            if(curItem.length){
                                if(data.avatarUrl){
                                    curItem.find('.user-head img').prop('src', data.avatarUrl+'?_r='+(+new Date()));
                                }
                                if(data.groupNewName)curItem.find('.user-name').text(data.groupNewName).prop('title', data.groupNewName);
                                if(group && data.groupId == group.id){
                                    if(data.groupNewName)group.name = data.groupNewName;
                                    if(data.avatarUrl)group.avatar = data.avatarUrl;
                                    chatTitle.find('.user-name').text(data.groupNewName).prop('title', data.groupNewName);
                                }
                            }
                            webIM.getMsg(data, isClear);
                            break;
                        default :
                            break;
                    }
                }else if(!data.groupId && !curGroupId && (data.senderId == curUserId || (data.senderId == me.id && data.receiverId == curUserId))){
                    switch (data.code){
                        case 3:
                            webIM.getMsg(data, isClear);
                            break;
                        case 11:
                            webIM.getFile(data, isClear);
                            break;
                        case 13:
                            webIM.getVoice(data, isClear);
                            break;
                        case 15:
                            webIM.getFile(data, isClear);
                            break;
                        case 103:
                            webIM.getMsg(data, isClear);
                            break;
                        default :
                            break;
                    }
                }else{
                    var itemPanel = listPanel.find('li[data-uid="'+ (data.senderId == me.id ? data.receiverId : data.senderId) +'"]'),
                        cl = null,
                        itemTipData = [],
                        itemData = data.senderId == me.id ? {
                            userId: data.receiverId,
                            userName: data.receiverName,
                            avatar: util.getAvatar(data.receiverId)
                        } : {
                            userId: data.senderId,
                            userName: data.senderName,
                            avatar: util.getAvatar(data.senderId)
                        };

                    if(data.groupId){
                        itemData = {
                            groupId: data.groupId,
                            groupName: data.groupName || '翻译群',
                            groupType: data.groupType || 'translate'
                        };
                        itemPanel = listPanel.find('li[data-gid="'+ data.groupId +'"]');
                    }
                    if(!itemPanel.length){
                        if(!listPanel.find('li:not(.user-none)').length){
                            self.setCurChat(itemData);
                        }else{
                            self.setLinkman(itemData, function(){
                                itemTipData.push(itemData);
                                self.setTipChat(itemTipData);
                            });
                        }
                    }else{
                        cl = itemPanel.clone();
                        itemPanel.remove();
                        listPanel.prepend(cl);
                        self.bindLinkmanEvent();
                        itemTipData.push(itemData);
                        self.setTipChat(itemTipData);
                    }
                }
            }
            if(data.code == 307 || data.code == 317){
                self._updateGroupHelper(data);
            }
        },
        /**
         * 更新群信息
         */
        _updateGroupHelper : function(data){
            var self = this,
                listPanel = $('#contentLeft .user-list'),
                chatTitle = $('#chatTitle'),
                curItem = null,
                isOpenChat = $.parseJSON(cookie.get('isOpenChat'+self.curUid)) || {};

            switch (data.state){
                case 4:
                case 10:  //创建群
                    break;
                case 5:
                case 11:  //邀请用户加入群
                    if(data.senderId == me.id){
                        util.setAjax(util.strFormat(inter.getApiUrl().queryGroupById, [data.groupId]), {}, function(json){
                            if(!json.error){
                                if(json.type == 'normal'){
                                    data.groupAvatar = json.headUrl;
                                }
                                data.groupType = json.type;
                                self.setCurChat(data);
                            }
                        }, null, 'GET');
                    }else{
                        self.getGroupMemberData(function(list){
                            if(group && data.groupId == group.id){
                                chatTitle.find('.i-look').text(list.length - 1);
                            }
                        }, data);
                    }
                    break;
                case 12:  //用户加入群
                    if(data.senderId == me.id){
                        util.setAjax(util.strFormat(inter.getApiUrl().queryGroupById, [data.groupId]), {}, function(json){
                            if(!json.error){
                                if(json.type == 'normal'){
                                    data.groupAvatar = json.headUrl;
                                }
                                data.groupType = json.type;
                                self.setCurChat(data);
                            }
                        }, null, 'GET');
                    }else{
                        self.getGroupMemberData(function(list){
                            if(group && data.groupId == group.id){
                                chatTitle.find('.i-look').text(list.length - 1);
                            }
                        }, data);
                    }
                    break;
                case 7:
                case 13:  //用户退出群
                    if(data.senderId != me.id){
                        self.getGroupMemberData(function(list){
                            if(group && data.groupId == group.id){
                                chatTitle.find('.i-look').text(list.length - 1);
                            }
                        });
                    }else{
                        self.removeLinkman(data);
                        msgLS.removeGroup(data.groupId);
                    }
                    break;
                case 8:
                case 14:  //移除用户
                    if(data.senderId == me.id || (data.groupTips && data.groupTips.memberId == me.id)){
                        self.removeLinkman(data);
                        msgLS.removeGroup(data.groupId);
                    }else{
                        self.getGroupMemberData(function(list){
                            if(group && data.groupId == group.id){
                                chatTitle.find('.i-look').text(list.length - 1);
                            }
                        }, data);
                    }
                    break;
                case 9:
                case 15:  //解散群
                    self.removeLinkman(data);
                    msgLS.removeGroup(data.groupId);
                    break;
                default: //修改群名称、头像 16 17 18
                    curItem = listPanel.find('li[data-gid="' + data.groupId + '"]');
                    if (curItem.length) {
                        if (data.avatarUrl) {
                            curItem.find('.user-head img').prop('src', data.avatarUrl);
                        }
                        if (data.groupNewName) {
                            curItem.find('.user-name').text(data.groupNewName).prop('title', data.groupNewName);
                        }
                        if (group && data.groupId == group.id) {
                            if (data.groupNewName) {
                                group.name = data.groupNewName;
                                chatTitle.find('.user-name').text(data.groupNewName).prop('title', data.groupNewName);
                                isOpenChat.groupName = data.groupNewName;
                            }
                            if (data.avatarUrl) {
                                group.avatar = data.avatarUrl;
                                isOpenChat.groupAvatar = data.avatarUrl;
                            }
                            if (data.groupNewName || data.avatarUrl) {
                                cookie.set('isOpenChat' + self.curUid, JSON.stringify(isOpenChat));
                            }
                        }
                    }
                    if (youList) {
                        $.each(function (i, n) {
                            if (n.groupId == data.groupId) {
                                if (data.groupNewName) {
                                    n.groupName = data.groupNewName;
                                }
                                if (data.avatarUrl) {
                                    n.groupAvatar = data.avatarUrl;
                                }
                                youList[i] = n;
                                return false;
                            }
                        })
                    }
                    break;
            }
        },
        /**
         * 保持视频邀请 的 提示
         */
        holdNotice: function () {
            var senderName = cookie.get('senderName'),
                time = new Date().getTime(),
                second = 0 ,
                socketStatus = socket.socket.readyState == 1,
                st210 = cookie.get('210'),  //收到210时的提示语
                st220 = cookie.get('220');  //收到220时的提示语

            if (st210) {
                second = parseInt(60 - (time - st210) / 1000, 10);
                if (second > 0 && socketStatus) {
                    window.webIM.notice('invite-video', senderName, '', second, false);
                }
            }
            if (st220) {
                second = parseInt(60 - (time - st220) / 1000, 10);
                if (second > 0 && socketStatus) {
                    window.webIM.notice('invite-voice', senderName, '', second, false);
                }
            }
        },
        /**
         * 打开聊天窗口
         */
        openChat : function (){
            var self = this,
                box = $('#chatBox'),
                wrap = box.find('#chatWrap'),
                warn = box.find('.chat-warn'),
                loadJs = warn.find('.chat-load-js');

            warn.hide();
            loadJs.remove();
            if (!wrap.length) {
                wrap = $(util.template(chatContentTpl, {
                    msgTip: '您有超过0条未读消息',
                    msgNum: '0'
                }));
                box.append(wrap);
                if(window.chatData){
                    if(window.chatData.chatMsgNum){
                        var msgTit = window.chatData.chatMsgNum > 99 ? '您有超过99条新消息' : '您有条'+ window.chatData.chatMsgNum +'新消息';
                        wrap.find('.home-num').removeClass('none').prop('title', msgTit).find('.home-num-txt').text(window.chatData.chatMsgNum);
                    }else{
                        if(wrap.find('.home-num').prop('class').indexOf('none') == -1){
                            wrap.find('.home-num').addClass('none');
                        }
                    }
                }
            }
            var wh = wrap.height(),
                tabType = $('.userTypeTab a');
            wrap.show();
            box.css({bottom: -wh, width: wrap.width()}).stop().animate({
                bottom: 0
            }, 'slow', 'swing', function(){
                /*cursorControl.setPos();*/
            });
            if(!self.loadedEvent){
                //绑定 最小化event
                $('#chatMenuMini').on('click', function () {
                    self.closeChat();
                });
                //绑定 联系人/圈tab event
                tabType.on('click', function (e) {
                    var $this = $(this),
                        index = tabType.index($this);
                    self.newGroup = false;
                    self.tabsType(index);
                });
                //绑定 搜索联系人event
                $('.chatSearch .searchInput').on('keyup', function (e) {
                    self.searchLinkman($(this));
                });
                //新建讨论组
                $('#newGroupBtn').on('click', function(){
                    var $this = $(this);
                    self.newGroup = true;
                    self.tabsType(1, 1);
                });
                //绑定 翻译设置event
                var setting = $('#translateSetting'),
                    setPanel = setting.siblings('.setting-panel'),
                    setItem = setPanel.find('li');

                self.renderSetting(setItem);

                setting.on('click', function(e){
                    if(setPanel.hasClass('none')){
                        setPanel.removeClass('none');
                    }else{
                        setPanel.addClass('none');
                    }
                    e.preventDefault();
                    e.stopPropagation();
                });
                $(document).on('click', function(e){
                    var target = $(e.target),
                        cls = target.prop('class') || '';
                    if(cls.indexOf('setting-panel')==-1){
                        setPanel.addClass('none');
                    }
                });
                setItem.on('click', function(){
                    var $this = $(this),
                        index = setItem.index($this),
                        mode = index || -1,
                        em = $this.find('em');

                    if(em.prop('class') == 'checked'){
                        mode = 0;
                        em.removeClass('checked');
                    }else{
                        setItem.find('em').removeClass('checked');
                        em.addClass('checked');
                    }
                    setPanel.addClass('none');
                    self.setTlMode(mode);
                });
                //渲染联系人列表
                self.renderLinkman();

                warn.off('click').on('click', function(){
                    self.openChat();
                });
                self.loadedEvent = true;

            }else{
                var curItem = wrap.find('#contentLeft .user-list li.user-highlight');
                if(curItem.length){
                    webIM.updateChat({id1: curItem.attr('data-gid') || curItem.attr('data-uid'), id2: me.id});
                }
            }

        },
        /**
         * 切换联系人选项卡
         */
        tabsType : function(index, kind, call){
            var self = this,
                leftChatPanel = $('#contentLeft'),
                newGroupBtns = leftChatPanel.find('.sel-user-btns'),
                tabType = leftChatPanel.find('.userTypeTab a'),
                obj = tabType.eq(index);
            if(!obj.hasClass('current') || kind){
                tabType.removeClass('current');
                obj.addClass('current');
                if(index){
                    if(self.newGroup && kind){
                        self.renderAtByNewGroup(call); // 渲染圈子
                    }else{
                        self.renderAt(call); // 渲染圈子
                    }
                }else{
                    if(newGroupBtns.length){
                        self.newGroupMember = null;
                        newGroupBtns.remove();
                        leftChatPanel.find('.user-name').css('max-width', 108);
                        leftChatPanel.find('.user-list').height(381);
                    }
                    self.renderLinkman(call); // 渲染联系人
                }
                $('.chatSearch .searchInput').val('');
            }else{
                call && call();
            }
        },
        /**
         * 渲染设置翻译模式
         */
        renderSetting : function(item){
            var self = this,
                tlMode = parseInt(cookie.get('tlMode'+self.curUid)) || 0;
            switch (tlMode){
                case -1: // 手动翻译
                    item.eq(0).find('em').addClass('checked');
                    break;
                case 1: // 自动翻译
                    item.eq(1).find('em').addClass('checked');
                    break;
                case 2: // 第三方翻译
                    break;
                default: // 没有设置翻译
                    break;
            }
        },
        /**
         * 设置翻译模式 -1 手动翻译  0 不翻译  1 自动翻译 2 人工翻译
         */
        setTlMode : function(n){
            var self = this,
                chatTl = $('#chatContent .chat-tl'),
                chatMsg = $('#chatContent .chat-left .chat-msg:has(.chat-content)');

            if(!group){
                switch (n){
                    case -1: // 手动翻译
                        chatTl.each(function(){
                            if($(this).prop('class').indexOf('none') > -1 && !$(this).siblings('.chat-msg:has(.tl)').length){
                                $(this).removeClass('none');
                            }
                        });
                        break;
                    case 1: // 自动翻译
                        webIM.chatMsgTl(chatMsg);
                        break;
                    case 2: // 第三方翻译
                        break;
                    default: // 没有设置翻译
                        chatTl.addClass('none');
                        break;
                }
            }
            cookie.set('tlMode'+self.curUid, n);
        },
        /**
         * 关闭聊天窗口
         */
        closeChat : function () {
            var self = this,
                box = $('#chatBox'),
                wrap = box.find('#chatWrap'),
                warn = box.find('.chat-warn'),
                wh = wrap.height(),
                isOpenChat = $.parseJSON(cookie.get('isOpenChat'+self.curUid)) || {};

            box.stop().animate({
                bottom: -wh
            }, 'fast', 'swing', function(){
                wrap.hide();
                warn.show();
                box.css({width: 'auto'}).stop().animate({bottom: 0}, 'fast');
                if(!$.isEmptyObject(isOpenChat)){
                    isOpenChat.closeChatWindow = true;
                    cookie.set('isOpenChat'+self.curUid, JSON.stringify(isOpenChat));
                }
            });
        },
        /**
         * 浏览器WebSocket连接错误
         * content :显示内容
         * action: 动作 destroy / hidden / show
         * time : 时间
         */
        notWebSocket : function (content, action, time) {
            if (action && 'destroy' === action) {
                setTimeout(function () {
                    $('.chat .socket-err').remove();
                }, time);
            }
            if (!time)time = 0;
            var temp = $(notWebSocketTemp),
                chatUtilPanel = $('.chat-util');
            chatUtilPanel.find('input').attr('disabled', 'disabled').css('cursor', 'auto');
            chatUtilPanel.find('.send').off().css('cursor', 'auto');
            if (content) {
                temp.html(content);
                if ($('.chat .socket-err').length) {
                    $('.chat .socket-err').html(content || '');
                } else {
                    $('.chat-content').after(temp);
                }
            }
            if (action && 'hidden' === action) {
                setTimeout(function () {
                    $('.chat .socket-err').hide();
                }, time);
            }
            if (action && 'show' === action) {
                setTimeout(function () {
                    $('.chat .socket-err').show();
                }, time);
            }
        }
    }
});