var ued_conf = {
    //cdnPath: 'http://172.16.4.222/',//测试环境CDN
    //cdnPath: 'http://resource.helome.com/',//线上环境CDN
    cdnPath: '/assets/',//开发环境CDN
    payUrl: 'http://172.16.4.96', // 充值中心测试URL
    //payUrl: 'http://usercent.helome.com', // 充值中心线上URL
    //root: 'http://172.16.4.222/topx/assets/misc/',
    //root: 'http://resource.helome.com/topx/assets/misc/',
    root: '/assets/misc/',  //开发
    //amdPath: 'http://172.16.4.222/topx/assets/misc/js/v1.0.0/',
    //amdPath: 'http://resource.helome.com/topx/assets/misc/js/v1.0.0/',
    amdPath: '/assets/misc/js/v1.0.0/', //开发
    //mcPath: 'ws://172.16.4.222:8001', //消息中心测试IP
    //mcPath: 'ws://mc.helome.com:8001', //消息中心线上IP
    mcPath: 'ws://172.16.4.204:8001', //消息中心开发
    version: 'v1.0.0',
    publishVersion: 'v20131021',
    mode: 'dev' // dev/online
};
window.UED_publishTime ="1411630370966"; window.UED_Souce ={"commonCss.css":["skin/v1.0.0/base.css","skin/v1.0.0/common.css","skin/v1.0.0/button.css","skin/v1.0.0/form.css","skin/v1.0.0/icon.css","skin/v1.0.0/header.css","skin/v1.0.0/footer.css","skin/v1.0.0/jquery.autocomplete.css","skin/v1.0.0/consult-msg.css","skin/v1.0.0/dialog-simple.css","skin/v1.0.0/tip-skin.css","skin/v1.0.0/chat.css"],"commonJs.js":["js/v1.0.0/core/require.js","js/v1.0.0/core/requireConfig.js","js/v1.0.0/core/jquery172.js","js/v1.0.0/widget/json2.js","js/v1.0.0/widget/jquery.autocomplete.js","js/v1.0.0/widget/artDialog/jquery.artDialog.js","js/v1.0.0/widget/artDialog/artDialog.plugins.js","js/v1.0.0/widget/jquery.knob.js"],"chatJs.js":["js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/widget/jquery.mousewheel.js","js/v1.0.0/widget/lightbox.js","js/v1.0.0/page/page_chat.js"],"consultCss.css":["skin/v1.0.0/consult.css","skin/v1.0.0/flag.css","skin/v1.0.0/video.css"],"consultJs.js":["js/v1.0.0/page/page_consult.js"],"indexCss.css":["skin/v1.0.0/flag.css","skin/v1.0.0/page_index.css"],"indexJs.js":["js/v1.0.0/page/page_index.js"],"canvasJs.js":["js/v1.0.0/widget/excanvas.js"],"loginCss.css":["skin/v1.0.0/page_login.css","skin/v1.0.0/single.css"],"loginJs.js":["js/v1.0.0/page/page_login.js"],"registerCss.css":["skin/v1.0.0/page_register.css","skin/v1.0.0/single.css"],"registerJs.js":["js/v1.0.0/page/page_register.js"],"singleCss.css":["skin/v1.0.0/single.css"],"forgetPwdJs.js":["js/v1.0.0/page/page_forgetPwd.js"],"resetPwdJs.js":["js/v1.0.0/page/page_resetPwd.js"],"resetSuccessJs.js":["js/v1.0.0/page/page_resetPwdSuccess.js"],"aboutUsCss.css":["skin/v1.0.0/doc.css","skin/v1.0.0/page_aboutus.css"],"tradeProcessCss.css":["skin/v1.0.0/doc.css","skin/v1.0.0/page_tradeProcess.css"],"tradeProcessJs.js":["js/v1.0.0/page/page_tradeProcess.js"],"legalNoticeCss.css":["skin/v1.0.0/doc.css","skin/v1.0.0/page_legalNotice.css"],"docPageJs.js":["js/v1.0.0/page/page_docPage.js"],"phoneCss.css":["skin/v1.0.0/page_phone.css"],"questionsCss.css":["skin/v1.0.0/doc.css","skin/v1.0.0/page_questions.css"],"docCss.css":["skin/v1.0.0/doc.css"],"searchCss.css":["skin/v1.0.0/search-filter.css","skin/v1.0.0/flag.css","skin/v1.0.0/search-result.css"],"searchJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/widget/country.js","js/v1.0.0/page/page_search.js"],"serviceOnlineJs.js":["js/v1.0.0/page/page_serviceOnline.js"],"userDetailCss.css":["skin/v1.0.0/tip-skin.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-detail.css","skin/v1.0.0/search-filter.css","skin/v1.0.0/text-box.css"],"userBaseInfoJs.js":["js/v1.0.0/widget/country.js","js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/page/page_userBaseInfo.js"],"userHeadJs.js":["js/v1.0.0/widget/swfobject.js","js/v1.0.0/page/page_userHead.js"],"userWorkInfoJs.js":["js/v1.0.0/widget/country.js","js/v1.0.0/page/page_userWorkInfo.js"],"userEduInfoJs.js":["js/v1.0.0/widget/country.js","js/v1.0.0/page/page_userEduInfo.js"],"resumeBaseInfoJs.js":["js/v1.0.0/widget/country.js","js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/page/page_resumeBaseInfo.js"],"resumeUserHeadJs.js":["js/v1.0.0/widget/swfobject.js","js/v1.0.0/page/page_resumeUserHead.js"],"resumeWorkInfoJs.js":["js/v1.0.0/widget/country.js","js/v1.0.0/page/page_resumeWorkInfo.js"],"resumeEduInfoJs.js":["js/v1.0.0/widget/country.js","js/v1.0.0/page/page_resumeEduInfo.js"],"userRecordCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-record.css","skin/v1.0.0/date-picker.css"],"userRecordJs.js":["js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userRecord.js"],"userRecordDetailJs.js":["js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userRecordDetail.js"],"userRecordSearchJs.js":["js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userRecordSearch.js"],"groupRecordJs.js":["js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_groupRecord.js"],"groupRecordDetailJs.js":["js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_groupRecordDetail.js"],"groupRecordSearchJs.js":["js/v1.0.0/widget/jquery.poshytip.js","js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_groupRecordSearch.js"],"userFavoriteCss.css":["skin/v1.0.0/flag.css","skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/search-result.css","skin/v1.0.0/user-favorite.css"],"userFavoriteJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userFavorite.js"],"userMessageCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-message.css"],"userMessageJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userMessage.js"],"userAtCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/search-result.css","skin/v1.0.0/user-at.css","skin/v1.0.0/flag.css"],"userAtJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userAt.js"],"userInvitationCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/search-result.css","skin/v1.0.0/user-invitation.css","skin/v1.0.0/flag.css"],"userInvitationJs.js":["js/v1.0.0/widget/ZeroClipboard.js","js/v1.0.0/page/page_userInvitation.js"],"userServiceCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-service.css"],"userServiceJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userService.js"],"userRequireCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-service.css"],"userRequireJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userRequire.js"],"userGroupsCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-groups.css","skin/v1.0.0/group.css"],"userGroupsJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userGroups.js"],"userGroupWriteInfoCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-groups.css","skin/v1.0.0/text-box.css","skin/v1.0.0/group.css","skin/v1.0.0/upload.css"],"userGroupWriteInfoJs.js":["js/v1.0.0/widget/swfobject.js","js/v1.0.0/widget/jquery.fileUploader.js","js/v1.0.0/page/page_userGroupWriteInfo.js"],"userGroupJoinCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-groups.css","skin/v1.0.0/group.css"],"userGroupJoinJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userGroupJoin.js"],"userGroupMultiCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-groups.css","skin/v1.0.0/group.css"],"userGroupMultiJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userGroupMulti.js"],"userGroupAddMemberCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-groups.css"],"userGroupAddMemberJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userGroupAddMember.js"],"userGroupMemberListCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/user-groups.css"],"userGroupMemberListJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_userGroupMemberList.js"],"userServiceWriteInfoCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/user-center.css","skin/v1.0.0/text-box.css","skin/v1.0.0/upload.css","skin/v1.0.0/user-service.css"],"userServiceWriteInfoJs.js":["js/v1.0.0/widget/jquery.fileUploader.js","js/v1.0.0/page/page_userServiceWriteInfo.js"],"userRequireWriteInfoJs.js":["js/v1.0.0/widget/jquery.fileUploader.js","js/v1.0.0/page/page_userRequireWriteInfo.js"],"developerCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/search-result.css","skin/v1.0.0/flag.css","skin/v1.0.0/doc.css","skin/v1.0.0/page_developer.css"],"developerJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_developer.js"],"developerServiceJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_developerService.js"],"developerRequireJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_developerRequire.js"],"skillTagsAllCss.css":["skin/v1.0.0/flag.css","skin/v1.0.0/search-filter.css","skin/v1.0.0/page_skillTagsAll.css"],"skillTagsAllJs.js":["js/v1.0.0/widget/country.js","js/v1.0.0/page/page_skill-tags-all.js"],"commentReplyCss.css":["skin/v1.0.0/flag.css","skin/v1.0.0/search-result.css","skin/v1.0.0/page_commentReply.css"],"commentReplyJs.js":["js/v1.0.0/page/page_commentReply.js"],"userSettingCss.css":["skin/v1.0.0/user-center.css","skin/v1.0.0/user-setting.css"],"userSettingJs.js":["js/v1.0.0/page/page_userSetting.js"],"chargeCss.css":["skin/v1.0.0/user-center.css"],"chargeJs.js":["js/v1.0.0/module/iframe-cross-domain.js","js/v1.0.0/page/page_charge.js"],"chargePayJs.js":["js/v1.0.0/module/iframe-cross-domain.js","js/v1.0.0/page/page_chargePay.js"],"errorCss.css":["skin/v1.0.0/error.css"],"userOrderCss.css":["skin/v1.0.0/flag.css","skin/v1.0.0/search-result.css","skin/v1.0.0/user-order.css"],"userOrderJs.js":["js/v1.0.0/widget/jquery.scroll.js","js/v1.0.0/page/page_userOrder.js"],"phoneModifyJs.js":["js/v1.0.0/page/page_phoneModify.js"],"phoneEmailSuccessJs.js":["js/v1.0.0/page/page_phoneEmailSuccess.js"],"feedbackCss.css":["skin/v1.0.0/doc.css","skin/v1.0.0/feedback.css","skin/v1.0.0/upload.css"],"feedbackJs.js":["js/v1.0.0/widget/jquery.fileUploader.js","js/v1.0.0/page/page_feedback.js"],"groupHomeCss.css":["skin/v1.0.0/group.css","skin/v1.0.0/page_grouphome.css"],"groupHomeJs.js":["js/v1.0.0/page/page_grouphome.js"],"groupDetailCss.css":["skin/v1.0.0/doc.css","skin/v1.0.0/group.css","skin/v1.0.0/page_groupdetail.css"],"groupDetailJs.js":["js/v1.0.0/page/page_groupdetail.js"],"searchGroupCss.css":["skin/v1.0.0/search-filter.css","skin/v1.0.0/group.css","skin/v1.0.0/page_groupSearch.css"],"searchGroupJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_groupSearch.js"],"servicesCss.css":["skin/v1.0.0/flag.css","skin/v1.0.0/sub-common.css","skin/v1.0.0/req-serv-common.css","skin/v1.0.0/page_reqService.css"],"servicesJs.js":["js/v1.0.0/page/page_services.js"],"servicesSearchCss.css":["skin/v1.0.0/search-filter.css","skin/v1.0.0/flag.css","skin/v1.0.0/sub-common.css","skin/v1.0.0/req-serv-common.css","skin/v1.0.0/page_reqServiceSearch.css"],"servicesSearchJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_servicesSearch.js"],"servicesDetailCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/search-result.css","skin/v1.0.0/flag.css","skin/v1.0.0/doc.css","skin/v1.0.0/page_reqServiceDetail.css"],"servicesDetailJs.js":["js/v1.0.0/widget/jquery.switchable.js","js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_servicesDetail.js"],"requireCss.css":["skin/v1.0.0/flag.css","skin/v1.0.0/sub-common.css","skin/v1.0.0/req-serv-common.css","skin/v1.0.0/page_reqService.css"],"requireJs.js":["js/v1.0.0/page/page_require.js"],"requireSearchCss.css":["skin/v1.0.0/search-filter.css","skin/v1.0.0/flag.css","skin/v1.0.0/sub-common.css","skin/v1.0.0/req-serv-common.css","skin/v1.0.0/page_reqServiceSearch.css"],"requireSearchJs.js":["js/v1.0.0/widget/jquery.pagination.js","js/v1.0.0/page/page_requireSearch.js"],"requireDetailCss.css":["skin/v1.0.0/tab-switch.css","skin/v1.0.0/search-result.css","skin/v1.0.0/flag.css","skin/v1.0.0/doc.css","skin/v1.0.0/page_reqServiceDetail.css"],"requireDetailJs.js":["js/v1.0.0/page/page_requireDetail.js"],"serviceCommentCss.css":["skin/v1.0.0/flag.css","skin/v1.0.0/req-serv-common.css","skin/v1.0.0/page_commentReply.css"],"serviceCommentJs.js":["js/v1.0.0/page/page_servicesComment.js"]};
/**
 * 静态文件加载器 - v0.1.1 - 2014-04-22
 * Copyright (c) 2014 TinTao
 */
