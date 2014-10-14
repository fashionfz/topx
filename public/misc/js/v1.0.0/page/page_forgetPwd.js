/**
 * @description: 忘记密码页面
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/util',
    'module/login',
    'module/pageCommon'
], function(util, login, common){
	common.initLogin();
    login.initForgetPasswordBind();
    
    //发送成功页面
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
            var href = util.location();

            login.getPwdEmail({email: href.email, captcha: href.captcha, reSend: 1});

			$(this).attr('disabled','true')
						.addClass('btn-disabled')
						.removeClass('btn-green');
			time = 60;
			timer = setInterval(setTime,1000);
		});
    }
});
