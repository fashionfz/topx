/**
 * @description: 群组首页
 * @author: young foo(young.foo@helome.com)
 * @update: 
 */
require([
    'common/util',
    'common/interface',
    'module/pageCommon',
    'module/group',
], function(util, inter, common, group){

    var $loadBtnBox = $(".J-load-wrapper"),
        memberListPage = 2;

    var addEllipsis = function($items, cb){
        var $itemInfo = $items.find(".introduce");

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
            '<a href="/group/detail/#{id}" target="_blank" class="search-head">',
                '<img src="#{headUrl}" height="190" width="190" class="search-head-img" />',
            '</a>',

            '<div class="search-center">',
                '<div class="name clearfix">',
                    '#{groupPriv}',
                    '<a href="/group/detail/#{id}" target="_blank" title="">#{groupName}</a>',
                '</div>',

                '<div class="info">',
                    '<span>分类：#{industryName}</span>',
                    '<span>成员数：#{countMem}人</span>',
                    '<span>创建时间：#{createDate}</span>',
                '</div>',

                '<p class="introduce">#{groupInfo}</p>',
                '<div class="skill clearfix">#{tags}</div>',
            '</div>',
            '<div class="search-right">',
                '<div class="btn-block">#{isJoin}</div>',
            '</div>',
        '</div>'].join("");

        var renderTemp = "";

        $.each(json, function(i, e){
            var tagHTML = "";

            $.each(e.tags, function(e){
                tagHTML = tagHTML + '<span>' + e + '</span>';
            });

            var privateHtml = '<span class="icon icon-group-lock" title="需要申请加入"></span>';
            var isLock = true;
            if (e.groupPriv == "public") {
                privateHtml = "";
                isLock = false;
            };

            var isJoinBtnHtml = '<span class="btn-default btn-green btn-lg btn-join" group-id="' + e.id + '" group-name="' + e.groupName + '" group-islock="'+ isLock +'"><i class="icon icon-btn-add-group"></i>加 入</span>';

            if (e.isJoin) {
                isJoinBtnHtml = '<span class="btn-default btn-green btn-lg btn-contact" group-id="' + e.id + '" group-name="' + e.groupName + '" group-islock="'+ isLock +'"><i class="index-icon icon-btn-chat"></i>联 系</span>';
            };

            var data = {
                groupName: e.groupName,
                countMem: e.countMem,
                createDate: e.createDateFormat,
                headUrl: e.headUrl,
                industryName: e.industryName,
                groupInfo: e.groupInfo,
                id: e.id,
                tags: tagHTML,
                groupPriv: privateHtml,
                isJoin: isJoinBtnHtml
            }
            renderTemp = renderTemp + util.template(htmlTmp, data);

        });
        
        //将视图添加到页面
        var $itemObj = $(renderTemp);
        $loadBtnBox.before($itemObj);
        addEllipsis($itemObj);
        if(json.length < 10){
            $loadBtnBox.hide();
        }

        //绑定chat按钮
        var $contactBtn = $itemObj.find(".btn-contact");
        $contactBtn.on("click", function(e){
            group.bindChatBtn($(this));
            e.preventDefault();
        });

        //bind join btn
        var $joinBtn = $itemObj.find(".btn-join");
        $joinBtn.on("click", function(e){
            group.bindJoinEvent($(this));
            e.preventDefault();
        });
    }

    var handleLoadBtnBox = function(){
        var initListLength = $(".group-right").find(".search-block").length,
            nextLength = 0,
            inCode = parseInt(util.location().ind);

        util.setAjax(
            inter.getApiUrl().getGroupHomeList,
            { p: 2, ind: inCode },
            function(json){
                if (json.status) {
                    nextLength = json.list.length;

                    if (initListLength < 10) {
                        $loadBtnBox.hide();
                    }else if(initListLength == 10 && nextLength == 0){
                        $loadBtnBox.hide();
                    };

                };
            },
            function(json){
                $.alert(json.error);
            },
            "GET");
    }
    
    common.searchTypeInstance.setActive(common.searchData.groupData);
    addEllipsis($(".list-block"), function(){
        $(".list-block").css({"visibility":"visible"});
    });
    common.initLogin();
    handleLoadBtnBox();

    $(".btn-join").on("click", function(){
        group.bindJoinEvent($(this));
    });
    
    $(".btn-contact").on("click", function(){
        group.bindChatBtn($(this));
    });

    $(".load-more-btn").on("click", function(){
        var inCode = parseInt(util.location().ind);

        util.setAjax(
            inter.getApiUrl().getGroupHomeList,
            { p: memberListPage++, ind: inCode },
            function(json){
                if (json.status) {
                    renderList(json.list);
                };
            },
            function(json){
                $.alert(json.error);
            },
            "GET");

    });



});
