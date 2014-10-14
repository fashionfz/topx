/**
 * @description: 群组首页
 * @author: young foo(young.foo@helome.com)
 * @update: 
 */
require([
    'common/util',
    'common/interface',
    'module/group',
    'module/pageCommon'
], function(util, inter, group, common){

    var $loadBtnBox = $(".J-load-wrapper"),
        $loadBtn = $("#load-more-btn"),
        memberListPage = 2;

    var renderList = function(json){
        var htmlTmp = ['<div class="group-member clearfix">',
                            '<a href="/expert/detail/#{userId}" target="_blank"><img src="#{mediumHeadUrl}" /></a>',
                            '<div class="group-info">',
                                '<div><a href="/expert/detail/#{userId}" target="_blank"><strong>#{userName}</strong></a>#{job}</div>',
                                '<div class="group-intro">#{personalInfo}</div>',
                            '</div>',
                        '</div>'].join("");

        var renderTemp = "";

        $.each(json, function(i, e){

            renderTemp = renderTemp + util.template(htmlTmp, e);

        });
    
        $loadBtnBox.before(renderTemp);
        if(json.length < 10){
            $loadBtnBox.hide();
        }
    }

    var handleLoadBtnBox = function(cb){
        var initListLength = $(".J-group-member-box").find(".group-member").length,
            groupId = $loadBtn.attr("group-id"),
            nextLength = 0;

        util.setAjax(
            inter.getApiUrl().getGroupDetailList,
            { p: 2, groupId: groupId },
            function(json){
                if (json.status) {
                    nextLength = json.list.length;

                    if (initListLength < 10) {
                        $loadBtnBox.hide();
                    }else if(initListLength == 10 && nextLength == 0){
                        $loadBtnBox.hide();
                    };

                };
                if (cb) { cb() };
            },
            function(json){
                $.alert(json.error);
            },
            "GET");
    }

    var showBanner = function(){
        if(!(window.PeerConnection || window.webkitPeerConnection00 || window.webkitRTCPeerConnection || window.mozRTCPeerConnection) || !window.WebSocket){
            $("#banner-img").hide();
            $("#banner-img-ie").show();
            $( document ).ready(function(){
                var $banner = $("#banner-img-ie"),
                    imgHeight = $banner.height(),
                    imgWidth = $banner.width(),
                    wrapperHeight = 180,
                    wrapperWidth = imgWidth;

                if (imgHeight/imgWidth > wrapperHeight/wrapperWidth) {
                    $banner.css({
                        "top": -imgHeight/2,
                        "margin-top": "90px"
                    });
                }else{
                    $banner.css({
                        "height": "180px",
                        "width": "auto",
                        "left": -(180 * imgWidth / imgHeight - imgWidth) / 2,
                    });
                };
            });
        }
    }

    //滚动window的时候 固定用户信息

    var fixLeftTop = function(){
        var leftHeight = $(".group-left").outerHeight(),
            rightHeight = $(".group-right").outerHeight();

        // if (leftHeight > rightHeight) {
        //     $(".group-right").height(leftHeight + 50);
        // };
        
        $(window).scrollTop(0);
        $(window).scroll(function(){
            var st = $(window).scrollTop(),
                height = 180,
                top = st - height;

            if (leftHeight >= rightHeight) {
                $(".group-left").css({ "position": "relative"});
                return;
            };

            if ((rightHeight - leftHeight) < top) {
                $(".group-left").css({ "position": "relative"});
                $(".group-left").css({ "top": rightHeight - leftHeight });
                return;
            };

            if (st > height) {
                $(".group-left").css({ "position": "absolute"});
                $(".group-left").css({ "top": top });
            }else{
                $(".group-left").css({ "position": "absolute"});
                $(".group-left").css({ "top": "0px" });
            };
            
        });
    }

    common.initLogin();
    common.searchTypeInstance.setActive(common.searchData.groupData);
    handleLoadBtnBox(function(){
        fixLeftTop();
    });
    showBanner();
    

    $(".btn-join").on("click", function(e){
        group.bindJoinEvent($(this));
        e.preventDefault();
    });
    
    $(".btn-contact").on("click", function(e){
        group.bindChatBtn($(this));
        e.preventDefault();
    });

    $loadBtn.on("click", function(){
        var groupId = $("#load-more-btn").attr("group-id");

        util.setAjax(
            inter.getApiUrl().getGroupDetailList,
            { p: memberListPage++, groupId: groupId },
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
