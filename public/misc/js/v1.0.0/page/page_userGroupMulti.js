/**
 * @description: 用户中心--》群组 多人会话列表
 * @author: zhiqiang.zhou@helome.com
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/pinyin',
    'module/imTips'
],function(inter, util, common, pinyin, imTips){
    common.initLogin();

    // 定义多人会话列表构造函数
    var GroupMultiList = function(){
        this.init();
    };

    GroupMultiList.prototype = {
        init : function(){
            var self = this;
            self.inter = inter.getApiUrl().queryTempGroups;
            self.getGroupMultiCount();
        },
        getGroupMultiCount : function(){
            var self = this,
                page = arguments[0] || 0,
                pageSize = 10;

            self.params = {'page': page, 'pageSize': pageSize, type: 2};
            if(self.keyword){
                self.params.groupName = self.keyword;
            }
            self._loading('show');
            util.setAjax(
                self.inter,
                self.params,
                function (json) {
                    if (json.error) {
                        $.alert(json.error);
                    } else {
                        self.totalCount = json.totalRowCount;
                        self.list = json.list;
                        self.bindPagination();
                    }
                },
                function () {
                    self._loading('hide');
                    $.alert('服务器繁忙，请稍后再试。');
                },
                'GET'
            );
        },
        getGroupMultiList : function(){
            var self = this;
            if(!self.list){
                if(!self.loading){
                    self._loading('show');
                }
                util.setAjax(
                    self.inter,
                    self.params,
                    function (json) {
                        if (json.error) {
                            self._loading('hide');
                            $.alert(json.error);
                        } else {
                            self.totalCount = json.totalRowCount;
                            self.list = json.list;
                            self._render();
                        }
                    },
                    function () {
                        self._loading('hide');
                        $.alert('服务器繁忙，请稍后再试。');
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
                listH = tab.closest('.user-info').outerHeight()-$('.tab-menu').outerHeight(),
                reCall = false;

            if(arguments[0] === 'show'){
                loading.show().height(listH).find('span').css({
                    top : (listH - loading.find('span').outerHeight())/2,
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
        },
        render : function(list){
            var self = this,
                tradeTpl = [
                    '<tr class="#{cls}" data-id="#{groupId}" data-type="#{groupType}">',
                        '<td colspan="2" class="group-item left">',
                            '<div class="p20 oh">',
                                    '<div class="search-head fl">',
                                        '<img src="#{headUrl}" height="41" width="41" />',
                                    '</div>',
                                    '<div class="search-center fl">',
                                        '<div class="name clearfix">',
                                            '<span class="f14" title="#{groupName}">#{groupName}</span>',
                                        '</div>',
                                        '<div class="info">',
                                            '<span>成员数：#{countMem}人</span>',
                                            '<span>创建时间：#{createDate}</span>',
                                        '</div>',
                                    '</div>',
                            '</div>',
                        '</td>',
                        '<td class="operate">',
                            '<a href="javascript:" title="聊天" class="btn-chat"><em class="icon icon-chat"></em></a>',
                            '<a href="javascript:" title="退出" class="btn-delete  ml10"><em class="icon icon-delete"></em></a>',
                        '</td>',
                    '</tr> '
                ].join(''),
                returnStr = [];

            if(list && list.length>0){
                $.each(list, function(i, n){
                    n.tags =  n.tags || [];
                    var skills = '';
                    for(var i=0 ;i< n.tags.length;i++){
                        skills += '<a href="/expertsearch?ft='+ n.tags[i] +'">'+ n.tags[i] +'</a>';
                    }
                    var data = {
                        cls:'',
                        groupId: n.id,
                        status:'',
                        groupName: n.groupName,
                        groupType: n.type,
                        industryId: n.industryId,
                        industryName: n.industryName,
                        countMem: n.countMem+"",
                        createDate: util.dateFormat(n.createDate,"yyyy-MM-dd"),
                        skills: skills,
                        authority: n.groupPriv=='public' ? '' : 'icon-group-lock mr10'
                    };
                    if (n.type == 'translate'){
                        data.headUrl = ued_conf.root+ "images/translate.png";
                    }else{
                        data.headUrl = ued_conf.root+ "images/session.png";
                    }
                    if(self.type > 0){
                        //删除了 edit & member  按钮
                        tradeTpl.splice(25,2);
                    }
                    returnStr.push(util.template(tradeTpl, data));
                });
            }else{
                returnStr.push('<tr><td align="center"><p class="p20">还没有参与任何多人会话</p></td></tr>');
            }

            return returnStr.join('');
        },
        bindPagination : function(){
            var self = this;
            $('.page').pagination(self.totalCount, {
                prev_text: '上一页',
                next_text: '下一页',
                link_to: 'javascript:',
                items_per_page: self.params.pageSize,
                num_display_entries: 6,
                current_page: self.params.page,
                num_edge_entries: 2,
                //prev_show_always: false,
                //next_show_always: false,
                callback: function(page_id, jq){
                    self.params.page = page_id;
                    self.getGroupMultiList();
                }
            })
        },

        initListEvent : function(){
            var self = this,
                listItem = $(".tab-table tr"),
                btnMember = listItem.find('.btn-member'),
                btnChat = listItem.find('.btn-chat'),
                btnDelete = listItem.find('.btn-delete'),
                keyWord = $('#keyWord');

            btnChat.on("click", function(){
                var $this = $(this),
                    itemTr = $this.closest('tr'),
                    chatId = itemTr.attr('data-id')|| 0,
                    chatType = itemTr.attr('data-type')|| 'translate',
                    groupData = {
                        groupId: chatId,
                        groupName: itemTr.find('.name span').prop('title'),
                        groupType: chatType,
                        groupAvatar: $('.search-head img').prop('src')
                    };

                imTips.fireChat(groupData);
            });

            btnDelete.on("click", function(){
                var $this = $(this);
                $.confirm(
                    '<strong class="f14">你确定要退出该多人会话吗？</strong>' +
                    '<p class="f12">退出后将无法再查看该会话消息记录，也不再收取该会话消息记录。</p>',
                    function(){
                        self.handleDeleteOK($this);
                    },
                    null,
                    {
                        title:'<strong>退出多人会话</strong>',
                        icon:false
                    }
                );
            });

            keyWord.on('keyup change',function(){
                var key = $(this).val()
                if(self.list){
                    self.search(key,self.list);
                }else{
                    util.trace('list  is null')
                }

            });

            listItem.hover(function(){
                $(this).addClass('odd');
            }, function(){
                $(this).removeClass('odd');
            });

            $('#searchGroup').on('click', function(){
                var keyword = $('#keyGroup').val();
                if(keyword){
                    self.keyword = keyword;
                }else{
                    self.keyword = null;
                }
                self.getGroupMultiCount();
            });
        },
        handleDeleteOK : function($this){
            var self = this,
                $tr = $this.closest('tr'),
                items = $tr.siblings('tr'),
                id = $tr.attr('data-id');
            //删除一行
            var tips = $.tips('loading');
            util.setAjax(
                util.strFormat(inter.getApiUrl().exitGroup, [id]),
                {},
                function (json) {
                    tips.close();
                    if(json.status == 1){
                        if(!items.length && self.params.page){
                            self.params.page--;
                        }
                        self.getGroupMultiCount(self.params.page);
                    }
                },
                function () {
                    tips.close();
                    $.alert('服务器繁忙，请稍后再试。');
                },
                'GET'
            );

        },
        matchHandler: function(key, item){
            if(key.length ==0 ||  item.indexOf(key) != -1){
                return true;
            }

            var qp = pinyin.getQP(item), //全拼
                jp = pinyin.getJP(item),//简拼,
                hp = pinyin.getHP(item); //

            if(key.length = 1){
                if(jp.indexOf(key) != -1){
                    return true;
                }
            }else{
                if(qp.indexOf(key) != -1){
                    return true;
                }
                if(jp.indexOf(key) != -1){
                    return true;
                }
                if(hp.indexOf(key) != -1){
                    return true;
                }
            }
            return false;
        },
        search : function(key,list){
            var self = this,
                listBox = $('.tab-table'),
                listTemp = [];
            util.trace('search '+list.length);
            $.each(list,function(i,n){
                var name = n.userName;
                util.trace(name);
                util.trace(i);
                if(self.matchHandler(key,name)){
                    listTemp.push(n);
                }else{
                    util.trace('xx')
                }
            });


            listBox.find('tr').remove();
            listBox.append(self.render(listTemp));
        }

    };
    new GroupMultiList();
});
