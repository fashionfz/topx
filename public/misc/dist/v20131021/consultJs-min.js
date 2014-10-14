define("module/cookie",[],function(){return{get:function(a){var b="; "+document.cookie+"; ",c=b.indexOf("; "+a+"=");if(-1!=c){var d=b.substring(c+a.length+3,b.length);return decodeURIComponent(d.substring(0,d.indexOf("; ")))}return null},set:function(a,b,c,d){var e=null,f=null;f=c?"; expires="+c.toGMTString():"",e=a+"="+encodeURIComponent(b)+f+"; path=/",d&&(e+=";domain="+d),document.cookie=e},del:function(a){var b=new Date((new Date).getTime()-1),c=this.get(a);null!=c&&(document.cookie=a+"="+c+";expires="+b.toGMTString()+":path=/")},clearAllCookie:function(){var a=document.cookie.match(/[^ =;]+(?=\=)/g);if(a)for(var b=a.length;b--;)document.cookie=a[b]+"=0;expires="+new Date(0).toUTCString()+":path=/"}}}),define("common/util",["module/cookie"],function(a){var b={};return{setAjax:function(b,c,d,e,f,g){var h=parseInt(a.get("_u_id"))||0,i={type:"POST",dataType:"json",data:JSON.stringify(c),cache:!1,url:b,async:g?!1:!0,success:function(a){a&&a.notLogin&&"true"===a.notLogin?location.href="/login?msg=1&referer="+encodeURIComponent(location.href):a&&a.notCompInfo&&"true"===a.notCompInfo?location.href="/user/thirdaccountsetting?referer="+encodeURIComponent(location.href):d&&d(a)},error:function(a,b){switch(b){case"timeout":break;case"error":0!=a.status&&(e?e():alert("服务器繁忙，请稍后再试！"));break;case"notmodified":break;case"parsererror":e?e():alert("服务器繁忙，请稍后再试！")}}};return h&&(i.headers={currentUid:h}),f&&"GET"===f?(i.type="GET",i.data=c):i.contentType="application/json; charset=utf-8",$.ajax(i)},objectToStr:function(a){var b=this;return"object"==typeof a?$.each(a,function(c,d){a[c]=b.safeHTML(b.objectToStr(d))}):a=b.safeHTML(a),a},isURL:function(a){var b=/^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i,c=new RegExp(b);return c.test(a)},strFormat:function(a,b){if(a&&0!=a.length)for(var c=0;c<a.length;c++)a=a.replace(new RegExp("\\{"+c+"\\}","g"),b[c]);else a="";return a},template:function(a,b,c){var d;if($.browser.msie)d=a;else{d=[];var e,f=a.length;for(e=0;f>e;e++)d.push(a[e]);d=d.join("")}var g=new RegExp("#{([a-z0-9_]+)}","ig");return d=d.replace(g,function(a,d){return 0==b[d]||"0"==b[d]?b[d]:b[d]?b[d]:c?a:""})},tab:function(a,b,c,d){var e=this;$(a).on("click",function(a){var f=$(this),g=f.siblings(),h=f.index();g.removeClass(b),f.addClass(b),c&&e.isNullString(c)&&(g.addClass(c),f.removeClass(c));var i=f.closest(".tab").find(".tab-table"),j=i.eq(h);j.removeClass("hide"),j.siblings(".tab-table").addClass("hide"),d&&d(h),a.preventDefault()})},location:function(){var a,b={},c=null,d=location.search;if(1==arguments.length){if(c=arguments[0],"string"==typeof c){var e=this.location();b=e[c]||""}else if("object"==typeof c)if(d.indexOf("?")>-1){var f=this.location(),g=$.extend(f,c);b=$.param(g)}else b="?"+$.param(c)}else 2==arguments.length?(b[arguments[0]]=arguments[1],b=this.location(b)):(d=d.replace("?",""),d.indexOf("&")>-1?(a=d.split("&"),$.each(a,function(a,c){c.indexOf("=")>-1?(c=c.split("="),b[c[0]]=decodeURIComponent(c[1]+"")):b[c]=""})):d.indexOf("=")>-1?(a=d.split("="),b[a[0]]=a[1]):b[d]="");return b},dateFormat:function(a,b){var c=new Date(a),d={"M+":c.getMonth()+1,"d+":c.getDate(),"h+":c.getHours(),"m+":c.getMinutes(),"s+":c.getSeconds(),"q+":Math.floor((c.getMonth()+3)/3),S:c.getMilliseconds()};/(y+)/.test(b)&&(b=b.replace(RegExp.$1,(c.getFullYear()+"").substr(4-RegExp.$1.length)));for(var e in d)new RegExp("("+e+")").test(b)&&(b=b.replace(RegExp.$1,1==RegExp.$1.length?d[e]:("00"+d[e]).substr((""+d[e]).length)));return b},getByName:function(a){var b=$('.main input[name="'+a+'"]'),c=$('.main select[name="'+a+'"]'),d=$('.main textarea[name="'+a+'"]');return b.length>0&&("radio"===b.attr("type")||"checkbox"===b.attr("type"))&&(b=$('.main input[name="'+a+'"]:checked')),b.length>0?b:c.length>0?c:d.length>0?d:null},isNullString:function(a){return 0==a.replace(/(^\s*)|(\s*$)/g,"").length},isArray:function(a){return a instanceof Array},getMaskEmail:function(a){if(this.isNullString(a))return a;var b=a.split("@"),c=b[0].length,d=4>c?c:4,e="";e+=b[0].substring(0,c-d);for(var f=0;d>f;f++)e+="*";return e+"@"+b[1]},imgLoadError:function(a){var b=a||$("img");b.each(function(a,b){b.onerror=function(){$(b).prop("src",ued_conf.root+"images/dev-head-default1.png")}})},safeHTML:function(a){return a&&"string"==typeof a&&(a=a.replace(/</gi,"&lt;"),a=a.replace(/>/gi,"&gt;")),a},getAvatar:function(b,c){var d=parseInt(a.get("_u_id"))||0,e=$(".J-login-mark .img-full").prop("src"),f="";if(e.indexOf("assets")>-1)f=e.substring(0,e.indexOf("assets"))+"uploadfile/avatar/"+b+"/avatar_190.jpg";else{var g=e.indexOf("?");f=e.substring(0,g>-1?g:e.length).replace("/"+d+"/","/"+b+"/")}return c&&"online"===ued_conf.mode&&(f=f+"_"+c+"_"+c),f},removeHTMLTag:function(a){return a&&"string"==typeof a&&(a=a.replace(/<\/?[^>]*>/g,""),a=a.replace(/[ | ]*\n/g,"\n"),a=a.replace(/&nbsp;/gi,"")),a},countdown:function(a,c,d,e,f){var g=this,h=c-1;f=f||"default",a&&($(a+":input").val(c),$(a).text(c)),c?(b[f]=setTimeout(function(){g.countdown(a,h,d,e,f)},1e3),e&&e(c)):d&&d()},clearCountdown:function(a){a=a||"default",clearTimeout(b[a])},wordsCount:function(a,b,c){$(a).on("keydown keyup blur mousecenter mouseleave mousemove",function(){var a=$(this).val().length||0,d=c-a;b&&$(b).text(d>0?d:0)}).attr("maxLength",c)},autoAddEllipsis:function(a,b,c){var d=this,e=d.cutString(a,b),f=e.cutFlag,g=e.cutString;return"1"==f?g+(c||"..."):g},cutString:function(a,b){var c=this;if(!a)return{cutString:"",cutFlag:""};var d,e=a.length,f="1",g=0,h=!1;if(b/2>=e&&(d=a,h=!0),!h)for(var i=0;e>i;i++){if(g+=c.isFull(a.charAt(i))?2:1,g>b){d=a.substring(0,i),h=!0;break}if(g==b){d=a.substring(0,i+1),h=!0;break}}return h||(d=a,h=!0),d.length==e&&(f="0"),{cutString:d,cutFlag:f}},isFull:function(a){for(var b=0;b<a.length;b++)return a.charCodeAt(b)>128},onBeforeBomUnload:function(a){$.browser.msie?window.document.body.onbeforeunload=a:(window.onBeforeUnload=a,$("body").attr("onbeforeunload","return onBeforeUnload();"))},toFixed:function(a,b){var c=parseFloat(a),d=b||2,e=Math.pow(10,d),f="0",g=0,h=0,i=0;if(c&&(f=Math.round(c*e)/e+""),g=f.indexOf("."),-1==g&&(f+="."),g=f.indexOf("."),h=f.substring(g,f.length).length-1,h>=d)f=f.substring(0,g+d+1);else for(;d-h>i;i++)f+="0";return f},IEVersion:function(){for(var a=3,b=document.createElement("div"),c=b.getElementsByTagName("i");b.innerHTML="<!--[if gt IE "+ ++a+"]><i></i><![endif]-->",c[0];);return a>4?a:!1},isIE6:function(){return 6===this.IEVersion()},isIE7:function(){return 7===this.IEVersion()},isIE8:function(){return 8===this.IEVersion()},isIE9:function(){return 9===this.IEVersion()},trace:function(a,b){"dev"===ued_conf.mode&&window.console&&(b?console.log("%c"+a,"color:"+b):console.log(a))},arrayBufferToString:function(a){var b=this.arrayBufferToBase64(a);return this.binaryToString(b)},binaryToString:function(a){var b;try{return console.info(decodeURIComponent(escape(a))),decodeURIComponent(escape(a))}catch(c){if(b=c,b instanceof URIError)return a;throw b}},arrayBufferToBase64:function(a){for(var b="",c=new Uint8Array(a),d=c.byteLength,e=0;d>e;e++)b+=String.fromCharCode(c[e]);return b}}}),define("common/interface",[],function(){var a=!1;return{getApiUrl:function(){var b={};return b=a?{getLoginUrl:ued_conf.root+"json/login.json",toLoginUrl:ued_conf.root+"/json/login.json",toRegisterUrl:ued_conf.root+"/json/reg.json",forgetPwdUrl:ued_conf.root+"/json/forget.json",editPwdUrl:ued_conf.root+"/json/editPwd.json",checkEmailUrl:ued_conf.root+"/json/email.json",captchaUrl:ued_conf.root+"/json/captcha.json",addAttentionUrl:ued_conf.root+"/json/addAttention.json",unAttentionUrl:ued_conf.root+"/json/unAttention.json",commentListUrl:ued_conf.root+"/json/commentList.json",leaveMessageUrl:ued_conf.root+"/json/leaveMessage.json",searchListUrl:ued_conf.root+"/json/expertVOList.json",saveUserInfoUrl:ued_conf.root+"/json/saveUserInfo.json",saveServiceUrl:ued_conf.root+"/json/saveService.json",getOrderUsedUrl:"",saveOrderDateUrl:"",uploadHeadUrl:ued_conf.root+"/json/upload.json",saveEmailUrl:ued_conf.root+"/json/saveEmail.json",savePhoneUrl:ued_conf.root+"/json/savePhone.json",saveModifyPhoneUrl:ued_conf.root+"/json/saveModifyPhone.json",checkPhoneUrl:ued_conf.root+"/json/checkPhone.json",sendPhoneSMSUrl:ued_conf.root+"/json/sendPhoneSMS.json",sendSMSByPhoneUrl:ued_conf.root+"/json/sendPhoneSMSwithoutPara.json",sendSMSByEmail:ued_conf.root+"/json/sendSMSByEmail.json",saveNewPwdUrl:ued_conf.root+"/json/saveNewPwd.json",saveTipsUrl:ued_conf.root+"/json/saveTips.json",saveAppLoginUrl:ued_conf.root+"/json/saveTips.json",saveBookingTipsUrl:ued_conf.root+"/json/saveTips.json",userMegUrl:ued_conf.root+"/user/user-message.shtml",sendMsgUrl:ued_conf.root+"/consult/sendMsg.shtml",getMsgUrl:ued_conf.root+"/json/getMsg.json",getTagsUrl:"",getTradeListUrl:"",getTradeShowSNSUrl:"",shareTradeSNSUrl:"",deleteTradeUrl:"",getMessageListUrl:"",deleteMessageUrl:"",getMessageCountUrl:"",updateMessageUrl:"",getConsultPollUrl:"",getCommentListUrl:"/comment/expertComments",saveCommentUrl:"",saveNewCommentUrl:"",addReplyUrl:"/comment/addReply",cancelTradeUrl:"/user/usertrade/cancel",consultUserUrl:"http://192.168.0.195:9000/host/{0}/{1}",consultExportUrl:"/chat/{0}",consultTradeOnlineUrl:"/user/usertrade/unConsult",getFbDataUrl:ued_conf.root+"/json/getFbData.json",saveFeedbackUrl:ued_conf.root+"/json/saveFeedback.json",saveComplainUrl:"",allExpertUrl:ued_conf.root+"/json/allExpert.json",uploadUrl:ued_conf.root+"",getVideoStatusUrl:"",rejectVideoUrl:"",getTypeTagsUrl:ued_conf.root+"/json/getTags.json",clearCookieUrl:"",expertInfoUrl:"/expert/expertInfo/",sendEmail:"/usersetting/sendEmail",phoneMailSuccess:"/usersetting/phonemailsuccess",moreChatRecordUrl:ued_conf.root+"/json/moreChatRecord.json",beforeBeginCharge:"/chat/begin/{0}",getNewMsgUrl:"",updateMsgUrl:"",getSelfInChatUrl:"",commentReplyUrl:"",getTranslate:"",addGroupByTranslate:"",getGroupMember:"",updateChatMsgUrl:"",getChatUser:ued_conf.root+"json/chatUser.json",getChatByOption:"",inviteFriends:"",addFriend:"",deleteFriend:"",queryChatDetail:"",uploadAvatar:"",uploadGroupBack:"",addGroup:"",inviteJoinGroup:"",queryGroups:"",queryJoinGroups:"",queryGroupById:"",exitGroup:"",deleteGroupMember:"",addDiscussionUrl:ued_conf.root+"json/addDiscussion.json"}:{getLoginUrl:"/login",toLoginUrl:"/login",toThirdLoginUrl:"/user/auth/bindAccountLogin",toRegisterUrl:"/register",toThirdRegisterUrl:"/user/auth/newAccountLogin",toThirdLoginNoAccountUrl:"/user/auth/directLogin",forgetPwdUrl:"/forgetpwd",editPwdUrl:"/resetpwd",checkEmailUrl:"/emailexists",captchaUrl:"/captcha",addAttentionUrl:"",unAttentionUrl:"",commentListUrl:"",leaveMessageUrl:"",searchListUrl:"/expertsearch",saveUserInfoUrl:"/user/userdetail/personalinfo",saveServiceUrl:"/user/userdetail/serviceinfo",getOrderDateUrl:"/expert/record/{0}",getOrderUsedUrl:"/expert/record/used/{0}/{1}",saveOrderDateUrl:"/expert/record",uploadHeadUrl:"/user/uploadavatar",saveEmailUrl:"/user/usersetting/changeEmail",savePhoneUrl:"/user/usersetting/bindMobilePhone",saveModifyPhoneUrl:"/user/usersetting/updateMobilePhone",checkPhoneUrl:"/user/usersetting/phoneNumExists",sendPhoneSMSUrl:"/user/usersetting/sendPhoneVerificationCode",sendSMSByPhoneUrl:"/user/usersetting/sendVerificationCodeByPhone",sendSMSByEmail:"/user/usersetting/sendVerificationCodeByNewPhone",saveNewPwdUrl:"/user/usersetting/changePassword",saveTipsUrl:"/user/usersetting/modifySafetyReminder",saveAppLoginUrl:"/user/usersetting/completeUserInfo",saveBookingTipsUrl:"/user/usersetting/modifyBookingReminder",userMegUrl:"/user/usermessage",sendMsgUrl:"/consult/sendMsg",getMsgUrl:ued_conf.root+"json/getMsg.json",getTagsUrl:"/skilltags/query",getTradeListUrl:"/user/usertrade",getTradeShowSNSUrl:"/user/usertrade/getShareInfo",shareTradeSNSUrl:"/user/usertrade/shareInfo",deleteTradeUrl:"/user/usertrade/delete",getChatRecUrl:"/msg/queryChatMessage",getMessageListUrl:"/msg/query",deleteMessageUrl:"/msg/delete",getMessageCountUrl:"/msg/poll",updateMessageUrl:"/msg/updateread",getConsultPollUrl:"/msg/consultClear",getCommentListUrl:"/comment/list",saveCommentUrl:"/comment/addComment",saveNewCommentUrl:"/comment/saveComment",addReplyUrl:"/comment/addReply",cancelTradeUrl:"/user/usertrade/cancel",consultTradeUserUrl:"/consult/beginReserve",consultExportUrl:"/chat/{0}",consultTradeExportUrl:"/user/usertrade/accepted/{0}",consultTradeOnlineUrl:"/user/usertrade/unConsult",getFbDataUrl:"/feedback/beforeComplain",saveFeedbackUrl:"/feedback/suggestion",saveComplainUrl:"/feedback/complain",allExpertUrl:"/skilltags/tagmore",uploadUrl:"/feedback/uploadproof",getTypeTagsUrl:"/skilltags/change",clearCookieUrl:"/forgetMe",expertInfoUrl:"/expert/expertInfo/{0}",sendEmail:"/user/usersetting/sendEmail",phoneMailSuccess:"/user/usersetting/phonemailsuccess",moreChatRecordUrl:"/chat/chatMsg/chatMsgQuery",beforeBeginCharge:"/chat/begin/{0}",getNewMsgUrl:"/msg/new",getNewChatMsgUrl:"/msg/chatMsgUnReadInfo",updateMsgUrl:"/msg/notice",getSelfInChatUrl:"/chat/whetherInChat/{0}/{1}",getYouInChatUrl:"/chat/whetherOnline/{0}",commentReplyUrl:"/comment/detail/{0}",addFavorite:"/user/addFavorite/{0}",deleteFavorite:"/user/deleteFavorite/{0}",queryFavorite:"/user/favorite/queryFavorite",getTranslateUrl:"/tl/autotl",addGroupByTranslate:"/msg/group/addUserToGroup/{0}/{1}",addMultiCommunicate:"/user/group/createMultiCommunicate",updateMultiCommunicate:"/user/group/updateMultiCommunicateName",getGroupMember:"/msg/group/queryUserUnderGroup/{0}",getGroupMemberByPage:"/msg/group/queryMemberUnderGroup",getTranslatorUrl:"/msg/queryCustomerServices",updateChatMsgUrl:"/chat/cleanChatNum/{0}/{1}",updateGroupMsgUrl:"/chat/cleanGroupChatNum/{0}/{1}",getChatUser:"/msg/queryRelationship",getChatByOption:"/msg/queryChatMessageByCondition",inviteFriends:"/user/at/inviteFriends",addFriend:"/user/at/addFriend",queryFriends:"/user/at/queryFriends",deleteFriend:"/user/at/deleteFriend/{0}/{1}",queryChatDetail:"/msg/queryChatMessageContext",uploadAvatar:"/user/group/uploadavatar",uploadGroupBack:"/user/group/uploadHeadBackGround",addGroup:"/user/group/createOrUpdateGroup",queryGroups:"/user/group/queryCreatedGroups",queryJoinGroups:"/user/group/queryJoinedGroups",queryGroupById:"/user/group/queryGroup/{0}",deleteGroup:"/user/group/deleteGroup/{0}",exitGroup:"/user/group/quitGroup/{0}",addMembers:"/msg/group/appendMemberToGroup",inviteJoinGroup:"/groupmsg/invitmembers",queryTempGroups:"/user/group/queryTempGroups",saveResume:"/resume/personalinfo",deleteGroupMember:"/user/group/removeMember",groupAgreeApply:"/groupmsg/agreeApply",groupAgreeInvite:"/groupmsg/agreeInvit",groupReject:"/groupmsg/reject",addEnResume:"/resume/addTaskForChinese",queryService:"/user/service/queryServices",queryRequire:"/user/require/queryRequires",addService:"/user/service/createOrUpdateService",addRequire:"/user/require/createOrUpdateRequire",deleteService:"/user/service/delete/{0}",deleteRequire:"/user/require/delete/{0}",getGroupDetailList:"/group/memberList",getGroupHomeList:"/groupList",applyJoinGroup:"/groupmsg/apply",requireHomeList:"/require",serviceHomeList:"/services",getDeveloperRequireList:"/user/require/requiresOfTa",getDeveloperServiceList:"/user/service/servicesOfTa",getMsgUnread:"/msg/queryUnread",markUnreadMsg:"/msg/updateread",requireSearch:"/require/search",serviceSearch:"/services/search"}}}}),define("module/uuid",[],function(){var a="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("");return{get:function(b,c){var d,e=a,f=[];if(c=c||e.length,b)for(d=0;b>d;d++)f[d]=e[0|Math.random()*c];else{var g;for(f[8]=f[13]=f[18]=f[23]="-",f[14]="4",d=0;36>d;d++)f[d]||(g=0|16*Math.random(),f[d]=e[19==d?3&g|8:g])}return f.join("")},fast:function(){for(var b,c=a,d=new Array(36),e=0,f=0;36>f;f++)8==f||13==f||18==f||23==f?d[f]="-":14==f?d[f]="4":(2>=e&&(e=33554432+16777216*Math.random()|0),b=15&e,e>>=4,d[f]=c[19==f?3&b|8:b]);return d.join("")},compact:function(){return"xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,function(a){var b=16*Math.random()|0,c="x"==a?b:3&b|8;return c.toString(16)})}}}),ued_conf.MC={mcSever:"172.16.4.200:8080",mcRegion:"1"},ued_conf.MC.mcApi=ued_conf.MC.mcSever?"http://"+ued_conf.MC.mcSever+"/getMCServer":"",define("module/mc",["common/util"],function(a){return{initConfig:function(){ued_conf.mcPath||(ued_conf.MC.mcApi?a.setAjax(ued_conf.MC.mcApi,{groupId:ued_conf.MC.mcRegion},function(a){a.error?$.alert("访问消息中心[M]出错 : "+a.error):a.addrIp&&a.wsPort&&(ued_conf.mcPath="ws://"+a.addrIp+":"+a.wsPort)},function(){$.alert("消息中心[M]繁忙，请稍后再试。")},"GET",!0):$.alert("无可访问的消息中心[M]地址.")),ued_conf.mcPath?console.debug("[MC]"+ued_conf.mcPath+" ..."):$.alert("无可访问的消息中心地址.")}}}),define("module/webRtc",["common/interface","common/util","module/uuid","module/mc"],function(a,b,c,d){function e(a){for(var b=a.split("\r\n"),c=null,d=0;d<b.length;d++)if(-1!==b[d].search("m=audio")){c=d;break}if(null===c)return a;for(var e=0;e<b.length;e++)if(-1!==b[e].search("opus/48000")){var i=f(b[e],/:(\d+) opus\/48000/i);i&&(b[c]=g(b[c],i));break}return b=h(b,c),a=b.join("\r\n")}function f(a,b){var c=a.match(b);return c&&2==c.length?c[1]:null}function g(a,b){for(var c=a.split(" "),d=[],e=0,f=0;f<c.length;f++)3===e&&(d[e++]=b),c[f]!==b&&(d[e++]=c[f]);return d.join(" ")}function h(a,b){for(var c=a[b].split(" "),d=a.length-1;d>=0;d--){var e=f(a[d],/a=rtpmap:(\d+) CN\/\d+/i);if(e){var g=c.indexOf(e);-1!==g&&c.splice(g,1),a.splice(d,1)}}return a[b]=c.join(" "),a}var i=window.RTCPeerConnection||window.webkitRTCPeerConnection||window.mozRTCPeerConnection,j=window.URL||window.webkitURL||window.msURL||window.oURL,k=navigator.getUserMedia||navigator.webkitGetUserMedia||navigator.mozGetUserMedia||navigator.msGetUserMedia;nativeRTCIceCandidate=window.mozRTCIceCandidate||window.RTCIceCandidate,nativeRTCSessionDescription=window.mozRTCSessionDescription||window.RTCSessionDescription,window.WebSocket=window.WebSocket||window.MozWebSocket,window.sdpConstraints={optional:[],mandatory:{OfferToReceiveAudio:!0,OfferToReceiveVideo:!0}},window.mozRTCPeerConnection&&(sdpConstraints.mandatory.MozDontOfferDataChannel=!0);var l=['<div id="consultMsgBox">','<div class="cmb-tit clearfix">','<span class="span-left">提示</span>',"</div>",'<div class="cmb-table">','<table border="0" width="100%">',"<tr>",'<td class="cmb-content pl0" align="center">',"<p>对不起您的浏览器不支持视频和语音通讯若希望获得更好的浏览体验请下载最新版",'<a href="http://www.google.cn/intl/zh-CN/chrome/browser/" target="_blank">Google Chrome浏览器</a></p>',"</td>","</tr>","<tr>",'<td class="cmb-button">','<button class="btn-default btn-xs btn-green cmb-start" id="downloadChrome">立即下载</button>','<button class="btn-default btn-xs btn-gray cmb-stop" id="closeMsg">以后再说</button>',"</td>","</tr>","</table>","</div>","</div>"].join(""),m=function(){b.trace("not support webrtc");var a=$(l);$("#init").find("#consultMsgBox").remove(),$("#init").append(a),a.find("#downloadChrome").bind("click",function(){a.remove(),window.open("http://www.google.cn/intl/zh-CN/chrome/browser/")}),a.find("#closeMsg").bind("click",function(){a.remove()}),setTimeout(function(){$("#video-status").removeAttr("class").addClass("status-init"),$(".init-bg").removeClass("none").show(),$(".video-me").remove()},500)},n=window.rtc={};n.onLine=!1,n._socket=null,n._events={},n.isAudio=!1,n.connectionTimeout=1e4,n.on=function(a,b){n._events[a]=n._events[a]||[],n._events[a].push(b)},n.streamCreated=!1,n.fire=function(a){var b=n._events[a],c=Array.prototype.slice.call(arguments,1);if(b)for(var d=0,e=b.length;e>d;d++)b[d].apply(null,c)},n.SERVER=function(){var a=[{iceServers:[{url:"stun:stun.1.google.com:19302"},{url:"turn:119.254.119.3:3478?transport=tcp",username:"class2",credential:"class2"}]},{iceServers:[{url:"stun:stun.1.google.com:19302"},{url:"turn:119.254.119.4:3478?transport=tcp",username:"class2",credential:"class2"}]}],b=[{iceServers:[{url:"stun:stun.1.google.com:19302"},{url:"turn:class2@119.254.119.3:3478?transport=tcp",credential:"class2"}]},{iceServers:[{url:"stun:stun.1.google.com:19302"},{url:"turn:class2@119.254.119.4:3478?transport=tcp",credential:"class2"}]}],c=[];if(c=navigator.mozGetUserMedia?a:b,c.length){var d=parseInt(Math.random()*c.length);return console.log("index "+d),c[d]}return{}},n.peerConnections={},n.connections=[],n.streams=[],n.pc_constraints={optional:[{DtlsSrtpKeyAgreement:!0}]},n.sendOffers=function(){for(var a=0,b=n.connections.length;b>a;a++){var c=n.connections[a];n.sendOffer(c)}},n.onClose=function(a){n.on("close_stream",function(){n.fire("close_stream",a)})},n.createPeerConnections=function(){for(var a=0;a<n.connections.length;a++)n.createPeerConnection(n.connections[a])},n.createPeerConnection=function(a){b.trace("createPeerConnection --  id="+a);var d=n.pc_constraints,e=n.peerConnections[a]=new i(n.SERVER(),d);return e.onaddstream=function(c){b.trace("检测到媒体流连接到本地 onaddstream"),n.fire("add remote stream",c.stream,a)},e.onicecandidate=function(a){b.trace("pc onicecandidate"),a.candidate&&n._socket.send(JSON.stringify({code:232,messageId:c.get(),senderId:me.id,receiverId:you.id,candidate:a.candidate,sessionId:0}))},e.onopen=function(){b.trace("pc open")},e},n.connect=function(a){b.trace("start to connect "+a);try{if(!window.WebSocket)return void b.trace("WebSocket not support");n._socket=new WebSocket(a),n._socket.onopen=function(){b.trace("soket open"),n._socket.send(JSON.stringify({code:1,id:me.id,messageId:c.get(),token:me.token,type:1})),n._socket.send(JSON.stringify({code:105,messageId:c.get(),date:(new Date).getTime(),senderId:me.id,senderName:me.name,receiverId:you.id,receiverName:you.name})),n._socket.onmessage=function(a){b.trace("on message "+a.data);var c=JSON.parse(a.data);n.fire(c.code+"",c)},n._socket.onerror=function(a){b.trace("socket error"+a)},n._socket.onclose=function(){b.trace("soket onclose");var a=you.id;n.fire("disconnect stream",a),"undefined"!=typeof n.peerConnections[a]&&n.peerConnections[a].close(),delete n.peerConnections[a],delete n.connections[a]},n.on("remove_peer_connected",function(){var a=you.id;n.fire("disconnect stream",a),"undefined"!=typeof n.peerConnections[a]&&n.peerConnections[a].close(),delete n.peerConnections[a],delete n.connections[a]})}}catch(d){b.trace("error :"+d)}},n.sendOffer=function(a){var d=n.peerConnections[a];b.trace("sdpConstraints  :"+JSON.stringify(sdpConstraints)),d.createOffer(function(a){a.sdp=e(a.sdp),d.setLocalDescription(a),n._socket.send(JSON.stringify({code:230,messageId:c.get(),senderId:me.id,receiverId:you.id,sessionId:0,agent:"agent",sdp:a}))},function(){b.trace("RTCPeerConnectionErrorCallback failureCallback")},sdpConstraints)},n.receiveOffer=function(a,c){var d=n.peerConnections[a];if(d)b.trace("receiveOffer and pc  is true"),n.addStreams();else{b.trace("receiveOffer and pc  is false"),n.connections.push(a),n.createPeerConnections(),d=n.peerConnections[a];for(var e=0;e<n.streams.length;e++){var f=n.streams[e];d.addStream(f)}}n.sendAnswer(a,c)},n.sendAnswer=function(a,d){var e=n.peerConnections[a];e.setRemoteDescription(new nativeRTCSessionDescription(d)),navigator.webkitGetUserMedia&&(d=sdpConstraints),e.createAnswer(function(a){e.setLocalDescription(a),n._socket.send(JSON.stringify({code:231,messageId:c.get(),senderId:me.id,receiverId:you.id,sessionId:0,agent:"agent",sdp:a}))},function(){b.trace("sendAnswer errorHandler")},d)},n.receiveAnswer=function(a,b){var c=n.peerConnections[a];c.setRemoteDescription(new nativeRTCSessionDescription(b))},n.createStream=function(a,d,e){var f;return d=d||function(){},e=e||function(){},f={video:!!a.video,audio:!!a.audio},k?(b.trace("getUserMedia "+JSON.stringify(f)),void k.call(navigator,f,function(a){var b={code:280,messageId:c.get(),userId:me.id,inviteeId:you.id};n._socket.send(JSON.stringify(b)),n.streams.push(a),d(a)},function(a){b.trace("Could not connect stream."),e(a)})):(b.trace("getUserMedia is failed! "),m(),!1)},n.addStreams=function(){for(var a=0;a<n.streams.length;a++){var c=n.streams[a];for(var d in n.peerConnections)b.trace("addStreams --"+d),n.peerConnections[d].addStream(c)}},n.attachStream=function(a,b){"string"==typeof b&&(b=document.getElementById(b)),navigator.mozGetUserMedia?b.mozSrcObject=a:b.src=j.createObjectURL(a)||a,b.play()},n.on("ready",function(){b.trace("ready fired"),n.createPeerConnections(),n.addStreams(),n.sendOffers()}),ued_conf.mcPath||d.initConfig(),n.connect(ued_conf.mcPath)}),define("module/chatUtil",["common/util","module/webRtc","module/uuid"],function(a,b,c){var d=function(){var a=this;rtc.connectionTimeCount+=1e3,rtc.connectionTimeCount>=rtc.connectionTimeout&&($.alert("您的网络不稳定，请稍后尝试"),$("#video-status").addClass("none"),$(".icon-break").addClass("none")),4==document.getElementById("remoteVideo").readyState?($(".icon-break").removeClass("none"),rtc.isAudio?a.setVideoStatus("status-voice"):$(".video-me").removeClass("none")):setTimeout(function(){a.playDetection()},1e3)},e=function(a){"none"!=a?($("#video-status").removeAttr("class").addClass(a),$(".init-bg").removeClass("none").show()):$(".init-bg").addClass("none")},f=function(){$(".info-content").addClass("none"),$(".cost-content").addClass("none"),$(".video-tips").remove()},g=function(){f(),$(".caption").append('<div class="video-tips no-video-tips">未检测到视频设备<i class="icon-video icon-info-triangle"></i></div>')},h=function(){f(),$(".caption").append('<div class="video-tips no-video-tips">未检测到对方视频设备<i class="icon-video icon-info-triangle"></i></div>')},i=function(){f(),$(".caption").append('<div class="video-tips no-audio-tips">未检测到音频设备<i class="icon-video icon-info-triangle"></i></div>')},j=function(){f(),$(".caption").append('<div class="video-tips no-audio-tips">未检测到对方音频设备<i class="icon-video icon-info-triangle"></i></div>')},k=function(){f(),$(".caption").append('<div class="video-tips no-audio-tips">连接错误<i class="icon-video icon-info-triangle"></i></div>')},l=function(){var a={code:242,senderId:me.id,senderName:me.name,receiverId:you.id,receiverName:you.name,sessionId:0,date:+new Date,messageId:c.get()};rtc._socket.send(JSON.stringify(a)),closePage()},m=function(){var a={code:252,senderId:me.id,senderName:me.name,receiverId:you.id,receiverName:you.name,sessionId:0,date:+new Date,messageId:c.get()};rtc._socket.send(JSON.stringify(a)),closePage()};return videoRequest=function(){a.trace("videoRequest"),e("status-conn-video"),$(".video-me").removeClass("none"),rtc.createStream({video:!0,audio:!0},function(a){rtc.attachStream(a,"localVideo")},function(){rtc.peerConnections={},rtc.connections=[],rtc.streams=[],g(),rtc.onLine?l():rtc.on("105",function(){l()})})},audioRequest=function(){e("status-conn-video"),rtc.createStream({video:!1,audio:!0},function(){$(".video-me").addClass("none")},function(){rtc.peerConnections={},rtc.connections=[],rtc.streams=[],i(),rtc.onLine?m():rtc.on("105",function(){m()})})},hangUp=function(){var a=this,b={};b.code=260,b.senderId=me.id,b.senderName=me.name,b.receiverId=you.id,b.receiverName=you.name,b.date=+new Date,b.sessionId=0,b.messageId=c.get(),rtc._socket.send(JSON.stringify(b)),rtc.peerConnections[you.id]&&rtc.peerConnections[you.id].close(),delete rtc.streams[0],delete rtc.peerConnections[you.id],delete rtc.connections[you.id],rtc.peerConnections={},rtc.connections=[],rtc.streams=[],window.isHangUp=!1,a.reset(),a.closePage()},reset=function(){$(".icon-break").addClass("none"),e("status-end"),$(".video-me").addClass("none")},ab2str=function(a){return String.fromCharCode.apply(null,new Uint16Array(a))},str2ab=function(a){for(var b=new ArrayBuffer(2*a.length),c=new Uint16Array(b),d=0,e=a.length;e>d;d++)c[d]=a.charCodeAt(d);return b},closePage=function(b){a.trace("will  closePage .....");var c=window.localStorage.getItem("dev");return(c="true"==c||"1"==c)?void a.trace("dev module"):(b=parseInt(b||0),void setTimeout(function(){window.close()},1e3*b))},{playDetection:d,videoRequest:videoRequest,audioRequest:audioRequest,hangUp:hangUp,reset:reset,ab2str:ab2str,str2ab:str2ab,hideInfo:f,setVideoStatus:e,closePage:closePage,otherNoVideoTips:h,otherNoAudioTips:j,connectErrTips:k}}),define("module/commentStar",["common/util"],function(a){var b,c,d,e='<span class="icon icon-star-base"><span class="icon"></span></span>',f='<input type="hidden" value="0" name="#{starScore}" id="starScore" />',g=["一星，不满意","两星，不太好","三星，一般般","四星，还不错","五星，相当满意"],h=0,i=function(){for(var c=[],d=0;5>d;d++)c.push(a.template(e,{title:g[d]}));return c.push(a.template(f,{starScore:b})),c.join("")},j=function(a){d=$("#starScore"),c=a.find(".icon-star-base");var b="icon-star-on-red",e="lock";c.each(function(){var a=$(this),f=a.prevAll(c).andSelf(),g=a.nextAll(c);a.hover(function(){g.removeClass(e).find(".icon").removeClass(b),f.find(".icon").addClass(b)},function(){if(h){var f=$(".icon-star-base:lt("+h+")");c.find(".icon").removeClass(b),f.addClass(e),f.find(".icon").addClass(b),d.val(h)}else a.hasClass(e)||c.find(".icon").removeClass(b)}).on("click",function(){f.addClass(e),h=f.length,d.val(h)})})};return{init:function(){var a=arguments[0],c=a.container,d=$(c);b=a.hiddenName,d.append(i()),j(d)}}}),define("module/video",["common/interface","common/util","module/commentStar","module/chatUtil"],function(a,b,c,d){var e=null,f=['<div class="info-top clearfix">','<div class="user-info">','<div class="expert-por">','<a href="#{linkUrl}" target="_blank">','<em></em><img class="img-full" src="#{headImgUrl}" alt="#{userName}" title="#{userName}">',"</a>","</div>",'<div class="country">','<span class="country-flag flag #{countryImgUrl}" title="#{country}"></span>','<img alt="#{gender}" title="#{gender}" src="#{genderImg}" class="country-sex" width="13" height="13">',"</div>","</div>",'<div class="user-right">','<p class="intr-name clearfix">','<span class="user-name"><a href="#{linkUrl}" target="_blank">#{userName}</a></span>','<span class="user-job">#{job}&nbsp;</span>','<span class="user-comment">#{goodCommentNum}</span>',"</p>",'<p class="intr-skills">#{skillTags}</p>',"</div>","</div>",'<div class="user-bottom">','<p title = "#{personalInfo}">#{personalInfo}</p>',"</div>",'<i class="icon-video icon-info-triangle"></i>'].join(""),g=function(){$(".info-content .info-top .user-info").length||(e&&e.abort(),e=b.setAjax(b.strFormat(a.getApiUrl().expertInfoUrl,[you.id]),{},function(a){if(a&&a.status&&200==a.status){var c=function(a){var b="";for(var c in a)b+='<a href="/expertsearch?ft='+encodeURI(a[c])+'" target="_blank">'+a[c]+"</a>";return b},d=b.template(f,{gender:"WOMAN"==a.sex?"女":"男",genderImg:"WOMAN"==a.sex?ued_conf.root+"images/female.png":ued_conf.root+"images/male.png",linkUrl:a.linkUrl,userName:a.userName||"未命名用户",skillTags:c(a.skillTagsArray)||"服务标签暂未设置",country:a.country,countryImgUrl:a.countryImgUrl,headImgUrl:a.headImgUrl,job:a.job||"职业未设置",personalInfo:b.autoAddEllipsis(a.personalInfo,750,null)||"这个家伙很懒，什么都没有留下。",goodCommentNum:a.goodCommentNum?a.goodCommentNum+"个人觉得Ta不错":""});$(".info-content").append(d)}else $(".info-content").append("读取对方信息失败。")},null,"GET"))},h=function(){var a=$("#remoteVideo"),b=$(".voice-control"),c=$(".volume"),e=$(".volume-bar"),f=$(".volume-dot"),h=$(".icon-volume-warp"),i=$(".icon-volume"),j=$(".loading"),k=$(".current"),l=$(".cost-content"),m=$(".icon-info"),n=$(".info-content"),o="icon-volume-shut",p="voice-control-hover",q="icon-volume-hover",r="none",s="click";
a.removeAttr("controls"),j.fadeIn(500),a.on("loadedmetadata",function(){$("#localVideo")[0].volume=0,k.text(v(0)),$(".duration").text(v(a[0].duration)),u(0,100),i.removeClass(o),e.css("height","100%"),f.css("bottom","124px")});i.on(s,function(){$(this).toggleClass(o),0!=a[0].volume?(a[0].volume=0,e.css("height",0),f.css("top","30px")):(a[0].volume=1,e.css("height","100%"),f.css("top","129px"))});var t=!1;$(".volume,.volume-dot").on("mousedown",function(b){t=!0,a[0].muted=!1,i.removeClass("muted"),u(b.pageY)}),$(document).on("mouseup",function(a){t&&(t=!1,u(a.pageY))}),$(document).on("mousemove",function(a){t&&u(a.pageY)});var u=function(b,d){var g;if(d)g=100*d;else{var h=b-c.offset().top;g=100*h/c.height()}g>100&&(g=100),0>g&&(g=0),e.css("height",g+"%"),f.css("top",30+g+"px"),a[0].volume=g/100,0==a[0].volume?i.addClass(o):i.removeClass(o)};b.hover(function(){c.show(),f.show(),b.addClass(p),h.addClass(q)},function(){c.hide(),f.hide(),b.removeClass(p),h.removeClass(q)});var v=function(a){var b=Math.floor(a/60)<10?"0"+Math.floor(a/60):Math.floor(a/60),c=Math.floor(a-60*b)<10?"0"+Math.floor(a-60*b):Math.floor(a-60*b);return b+":"+c};m.on(s,function(a){l.addClass(r),n.toggleClass(r),g(),$(".video-tips").remove(),a.stopPropagation()}),$(document).on(s,function(a){var b=$(a.target);0==b.closest(".video-tips").length&&0==b.closest(".info-content").length&&0==b.closest(".control-span").length&&d.hideInfo(),a.stopPropagation()}),$(".icon-break").on("click",function(){d.hangUp(),$("#vedioBox").remove()})},i=function(){d.closePage(0)};return{init:function(){h(),d.setVideoStatus("status-wait-confirm-video"),$.browser.msie?window.document.body.onbeforeunload=i:(window.onBeforeUnload=i,$("body").attr("onbeforeunload","return onBeforeUnload();"))}}}),define("module/consult",["common/util","module/chatUtil"],function(a,b){var c=window.rtc,d=function(){c.on("add remote stream",function(d){a.trace("add remote stream"),c.attachStream(d,"remoteVideo"),c.isAudio||$("#init").hide(),b.playDetection()}),c.on("disconnect stream",function(c){a.trace("peer connection "+c+" closed"),b.closePage()}),c.on("2",function(){c._socket.send(JSON.stringify({code:2}))}),c.on("20",function(){b.closePage()}),c.on("21",function(){b.closePage()}),c.on("105",function(a){a.senderId==you.id&&a.receiverId==me.id&&(c.onLine=!0)}),c.on("108",function(){}),c.on("211",function(a){a.inviterId==me.id&&a.userId==you.id&&b.setVideoStatus("status-wait-conn-video")}),c.on("212",function(a){a.inviterId==me.id&&a.userId==you.id&&(b.reset(),b.setVideoStatus("status-refuse-video"),c.peerConnections[you.id]&&c.peerConnections[you.id].close(),delete c.peerConnections[you.id],delete c.connections[you.id],c.peerConnections={},c.connections=[],c.streams=[],b.closePage(5))}),c.on("213",function(a){a.userId==me.id&&a.inviteeId==you.id&&(b.setVideoStatus("status-video-time-out"),$(".video-me").addClass("none")),b.closePage(0)}),c.on("221",function(c){c.inviterId==me.id&&c.userId==you.id?b.setVideoStatus("status-wait-conn-voice"):a.trace("221 bind error "+JSON.stringify(c))}),c.on("222",function(a){a.inviterId==me.id&&a.userId==you.id&&(b.reset(),b.setVideoStatus("status-refuse-voice"),c.peerConnections[you.id]&&c.peerConnections[you.id].close(),delete c.peerConnections[you.id],delete c.connections[you.id],c.peerConnections={},c.connections=[],c.streams=[])}),c.on("223",function(a){a.userId==me.id&&a.inviteeId==you.id&&b.setVideoStatus("status-voice-time-out")}),c.on("230",function(b){b.receiverId==me.id&&b.senderId==you.id&&(a.trace("receive Offer  230"),c.receiveOffer(you.id,b.sdp))}),c.on("231",function(b){b.receiverId==me.id&&b.senderId==you.id&&(a.trace("receive Answer  231"),c.receiveAnswer(you.id,b.sdp))}),c.on("232",function(a){if(a.receiverId==me.id&&a.senderId==you.id){var b=new nativeRTCIceCandidate(a.candidate),d=c.peerConnections[you.id];d&&d.addIceCandidate(b)}}),c.on("242",function(a){a.receiverId==me.id&&a.senderId==you.id&&(b.otherNoVideoTips(),b.setVideoStatus("status-init"),$(".video-me").addClass("none"),b.closePage(0))}),c.on("252",function(c){a.trace("252"),c.receiverId==me.id&&c.senderId==you.id&&(b.otherNoAudioTips(),b.setVideoStatus("status-init"),$(".video-me").addClass("none"),b.closePage(0))}),c.on("260",function(a){a.receiverId==me.id&&a.senderId==you.id&&(c.peerConnections[you.id]&&c.peerConnections[you.id].close(),delete c.peerConnections[you.id],delete c.connections[you.id],c.peerConnections={},c.connections=[],c.streams=[],$(".icon-break").addClass("none"),b.setVideoStatus("status-video-you-end"),$(".video-me").addClass("none")),b.closePage()}),c.on("270",function(b){b.inviteeId==you.id&&b.userId==me.id&&a.trace("您正在与其他人视频或者音频通话中！")}),c.on("271",function(){a.trace(you.name+"正在视频或者音频通话中！"),b.closePage(5)}),c.on("280",function(d){switch(d.state){case 0:break;case 1:a.trace("data :"+d),c.connections.push(you.id),c.fire("ready");break;case 2:a.trace("请求超时"),b.setVideoStatus("status-video-time-out"),$(".video-me").addClass("none"),b.closePage(0);break;case 3:a.trace("其它错误"),b.connectErrTips(),b.closePage(0);break;default:b.closePage()}})};return{init:d}}),require(["common/util","module/chatUtil","module/cookie","module/video","module/consult"],function(a,b,c,d,e){window.isHangUP=!0,d.init(),e.init();var f=parseInt(a.location("init"),10);rtc.on("0",function(){switch(f){case 0:case 1:b.videoRequest();break;case 2:case 3:default:rtc.isAudio=!0,window.sdpConstraints.mandatory.OfferToReceiveVideo=!1,b.audioRequest(),b.setVideoStatus("status-wait-confirm-voice")}}),a.onBeforeBomUnload(function(){window.isHangUp&&b.hangUp()})}),define("page/page_consult",function(){});