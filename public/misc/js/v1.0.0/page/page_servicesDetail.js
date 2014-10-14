/**
 * @description: 服务详细
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/interface',
    'module/pageCommon',
    'common/util',
    'module/commentStar',
    'module/imTips'
],function(inter, common, util, star, imTips){
    var api = null,
        tip = null;
        
    common.initLogin();
    common.searchTypeInstance.setActive(common.searchData.serviceData);
    /**
     * 联系按钮事件
     */
    function bindChatBtn($this){
        var self = this,
            chatId = $this.attr('data-id')|| 0,
            youData = {
                userId: chatId,
                userName: $('.posted-name').text(),
                avatar: $('.posted-head img').attr('src')
            };

        imTips.fireChat(youData);
    }

    /**
     * 保存评价
     */
    function saveComment(api){
        var uid = parseInt($('#servicesId').val())||0,
            content = $.trim($('#comment-content').val()),
            starScore = parseInt($('#starScore').val())|| 0,
            menu = $('.tab-menu-btn[data-level="'+starScore+'"]'),
            menuTxt = menu.text().match(/\d+/g) || [],
            menuVal = parseInt(menuTxt.join(''))||0,
            commentStar = $('.star-num'),
            commentNum = parseInt(commentStar.text().replace(/\D+/g, ''))||0,
            oSns = $('.share a'),
            //oAverage = $('.stars .icon-star-base'),
            //oAverageTit = oAverage.prop('title'),
            //average = parseFloat(oAverageTit.substring(4, oAverageTit.length))||0,
            //totalScore = average * commentNum,
            sns = [];

        if(oSns.length>0){
            oSns.each(function(i, n){
                if($(n).prop('class').indexOf('unuse')<=-1){
                    sns.push($(n).attr('data-value'));
                }
            })
        }
        if(!starScore) {
            $.alert('请为这位用户的服务评分!');
        }else if(content.length<1){
            $('#comment-content').val('');
            $.alert('请输入评价内容!');
        }else if(content.length>250){
            $.alert('评价内容最多250个字符!');
        }else{
            var tips = $.tips('loading');
            util.setAjax(inter.getApiUrl().saveNewCommentUrl, {
                'toCommentId': uid,
                'content': content,
                'level': starScore,
                'type': 'service',
                'sns': sns
            }, function(json){
                tips.close();
                if(json.error){
                    $.alert(json.error);
                }else{
                    //totalScore = totalScore + starScore;
                    api.close();
                    comment.init();
                    menu.text(menu.text().replace(/\d+/g, menuVal+1));
                    //commentStar.text('(' + (commentNum + 1) + ')');
                    /*average = totalScore/(commentNum + 1);
                    oAverage.prop( 'title', '平均分：'+ Math.ceil(average * Math.pow(10,1))/Math.pow(10,1) );
                    oAverage.find('.icon-star-on').width(average/5*73);*/
                }
            }, function(){
                tips.close();
                $.alert('服务器繁忙，请稍后再试。');
            });
        }
    }

    /**
     * 评价按钮事件
     */
    $('.btn-toComment').on('click', function(e){
        var tpl = [
            '<div class="share-block clearfix">',
                '<div class="dialog-tit clearfix">',
                    '<span class="span-left">写评价</span>',
                '</div>',
                '<div class="comment-panel lh10">',
                    '<table class="comment-table">',
                        '<tr class="comm-tr-two">',
                            '<td class="left">服务星级：</td>',
                            '<td>',
                                '<span class="comm-right clearfix" id="comment-star"></span>',
                            '</td>',
                            '<td>',
                                '<span class="share clearfix"></span>',
                            '</td>',
                        '</tr>',
                        '<tr class="comm-tr-four">',
                            '<td class="left" valign="top">评价内容：</td>',
                            '<td colspan="2"><textarea id="comment-content" class="textarea"></textarea></td>',
                        '</tr>',
                        '<tr class="comm-tr-five">',
                            '<td></td>',
                            '<td colspan="2">',
                                '<span class="tips">评价内容限制250个字符，还可以输入<b>250</b>个字符</span>',
                            '</td>',
                        '</tr>',
                    '</table>',
                '</div>',
            '</div>'
        ].join('');

        $.dialog({
            title: '写评价',
            content: tpl,
            lock: true,
            okValue: '提交评价',
            ok: function(){
                saveComment(this);
                return false;
            },
            initialize: function(){
                star.init({
                    container : '#comment-star',
                    hiddenName : 'starScore'
                });
                util.wordsCount('#comment-content', '.comm-tr-five .tips b', 250);
            },
            cancel: function(){}
        });
        e.preventDefault();
    });

    $('#consult').on('click', function(e){
        bindChatBtn($(this));
        e.preventDefault();
    });

    $('#slide').switchable({
        triggers: '&bull;',
        putTriggers: 'insertAfter',
        panels: 'li',
        easing: 'ease-in-out',
        effect: 'scrollLeft',
        end2end: true,
        autoplay: true
    });

    if ($(".slide-list").find("li").length == 1) {
        $(".switchable-triggers").hide();
    };

    /**
     * 定义评价易列表构造函数
     */
    var CommentList = function(){
        this.init();
    };

    CommentList.prototype = {
        /**
         * 初始化评价列表
         */
        init : function(){
            var self = this,
                getType = util.location().type;

            if(getType){
                self.tabList(getType);
            }else{
                self.type = 0;
            }
            self.level = $('.tab-menu-btn').eq(self.type).attr('data-level');
            self.getCommentCount(0, function(json){
                if(!json.error && json.totalRecords > 0){
                    util.tab($(".tab-menu-btn"), "current", "", function(i){
                        self.type = i;
                        self.level = $('.tab-menu-btn').eq(self.type).attr('data-level');
                        $('.tab-table').hide().eq(i).show();
                        self.getCommentCount(0);
                    });
                }
            });
        },
        /**
         * 切换到规定的tablist上
         */
        tabList : function(t){
            var self = this;
            self.type = parseInt(t);
            $('.tab-table').hide().eq(t).show();
            $(".tab-menu-btn").removeClass('current').eq(t).addClass('current');
        },
        /**
         * ajax获取评价数据总数
         */
        getCommentCount : function(index, callback){
            var self = this;
            self.page = index || 0;
            self.pageSize = 10;
            self.serviceId = $('#servicesId').val();
            self._loading('show');
            util.setAjax(inter.getApiUrl().getCommentListUrl, {
                'serviceId' : self.serviceId,
                'level': self.level,
                'type': 'service',
                'pageIndex': self.page + 1,
                'pageSize': self.pageSize
            }, function(json){
                if(json.error){
                    $.alert(json.error);
                }else{
                    self.totalRecords = json.totalRecords;
                    self.comments = json.comments;
                    self.bindPagination();
                }
                if(callback)callback(json);
            },function(){
                self._loading('hide');
                $.alert('服务器繁忙，请稍后再试。');
            });
        },
        /**
         * ajax获取评价列表数据
         */
        getCommentList : function(){
            var self = this;

            if(!self.comments){
                if(!self.loading){
                    self._loading('show');
                }
                util.setAjax(inter.getApiUrl().getCommentListUrl, {
                    'serviceId' : self.serviceId,
                    'level': self.level,
                    'type': 'service',
                    'pageIndex': self.page + 1,
                    'pageSize': self.pageSize
                }, function(json){
                    if(json.error){
                        self._loading('hide');
                        $.alert(json.error);
                    }else{
                        self.totalRecords = json.totalRecords;
                        self.comments = json.comments;
                        self._render();
                    }
                },function(){
                    self._loading('hide');
                    $.alert('服务器繁忙，请稍后再试。');
                });
            }else{
                self._render();
            }
        },
        /**
         * 载入状态
         */
        _loading : function(){
            var self = this,
                loading = $('.tab-loading'),
                tab = loading.closest('.tab'),
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
         * 渲染数据列表
         */
        _render : function(){
            var self = this,
                listBox = $('.comment-list');

            listBox.html(self.render(self.comments));
            self.comments = null;
            self._loading('hide');
        },
        /**
         * 渲染数据列表辅助方法
         */
        render : function(list){
            var self = this,

                tradeTpl = [
                    '<div class="comment clearfix">',
                        '<div class="comment-left">',
                            '<div class="comment-head">',
                                '<a href="/expert/detail/#{uid}" target="_blank">',
                                    '<img src="#{headUrl}" height="190" width="190" alt="#{username}">',
                                '</a>',
                            '</div>',
                            '<div class="dev-name">',
                                '<a href="/expert/detail/#{uid}" target="_blank" title="#{username}">#{username}</a>',
                                ' #{job}',
                            '</div>',
                        '</div>',
                        '<div class="comment-right">',
                            '<p class="comment-tit clearfix">',
                                '<span class="comment-rank">',
                                    '<span class="icon icon-star-base"><span class="icon icon-star-on-red" style="width:#{level}%;"></span></span>',
                                '</span>',
                                '<span class="comment-date">#{commentTime}</span>',
                            '</p>',
                            '<p class="comment-cont">',
                                '#{content}',
                                 '<a href="/comment/detail/#{id}" target="_blank" class="cont-more">更多</a>',
                            '</p>',
                        '</div>',
                    '</div>'
                ].join(''),
                returnStr = [];

            if(list && list.length>0){
                $.each(list, function(i, n){
                    n.level = n.level/5*100;
                    returnStr.push(util.template(tradeTpl, n));
                });
            }else{
                returnStr.push('<div class="no-comment"><em class="icon icon-no-comment"></em><br>对不起，暂时没有任何人评价！</div>');
            }

            return returnStr.join('');
        },
        /**
         * 绑定分页按钮
         */
        bindPagination : function(){
            var self = this;
            $('.page').pagination(self.totalRecords, {
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
                    self.getCommentList();
                }
            })
        }
    };
    var comment = new CommentList();
});
