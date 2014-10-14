/**
 * @description: 个人头像信息
 * @author: Zhang Guanglin(zhangguang.lin@163.com)
 * @data: 2014/2/18
 */

require([
    'common/interface',
    'module/pageCommon',
    'module/swfUpload',
    'module/enResume'
], function(inter, common, swfUpload, enResume){
    
	//初始化登陆和搜索
    common.initLogin();
    enResume.init($('.user-info .tab-menu'));

    //头像上传
    swfUpload.init(inter.getApiUrl().uploadHeadUrl, 'altContent');
});