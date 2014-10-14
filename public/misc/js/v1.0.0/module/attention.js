/**
 * @description: 关注模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/attention', [
    'common/interface',
    'common/util'
], function(inter, util){

    return {
        init : function(){
            var self = this;
            self.addAttention();
            self.unAttention();
        },
        /**
         * 添加关注
         * 参数： obj 按钮对象 [可选]
         */
        addAttention : function(obj){
            var self = this,
                btn = obj || $('.attention');
            btn.on('click', function(e){
                var nowBtn = $(this);
                //util.setAjax(inter.getApiUrl().addAttentionUrl, {}, function(josn){
                nowBtn.unbind('click');
                nowBtn.attr('class','already-attention').find('span').html('取消关注');
                self.unAttention(nowBtn);
                //});
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