/**
 * @description: 群组搜索结果
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 */

require([
    'common/util',
    'common/interface',
    'module/pageCommon',
    'module/searchList',
    'module/filter',
    'module/group'
],function(util, inter, common, searchList, filter, group){

    common.initLogin();
    common.searchTypeInstance.setActive(common.searchData.groupData);
    filter.init();

    //search list object
    var sl = new searchList.SearchList();
        /**
         * 点击联系按钮事件
         */

    var searchTpl = ['<div class="search-block clearfix">',
                '<a href="/group/detail/#{id}" target="_blank" class="search-head"><img src="#{headUrl}" height="190" width="190" class="search-head-img" /></a>',
                '<div class="search-center">',
                    '<div class="name clearfix">#{lockHTML}',
                        '<a href="/group/detail/#{id}" target="_blank" title="">#{groupName}</a>',
                    '</div>',
                    '<div class="info">',
                        '<span>分类：#{industryName}</span>',
                        '<span>成员数：#{countMem} 人</span>',
                        '<span>创建时间：#{createDate}</span>',
                    '</div>',
                    '<p class="introduce">#{groupInfo}</p>',
                    '<div class="skill clearfix">#{tags}</div>',
                '</div>',
                '<div class="search-right">',
                    '<div class="btn-block">#{btnHTML}</div>',
                '</div>',
            '</div>'
        ].join('');
    /**
     * 渲染页面
     */
    var render = function(data, sucCall){
        var aSearch = [],
            list = data.list;
        
        if(sucCall){
            sucCall({
                pageSize : data.pageSize,
                totalRowCount : data.totalRowCount,
                pageIndex : data.pageIndex
            });
        }
        if(list.length>0){
            sl.bLoad = true;
            for(var i in list){
                var o = list[i];

                var tagsHTML = "";
                $.each(o.tags, function(m, n){
                    tagsHTML = tagsHTML + '<a href="/searchgroup?ft='+n.noMarkedTag+'">'+ n.tag +'</a>'
                });

                var lockHTML = "";
                if (o.groupPrivId) {
                    lockHTML = '<span class="icon icon-group-lock" title="需要申请加入"></span>';
                };

                var btnHTML = "";
                if (o.isJoin) {
                    btnHTML = '<span class="btn-default btn-green btn-lg btn-contact" group-id="'+ o.id +'" group-name="'+ o.groupName +'" group-islock="'+ o.groupPrivId +'"><i class="index-icon icon-btn-chat"></i>群 聊</span>';
                }else{
                    btnHTML = '<span class="btn-default btn-green btn-lg btn-join" group-id="'+ o.id +'" group-name="'+ o.groupName +'" group-islock="'+ o.groupPrivId +'"><i class="icon icon-btn-add-group"></i>加 入</span>';
                };

                aSearch.push(util.template(searchTpl, {
                    id : o.id,
                    headUrl: o.headUrl,
                    groupName: o.groupName,
                    industryName: o.industryName,
                    countMem: o.countMem,
                    createDate: o.createDate,
                    groupInfo: o.groupInfo,
                    groupPrivId: o.groupPrivId,
                    tags: tagsHTML,
                    lockHTML: lockHTML,
                    btnHTML: btnHTML
                }));
            }
            var searchListStr = $(aSearch.join(''));
            $('.search-loading').before(searchListStr);

            searchListStr.find(".btn-join").on('click', function(e){
                group.bindJoinEvent($(this));
                e.preventDefault();
            });

            searchListStr.find(".btn-contact").on('click', function(e){
                group.bindChatBtn($(this));
                e.preventDefault();
            });
            
            sl.pageIndex = sl.pageIndex + 1;
            if( sl.pageIndex%4 == 0){
                sl.pageEnd = true;
                sl.bLoad = false;
                sl.getPageLink();
            }
        }else{
            sl.pageEnd = true;
            sl.bLoad = false;
            sl.getPageLink();
            $('.search-loading').before('<div class="noMore">没有更多结果了</div>');
        }
    };

    sl.scrollLoad({
        "url": "/searchgroup",
        "btnBind": function(){
            $(".btn-join").on("click", function(e){
                group.bindJoinEvent($(this));
                e.preventDefault();
            });
            
            $(".btn-contact").on("click", function(e){
                group.bindChatBtn($(this));
                e.preventDefault();
            });
        },
        "render": render
    });

});