/**
 * @description: 邀请
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon'
], function (inter, util, common) {
    common.initLogin();

    function copyUrl(){
        var clip = new ZeroClipboard.Client();
        clip.setHandCursor( true );
        clip.addEventListener( "mouseUp", function(client) {
            clip.setText($('#invitedUrl').text());
            $.alert("复制邀请链接成功！");
        });
        clip.glue("copyInvited");
    }
    copyUrl();
});
