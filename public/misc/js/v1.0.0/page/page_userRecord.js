/**
 * @description: 会话记录-用户
 * @author: YoungFoo(young.foo@helome.com)
 * @update: TinTao(tintao.li@helome.com)
 * update: zhouzhiqiang(zhiqiang.zhou.helome.com)
 */
require([
    'module/pageCommon',
    'common/interface',
    'common/util',
    'module/cookie',
    'module/imTips',
    'module/emotion',
    'module/recordListSearch'
], function (common, inter, util, cookie, imTips, emotion, recordListSearch) {
    common.initLogin();

    window.chatType = 1;


    var page = 0,
        pageSize = 10,
        totalCount = 0,
        list = null,
        loading = null,
        listPanel = $('.tab-table'),
        /**
         * 设置加载中状态
         */
            _loading = function () {
            var tab = $('.J-tab-content'),
                loading = $('.tab-loading'),
                reCall = false;

            if (arguments[0] === 'show') {
                loading.show();
                reCall = true;
            } else if (arguments[0] === 'hide') {
                loading.hide();
                reCall = false;
            }
            loading = reCall;
        },
        /**
         * 绑定数据到模板
         */
            _render = function () {
            var config = config || {},
                listBox = $('.tab-table');

            listBox.find('tr').remove();
            listBox.html(render({list: list}));
            list = null;
            _loading('hide');
            initListEvent();
        },
        /**
         * 获取数据失败
         */
            _loadFailed = function (panel, errMsg, call) {
            var errorTpl = $('<tr><td><div class="cr-content load-failed tc">' + (errMsg || '列表加载失败') + '</div></td></tr>');

            panel.html(errorTpl);
            errorTpl.find('a').on('click', function (e) {
                errorTpl.remove();
                call && call();
                e.preventDefault();
            })
        },
        /**
         * 绑定模板
         */
            render = function (json) {
            var list = json.list,

                tradeTpl1 = [
                    '<tr class="cr-content" data-id="#{id}" time="#{time}" userId="#{userId}" userName = "#{userName}">',
                        '<td class="td">',
                            '<a class="cr-name" href="/expert/detail/#{userId}" target="_blank">',
                                '<strong class="f14 ellipsis pr10" title="#{userNameT}">#{userName}</strong>',
                            '</a>',
                            '<a class="" href="/record/person_detail?uid=#{userId}">',
                                '<span class="cr-text  ellipsis">#{content}</span>',
                            '</a>',
                        '</td>',
                        '<td class="w120 td">',
                            '<span class="cr-time dib">#{msgTime}</span>',
                        '</td>',
                    '</tr>'
                ],
                returnStr = [];

            if (list && list.length > 0) {
                $.each(list, function (i, n) {
                    var data = n,
                        content = data.content;
                    data.cls = '';//i % 2 == 0 ? '' :'odd';
                    if (true) {
                        if (n.msgNum > 99) {
                            data.msgNum = '99+';
                            data.msgTip = '有超过99条新消息';
                        } else if (n.msgNum > 0) {
                            data.msgTip = '有' + n.msgNum + '条新消息';
                        } else {
                            data.showOrHide = ' hide';
                        }
                    }
                    try {
                        content = $.parseJSON(content);

                        switch (content.subType) {
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


                                if(data.userName && data.userName.length > 7){
                                    data.userNameT = data.userName;
                                }
                                data.content = emotion.replaceEmotion(data.content);
                                break;
                            case "picture":
                                if (data.type == "me") {
                                    data.content = "发送了图片，<span class='di' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</span>";
                                }else{
                                    data.content = "向您发送了图片，<span class='di' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</span>";
                                }
                                break;
                            case "file":
                                if (data.type == "me") {
                                    data.content = "发送了文件，<span class='di' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</span>";
                                }else{
                                    data.content = "向您发送了文件，<span class='di' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</span>";
                                }
                                break;
                            case "multimedia":
                                if (data.type == "me") {
                                    data.content = "发送了语音消息，<span class='di' href='"+ content.url +"' title='"
                                        + content.fileName +"' target='_blank'>点击查看</span>";
                                }else{
                                    data.content = "向您发送了语音消息，<span class='di' href='"+ content.url +"' title='"
                                        +content.fileName +"' target='_blank'>点击查看</span>";
                                }
                                break;
                        }
                    } catch (e) {
                        data.content = content || '';
                        util.trace('json error')
//                        throw e;
                    } finally {
                        data.time = n.msgTime
                        data.msgTime = util.dateFormat(new Date(n.msgTime), 'yyyy-MM-dd hh:mm:ss');
                        returnStr.push(util.template(tradeTpl1.join(''), data));
                    }
                });
            } else {
                returnStr.push('<tr><td><p class="cr-content tc">还没有任何记录</p></td></tr>');
            }

            return returnStr.join('');
        },
        /**
         * 绑定分页
         */
            bindPagination = function () {
            $('.page').pagination(totalCount, {
                prev_text: '上一页',
                next_text: '下一页',
                link_to: 'javascript:',
                items_per_page: pageSize,
                num_display_entries: 6,
                current_page: page,
                num_edge_entries: 2,
                callback: function (page_id, jq) {
                    page = page_id;
                    getMessageList();
                }
            })
        },

        /**
         * 绑定行事件
         */
        initListEvent = function () {
            var listItem = $(".tab-table tr");

            listItem.hover(function () {
                $(this).addClass('odd');
            }, function () {
                $(this).removeClass('odd');
            });

            $('.ellipsis').poshytip({
                showAniDuration: 0,
                alignTo: 'target',
                alignX: 'center',
                offsetY: -2,
                offsetX: 0
            });
            listItem.each(function(i,n){
                var line =  $(n),
                    nameW = line.find('.cr-name').width(),
                    mw = 600 -20- nameW;
                line.find('.cr-text').css('width',mw+'px');
            })
        },


        /***
         * 获取消息总数
         */
        getMessageCount = function () {
            _loading('show');
            /***
             *  调用 getChatRecUrl
             * */

            util.setAjax(
                inter.getApiUrl().getChatRecUrl,
                {'msgType': 'no_system', 'chatType': 1, 'page': page, 'pageSize': pageSize},
                function (json) {
                    if (json.error) {
                        _loading('hide');
                        _loadFailed(listPanel, json.error, function () {
                            getMessageCount();
                        })
                    } else {
                        totalCount = json.totalRowCount;
                        list = json.list;
                        bindPagination();
                    }
                },
                function () {
                    _loading('hide');
                    _loadFailed(listPanel, '服务器繁忙，请稍后再试。', function () {
                        getMessageCount();
                    })
                },
                'GET'
            );
        },
        /**
         * 获取消息列表
         */
            getMessageList = function () {
            if (!list) {
                if (!loading) {
                    _loading('show');
                }
                util.setAjax(
                    inter.getApiUrl().getChatRecUrl,
                    {'msgType': 'no_system', 'chatType': 1, 'page': page, 'pageSize': pageSize},
                    function (json) {
                        if (json.error) {
                            _loading('hide');
                            _loadFailed(listPanel, json.error, function () {
                                getMessageCount();
                            })
                        } else {
                            totalCount = json.totalRowCount;
                            list = json.list;
                            _render();
                        }
                    },
                    function () {
                        _loading('hide');
                        _loadFailed(listPanel, '服务器繁忙，请稍后再试。', function () {
                            getMessageCount();
                        })
                    },
                    'GET'
                );
            } else {
                _render();
            }
        };

    recordListSearch.init(function (json) {
        var json = json || {},
            url = '/record/person_search?';
        if(json.startDate){
            url += 'start=' + json.startDate;
        }
        if(json.endDate){
            url += '&end=' + json.endDate;
        }
        if(json.time){
            url += '&time=' + json.time;
        }
        if(json.keyWords){
            url += '&key=' + json.keyWords;
        }
        if(json.type){
            url += '&type=' + json.type;
        }
        if(json.userName){
            url += '&name=' + json.userName;
        }
        if(json.binding){
            url += '&bind=' + json.binding;
        }
        window.location.href = url;
    });
    getMessageCount();
});
