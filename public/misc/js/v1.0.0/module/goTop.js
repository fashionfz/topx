/**
 * @description: 回到顶部模块
 * @author: Zhang Guanglin
 * @update: Young Foo
 */
define('module/goTop', [], function(){

	return {
		init : function(){

            var self = this;
            var html = ['<div id="rightBox">',
                            '<div class="right-item show-fb "><a href="/feedback" target="_blank"><table><tr><td class="right-item-cell"><em class="show-fb-icon"></em></td><td class="right-item-cell go-top-text">反馈<br>建议</td></tr></table></a></div>',
                            '<div class="right-item scan-phone"><a href="/phone" target="_blank"><table><tr><td class="right-item-cell"><em class="scan-qrcode-icon"></em></td><td class="right-item-cell go-top-text">手机<br>版</td></tr></table></a></div>',
                            '<div class="right-item show-service"><a href="/tagsearch?st=嗨啰在线客服" target="_blank"><table><tr><td class="right-item-cell"><em class="show-service-icon"></em></td><td class="right-item-cell go-top-text">客服</td></tr></table></a></div>',
                            '<div class="right-item go-top"><table><tr><td class="right-item-cell"><em class="go-top-icon"></em></td><td class="right-item-cell go-top-text">返回<br>顶部</td></tr></table></div>',
                        '</div>'].join("");
            
            $('body').append(html);

            var goBtn = $('#rightBox .go-top'),
                rightBox = $('#rightBox');

            self._effect(goBtn);
		    $(window).scroll(function () {
                self._effect(goBtn);
		    });
            goBtn.on('click',function () {
		        $('html, body').animate({ scrollTop: 0 }, 200);
		    });
		},
        /**
         * 按钮渐隐渐现
         */
        _effect : function (thi) {
            if ($(document).scrollTop() > 150) {
                thi.fadeIn(200);
            } else {
                thi.fadeOut(200);
            }
        }
	}
});