/**
 * @description: 搜索列表模块
 * @author: Zhang Guanglin
 * @update: Young Foo
 */
define('module/searchList', [
    'common/util',
    'module/cookie',
    'module/favorite'
], function(util, cookie, favorite){

    var SearchList = function(){
        var loadingTpl = '<div class="search-loading"><span class="loader"></span></div>';

        var pageBottomTpl = [
                '<div class="page page-bottom">',
                '#{pageLink}',
                '</div>'
            ].join('');

        this.bLoad = true;
        this.pageEnd = false;

        /**
         * 将瀑布流绑定到scroll
         */
        this.scrollLoad = function(obj){
            var self = this,
                keyword = $('#ft').val(),
                location = util.location(),
                nowList = $('.search-list .search-block').length;

            $(window).scrollTop(0);

            if (obj.btnBind) {obj.btnBind()};

            this.initPage = parseInt(location.p) || 1;
            this.pageIndex = 2;
            this.pageParams = {
                pageIndex : this.pageIndex,
                pageCount : null,
                pageSize : null,
                pageUrl : util.location({'p': '__id__|1', 'ft': keyword})
            };

            var curPage = (parseInt($('#curPage').val())||0) + 1,
                totalCount = parseInt($('#totalCount').val())||0;

            this.pageParams = {
                pageIndex : curPage,
                pageCount : totalCount,
                pageSize : 10,
                pageUrl : util.location({'p': '__id__|1', 'ft': keyword})
            };

            this.getPageLink();

            $(window).on('scroll',function(){

                if(self.bLoad && $(window).scrollTop() + $(window).height() >= $(document).height()-100 ){
                    var load = $('.search-loading'),
                        keyword = $('#ft').val();
                    
                    self.bLoad = false;
                    self.data = util.location({'p': self.initPage + '|' + self.pageIndex, 'type': 'json', 'ft': keyword});
                    self.data = self.data.replace(/^\?*/g, '');
                    if(load.length<1){
                        load = $(loadingTpl);
                        $('.search-list').append(load);
                    }
                    load.show();
                    util.setAjax(obj.url, self.data, function(josn){
                        load.hide();
                        if (obj.render) {
                            obj.render(josn, function(pageParams){
                                self.pageParams.pageSize = pageParams.pageSize;
                                self.pageParams.pageIndex = pageParams.pageIndex;
                                self.pageParams.pageCount = pageParams.totalRowCount;
                            });
                        };
                    },function(){
                        self.pageEnd = true;
                    },'GET');
                }

            });
        }

        /**
         * 初始化分页
         */
        this.getPageLink = function(){
        	var pageSize = this.pageParams.pageSize || 10,
    			pageCount = this.pageParams.pageCount,
    			pageUrl = this.pageParams.pageUrl,
    			totalNum = parseInt(pageCount/(pageSize*3)),
                aPage = [];
    			if(totalNum<pageCount/(pageSize*3)){
    				totalNum ++;
    			};

            aPage.push(util.template(pageBottomTpl, {
                pageLink : ''
            }));

            $('.page-bottom').remove();
            $('.search-list').after(aPage.join(''));

            if(totalNum > 1){
                $(".page").pagination(pageCount, {
                    callback: function(page_id, jq){

                    },
                    prev_text: '上一页',
                    next_text: '下一页',
                    link_to: '?' + pageUrl,
                    items_per_page: pageSize * 3,
                    num_display_entries: 4,
                    current_page: this.initPage-1,
                    num_edge_entries: 1
                });
            }
        }
    }

    return {
        SearchList: SearchList
    }
});