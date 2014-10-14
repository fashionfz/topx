/**
 * @description: 搜索结果
 * @author: TinTao(tintao.li@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 */

require([
    'common/util',
    'common/interface',
    'module/pageCommon',
    'module/searchList',
    'module/filter',
    'module/imTips'
],function(util, inter, common, searchList, filter, imTips){

    common.initLogin();
    filter.init();
    //search list object
    var sl = new searchList.SearchList();
        /**
         * 点击联系按钮事件
         */
    var bindChatBtn = function(obj){
        var chatId = obj.attr('data-id')|| 0,
            tip = obj.data('tip'),
            item = obj.closest('.search-block'),
            youData = {
                userId: chatId,
                userName: item.find('.name a:first').text(),
                avatar: item.find('.search-head img').prop('src')
            };

        imTips.fireChat(youData);
    }

    var searchTpl = [
            '<div class="search-block clearfix">',

                '<div class="search-head">',
                    '<a href="#{linkUrl}" target="_blank"><img src="#{headUrl}" height="190" width="190" class="search-head-img" /></a>',
                    '<div class="search-country">',
                        '<span class="country-flag flag #{country}" title="#{region}"></span>',
                        '#{sex}',
                    '</div>',
                '</div>',

                '<div class="search-center">',
                    '<div class="name clearfix">',
                        '<a href="#{linkUrl}" target="_blank">#{userName}</a>',
                        '<span class="job" title="#{jobTitle}">#{job}</span>',
                        '#{isOnline}',
                    '</div>',
                    '<p class="introduce">',
                        '#{personalInfo}',
                    '</p>',
                    '<p class="skill clearfix">#{skillsTags}</p>',
                '</div>',
                '<div class="search-right">',
                    '#{consulPanel}',
                    '<p class="stars">',
                        '<span class="icon icon-star-base" title="平均分：#{commentStarScore}">',
                            '<span class="icon icon-star-on" style="width:#{commentStarNum}px;"></span>',
                        '</span>',
                        '<a href="#{linkUrl}#comments" class="star-num" target="_blank">(#{commentNum})</a>',
                    '</p>',
                    '<p class="tariff">#{expenses}</p>',
                '</div>',
            '</div>'
        ].join(''),
        sexTpl = '<img alt="#{sex}" title="#{sex}" src="#{sexUrl}" class="country-sex" width="13" height="13">',//男male //女female
        btnTpl1 = ['<p class="consult">',
                    '<span data-id="#{userId}" class="btn-default btn-lg btn-consult btn-green">',
                        '<i class="index-icon icon-btn-chat"></i>#{btnText}',
                    '</span>',
                '</p>'].join('');
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
                $.each(o.skillsTags, function(m, n){
                    tagsHTML = tagsHTML + '<a href="/expertsearch?ft='+n.noMarkedTag+'">'+ n.tag +'</a>'
                });

                aSearch.push(util.template(searchTpl, {
                    id : o.id,
                    userId : o.userId,
                    linkUrl : o.linkUrl,
                    headUrl : o.headUrl,
                    user : o.userName + o.job,
                    userName : o.userName,
                    sex : util.template(sexTpl, {sex: o.gender == 'MAN' ? '男' : '女', sexUrl: ued_conf.root + 'images/' + (o.gender == 'MAN' ? 'male' : 'female')+'.png'}),
                    job : o.job,
                    jobTitle : o.jobNoMark,
                    country : o.countryUrl,
                    isOnline : o.isOnline ? '<em class="search-icon search-icon-online" title="在线"></em>' : '',
                    region : o.country,
                    personalInfo : o.personalInfo,
                    skillsTags : tagsHTML,
                    consulPanel : (!o.isSelf && o.userId) ? util.template(btnTpl1, {
                        url : o.consultUrl,
                        userId : o.userId,
                        btnText : '联 系',
                        isFavorite: (o.isFavorite ? 1 : 0) + "",
                        favBtnColor: o.isFavorite ? "btn-gray" : "btn-white",
                        favBtnText: o.isFavorite ? "已收藏" : "收 藏"
                    }) : '',
                    commentStarNum : o.averageScore==0 ? '0' : o.averageScore/5*73,
                    commentStarScore : o.averageScore==0 ? '0' : Math.round(o.averageScore*100)/100,
                    commentNum : o.commentNum == 0 ? '<b>暂无评价</b>' : o.commentNum,
                    //TIMEBILL, NEGOTIABLE
                    expenses : o.payType == 'TIMEBILL' ? '资费：<span>免费</span>' : '资费：<span>面议</span>'
                    /*goodComment : object.goodComment + '',
                    averageComment : object.averageComment + '',
                    poorComment : object.poorComment + ''*/
                }));
            }
            var searchListStr = $(aSearch.join(''));
            $('.search-loading').before(searchListStr);
            searchListStr.find('.btn-consult').on('click', function(e){
                bindChatBtn($(this));
                e.preventDefault();
            });

            //绑定favorite按钮
            //favorite.bindFavBtn(searchListStr.find('.btn-fav'));

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

    /**
     * 初始化洲和国家并绑定事件
     */
    // var initCountry = function(){
    //     var $continent = $('.continent'),
    //         url = location.pathname + '?',
    //         keyword = $('#ft').val(),
    //         continent,countries,str = [],
    //         getCountry = function(countries){
    //             var arr = [];
    //             $.each(countries,function(i,e){
    //                 arr.push(util.template(countryTpl, {
    //                     url : url + util.location({'cf': e=='全部'?'':e, 'ft': keyword}),
    //                     countryName : e.length>5? e.substring(0,5)+'..':e,
    //                     countryNameFull : e
    //                 }))
    //             });
    //             return arr.join('');
    //         };
    //     $.each(country,function(i,e){
    //         continent = e.continent;
    //         countries = e.country;
    //         str.push(util.template(countryAllTpl, {
    //             continentName : continent,
    //             isHide : i==0?'':'none',
    //             countryList : getCountry(countries)
    //         }));
    //     });
    //     $continent.append(str.join(''));
    //     $('.continent-name').on('mouseover',function(){
    //         var $this = $(this),
    //             $continentName = $this.siblings('.continent-name'),
    //             continent = $this.attr('data-continent'),
    //             $countries = $('.continent-country[data-continent="'+continent+'"]');
    //         $continentName.removeClass('current');
    //         $this.addClass('current');
    //         $countries.removeClass('none');
    //         $countries.siblings('.continent-country').addClass('none');
    //     });

    // }

    sl.scrollLoad({
        "url": inter.getApiUrl().searchListUrl,
        "btnBind": function(){
            $('.btn-consult').on('click', function(e){
                bindChatBtn($(this));
                e.preventDefault();
            });
        },
        "render": render
    });

    //initCountry();
});