/**
 * @description: 用户详情中的service tab
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

    var GetServiceData = function(){
        var page = 0;
        var pageSize = 9;
        var totalCount = 0;
        var serviceListTmp = ['<li class="service-item" title="#{title}"><a href="/services/detail/#{id}" target="_blank">',
                '<img src="#{coverUrl}">',
                '<div class="service-main">',
                    '<div class="service-price">#{price}</div>',
                    '<div class="service-intro">#{title}</div>',
                    '<div class="service-star">',
                        '<span class="icon icon-star-base" title="#{averageScore}分">',
                            '<span class="icon icon-star-on" style="width:#{score}px;"></span>',
                        '</span>',
                        '<span class="star-num">(#{commentNum})</span>',
                    '</div>',
                '</div>',
            '</a></li>'].join('');

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
                    getServiceList();
                }
            });
        };

        this.getTotalCount = function(){
            var id = $(".search-left").attr("id");
            util.setAjax(
                inter.getApiUrl().getDeveloperServiceList,
                { "userId":id, "page": page, "pageSize": pageSize},
                function(json){
                    if (json.error) {
                        $.alert(json.error);
                    }else{
                        totalCount = json.totalRowCount;
                        bindPagination();
                    };
                },
                function(json){
                    $.alert('服务器繁忙，请稍后再试。');
                },
                "GET");
        };

        var getServiceList = function(){
            var id = $.trim($(".search-left").attr("id")),
                currentId = $.trim($("#currentId").val()),
                noMsgTmp = '<div class="no-msg"><em class="icon icon-no-msg"></em><br/>对方还没有任何服务发布！<br/></div>';
            
            if (id == currentId) {
                noMsgTmp = '<div class="no-msg"><em class="icon icon-no-msg"></em><br/>您还没有任何服务发布！<br/><a class="btn-default btn-lg btn-white mt10" href="/user/service/write" target="_blank"><em class="icon add-service"></em>发布服务</a></div>';
            };

            util.setAjax(
                inter.getApiUrl().getDeveloperServiceList,
                { "userId":id, "page": page, "pageSize": pageSize},
                function(json){
                    if (json.status) {
                        var serviceListStr = "";
                        if(json.list && json.list.length){
                            $.each(json.list, function(i, e){
                                var item = json.list[i];

                                var score = (item.averageScore/5*73).toString();
                                var data = {
                                    coverUrl:       item.coverUrl,
                                    price:          item.price == 0 ? '免费' : (!item.price ? '面议' : ('￥' + util.toFixed(item.price ,1))),
                                    averageScore:   item.averageScore.toString(),
                                    score:          score,
                                    title:          item.title,
                                    id:             item.id,
                                    commentNum:     item.commentNum
                                };

                                serviceListStr = serviceListStr + util.template(serviceListTmp, data);
                            });
                        }else{
                            serviceListStr = noMsgTmp;
                        }
                        $(".service-block").empty().append(serviceListStr);
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

    var service = new GetServiceData();
    service.getTotalCount();

    
});