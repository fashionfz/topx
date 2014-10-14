/**
 * @description: 注册页面
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'module/login',
    'module/pageCommon',
    'common/interface',
    'module/checkForm',
    'module/strength',
    'module/prettifyForm'
], function(login, common, inter, check, strength, prettify){
	
    /**
     * 初始化注册窗口
     */
    var initRegBind = function(){
        var $block = $('.reg-main'),
            $regEmail = $('#rEmail');

        prettify.initCheckbox();

        strength.init('#rPwd', {min: 6, max: 18});

        check.emailTips('#rEmail');

        login._changeCode();

        $block.find('.replace-code').on('click', function(e){
            login._changeCode();
            e.preventDefault();
        });

        $block.find('.btn-reg').on('click', function(e){
            login.register({
                block:          $block,
                regName:        $regEmail,
                regPwd:         $('#rPwd'),
                regConfirmPwd:  $('#rConfirmPwd'),
                regCode:        $('#rCode'),
                regUrl:         inter.getApiUrl().toRegisterUrl
            });
            e.preventDefault();
        });

        $regEmail.focus();
    };

    common.initLogin();
    initRegBind();
});