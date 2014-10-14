/**
 * @description: 留言模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/message', [
    'common/interface',
    'common/util'
], function(inter, util){
    var boxTpl = [
            '<div class="reset-password-main">',
                '<p class="dialog-tit"><span class="span-left">留言</span></p>',
                '<form class="reset-password-form">',
                    '<table>',
                        '<tr><td height="52" style="font-size:12px; font-weight: bold; color: #707070">留言内容</td></tr>',
                        '<tr><td><textarea name="msg" id="msg" class="textarea" style="width:330px; height: 95px;"></textarea></td></tr>',
                        '<tr><td height="30" style="font-size: 12px;">',
                        '留言内容限制200字符以内。<span class="error-msg" style="color: red; font-weight: normal;"></span>',
                        '</td></tr>',
                    '</table>',
                '</form>',
            '</div>'
        ].join('');

    return {
        init : function(){
            var self = this;
            self.leaveMessage();
        },
        /**
         * 发起留言
         * 参数： obj 按钮对象 [可选]
         */
        leaveMessage : function(obj){
            var self = this,
                btn = obj || $('.leave-message');
            btn.on('click', function(e){
                var api = $.dialog({
                    content : boxTpl,
                    lock: true,
                    okValue : '提 交',
                    ok : function(){
                        var msg = $('#msg').val();
                        if(msg.length<1){
                            $('.error-msg').html('请输入留言内容！');
                        }else{
                            $('.error-msg').html('');
                            var tips = $.tips('loading');
                            /*util.setAjax(inter.getApiUrl().leaveMessageUrl, {msg : msg}, function(josn){
                                tips.close();
                                api.content('');
                            });*/
                        }
                        return false;
                    },
                    cancelValue : '取 消',
                    cancel : function(){
                    }
                });
                e.preventDefault();
            })
        },
        /**
         * 取消关注
         * 参数： obj 按钮对象 [可选]
         */
        unAttention : function(obj){
            var self = this,
                btn = obj || $('.already-attention');
            btn.on('click', function(e){
                var nowBtn = $(this);
                //util.setAjax(inter.getApiUrl().addAttentionUrl, {}, function(josn){
                nowBtn.unbind('click');
                nowBtn.attr('class','attention').find('span').html('+ 关注Ta');
                self.addAttention(nowBtn);
                //});
                e.preventDefault();
            })
        }
    }
});