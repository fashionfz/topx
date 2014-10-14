/**
 * @description: 重置密码页面
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'module/login',
    'module/pageCommon'
], function(login, common){
	common.initLogin();
    login.initResetPwdBind();
});
