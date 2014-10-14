/**
 * @description: 需求详细
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'module/pageCommon',
    'common/util',
    'module/imTips',
],function(common, util, imTips){
    var api = null,
        tip = null;
        
    common.initLogin();
    common.searchTypeInstance.setActive(common.searchData.requireData);
    /**
     * 联系按钮事件
     */
    function bindChatBtn($this){
        var self = this,
            chatId = $this.attr('data-id')|| 0,
            youData = {
                userId: chatId,
                userName: $('.posted-name').text(),
                avatar: $('.posted-head img').attr('src')
            };

        imTips.fireChat(youData);
    }

    $('#consult').on('click', function(e){
        bindChatBtn($(this));
        e.preventDefault();
    });

});
