/**
 * @description: 消息中心模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/msgSocket', [
    'common/interface',
    'common/util',
    'module/uuid',
    'module/msgLS',
    'module/cookie',
    'module/imTips',
    'module/mc'
], function(inter, util, uuid, msgLS, cookie, imTips, mc){
    var oldTitle = $('title').text(),
        winStatus = true,
        statusTimer = null,
        existTitle = false,
        closeTimer = null,
        msgTimer = null,
        flickTimer = true,
        isRequestMsg = true,
        askNotify = false,
        isOpenNotify = false,
        isTipTitle = false,
        tpl = [
            '<div class="home-num #{cls}" title="#{msgTitle}">', // 头像上加样式：home-num-trade
                '<div class="index-icon left"></div>',
                '<div class="center">',
                    '<span class="home-num-txt">#{msgNum}</span>',
                '</div>',
                '<div class="index-icon right"></div>',
            '</div>'
        ].join('');

    window.isOpenChat = {};

    return {
        /**
         * 初始化WebSocket
         */
        init : function(token){
            var self = this;
            self.curUid = parseInt(cookie.get('_u_id')) || 0;
            self.isOpenChat = cookie.get('isOpenChat'+self.curUid) || {};
            self.token = token || cookie.get('_u_tk') || '';
            self.socket = {};
            util.trace('用户ID：'+self.curUid);
            imTips.init();
            self.getDataForSelf();
            self.initSocket();
            self.setWindowFocus();
        },
        /**
         * 实例化WebSocket
         */
        initSocket : function(){
            var self = this;

            self.socket = {};

            try {
                if(self.socket.readyState === undefined || self.socket.readyState > 1) {
                    if(!ued_conf.mcPath){
                        mc.initConfig();
                    }
                    self.socket = new WebSocket(ued_conf.mcPath);
                    self.socket.onopen = function(){
                        self.socketOnOpen();
                    };
                    self.socket.onmessage = function(data){
                        self.socketOnMessage(data.data);
                    };
                    self.socket.onerror = function(e){
                        self.socketOnError(e);
                    };
                    self.socket.onclose = function(){
                        self.socketOnClose();
                    };
                }
            } catch (exception){
                /**
                 * 启用IE插件
                 */
                if( $.browser.msie ){
                    $('body').append('<object name="peerconnect_client_ocx" classid="clsid:963B7657-E3D7-4C8F-A8D3-C5FB2AFE37A8" id="peerconnect_client_ocx" class="none" codebase="'+ued_conf.cdnPath+'peerconnection.cab")"></object>');
                    self.socket_ocx = document.all.peerconnect_client_ocx;//$('#peerconnect_client_ocx')[0];
                    if(self.socket_ocx && self.socket_ocx.WebSocket_Verify){
                        if(!ued_conf.mcPath){
                            mc.initConfig();
                        }
                        self.socket_ocx.WebSocket_Open(ued_conf.mcPath+'/');
                        self.socket_ocx.attachEvent('WebSocket_OnOpen', function(){
                            self.socketOnOpen();
                        });
                        self.socket_ocx.attachEvent('WebSocket_onMessage', function(data){
                            self.socketOnMessage(data);
                        });
                        self.socket_ocx.attachEvent('WebSocket_OnError', function(e){
                            self.socketOnError(e);
                        });
                        self.socket_ocx.attachEvent('WebSocket_OnClose', function(){
                            self.socketOnClose();
                        });
                    }else{
                        $.alert('允许安装IE专用插件可以获得更好体验，安装完成后请<a href="javascript:location.reload()">刷新页面。</a><br>无法安装？<a href="'+ued_conf.cdnPath+'peerconnection.cab" target="_blank">点击下载插件</a>手动安装。');
                    }
                }
            } finally {
                window.socket = self;
            }
        },
        /**
         * socket 连接成功事件回调函数
         */
        socketOnOpen : function(){
            var self = this;
            window.sendChatArr = {};
            self.startSocketTime = new Date().getTime();
            self.sendData(JSON.stringify({
                "code": 1,
                    "messageId": uuid.get(),
                    "id": self.curUid,
                    "token": self.token,
                    "type": 0
            }));
            self.getUnreadChatMsg();
            util.trace('Socket消息 socket open', 'blue');
            /*if($('.register_success').length){//注册成功推送欢迎消息
                util.setAjax(inter.getApiUrl().welcomeMsgUrl, {})
            }*/
        },
        /**
         * socket 收到消息事件回调函数
         */
        socketOnMessage : function(data){
            var self = this;
            try{
                util.trace('Socket消息 return Data:'+ data, 'blue');
                self.checkData($.parseJSON(data));
            }catch (e){
                throw e;
                util.trace('Socket消息 格式化json错误：'+ data, 'blue');
            }
        },
        /**
         * socket 发生错误事件回调函数
         */
        socketOnError : function(e){
            var self = this;
            util.trace('Socket消息 socket error', 'blue');
            e && util.trace(e);

            setTimeout(function(){
                if(window.webIM){
                    //当socket断开后 ,恢复状态
                    window.webIM.notice('notice','','通讯中断，请稍后重试！',4,false);
                    window.webIM.changeVideo(true);
                }
            },1000);
        },
        /**
         * socket 关闭事件回调函数
         */
        socketOnClose : function(){
            var self = this;
            util.trace('Socket消息 socket close', 'blue');

            setTimeout(function(){
                if(window.webIM){
                    //当socket断开后 ,恢复状态
                    window.webIM.changeVideo(true);
                }
            },1000);
        },
        /**
         * 发送socket数据
         */
        sendData : function(data, errCall){
            var self = this,
                jsonData = JSON.parse(data) || {},
                state = self.socket_ocx ? self.socket_ocx.WebSocket_GetSocketState : self.socket.readyState;
            if( state == 1 ){
                if( self.socket_ocx ){
                    self.socket_ocx.WebSocket_Send(data);
                }else{
                    self.socket.send(data);
                }
                if ( jsonData.code == 3 || jsonData.code == 305 ){
                    jsonData.sendTime = new Date().getTime();
                    window.sendChatArr[jsonData.messageId] = {
                        sendTime: jsonData.sendTime,
                        data: jsonData
                    };
                    if(jsonData.parentId){
                        msgLS.edit(jsonData.parentId, function(ld){
                            ld.subSenderId = jsonData.senderId;
                            ld.subName = jsonData.senderName;
                            ld.subDate = jsonData.data;
                            return ld;
                        });
                    }else{
                        msgLS.save(jsonData);
                    }
                }
                util.trace('Socket消息 send Data:'+data, 'blue');
            }else{
                util.trace('Socket消息 socket连接异常', 'blue');
                errCall && errCall();
            }
        },
        /**
         * 数据业务处理
         */
        checkData : function(json){
            var self = this,
                senderWin = null,
                receiverWin = null,
                chatPage = $('.J-chat-page');
            if ($.isEmptyObject(json)) {
            } else {
                switch (json.code) {
                    case 0: // 上报消息
                        break;
                    case 2: // 心跳消息
                        self.sendData(JSON.stringify({"code": 2}));
                        break;
                    case 3: // 聊天消息
                        self.setNewChatMsg(json);
                        self.getDataForSelf();
                        self.getUnreadChatMsg();
                        if(json.senderId){
                            msgLS.save(json);
                            self._notify({
                                img: util.getAvatar(json.senderId),
                                imgUrl: '/expert/detail/'+json.senderId,
                                title: json.senderName + '发来消息',
                                content: json.data,
                                url: '/expert/detail/'+json.senderId
                            });
                        }
                        break;
                    case 5: // 回执消息
                        var isTl = json.parentId && json.parentId != '0';
                        if(isTl){
                            if(window.webIM){
                                window.webIM.setChatSendSuc(json.parentId);
                            }
                        }else{
                            if(window.webIM){
                                window.webIM.setChatSendSuc(json.messageId);
                            }
                            msgLS.edit(json.messageId, function(lsData){
                                lsData.sendTime = json.date;
                                return lsData;
                            });
                        }
                        delete window.sendChatArr[json.messageId];
                        break;
                    case 8: // 文件流上传成功返回消息
                        json.finish && msgLS.save(json);
                        if (window.remain && window.webIM) {
                            window.webIM.onRead(null, window.remain);
                            var chatMain = $('.chat-main[data-uuid="'+ json.messageId +'"]');
                            if(chatMain.length){
                                chatMain.find('.file-pic a').prop('href', json.url);
                            }
                        }
                        break;
                    case 11: // 文件消息
                        msgLS.save(json);
                        self.setNewChatMsg(json);
                        self.getUnreadChatMsg();
                        self._notify({
                            img: util.getAvatar(json.senderId),
                            imgUrl: '/expert/detail/'+json.senderId,
                            title: json.senderName + '发来一个文件',
                            content: json.fileName,
                            url: '/expert/detail/'+json.senderId
                        });
                        break;
                    case 13: // 语音消息
                        msgLS.save(json);
                        self.setNewChatMsg(json);
                        self.getUnreadChatMsg();
                        self._notify({
                            img: util.getAvatar(json.senderId),
                            imgUrl: '/expert/detail/'+json.senderId,
                            title: json.senderName + '发来语音消息',
                            content: '打开网页查看语音消息',
                            url: '/expert/detail/'+json.senderId
                        });
                        break;
                    case 15: // 图片消息
                        msgLS.save(json);
                        self.setNewChatMsg(json);
                        self.getUnreadChatMsg();
                        self._notify({
                            img: util.getAvatar(json.senderId),
                            imgUrl: '/expert/detail/'+json.senderId,
                            title: json.senderName + '发来了一张图片',
                            content: '打开网页查看图片',
                            url: '/expert/detail/'+json.senderId
                        });
                        break;
                    case 20: // 退出登录
                        if(window.webIM){
                            window.webIM.notice();
                            window.webIM.changeVideo(true);
                        }

                        self.socket.close();
                        self.socket.onclose = function () {
                            util.trace('Socket消息 socket close', 'blue');
                            clearTimeout(closeTimer);
                            util.trace('Socket消息 clear reconnect', 'blue');
                            return;
                        };
                        break;
                    case 21: // 异常下线
                        if(window.webIM){
                            window.webIM.notice();
                            window.webIM.changeVideo(true);
                        }

                        self.offlineTips();
                        self.socket.close();
                        self.socket.onclose = function () {
                            util.trace('Socket消息 socket close', 'blue');
                            clearTimeout(closeTimer);
                            util.trace('Socket消息 clear reconnect', 'blue');
                            return;
                        };
                        break;
                    case 103: // 系统消息
                        //把消息写入IM
                        /*
                        [/audio_invite_mc] -> 发起语音通话邀请 | Audio calls
                        [/video_invite_mc] -> 发起视频通话邀请 | Video calls
                        [/audio_invite_timeout_mc] -> 发起的语音通话请求超时 | Audio call timeout
                        [/video_invite_timeout_mc] -> 发起的视频通话请求超时 | Video call timeout
                        [/av_hangup_mc] -> 已挂断通话 | hung up
                        */
                        if(json.data == '[/audio_invite_mc]'){
                            json.data = json.senderName+'发起语音通话邀请'
                        }else if(json.data == '[/video_invite_mc]'){
                            json.data = json.senderName+'发起视频通话邀请'
                        }else if(json.data == '[/audio_invite_timeout_mc]'){
                            json.data = json.senderName+'发起的语音通话请求超时'
                        }else if(json.data == '[/video_invite_timeout_mc]'){
                            json.data = json.senderName+'发起的视频通话请求超时'
                        }else if(json.data == '[/av_hangup_mc]'){
                            json.data = json.senderName+'已挂断通话'
                        }
                        self.setNewChatMsg(json);
                        //获取最新未读消息条数
//                        self.getDataForSelf(json.code);
                        if(json.senderId){
                            msgLS.save(json);
                            self._notify({
                                img: util.getAvatar(json.senderId),
                                imgUrl: '/expert/detail/'+json.senderId,
                                title: json.senderName + '发来消息',
                                content: json.data,
                                url: '/expert/detail/'+json.senderId
                            });
                        }
                        break;
                    case 105: // 进入聊天室消息
                        senderWin = isOpenChat[self.curUid + 'to' + json.receiverId];
                        receiverWin = isOpenChat[self.curUid + 'to' + json.senderId];

                        if (self.curUid == json.senderId) {
                            if (!senderWin) {
                                senderWin = {};
                            }
                            senderWin.self = 1;
                            isOpenChat[self.curUid + 'to' + json.receiverId] = senderWin;
                        } else if (self.curUid == json.receiverId) {
                            if (!receiverWin) {
                                receiverWin = {};
                            }
                            receiverWin.other = 1;
                            isOpenChat[self.curUid + 'to' + json.senderId] = receiverWin;
                        }
                        //self.showMsgBox(0, json);
                        break;
                    case 106: // 打开聊天窗口
                        self.hideMsgBox();
                        break;
                    case 107: // 忽略进入聊天室
                        self.hideMsgBox();
                        break;
                    case 108: // 下线消息

                       /* senderWin = isOpenChat[self.curUid + 'to' + json.receiverId];
                        receiverWin = isOpenChat[self.curUid + 'to' + json.senderId];

                        if (self.curUid == json.senderId) {
                            if (!senderWin) {
                                senderWin = {};
                            }
                            senderWin.self = 0;
                            isOpenChat[self.curUid + 'to' + json.receiverId] = senderWin;
                        } else if (self.curUid == json.receiverId) {
                            if (!receiverWin) {
                                receiverWin = {};
                            }
                            receiverWin.other = 0;
                            isOpenChat[self.curUid + 'to' + json.senderId] = receiverWin;
                        }*/
                        //self.showMsgBox(1, json);
                        break;
                    case 210 : //视频邀请

                        var datatime = (new Date().getTime());
                        cookie.set("senderId", json.userId);
                        cookie.set("senderName", json.userName);
                        cookie.set("210", datatime);

                        if(window.webIM){
                            window.webIM.notice('invite-video',json.userName);
                            window.webIM.changeVideo(false);
                        }
                        self.chatTipAnimate('收到“'+ util.autoAddEllipsis(json.userName, 10) +'”的视频会话邀请', 1);
                        window.inviterTiper = util.countdown(null, 60, function(){
                            self.chatTipAnimate('', 0);
                        },null,'chatTipAnimate');
                        break;
                    case 211 : //接受视频
                        if(window.webIM){
                            window.webIM.notice();
                            //打开视频窗口
                            window.webIM.openVideo(0);

                            window.webIM.changeVideo(false);
                        }
                        break;
                    case 212 : //拒绝视频
                        if(window.webIM){
                            util.clearCountdown();
                            window.webIM.notice()
                            window.webIM.changeVideo(true);
                        }
                        break;
                    case 213 : //视频超时
                        if(window.webIM){
                            window.webIM.notice();
                            window.webIM.changeVideo(true);
                        }
                        break;
                    case 220 : //语音邀请
                        var datatime = (new Date().getTime());

                        cookie.set("senderId", json.userId);
                        cookie.set("senderName", json.userName);
                        cookie.set("220", datatime);

                        if(window.webIM){
                            window.webIM.notice('invite-voice', json.userName);
                            window.webIM.changeVideo(false);
                        }
                        self.chatTipAnimate('收到“'+ util.autoAddEllipsis(json.userName, 10) +'”的语音会话邀请', 1);
                        window.inviterTiper = util.countdown(null, 60, function(){
                            self.chatTipAnimate('', 0);
                        },null,'chatTipAnimate');
                        break;
                    case 221 : //接受音频
                        if(window.webIM){
                            window.webIM.notice()
                            //打开音频窗口
                            window.webIM.openVideo(2);
                            window.webIM.changeVideo(false);
                        }
                        break;
                    case 222 : //拒绝音频
                        if(window.webIM){
                            util.clearCountdown();
                            window.webIM.notice( );
                            window.webIM.changeVideo(true);
                        }
                        break;
                    case 223 : //音频超时
                        if(window.webIM){
                            window.webIM.notice();
                            window.webIM.changeVideo(true);
                        }
                        break;
                    case 242 : //
                        if(window.webIM){
                            window.webIM.changeVideo(true);
                            if(json.senderId == me.id && json.receiverId == you.id){
                                window.webIM.notice('notice', json.userName, '您没有可调用的视频设备,或调用失败!');
                                cookie.del('210');
                            }else{
                                window.webIM.notice('notice', json.userName, '对方没有可调用的视频设备,或调用失败!');
                            }
                        }
                        break;
                    case 252 : //
                        if(window.webIM){

                            window.webIM.changeVideo(true);
                            if(json.senderId == me.id && json.receiverId == you.id){
                                window.webIM.notice();
                                cookie.del('220');
                            }else{
                                window.webIM.notice('notice', json.userName, '对方没有可调用的音频设备,或调用失败!');
                            }
                        }
                        break;

                    case 260 : //挂断视频
                        //清除视频/音频提示
                        cookie.del('210');
                        cookie.del('220');
                        if(window.webIM){
                            window.webIM.changeVideo(true);
                            util.clearCountdown();
                            if(json.senderId == me.id && json.receiverId == you.id){
                                window.webIM.notice();
                            }else{
                                window.webIM.notice();
                            }
                        }
                        break;
                    case 270 : //自己正在视频中
                        cookie.del('210');
                        cookie.del('220');
                        if(window.webIM){
                            var name = cookie.get('inviteeName'),
                                content = '正在语音或视频会话中,请先关闭该会话';
                            if(name && name != json.inviteeName){
                                content = '正在和<strong class="user-name">'+name+'</strong>语音或视频会话中,请先关闭该会话'
                            }
                            window.webIM.notice('notice','',content,4);
                            window.webIM.changeVideo(false);
                        }
                        break;
                    case 271 : //对方正在视频中
                        if(window.webIM){
                            window.webIM.notice();
                            window.webIM.notice('notice','',
                                '<strong class="user-name">'+ json.inviteeName +'</strong>正在音频或视频中,请稍后!', 4, false);

                            window.webIM.changeVideo(true);

                        }
                        break;
                    case 305 : //群聊天消息
                        if(json.type == 2){
                            msgLS.edit(json.parentId, function(ld){
                                ld.subSenderId = json.senderId;
                                ld.subName = json.senderName;
                                ld.subDate = json.data;
                                return ld;
                            });
                        }else{
                            msgLS.save(json);
                        }
                        self.setNewChatMsg(json);
                        self.getUnreadChatMsg();
                        self._notify({
                            img: util.getAvatar(json.senderId),
                            imgUrl: '/expert/detail/'+json.senderId,
                            title: '群消息提醒',
                            content: json.data,
                            url: '/expert/detail/'+json.senderId
                        });
                        break;
                    case 307: //群通知消息
                        try{
                            json.groupTips = $.parseJSON(json.message);
                        }catch (e){

                        }finally{
                            switch (json.type){
                                case 0:
                                    json.groupType = 'normal';
                                    break;
                                case 1:
                                    json.groupType = 'multicommunicate';
                                    break;
                                case 2:
                                    json.groupType = 'translate';
                                    break;
                                default:
                                    break;
                            }
                            if(json.state){
                                switch (json.state){
                                    case 10: // 创建群组
                                        json.message = json.groupTips.memberName + '创建群组：' + json.groupName;
                                        break;
                                    case 11: // 邀请成员
                                        json.message = '邀请'+ json.groupTips.memberName +'加入群组：' + json.groupName;
                                        break;
                                    case 12: // 加入群组
                                        json.message = json.groupTips.memberName + '加入该群组';
                                        break;
                                    case 13: // 退出群组
                                        json.message = json.groupTips.memberName + '退出该群组';
                                        break;
                                    case 14: // 移除成员
                                        json.message = json.groupTips.memberName + '被群主移出该群组';
                                        break;
                                    case 15: // 解散群组
                                        json.message = '该群组被群主'+ json.groupTips.memberName +'解散';
                                        break;
                                    default:
                                        break;
                                }
                                if(json.state == 9 || json.state >= 15 || ((json.state == 13 || json.state == 14) && json.groupTips.memberId == self.curUid)){// && json.senderId != self.curUid

                                }else{
                                    msgLS.save(json);
                                }
                                self.setNewChatMsg(json);
                                self.getUnreadChatMsg();
                                self._notify({
                                    img: util.getAvatar(json.senderId),
                                    imgUrl: '/expert/detail/'+json.senderId,
                                    title: '群消息提醒',
                                    content: json.message,
                                    url: '/expert/detail/'+json.senderId
                                });
                            }
                        }
                        break;
                    case 308: // 发送群文件消息
                        json.finish && msgLS.save(json);
                        if (window.remain && window.webIM) {
                            window.webIM.onRead(null, window.remain);
                            var chatMain = $('.chat-main[data-uuid="'+ json.messageId +'"]');
                            if(chatMain.length){
                                chatMain.find('.file-pic a').prop('href', json.url);
                            }
                        }
                        break;
                        break;
                    case 311: // 群文件消息
                        msgLS.save(json);
                        self.setNewChatMsg(json);
                        self.getUnreadChatMsg();
                        self._notify({
                            img: util.getAvatar(json.senderId),
                            imgUrl: '/expert/detail/'+json.senderId,
                            title: '群消息提醒',
                            content: json.senderName + '分享了一个文件',
                            url: '/expert/detail/'+json.senderId
                        });
                        break;
                    case 313: // 群语音消息
                        msgLS.save(json);
                        self.setNewChatMsg(json);
                        self.getUnreadChatMsg();
                        self._notify({
                            img: util.getAvatar(json.senderId),
                            imgUrl: '/expert/detail/'+json.senderId,
                            title: '群消息提醒',
                            content: '收到 ' + json.senderName + ' 的语音消息',
                            url: '/expert/detail/'+json.senderId
                        });
                        break;
                    case 315: // 群图片消息
                        msgLS.save(json);
                        self.setNewChatMsg(json);
                        self.getUnreadChatMsg();
                        self._notify({
                            img: util.getAvatar(json.senderId),
                            imgUrl: '/expert/detail/'+json.senderId,
                            title: '群消息提醒',
                            content: json.senderName + '分享了一张图片',
                            url: '/expert/detail/'+json.senderId
                        });
                        break;
                    case 317: //群信息更改通知消息
                        try{
                            json.groupTips = $.parseJSON(json.message);
                        }catch (e){

                        }finally{
                            if(json.type){
                                switch (json.type){
                                    case 0:
                                        json.groupType = 'normal';
                                        break;
                                    case 1:
                                        json.groupType = 'multicommunicate';
                                        break;
                                    case 2:
                                        json.groupType = 'translate';
                                        break;
                                    default:
                                        break;
                                }
                            }
                            if(json.state){
                                switch (json.state){
                                    case 16: // 修改群组名称
                                        json.message = json.groupTips.memberName +'修改了群组名称';
                                        break;
                                    case 17: // 修改群组头像
                                        json.message = json.groupTips.memberName +'修改了群组头像';
                                        break;
                                    case 18: // 修改群组名称和头像
                                        json.message = json.groupTips.memberName +'修改了群组名称和头像';
                                        break;
                                    default:
                                        break;
                                }
                            }
                            //msgLS.save(json);
                            self.setNewChatMsg(json);
                            //self.getUnreadChatMsg();
                            /*self._notify({
                                img: util.getAvatar(json.senderId),
                                imgUrl: '/expert/detail/'+json.senderId,
                                title: '群消息提醒',
                                content: json.message,
                                url: '/expert/detail/'+json.senderId
                            });*/
                        }
                        break;
                    case 400:
                        if(window.webIM){
                            switch (json.data.action){
                                case 'notice':
                                    window.webIM.notice(json.data.type, json.data.name, json.data.content, json.data.time, false);
                                    break;
                                case 'videoStatus':
                                    window.webIM.changeVideo(json.data.boolean, false);
                                    break;
                            }
                        }

                        break;
                    default : //系统消息
                        self.getDataForSelf(json.code);
                        break;
                }
            }
        },
        /**
         * 根据回执重写本地消息记录
         */
        setNewChatData : function(data){
            var self = this,
                msgList = msgLS.readAll();

            $.each(msgList, function(i, n){
                $.each(n, function(x, y){
                    if(y && y.messageId == data.messageId){

                    }
                })
            })
        },
        /**
         * 收到新消息并写入webIM
         */
        setNewChatMsg : function(json){
            if(window.isLoadChat && json.senderId){
                chatAct.setChatMsg(json);
            }
        },
        /**
         * 请求最新消息状态
         */
        getDataForSelf : function(k){
            var self = this;
            util.setAjax(inter.getApiUrl().getNewMsgUrl, {}, function(json){
                if(json.status){
                    window.msgData = json;
                    self.sysMsgTips(json);
                }
            }, null, 'GET');
        },
        /**
         * 获取未读聊天消息
         */
        getUnreadChatMsg : function(){
            var self = this;
            util.setAjax(inter.getApiUrl().getNewChatMsgUrl, {}, function(json){
                if(json.status){
                    window.chatData = json;
                    self.chatMsgTips(json);
                }
            }, null, 'GET');
        },
        /**
         * 异常下线提醒
         */
        offlineTips : function(){
            util.setAjax(inter.getApiUrl().clearCookieUrl, {});
            $.confirm('您的账号在别处登录，如非本人操作请及时登录并修改密码。', function(){
                location.href = '/login';
            }, function(){

            }, {okValue: '重新登录', cancelValue: '我知道了'})
        },
        /**
         * 聊天消息提示
         */
        chatMsgTips : function(json){
            var self = this,
                chatBox = $('#chatBox'),
                type = 0,
                tipsTxt = '最近联系人',
                msgTitle = '',
                totalNum = 0,
                chatMsgList = JSON.parse(json.chatMsgNumJson) || [],
                warn = chatBox.children('.chat-warn'),
                chatMsgTip = $('#contentLeft .userTypeTab');

            if(chatMsgList.length && json.chatMsgNum){
                if(chatMsgList.length === 1){
                    var showName = chatMsgList[0].type == 'person' ? chatMsgList[0].userName : chatMsgList[0].groupName;
                    tipsTxt = '<span class="chat-warn-u" title="'+ showName +'">' + util.autoAddEllipsis(showName, 12) + '</span>&nbsp;发来'+ chatMsgList[0].msgNum +'条消息';
                }else{
                    tipsTxt = chatMsgList.length+'个联系人&nbsp;发来'+ json.chatMsgNum +'条消息';
                }
                type = 1;
            }
            self.chatTipAnimate(tipsTxt, type);

            //IM消息提示
            if(chatMsgTip.length){
                var chatNumObj = chatMsgTip.find('.home-num-trade');
                if(json.chatMsgNum){
                    if(json.chatMsgNum > 99){
                        totalNum = '99+';
                        msgTitle = '您有超过99条未读聊天消息';
                    }else{
                        totalNum = json.chatMsgNum;
                        msgTitle = '您有'+ json.systemMsgNum +'条未读聊天消息';
                    }
                    if(chatNumObj.length){
                        chatNumObj.removeClass('none').find('.home-num-txt').text(totalNum);
                    }else{
                        chatNumObj = $(util.template(tpl, {
                            cls: 'home-num-trade',
                            msgTitle: msgTitle,
                            msgNum: totalNum
                        }));
                        chatMsgTip.prepend(chatNumObj);
                    }
                }else{
                    chatNumObj.remove();
                }
            }
            //添加系统消息到聊天记录处
            if((window.msgData && window.msgData.systemMsgNum) || (window.chatData && window.chatData.chatMsgNum)){
                self._title();
            }else{
                self._cleanTitle();
            }
        },
        /**
         * IM消息动画
         */
        chatTipAnimate : function(txt, kind){
            var self = this,
                showTxt = txt || '最近联系人',
                chatBox = $('#chatBox'),
                warn = chatBox.children('.chat-warn'),
                warnCls = warn.prop('class');


            warn.html(showTxt);
            if(kind){

                //控制 提示框闪动 n:闪动次数
                var flicker = function(n){
                    var i = 0,
                        flickerFun = function(){
                            i++;
                            setTimeout(function(){
                                warn.toggleClass('chat-warn-h');
                                if(i < n*2){
                                    flickerFun();
                                }else{
                                    flickTimer = true;
                                }
                            }, 600)
                        };
                    flickerFun();
                };

                if(flickTimer){
                    if(warnCls == 'chat-warn'){
                        warn.addClass('chat-warn-h');
                    }
                    flickTimer = false;
                    flicker(3);
                }

            }else{
                if(warnCls && warnCls.indexOf('chat-warn-h')>-1){
                    warn.removeClass('chat-warn-h');
                }
            }
        },
        /**
         * 系统消息提示
         */
        sysMsgTips : function(json){
            var self = this,
                newMsg = $('.J-login-mark'),
                totalNum = 0,
                msgTitle = '',
                totalNumObj = newMsg.find('.home-num-trade');

            if(json){
                //添加系统消息提示到头像处
                if( json.systemMsgNum ){
                    if(json.systemMsgNum > 99){
                        totalNum = '99+';
                        msgTitle = '您有超过99条新消息';
                    }else{
                        totalNum = json.systemMsgNum;
                        msgTitle = '您有'+ json.systemMsgNum +'条新消息';
                    }
                    if(totalNumObj.length){
                        totalNumObj.find('.home-num-txt').text(totalNum);
                    }else{
                        totalNumObj = $(util.template(tpl, {
                            cls: 'home-num-trade',
                            msgTitle: msgTitle,
                            msgNum: totalNum
                        }));
                        newMsg.find('#J-header-msg').prepend(totalNumObj);
                        //totalNumObj.addClass('home-num-trade');
                    }
                }else{
                    if(totalNumObj.length){
                        totalNumObj.remove();
                    }
                }
            }

            //添加系统消息到聊天记录处
            if((window.msgData && window.msgData.systemMsgNum) || (window.chatData && window.chatData.chatMsgNum)){
                self._title();
            }else{
                self._cleanTitle();
            }
        },
        /**
         * 即时咨询消息提示窗口
         */
        inviteConsult : function(json){
            var self = this,
                pollConsultCountDown = json.countDown || 60,
                openUrl = util.strFormat(inter.getApiUrl().consultExportUrl, [json.userId]),
                tpl = [
                    '<div id="consultMsgBox">',
                        '<div class="cmb-tit clearfix">',
                            '<span class="span-left">咨询请求</span>',
                        '</div>',
                        '<div class="cmb-table">',
                            '<table border="0" width="100%">',
                                '<tr>',
                                    '<td class="cmb-content" align="center">',
                                        '<div class="countdown">',
                                            '<input class="knob" data-min="0" data-max="60" data-readOnly=true data-bgColor="#ffd4d9" data-fgColor="#e06b64" data-displayInput=true data-width="85" data-height="85" data-thickness=".2">',
                                        '</div>',
                                        '<div class="tips-text">收到<span class="cmb-user">#{consultUser}</span>的视频咨询请求</div>',
                                    '</td>',
                                '</tr>',
                                '<tr>',
                                    '<td class="cmb-button">', // colspan="2"
                                        '<button class="btn-default btn-lg btn-green cmb-start"><i class="icon icon-consult"></i>立即开始</button>',
                                        '<button class="btn-default btn-lg btn-white cmb-stop">拒绝咨询</button>',
                                    '</td>',
                                '</tr>',
                            '</table>',
                        '</div>',
                    '</div>'
                ].join('');

            var consultApi = $('#consultMsgBox');
            if(consultApi.length){
                util.clearCountdown();
                $('.knob').val(0).trigger("change");
                consultApi.find('.cmb-content .tips-text').html('收到<span class="cmb-user">'+ json.userName +'</span>的视频咨询请求');
                consultApi.find('.cmb-content .tips-red').remove();
                consultApi.find('.cmb-button').html('<button class="btn-default btn-lg btn-green cmb-start"><i class="icon icon-consult"></i>立即开始</button><button class="btn-default btn-lg btn-white cmb-stop">拒绝咨询</button>');
                consultApi.show();
            }else{
                consultApi = $(util.template(tpl, {
                    consultUser : json.userName
                }));

                $('body').append(consultApi);
            }
            $('.cmb-start').on('click', function(){ // 打开咨询窗口
                window.open(openUrl);
                self._consultClose('clear');
                util.clearCountdown();
                self._consultPoll();
            });
            $('.cmb-stop').on('click', function(){ // 拒绝咨询
                self._rejectConsult(json.userId, function(){
                    self._consultClose('clear');
                    util.clearCountdown();
                    self._consultPoll();
                });
            });

            $(".knob").knob({
                draw : function(){
                    this.$.val(this.v < 10 ? '0' + this.v : this.v);
                }
            });

            util.countdown('.knob', pollConsultCountDown, function(){
                var btnRemember = $('<button class="btn-default btn-lg btn-green cmb-remember">我知道了</button>');
                consultApi.find('.tips-text').html( '<span class="cmb-user">' + json.userName + '</span>的视频咨询请求已过期' );
                consultApi.find('.cmb-content').append('<div class="tips-red">收到咨询请求请在60秒内接受或拒绝</div>');
                consultApi.find('.cmb-button').html(btnRemember);
                btnRemember.on('click', function(){ // 咨询超时
                    self._consultPoll();
                    self._consultClose('clear');
                });
            }, function(t){
                $('.knob').val(t).trigger("change");
            });

        },
        /**
         * 展示右下角消息窗口
         */
        showMsgBox : function(kind, json){
            var self = this,
                openUrl = util.strFormat(inter.getApiUrl().consultExportUrl, [json.senderId]),
                tpl = [
                    '<div id="consultMsgBox">',
                        '<div class="cmb-tit clearfix">',
                            '<span class="span-left">温馨提示</span>',
                        '</div>',
                        '<div class="cmb-table">',
                            '<table border="0" width="100%">',
                                '<tr>',
                                    '<td class="cmb-content" align="center">',
                                        '<div class="tips-text"><b>#{consultUser}</b> #{chatMessage}</div>',
                                    '</td>',
                                '</tr>',
                                '<tr>',
                                    '<td class="cmb-button">',
                                        '<button class="btn-default btn-lg btn-green cmb-open">马上进入</button>',
                                        '<button class="btn-default btn-lg btn-white cmb-ignore">忽略</button>',
                                    '</td>',
                                '</tr>',
                            '</table>',
                        '</div>',
                    '</div>'
                ].join(''),
                consultApi = $('#consultMsgBox');

            if(kind){
                if(consultApi.length){
                    consultApi.stop().animate({
                        opacity: 1
                    }, 1, function(){
                        consultApi.show().find('.tips-text b').html(json.senderName);
                    });
                }else{
                    consultApi = $(util.template(tpl, {
                        consultUser : json.senderName,
                        chatMessage : '退出聊天室'
                    }));

                    $('body').append(consultApi);
                }
                consultApi.find('.cmb-button').hide();
                self._boxCountDown(consultApi);
            }else{
                self._getChatStatus({otherUid : json.senderId, selfUid : self.curUid, reUid: json.receiverId}, function(){
                    if(consultApi.length){
                        consultApi.stop().animate({
                            opacity: 1
                        }, 1, function(){
                            consultApi.show().find('..tips-text b').html(json.senderName);
                        });
                    }else{
                        consultApi = $(util.template(tpl, {
                            consultUser : json.senderName,
                            chatMessage : '邀请您进入聊天室'
                        }));

                        $('body').append(consultApi);
                    }
                    consultApi.find('.cmb-button').show();
                    self._boxCountDown(consultApi);

                    consultApi.hover(function(){
                        util.clearCountdown();
                        consultApi.stop().animate({
                            opacity: 1
                        }, 100);
                    }, function(){
                        self._boxCountDown(consultApi);
                    });

                    $('.cmb-open').off('click').on('click', function(){
                        var $this = $(this);
                        self._getChatStatus({otherUid : json.senderId, selfUid : self.curUid, reUid: json.receiverId}, function(){
                            self.sendData(JSON.stringify({
                                "code" : 106,
                                "messageId" : uuid.get(),
                                "userId" : self.curUid
                            }));
                            window.open(openUrl, json.senderId+'', 'height=446,width=1002,top=100,left=200,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
                            self.hideMsgBox();
                        }, function(){
                            $.alert('已经存在与该用户的聊天窗口，请检查任务栏。');
                        })
                    });

                    $('.cmb-ignore').off('click').on('click', function(){
                        self.sendData(JSON.stringify({
                            "code" : 107,
                            "messageId" : uuid.get(),
                            "userId" : self.curUid
                        }));
                        self.hideMsgBox();
                    });
                })
            }
        },
        /**
         * 隐藏消息窗口
         */
        hideMsgBox : function(){
            util.clearCountdown();
            $('#consultMsgBox').hide();
        },
        /**
         * 获取当前用户与另一指定用户的联系状态
         */
        _getChatStatus : function(json, sCall, eCall){
            if($.isEmptyObject(isOpenChat)){
                util.setAjax(util.strFormat(inter.getApiUrl().getSelfInChatUrl, [json.otherUid, json.selfUid]), {}, function(data){
                    data.status ? eCall && eCall() : sCall && sCall();
                }, null, 'GET')
            }else{
                var chatStatus = json.selfUid == json.otherUid ? isOpenChat[json.selfUid + 'to' + json.reUid] : isOpenChat[json.selfUid + 'to' + json.otherUid];
                chatStatus && chatStatus.self ? eCall && eCall() : sCall && sCall();
            }
        },
        /**
         * 计时隐藏消息窗口
         */
        _boxCountDown : function(consultApi){
            util.clearCountdown();
            util.countdown(null, 10, function(){
                consultApi.stop().animate({
                    opacity: 0
                }, 2000, function(){
                    consultApi.hide();
                });
            });
        },
        _resetChatList : function(){

        },
        /**
         * 关闭咨询消息提示窗口并清除Cookie
         */
        _consultClose : function(act){
            var self = this;
            $('.knob').val(60).trigger("change");
            $('#consultMsgBox').hide();
            util.clearCountdown();

            if(act && act == 'clear'){
                self._cleanTitle('新咨询');
            };
        },
        /**
         * 发起请求使长连接返回数据关闭提示框
         */
        _consultPoll : function(){
            var self = this;
            util.setAjax(inter.getApiUrl().getConsultPollUrl, {}, function(json){}, null, 'GET')
        },
        /**
         * 新消息声音提醒
         */
        _tip : function(){
            var tip = $('<embed src="'+ ued_conf.root +'images/tips.mp3" hidden="true" type="audio/mpeg" loop="0" autostart="true"/>');
            if(!!document.createElement('video').canPlayType){
                tip = $('<video src="'+ ued_conf.root +'images/tips.mp3" autoplay="true" width="0" height="0"/>');
            }
            $('body').append(tip);

            setTimeout(function(){
                tip.remove();
            }, 10000);
        },
        /**
         * 新消息title提醒
         */
        _title : function(tips){
            var self = this,
                newTitle = (winStatus ? '【'+ (tips||'新消息') +'】' : '【　　　】') + oldTitle;

            $(document).attr('title', newTitle);
            winStatus = !winStatus;
            existTitle = true;
            clearTimeout(statusTimer);

            statusTimer = setTimeout(function(){
                self._title(tips);
            }, 1000);
        },
        /**
         * 清除新消息title提醒
         */
        _cleanTitle : function(t){
            var nowTitle = $('title').text();
            if(nowTitle !== oldTitle){
                if(t){
                    if( nowTitle.indexOf(t) > -1 ){
                        $(document).attr('title', oldTitle);
                        winStatus = false;
                        existTitle = false;
                        clearTimeout(statusTimer);
                    }
                }else{
                    $(document).attr('title', oldTitle);
                    winStatus = false;
                    existTitle = false;
                    clearTimeout(statusTimer);
                }
            }
        },
        /**
         * 消息桌面提醒
         */
        _notify : function(args){
            var self = this;
            if ((isOpenNotify || self._isPageMinimized()) && window.webkitNotifications) {
                if (window.webkitNotifications.checkPermission() == 0) { //判断是否授权桌面通知 0 已授权  1 未授权  2 已拒绝
                    var notification_obj = window.webkitNotifications.createNotification(args.img, args.title, args.content);
                    notification_obj.display = function() {};
                    notification_obj.onerror = function() {};
                    notification_obj.onclose = function() {};
                    notification_obj.onclick = function() {this.cancel();};
                    notification_obj.replaceId = 'chatNotify';
                    notification_obj.show();
                    askNotify = false;
                } else {
                    if(!askNotify){
                        askNotify = true;
                        window.webkitNotifications.requestPermission(self._notify(args));
                    }
                }
            } else if(window.Notification){
                var notification = new Notification(args.title, {iconUrl: args.img, tag: "tag_by_alien", body: args.content});
                notification.onerror = function(f) {};
                notification.onshow = function(f) {};
                notification.ondisplay = function(f) {};
                notification.onclick = function(f) {notification.cancel()};
                notification.onclose = function(f) {};
                try{
                    notification.show();
                }catch (e){

                }
            }
        },
        /**
         * 浏览器窗口最小化时触发
         */
        setWindowFocus : function(){
            window.onblur = function () {
                isOpenNotify = true;
            };
            window.onfocus = function () {
                isOpenNotify = false;
            }
        },
        /**
         * 判断浏览器窗口是否最小化
         */
        _isPageMinimized : function(){
            //document.hidden || document.visibilityStats == 'hidden' (HTML5 Visibility API)Firefox10+, IE10+, Chrome14+
            var list = ['mozHidden', 'msHidden', 'webkitHidden', 'oHidden'];
            for (var i = list.length ; i-- ; ){
                if(typeof document[list[i]] === 'boolean'){
                    return document[list[i]];
                }
            }
            if(typeof window.screenTop === 'number'){//Opera, Safari, Chrome,IE9
                return window.screenTop < -30000 ;//不考虑Safari的话,其实 <0 即可判断.
            }
            return window.outerWidth <= 160 && window.outerHeight <= 27;
        }
    }
});