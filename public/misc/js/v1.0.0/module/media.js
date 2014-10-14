/**
 * @description: 多媒体操作模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/media', ['common/util'], function(util){
    var playState = 0;

    return {
        mediaList : {},
        /**
         * 初始化模块
         */
        init : function(opts){
            var self = this,
                defaultOpts = {
                    ele: null,
                    url: '',
                    _onPlay: function(){},
                    _onPause: function(){},
                    _onStop: function(){}
                };
            self.config = $.extend({}, defaultOpts, opts);
            self.supHtml5 = self.checkMedia();
            self.playing = false;
            self.pauseing = false;
            if(self.mediaIsExist()){
                return self.mediaIsExist();
            }else{
                self.renderMedia();
            }
            return self;
        },
        /**
         * 验证是否已经多媒体化
         */
        mediaIsExist : function(){
            var self = this,
                subItem = self.config.ele.find(self.supHtml5 ? 'audio' : 'object'),
                subMediaId = subItem.length ? subItem.prop('id') : 0;

            if(subMediaId){
                return self.mediaList[subMediaId];
            }else{
                return false;
            }
        },
        /**
         * 渲染多媒体控件
         */
        renderMedia : function(){
            var self = this,
                mediaId = 'media' + (+ new Date),
                audioTpl = '<audio id="#{id}" src="#{url}" class="media" controls="false"></audio>',
                objectTpl = [
                    '<object id="#{id}" class="media" data="#{url}" type="application/x-mplayer2" width="1" height="1">',
                        '<param name="src" value="#{url}" />',
                        '<param name="autostart" value="0" />',
                        '<param name="playcount" value="infinite" />',
                    '</object>'
                ].join(''),
                mediaObj = $('#'+mediaId);

            if(!mediaObj.length){
                self.mediaId = mediaId;
                mediaObj = $(util.template(self.supHtml5 ? audioTpl : objectTpl, {id: mediaId, url: self.config.url}));
                self.config.ele.append(mediaObj);
                self.media = mediaObj[0];
                self.bindEvent(mediaId);
                self.mediaList[mediaId] = $.extend({}, {}, self);
            }
        },
        /**
         * 判断是否支持html5
         */
        checkMedia : function () {
            try{
                return !!$('<audio></audio>')[0].canPlayType('audio/mp3');
            }catch (e){
                return !!0;
            }
        },
        /**
         * 播放
         */
        play : function(){
            var self = this;
            $.each(self.mediaList, function(i, n){
                if(n.mediaId != self.mediaId){
                    n.stop();
                }
            });
            self.playing = true;
            self.pauseing = false;
            self.media.play();
            self.config._onPlay && self.config._onPlay();
            return self;
        },
        /**
         * 暂停
         */
        pause : function(){
            var self = this;
            self.playing = false;
            self.pauseing = true;
            self.media.pause();
            self.config._onPause && self.config._onPause();
            return self;
        },
        /**
         * 停止
         */
        stop : function(){
            var self = this;
            if(self.playing || self.pauseing){
                self.playing = false;
                self.pauseing = false;
                if(self.supHtml5){
                    self.media.pause();
                    self.media.currentTime = 0.0;
                }else{
                    self.media.stop();
                }
            }
            self.config._onStop && self.config._onStop(self.config.ele);
            return self;
        },
        bindEvent : function(mid){
            var self = this,
                config = self.mediaList[mid] ? self.mediaList[mid].config : self.config,
                oMedia = $(self.media);
            if(self.supHtml5){
                oMedia.on('play', function(){
                    self.playing = true;
                    self.pauseing = false;
                    self.config._onPlay && self.config._onPlay();
                });
                oMedia.on('pause', function(){
                    if(self.media.currentTime && !self.media.ended){
                        self.playing = false;
                        self.pauseing = true;
                        self.config._onPause && self.config._onPause();
                    }else{
                        self.playing = false;
                        self.pauseing = false;
                        config._onStop && config._onStop(config.ele);
                    }
                });
                oMedia.on('ended', function(){
                    if(self.playing || self.pauseing){
                        self.playing = false;
                        self.pauseing = false;
                        self.config._onStop && self.config._onStop(config.ele);
                    }
                });
            }else{
                self.timer = setInterval(function(){
                    switch (self.media.PlayState){
                        case 0:
                            if(playState){
                                self.playing = false;
                                self.pauseing = false;
                                self.config._onStop && self.config._onStop(config.ele);
                            }
                            break;
                        case 1:
                            if(playState != 1){
                                self.playing = false;
                                self.pauseing = true;
                                self.config._onPause && self.config._onPause();
                            }
                            break;
                        case 2:
                            if(playState != 2){
                                self.playing = true;
                                self.pauseing = false;
                                self.config._onPlay && self.config._onPlay();
                            }
                            break;
                        default :
                            break;
                    }
                    playState = self.media.PlayState;
                }, 1000);
            }
        }
    }
});