var ued_souce = window.UED_Souce || {},
    ued_root = window.UED_ROOT || ued_conf.root,
    ued_mode = window.UED_IMPORT_MODE || ued_conf.mode,
    ued_version = window.UED_VERSION || ued_conf.version,
    ued_language = navigator.language || navigator.browserLanguage,
    ued_publishVersion = window.UED_PUBLISH_VERSION || ued_conf.publishVersion;

/**
 * 根据浏览器语言判断是否跳转到国际版
 */
/*if(ued_language.toLowerCase() != 'zh-cn'){
    location.href = 'http://www.helome.us';
}*/

/** 
 * @function import
 * @param id 静态文件的id名称
 * @param fileType  文件类型  js/css
 * @param mode 运行环境 dev/online dev表示环境加载多个源码文件 online代表线上环境 加载单个合并压缩后的文件
*/
function ued_import(id, fileType, mode, isHead) {
	var __mode = ued_mode,
        __id = id +'.' + fileType,
        jsTemplate = '<script src="${src}" charset="utf-8" type="text/javascript" itemid="${itemid}"><\/script>',
        cssTemplate = '<link rel="stylesheet" type="text/css" href="${href}">';

	if (mode) {
		__mode = mode;
	} else if (window.UED_IMPORT_MODE) {
		__mode = window.UED_IMPORT_MODE;
	}

	if (!ued_souce[__id]) {
		return false;
	}

	function __import(aFiles, type, id) {
		for(var i=0; i<aFiles.length; i++) {
            var outStr = '';
			if (type == "js") {
                outStr = jsTemplate.replace("${src}", ued_root + aFiles[i]).replace("${itemid}", id);
			} else if (type == "css") {
                outStr = cssTemplate.replace("${href}", ued_root + aFiles[i]).replace("${itemid}", id);
			}
            if(isHead){
                asyncImportJs(ued_root + aFiles[i]);
            }else{
                document.write(outStr);
            }
		}
	}

    if (__mode == "online") {
		if ("css" == fileType) {
			__import(["dist/"+ ued_publishVersion + "/" + __id.replace(".css", "-min.css")], fileType, __id);
		} else {
			__import(["dist/"+ ued_publishVersion + "/" + __id.replace(".js", "-min.js")], fileType, __id);
		}
    } else if (__mode == "dev") {
        __import(ued_souce[__id], fileType, __id);
    }
} 
/** 
 * @function asyncImport
 * @param src js的路径
*/
function asyncImportJs(src, charset) {
	var container = document.getElementsByTagName("head")[0];
    var script = document.createElement("script");
    script.src = src;

    if (charset) {
        script.charset = charset;
    }
    // 防止没有head标记的情况
    if (!container) {
        document.body.insertBefore(script, document.body.firstChild);
    } else {
        container.appendChild(script);
    }
}