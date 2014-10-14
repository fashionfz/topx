/**
 * @description:  视频音频的基方法
 * @author: Zhang Guanglin
 * @update: 2014-5-21 zhouzhiqiang
 */
define('module/chatUtil', ['common/util','module/webRtc', 'module/uuid'], function (util, webRtc, uuid) {

    /* 检测视频音频是否正常播放 */
    /*
    0 = HAVE_NOTHING - 没有关于音频/视频是否就绪的信息
    1 = HAVE_METADATA - 关于音频/视频就绪的元数据
    2 = HAVE_CURRENT_DATA - 关于当前播放位置的数据是可用的，但没有足够的数据来播放下一帧/毫秒
    3 = HAVE_FUTURE_DATA - 当前及至少下一帧的数据是可用的
    4 = HAVE_ENOUGH_DATA - 可用数据足以开始播放
    */

    var playDetection = function () {
            var self = this;
            rtc.connectionTimeCount += 1000;
            if (rtc.connectionTimeCount >= rtc.connectionTimeout) {
                $.alert('您的网络不稳定，请稍后尝试');
                $('#video-status').addClass('none');
                $('.icon-break').addClass('none');
            }

            if (document.getElementById('remoteVideo').readyState == 4) {

                $('.icon-break').removeClass('none');
                if (rtc.isAudio) {
                    self.setVideoStatus('status-voice');
                } else {
                    $('.video-me').removeClass('none')
                }

            } else {
                setTimeout(function () {
                    self.playDetection();
                }, 1000)
            }
        },
        /**
         * 改变视频提示状态
         * 参数status
         * "status-close-video":对方已关闭视频头
         * "status-refuse-video":对方已拒绝您的视频请求
         * "status-voice":语音链接成功，正在语音通话
         * "status-wait-confirm-video":正在等待对方确认视频通话
         * "status-wait-confirm-voice":正在等待对方确认音频通话
         * "status-wait-conn-video":对方已确认，正在为你连接视频
         * "status-wait-conn-voice":对方已确认，正在为你连接音频
         * "status-conn-video":正在为你连接视频
         * "status-conn-voice":正在为你连接音频
         * "status-video-time-out":视频请求超时
         * "status-voice-time-out":音频请求超时
         * "status-end":通话已结束
         * "status-init"点击图标开始于对方视频或语音
         * "status-video-you-end" : 对方挂断视频
         * "status-refuse-voice" :对方已拒绝您的音频请求
         * */
        setVideoStatus = function (status) {
            if (status != 'none') {
                $('#video-status').removeAttr('class').addClass(status);
                $('.init-bg').removeClass('none').show();
            } else {
                $('.init-bg').addClass('none')
            }
        },
    //隐藏提示信息
        hideInfo = function () {
            $('.info-content').addClass('none');
            $('.cost-content').addClass('none');
            $('.video-tips').remove();
        },
        selfNoVideoTips = function () {
            hideInfo();
            $('.caption').append('<div class="video-tips no-video-tips">未检测到视频设备<i class="icon-video icon-info-triangle"></i></div>');
        },
        /**
         * 未检测到对方视频设备
         */
        otherNoVideoTips =  function () {
            hideInfo();
            $('.caption').append('<div class="video-tips no-video-tips">未检测到对方视频设备<i class="icon-video icon-info-triangle"></i></div>');
        },
        /**
         * 未检测到音频设备
         */
        selfNoAudioTips =  function () {
            hideInfo();
            $('.caption').append('<div class="video-tips no-audio-tips">未检测到音频设备<i class="icon-video icon-info-triangle"></i></div>');
        },
        /**
         * 未检测到对方音频设备
         */
        otherNoAudioTips =  function () {
            hideInfo();
            $('.caption').append('<div class="video-tips no-audio-tips">未检测到对方音频设备<i class="icon-video icon-info-triangle"></i></div>');
        },
        /**
         * 连接错误
         */
        connectErrTips =  function () {
            hideInfo();
            $('.caption').append('<div class="video-tips no-audio-tips">连接错误<i class="icon-video icon-info-triangle"></i></div>');
        },
        getVideoFailed = function(){
            var data = {
                "code": 242,
                "senderId": me.id,
                "senderName": me.name,
                "receiverId": you.id,
                "receiverName": you.name,
                "sessionId": 0,
                "date": +new Date(),
                "messageId": uuid.get()
            };
            rtc._socket.send(JSON.stringify(data));
            closePage();
        },
        getAudioFailed = function(){
            var data = {
                "code": 252,
                "senderId": me.id,
                "senderName": me.name,
                "receiverId": you.id,
                "receiverName": you.name,
                "sessionId": 0,
                "date": +new Date(),
                "messageId": uuid.get()
            };
            rtc._socket.send(JSON.stringify(data));
            closePage();
        }
    /*  视频请求 */
        videoRequest = function () {
            util.trace('videoRequest');
            setVideoStatus('status-conn-video');
            $('.video-me').removeClass('none');
            rtc.createStream(
                {"video": true, "audio": true},
                function (stream) {
                    rtc.attachStream(stream, 'localVideo');
                },
                function (error) {
                    rtc.peerConnections = {};
                    rtc.connections = [];
                    rtc.streams = [];
                    selfNoVideoTips();
                    if(rtc.onLine){
                        getVideoFailed();
                    }else{
                        rtc.on('105',function(){
                            getVideoFailed();
                        });
                    }
                }
            );
        },

    /* 音频请求*/
        audioRequest = function () {
            setVideoStatus('status-conn-video');
            rtc.createStream(
                {"video": false, "audio": true},
                function (stream) {
                    $('.video-me').addClass('none');
                },
                function (error) {
                    rtc.peerConnections = {};
                    rtc.connections = [];
                    rtc.streams = [];

                    selfNoAudioTips();
                    if(rtc.onLine){
                        getAudioFailed();
                    }else{
                        rtc.on('105',function(){
                            getAudioFailed();
                        })
                    }
                }
            );
        },

    //挂断视频;
        hangUp = function () {
            var self = this,
                data = {};
            data.code = 260;
            data.senderId = me.id;
            data.senderName = me.name;
            data.receiverId = you.id;
            data.receiverName = you.name;
            data.date = +new Date();
            data.sessionId = 0;
            data.messageId = uuid.get();
            rtc._socket.send(JSON.stringify(data));
            if (rtc.peerConnections[you.id])rtc.peerConnections[you.id].close();
            delete rtc.streams[0];
            delete rtc.peerConnections[you.id];
            delete rtc.connections[you.id];
            rtc.peerConnections = {};
            rtc.connections = [];
            rtc.streams = [];
            window.isHangUp = false;
            self.reset();
            self.closePage();

        },

        reset = function () {
            $('.icon-break').addClass('none');
            setVideoStatus('status-end');
            $('.video-me').addClass('none');
        },

        ab2str = function (buf) {
            return String.fromCharCode.apply(null, new Uint16Array(buf));
        },
        str2ab = function (str) {
            var buf = new ArrayBuffer(str.length * 2); // 2 bytes for each char
            var bufView = new Uint16Array(buf);
            for (var i = 0, strLen = str.length; i < strLen; i++) {
                bufView[i] = str.charCodeAt(i);
            }
            return buf;
        },

        closePage = function (time) {
            util.trace('will  closePage .....');
            var dev = window.localStorage.getItem('dev');
            dev = dev == "true" || dev == "1";
            if( dev ){
                util.trace('dev module');
                return;
            }
            time = parseInt(time || 0);
            setTimeout(function () {
                window.close();
            }, time*1000);
        };

    return {
        playDetection: playDetection,
        videoRequest: videoRequest,
        audioRequest: audioRequest,
        hangUp: hangUp,
        reset: reset,
        ab2str: ab2str,
        str2ab: str2ab,
        hideInfo: hideInfo,
        setVideoStatus: setVideoStatus,
        closePage: closePage,
        otherNoVideoTips: otherNoVideoTips,
        otherNoAudioTips: otherNoAudioTips,
        connectErrTips: connectErrTips
    }
})
