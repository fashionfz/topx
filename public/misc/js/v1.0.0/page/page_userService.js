
/**
 * @description: 个人中心--》 服务
 * @author: zhiqiang.zhou@helome.com
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/pinyin'
],function(inter, util, common, pinyin){
    common.initLogin();

    // 定义列表构造函数
    var MessageList = function(){
        this.init();
    };

    MessageList.prototype = {
        init : function(){
            var self = this;
            self.getMessageCount();
        },
        getMessageCount : function(){
            var self = this;
            self.page = arguments[0] || 0;
            self.pageSize = 10;
            self._loading('show');
            util.setAjax(
                inter.getApiUrl().queryService,
                {'page': self.page, 'pageSize': self.pageSize, 'searchText': self.searchText || ''},
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
                }
            );
        },
        getMessageList : function(){
            var self = this;
            if(!self.list){
                if(!self.loading){
                    self._loading('show');
                }
                util.setAjax(
                    inter.getApiUrl().queryService,
                    {'page': self.page, 'pageSize': self.pageSize, 'searchText': self.searchText || ''},
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
                    }
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
        render : function(list, nullMsg){
            var self = this,
                tradeTpl = [
                    '<tr class="#{cls}" data-id="#{id}">',
                        '<td class="time"><span class="">#{time}</span></td>',
                        '<td class="industry">',
                            '<span class="">#{industry}</span>',
                        '</td>',
                        '<td class="title">',
                            '<a class="dib ellipsis txt-link" href="/services/detail/#{id}" target="_blank" title="#{title}">#{title}</a>',
                        '</td>',
                        '<td class="price">',
                            '<span class="red">#{price}</span>',
                        '</td>',
                        '<td class="operate">',
                            '<a href="/user/service/write?sid=#{id}" title="编辑" class="btn-edit"><em class="icon icon-edit"></em></a>',
                            '<a href="javascript:" title="删除" class="btn-delete ml10"><em class="icon icon-delete"></em></a>',
                        '</td>',
                    '</tr> '
                ].join(''),
                returnStr = [];

            if(list && list.length>0){
                $.each(list, function(i, n){
                    var data = {
                        id: n.id,
                        time: n.createDate,
                        title: n.title,
                        industry: n.industryName,
                        price : n.price == 0 ? '免费' : (!n.price ? '面议' : ('￥' + util.toFixed(n.price ,1)))
                    }
                    returnStr.push(util.template(tradeTpl, data));
                });
            }else{
                returnStr.push('<tr><td clospan ="5" align="center">'+ (nullMsg ? nullMsg: '暂未发布任何服务!') +'</td></tr>');
            }

            return returnStr.join('');
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
                deleteBtn = $(".btn-delete");
            deleteBtn.on("click", function(){
                var $this = $(this);
                $.confirm(
                    '<strong class="f14">确定删除该服务吗？</strong>',
                    function(){
                        self.handleDeleteOK($this);
                    },
                    function(){   },
                    {
                        title:'删除服务'
                    }
                );
            });
        },
        initListEvent : function(){
            var self = this,
                listItem = $(".tab-table tr");
            $('#searchService').on('click',function(){
                var key = $('#keyService').val();
                // 空值不允许搜索
                if(key){
                    self.search(key);
                }
            })
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

            util.setAjax(
                util.strFormat(inter.getApiUrl().deleteService, [id]),
                {},
                function (json) {
                    tips.close();
                    if(json.status == 1){
                        self.list = null;
                        if (!items.length && self.page >0) {
                            self.page--;
                        }
                        self.getMessageCount(self.page);
                    }
                },
                function () {
                    tips.close();
                    $.alert('服务器繁忙，请稍后再试。');
                }
            );

        },
        search : function(key){
            var self = this,
                map = $('.user-info-map .user-info-map-content');
            self.searchText = key;
            self.getMessageCount(0,key);
            if(map.find('a').length == 0){
                map.append('<a class="color-blue f12 dib pl10" href="/user/service">&lt;&lt;返回</a>')
            }
        }
    };
    new MessageList();
});
