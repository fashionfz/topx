/**
 * @description: 用户详情中的require tab
 * @author: Young Foo(young.foo@helome.com)
 * @update:
 */
require([
    'module/pageCommon',
    'common/util',
    'common/interface',
    'module/userPageDetail'
],function(common, util, inter, userPageDetail){

    common.initLogin();

    var GetRequireData = function(){
        var page = 0;
        var pageSize = 10;
        var totalCount = 0;
        var requireListTmp = ['<li class="require-item clearfix">',
                        '<div class="require-index-box">',
                            '<div class="require-index icon">#{index}</div>',
                        '</div>',
                        '<a class="require-title" href="/require/detail/#{id}" target="_blank">#{title}</a>',
                        '<ul class="require-item-info">',
                            '<li class="require-price">#{budget}</li>',
                            '<li class="require-date">#{createDate}发布</li>',
                        '</ul>',
                    '</li>'].join('');

        var bindPagination = function(){
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
                    getRequireList();
                }
            });
        };

        this.getTotalCount = function(){
            var id = $(".search-left").attr("id");
            util.setAjax(
                inter.getApiUrl().getDeveloperRequireList,
                { "userId":id, "page": page, "pageSize": pageSize},
                function(json){
                    if (json.status) {
                        totalCount = json.totalRowCount;
                        bindPagination();
                    }else{
                        $.alert(json.error);
                    };
                },
                function(json){
                    $.alert('服务器繁忙，请稍后再试。');
                },
                "GET");
        };

        var getRequireList = function(){
            var id = $.trim($(".search-left").attr("id")),
                currentId = $.trim($("#currentId").val()),
                noMsgTmp = '<div class="no-msg"><em class="icon icon-no-msg"></em><br/>对方还没有任何需求发布！<br/></div>';

            if (id == currentId) {
                noMsgTmp = '<div class="no-msg"><em class="icon icon-no-msg"></em><br/>您还没有任何需求发布！<br/><a class="btn-default btn-lg btn-white mt10" href="/user/require/write" target="_blank"><em class="icon add-require"></em>发布需求</a></div>';
            };
            
            util.setAjax(
                inter.getApiUrl().getDeveloperRequireList,
                { "userId":id, "page": page, "pageSize": pageSize},
                function(json){
                    if (json.status) {
                        var requireListStr = "";
                        if(json.list && json.list.length){
                            $.each(json.list, function(i, e){
                                var item = json.list[i];

                                var data = {
                                    budget:         item.budget == 0 ? '免费' : (!item.budget ? '面议' : ('￥' + util.toFixed(item.budget ,1))),
                                    createDate:     item.createDate,
                                    title:          item.title,
                                    id:             item.id,
                                    index:          item.index
                                };

                                requireListStr = requireListStr + util.template(requireListTmp, data);
                            });
                        }else{
                            requireListStr = noMsgTmp;
                        }
                        $(".require-block").empty().append(requireListStr);
                    }else{
                        $.alert(json.error);
                    }
                },
                function(json){
                    $.alert('服务器繁忙，请稍后再试。');
                },
                "GET");
        }
    }

    var require = new GetRequireData();
    require.getTotalCount();

});