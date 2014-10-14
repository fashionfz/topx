/**
 * @description: 历史记录 详情
 * @author: YoungFoo(young.foo@helome.com)
 * @update: TinTao(tintao.li@helome.com)
 * @update: zhouzhiqiang(zhiqiang.zhou.helome.com)
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/imTips',
    'module/emotion',
    'module/recordListSearch'
], function(inter, util, common, imTips, emotion, recordListSearch){

    common.initLogin();
    window.chatType = 1;

    var userName = $('#linkman').val(),
        userId= util.location('uid'),
        avatar = '',

    detail = {

        init : function(){
            var pageSize = 10,
                self = this,
                tr = $(this).closest('tr'),
                pageTemplate = [
                    '<div id="paginationDetail">',
                    '<a class="prev mr10">上一页</a><a class="next">下一页</a>',
                    '<div>'
                ].join('');

            self.renderDetailList({
                containCurrent: true,
                userId: userId,
                groupId: '',
                preSize: pageSize,
                nextSize: 0,
                sendTime: new Date().getTime(),
                current: true //标记是第一次初始化,下一页不可点
            });
            if($('#paginationDetail').length){
                $('#paginationDetail').show();
            }else{
                $('.page').html(pageTemplate);
            }

            $('#paginationDetail').find('.prev').on('click',function(){
                if($(this).hasClass('current')) return;
                var pageSize = 10,
                    tr = $('.tab-table tr.tr-common').first(),
                    data = tr.length ? tr.attr('time') : last;

                self.renderDetailList({
                    containCurrent: tr.length == 0,
                    userId: userId,
                    groupId: '',
                    preSize: pageSize,
                    nextSize: 0,
                    sendTime: data
                })

            });
            $('#paginationDetail').find('.next').on('click',function(){
                if($(this).hasClass('current')) return;
                var pageSize = 10,
                    tr = $('.tab-table tr.tr-common').last(),
                    data = tr.length ? tr.attr('time') : first;

                self.renderDetailList({
                    containCurrent: tr.length == 0,
                    userId: userId,
                    groupId: '',
                    preSize: 0,
                    nextSize: pageSize,
                    sendTime: data
                })
            });
        },


        /**
         * 设置加载中状态
         */
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
        /**
         * 绑定数据到模板
         */
        _render : function(){
            var self = this,
                listBox = $('.tab-table');

            listBox.find('tr').remove();
            listBox.html(self.render({list: self.list}));
            self.list = null;
            self._loading('hide');
            self.initListEvent();
        },
        /**
         * 获取数据失败
         */
        _loadFailed : function(panel, errMsg, call){
            var errorTpl = $('<tr><td><div class="cr-content load-failed  tc">'+ (errMsg || '列表加载失败') +'</div></td></tr>');

            panel.html(errorTpl);
            errorTpl.find('a').on('click', function(e){
                errorTpl.remove();
                call && call();
                e.preventDefault();
            })
        },
        /**
         * 绑定模板
         */
        render : function(json){
            var self = this,
                list = json.list,
                recordHead = [
                    '<tr class="user-info-map" userId="'+ userId +'">',
                        '<td colspan="2">',
                            '<p class="user-info-map-content tl" style="background-color: #ffffff">',
                            '<strong>和联系人</strong>"<span class="username ellipsis">'+ userName +'</span>"',
                            '<input class="J-openChat btn-default btn-green btn-xs" type="button" value="联系">',
                            '<strong>的会话记录详情</strong>',
                            '<a href="/record/person" class="color-blue f12 dib pl10">&lt;&lt;返回</a>',
                            '</p>',
                        '</td>',
                    '</tr>'
                ],
                tradeTpl1 = [
                    '<tr class="tr-common me cr-content" data-id="#{id}" time="#{time}" groupId="#{groupId}">',
                        '<td class="cr-content-left">',
                            '<table>',
                            '<tr>',
                                '<td>',
                                    '<div class="cr-name">',
                                    '<strong class="f14 ellipsis username dib" title="#{userNameT}">#{userName}:</strong>',
                                    '</div>',
                                '</td>',
                                '<td class="">',
                                    '<span class="cr-text dib" data-all="#{allContent}">#{content}</span>',
                                '</td>',
                                '<td class="">',
                                    '<div class="cr-text-panel #{none}"><span class="icon panel-down"></span><strong class="pl5">展开</strong></div>',
                                '</td>',
                            '</tr>',
                            '</table>',
                        '</td>',
                        '<td class="cr-content-right">',
                            '<div class="cr-time ">#{msgTime}</div>',
                        '</td>',
                    '</tr>'
                ],
                $recordHead='',
                returnStr = [];

            if(list && list.length>0){
                // 记录第一行和最后一行的时间戳
                first = list[0].msgTime;
                last = list[list.length-1].msgTime;

                $.each(list, function(i, n){
                    var data = n,
                        content = data.content;
                    data.cls = '';//i % 2 == 0 ? '' :'odd';
                    if(true){
                        if(n.msgNum > 99) {
                            data.msgNum = '99+';
                            data.msgTip = '有超过99条新消息';
                        }else if(n.msgNum > 0){
                            data.msgTip = '有'+ n.msgNum +'条新消息';
                        }else {
                            data.showOrHide = ' hide';
                        }
                    }

                    if(data.type == 'me'){
                        data.userName = '我';
                    }else{
                        userName = data.userName;
                        userId = data.userId;
                        avatar = data.avatar;
                    }

                    try{
                        content = $.parseJSON(content);

                        data.none = 'none';
                        switch(content.subType){
                            case "text":
                                data.content = content.data.replace(/(<br>)+|(\n\r)+|(\n)+|(\s)+/gim, '&nbsp;');
                                /*
                                 [/audio_invite_mc] -> 发起语音通话邀请 | Audio calls
                                 [/video_invite_mc] -> 发起视频通话邀请 | Video calls
                                 [/audio_invite_timeout_mc] -> 发起的语音通话请求超时 | Audio call timeout
                                 [/video_invite_timeout_mc] -> 发起的视频通话请求超时 | Video call timeout
                                 [/av_hangup_mc] -> 已挂断通话 | hung up
                                 */
                                if(data.content == '[/audio_invite_mc]'){
                                    data.content = '发起语音通话邀请'
                                }else if(data.content == '[/video_invite_mc]'){
                                    data.content = '发起视频通话邀请'
                                }else if(data.content == '[/audio_invite_timeout_mc]'){
                                    data.content = '发起的语音通话请求超时'
                                }else if(data.content == '[/video_invite_timeout_mc]'){
                                    data.content = '发起的视频通话请求超时'
                                }else if(data.content == '[/av_hangup_mc]'){
                                    data.content = '已挂断通话'
                                }

                                if(data.content.length > 120){
                                    data.none = ''
                                    data.allContent = data.content;
                                    var tplStr = data.content.substring(120-4,120+5);
                                    var ii = tplStr.search(/\[!\w{2}\]/);
                                    ii = ii == -1 ? 0: ii-4;
                                    data.content = data.content.substring(0,120+ii)+' ...';
                                }
                                data.content = emotion.replaceEmotion(data.content);
                                break;
                            case "picture":
                                if (data.type == "me") {
                                    data.content = "发送了图片，<a class='dib' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</a>";
                                }else{
                                    data.content = "向您发送了图片，<a class='dib' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</a>";
                                }
                                break;
                            case "file":
                                if (data.type == "me") {
                                    data.content = "发送了文件，<a class='dib' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</a>";
                                }else{
                                    data.content = "向您发送了文件，<a class='dib' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</a>";
                                }
                                break;
                            case "multimedia":
                                if (data.type == "me") {
                                    data.content = "发送了语音消息，<a href='"+ content.url +"' title='"
                                        + content.fileName +"' target='_blank'>点击查看</a>";
                                }else{
                                    data.content = "向您发送了语音消息，<a href='"+ content.url +"' title='"
                                        +content.fileName +"' target='_blank'>点击查看</a>";
                                }
                                break;
                        }
                    }catch (e){
                        data.content = '';
                        throw e;
                    }finally{
                        data.time = n.msgTime
                        data.msgTime = util.dateFormat(new Date(n.msgTime), 'yyyy-MM-dd hh:mm:ss');
                        if(data.userName.length > 7){
                            data.userNameT = data.userName;
                        }
                        returnStr.push(util.template(tradeTpl1.join(''), data));
                    }
                });

            }else{
                returnStr.push('<tr><td align="center">还没有任何记录</td></tr>');
                $("#paginationDetail .next").addClass('current');
            }
            $('#searchName').val(userName);

            $recordHead = util.template(recordHead.join(''));

            returnStr.splice(0,0,$recordHead);
            return returnStr.join('');
        },


        /**
         * 绑定行事件
         */
        initListEvent : function(){
            var self = this,
                listItem = $(".tab-table tr");

            listItem.hover(function(){
                $(this).addClass('odd');
            }, function(){
                $(this).removeClass('odd');
            });


            $('.J-openChat').on('click', function(e){
                var $this = $(this),
                    userData = {
                        userId: userId,
                        userName: userName,
                        avatar: avatar
                    };

                imTips.fireChat(userData);
                e.preventDefault();
            });

            $('.tab-table .ellipsis').poshytip({
                showAniDuration: 0,
                alignTo: 'target',
                alignX: 'center',
                offsetY: -2,
                offsetX: 0
            });
            listItem.find('.cr-text-panel').each(function(i,e){
                var that = $(e),
                    text =  that.closest('tr').find('.cr-text'),
                    temp = text.html(),
                    all = text.attr('data-all');
                all = emotion.replaceEmotion(all);
                (function(that,text,temp,all){
                    that.on('click',function(){
                        var icon = that.find('span.icon'),
                            strong = that.find('strong');
                        if(icon.hasClass('panel-down')){
                            text.html(all);
                            icon.addClass('panel-up').removeClass('panel-down');
                            strong.html('收起');
                        }else{
                            text.html(temp);
                            icon.addClass('panel-down').removeClass('panel-up');
                            strong.html('展开');
                        }
                    })
                })(that,text,temp,all)
            })
        },

        /*加载 聊天上下文*/

        renderDetailList : function(option){
            var self = this,
                listPanel = $('.tab-table'),
                current = option.current || false;

            self._loading('show');
            util.setAjax(
                inter.getApiUrl().queryChatDetail,
                option,
                function(json){
                    self._loading('hide');
                    if(json.totalRowCount == 0){
//                        $.alert('没有更多了！');
                        if(option.preSize){
                            $("#paginationDetail .prev").addClass('current');
                        }else if(option.nextSize){
                            $("#paginationDetail .next").addClass('current');
                        }
                    }else{
                        $("#paginationDetail .current").removeClass('current');
                        if(option.preSize && json.totalRowCount < 10){
                            $("#paginationDetail .prev").addClass('current');
                        }
                        if(current || option.nextSize && json.totalRowCount < 10){
                            $("#paginationDetail .next").addClass('current');
                        }
                    }
                    self.list = json.list;
                    self._render();
                },
                function(err){
                    self._loading('hide');
                    $.alert(err);
//                    self._loadFailed(listPanel,err,null);
                },
                'GET'
            )
        }
    };

    detail.init();

    recordListSearch.init(function(json){
        var url = '/record/person_search?';
        if(json.startDate){
            url += 'start=' + json.startDate;
        }
        if(json.endDate){
            url += '&end=' + json.endDate
        }
        if(json.time){
            url += '&time=' + json.time;
        }
        if(json.keyWords){
            url += '&key=' + json.keyWords
        }
        if(json.type){
            url += '&type=' + json.type
        }
        if(json.userName){
            url += '&name=' + json.userName
        }
        if(json.binding){
            url += '&bind=' + json.binding
        }
        window.location.href = url;
    });

});
