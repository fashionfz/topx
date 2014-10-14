/**
 * @description: 交易流程
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */

require([
    'module/pageCommon',
    'module/fixed'
],function(common, fixed){
    common.initLogin();

    $('.J-search-input').val('');
    $('.trade-btn-search').on('click', function(event){


        if($.trim($('.J-search-input').val()).length<1){
            event.stopPropagation();
            event.preventDefault();
            return false;
        }
    });


    /**
     * fixed静止定位插件
     *//*
    var fixed = function(target, opt){
        var defaults = {
            target : $(target),
            beforeFixed : null,
            afterFixed : null,
            afterNoFixed : null
        };
        this.opt = $.extend(defaults, opt || {});
        this.init();
    };

    fixed.prototype = {
        init : function(){
            var self = this,
                fixPanel = self.opt.target,
                panelClone = fixPanel.clone(),
                fixedTop = fixPanel.offset().top;

            self.beforeFixed(panelClone);

            self.scroll(function(st){
                if(st > fixedTop){
                    fixPanel.after(panelClone);
                    panelClone.show();
                    if(self.opt.afterFixed)self.opt.afterFixed(panelClone);
                }else{
                    panelClone.hide();
                    if(self.opt.afterNoFixed)self.opt.afterNoFixed(panelClone);
                }
            });
        },
        scroll : function(call){
            $(window).on('scroll', function(){
                var scrollTop = $(window).scrollTop();
                if(call)call(scrollTop);
            });
        },
        beforeFixed : function(clone){
            if(this.opt.beforeFixed)this.opt.beforeFixed(this.opt.target, clone);
        }
    };
    $.fn.fixed = function(opts){
        return this.each(function () {
            if (!$.data(this, "plugin_fixed")) {
                $.data(this, "plugin_fixed", new fixed( this, opts ));
            }
        });
    };*/

    var goScroll = function(target, clone){
        var tip = target.attr('href'),
            scrollH = $(tip).offset().top - (clone.height()||116);

        $("html,body").animate({scrollTop: scrollH}, 500);
    };
    /**
     * 使用静止定位插件
     */
    fixed.init({
        target : $('.menuFixed'),
        beforeFixed : function(menuPanel, clone){
            menuPanel.delegate('a', 'click', function(e){
                goScroll($(this), clone);
                e.preventDefault();
            });
            clone.delegate('a', 'click', function(e){
                goScroll($(this), clone);
                e.preventDefault();
            })
        },
        afterFixed : function(menuPanel, clone){
            clone.attr('class', 'banner-fixed').find('.dev-icon').css('padding-top',16);
        }
    })
});