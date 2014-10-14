/**
 * @description: 在线客服
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */

require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/goTop',
    'module/imTips'
],function(inter, util, common, gotop, imTips){
    var tip = null;
    /**
     * 获取当前用户与另一指定用户的联系状态
     */
    function getChatStatus(json, sCall, eCall){
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
    function bindChatBtn($this){
        var chatId = $this.attr('data-id')|| 0,
            item = $this.closest('.search-block'),
            youData = {
                userId: chatId,
                userName: item.find('.name a:first').text(),
                avatar: item.find('.search-head img').prop('src')
            };

        imTips.fireChat(youData);
    }
    $('.btn-consult').on('click', function(e){
        bindChatBtn($(this));
        e.preventDefault();
    });
    common.initLogin();
    gotop.init();
});