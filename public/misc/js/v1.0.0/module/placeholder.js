/**
 * @description: placeholder模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/placeholder', [
    'common/util'
], function(util){
    var template = '<span class="placeholder">{0}</span>';

    return {
        init : function(){
            var self = this;
            if('placeholder' in document.createElement('input')){

            }else{
                var inputList = $('input[placeholder][type="text"], input[placeholder][type="password"], textarea[placeholder]') || $();
                inputList.each(function(i, n){
                    var obj = $(n),
                        placeholder = obj ? obj.attr('placeholder') : null;

                    if( placeholder && placeholder.length > 0 && obj.val().length < 1 ){
                        //排除重复项
                        if (obj.next("span").hasClass("placeholder")) {
                            return;
                        }
                        //程序开始
                        var p = $(util.strFormat(template,[placeholder])),
                            clp = obj.clone(),
                            body = $('body'),
                            h = obj.outerHeight(),
                            hh = 0,
                            position = obj.position(),
                            pp = obj.parent().css('position'),
                            left = (parseInt(obj.css("padding-left")) + parseInt(obj.css("text-indent"))) || 10,
                            top = obj.css("padding-top");
                        clp.css({'position':'absolute', 'top': '-100000px'});
                        body.append(clp);
                        hh = clp.outerHeight();
                        h = hh > h ? hh : h;
                        clp.remove();

                        if(pp != 'relative' && pp != 'absolute'){
                            obj.parent().css('position', 'relative');
                        }
                        obj.after(p);
                        top = (h - 18)/2 + position.top;
                        p.css({
                            'top' : top,
                            'left' : left + 2 + position.left,
                            'position' : 'absolute'
                        });
                        obj.on('keyup keypress keydown',function(){
                            /*self._holder($(this).val(), p);*/
                        }).on('blur',function(){
                            if($(this).val().length === 0){
                                p.show();
                            }else{
                                p.hide();
                            }
                        }).on('focus', function(){
                            p.hide();
                        });

                        $('.placeholder').on('click',function(){
                            $(this).siblings('input[type="text"], input[type="password"], input[type="submit"]').focus();
                        });
                    }
                })
            }
        },
        _holder : function(s, p){
            if( s.length < 0 ){
                p.show();
            }else{
                p.hide();
            }
        }
    }
});
