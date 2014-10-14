/**
 * @description: 用户中心--》群组 -->成员列表
 * @author: zhiqiang.zhou@helome.com
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/pinyin',
    'module/cookie'
],function(inter, util, common, pinyin, cookie){
    common.initLogin();

    var $count = $('.J-count-member'),
        countM = parseInt($count.html()) || 1;
    // 定义群成员列表构造函数
    var GroupMemberList = function(){
        this.init()
    };
    GroupMemberList.prototype = {
        init : function(){
            var self = this;
            self.gid = util.location().gid;
            self.getMemberCount(0);
        },
        /**
         * 获取列表数据用于分页
         */
        getMemberCount : function(){
            var self = this;
            self.page = arguments[0] || 0;
            self.pageSize = 10;
            self._loading('show');
            util.setAjax(
                inter.getApiUrl().getGroupMemberByPage,
                {'page': self.page, 'pageSize': self.pageSize, groupId: self.gid},
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
        /**
         * 获取列表数据用于渲染列表
         */
        getMemberList : function(){
            var self = this;
            if(!self.list){
                if(!self.loading){
                    self._loading('show');
                }
                util.setAjax(
                    inter.getApiUrl().getGroupMemberByPage,
                    {
                        'page': self.page,
                        'pageSize': self.pageSize,
                        groupId: self.gid
                    },
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
        /**
         * 加载提示框
         */
        _loading : function(){
            var self = this,
                tab = $('.J-tab-content'),
                loading = $('.tab-loading'),
                reCall = false;

            if(arguments[0] === 'show'){
                loading.show().height(tab.height()).find('span').css({
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
         * 渲染列表辅助方法
         */
        _render : function(){
            var self = this,
                listBox = $('.tab-table');

            listBox.find('tr').remove();
            listBox.append(self.render(self.list));
            self.list = null;
            self._loading('hide');
            self.initListEvent();
            self.initDeleteItem();
        },
        /**
         * 渲染列表
         */
        render : function(list){
            var self = this,
                curUid = cookie.get('_u_id'),
                tradeTpl = [
                    '<tr class="#{cls}" data-id="#{userId}">',
                        '<td colspan="2" class="left user-common">',
                        '<div class="user-head fl">',
                            '<img src="#{headUrl}" width="41" height="41">',
                            '<span class="chat-icon user-head-i"></span>',
                        '</div>',
                        '<div class="search-center fl">',
                            '<div class="name clearfix">',
                                '<a class="fl" href="#{linkUrl}" target="_blank" title="#{userName}">#{userName}</a>',
                                '<span class="job fl" title="#{job}">#{job}</span>',
                            '</div>',
                            '<p class="introduce">#{personalInfo}</p>',
                        '</div>',
                        '</td>',
                        '<td class="operate" width="50">',
                            '#{operation}',
                        '</td>',
                    '</tr> '
                ].join(''),
                returnStr = [];

            if(list && list.length>0){
                $.each(list, function(i, n){

                    var data = {
                        cls: '',
                        userId: n.userId,
                        linkUrl: '/expert/detail/'+n.userId,
                        headUrl: n.headUrl,
                        userName: n.userName,
                        job: n.job,
                        personalInfo: n.personalInfo,
                        operation: curUid == n.userId ? '' : '<a href="javascript:" title="删除" class="btn-delete"><em class="icon icon-delete"></em></a>'
                    };
                    returnStr.push(util.template(tradeTpl, data));
                });
            }else{
                returnStr.push('<tr><td align="center">还没有任何好友</td></tr>');
            }

            return returnStr.join('');
        },
        /**
         * 根据数据绑定分页
         */
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
                    self.getMemberList();
                }
            })
        },
        /**
         * 绑定每行删除操作事件
         */
        initDeleteItem : function(){
            var self = this,
                deleteBtn = $(".btn-delete");
            deleteBtn.on("click", function(){
                var $this = $(this);
                $.confirm('<strong class="f14">您确定要删除该群成员吗？</strong>',
                    function(){
                        self.handleDeleteOK($this);
                    },
                    null,
                    {
                        title:'删除群成员',
                        icon:false
                    }
                );
            });
        },
        /**
         * 绑定列表事件
         */
        initListEvent : function(){
            var self = this,
                listItem = $(".tab-table tr");

            listItem.hover(function(){
                $(this).addClass('odd');
            }, function(){
                $(this).removeClass('odd');
            });
        },
        /**
         * 删除辅助方法
         */
        handleDeleteOK : function($this){
            var self = this,
                $tr = $this.closest('tr'),
                items = $tr.siblings('tr'),
                id = $tr.attr('data-id');
            //删除一行
            var tips = $.tips('loading');
            util.setAjax(
                inter.getApiUrl().deleteGroupMember,
                {
                    groupId: self.gid,
                    userId: id
                },
                function (json) {
                    tips.close();
                    if(json.status){
                        countM -= 1;
                        $count.html(countM);
                        if(!items.length && self.page >0){
                            self.page--;
                        }
                        self.getMemberCount(self.page);
                    }
                },
                function () {
                    tips.close();
                    $.alert('服务器繁忙，请稍后再试。');
                },
                'GET'
            );

        }

    };
    new GroupMemberList();
});
