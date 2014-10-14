/**
 * @description: 忘记密码页面
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon'
], function(inter, util, common){
    common.initLogin();
    if($('.btn-reSend').length){
    	var $btnReSend = $('.btn-reSend'),
    		time = 60,
			setTime = function(){
				if(time>0){
					$btnReSend.val('重新发送邮件('+time+')');
					time--;
				}else{
					$btnReSend.val('重新发送邮件')
						.removeAttr('disabled')
						.removeClass('btn-disabled')
						.addClass('btn-green');
					clearInterval(timer);
				}
			},
			timer = setInterval(setTime,1000);
		$btnReSend.on('click',function(){
            var $this = $(this),
                method = "GET",
                url = inter.getApiUrl(),
                sucCall = function(d){
                    if(d.status === 1){
                        $.alert("邮件已发送！请注意查收。");
                        $this.attr('disabled','true')
                            .addClass('btn-disabled')
                            .removeClass('btn-green');
                        time = 60;
                        timer = setInterval(setTime,1000);
                    }else{
                        $.alert("邮件发送失败，请重试！");
                    }
                },
                errCall = function(){
                    $.alert("服务器繁忙请稍后重试")
                };
            util.setAjax(url.sendEmail,'', sucCall, errCall,method);
		});
    }
});
