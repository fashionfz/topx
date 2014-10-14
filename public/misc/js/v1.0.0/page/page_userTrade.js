/**
 * @description: 历史记录
 * @author: YoungFoo(young.foo@helome.com)
 * @update: TinTao(tintao.li@helome.com)
 */
require([
    'module/pageCommon',
    'module/messageList',
    'module/messageListSearch'
], function(common, msgList,msgSearch){
    var $searchBox = $('.search-box')
    common.initLogin();
    msgList.init();
    $('#chatShow').on('click',function(){
        $searchBox.addClass('none');
//        msgList.init();
    });
    $('#chatSearch').on('click',function(){
        $searchBox.removeClass('none');
        $searchBox.find('.search-left-body').addClass('none');
        $('.tab-table').html('');
        msgSearch.init();
    });

});
