define('module/userPageDetail', [
    'common/interface',
    'common/util',
    'module/imTips'
], function(inter, util, imTips){

    /**
     * 获取当前用户与另一指定用户的联系状态
     */
    var getChatStatus = function(json, sCall, eCall){
        if($.isEmptyObject(isOpenChat)){
            util.setAjax(util.strFormat(inter.getApiUrl().getSelfInChatUrl, [json.otherUid, json.selfUid]), {}, function(data){
                data.status ? eCall && eCall() : sCall && sCall();
            }, null, 'GET')
        }else{var chatStatus = isOpenChat[json.selfUid + 'to' + json.otherUid];
            chatStatus && chatStatus.self ? eCall && eCall() : sCall && sCall();
        }
    }

    /**
     * 联系按钮事件
     */
    $('#consult').on('click', function(e){
        var chatId = $(this).attr('data-id')|| 0,
            youData = {
                userId: chatId,
                userName: $('.name').text(),
                avatar: $('.search-head img').prop('src')
            };

        imTips.fireChat(youData);
        e.preventDefault();
    });

    /** @ Ta **/
    $('#content .btn-at').off('click').on('click',function(){
        var id = $(this).attr('data-id');

        $.dialog({
            id: 'Confirm',
            fixed: true,
            lock: true,
            content: [
                '<div class="dialog-panel">',
                '<div class="dialog-tit clearfix">',
                '<span class="span-left">@圈Ta</span>',
                '</div>',
                '<table class="dialog-table">',
                '<tr>',
                    '<td class="d-content lh10">',
                        '<h3>需要通过对方验证才能将其加入您的圈，对方通过后</h3><h3>您也将进入Ta的圈。</h3>',
                        '<p class="mt20">邀请消息:</p>',
                        '<textarea id="atContentTxt" class="textarea mt10" maxlength="60" placeholder="最多60个字符"></textarea>',
                        '<div class="mt10 tips">内容限制60个字符，还可以输入<b>60</b>个字符</div>',
                    '</td>',
                '</tr>',
                '</table>',
                '</div>'
            ].join(''),
            okValue:  '确定',
            ok:function(){
                util.setAjax(
                    inter.getApiUrl().inviteFriends,
                    {
                        recevierUserId:id,
                        content:$('#atContentTxt').val()
                    },
                    function(json){
                        if(json.status == 0){
                            $.alert(json.error,null,null,{title: '@圈Ta', icon: true});
                        }else{
                            $.alert('邀请信息已发送成功！请耐心<br>等待对方通过',null,null,{title: '@圈Ta', icon: false});
                        }

                        //$('#content .btn-at').addClass('btn-ated').removeClass('btn-at').off('click').find('i').after('已');
                    },
                    function(){
                        $.alert('系统繁忙，请稍后重试!', null, null, {title: '@圈Ta', icon: false});
                    }
                )
            },
            cancelValue: '取消',
            cancel: function(){
                this.close()
            },
            initialize: function(){
                util.wordsCount('#atContentTxt', '.d-content .tips b', 60);
            }
        });

    });

    //滚动window的时候 固定用户信息
    var fixLeftTop = function(){
        var leftHeight = $(".search-left").outerHeight(),
            rightHeight = $(".doc-list").outerHeight();

        if (leftHeight > rightHeight) {
            $(".developer-detail").height(leftHeight + 50);
        };
        
        $(window).scrollTop(0);
        $(window).scroll(function(){
            var st = $(window).scrollTop(),
                height = 0,
                top = st - height;

            if ((rightHeight - leftHeight) < top) {
                $(".search-left").css({ "position": "relative"});
                $(".search-left").css({ "top": rightHeight - leftHeight });
                return;
            };

            if (st > height) {
                $(".search-left").css({ "position": "absolute"});
                $(".search-left").css({ "top": top });
            }else{
                $(".search-left").css({ "position": "absolute"});
                $(".search-left").css({ "top": "0px" });
            };
            
        });
    }

    fixLeftTop();

});