/**
 * @description:  各种 code 的事件绑定
 * @author: Zhang Guanglin(zhangguang.lin@163.com)
 * @update :zhiqinag.zhou(zhiqinag.zhou@helome.com) 2014-5-15
 */
define('module/consult',['common/util', 'module/chatUtil'],function(util,chatUtil){
    var rtc = window.rtc;
    var init = function(){
        //加载对方视频
        rtc.on('add remote stream', function(stream,id){
            util.trace('add remote stream');

            rtc.attachStream(stream, 'remoteVideo');
            if(!rtc.isAudio)
                $('#init').hide();
            chatUtil.playDetection();
        });

        //结束视频
        rtc.on('disconnect stream', function(id){
            util.trace('peer connection ' + id + ' closed');
            chatUtil.closePage();
        });

        //监听到接收消息
        rtc.on("2", function(data){
            rtc._socket.send(JSON.stringify({"code":2}));
        });

        //自己正常下线
        rtc.on("20", function(data){
            chatUtil.closePage();
        });
        //
        rtc.on("21", function(data){
            chatUtil.closePage();
        });

        rtc.on('105',function(data){
            if(data.senderId == you.id && data.receiverId == me.id){
                /* me and  you on line */
                rtc.onLine = true;
            }
        })

        //有人离开
        rtc.on("108", function(data){
//            if(data.senderId == you.id){
//                if(rtc.peerConnections[you.id])rtc.peerConnections[you.id].close();
//                delete rtc.peerConnections[you.id];
//                delete rtc.connections[you.id];
//                rtc.peerConnections = {};
//                rtc.connections = [];
//                rtc.streams = [];
//                $('.icon-break').addClass('none');
//                $('.video-me').addClass('none');
//                chatUtil.setVideoStatus('status-init');
//                //注释掉：当对方拒绝调用摄像头；再重新调用，会发生108
//                /* 修改逻辑 拒绝后需到browser设置*/
//                /* mc有改动,刷新任意tab页都会有108, update 8-27*/
////                chatUtil.closePage();
//            }
        });

        //对方接受视频
        rtc.on("211", function(data){
            if(data.inviterId == me.id && data.userId == you.id)
                chatUtil.setVideoStatus('status-wait-conn-video');
        });

        //对方拒绝视频
        rtc.on("212", function(data){
            if(data.inviterId == me.id && data.userId == you.id){
                chatUtil.reset();
                chatUtil.setVideoStatus('status-refuse-video');
                if(rtc.peerConnections[you.id])rtc.peerConnections[you.id].close();
                delete rtc.peerConnections[you.id];
                delete rtc.connections[you.id];
                rtc.peerConnections = {};
                rtc.connections = [];
                rtc.streams = [];
                //3秒后关闭页面，否则后续操作有误
                chatUtil.closePage(5);
            }
        });

        //视频请求超时
        rtc.on("213", function(data){
            if(data.userId == me.id && data.inviteeId == you.id){
                chatUtil.setVideoStatus('status-video-time-out');
                $('.video-me').addClass('none')
            }
            //关闭页面，否则后续操作有误
            chatUtil.closePage(0);
        });


        //对方接受音频
        rtc.on("221", function(data){
            if(data.inviterId == me.id && data.userId == you.id){
                chatUtil.setVideoStatus('status-wait-conn-voice');
            }else{
                util.trace('221 bind error '+JSON.stringify(data))
            }
        });

        //拒绝对方音频
        rtc.on("222", function(data){
            if(data.inviterId == me.id && data.userId == you.id){
                chatUtil.reset();
                chatUtil.setVideoStatus('status-refuse-voice');
                if(rtc.peerConnections[you.id])rtc.peerConnections[you.id].close();
                delete rtc.peerConnections[you.id];
                delete rtc.connections[you.id];
                rtc.peerConnections = {};
                rtc.connections = [];
                rtc.streams = [];
            }
        });

        //音频请求超时
        rtc.on("223", function(data){
            if(data.userId == me.id && data.inviteeId == you.id){
                chatUtil.setVideoStatus('status-voice-time-out');
            }
        });



        rtc.on('230', function(data){
            if(data.receiverId == me.id && data.senderId == you.id){
                util.trace("receive Offer  230");
                rtc.receiveOffer(you.id, data.sdp);
            }
        })

        rtc.on('231', function(data){
            if(data.receiverId == me.id && data.senderId == you.id){
                util.trace("receive Answer  231");
                rtc.receiveAnswer(you.id, data.sdp);
            }
        })

        rtc.on('232', function(data){
            if(data.receiverId == me.id && data.senderId == you.id){
                var candidate = new nativeRTCIceCandidate(data.candidate);
                var pc = rtc.peerConnections[you.id];
                if(pc)  pc.addIceCandidate(candidate);
            }
        })

        //对方没有视频设备
        rtc.on('242', function(data){
            if(data.receiverId == me.id && data.senderId == you.id){
                chatUtil.otherNoVideoTips();
                chatUtil.setVideoStatus('status-init');
                $('.video-me').addClass('none');
                chatUtil.closePage(0);
            }
        });

        //对方没有音频设备
        rtc.on('252', function(data){
            util.trace("252")
            if(data.receiverId == me.id && data.senderId == you.id){
                chatUtil.otherNoAudioTips();
                chatUtil.setVideoStatus('status-init');
                $('.video-me').addClass('none');
                chatUtil.closePage(0);
            }
        });

        //对方挂断视频;(或自己主动挂断)
        rtc.on("260", function(data){
            if(data.receiverId == me.id && data.senderId == you.id){

                if(rtc.peerConnections[you.id])rtc.peerConnections[you.id].close();
                delete rtc.peerConnections[you.id];
                delete rtc.connections[you.id];
                rtc.peerConnections = {};
                rtc.connections = [];
                rtc.streams = [];
                $('.icon-break').addClass('none');
                chatUtil.setVideoStatus('status-video-you-end');
                $('.video-me').addClass('none');
            }
            chatUtil.closePage();
        });

        //自己正在视频或者音频中
        rtc.on('270', function(data){
            if(data.inviteeId == you.id && data.userId == me.id ){
                util.trace('您正在与其他人视频或者音频通话中！');
            }
        })

        //对方正在视频或者音频中
        rtc.on('271', function(data){
//            if(data.inviteeId == you.id && data.userId == me.id ){
                util.trace(you.name+'正在视频或者音频通话中！');
//            }
            chatUtil.closePage(5);
        });

        rtc.on('280',function(data){
            switch (data.state){
                case 0:
                    break;
                case 1:
                    util.trace('data :'+ data);
                    rtc.connections.push(you.id)
                    rtc.fire('ready');
                    break;
                case 2:
                    util.trace('请求超时');
                    chatUtil.setVideoStatus('status-video-time-out');
                    $('.video-me').addClass('none')
                    //关闭页面，否则后续操作有误
                    chatUtil.closePage(0);
                    break;
                case 3:
                    util.trace('其它错误');
                    chatUtil.connectErrTips();
                    chatUtil.closePage(0);
                    break;
                default :
                    chatUtil.closePage();
                    break;
            }
        });
    }
    return {
        init : init
    }
});
