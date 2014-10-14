/**
 * @description: 圈
 * @author: zhiqiang.zhou
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/pinyin'
], function (inter, util, common, pinyin) {
    common.initLogin();

    // 定义圈好友列表构造函数
    var MessageList = function () {
        this.init();
    };

    MessageList.prototype = {
        init: function () {
            var self = this;
            self.getMessageCount();
        },
        getMessageCount: function () {
            var self = this;
            self.page = arguments[0] || 0;
            self.pageSize = 10;
            self._loading('show');
            util.setAjax(
                inter.getApiUrl().queryFriends,
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
                },
                'GET'
            );
        },
        getMessageList: function () {
            var self = this;
            if (!self.list) {
                if (!self.loading) {
                    self._loading('show');
                }
                util.setAjax(
                    inter.getApiUrl().queryFriends,
                    {'page': self.page, 'pageSize': self.pageSize, 'searchText': self.searchText},
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
            } else {
                self._render();
            }
        },
        _loading: function () {
            var self = this,
                tab = $('.J-tab-content'),
                loading = $('.tab-loading'),
                reCall = false;

            if (arguments[0] === 'show') {
                loading.show().find('span').css({
                    top: (tab.height() - loading.find('span').height()) / 2,
                    left: (tab.width() - loading.find('span').width()) / 2
                });
                reCall = true;
            } else if (arguments[0] === 'hide') {
                loading.hide();
                reCall = false;
            }
            self.loading = reCall;
        },
        _render: function () {
            var self = this,
                nullMsg = null,
                listBox = $('.tab-table');

            listBox.find('tr').remove();
            if(self.searchText){
                nullMsg = '未找到任何好友';
            }
            listBox.append(self.render(self.list,nullMsg));
            self.list = null;
            self._loading('hide');
            self.initListEvent();
            self.initDeleteMessage();
        },
        render: function (list, nullMsg) {
            var self = this,
                tradeTpl = [
                    '<tr class="#{cls}" data-id="#{userId}">',
                    '<td class="message-status"><span class="#{status}"></span></td>',
                    '<td colspan="2" class="system-message-content left">',
                    /*-------------#{headUrl}------*/
                    '<div class="search-head fl">',
                    '<a href="#{linkUrl}" target="_blank"><img src="#{headUrl}" height="190" width="190" class="search-head-img" /></a>',
                    '<div class="search-country">',
                    '<span class="country-flag flag #{countryUrl}" title="#{country}"></span>',
                    '<img alt="#{gender}" title="#{gender}" src="#{genderUrl}" class="country-sex" width="13" height="13">',
                    '</div>',
                    '</div>',

                    '<div class="search-center fl">',
                    '<div class="name clearfix">',
                    '<a href="#{linkUrl}" target="_blank" title="#{userName}">#{userName}</a>',
                    '<span class="job" title="#{job}">#{job}</span>',
                    '</div>',
                    '<p class="introduce">#{personalInfo}</p>',
                    '<p class="skill clearfix">#{skills}</p>',
                    '</div>',
                    /*---------------*/
                    '</td>',
                    '<td class="operate" width="50">',
                    '<a href="javascript:" title="删除" class="btn-delete"><em class="icon icon-delete"></em></a>',
                    '</td>',
                    '</tr> '
                ].join(''),
                returnStr = [];

            if (list && list.length > 0) {
                $.each(list, function (i, n) {
                    n.skillstags = n.skillstags || [];
                    var skills = '',
                        gender = '男',
                        genderUrl = ued_conf.root + 'images/male.png';

                    for (var i = 0; i < n.skillsTags.length; i++) {
                        skills += '<a href="/expertsearch?ft=' + n.skillsTags[i].tag + '">' + n.skillsTags[i].tag + '</a>';
                    }
                    if (n.gender != 'MAN') {
                        gender = '女',
                            genderUrl = ued_conf.root + 'images/female.png';
                    }

                    var data = {
                        cls: '',
                        userId: n.userId,
                        status: '',
                        linkUrl: '/expert/detail/' + n.userId,
                        headUrl: n.headUrl,
                        countryUrl: n.countryUrl,
                        country: n.country,
                        userName: n.userName,
                        job: n.job,
                        skills: skills,
                        gender: gender,
                        genderUrl: genderUrl,
                        personalInfo: n.personalInfo
                    }
                    returnStr.push(util.template(tradeTpl, data));
                });
            } else {
                returnStr.push('<tr><td align="center">' + (nullMsg ? nullMsg : '还没有任何好友') + '</td></tr>');
            }

            return returnStr.join('');
        },
        bindPagination: function () {
            var self = this;
            $('.page').pagination(self.totalCount, {
                prev_text: '上一页',
                next_text: '下一页',
                link_to: 'javascript:',
                items_per_page: self.pageSize,
                num_display_entries: 6,    //连续分页主体部分分页条目数
                current_page: self.page,
                num_edge_entries: 2,      //两侧首尾分页条目数
                //prev_show_always: false,
                //next_show_always: false,
                callback: function (page_id, jq) {
                    self.page = page_id;
                    self.getMessageList();
                }
            })
        },
        initDeleteMessage: function () {
            var self = this,
                deleteBtn = $(".btn-delete");
            deleteBtn.on("click", function () {
                var $this = $(this);
                $.confirm(
                    '<strong class="f14">删除Ta后，您将不再出现在Ta的圈列表<br>中，系统不会发消息通知对方。</strong>',
                    function () {
                        self.handleDeleteOK($this);
                    },
                    function () {
                    },
                    {
                        title: '删除好友',
                        icon: false
                    }
                );
            });
        },
        initListEvent: function () {
            var self = this,
                listItem = $(".tab-table tr"),
                keyWord = $('#keyWord'),
                btn = $('.search-box .btn-default');
            btn.on('click', function () {
                var key = keyWord.val();
                key = $.trim(key);
                if(key != ''){
                    self.search(key);
                }
            })
            listItem.hover(function () {
                $(this).addClass('odd');
            }, function () {
                $(this).removeClass('odd');
            });
        },
        handleDeleteOK: function ($this) {
            var self = this,
                $tr = $this.closest('tr'),
                items = $tr.siblings('tr'),
                id = $tr.attr('data-id');
            //删除一行
            var tips = $.tips('loading');

            util.setAjax(
                // true 双方删除好友关系，false 但方面删除
                util.strFormat(inter.getApiUrl().deleteFriend, [id, 'true']),
                {},
                function (json) {
                    tips.close();
                    if (json.status == 1) {
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
                },
                'GET'
            );

        },
        matchHandler: function (key, item) {
            if (key.length == 0 || item.indexOf(key) != -1) {
                return true;
            }

            var qp = pinyin.getQP(item), //全拼
                jp = pinyin.getJP(item),//简拼,
                hp = pinyin.getHP(item); //

            if (key.length = 1) {
                if (jp.indexOf(key) != -1) {
                    return true;
                }
            } else {
                if (qp.indexOf(key) != -1) {
                    return true;
                }
                if (jp.indexOf(key) != -1) {
                    return true;
                }
                if (hp.indexOf(key) != -1) {
                    return true;
                }
            }
            return false;
        },
        search: function (key) {
            var self = this;
            self.searchText = key;
            self.getMessageCount(0);
            $('.user-info-map .user-info-map-content').html('<strong>搜索结果</strong><a class="color-blue f12 dib pl10" href="/user/at">&lt;&lt;返回</a>')
        }
    };
    new MessageList();
});
