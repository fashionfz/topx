/**
 * @description: 静止定位模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */

define('module/fixed', [],function(){
    return {
        /**
         * 初始化模块
         */
        init : function(opt){
            var self = this,
                defaults = {
                    target : null,
                    beforeFixed : null,
                    afterFixed : null,
                    afterNoFixed : null
                };

            self.opt = $.extend(defaults, opt || {});
            var fixPanel = self.opt.target,
                panelClone = fixPanel.clone(),
                fixedTop = fixPanel.offset()? fixPanel.offset().top : 0;

            self.beforeFixed(panelClone);
            self.scroll(function(st){
                if(st > fixedTop){
                    fixPanel.after(panelClone);
                    panelClone.show();
                    if(self.opt.afterFixed)self.opt.afterFixed(self.opt.target, panelClone);
                }else{
                    panelClone.hide();
                    if(self.opt.afterNoFixed)self.opt.afterNoFixed(self.opt.target, panelClone);
                }
            });
            return this;
        },
        /**
         * 窗口滚动事件
         */
        scroll : function(call){
            $(window).on('scroll', function(){
                var scrollTop = $(window).scrollTop();
                if(call)call(scrollTop);
            });
        },
        /**
         * 回调函数
         */
        beforeFixed : function(clone){
            if(this.opt.beforeFixed)this.opt.beforeFixed(this.opt.target, clone);
        },
        /**
         * 回调函数
         */
        remove : function(){
            this.call(null);
        }
    };
});