/**
 * @description: 需求首页
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/util',
    'common/interface',
    'module/pageCommon'
],function(util, inter, common){

    var $loadBtnBox = $(".J-load-wrapper")
    	pageCount = 2;

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

    var renderList = function(json){
        var htmlTmp = ['<div class="search-block clearfix">',
            '<div class="reqSer-info">',
                '<div class="reqSer-title">',
                '<a href="/require/detail/#{id}" target="_blank">#{title}</a></div>',
                '<div class="reqSer-introduction">#{info}</div>',
                '<div class="reqSer-user clearfix">',
                    '<a href="/expert/detail/#{ownerUserId}" class="user-head" target="_blank">',
                        '<em></em>',
                        '<img src="#{headUrl}" width="41" height="41">',
                    '</a>',
                    '<div class="user-name"><a href="/expert/detail/#{ownerUserId}" target="_blank">#{ownerUserName}</a></div>',
                    '<div class="user-job">#{job}</div>',
                    '<div class="user-country flag #{countryUrl}" title="#{countryUrl}"></div>#{gender}',
                '</div>',
            '</div>',
            '<div class="reqSer-right">',
                '<div class="reqSer-price">#{price}</div>',
                '<div class="reqSer-date">#{createDate} 发布</div>',
            '</div>',
        '</div>'].join("");

        var renderTemp = "";

        $.each(json, function(i, e){
            
            var sex = "";
            if (e.gender == "MAN") {
                sex = '<img alt="男" title="男" src="'+ ued_conf.root +'/images/male.png" class="user-gender" width="13" height="13">';
            }else if(e.gender == "WOMAN"){
                sex = '<img alt="女" title="女" src="'+ ued_conf.root +'/images/female.png" class="user-gender" width="13" height="13">';
            };


            var data = {
				country: e.country,
				countryUrl: e.countryUrl,
				gender: sex,
				headUrl: e.headUrl,
				id: e.id,
				info: e.info,
				job: e.job,
				ownerUserId: e.ownerUserId,
				ownerUserName: e.ownerUserName,
				price: e.budget == 0 ? '免费' : (!e.budget ? '面议' : ('￥' + util.toFixed(e.budget ,1))),
				title: e.title,
				createDate: e.createDate
            }
            renderTemp = renderTemp + util.template(htmlTmp, data);

        });
        
        //将视图添加到页面
        var $itemObj = $(renderTemp);
        $loadBtnBox.before($itemObj);
        addEllipsis($itemObj);

        if(json.length == 10){
            $loadBtnBox.show();
        }else{
            $loadBtnBox.hide();
        }

    }

    var handleLoadBtnBox = function(){
        var cat = $("#currentCatId").val();

        util.setAjax(inter.getApiUrl().requireHomeList,
            { page: 1, pageSize: 10, type:"json", categoryId: cat }, 
            function(json){
                if (json.status) {
                    total = json.totalRowCount;
                    if (total > 10) {
                        $loadBtnBox.show();
                    }else{
                        $loadBtnBox.hide();
                    };
                };
            },
            function(json){
                $.alert(json.error);
            }, 'GET');
    }
    common.searchTypeInstance.setActive(common.searchData.requireData);
    addEllipsis($(".list-block"), function(){
        $(".list-block").css({"visibility":"visible"});
    });
    common.initLogin();
    handleLoadBtnBox();

    $(".load-more-btn").on("click", function(){
        var cat = $("#currentCatId").val();

        util.setAjax(inter.getApiUrl().requireHomeList,
            { page: pageCount++, pageSize: 10, type:"json", categoryId: cat }, 
            function(json){
                if (json.status) {
                    renderList(json.list);
                };
            },
            function(json){
                $.alert(json.error);
            }, 'GET');
    });


});
