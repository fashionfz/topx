/**
 * @description: 会话记录
 * @author: YoungFoo(young.foo@helome.com)
 * @update: TinTao(tintao.li@helome.com)
 * @update: zhouzhiqiang(zhiqiang.zhou.helome.com)
 */
require([
    'module/pageCommon',
    'common/interface',
    'common/util',
    'module/cookie',
    'module/imTips',
    'module/emotion',
    'module/recordListSearch'
], function(common, inter, util, cookie, imTips, emotion, recordListSearch){
    window.chatType = 2;// 群组
    common.initLogin();

    var page = 0,
        pageSize = 10,
        totalCount = 0,
        location = util.location(),
        list = null,
        loading = null,
        listPanel = $('.tab-table'),
        /**
         * 设置加载中状态
         */
        _loading = function(){
            var tab = $('.J-tab-content'),
                loading = $('.tab-loading'),
                reCall = false;

            if(arguments[0] === 'show'){
                loading.show();
                reCall = true;
            }else if(arguments[0] === 'hide'){
                loading.hide();
                reCall = false;
            }
            loading = reCall;
        },
        /**
         * 绑定数据到模板
         */
            _render = function(config){
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
            _loadFailed = function(panel, errMsg, call){
            var errorTpl = $('<tr><td><div class="cr-content load-failed tc">'+ (errMsg || '列表加载失败') +'</div></td></tr>');

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
            render = function(json){
            var list = json.list,
                tradeTpl1 = [
                    '<tr class="cr-content" time="#{time}" groupId="#{groupId}">',
                    '<td class="td">',
                        '<span class="cr-name dib fl">#{groupNameHtml}</span>',
                        '<a class="dib" href="/record/group_detail?gid=#{groupId}">',
                            '<span class="cr-text dib ellipsis">#{content}</span>',
                        '</a>',
                    '</td>',
                    '<td class="w120 td">',
                        '<span class="cr-time dib">#{msgTime}</span>',
                    '</td>',
                    '</tr>'
                ],
                returnStr = [];

            if(list && list.length>0){
                $.each(list, function(i, n){
                    var data = n,
                        content = data.content;

                    //忽略 没有群名字的消息
                    if(n.groupName == null){
                        return;
                    }
                    data.cls = '';//i % 2 == 0 ? '' :'odd';

                    if(n.msgNum > 99) {
                        data.msgNum = '99+';
                        data.msgTip = '有超过99条新消息';
                    }else if(n.msgNum > 0){
                        data.msgTip = '有'+ n.msgNum +'条新消息';
                    }else {
                        data.showOrHide = ' hide';
                    }

                    try{
                        content = $.parseJSON(content);

                        if( !content.subType){
                            switch(data.contentType){
                                case 1:
                                    content.subType = 'text';
                                    break;
                                case 2:
                                    content.subType = 'file';
                                    break;
                                case 3:
                                    content.subType = 'picture';
                                    break;
                                case 4:
                                    content.subType = 'multimedia';
                                    break;
                                default :
                                    content.subType = 'text';
                                    break;
                            }

                        }
                        switch(content.subType){
                            case "text":
                                data.content = content.data.replace(/(<br>)+|(\n\r)+|(\n)+|(\s)+/gim, '&nbsp;');
                                data.content = emotion.replaceEmotion(data.content);
                                break;
                            case "picture":
                                if (data.type == "me") {
                                    data.content = "发送了图片，<span class='dib' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</span>";
                                }else{
                                    data.content = "向您发送了图片，<span class='dib' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</span>";
                                }
                                break;
                            case "file":
                                if (data.type == "me") {
                                    data.content = "发送了文件，<span class='dib' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</span>";
                                }else{
                                    data.content = "向您发送了文件，<span class='dib' href='"+ content.url
                                        +"' target='_blank'>"+content.fileName+"</span>";
                                }
                                break;
                            case "multimedia":
                                if (data.type == "me") {
                                    data.content = "发送了语音消息，<span href='"+ content.url +"' title='"
                                        + content.fileName +"' target='_blank'>点击查看</span>";
                                }else{
                                    data.content = "向您发送了语音消息，<span href='"+ content.url +"' title='"
                                        +content.fileName +"' target='_blank'>点击查看</span>";
                                }
                                break;
                        }
                    }catch (e){
                        console.log('exception')
                        data.content = data.content || '';
                    }finally{
                        data.time = n.msgTime
                        data.msgTime = util.dateFormat(new Date(n.msgTime), 'yyyy-MM-dd hh:mm:ss');

                        data.avatar = ued_conf.root + (data.groupType ? 'images/group.png' : 'images/translate.png');

                        if(data.groupName.length > 7){
                            data.groupNameT = data.groupName;
                        }else{
                            data.groupNameT = '';
                        }
                        if(data.groupType == 'normal'){
                            data.groupNameHtml = '<a href="/group/detail/'+data.groupId+'" target="_blank">'+
                                '<strong class="f14 ellipsis pr10" title="'+ data.groupNameT +'">'+data.groupName+'</strong></a>';
                        }else{
                            data.groupNameHtml = '<strong class="f14 ellipsis pr10" title="'+ data.groupNameT +'">'+data.groupName+'</strong>';
                        }
                        returnStr.push(util.template(tradeTpl1.join(''), data));
                    }
                });
            }else{
                returnStr.push('<tr><td><p class="cr-content tc">还没有任何记录</p></td></tr>');
            }

            return returnStr.join('');
        },
        /**
         * 绑定分页
         */
            bindPagination = function(){
            $('.page').pagination(totalCount, {
                prev_text: '上一页',
                next_text: '下一页',
                link_to: 'javascript:',
                items_per_page: pageSize,
                num_display_entries: 6,
                current_page: page,
                num_edge_entries: 2,
                callback: function(page_id, jq){
                    page = page_id;
                    getMessageList();
                }
            })
        },

        /**
         * 绑定行事件
         */
            initListEvent = function(){
            var listItem = $(".tab-table tr");

            listItem.hover(function(){
                $(this).addClass('odd');
            }, function(){
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



        /**
         * 获取消息总数
         */
            getMessageCount = function(){
            var listPanel = $('.tab-table');


            _loading('show');
            /***
             *调用 getChatRecUrl
             * */


                util.setAjax(
                    inter.getApiUrl().getChatRecUrl,
                    {'msgType': 'no_system','chatType': 2, 'page': page, 'pageSize': pageSize},
                    function(json){
                        if(json.error){
                            _loading('hide');
                            _loadFailed(listPanel, json.error, function(){
                                getMessageCount();
                            })
                        }else{
                            totalCount = json.totalRowCount;
                            list = json.list;
                            bindPagination();
                        }
                    },
                    function(){
                        _loading('hide');
                        _loadFailed(listPanel, '服务器繁忙，请稍后再试。', function(){
                            getMessageCount();
                        })
                    },
                    'GET'
                );
        },
        /**
         * 获取消息列表
         */
            getMessageList = function(){
            var listPanel = $('.tab-table');
            if(!list){
                if(!loading){
                    _loading('show');
                }
                util.setAjax(
                    inter.getApiUrl().getChatRecUrl,
                    {'msgType': 'no_system', 'chatType': 2, 'page':  page, 'pageSize':  pageSize},
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
            }else{
                _render();
            }
        };



	recordListSearch.init(function(json){

        var json = json || {},
            url = '/record/group_search?';
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
    getMessageCount();

});
