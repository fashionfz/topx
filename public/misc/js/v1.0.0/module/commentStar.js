/*
 * @description: 评论星级模块
 * @author: Zhang Guanglin
 */
define('module/commentStar', [
    'common/util'
], function(util){
    var sStarDefaultTpl = '<span class="icon icon-star-base"><span class="icon"></span></span>',
        sStarHiddenTpl = '<input type="hidden" value="0" name="#{starScore}" id="starScore" />',
        aTips = ['一星，不满意','两星，不太好','三星，一般般','四星，还不错','五星，相当满意'],
        defaultScore = 0,
        starScore,
        $star,
        $starScore,
        _getStarTpl = function(){
            var aStarTpl = [];
            for(var i=0;i<5;i++){
                aStarTpl.push(util.template(sStarDefaultTpl,{
                    title : aTips[i]
                }));
            }
            aStarTpl.push(util.template(sStarHiddenTpl,{
                starScore : starScore
            }));
            return aStarTpl.join('');
        },
        _bindEvens = function(panel){
            $starScore = $('#starScore');
            $star = panel.find('.icon-star-base');
            var sRed = 'icon-star-on-red',
                sLock = 'lock';

            $star.each(function(){
                var $this = $(this),
                    $prevAll = $this.prevAll($star).andSelf(),
                    $nextAll = $this.nextAll($star);
                $this.hover(
                    function(){
                        $nextAll.removeClass(sLock).find(".icon").removeClass(sRed);
                        $prevAll.find(".icon").addClass(sRed);
                    },
                    function(){
                        if(!defaultScore){
                            if(!$this.hasClass(sLock)){
                                $star.find(".icon").removeClass(sRed);
                            }
                        }else{
                            var $defaultScore = $('.icon-star-base:lt('+defaultScore+')');
                            $star.find(".icon").removeClass(sRed);
                            $defaultScore.addClass(sLock);
                            $defaultScore.find(".icon").addClass(sRed);
                            $starScore.val(defaultScore);
                        }
                    }
                ).on('click',function(){
                    $prevAll.addClass(sLock);
                    defaultScore = $prevAll.length;
                    $starScore.val(defaultScore);
                });
            });
        };
    return {
        init : function(){
            var self = this,
                arg = arguments[0],
                container = arg.container,
                $container = $(container);
            starScore = arg.hiddenName;
            $container.append(_getStarTpl());
            _bindEvens($container);
        }
    }
});