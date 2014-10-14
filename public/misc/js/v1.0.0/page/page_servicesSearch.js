/**
 * @description: 服务搜索结果首页
 * @author: Young Foo(young.foo@helome.com)
 * @update:
 */
require([
    'common/interface',
    'module/pageCommon',
    'common/util',
    'module/searchList',
    'module/filter'
],function(inter, common, util, searchList, filter){

    common.initLogin();
    common.searchTypeInstance.setActive(common.searchData.serviceData);
    filter.init();

    //search list object
    var sl = new searchList.SearchList();
    var searchTpl = [
        '<div class="search-block clearfix">',

            '<a href="/services/detail/#{id}" class="reqSer-head" target="_blank"><img src="#{coverUrl}"></a>',

            '<div class="reqSer-info">',
                '<div class="reqSer-title"><a href="/services/detail/#{id}" target="_blank">#{title}</a></div>',
                '<div class="reqSer-introduction">#{info}</div>',
                '<div class="reqSer-user clearfix">',
                    '<a href="/expert/detail/#{ownerUserId}" target="_blank"  class="user-head">',
                        '<em></em>',
                        '<img src="#{headUrl}" width="41" height="41">',
                    '</a>',
                    '<div class="user-name"><a href="/expert/detail/#{ownerUserId}" target="_blank">#{ownerUserName}</a></div>',
                    '<div class="user-job">#{job}</div>',
                    '<div class="user-country flag #{countryUrl}" title="#{country}"></div>',
                    '#{gender}',
                '</div>',
            '</div>',
            '<div class="reqSer-right">',
                '<div class="reqSer-price">#{price}</div>',
                '<div class="reqSer-level">',
                    '<span class="icon icon-star-base" title="#{score}分">',
                        '<span class="icon icon-star-on" style="width:#{commentStarNum}px"></span>',
                    '</span>',
                    '<span class="star-num"><a href="#{linkUrl}#comments" target="_blank">(#{commentNum})</a></span>',
                '</div>',
            '</div>',
        '</div>'
    ].join('');

    var addEllipsis = function($items, cb){
        var $itemInfo = $items.find(".reqSer-introduction");
            

        $itemInfo.each(function(i, e){

            var infoHeight = $(e).height();

            if (infoHeight > 36) {
                $(e).append("<span class='ellipsis'>...<span>");

                $(e).height(36);
            };

        });

        if (cb) { cb() };
    }

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

                var sex = "";
                if (o.gender == "MAN") {
                    sex = '<img alt="男" title="男" src="'+ ued_conf.root +'/images/male.png" class="user-gender" width="13" height="13">';
                }else if(o.gender == "WOMAN"){
                    sex = '<img alt="女" title="女" src="'+ ued_conf.root +'/images/female.png" class="user-gender" width="13" height="13">';
                };

                aSearch.push(util.template(searchTpl, {
                    commentNum: o.commentNum,
                    country: o.country,
                    countryUrl: o.countryUrl,
                    coverUrl: o.coverUrl,
                    gender: sex,
                    headUrl: o.headUrl,
                    id: o.id,
                    info: o.info,
                    job: o.job,
                    ownerUserId: o.ownerUserId,
                    ownerUserName: o.ownerUserName,
                    price: o.price == 0 ? '免费' : (!o.price ? '面议' : ('￥' + util.toFixed(o.price ,1))),
                    score: o.score,
                    title: o.title
                }));
            }
            var searchListStr = $(aSearch.join(''));
            $('.search-loading').before(searchListStr);

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
        "url": inter.getApiUrl().serviceSearch,
        "render": render
    });
    
});
