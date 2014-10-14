/**
 * @description: 视频入口
 */
require(
    ['common/util', 'module/chatUtil', 'module/cookie', 'module/video', 'module/consult'],
    function (util, chatUtil, cookie, video, consult) {
        window.isHangUP = true;
        video.init();
        consult.init();
        var init = parseInt(util.location('init'), 10);
        rtc.on('0', function () {
            switch (init) {
                case 0:
                case 1:
                    chatUtil.videoRequest();
                    break;
                case 2:
                case 3:
                default :
                    rtc.isAudio = true;
                    window.sdpConstraints.mandatory.OfferToReceiveVideo = false;
                    chatUtil.audioRequest();
                    chatUtil.setVideoStatus('status-wait-confirm-voice');
                    break;
            }
        });
        util.onBeforeBomUnload(function () {
            if(window.isHangUp){
                chatUtil.hangUp();
            }
        });
    });
