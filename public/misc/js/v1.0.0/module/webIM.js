/**
 * @description: 文字聊天
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/webIM', [
    'common/interface',
    'common/util',
    'module/cookie',
    'module/uuid',
    'module/cursorControl',
    'module/emotion',
    'module/media',
    'module/msgLS'
], function (inter, util, cookie, uuid, cursorControl, emotion, media, msgLS) {
    // 查看更多消息模板
    var sChatLineTpl = [
            '<div class="chat-body chat-center">',
                '<div class="chat-line">',
                    '<span class="chat-lineDate">#{date}</span>',
                '</div>',
            '</div>'
        ].join(''),
        sChatMainTpl = [
            '<div class="chat-loading"></div>',
            '<a class="JS-get-chat">查看更多消息</a>'
        ].join(''),
        //接收聊天消息 模板
        sChatGroupYouTpl = [
            '<div class="chat-body chat-left clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span title="#{youName}">#{youName}</span><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg fl"><em class="chat-icon ornament"></em><span class="chat-content">#{msg}</span>#{tl}</div>',
                '</div>',
            '</div>'
        ].join(''),
        //接收聊天消息 模板
        sChatMsgYouTpl = [
            '<div class="chat-body chat-left clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg fl"><em class="chat-icon ornament"></em><span class="chat-content">#{msg}</span>#{tl}</div>',
                '</div>',
            '</div>'
        ].join(''),
        //发送聊天消息 模板
        sChatMsgMeTpl = [
            '<div class="chat-body chat-right clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg"><em class="chat-icon ornament"></em><span class="chat-content">#{msg}</span>#{tl}</div>',
                '</div>',
            '</div>'
        ].join(''),
        //群邀请消息 - 无需验证 模板
        sGroupTipsTpl = [
            '<div class="chat-body chat-center clearfix">',
                '<div class="chat-main #{showCls}" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time"><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-sys-tips">#{msg}</div>',
                '</div>',
            '</div>'
        ].join(''),
        //接收语音消息 模板
        sChatAudioYouTpl = [
            '<div class="chat-body chat-left clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span title="#{youName}">#{youName}</span><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg fl">',
                        '<em class="chat-icon ornament"></em>',
                        '<span class="chat-content chat-voice clearfix" style="width: #{voiceWidth}px">',
                            '<span class="chat-icon chat-voice-start" title="播放语音" data-src="#{src}"></span>',
                            '<span class="chat-icon chat-voice-static"></span>',
                        '</span>',
                    '</div>',
                    '<span class="chat-voice-time fl">#{voiceLength}″</span>',
                '</div>',
            '</div>'
        ].join(''),
        //发送语音消息 模板
        sChatAudioMeTpl = [
            '<div class="chat-body chat-right clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg fr">',
                        '<em class="chat-icon ornament"></em>',
                        '<span class="chat-content chat-voice clearfix" style="width: #{voiceWidth}px">',
                            '<span class="chat-icon chat-voice-start" title="播放语音" data-src="#{src}"></span>',
                            '<span class="chat-icon chat-voice-static"></span>',
                            //'<embed src="#{src}"></embed>',
                        '</span>',
                    '</div>',
                    '<span class="chat-voice-time fr">#{voiceLength}″</span>',
                '</div>',
            '</div>'
        ].join(''),
        // 发送文件模板
        sChatMsgMeFileTpl = [
            '<div class="chat-body chat-right chat-file clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg">',
                        '<em class="chat-icon ornament"></em>',
                        '<span class="chat-file">',
                            '<p class="file-send"><span class="file-icon"></span></p>',
                            '<p class="file-name" title="#{fileName}"><span>#{fileName}</span></p>',
                            '<p class="file-size"><span>#{fileSize}KB</span></p>',
                            '<p class="file-progress"><span><em class="on"></em></span></p>',//<em class="base"></em>
                        '</span>',
                    '</div>',
                '</div>',
            '</div>'
        ].join(''),
        // 发送图片模板
        sChatMsgMePicTpl = [
            '<div class="chat-body chat-right chat-file clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg">',
                        '<em class="chat-icon ornament"></em>',
                        '<span class="chat-file">',
                            '<div class="file-pic">',
                            '<div class="file-progress"><em class="on"></em></div>',//<em class="base"></em>
                            '<a href="#{url}" target="_blank" data-lightbox="example-set"><img src="#{url}"></a>',
                            '</div>',
                        '</span>',
                    '</div>',
                '</div>',
            '</div>'
        ].join(''),
        //历史记录 发送文件模板
        sChatMsgRecMeFileTpl = [
            '<div class="chat-body chat-right chat-file clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg">',
                        '<em class="chat-icon ornament"></em>',
                        '<span class="chat-file">',
                            '<p class="file-send"><span class="file-icon"></span></p>',
                            '<p class="file-name"><span><a href="#{url}" target="_blank" download="#{name}">#{name}</a></span></p>',
                            '<p class="file-size"><span>#{size}KB</span></p>',
                        '</span>',
                    '</div>',
                '</div>',
            '</div>'
        ].join(''),
        //历史记录 发送图片模板
        sChatMsgRecMePicTpl = [
            '<div class="chat-body chat-right chat-file clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg">',
                        '<em class="chat-icon ornament"></em>',
                        '<span class="chat-file">',
                            '<div class="file-pic">',
                                '<a href="#{url}" target="_blank" title="#{name} #{size}KB" data-lightbox="example-set"><img src="#{url}" alt="#{name}"></a>',
                            '</div>',
                        '</span>',
                    '</div>',
                '</div>',
            '</div>'
        ].join(''),
        //接收图片的模板
        sChatMsgYouPicTpl = [
            '<div class="chat-body chat-left chat-file clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span title="#{youName}">#{youName}</span><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg">',
                        '<em class="chat-icon ornament"></em>',
                        '<span class="chat-file">',
                            '<div class="file-pic">',
                                '<a href="#{url}" target="_blank" title="#{name} #{size}KB" data-lightbox="example-set"><img src="#{url}" alt="#{name}"></a>',
                            '</div>',
                        '</span>',
                    '</div>',
                '</div>',
            '</div>'
        ].join(''),
        //接收文件的模板
        sChatMsgYouFileTpl = [
            '<div class="chat-body chat-left chat-file clearfix">',
                '<div class="chat-main" data-uuid="#{msgId}" title="#{chatTime}">',
                    '<div class="chat-time #{showCls}"><span title="#{youName}">#{youName}</span><span class="im-time">#{chatTime}</span></div>',
                    '<div class="chat-msg">',
                        '<em class="chat-icon ornament"></em>',
                        '<span class="chat-file">',
                            '<p class="file-send"><span class="file-icon"></span></p>',
                            '<p class="file-name"><span><a href="#{url}" target="_blank" download="#{name}">#{name}</a></span></p>',
                            '<p class="file-size"><span>#{size}KB</span></p>',
                        '</span>',
                    '</div>',
                '</div>',
            '</div>'
        ].join(''),
        sChatMsgTranslateTpl = '<a class="chat-tl fl none" href="javascript:" title="翻译"><em class="chat-icon translate"></em></a>',
        tlMsgTpl = '<span class="tl" data-uuid="#{uuid}"><img class="tl-head" src="#{avatar}" width="28" height="28" title="翻译者：#{title}"><span class="tl-con">#{message}</span></span>',
        sChatMsgLoading = '<span class="loading fl"><img src="' + ued_conf.root + 'images/loading-tl.gif" width="16" height="16" title="loading" alt="loading"></span>',
        //这个4个dom节点,要在执行了打开聊天窗口后才取得到；所以放init方法里取
        $chatContent ,
        $msgBox ,
        $inputMsg,
        $sendMsg ,
        $chatTitle,

        pageParams = { pageSize: 10 },
        sInputMsg = '',
        bLoad = false,
        nSendFileSizeFloat = 0,//文件总大小
        nSendFileSize = 0,//已上传文件大小
        requestTime = "",
        ajaxApi = null,
        $sFileMsgMe = '',//显示上传的dom节点
        prevUid = null,
        prevDate = null;

    window.uploadFiles = null;
    window.fileDataIndex = -1;
    window.fileDate = null;
    window.fileUploading = false;
    window.isLossData = false;

    var webIM = {
        init: function () {
            var self = this,
                isNoRead = false,
                curUid = parseInt(cookie.get('_u_id')) || 0,
                chatTitle = $(''),
                data = {},
                lastLocalChat = self._getLastLocalChat(me.id, you.id);

            self.tlMode = parseInt(cookie.get('tlMode' + curUid)) || 0;
            self.first = true;
            self.pageIndex = 1;
            $chatContent = $('#chatContent');
            $chatTitle = $('#chatTitle');
            $msgBox = $chatContent.find('.msg-box');
            $inputMsg = $('#chatInput');
            $sendMsg = $('#chatBtn');
            pageParams.pageIndex = 0;
            requestTime = '';
            /*添加更多消息*/
            if (!$msgBox.find('.JS-get-chat').length) {
                $msgBox.append(sChatMainTpl);
            }
            self._bindEvens();
            if (window.linkmanList) {
                $.each(window.linkmanList, function (i, n) {
                    if (!n.groupId && n.userId == you.id && n.msgNum && n.msgTime > (lastLocalChat ? lastLocalChat.sendTime : 0)) {
                        isNoRead = true;
                        self._getNoReadChatData(you.id, n.msgNum, n.msgTime, function (data) {
                            msgLS.saveList(self._formatChatData(data.list));
                            self._getChatRecord(self.chatScrollTop, you.id);
                        });
                        return false;
                    }
                })
            }
            if (!isNoRead) {
                self._getChatRecord(self.chatScrollTop, you.id);
            }
            self.checkChatSend();

            data.userId = me.id;
            data.userName = me.name;
            data.inviteeId = you.id;
            data.inviteeName = you.name;
            data.date = +new Date();
            data.sessionId = 0;
            //视频邀请
            $chatTitle.find('.i-video').off('click').on('click', function () {
                var socketState = socket.socket.readyState != 1;
                if (socketState) {
                    //当是socket断开后恢复状态
                    if(window.webIM){
                        //当socket断开后 ,恢复状态
                        window.webIM.notice('notice','','通讯中断，请稍后重试！',4,false);
                        window.webIM.changeVideo(true);
                    }
                } else {
                    /* 若此次为重复发送，则以此次为准，并清掉其他的邀请按钮 */
                    window.webIM.notice('','','',0,true);

                    if(window.webIM){
                        window.webIM.notice('notice','','<span class="d-loading"></span>');
                    }

                    data.code = 210;
                    data.messageId = uuid.get();
                    socket.sendData(JSON.stringify(data));

                    cookie.set("inviteeId", you.id);
                    cookie.set("inviteeName", you.name);
                    if(window.webIM){
                        webIM.notice('notice', you.name, '正在邀请<strong class="user-name">' + you.name + '</strong>视频会话', null, false);
                        webIM.changeVideo(false);
                    }
                    //if(window.webIM) webIM.notice('notice', you.name, '<strong class="user-name">' + you.name + '</strong>不在线，无法接收您的视频会话邀请', 4 ,false);

                }
            });
            //音频邀请
            $chatTitle.find('.i-voice').off('click').on('click', function () {
                var socketState = socket.socket.readyState != 1
                if (socketState) {
                    //当是socket断开后恢复状态
                    if(window.webIM){
                        //当socket断开后 ,恢复状态
                        window.webIM.notice('notice','','通讯中断，请稍后重试！',4,false);
                        window.webIM.changeVideo(true);
                    }
                } else{
                    /* 若此次为重复发送，则以此次为准，并清掉其他的邀请按钮 */
                    window.webIM.notice('','','',0,true);
                    if(window.webIM){
                        window.webIM.notice('notice','','<span class="d-loading"></span>');
                    }

                    data.code = 220;
                    data.messageId = uuid.get();
                    socket.sendData(JSON.stringify(data));

                    cookie.set("inviteeId", you.id);
                    cookie.set("inviteeName", you.name);
                    if(window.webIM){

                    window.webIM.notice('notice', you.name, '正在邀请<strong class="user-name">' + you.name + '</strong>语音会话',null,false);

                    webIM.changeVideo(false);}

                    //if(window.webIM) webIM.notice('notice', you.name, '<strong class="user-name">' + you.name + '</strong>不在线，无法接收您的语音会话邀请', 4,false);

                }
            });
        },
        /**
         * 初始化群组聊天
         */
        initGroupChat: function () {
            var self = this,
                isNoRead = false,
                curUid = parseInt(cookie.get('_u_id')) || 0,
                chatTitle = $(''),
                lastLocalChat = self._getLastLocalChat(me.id, group.id);

            self.tlMode = parseInt(cookie.get('tlMode' + curUid)) || 0;
            self.first = true;
            self.pageIndex = 1;
            $chatContent = $('#chatContent');
            $chatTitle = $('#chatTitle');
            $msgBox = $chatContent.find('.msg-box');
            $inputMsg = $('#chatInput');
            $sendMsg = $('#chatBtn');
            pageParams.pageIndex = 0;
            requestTime = '';

            /*添加更多消息*/
            $msgBox.append(sChatMainTpl);
            self._bindEvens();
            if (window.linkmanList) {
                $.each(window.linkmanList, function (i, n) {
                    if (n.groupId == group.id && n.msgNum && n.msgTime > (lastLocalChat ? lastLocalChat.sendTime : 0)) {
                        isNoRead = true;
                        self._getNoReadChatData(group.id, n.msgNum, n.msgTime, function (data) {
                            msgLS.saveList(self._formatChatData(data.list));
                            self._getGroupMember(function (json) {
                                self._getChatRecord(self.chatScrollTop, 0, group.id);
                            }, null);
                        });
                        return false;
                    }
                })
            }
            if (!isNoRead) {
                self._getGroupMember(function (json) {
                    self._getChatRecord(self.chatScrollTop, 0, group.id);
                }, null);
            }
            self.checkChatSend();
        },
        /**
         * 获取本地最后一条聊天记录
         */
        _getLastLocalChat: function (fid, tid) {
            var self = this,
                lsMsg = msgLS.read(fid, tid);

            return lsMsg && lsMsg[lsMsg.length - 1];
        },
        /**
         * 获取群成员数据
         */
        _getGroupMember: function (sucCall, errCall) {
            var self = this,
                url = util.strFormat(inter.getApiUrl().getGroupMember, [group.id]),
                param = {},
                groupMember = 0;
            $(".JS-get-chat").addClass('none');
            util.setAjax(url, param, function (json) {
                if (json.error) {
                    errCall && errCall(json);
                } else {
                    group.member = json;
                    groupMember = group.member.length - 1;
                    if(groupMember < 0)groupMember = 0;
                    $('#chatTitle .i-look').text(groupMember);
                    //$('#chatTitle .i-look').text(group.member.length - (group.type == 'translate'));
                    sucCall && sucCall(json);
                }
            }, function () {
                errCall && errCall();
            }, 'GET');
        },
        /**
         * 获取最近联系人失败
         */
        loadFailed: function (call) {
            var listPanel = $chatTitle.find('.tl-user-panel .user-list'),
                errorTpl = $('<li class="load-failed">列表加载失败 <a href="javascript:">重新加载</a></li>');

            listPanel.html(errorTpl);
            errorTpl.find('a').on('click', function (e) {
                call && call();
                e.preventDefault();
            })
        },
        /**
         * 滚动到底部
         */
        chatScrollTop : function (h) {
            var scrollH = h || $msgBox.height();
            $chatContent.scrollTop(scrollH);
        },
        /**
         * 验证并发送消息
         */
        send : function (s) {
            var self = this,
                getUuid = uuid.get(),
                chatTips = $('.chatTips'),
                chatEditPanel = $('#chatEditPanel'),
                gid = $chatTitle.attr('data-gid') || 0,
                sendTime = +new Date(),
                showCls = me.id == prevUid ? 'none' : '',
                loading = $(sChatMsgLoading);

            sInputMsg = util.safeHTML(s);
            if (sInputMsg == '' || sInputMsg == '\n' || sInputMsg == '<p><br></p>') {
                cursorControl.clearDoc();
                chatTips.html('发送内容不能为空').show();
                return;
            } else {
                chatTips.empty();
            }
            //发送字数不做上限
            /*
             if(sInputMsg.length>2000){

             $inputMsg.poshytip('update','发送内容不能超过2000个字');
             $inputMsg.poshytip('show').poshytip('hideDelayed', 1500);
             return;
             }*/
            var sMsgMe = $(util.template(sChatMsgMeTpl, {
                showCls: showCls,
                msgId: getUuid,
                chatTime: util.dateFormat(sendTime, 'hh:mm:ss'),
                msg: emotion.replaceEmotion(sInputMsg)
            }));
            $inputMsg.val('');
            cursorControl.clearDoc();
            chatEditPanel.focus();
            chatTips.empty();
            $msgBox.append(sMsgMe);
            sMsgMe.find('.chat-main').append(loading);
            loading.removeClass('fl').addClass('fr');
            group ? self._sendChatMessage(getUuid, s, 1, gid) : self._sendChatMessage(getUuid, s, 0);
            self.chatScrollTop();
            prevUid = me.id;
        },
        /**
         * 发送消息到消息中心
         */
        _sendChatMessage: function (u, msg, t, gid, pid) {
            var self = this,
                data = {
                    code: 3,
                    messageId: u,
                    data: util.safeHTML(msg).replace(/&nbsp;/g, ' '),
                    senderId: me.id,
                    senderName: me.name
                };
            switch (t) {
                case 1: //群消息
                    data.code = 305;
                    data.type = 1;
                    data.groupId = gid;
                    data.parentId = 0;
                    break;
                case 2: //翻译消息
                    data.code = 305;
                    data.type = 2;
                    data.groupId = gid;
                    data.parentId = pid;
                    break;
                default : //单用户消息
                    data.receiverId = you.id;
                    data.receiverName = you.name;
                    break
            }
            socket.sendData(JSON.stringify(data), function () {
                self.setChatSendErr(data);
            });
        },
        /**
         * 发送本地文件/图片
         */
        sendFile: function (obj) {
            var self = this,
                file = obj.files[0],
                reader = new FileReader();
            window.uploadFiles = obj;
            window.fileDate = +new Date();
            window.fileDataIndex = -1;
            window.fileUploading = true;
            window.isLossData = false;
            self.fileSendUuid = uuid.get();
            $inputMsg.focus();
            if (!file.size) return;
            if (self.doSendFile(obj)) {
                reader.readAsArrayBuffer(file);
                reader.onload = function (e) {
                    self.onRead(e);
                };
            }
        },
        /**
         * 读取本地文件/图片
         */
        onRead: function (event, ab) {
            var self = this,
                chunkLength = 1024 * 10,
                data,
                json = null,
                obj = window.uploadFiles,
                fileSize = obj.files[0].size,
                filePath = obj.value.replace(/\\/g, '/'),
                fn = filePath.substring(filePath.lastIndexOf("/") * 1 + 1);

            if (event) {
                ab = util.arrayBufferToString(event.target.result);
                if(ab.length != fileSize){
                    window.isLossData = true;
                }
            }
            var finish = 0;
            if (ab.length > chunkLength) {
                window.fileDataIndex++;
                data = ab.substring(0, chunkLength);
                window.remain = ab.substring(chunkLength, ab.length);
            } else {
                window.remain = ''; //没有剩余数据
                data = ab;
                finish = 1;
                window.fileDataIndex++;
            }
            try{
                if(window.isLossData){
                    window.fileSendLength = unescape(encodeURIComponent(data)).length;
                }else{
                    window.fileSendLength = data.length;
                }
            }catch (e){
                window.fileSendLength = data.length;
            }finally{
                if(group){
                    json = {
                        code : 310,
                        senderId : me.id,
                        senderName : me.name,
                        messageId : self.fileSendUuid,
                        groupId : group.id,
                        groupName : group.name,
                        data : data,
                        fileName : fn,
                        packageNo : window.fileDataIndex,
                        dateTime : window.fileDate,
                        finish : finish,
                        size : fileSize
                    };
                }else{
                    json = {
                        code : 10,
                        senderId : me.id,
                        senderName : me.name,
                        messageId : self.fileSendUuid,
                        receiverId : you.id,
                        receiverName : you.name,
                        data : data,
                        fileName : fn,
                        packageNo : window.fileDataIndex,
                        dateTime : window.fileDate,
                        finish : finish,
                        size : fileSize
                    };
                }
                self._readFile(json);
            }
        },
        /**
         * 发送本地文件/图片
         */
        _readFile: function (json) {
            var self = this,
                sendState = true,
                fileSendLength = window.fileSendLength,
                obj = window.uploadFiles;

            socket.sendData(JSON.stringify(json), function () {
                sendState = false;
                self.setChatSendErr(json);
            });
            sendState && self.doSendFile(obj, fileSendLength);
        },
        /**
         * 发送文件/图片
         */
        doSendFile: function (obj, size) {
            var self = this,
                $file = $("#fileUpload");
            if (!size) {
                nSendFileSize = 0;
                var nMaxsize = 10 * 1024,//10M
                    sErrMsg = '发送文件不能超过10M!';
                nSendFileSizeFloat = obj.files[0].size;
                var nFileSize = Math.ceil(nSendFileSizeFloat / 1024);
                if (nFileSize == 0) return;
                if (nFileSize > nMaxsize) {
                    $.alert(sErrMsg);
                    return false
                }
                $file.attr('disabled', 'true');
                var nPosition = obj.value.lastIndexOf("\\") * 1,
                    sFileName = obj.value.substring(nPosition + 1),
                    sFileNameWithOutExt = sFileName.substring(0, sFileName.lastIndexOf(".") * 1),
                    nLength = sFileNameWithOutExt.length,
                    sFileExtName = sFileName.substring(sFileName.lastIndexOf(".") * 1),
                    showCls = me.id == prevUid ? 'none' : '';
                if (nLength > 10) {
                    sFileName = sFileNameWithOutExt.substring(0, 3) + '...' + sFileNameWithOutExt.substring(nLength - 1) + sFileExtName;
                }
                $sFileMsgMe = self._previewImage(obj, function () {
                    return $(util.template(sChatMsgMeFileTpl, {
                        showCls: showCls,
                        msgId: self.fileSendUuid,
                        headUrl: me.headUrl,
                        fileName: sFileName,
                        chatTime: util.dateFormat(new Date(), 'hh:mm:ss'),
                        fileSize: nFileSize
                    }));
                });
                $msgBox.append($sFileMsgMe);
                prevUid = me.id;
            }
            if ($sFileMsgMe.length > 0) {
                var $obj = $sFileMsgMe.find('.file-progress'),
                    $progress = $obj.find('.on');
                nSendFileSize += (size || 0);
                var rate = nSendFileSize / nSendFileSizeFloat;
                $progress.width((rate > 1 ? 1 : rate) * 100 + '%');
                if (rate >= 1) {
                    setTimeout(function () {
                        $obj.remove();
                        $file.removeAttr('disabled');
                        $file.val('');
                        nSendFileSize = 0;
                        window.fileUploading = false;
                        window.fileSendLength = 0;
                    }, 500)
                }
            } else {
                $file.removeAttr('disabled');
                $file.val('');
            }
            self.chatScrollTop();
            return true;
        },
        /**
         * 收取文件/图片
         */
        getFile: function (data, isClear) {
            var self = this,
                time = util.dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss'),
                date = util.dateFormat(new Date(data.sendTime), 'yyyy-MM-dd'),
                nowDate = util.dateFormat(new Date(), 'yyyy-MM-dd'),
                dateTime = date == nowDate ? util.dateFormat(new Date(data.sendTime), 'hh:mm:ss') : time,
                showCls = data.senderId == prevUid ? 'none' : '',
                isPic = self._isPic(data.fileName),
                sFileNameWithOutExt = data.fileName.substring(0, data.fileName.lastIndexOf(".") * 1),
                nLength = sFileNameWithOutExt.length,
                sFileExtName = data.fileName.substring(data.fileName.lastIndexOf(".") * 1),
                id1 = group ? group.id : you.id;
            if (nLength > 10) {

                data.fileName = sFileNameWithOutExt.substring(0, 3) + '...' + sFileNameWithOutExt.substring(nLength - 1) + sFileExtName;
            }

            var $sFileMsgYou = $(util.template(isPic ? sChatMsgYouPicTpl : sChatMsgYouFileTpl, {
                showCls: showCls,
                youName: data.senderName,//you.name
                name: data.fileName,
                chatTime: dateTime,
                url: data.url,
                size: Math.ceil(data.fileSize / 1024)
            }));
            $msgBox.append($sFileMsgYou);
            self.chatScrollTop();
            if (isPic) {
                $sFileMsgYou.find('.file-pic img')[0].onload = function () {
                    self.chatScrollTop();
                };
            }
            if (isClear)self.updateChat({id1: id1, id2: me.id});
            prevUid = data.senderId;
        },
        /**
         * 收取文本消息
         */
        getMsg: function (data, isClear) {
            var self = this,
                showCls = data.senderId == prevUid ? 'none' : '',
                time = util.dateFormat(new Date(data.sendTime), 'yyyy-MM-dd hh:mm:ss'),
                date = util.dateFormat(new Date(data.sendTime), 'yyyy-MM-dd'),
                nowDate = util.dateFormat(new Date(), 'yyyy-MM-dd'),
                dateTime = date == nowDate ? util.dateFormat(new Date(data.sendTime), 'hh:mm:ss') : time,
                chatMsg = $('.chat-main[data-uuid="' + data.parentId + '"]'),
                sMsgYou = $(util.template(group ? sChatGroupYouTpl : sChatMsgYouTpl, {
                    showCls: showCls,
                    msgId: data.messageId,
                    youName: data.senderName,
                    chatTime: dateTime,
                    msg: emotion.replaceEmotion(data.data)
                })),
                sMsgMe = $(util.template(sChatMsgMeTpl, {
                    showCls: showCls,
                    msgId: data.messageId,
                    chatTime: dateTime,
                    msg: emotion.replaceEmotion(data.data)
                })),
                id1 = group ? group.id : you.id;

            if (data.code == 307 || data.code == 317) { // 群通知消息
                $msgBox.append(util.template(sGroupTipsTpl, {
                    msgId: data.messageId,
                    chatTime: dateTime,
                    msg: data.message
                }));
            } else if (data.code == 103) { // 音视频通知消息
                $msgBox.append(util.template(sGroupTipsTpl, {
                    msgId: data.messageId,
                    chatTime: dateTime,
                    msg: data.data
                }));
            } else if (chatMsg.length) { //收到译文消息
                var leftChat = $('.chat-main[data-uuid="' + data.parentId + '"]'),
                    chatMsgPanel = chatMsg.find('.chat-msg'),
                    chatTl = chatMsgPanel.find('.tl');
                if (leftChat.length) {
                    if (chatTl.length) {
                        chatTl.find('.tl-con').text(data.data);
                    } else {
                        chatMsgPanel.append(util.template(tlMsgTpl, {
                            uuid: data.messageId,
                            avatar: util.getAvatar(data.senderId),
                            title: data.senderName,
                            message: data.data
                        }));
                        util.imgLoadError(chatMsgPanel.find('.tl-head'));
                    }
                }
            } else {
                var chatMsgMC = data.senderId == me.id ? sMsgMe : sMsgYou;
                $msgBox.append(chatMsgMC);
                self._chatMsgTranslate(chatMsgMC);
                if (self._isTranslator()) {
                    self._chatGroupTranslate(chatMsgMC);
                }
            }
            self.chatScrollTop();
            if (isClear)self.updateChat({id1: id1, id2: me.id});
            prevUid = data.senderId;
        },
        /**
         * 收取语音消息
         */
        getVoice: function (data, isClear) {
            var self = this,
                voiceWidth = 40,
                voiceLen = data.multimediaLength || 1,
                time = util.dateFormat(new Date(data.sendTime), 'yyyy-MM-dd hh:mm:ss'),
                date = util.dateFormat(new Date(data.sendTime), 'yyyy-MM-dd'),
                nowDate = util.dateFormat(new Date(), 'yyyy-MM-dd'),
                dateTime = date == nowDate ? util.dateFormat(new Date(data.sendTime), 'hh:mm:ss') : time,
                sMsgYou = null,
                id1 = group ? group.id : you.id;

            if (voiceLen > 20) {
                voiceWidth = 220;
            } else if (voiceLen > 4) {
                voiceWidth = voiceLen * 11;
            }
            sMsgYou = $(util.template(sChatAudioYouTpl, {
                msgId: data.messageId,
                youName: data.senderName,
                src: data.url,
                chatTime: dateTime,
                voiceWidth: voiceWidth,
                voiceLength: voiceLen || 1
            }));
            $msgBox.append(sMsgYou);
            self._bindMediaEvent(sMsgYou);
            /*self._chatMsgTranslate(sMsgYou);
             if(self._isTranslator()){
             self._chatGroupTranslate(sMsgYou);
             }*/
            self.chatScrollTop();
            if (isClear)self.updateChat({id1: id1, id2: me.id});
        },
        _bindMediaEvent: function (mediaPanel) {
            var self = this,
                oPlay = mediaPanel.find('.chat-voice-start');

            oPlay.off('click').on('click', function () {
                var $this = $(this),
                    cls = $this.prop('class'),
                    url = $this.attr('data-src'),
                    status = $this.siblings('.chat-voice-static'),
                    mediaM = media.init({
                        ele: $this,
                        url: url,
                        _onStop: function (ele) {
                            ele.siblings('.chat-voice-static').removeClass('chat-voice-dynamic');
                            ele.removeClass('chat-voice-stop');
                        }
                    });

                if (cls.indexOf('chat-voice-stop') > -1) { //停止
                    status.removeClass('chat-voice-dynamic');
                    $this.removeClass('chat-voice-stop');
                    mediaM.stop();
                } else {// 播放
                    status.addClass('chat-voice-dynamic');
                    $this.addClass('chat-voice-stop');
                    mediaM.play();
                }
            })
        },
        /**
         * 根据设置 翻译聊天
         */
        _chatMsgTranslate: function (obj) {
            var self = this,
                curUid = parseInt(cookie.get('_u_id')) || 0,
                item = obj.find('.chat-content').closest('.chat-left').find('.chat-msg');

            if (!group) {
                item.each(function () {
                    var $item = $(this),
                        chatTlTpl = $(sChatMsgTranslateTpl);
                    if (!$item.find('.tl').length) {
                        $item.after(chatTlTpl);
                        chatTlTpl.on('click', function () {
                            self.chatMsgTl($item);
                        });
                    }
                });
                self.tlMode = parseInt(cookie.get('tlMode' + curUid)) || 0;
                switch (self.tlMode) {
                    case -1: // 手动翻译
                        obj.find('.chat-tl').removeClass('none');
                        break;
                    case 1: // 自动翻译
                        self.chatMsgTl(item);
                        break;
                    case 2: // 第三方翻译
                        break;
                    default: // 没有设置翻译
                        break;
                }
            }
        },
        /**
         * 设置翻译者可以操作翻译
         */
        _chatGroupTranslate: function (obj) {
            var self = this,
                curUid = parseInt(cookie.get('_u_id')) || 0,
                item = obj.find('.chat-content').closest('.chat-left').find('.chat-msg');

            item.each(function () {
                var $item = $(this),
                    dataUuid = $item.closest('.chat-main').attr('data-uuid') || 0,
                    editTlTpl = [
                        '<div class="translate-edit">',
                        '<div class="tl-area"><textarea name="tlContent" id="tlContent">#{content}</textarea></div>',
                        '<div class="tl-btns">',
                        '<span class="chat-icon chat-btn-ok" title="确定"></span>',
                        '<span class="chat-icon chat-btn-no" title="取消"></span>',
                        '</div>',
                        '</div>'
                    ].join(''),
                    chatTlTpl = $('<a class="chat-tl-edit fl" href="javascript:" title="翻译"><em class="chat-icon i-translate-edit"></em></a>');

                if (dataUuid) {
                    $item.after(chatTlTpl);
                    chatTlTpl.on('click', function () {
                        var $this = $(this),
                            getUuid = uuid.get(),
                            msg = $this.siblings('.chat-msg'),
                            tlPanel = msg.find('.tl'),
                            tlText = tlPanel ? tlPanel.text() : '',
                            editTlPanel = $(util.template(editTlTpl, {content: tlText}));

                        if (!tlPanel.length) {
                            tlPanel = $('<span class="tl"></span>');
                            msg.append(tlPanel);
                        }
                        $this.addClass('none');
                        msg.css({width: 300, 'max-width': 300});
                        tlPanel.empty().html(editTlPanel);
                        editTlPanel.find('#tlContent').focus();
                        // 提交翻译结果
                        editTlPanel.find('.chat-btn-ok').on('click', function () {
                            var $btnOk = $(this),
                                tlCon = editTlPanel.find('#tlContent').val(),
                                gid = $chatTitle.attr('data-gid') || 0,
                                loading = $(sChatMsgLoading),
                                errTips = $btnOk.siblings('.error'),
                                pid = $(this).closest('.chat-main').attr('data-uuid') || 0;
                            if(tlCon.length<1){
                                if(!errTips.length){
                                    $btnOk.before('<span class="red error">翻译内容不能为空 </span>');
                                }
                            }else{
                                errTips.remove();
                                if (!$item.siblings('.loading').length) {
                                    $item.closest('.chat-main').append(loading);
                                }
                                self._sendChatMessage(getUuid, tlCon, 2, gid, pid);
                                tlPanel.empty().text(tlCon).attr('data-uuid', getUuid);
                                $this.removeClass('none');
                                msg.css({width: 'auto', 'max-width': 250});
                            }
                        });
                        // 取消翻译编辑
                        editTlPanel.find('.chat-btn-no').on('click', function () {
                            if (tlText) {
                                tlPanel.empty().text(tlText);
                            } else {
                                tlPanel.remove();
                            }
                            $this.removeClass('none');
                            msg.css({width: 'auto', 'max-width': 250});
                        });
                    });
                }
            });
        },
        /**
         * 翻译聊天消息
         */
        chatMsgTl: function (obj) {
            var self = this;
            obj.each(function () {
                var $this = $(this),
                    chatTl = $this.siblings('.chat-tl'),
                    boxH = $msgBox.height(),
                    loading = $(sChatMsgLoading);

                if (!$this.find('.tl').length) {
                    $this.after(loading);
                    chatTl.addClass('none');
                    self.translate(self._formatChatMsg($this.find('.chat-content')), function (tl) {
                        $this.append('<span class="tl">' + tl + '</span>');
                        loading.hide();
                        if ($this.position().top + $this.height() > $chatContent.height()) {
                            self.chatScrollTop(Math.abs($msgBox.position().top) + $msgBox.height() - boxH);
                        }
                    }, function () {
                        chatTl.removeClass('none');
                        loading.hide();
                    });
                }
            });
        },
        /**
         * 格式化含有表情的消息
         */
        _formatChatMsg: function (obj) {
            var self = this,
                txt = obj.html(),
                regList = [],
                img = obj.find('img');

            $.each(img, function (i, n) {
                regList.push($(n).prop('title') + ' ');
            });
            $.each(regList, function (i, n) {
                txt = txt.replace(/<img\s([^>]{0,})>/i, n);
            });
            return txt;
        },
        /**
         * 向上滚动加载聊天记录
         */
        _scrollBind: function () {
            var self = this;
            bLoad = true;
            if (bLoad) {
                if ($('.chat-warp').offset().top >= 55) {
                    self._getChatRecord(null, you.id);
                }
            }
        },
        /**
         * 获取历史聊天记录
         */
        _getChatRecord: function (errCall, youId, groupId) {
            var self = this;
            ajaxApi && ajaxApi.abort(); // 取消已有的ajax请求
            if (group && (!group.type || group.type == 'translate')) { // 翻译群不走本地存储聊天
                var $getChatBtn = $(".JS-get-chat"),
                    $loading = $(".chat-loading"),
                    loadFailed = $('<div class="load-failed">获取聊天记录失败，请<a href="javascript:">重试</a>。</div>'),
                    tlMsgList = [];

                pageParams.userId = youId;
                pageParams.groupId = groupId;
                pageParams.lastQueryDate = requestTime || '';
                $loading.show();
                $getChatBtn.addClass('none');
                ajaxApi = util.setAjax(inter.getApiUrl().moreChatRecordUrl, pageParams, function (data) {
                    pageParams.pageIndex = data.pageIndex;
                    requestTime = data.lastQueryDate;
                    if (data.isEmpty && $('.chat-body').length) {
                        $('.msg-box').prepend('<div class="noMore">没有更多消息了</div>');
                    } else {
                        self._renderChatList(data.list);
                    }
                    if (data.list.length < pageParams.pageSize) {
                        $loading.hide();
                        $getChatBtn.addClass('none');
                    } else {
                        $loading.hide();
                        $getChatBtn.removeClass('none');
                    }
                    if (self.first) {
                        self.chatScrollTop();
                        self.first = false;
                    }
                    self.updateChat({id1: group.id, id2: me.id});
                    if (errCall) {
                        errCall()
                    }
                }, function (data) {
                    $getChatBtn.after(loadFailed);
                    loadFailed.delegate('a', 'click', function () {
                        self._getChatRecord(errCall, youId);
                        loadFailed.remove();
                    })
                }, 'GET')
            } else {
                this._getLocalChatRecord(errCall, youId, groupId);
                self.updateChat({id1: groupId || youId, id2: me.id});
            }
        },
        /**
         * 获取云端未读聊天记录
         */
        _getNoReadChatData: function (id, msgNum, lastTime, call) {
            var self = this,
                $getChatBtn = $(".JS-get-chat"),
                $loading = $(".chat-loading"),
                loadFailed = $('<div class="load-failed">获取聊天记录失败，请<a href="javascript:">重试</a>。</div>');

            $loading.show();
            $getChatBtn.addClass('none');
            util.setAjax(inter.getApiUrl().queryChatDetail, {
                sendTime: lastTime, //发送时间
                userId: group ? '' : id, //用户id
                groupId: group ? id : '', //群组id
                preSize: msgNum, //向前查询行数，默认为5
                containCurrent: true //是否包含查询时间的这条记录
            }, function (data) {
                call && call(data);
            }, function () {
                $getChatBtn.after(loadFailed);
                loadFailed.delegate('a', 'click', function () {
                    self._getNoReadChatData(id, msgNum, lastTime, call);
                    loadFailed.remove();
                })
            }, 'GET')
        },
        /**
         * 获取本地聊天数据
         */
        _getLocalChatData: function (opts) {
            var self = this,
                pz = opts.pageSize || 10,
                p = opts.pageIndex || 1,
                returnData = {},
                forPage = 0,
                forData = [],
                localKey = me.id + 'And' + (group ? 'G' : '') + opts.uid;

            if (!self.localChatData) {
                self.localChatData = {};
            }
            //if(!self.localChatData[localKey]){
            var memArr = [];
            if (group) {
                memArr = $.merge(memArr, msgLS.readGroup(group.id) || []);
            } else {
                memArr = $.merge(memArr, msgLS.read(me.id, opts.uid) || []);
            }
            self.localChatData[localKey] = memArr.sort(function (a, b) {
                var result = 0,
                    aTime = a.sendTime || a.senderTime || 0,
                    bTime = b.sendTime || b.senderTime || 0;
                if (aTime < bTime) {
                    result = 1;
                } else if (aTime > bTime) {
                    result = -1;
                }
                return result;
            });
            //}
            for (var i = 1; i <= self.localChatData[localKey].length; i++) {
                forData.push(self.localChatData[localKey][i - 1]);
                if (i % pz == 0) {
                    forPage = parseInt(i / pz);
                    returnData[forPage] = forData;
                    forData = [];
                } else if (self.localChatData[localKey].length == i) {
                    forPage = parseInt(i / pz);
                    returnData[forPage + 1] = forData;
                    forData = [];
                }
            }
            opts.sucCall && opts.sucCall(returnData[p] || []);
        },
        /**
         * 获取本地聊天记录
         */
        _getLocalChatRecord: function (errCall, youId, groupId) {
            var self = this,
                $getChatBtn = $(".JS-get-chat"),
                $loading = $(".chat-loading"),
                noMoreDataTpl = '<div class="noMore">更多消息请在会话记录中查阅，<a href="#{url}">打开会话记录</a></div>',
                noMoreDateUrl = '';
            self._getLocalChatData({
                uid: groupId || youId,
                pageSize: 10,
                pageIndex: self.pageIndex,
                sucCall: function (localChatData) {
                    if (localChatData.length) {
                        localChatData.sort(function (a, b) {
                            var result = 0,
                                aTime = a.sendTime || 0,
                                bTime = b.sendTime || 0;
                            if (aTime > bTime) {
                                result = 1;
                            } else if (aTime < bTime) {
                                result = -1;
                            }
                            return result;
                        });
                        self._renderLocalChatList(localChatData);
                    }
                    if (localChatData && localChatData.length < 10) {
                        $loading.hide();
                        $getChatBtn.addClass('none');
                        if (localChatData.length && !self.first) {
                            if(group) {
                                noMoreDateUrl = '/record/group_detail?gid=' + group.id;
                            } else {
                                noMoreDateUrl = '/record/person_detail?uid=' + you.id;
                            }
                            noMoreDateUrl += '&time=' + (localChatData[0].sendTime || localChatData[0].dateTime);
                            $('.msg-box').prepend(util.template(noMoreDataTpl, {
                                url: noMoreDateUrl
                            }));
                        }
                    } else {
                        $loading.hide();
                        $getChatBtn.removeClass('none');
                    }
                    if (self.first) {
                        self.chatScrollTop();
                        self.first = false;
                    }
                    self.pageIndex = self.pageIndex + 1;
                }
            })
        },
        /**
         * 根据聊天数据渲染聊天记录
         */
        _renderLocalChatList: function (data) {
            var self = this,
                $getChatBtn = $(".JS-get-chat"),
                record = [];

            $.each(data, function (i, content) {
                var time = util.dateFormat(content.sendTime || content.dateTime, "yyyy-MM-dd hh:mm:ss"),
                    date = util.dateFormat(content.sendTime || content.dateTime, "yyyy-MM-dd"),
                    dateTime = util.dateFormat(content.sendTime || content.dateTime, "hh:mm:ss"),
                    showCls = '';
                if(prevDate && date > util.dateFormat(prevDate, 'yyyy-MM-dd')){
                    record.push(util.template(sChatLineTpl, {
                        date: date
                    }));
                    prevUid = null;
                }
                if(date == util.dateFormat(new Date(), "yyyy-MM-dd")){
                    time = dateTime;
                }
                if (group && content.type && content.type == 2) { //群邀请消息
                    record.push(util.template(sGroupTipsTpl, {
                        msgId: content.messageId,
                        chatTime: time,
                        msg: content.data || content.message
                    }))
                } else {
                    var voiceWidth = 40;
                    if (content.code == 307 || content.code == 317) { //群通知消息
                        record.push(util.template(sGroupTipsTpl, {
                            msgId: content.messageId,
                            chatTime: time,
                            msg: content.message
                        }));
                        prevUid = null;
                    } else if (content.code == 103) { //音视频通知消息
                        record.push(util.template(sGroupTipsTpl, {
                            msgId: content.messageId,
                            chatTime: time,
                            msg: content.data
                        }));
                        prevUid = null;
                    } else {
                        showCls = content.senderId == prevUid ? 'none' : '';
                        if (content.senderId == me.id) {
                            switch (content.code) {
                                case 3: //文字
                                    record.push(util.template(sChatMsgMeTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        msg: emotion.replaceEmotion(content.data),
                                        chatTime: time
                                    }));
                                    break;
                                case 8: //发送文件
                                    record.push(util.template(self._isPic(content.url) ? sChatMsgRecMePicTpl : sChatMsgRecMeFileTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil((content.fileSize || content.size) / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 11: //文件
                                    record.push(util.template(self._isPic(content.url) ? sChatMsgRecMePicTpl : sChatMsgYouFileTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil(content.fileSize / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 13: //语音
                                    if (content.multimediaLength > 20) {
                                        voiceWidth = 220;
                                    } else if (content.multimediaLength > 4) {
                                        voiceWidth = content.multimediaLength * 11;
                                    }
                                    record.push(util.template(sChatAudioMeTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        src: content.url,
                                        chatTime: time,
                                        voiceWidth: voiceWidth,
                                        voiceLength: content.multimediaLength || 1
                                    }));
                                    break;
                                case 305: //群文字
                                    if (content.subDate) { //有译文
                                        record.push(util.template(sChatMsgMeTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            msg: emotion.replaceEmotion(content.data),
                                            chatTime: time,
                                            tl: util.template(tlMsgTpl, {
                                                uuid: content.messageId,
                                                avatar: util.getAvatar(content.subSenderId),
                                                title: content.subName,
                                                message: content.subDate
                                            })
                                        }))
                                    } else {
                                        record.push(util.template(sChatMsgMeTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            msg: emotion.replaceEmotion(content.data),
                                            chatTime: time
                                        }));
                                    }
                                    break;
                                case 308: //发送群文件
                                    record.push(util.template(self._isPic(content.url) ? sChatMsgRecMePicTpl : sChatMsgRecMeFileTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil((content.fileSize || content.size) / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 311: //群文件
                                    record.push(util.template(self._isPic(content.url) ? sChatMsgRecMePicTpl : sChatMsgYouFileTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil(content.fileSize / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 313: //群语音
                                    if (content.multimediaLength > 20) {
                                        voiceWidth = 220;
                                    } else if (content.multimediaLength > 4) {
                                        voiceWidth = content.multimediaLength * 11;
                                    }
                                    record.push(util.template(sChatAudioMeTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        src: content.url,
                                        chatTime: time,
                                        voiceWidth: voiceWidth,
                                        voiceLength: content.multimediaLength || 1
                                    }));
                                    break;
                            }
                        } else {
                            switch (content.code) {
                                case 3: //文字
                                    record.push(util.template(sChatMsgYouTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        msg: emotion.replaceEmotion(content.data),
                                        chatTime: time
                                    }));
                                    break;
                                case 8: //发送文件
                                    record.push(util.template(self._isPic(content.url) ? sChatMsgYouPicTpl : sChatMsgRecMeFileTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil((content.fileSize || content.size) / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 11: //文件
                                    record.push(util.template(self._isPic(content.url) ? sChatMsgYouPicTpl : sChatMsgYouFileTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil(content.fileSize / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 13: //语音
                                    if (content.multimediaLength > 20) {
                                        voiceWidth = 220;
                                    } else if (content.multimediaLength > 5) {
                                        voiceWidth = content.multimediaLength * 11;
                                    }
                                    record.push(util.template(sChatAudioYouTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        src: content.url,
                                        chatTime: time,
                                        voiceWidth: voiceWidth,
                                        voiceLength: content.multimediaLength || 1
                                    }));
                                    break;
                                case 15:
                                    record.push(util.template(sChatMsgYouPicTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil(content.fileSize / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 305: //群文字
                                    if (content.subDate) { // 有译文
                                        if (self._isTranslator()) {
                                            record.push(util.template(sChatGroupYouTpl, {
                                                showCls: showCls,
                                                msgId: content.messageId,
                                                youName: content.senderName,
                                                msg: emotion.replaceEmotion(content.data),
                                                chatTime: time,
                                                tl: '<span class="tl">' + content.subDate + '</span>'
                                            }))
                                        } else {
                                            record.push(util.template(sChatGroupYouTpl, {
                                                showCls: showCls,
                                                msgId: content.messageId,
                                                youName: content.senderName,
                                                msg: emotion.replaceEmotion(content.data),
                                                chatTime: time,
                                                tl: util.template(tlMsgTpl, {
                                                    uuid: content.messageId,
                                                    avatar: util.getAvatar(content.subSenderId),
                                                    title: content.subName,
                                                    message: content.subDate
                                                })
                                            }))
                                        }
                                    } else {
                                        record.push(util.template(sChatGroupYouTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            youName: content.senderName,
                                            msg: emotion.replaceEmotion(content.data),
                                            chatTime: time
                                        }));
                                    }
                                    break;
                                case 308: //发送群文件
                                    record.push(util.template(self._isPic(content.url) ? sChatMsgYouPicTpl : sChatMsgRecMeFileTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil((content.fileSize || content.size) / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 311: //群文件
                                    record.push(util.template(self._isPic(content.url) ? sChatMsgYouPicTpl : sChatMsgYouFileTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil(content.fileSize / 1024),
                                        url: content.url
                                    }));
                                    break;
                                case 313: //群语音
                                    if (content.multimediaLength > 20) {
                                        voiceWidth = 220;
                                    } else if (content.multimediaLength > 5) {
                                        voiceWidth = content.multimediaLength * 11;
                                    }
                                    record.push(util.template(sChatAudioYouTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        src: content.url,
                                        chatTime: time,
                                        voiceWidth: voiceWidth,
                                        voiceLength: content.multimediaLength || 1
                                    }));
                                    break;
                                case 315: // 群图片
                                    record.push(util.template(sChatMsgYouPicTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: content.senderName,
                                        chatTime: time,
                                        name: content.fileName,
                                        size: Math.ceil(content.fileSize / 1024),
                                        url: content.url
                                    }));
                                    break;
                            }
                        }
                        prevUid = content.senderId;
                        prevDate = content.sendTime || content.dateTime;
                    }
                }
            });
            //添加记录到对话框
            var recordRender = $(record.join('')),
                boxH = $msgBox.height();
            $getChatBtn.after(recordRender);
            util.imgLoadError(recordRender.find('.tl-head'));//加载失败的图片替换为默认图片
            self._bindMediaEvent(recordRender);//绑定语音事件
            self._chatMsgTranslate(recordRender);//绑定手动/自动翻译事件
            if (self._isTranslator()) {//翻译者绑定事件
                self._chatGroupTranslate(recordRender);
            }
            // 加载图片后重置滚动条位置
            recordRender.find('.file-pic img').each(function () {
                var $this = $(this).closest('.chat-msg');
                this.onload = function () {
                    if ($this.position().top + $this.height() > $chatContent.height()) {
                        self.chatScrollTop(Math.abs($msgBox.position().top) + $msgBox.height() - boxH);
                    }
                }
            });
            // 更新未读消息后通知其他标签页
            util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {
            }, null, 'GET');
        },
        /**
         * 根据聊天数据渲染聊天记录
         */
        _renderChatList: function (data) {
            var self = this,
                $getChatBtn = $(".JS-get-chat"),
                record = [];

            $.each(data, function (i, o) {
                var time = util.dateFormat(o.msgTime, "yyyy-MM-dd hh:mm:ss"),
                    date = util.dateFormat(o.msgTime, 'yyyy-MM-dd'),
                    dateTime = util.dateFormat(o.msgTime, 'hh:mm:ss'),
                    content = o.content,
                    showCls = '';

                try {
                    content = $.parseJSON(content);
                } catch (e) {
                    util.trace('格式化聊天历史记录错误，错误数据：' + JSON.stringify(o));
                } finally {
                    if(prevDate && date > util.dateFormat(prevDate, 'yyyy-MM-dd')){
                        record.push(util.template(sChatLineTpl, {
                            date: date
                        }));
                        prevDate = null;
                    }
                    if(date == util.dateFormat(new Date(), 'yyyy-MM-dd')){
                        time = dateTime;
                    }
                    if(o.contentType){
                        if(!content.subType){
                            switch (o.contentType){
                                case 1: // 文本
                                    content.subType = 'text';
                                    break;
                                case 2: // 文件
                                    content.subType = 'file';
                                    break;
                                case 3: // 图片
                                    content.subType = 'picture';
                                    break;
                                case 4: // 语音
                                    content.subType = 'multimedia';
                                    break;
                                default:
                                    content.subType = 'text';
                                    break;
                            }
                        }
                        if(o.contentType >= 10){
                            content.type = 2;
                        }
                    }
                    if (group && content.type && content.type == 2) { //群邀请消息
                        record.push(util.template(sGroupTipsTpl, {
                            msgId: content.messageId,
                            chatTime: time,
                            msg: content.data
                        }))
                    } else {
                        showCls = o.userId == prevUid ? 'none' : '';
                        var voiceWidth = 40;
                        if (o.userId == me.id) {
                            if (content.subDate) { // 译文消息
                                record.push(util.template(sChatMsgMeTpl, {
                                    showCls: showCls,
                                    msgId: content.messageId,
                                    youName: o.userName,
                                    msg: emotion.replaceEmotion(content.data),
                                    chatTime: time,
                                    tl: util.template(tlMsgTpl, {
                                        uuid: content.messageId,
                                        avatar: util.getAvatar(content.subSenderId),
                                        title: content.subName,
                                        message: content.subDate
                                    })
                                }))
                            } else {
                                switch (content.subType) {
                                    case "text": //文字
                                        record.push(util.template(sChatMsgMeTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            msg: emotion.replaceEmotion(content.data),
                                            chatTime: time
                                        }));
                                        break;
                                    case "picture"://图片
                                        record.push(util.template(sChatMsgMePicTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            youName: o.userName,
                                            chatTime: time,
                                            name: content.fileName,
                                            size: Math.ceil(content.fileSize / 1024),
                                            url: content.url
                                        }));
                                        break;
                                    case "file": //文件
                                        record.push(util.template(self._isPic(content.url) ? sChatMsgRecMePicTpl : sChatMsgRecMeFileTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            chatTime: time,
                                            name: content.fileName,
                                            size: Math.ceil(content.fileSize / 1024),
                                            url: content.url
                                        }));
                                        break;
                                    case "multimedia": //语音
                                        if (content.multimediaLength > 20) {
                                            voiceWidth = 220;
                                        } else if (content.multimediaLength > 4) {
                                            voiceWidth = content.multimediaLength * 11;
                                        }
                                        record.push(util.template(sChatAudioMeTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            src: content.url,
                                            chatTime: time,
                                            voiceWidth: voiceWidth,
                                            voiceLength: content.multimediaLength || 1
                                        }));
                                        break;
                                }
                            }
                        } else {
                            if (content.subDate) { // 译文消息
                                if (self._isTranslator()) {
                                    record.push(util.template(sChatGroupYouTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: o.userName,
                                        msg: emotion.replaceEmotion(content.data),
                                        chatTime: time,
                                        tl: '<span class="tl">' + content.subDate + '</span>'
                                    }))
                                } else {
                                    record.push(util.template(sChatGroupYouTpl, {
                                        showCls: showCls,
                                        msgId: content.messageId,
                                        youName: o.userName,
                                        msg: emotion.replaceEmotion(content.data),
                                        chatTime: time,
                                        tl: util.template(tlMsgTpl, {
                                            uuid: content.messageId,
                                            avatar: util.getAvatar(content.subSenderId),
                                            title: content.subName,
                                            message: content.subDate
                                        })
                                    }))
                                }
                            } else {
                                switch (content.subType) {
                                    case "text": //文字
                                        record.push(util.template(sChatGroupYouTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            youName: o.userName,
                                            msg: emotion.replaceEmotion(content.data),
                                            chatTime: time
                                        }));
                                        break;
                                    case "picture":
                                        record.push(util.template(sChatMsgYouPicTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            youName: o.userName,
                                            chatTime: time,
                                            name: content.fileName,
                                            size: Math.ceil(content.fileSize / 1024),
                                            url: content.url
                                        }));
                                        break;
                                    case "file": //文件
                                        record.push(util.template(self._isPic(content.url) ? sChatMsgYouPicTpl : sChatMsgYouFileTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            youName: o.userName,
                                            chatTime: time,
                                            name: content.fileName,
                                            size: Math.ceil(content.fileSize / 1024),
                                            url: content.url
                                        }));
                                        break;
                                    case "multimedia": //语音
                                        if (content.multimediaLength > 20) {
                                            voiceWidth = 220;
                                        } else if (content.multimediaLength > 5) {
                                            voiceWidth = content.multimediaLength * 11;
                                        }
                                        record.push(util.template(sChatAudioYouTpl, {
                                            showCls: showCls,
                                            msgId: content.messageId,
                                            youName: o.userName,
                                            src: content.url,
                                            chatTime: time,
                                            voiceWidth: voiceWidth,
                                            voiceLength: content.multimediaLength || 1
                                        }));
                                        break;
                                }
                            }
                        }
                        prevUid = o.userId;
                        prevDate = o.msgTime;
                    }
                }
            });
            //添加记录到对话框
            var recordRender = $(record.join('')),
                boxH = $msgBox.height();
            $getChatBtn.after(recordRender);
            util.imgLoadError(recordRender.find('.tl-head'));//加载失败的图片替换为默认图片
            self._bindMediaEvent(recordRender);//绑定语音事件
            self._chatMsgTranslate(recordRender);//绑定手动/自动翻译事件
            if (self._isTranslator()) {//翻译者绑定事件
                self._chatGroupTranslate(recordRender);
            }
            // 加载图片后重置滚动条位置
            recordRender.find('.file-pic img').each(function () {
                var $this = $(this).closest('.chat-msg');
                this.onload = function () {
                    if ($this.position().top + $this.height() > $chatContent.height()) {
                        self.chatScrollTop(Math.abs($msgBox.position().top) + $msgBox.height() - boxH);
                    }
                }
            });
            // 更新未读消息后通知其他标签页
            util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {
            }, null, 'GET');
        },
        /**
         * 格式化云端数据存储到本地
         */
        _formatChatData: function (data) {
            var self = this,
                content = {},
                otherData = group || you,
                prevMessageId = null,
                isAdd = true;
                returnList = [];

            if (data) {
                $.each(data, function (i, n) {
                    var json = {};
                    isAdd = true;
                    try {
                        content = $.parseJSON(n.content);
                    } catch (e) {
                        util.trace('格式化JSON错误：' + n);
                    } finally {
                        if (!$.isEmptyObject(content)) {
                            json.messageId = content.messageId;
                            if(prevMessageId != json.messageId){
                                json.data = content.data;
                                json.senderId = n.userId;
                                json.senderName = n.userName;
                                if (n.groupId) {
                                    json.groupId = n.groupId;
                                    json.groupName = n.groupName;
                                    if (content.message) {
                                        json.message = content.message;
                                    }
                                } else {
                                    if (n.userId == me.id) {
                                        json.receiverId = otherData.id;
                                        json.receiverName = otherData.name;
                                    } else {
                                        json.receiverId = me.id;
                                        json.receiverName = me.name;
                                    }
                                }
                                json.sendTime = n.msgTime;
                                if(n.contentType){
                                    if(!content.subType){
                                        switch (n.contentType){
                                            case 1: // 文本
                                                content.subType = 'text';
                                                break;
                                            case 2: // 文件
                                                content.subType = 'file';
                                                break;
                                            case 3: // 图片
                                                content.subType = 'picture';
                                                break;
                                            case 4: // 语音
                                                content.subType = 'multimedia';
                                                break;
                                            default:
                                                content.subType = 'text';
                                                break;
                                        }
                                    }
                                }
                                switch (content.subType) {
                                    case "text": //文字
                                        if(json.groupId){
                                            json.code = 305;
                                            if(content.type == 2){
                                                json.code = 307;
                                                json.message = content.data;
                                            }
                                        }else{
                                            json.code = 3;
                                        }
                                        break;
                                    case "file": //文件
                                        if (n.userId == me.id) {
                                            json.code = json.groupId ? 308 : 8;
                                        } else {
                                            json.code = json.groupId ? 311 : 11;
                                            json.url = content.url;
                                            json.fileName = content.fileName;
                                            json.fileSize = content.fileSize;
                                        }
                                        break;
                                    case "multimedia": //语音
                                        json.code = json.groupId ? 313 : 13;
                                        json.url = content.url;
                                        json.multimediaLength = content.multimediaLength;
                                        break;
                                    case "picture": //图片
                                        json.code = json.groupId ? 315 : 15;
                                        json.url = content.url;
                                        json.fileName = content.fileName;
                                        json.fileSize = content.fileSize;
                                        break;
                                }
                                prevMessageId = json.messageId;
                            }else{
                                isAdd = false;
                            }
                        }
                        if(isAdd){
                            returnList.push(json);
                        }
                    }
                });
            }
            return returnList;
        },
        /**
         * 验证是否是翻译者
         */
        _isTranslator: function () {
            var isTranslator = false;
            if (group && group.member && group.type == 'translate') {
                $.each(group.member, function (i, n) {
                    if (n.userId == me.id && n.role == 'TRANSLATE') {
                        isTranslator = true;
                        return false;
                    }
                })
            }
            return isTranslator;
        },
        /**
         * 绑定音视频/发送消息/更多消息按钮事件
         */
        _bindEvens: function () {
            var self = this,
                videoMe = $('.video-me');

            $('.icon-close-me').on('click', function () {
                videoMe.addClass('opacity0');
            });
            $('.icon-show-me').on('click', function () {
                videoMe.removeClass('opacity0');
            });
            $(".JS-get-chat").on("click", function () {
                if (group) {
                    self._getChatRecord(null, 0, group.id);
                } else {
                    self._getChatRecord(null, you.id);
                }
            });
        },

        /**
         * 打开时视屏窗口 ; 0 视频邀请，1 接受视频邀请  2 音频邀请  3  接受音频邀请
         */
        openVideo: function (init) {
            var id = 0;
            var leftSize = ($(document).width() - 594) / 2,
                id = cookie.get("inviteeId");
            if (parseInt(init) % 2 === 1) {
                id = cookie.get("senderId");
            }
            if (!id) {
                util.trace('未取到对方id，无法音视频');
                return;
            }
            window.open('/chat/' + id + '?init=' + init, 'chat', 'width=594,height=446,left=' + leftSize + ',top=200, resizable=no');
        },
        /**
         * chatTitleDown 里面显示的 内容
         * type ：类型，''(传空值  隐藏) invite-video(收到视频邀请), invite-voice(收到音频邀请), notice
         * name ：username
         * time:  定时器时间second
         * content: 显示内容
         * sync : 是否同步到其他tab页(默认为false)
         */
        notice: function (type, name, content, time, sync) {

            sync =  sync == 'true' || sync == '1';

            if (sync) {
                //通知其他tab页
                socket.sendData(JSON.stringify({
                    "code": 400,
                    "userId": cookie.get('_u_id'),
                    "data": {
                        "date": new Date().getTime(),
                        'action': 'notice',
                        "type": type,
                        "name": name,
                        "content": content,
                        "time": time
                    }
                }));
            }


            util.clearCountdown('noticeInfo');
            util.clearCountdown('video');

            var $titleDown = $('#chatTitleDown');
            if (!type) {
                $titleDown.addClass('none');
                return;
            } else {
                var $type = $titleDown.find('.' + type);
                $titleDown.find('p').addClass('none');
                $titleDown.removeClass('none');
                $titleDown.find('.user-name').text(name || "");
                $type.removeClass('none');
                if (type == 'notice') {
                    $titleDown.find('.notice .Js-content').html(content);

                    if (/^正在邀请<strong class="user-name">/.test(content)) {
                        //显示挂断按钮
                        $titleDown.find('.notice .chat-btn-no').removeClass('none');
                    } else {
                        $titleDown.find('.notice .chat-btn-no').addClass('none');
                    }

                    //设置notice消失时间
                    if (time && time > 0) {
                        util.countdown(null, time, function () {
                            window.webIM.notice();
                            util.clearCountdown('noticeInfo');
                        }, null, 'noticeInfo')
                    }
                } else {
                    var $time = $type.find('.keep-time');
                    window.inviterNoticer = util.countdown(null, (time && time > 0) ? time : 60, function () {
                        window.webIM.notice();
                        window.webIM.changeVideo(true,true);
                        util.clearCountdown();
                    }, function (t) {
                        if ($time.length > 0)
                            $time.html("(" + ( (t < 10) ? ('0' + t ) : t) + ")");
                    }, 'video')

                }
            }
        },

        disable: function (target, flag) {
            flag = (typeof flag === 'boolean') ? flag : true;
            var clone = '<span class="un-use"></div>',
                $prev = target.prev('.un-use');
            if (flag) {
                if (!$prev.length)
                    target.before(clone);
            } else {
                $prev.remove();
            }
        },
        /**
         * boolean 用来改变视频状态         * true : 启用         * false:禁用
         * sync  是否同步到其他tab页
         */
        changeVideo: function (boolean, sync) {
            var self = this,
                boolean = boolean == '1' || boolean == 'true',
                $that = $chatTitle.find('.i-video');

            sync = (typeof sync == 'undefined') ? true : sync;

            if (sync) {
                //通知其他tab页
                socket.sendData(JSON.stringify({
                    "code": 400,
                    "userId": cookie.get('_u_id'),
                    "data": {
                        "date": new Date().getTime(),
                        "action": 'videoStatus',
                        "boolean": boolean
                    }
                }));
            }


            if (boolean) {
                $that.removeClass('i-video-h')
            } else {
                $that.addClass('i-video-h');
            }
            self.disable($that, !boolean);

            //当禁用 视频时，也禁用音频
            self.changeVoice(boolean);
        },
        /**
         * 用来改变语音状态
         * true : 启用
         * false:禁用
         */
        changeVoice: function (boolean) {
            var self = this,
                boolean = (typeof boolean === 'boolean') ? boolean : true,
                $that = $chatTitle.find('.i-voice');
            if (boolean) {
                $that.removeClass('i-voice-h')
            } else {
                $that.addClass('i-voice-h');
            }
            self.disable($that, !boolean);
        },

        /**
         * 判断后缀是否是图片
         */
        _isPic: function (fn) {
            var allowExt = ".jpg,.bmp,.gif,.png,.jpeg,.tiff,.tga",
                ext = /\.[^\.]+$/.exec(fn);

            return allowExt.indexOf(ext) > -1;
        },
        /**
         * 本地图片预览
         */
        _previewImage: function (fileObj, noCall) {
            var self = this,
                browserVersion = window.navigator.userAgent.toUpperCase();


            if (self._isPic(fileObj.value)) {
                var divPreview = $(util.template(sChatMsgMePicTpl, {
                    msgId: self.fileSendUuid,
                    url: 'javascript:',
                    chatTime: util.dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss')
                }));
                $msgBox.append(divPreview);
                var imgPreview = divPreview.find('.file-pic img');
                if (fileObj.files) {//HTML5实现预览，兼容chrome、火狐7+等
                    if (window.FileReader) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            imgPreview.prop("src", e.target.result);
                        }
                        reader.readAsDataURL(fileObj.files[0]);
                    } else if (browserVersion.indexOf("SAFARI") > -1) {
                        //alert("不支持Safari6.0以下浏览器的图片预览!");
                        if (noCall) {
                            return noCall();
                        }
                    }
                } else if (browserVersion.indexOf("MSIE") > -1) {
                    if (browserVersion.indexOf("MSIE 6") > -1) {//ie6
                        imgPreview.prop("src", fileObj.value);
                    } else {//ie[7-9]
                        fileObj.select();
                        if (browserVersion.indexOf("MSIE 9") > -1)
                            fileObj.blur();//不加上document.selection.createRange().text在ie9会拒绝访问
                        var newPreview = $('#' + divPreview + "New");
                        if (!newPreview.length) {
                            newPreview = $('<div id="' + divPreview + 'New"></div>');
                            newPreview.width(imgPreview.width());
                            newPreview.height(imgPreview.height());
                            newPreview.css("border", "solid 1px #d2e2e2");
                        }
                        newPreview[0].style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod='scale',src='" + document.selection.createRange().text + "')";
                        var tempDivPreview = $(divPreview);
                        tempDivPreview.before(newPreview);
                        tempDivPreview.hide();
                    }
                } else if (browserVersion.indexOf("FIREFOX") > -1) {//firefox
                    var firefoxVersion = parseFloat(browserVersion.toLowerCase().match(/firefox\/([\d.]+)/)[1]);
                    if (firefoxVersion < 7) {//firefox7以下版本
                        imgPreview.prop("src", fileObj.files[0].getAsDataURL());
                    } else {//firefox7.0+
                        imgPreview.prop("src", window.URL.createObjectURL(fileObj.files[0]));
                    }
                } else {
                    imgPreview.prop("src", fileObj.value);
                }
                return divPreview;
            } else {

                /*fileObj.value = "";//清空选中文件
                 if(browserVersion.indexOf("MSIE")>-1){
                 fileObj.select();
                 document.selection.clear();
                 }

                 fileObj.outerHTML = fileObj.outerHTML;*/
                if (noCall) {
                    return noCall();
                }
            }
        },
        /**
         * 翻译接口
         */
        translate: function (words, call, errCall) {
            var costTime = +new Date();
            util.trace('翻译中...', 'red');
            util.setAjax(inter.getApiUrl().getTranslateUrl, {q: words}, function (json) {
                if (!json.errorCode) {
                    call && call(json.results[0].dst);
                    util.trace('翻译语种：' + json.from + ' → ' + json.to, 'red');
                    util.trace('翻译原文：' + words, 'red');
                    util.trace('翻译结果：' + json.results[0].dst, 'red');
                    util.trace('翻译耗时：' + (+new Date() - costTime) / 1000 + '秒', 'red');
                } else {
                    errCall && errCall(json);
                    util.trace('翻译错误：errCode:' + json.errorCode + ' errMsg:' + json.errorMsg, 'red');
                }
            }, errCall)
        },
        /**
         * 设置与某人/某群的消息为已读
         */
        updateChat: function (args, sucCall, errCall) {
            var url = util.strFormat(inter.getApiUrl().updateChatMsgUrl, [args.id1, args.id2]);
            if (group) {
                url = util.strFormat(inter.getApiUrl().updateGroupMsgUrl, [args.id1, args.id2]);
            }
            util.setAjax(url, {}, function (json) {
                if (json.error) {
                    errCall && errCall(json);
                } else {
                    sucCall && sucCall(json);
                    util.setAjax(inter.getApiUrl().updateMsgUrl, {}, function () {

                    }, null, 'GET')
                }
            }, function () {
                errCall && errCall(json);
            }, 'GET')
        },
        /**
         * 检测是否有消息发送超时
         */
        checkChatSend: function () {
            var self = this,
                timeout = 20000;
            self.sendChatTimer = setInterval(function () {
                if (window.sendChatArr && !$.isEmptyObject(window.sendChatArr)) {
                    $.each(window.sendChatArr, function (i, n) {
                        if (+new Date() - n.sendTime > timeout) {
                            self.setChatSendErr(n.data);
                            delete window.sendChatArr[i]
                        }
                    })
                }
            }, 5000)
        },
        /**
         * 设置某条消息因发送超时而失败
         */
        setChatSendErr: function (json) {
            var self = this,
                chatMsg = $msgBox.find('.chat-main[data-uuid="' + (json.parentId && json.parentId != '0' ? json.parentId : json.messageId) + '"]'),
                errorTips = $('<span class="chat-error fr"><em class="chat-icon i-send-error" title="发送失败，点击重新发送"></em></span>');

            if (chatMsg.length) {
                chatMsg.find('.loading').hide();
                chatMsg.append(errorTips);
                if (chatMsg.closest('.chat-left').length) {
                    errorTips.removeClass('fr').addClass('fl').css('padding', '0 0 0 10px');
                }
                errorTips.on('click', function () {
                    errorTips.remove();
                    chatMsg.find('.loading').show();
                    if (json.code == 10 || json.code == 310) {
                        self._readFile(json);
                    } else {
                        socket.sendData(JSON.stringify(json), function () {
                            self.setChatSendErr(json);
                        });
                    }
                })
            }
        },
        /**
         * 设置某条消息发送成功
         */
        setChatSendSuc: function (mid) {
            var self = this,
                chatMsg = $msgBox.find('.chat-main[data-uuid="' + mid + '"]');

            if (chatMsg.length) {
                chatMsg.find('.loading').remove();
            }
        }
    };
    window.webIM = webIM;
    return webIM;
});
