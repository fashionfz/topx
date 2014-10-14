/**
 * Created by zhiqiang.zhou on 14-8-20.
 * 上传图片后查看
 */

define('module/showPicture',[],function(){
    var showPict = function (src) {

        var dialog = $.dialog({
            id: 'Alert',
            fixed: true,
            content: [
                '<div class="dialog-panel">',
                    '<div class="dialog-tit clearfix">',
                    '<span class="span-left">图片查看</span>',
                    '</div>',
                    '<table class="dialog-table">',
                    '<tr>',
                    '<td class="d-alert-content"><img style="max-height: 600px; max-width: 800px" src="' + src + '"></td>',
                    '</tr>',
                    '</table>',
                '</div>'
            ].join(''),
            lock: true,
            zIndex: 10000,
            initialize: function () {
                var self = this;
                $(document).one('keydown', function (e) {
                    var target = e.target,
                        nodeName = target.nodeName,
                        rinput = /^input|textarea$/i,
                        api = artDialog.focus,
                        keyCode = e.keyCode;

                    if (!api || rinput.test(nodeName) && target.type !== 'button') {
                        return;
                    }
                    if (keyCode === 13 || keyCode === 32) {
                        self.close();
                    }
                    return false;
                });
                var img = new Image();
                img.onload = function () {
                    dialog.position();
                };
                img.src = src;
            },
            beforeunload: null
        });

    };
    return {
        init : showPict
    }
})
