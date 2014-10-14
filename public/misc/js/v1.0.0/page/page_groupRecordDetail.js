/**
 * @description: 群组会话记录 详情
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

    common.initLogin();

    window.chatType = 2;// 群组

    var groupName ='',
        groupId = util.location('gid'),
        groupType = '',
        detail = {

            init : function(){
                var pageSize = 10,
                    self = this,
                    pageTemplate = [
                        '<div id="paginationDetail">',
                            '<a class="prev mr10">前'+pageSize+'条</a><a class="next">后'+pageSize+'条</a>',
                        '<div>'
                    ].join('');

                self.renderDetailList({
                    containCurrent: true,
                    groupId: groupId,
                    preSize: pageSize,
                    nextSize: 0
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
                        data = tr.attr('time');

                    self.renderDetailList({
                        containCurrent: false,
                        groupId: groupId,
                        preSize: pageSize,
                        nextSize: 0,
                        sendTime: data
                    })

                });
                $('#paginationDetail').find('.next').on('click',function(){
                    if($(this).hasClass('current')) return;
                    var pageSize = 10,
                        tr = $('.tab-table tr.tr-common').last(),
                        data = tr.attr('time');

                    self.renderDetailList({
                        containCurrent: false,
                        groupId: groupId,
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
                    loading.show();
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
            _render : function(config){
                var self = this,
                    config = config || {},
                    listBox = $('.tab-table');

                listBox.find('tr').remove();
                listBox.html(self.render({list: self.list, isDetail: config.isDetail}));
                self.list = null;
                self._loading('hide');
                self.initListEvent();
                if(config.isDetail){
                    listBox.find('tr .cr-text').off('click');
                }
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
                    isDetail = json.isDetail,
                    recordHead = [
                        '<tr class="user-info-map">',
                            '<td colspan="2" class="" userId="#{id}">',
                            '<p class="user-info-map-content tl">',
                                '<strong>#{str}</strong>"<span class="username ellipsis">#{name}</span>"',
                                '<input class="J-openChat btn-default btn-green btn-xs" type="button" value="联系">',
                                '<strong>的会话记录详情</strong>',
                                '<a href="/record/group" class="color-blue f12 dib pl10">&lt;&lt;返回</a>',
                            '</p>',
                            '</td>',
                        '</tr>'
                    ],
                    tradeTpl1 = [
                        '<tr class="tr-common  cr-content" data-id="#{id}" time="#{time}" groupId="#{groupId}">',
                        '<td class="cr-content-left #{type}">',
                        '<table>',
                        '<tr>',
                        '<td>',
                        '<div class="cr-name">',
                        '<#{a} href="/expert/detail/#{userId}" target="_blank">',
                        '<strong class="f14 ellipsis username dib" title="#{userNameT}">#{userName}:</strong>',
                        '</#{a}>',
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
                    $.each(list, function(i, n){
                        var data = n,
                            content = data.content;
                        data.cls = '';//i % 2 == 0 ? '' :'odd';
                        data.display = 'dib';
                        data.a = 'a';
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
                        if(isDetail){
                            if(data.type == 'me'){
                                data.userName = '我';
                                data.a ='span';
                            }
                            groupName = data.groupName;
                            groupId = data.groupId;
                            groupType = data.groupType;

                        }
                        try{

                            content = $.parseJSON(content);
                            data.none = 'none';
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
                                        content.subType = 'system';
                                        break;
                                }

                            }else{
                                if(content.type == 2 ){
                                    content.subType = 'system'
                                }
                            }
                            switch(content.subType){
                                case "text":
                                    data.content = content.data.replace(/(<br>)+|(\n\r)+|(\n)+|(\s)+/gim, '&nbsp;');
                                        if(data.content.length > 120){
                                            data.none = ''
                                            data.allContent = data.content;
                                            data.content = data.content.substring(0,120)+' ...';
                                        }
                                        if(data.userName && data.userName.length > 7){
                                            data.userNameT = data.userName;
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
                                case "system" :
                                    data.type = 'me';
                                    data.a = 'span';
                                    data.userName = '系统消息';
                                    data.content = '<span class="wrap-gray">'+ content.data +'</span>';
                                    break;
                            }
                        }catch (e){
                            data.content =  '';
                            throw e;
                        }finally{
                            data.time = n.msgTime
                            data.msgTime = util.dateFormat(new Date(n.msgTime), 'yyyy-MM-dd hh:mm:ss');
                            if(data.groupId){
                                data.id = data.groupId;
                                data.avatar = ued_conf.root + (data.groupType ? 'images/group.png' : 'images/translate.png');
                            }
                            returnStr.push(util.template(tradeTpl1.join(''), data));
                        }
                    });

                }else{
                    returnStr.push('<tr><td align="center">还没有任何记录</td></tr>');
                }
                $('#searchName').val(groupName);
                $recordHead = util.template(recordHead.join(''), {name: groupName, id: groupId, str: '在群组'});

                returnStr.splice(0,0,$recordHead);
                return returnStr.join('');
            },


            /**
             * 绑定行事件
             */
            initListEvent : function(){
                var self = this,
                    listItem = $(".tab-table tr.tr-common");

                listItem.hover(function(){
                    $(this).addClass('odd');
                }, function(){
                    $(this).removeClass('odd');
                });


                $('.J-openChat').on('click', function(e){
                    var $this = $(this),
                        groupData = {
                            groupId: groupId,
                            groupName: groupName,
                            groupType: groupType
                        };

                    imTips.fireChat(groupData);
                    e.preventDefault();
                });
                $('.ellipsis').poshytip({
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
                    current = false;
                if(!option.sendTime){
                    option.sendTime = new Date().getTime();
                    current = true;
                }
                self._loading('show');
                util.setAjax(
                    inter.getApiUrl().queryChatDetail,
                    option,
                    function(json){
                        self._loading('hide');
                        if(json.totalRowCount == 0){
                            $.alert('没有更多了！')
//                            self._loadFailed(listPanel,'没有更多了！',null);
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

                            self.list = json.list;
                            self._render({isDetail:true});
                        }
                    },
                    function(err){
                        self._loading('hide');
                        $.alert(err);
//                        self._loadFailed(listPanel,err,null);
                    },
                    'GET'
                )
            }

        };

    detail.init();

    recordListSearch.init(function(json){
        var url = '/record/group_search?';
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
