/**
 * @description: 重置密码页面
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/util',
    'module/pageCommon'
], function(util, common){
	
	common.initLogin();

    util.countdown('.seconds-return-num', 3, function(){
        window.location = '/';
    });
});
