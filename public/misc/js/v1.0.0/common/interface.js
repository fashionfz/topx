/**
 * @description: 接口
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('common/interface', [], function(){
    var dev = false; // 设置接口模式 true为开发模式 false为真实接口

    return {
        /**
         * 接口连接配置
         */
        getApiUrl : function(){
            var url = {};
            if(dev){
                url = {
                    getLoginUrl : ued_conf.root + 'json/login.json', // 获取登录状态
                    toLoginUrl : ued_conf.root + '/json/login.json', // 登录
                    toRegisterUrl : ued_conf.root + '/json/reg.json', // 注册
                    forgetPwdUrl : ued_conf.root + '/json/forget.json', // 忘记密码
                    editPwdUrl : ued_conf.root + '/json/editPwd.json', // 修改密码
                    checkEmailUrl : ued_conf.root +  '/json/email.json', // 验证邮箱唯一性
                    captchaUrl : ued_conf.root + '/json/captcha.json', // 获取验证码或验证验证码
                    addAttentionUrl : ued_conf.root + '/json/addAttention.json', // 添加关注
                    unAttentionUrl : ued_conf.root + '/json/unAttention.json', // 取消关注
                    commentListUrl : ued_conf.root + '/json/commentList.json', // 评价列表
                    leaveMessageUrl : ued_conf.root + '/json/leaveMessage.json', // 留言
                    searchListUrl : ued_conf.root + '/json/expertVOList.json', // 搜索结果列表
                    saveUserInfoUrl : ued_conf.root + '/json/saveUserInfo.json', // 保存个人信息
                    saveServiceUrl : ued_conf.root + '/json/saveService.json', // 保存服务信息
                    //getOrderDateUrl : ued_conf.root + '/json/getOrderDate.json', // 得到预约时间范围
                    getOrderUsedUrl : '', // 获取已被预约的记录
                    saveOrderDateUrl : '', // 保存预约
                    uploadHeadUrl : ued_conf.root + '/json/upload.json', // 头像上传
                    saveEmailUrl : ued_conf.root + '/json/saveEmail.json', // 保存修改的登陆邮箱
                    savePhoneUrl : ued_conf.root + '/json/savePhone.json', // 保存绑定的手机
                    saveModifyPhoneUrl : ued_conf.root + '/json/saveModifyPhone.json', // 保存修改后的手机
                    checkPhoneUrl : ued_conf.root + '/json/checkPhone.json', // 手机号重复性验证
                    sendPhoneSMSUrl : ued_conf.root + '/json/sendPhoneSMS.json', // 发送手机验证码
                    sendSMSByPhoneUrl : ued_conf.root + '/json/sendPhoneSMSwithoutPara.json', //发送手机验证码，不带参数
                    sendSMSByEmail : ued_conf.root + '/json/sendSMSByEmail.json', //通过邮箱修改手机号码
                    saveNewPwdUrl : ued_conf.root + '/json/saveNewPwd.json', // 修改密码
                    saveTipsUrl : ued_conf.root + '/json/saveTips.json', // 保存修改提示信息
                    saveAppLoginUrl : ued_conf.root + '/json/saveTips.json', // 保存第三方登录设置信息
                    saveBookingTipsUrl : ued_conf.root + '/json/saveTips.json', // 保存预约提示信息
                    userMegUrl : ued_conf.root + '/user/user-message.shtml', // 我的消息地址
                    sendMsgUrl : ued_conf.root + '/consult/sendMsg.shtml', // 聊天页面发送消息接口
                    getMsgUrl : ued_conf.root + '/json/getMsg.json', // 聊天页面接受消息接口
                    getTagsUrl : '', // 联想匹配标签
                    getTradeListUrl : '', // 交易列表
                    getTradeShowSNSUrl : '', // 显示SNS分享
                    shareTradeSNSUrl : '', // 交易SNS分享
                    deleteTradeUrl : '', // 删除交易
                    getMessageListUrl : '', // 消息列表
                    deleteMessageUrl : '', // 删除消息
                    getMessageCountUrl : '', // 实时获取消息
                    updateMessageUrl : '', // 更新消息为已读
                    getConsultPollUrl : '', // 反馈咨询信息
                    getCommentListUrl : '/comment/expertComments', // 获取评价列表
                    saveCommentUrl : '', // 保存评价
                    saveNewCommentUrl : '', // 新保存评价
                    addReplyUrl : '/comment/addReply', // 添加评价回复
                    cancelTradeUrl : '/user/usertrade/cancel', // 取消预约
                    consultUserUrl : 'http://192.168.0.195:9000/host/{0}/{1}',  // 用户咨询视频跳转页面
                    consultExportUrl : '/chat/{0}',  // 专家咨询视频跳转页面
                    consultTradeOnlineUrl : '/user/usertrade/unConsult',  // 在线咨询确认页面
                    getFbDataUrl : ued_conf.root + '/json/getFbData.json',  // 获取用户投诉所需数据：用户+交易
                    saveFeedbackUrl : ued_conf.root + '/json/saveFeedback.json',  // 保存反馈信息
                    saveComplainUrl : '', // 保存投诉信息
                    allExpertUrl : ued_conf.root + '/json/allExpert.json',  // 所有专家
                    uploadUrl : ued_conf.root + '',  // 投诉反馈凭证上传
                    getVideoStatusUrl : '', // 查询用户视频状态
                    rejectVideoUrl : '', // 专家拒绝视频请求
                    getTypeTagsUrl : ued_conf.root + '/json/getTags.json', // 获取行业下的标签
                    clearCookieUrl : '',
                    expertInfoUrl : '/expert/expertInfo/', // 视频页面专家信息
                    sendEmail: "/usersetting/sendEmail",
                    phoneMailSuccess : "/usersetting/phonemailsuccess",
                    moreChatRecordUrl : ued_conf.root + '/json/moreChatRecord.json', // 更多聊天记录
                    beforeBeginCharge : '/chat/begin/{0}',                       //发起计费之前请求
                    getNewMsgUrl : '', //获取最新未读消息
                    updateMsgUrl : '', //通知其他页面更新消息
                    getSelfInChatUrl : '', //获取自己是否在聊天中
                    commentReplyUrl : '', // 跳转到评价/回复评价页面
                    getTranslate : '', // 翻译
                    addGroupByTranslate : '',
                    getGroupMember : '',
                    updateChatMsgUrl : '',
                    getChatUser : ued_conf.root + 'json/chatUser.json', // 获取聊过天的人
                    getChatByOption: '',
                    inviteFriends: '', // 邀请加入圈
                    addFriend:   '', //添加到圈
                    deleteFriend:  '', //删除圈中好友
                    queryChatDetail:    '', //查询聊天详细
                    uploadAvatar : "", //上传群组头像;
                    uploadGroupBack: "",//#群组 - 上传头部背景
                    addGroup : "", // 创建群组
                    inviteJoinGroup : '', // 邀请入群
                    queryGroups : "", //查询群组
                    queryJoinGroups : '', //查询群组--加入的
                    queryGroupById: "", //根据id查询群组
                    exitGroup : '', // 退出群组
                    deleteGroupMember: '',//删除群成员
                    addDiscussionUrl : ued_conf.root + 'json/addDiscussion.json' //创建多人会话/讨论组
            };
            }else{
                url = {
                    getLoginUrl : '/login',
                    toLoginUrl : '/login',
                    toThirdLoginUrl : '/user/auth/bindAccountLogin',
                    toRegisterUrl : '/register',
                    toThirdRegisterUrl : '/user/auth/newAccountLogin',
                    toThirdLoginNoAccountUrl : '/user/auth/directLogin',
                    forgetPwdUrl : '/forgetpwd',
                    editPwdUrl : '/resetpwd',
                    checkEmailUrl : '/emailexists',
                    captchaUrl : '/captcha',
                    addAttentionUrl : '',
                    unAttentionUrl : '',
                    commentListUrl : '',
                    leaveMessageUrl : '',
                    searchListUrl : '/expertsearch',
                    saveUserInfoUrl : '/user/userdetail/personalinfo',
                    saveServiceUrl : '/user/userdetail/serviceinfo',
                    //getOrderDateUrl : ued_conf.root + '/json/getOrderDate.json', // 得到预约时间范围
                    getOrderDateUrl : '/expert/record/{0}',
                    getOrderUsedUrl : '/expert/record/used/{0}/{1}',
                    saveOrderDateUrl : '/expert/record',
                    uploadHeadUrl : '/user/uploadavatar',
                    saveEmailUrl :'/user/usersetting/changeEmail',
                    savePhoneUrl : '/user/usersetting/bindMobilePhone',
                    saveModifyPhoneUrl: '/user/usersetting/updateMobilePhone', // 保存修改后的手机
                    checkPhoneUrl : '/user/usersetting/phoneNumExists',
                    sendPhoneSMSUrl : '/user/usersetting/sendPhoneVerificationCode',
                    sendSMSByPhoneUrl : '/user/usersetting/sendVerificationCodeByPhone', //发送手机验证码，不带参数
                    sendSMSByEmail: '/user/usersetting/sendVerificationCodeByNewPhone', //通过邮箱修改手机号码
                    saveNewPwdUrl : '/user/usersetting/changePassword',
                    saveTipsUrl : '/user/usersetting/modifySafetyReminder',
                    saveAppLoginUrl : '/user/usersetting/completeUserInfo',
                    saveBookingTipsUrl : '/user/usersetting/modifyBookingReminder',
                    userMegUrl : '/user/usermessage',
                    sendMsgUrl : '/consult/sendMsg', // 聊天页面发送消息接口
                    getMsgUrl : ued_conf.root + 'json/getMsg.json', // 聊天页面接受消息接口
                    getTagsUrl : '/skilltags/query',
                    getTradeListUrl : '/user/usertrade',
                    getTradeShowSNSUrl : '/user/usertrade/getShareInfo',
                    shareTradeSNSUrl : '/user/usertrade/shareInfo',
                    deleteTradeUrl : '/user/usertrade/delete',
                    getChatRecUrl: "/msg/queryChatMessage",  //#聊天记录 {page: ,pageSize:,chatType  1:人 2：群组,msgType: 'no_system'}
                    getMessageListUrl : '/msg/query',
                    deleteMessageUrl : '/msg/delete',
                    getMessageCountUrl : '/msg/poll',//'http://sms.test.com:9000/msg/poll?callback=?',
                    updateMessageUrl : '/msg/updateread',
                    getConsultPollUrl : '/msg/consultClear',
                    getCommentListUrl : '/comment/list',
                    saveCommentUrl : '/comment/addComment',
                    saveNewCommentUrl: '/comment/saveComment',
                    addReplyUrl : '/comment/addReply',
                    cancelTradeUrl : '/user/usertrade/cancel',
                    consultTradeUserUrl : '/consult/beginReserve',
                    consultExportUrl : '/chat/{0}',
                    consultTradeExportUrl : '/user/usertrade/accepted/{0}',
                    consultTradeOnlineUrl : '/user/usertrade/unConsult',
                    getFbDataUrl : '/feedback/beforeComplain',
                    //getFbDataUrl : ued_conf.root + '/json/getFbData.json',  // 获取用户投诉所需数据：用户+交易
                    saveFeedbackUrl : '/feedback/suggestion',
                    saveComplainUrl : '/feedback/complain',
                    allExpertUrl : '/skilltags/tagmore',
                    uploadUrl : '/feedback/uploadproof',
                    getTypeTagsUrl : '/skilltags/change',
                    clearCookieUrl : '/forgetMe',
                    expertInfoUrl : '/expert/expertInfo/{0}',
                    sendEmail: "/user/usersetting/sendEmail",
                    phoneMailSuccess : "/user/usersetting/phonemailsuccess",
                    moreChatRecordUrl : '/chat/chatMsg/chatMsgQuery',
                    beforeBeginCharge : '/chat/begin/{0}',
                    getNewMsgUrl : '/msg/new',//获取最新未读系统消息
                    getNewChatMsgUrl : '/msg/chatMsgUnReadInfo',//获取最新未读聊天消息
                    updateMsgUrl : '/msg/notice',//通知其他页面更新消息
                    getSelfInChatUrl : '/chat/whetherInChat/{0}/{1}', //判断对方是否在线  deprecated
                    getYouInChatUrl : '/chat/whetherOnline/{0}',  //判断对方是否打开socket
                    commentReplyUrl : '/comment/detail/{0}',
                    addFavorite: '/user/addFavorite/{0}',
                    deleteFavorite: '/user/deleteFavorite/{0}',
                    queryFavorite: '/user/favorite/queryFavorite',
				    getTranslateUrl : '/tl/autotl',
                    addGroupByTranslate : '/msg/group/addUserToGroup/{0}/{1}', //新建翻译模式群
                    addMultiCommunicate : '/user/group/createMultiCommunicate', //新建多人会话/讨论组
                    updateMultiCommunicate : '/user/group/updateMultiCommunicateName', //修改多人会话名称
                    getGroupMember : '/msg/group/queryUserUnderGroup/{0}', //获取群成员
	                getGroupMemberByPage: '/msg/group/queryMemberUnderGroup',//查询群组下面的成员 (提供分页)
	                getTranslatorUrl : '/msg/queryCustomerServices', // 获取翻译中心翻译者
                    updateChatMsgUrl : '/chat/cleanChatNum/{0}/{1}', //更新与某人的消息为已读
                    updateGroupMsgUrl : '/chat/cleanGroupChatNum/{0}/{1}', //更新与某群的消息为已读
                    getChatUser : "/msg/queryRelationship", // 获取聊过天的人
                    getChatByOption : '/msg/queryChatMessageByCondition', //查询聊天记录 by condition
                    inviteFriends : '/user/at/inviteFriends', // 邀请加入圈
                    addFriend :   '/user/at/addFriend', //添加到圈
                    queryFriends :  '/user/at/queryFriends', //査圈中好友  {page, pageSize}
                    deleteFriend :  '/user/at/deleteFriend/{0}/{1}', //删除圈中好友/user/at/deleteFriend/:friendId/:isBothDeleted
                    queryChatDetail : '/msg/queryChatMessageContext', //查询聊天上下文详细
                    uploadAvatar : "/user/group/uploadavatar", //上传群组头像;
                    uploadGroupBack: "/user/group/uploadHeadBackGround",//#群组 - 上传头部背景
                    addGroup : "/user/group/createOrUpdateGroup", // 创建群组
                    queryGroups : "/user/group/queryCreatedGroups", //查询群组 --建立的
                    queryJoinGroups : '/user/group/queryJoinedGroups', //查询群组--加入的
                    queryGroupById : "/user/group/queryGroup/{0}",//根据id查询群组
                    deleteGroup : "/user/group/deleteGroup/{0}",// 删除群    /user/group/deleteGroup/:groupId
                    exitGroup : '/user/group/quitGroup/{0}', // 退出群组
                    addMembers :"/msg/group/appendMemberToGroup", // 成员加群
                    inviteJoinGroup : '/groupmsg/invitmembers', // 邀请入群
                    queryTempGroups: "/user/group/queryTempGroups", //查询临时群组     {page  pageSize}
                    saveResume:      "/resume/personalinfo",  //保存海外简历
                    deleteGroupMember: '/user/group/removeMember', //删除群成员
                    groupAgreeApply: "/groupmsg/agreeApply",       //同意入群申请
                    groupAgreeInvite: "/groupmsg/agreeInvit",     //同意群主的邀请
                    groupReject: "/groupmsg/reject",  //拒绝入群申请
				addEnResume: "/resume/addTaskForChinese", //发布海外简历
                    queryService: "/user/service/queryServices", //服务查询  searchText不传查全部
                    queryRequire: '/user/require/queryRequires',//需求查询  searchText不传查全部
                    addService: '/user/service/createOrUpdateService', //添加编辑服务
                    addRequire: '/user/require/createOrUpdateRequire', //添加编辑需求
                    deleteService: '/user/service/delete/{0}',  //删除服务
                    deleteRequire: '/user/require/delete/{0}', // 删除需求
                    getGroupDetailList: '/group/memberList',  //群组详情页面
                    getGroupHomeList: "/groupList",  //群组首页获取列表
                    applyJoinGroup: "/groupmsg/apply",  //申请加入群
                    requireHomeList: "/require",  //需求首页
                    serviceHomeList: "/services",
                    getDeveloperRequireList: "/user/require/requiresOfTa",  //个人详情页面获取需求列表
                    getDeveloperServiceList: "/user/service/servicesOfTa",  //个人详情页面获取服务列表
                    getMsgUnread: "/msg/queryUnread",  //消息面板获取未读消息
                    markUnreadMsg: "/msg/updateread",  //标记消息面板未读消息
                    requireSearch: "/require/search",  //需求搜索
                    serviceSearch: "/services/search"
                }
            }
            return url;
        }
    }
});