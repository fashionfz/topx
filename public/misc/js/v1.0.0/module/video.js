/**
 * @description: 视频模块
 * @author: Zhang Guanglin
 * @create: 2013-11-18
 */
define('module/video', [
    'common/interface',
    'common/util',
    'module/commentStar',
    'module/chatUtil'
], function (inter, util, star, chatUtil) {

    //查看对方信息 弹出面板的模板
    var ajaxApi = null,
        sExpertInfo = [
        '<div class="info-top clearfix">',
            '<div class="user-info">',
                '<div class="expert-por">',
                '<a href="#{linkUrl}" target="_blank">',
                    '<em></em><img class="img-full" src="#{headImgUrl}" alt="#{userName}" title="#{userName}">',
                '</a>',
                '</div>',
                '<div class="country">',
                    '<span class="country-flag flag #{countryImgUrl}" title="#{country}"></span>',
                    '<img alt="#{gender}" title="#{gender}" src="#{genderImg}" class="country-sex" width="13" height="13">',
                '</div>',
            '</div>',
            '<div class="user-right">',
                '<p class="intr-name clearfix">',
                    '<span class="user-name"><a href="#{linkUrl}" target="_blank">#{userName}</a></span>',
                    '<span class="user-job">#{job}&nbsp;</span>',
                    '<span class="user-comment">#{goodCommentNum}</span>',
                '</p>',
                '<p class="intr-skills">#{skillTags}</p>',
            '</div>',
        '</div>',
        '<div class="user-bottom">',
            '<p title = "#{personalInfo}">#{personalInfo}</p>',
        '</div>',
        '<i class="icon-video icon-info-triangle"></i>'
    ].join('');


    /* 加载个人信息 的代码; */
    var getUserInfo = function () {
        if($('.info-content .info-top .user-info').length)
            return;
        ajaxApi && ajaxApi.abort();
        ajaxApi = util.setAjax(util.strFormat(inter.getApiUrl().expertInfoUrl, [you.id]), {}, function (data) {
            if (data && data.status && data.status == 200) {
                var getSkillTags = function (arr) {
                        var str = '';
                        for (var i in arr) {
                            str += '<a href="/expertsearch?ft=' + encodeURI(arr[i]) + '" target="_blank">' + arr[i] + '</a>';
                        }
                        return str;
                    },
                    renderHtml = util.template(sExpertInfo, {
                        gender: data.sex == 'WOMAN' ? '女' : '男',
                        genderImg: data.sex == 'WOMAN' ? ued_conf.root + 'images/female.png' : ued_conf.root + 'images/male.png',
                        linkUrl: data.linkUrl,
                        userName: data.userName || '未命名用户',
                        skillTags: getSkillTags(data.skillTagsArray) || '服务标签暂未设置',
                        country: data.country,
                        countryImgUrl: data.countryImgUrl,
                        headImgUrl: data.headImgUrl,
                        job: data.job || '职业未设置',
                        personalInfo: util.autoAddEllipsis(data.personalInfo, 750, null) || '这个家伙很懒，什么都没有留下。',
                        goodCommentNum: data.goodCommentNum ? data.goodCommentNum + '个人觉得Ta不错' : ''
                    });
                $('.info-content').append(renderHtml);
            } else {
                $('.info-content').append('读取对方信息失败。');
            }
        }, null, 'GET');
    };


    var _bindEvent = function () {
        var video = $('#remoteVideo'),
            $voiceControl = $('.voice-control'),
            $volume = $('.volume'),
            $volumeBar = $('.volume-bar'),
            $volumeDot = $('.volume-dot'),
            $iconVolumeWarp = $('.icon-volume-warp'),
            $iconVolume = $('.icon-volume'),
            $loading = $('.loading'),
            $current = $('.current'),
            $costContent = $('.cost-content'),
            $iconInfo = $('.icon-info'),
            $infoContent = $('.info-content'),
            sIconVolumeShut = 'icon-volume-shut',
            sVoiceControlHover = 'voice-control-hover',
            sIconVolumeHover = 'icon-volume-hover',
            sNone = 'none',
            sClick = 'click';
        video.removeAttr("controls");
        $loading.fadeIn(500);

        //视频开始前
        video.on('loadedmetadata', function () {
            $('#localVideo')[0].volume = 0;

            //设置视频属性
            $current.text(timeFormat(0));
            $('.duration').text(timeFormat(video[0].duration));
            updateVolume(0, 100);
            $iconVolume.removeClass(sIconVolumeShut);
            $volumeBar.css('height', '100%');
            $volumeDot.css('bottom', '124px');
        });
        //缓冲
        var startBuffer = function () {
            var currentBuffer = video[0].buffered.end(0);
            var maxduration = video[0].duration;
            var perc = 100 * currentBuffer / maxduration;
            $('.bufferBar').css('width', perc + '%');
            if (currentBuffer < maxduration) {
                setTimeout(startBuffer, 500);
            }
        };

        //音量控件
        $iconVolume.on(sClick, function () {
            $(this).toggleClass(sIconVolumeShut);
            if (video[0].volume != 0) {
                video[0].volume = 0;
                $volumeBar.css('height', 0);
                $volumeDot.css('top', '30px');
            }
            else {
                video[0].volume = 1;
                $volumeBar.css('height', '100%');
                $volumeDot.css('top', '129px');
            }
        });
        var volumeDrag = false;
        $('.volume,.volume-dot').on('mousedown', function (e) {
            volumeDrag = true;
            video[0].muted = false;
            $iconVolume.removeClass('muted');
            updateVolume(e.pageY);
        });
        $(document).on('mouseup', function (e) {
            if (volumeDrag) {
                volumeDrag = false;
                updateVolume(e.pageY);
            }
        });
        $(document).on('mousemove', function (e) {
            if (volumeDrag) {
                updateVolume(e.pageY);
            }
        });
        /* 控制音量 */
        var updateVolume = function (y, vol) {
            var percentage;
            if (vol) {
                percentage = vol * 100;
            }
            else {
                var position = y - $volume.offset().top;
                percentage = 100 * position / $volume.height();
            }
            if (percentage > 100) {
                percentage = 100;
            }
            if (percentage < 0) {
                percentage = 0;
            }
            //更新声音和声音条
            $volumeBar.css('height', (percentage) + '%');
            $volumeDot.css('top', (30 + percentage) + 'px');
            video[0].volume = percentage / 100;

            //根据声音控制图标
            if (video[0].volume == 0) {
                $iconVolume.addClass(sIconVolumeShut);
            }
            else {
                $iconVolume.removeClass(sIconVolumeShut);
            }
        };

        $voiceControl.hover(
            function () {
                $volume.show();
                $volumeDot.show();
                $voiceControl.addClass(sVoiceControlHover);
                $iconVolumeWarp.addClass(sIconVolumeHover);
            },
            function () {
                $volume.hide();
                $volumeDot.hide();
                $voiceControl.removeClass(sVoiceControlHover);
                $iconVolumeWarp.removeClass(sIconVolumeHover);
            }
        );


        //格式化时间
        var timeFormat = function (seconds) {
            var m = Math.floor(seconds / 60) < 10 ? "0" + Math.floor(seconds / 60) : Math.floor(seconds / 60);
            var s = Math.floor(seconds - (m * 60)) < 10 ? "0" + Math.floor(seconds - (m * 60)) : Math.floor(seconds - (m * 60));
            return m + ":" + s;
        };


        //专家信息
        $iconInfo.on(sClick, function (event) {
            $costContent.addClass(sNone);
            $infoContent.toggleClass(sNone);
            getUserInfo();
            $('.video-tips').remove();
            event.stopPropagation();
        });

        $(document).on(sClick, function (event) {
            var $this = $(event.target);
            if ($this.closest('.video-tips').length == 0 && $this.closest('.info-content').length == 0 && $this.closest('.control-span').length == 0) {
                chatUtil.hideInfo();
            }
            event.stopPropagation();
        });


        //挂断视频
        $('.icon-break').on('click', function () {
            chatUtil.hangUp();
            $('#vedioBox').remove();
        });

    };

    var onBeforeUnload = function(){
        chatUtil.closePage(0);
    }

    return {
        init: function(){
            _bindEvent();
            chatUtil.setVideoStatus('status-wait-confirm-video');

            if ($.browser.msie) {
                window.document.body.onbeforeunload = onBeforeUnload;
            }else{
                window.onBeforeUnload = onBeforeUnload;
                $('body').attr('onbeforeunload', 'return onBeforeUnload();')
            };
        }
    }
});