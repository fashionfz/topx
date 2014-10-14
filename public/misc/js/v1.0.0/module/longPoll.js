/**
 * @description: 长连接模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/longPoll', [
    'common/interface',
    'common/util',
    'module/cookie'
], function(inter, util, cookie){
    var times = 1,
        err = false,
        ExistPoll = false,
        selfPoll = false,
        isCloseMessage = false,
        isOpenConsult = false,
        timer = null,
        rTime = new Date().getTime(),
        pollMessageCookie = {},
        pollConsultCookie = {},
        pollArrWindow = {},
        consultApi = null,
        countDownNum = 60,
        cookieMessage = 'pollMessage',
        cookieConsult = 'pollConsult',
        cookieWindow = 'pollWindow',
        existCName = 'existPoll',
        cookieCountDown = 'countDown',
        timeExpires = 90000,
        oldTitle = $('title').text(),
        winStatus = true,
        statusTimer = null,
        existTitle = false,
        isCountDown = false;

    return {
        /**
         * 初始化长连接
         */
        init : function(){
            var self = this;

            pollMessageCookie = self._getPollCookie(cookieMessage) || {};
            pollArrWindow = self._getPollCookie(cookieWindow) || {};
            var arrWindow = pollArrWindow.arrWindow || [],
                existPoll = parseInt(cookie.get(existCName)) || 0;

            //console.log('init:'+cookie.get(existCName))
            if(!existPoll){
                cookie.set(existCName, existPoll, new Date(rTime + timeExpires));
            }

            arrWindow.push(rTime);
            pollArrWindow.arrWindow = arrWindow;
            cookie.set(cookieWindow, JSON.stringify(pollArrWindow));

            if(existPoll && existPoll === 1){
                self.setTimer();
            }else{
                self.render();
            }
            self.closeTab();

            return self;
        },
        /**
         * 新消息数据绑定
         */
        render : function(){
            var self = this;
            //console.log('update:'+new Date(rTime + timeExpires))
            if(!selfPoll){
                ExistPoll = true;
                selfPoll = true;
                err = false;
                self._poll(function(json){
                    if(json.status === "200"){
                        switch (json.msgtype){
                            case 'message': // 有即时消息
                                if(consultApi){
                                    consultApi.close();
                                }
                                pollMessageCookie.reserveMsgNum = json.reserveMsgNum;
                                pollMessageCookie.systemMsgNum = json.systemMsgNum;
                                pollMessageCookie.commentMsgNum = json.commentMsgNum;
                                pollMessageCookie.moneyMsgNum = json.moneyMsgNum;
                                pollMessageCookie.replyMsgNum = json.replyMsgNum;
                                //self.renderHtml(json);
                                self.setTimer();
                                isCloseMessage = false;
                                cookie.set(cookieMessage, JSON.stringify(pollMessageCookie));
                                break;
                            case 'consult': // 有即时咨询 弹窗
                                if(json.clear){
                                    self._consultClose('clear');
                                }else{
                                    self._tip();
                                    self._title('新咨询');
                                    pollConsultCookie.userId = json.userId;
                                    pollConsultCookie.userName = json.userName;
                                    pollConsultCookie.consultId = json.consultId;
                                    isOpenConsult = true;
                                    cookie.set(cookieCountDown, countDownNum, new Date(new Date().getTime() + 60000));
                                    self._consultConfirm(json);
                                    cookie.set(cookieConsult, JSON.stringify(pollConsultCookie));
                                }
                                break;
                            case 'kickoff':
                                self._kickLogin(json);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        },
        /**
         * 新消息提示框
         */
        renderHtml : function(json){
            var self = this,
                tpl = [
                    '<ul class="header-msg nav clearfix">',
                        '<a href="javascript:" class="header-tips-delete tips-close"></a>',
                        '#{reserveMsg}',
                        '#{systemMsg}',
                        '#{commentMsg}',
                        '#{replyMsg}',
                        '#{moneyMsg}',
                        '<li class="ignore"><a href="javascript:;" class="tips-delete">忽略所有</a></li>',
                    '</ul>'
                ].join(''),
                newTpl = [
                    '<div class="home-num" title="#{msgTitle}">', // 头像上加样式：home-num-trade
                        '<div class="left"></div>',
                        '<div class="cneter">',
                            '<span class="home-num-txt">#{msgNum}</span>',
                        '</div>',
                        '<div class="right"></div>',
                    '</div>'
                ].join(''),
                data = {
                    reserveMsg : '',
                    systemMsg : '',
                    commentMsg : '',
                    moneyMsg : '',
                    replyMsg : ''
                },
                msg = $('.header-msg'),
                newMsg = $('.J-login-mark'),
                totalNum = 0,
                msgTitle = '',
                totalNumObj = $('.home-num-trade');

            if(json){
                totalNum = json.reserveMsgNum + json.systemMsgNum + json.commentMsgNum + json.moneyMsgNum + json.replyMsgNum;
                if( totalNum ){
                    if(totalNum > 99){
                        totalNum = '99+';
                        msgTitle = '您有超过99条新消息';
                    }else{
                        msgTitle = '您有条'+ totalNum +'新消息';
                    }
                    if(totalNumObj.length){
                        totalNumObj.find('.home-num-txt').text(totalNum);
                    }else{
                        totalNumObj = $(util.template(newTpl, {
                            msgTitle: msgTitle,
                            msgNum: totalNum
                        }));
                        newMsg.find('#user-center').prepend(totalNumObj);
                        totalNumObj.addClass('home-num-trade');
                    }
                }else{
                    if(totalNumObj.length){
                        totalNumObj.remove();
                    }
                }
            }
            if(!json.reserveMsgNum && !json.systemMsgNum && !json.commentMsgNum && !json.moneyMsgNum && !json.replyMsgNum){
                msg.remove();
                self._cleanTitle();
            }else{
                self._title();
                if(json.systemMsgNum || json.commentMsgNum || json.replyMsgNum){
                    var sysMsgNum = json.systemMsgNum + json.commentMsgNum + json.replyMsgNum;
                    if(sysMsgNum > 99){
                        sysMsgNum = '99+';
                        msgTitle = '您有超过99条新系统消息';
                    }else{
                        msgTitle = '您有条'+ sysMsgNum +'新系统消息';
                    }
                    if(newMsg.find('.home-panel li:eq(2) .home-num').length){
                        newMsg.find('.home-panel li:eq(2) .home-num-txt').text(sysMsgNum);
                    }else{
                        newMsg.find('.home-panel li:eq(2)').prepend(util.template(newTpl, {
                            msgTitle: msgTitle,
                            msgNum: sysMsgNum
                        }))
                    }
                }else{
                    if(newMsg.find('.home-panel li:eq(2) .home-num').length){
                        newMsg.find('.home-panel li:eq(2) .home-num').remove();
                    }
                }
                if(json.moneyMsgNum){
                    if(json.moneyMsgNum > 99){
                        json.moneyMsgNum = '99+';
                        msgTitle = '您有超过99条新转账消息';
                    }else{
                        msgTitle = '您有条'+ json.moneyMsgNum +'新转账消息';
                    }
                    if(newMsg.find('.home-panel li:eq(3) .home-num').length){
                        newMsg.find('.home-panel li:eq(3) .home-num-txt').text(json.moneyMsgNum);
                    }else{
                        newMsg.find('.home-panel li:eq(3)').prepend(util.template(newTpl, {
                            msgTitle: msgTitle,
                            msgNum: json.moneyMsgNum
                        }))
                    }
                }else{
                    if(newMsg.find('.home-panel li:eq(3) .home-num').length){
                        newMsg.find('.home-panel li:eq(3) .home-num').remove();
                    }
                }
            }

            /*if(msg.length>0){
                if(!json.reserveMsgNum && !json.systemMsgNum && !json.commentMsgNum && !json.moneyMsgNum && !json.replyMsgNum){
                    msg.remove();
                    self._cleanTitle();
                }else{
                    self._title();
                    if(json.reserveMsgNum){
                        if(msg.find('.msg-reserve').length>0){
                            msg.find('.msg-reserve strong').text(json.reserveMsgNum);
                        }else{
                            var li = '<li class="msg-reserve"><a href="' + inter.getApiUrl().userMegUrl + '"><strong>'+ json.reserveMsgNum +'</strong>预约消息</a></li>';
                            msg.find('.ignore').before(li);
                        }
                    }else{
                        msg.find('.msg-reserve').remove();
                    }
                    if(json.systemMsgNum){
                        if(msg.find('.msg-system').length>0){
                            msg.find('.msg-system strong').text(json.systemMsgNum);
                        }else{
                            var li = '<li class="msg-system"><a href="' + inter.getApiUrl().userMegUrl + '?type=1"><strong>'+ json.systemMsgNum +'</strong>系统消息</a></li>';
                            msg.find('.ignore').before(li);
                        }
                    }else{
                        msg.find('.msg-system').remove();
                    }
                    if(json.commentMsgNum){
                        if(msg.find('.msg-comment').length>0){
                            msg.find('.msg-comment strong').text(json.commentMsgNum);
                        }else{
                            var li = '<li class="msg-comment"><a href="' + inter.getApiUrl().userMegUrl + '"><strong>'+ json.commentMsgNum +'</strong>评价消息</a></li>';
                            msg.find('.ignore').before(li);
                        }
                    }else{
                        msg.find('.msg-comment').remove();
                    }
                    if(json.replyMsgNum){
                        if(msg.find('.msg-reply').length>0){
                            msg.find('.msg-reply strong').text(json.replyMsgNum);
                        }else{
                            var li = '<li class="msg-reply"><a href="' + inter.getApiUrl().userMegUrl + '"><strong>'+ json.replyMsgNum +'</strong>回复消息</a></li>';
                            msg.find('.ignore').before(li);
                        }
                    }else{
                        msg.find('.msg-reply').remove();
                    }
                    if(json.moneyMsgNum){
                        if(msg.find('.msg-money').length>0){
                            msg.find('.msg-money strong').text(json.moneyMsgNum);
                        }else{
                            var li = '<li class="msg-money"><a href="' + inter.getApiUrl().userMegUrl + '"><strong>'+ json.moneyMsgNum +'</strong>转账消息</a></li>';
                            msg.find('.ignore').before(li);
                        }
                    }else{
                        msg.find('.msg-money').remove();
                    }
                }
            }else{
                if(!json.reserveMsgNum && !json.systemMsgNum && !json.commentMsgNum && !json.moneyMsgNum && !json.replyMsgNum && (!$('#consultMsgBox').length || $('#consultMsgBox').css('display') == 'none')){
                    self._cleanTitle();
                }else{
                    self._title();
                    if(json.reserveMsgNum){
                        data.reserveMsg = '<li class="msg-reserve"><a href="' + inter.getApiUrl().userMegUrl + '"><strong>'+ json.reserveMsgNum +'</strong>预约消息</a></li>'
                    }
                    if(json.systemMsgNum){
                        data.systemMsg = '<li class="msg-system"><a href="' + inter.getApiUrl().userMegUrl + '?type=1"><strong>'+ json.systemMsgNum +'</strong>系统消息</a></li>'
                    }
                    if(json.commentMsgNum){
                        data.commentMsg = '<li class="msg-comment"><a href="' + inter.getApiUrl().userMegUrl + '"><strong>'+ json.commentMsgNum +'</strong>评价消息</a></li>'
                    }
                    if(json.replyMsgNum){
                        data.replyMsg = '<li class="msg-reply"><a href="' + inter.getApiUrl().userMegUrl + '"><strong>'+ json.replyMsgNum +'</strong>回复消息</a></li>'
                    }
                    if(json.moneyMsgNum){
                        data.moneyMsg = '<li class="msg-money"><a href="' + inter.getApiUrl().userMegUrl + '"><strong>'+ json.moneyMsgNum +'</strong>转账消息</a></li>'
                    }

                    if(data.reserveMsg.length>0||data.systemMsg.length>0||data.commentMsg.length>0||data.replyMsg.length>0||data.moneyMsg.length>0){
                        $('.header-top').append(util.template(tpl, data));

                        self.close();
                        self.update(json.consumeOnly);
                    }
                }
            }*/
        },
        /**
         * 即时咨询消息提示窗口
         */
        _consultConfirm : function(json){
            var self = this,
                pollConsultCookie = self._getPollCookie(cookieConsult),
                pollConsultCountDown = parseInt(cookie.get(cookieCountDown))||0,
                openUrl = util.strFormat(inter.getApiUrl().consultExportUrl, [json.consultId]),
                tpl = [
                    '<div id="consultMsgBox">',
                        '<div class="cmb-tit clearfix">',
                            //'<a href="javascript:" class="cmb-close">×</a>',
                            '<span class="span-left">咨询请求</span>',
                        '</div>',
                        '<div class="cmb-table">',
                            '<table border="0" width="100%">',
                                '<tr>',
                                    //'<td><div class="icon-alert"></div></td>',
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
                $('.cmb-close').on('click', function(){ // 关闭提示窗口
                    self._rejectConsult(json.userId, function(){
                        self._consultClose('clear');
                        util.clearCountdown();
                        self._consultPoll();
                    });
                });
            }
            $('.cmb-start').on('click', function(){ // 打开视频咨询窗口
                //self._getExpertStatus(-1, function(){
                    window.open(openUrl, '_blank');
                    self._consultClose('clear');
                    util.clearCountdown();
                    self._consultPoll();
                //});
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

            if(!isCountDown){
                util.countdown('.knob', pollConsultCountDown, function(){
                    var btnRemember = $('<button class="btn-default btn-lg btn-green cmb-remember">我知道了</button>');
                    consultApi.find('.tips-text').html( '<span class="cmb-user">' + json.userName + '</span>的视频咨询请求已过期' );
                    consultApi.find('.cmb-content').append('<div class="tips-red">收到咨询请求请在60秒内接受或拒绝</div>');
                    consultApi.find('.cmb-button').html(btnRemember);
                    btnRemember.on('click', function(){ // 咨询超时
                        self._consultPoll();
                        self._consultClose('clear');
                    });
                    isCountDown = false;
                    countDownNum = 60;
                    cookie.set(cookieCountDown, 0);
                }, function(t){
                    var nowCD = (parseInt(cookie.get(cookieCountDown))|| 0) - 1,
                        newNum = t - 1,
                        newCookie = nowCD < newNum ? nowCD : newNum;

                    $('.knob').val(t).trigger("change");
                    if(selfPoll){
                        cookie.set(cookieCountDown, newCookie);
                    }
                    isCountDown = true;
                });
                countDownNum --;
            }
            /*pollConsultCookie.msgtype = 'message';
            cookie.set(cookieConsult, JSON.stringify(pollConsultCookie));*/
        },
        /**
         * 关闭咨询消息提示窗口并清除Cookie
         */
        _consultClose : function(act){
            var self = this;
            $('.knob').val(60).trigger("change");
            $('#consultMsgBox').hide();
            isOpenConsult = false;
            isCountDown = false;
            countDownNum = 60;
            util.clearCountdown();

            if(act && act == 'clear'){
                self._cleanTitle('新咨询');
                cookie.set(cookieConsult, '{}');
                cookie.del(cookieConsult);
                cookie.set(cookieCountDown, '0');
                cookie.del(cookieCountDown);
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
         * 查询用户状态
         */
        _getExpertStatus : function(uid, callback, errorCallback){
            var selfUserId = parseInt(cookie.get('_u_id')) || -1;
            $.ajax({
                type: "GET",
                url: util.strFormat(inter.getApiUrl().getVideoStatusUrl, [uid, selfUserId]) + '?callback=?',
                crossDomain: true,
                dataType: 'jsonp',
                success: function(data){
                    if(data){
                        if( data.isOnVideo ){
                            $.alert('对方正在咨询中，请稍后再试。');
                        }else if( data.selfIsOnVideo ){
                            $.alert('您已经在咨询中，无法开始新的咨询。');
                        }else{
                            if(callback)callback(data);
                        }
                    }else{
                        if(callback)callback(data);
                    }
                },
                error: function(){
                    if(errorCallback){
                        errorCallback();
                    }else{
                        $.alert('视频系统正在维护，请稍后再试。');
                    }
                }
            });
        },
        /**
         * 拒绝咨询
         */
        _rejectConsult : function(uid, callback){
            var currentUid = parseInt(cookie.get('_u_id')) || -1;
            $.getJSON(util.strFormat(inter.getApiUrl().rejectVideoUrl, [uid, currentUid]) + '?callback=?', function(json){
                if(callback)callback(json);
            });
        },
        /**
         * 获取longPoll的cookie
         */
        _getPollCookie : function(cn){
            var poll = cookie.get(cn);

            if(poll){
                poll = JSON.parse(poll);
            }else{
                poll = {};
            }
            return poll;
        },
        /**
         * 被踢出登录状态
         */
        _kickLogin : function(data){
            this._stopPoll();
            util.setAjax(inter.getApiUrl().clearCookieUrl, {});
            $.confirm('您的账号在别处登录，如非本人操作请及时登录并修改密码。', function(){
                location.href = '/login';
            }, function(){

            }, {okValue: '重新登录', cancelValue: '我知道了'})
        },
        /**
         * 定时轮询cookie
         */
        setTimer : function(){
            var self = this;
            timer = setInterval(function(){
                pollMessageCookie = self._getPollCookie(cookieMessage) || {};
                pollConsultCookie = self._getPollCookie(cookieConsult) || {};
                pollArrWindow = self._getPollCookie(cookieWindow) || {};

                var existPoll = parseInt(cookie.get(existCName)) || 0,
                    pollConsultCountDown = parseInt(cookie.get(cookieCountDown))||0;

                //console.log('time')

                if(existPoll && existPoll == 1){
                    if (!$.isEmptyObject(pollMessageCookie)){// 有即时消息
                        if(!isCloseMessage){
                            if(consultApi){
                                consultApi.close();
                            }
                            var reserveMsgNum = pollMessageCookie.reserveMsgNum,
                                systemMsgNum = pollMessageCookie.systemMsgNum,
                                commentMsgNum = pollMessageCookie.commentMsgNum,
                                replyMsgNum = pollMessageCookie.replyMsgNum,
                                moneyMsgNum = pollMessageCookie.moneyMsgNum;

                            self.renderHtml({
                                reserveMsgNum : parseInt(reserveMsgNum || 0),
                                systemMsgNum : parseInt(systemMsgNum || 0),
                                commentMsgNum : parseInt(commentMsgNum || 0),
                                replyMsgNum : parseInt(replyMsgNum || 0),
                                moneyMsgNum : parseInt(moneyMsgNum || 0)
                            });
                        }
                    }
                    if(!$.isEmptyObject(pollConsultCookie)){// 有即时咨询 弹窗
                        if( (!isOpenConsult) || (pollConsultCountDown > 0 && countDownNum === 60) ){
                            isCountDown = false;
                            self._consultConfirm(pollConsultCookie);
                            isOpenConsult = true;
                        }
                    }else{
                        self._consultClose('clear');
                    }
                }else{
                    var arrWin = pollArrWindow.arrWindow;
                    arrWin.sort();
                    if(arrWin.length>0){
                        if(arrWin[arrWin.length-1] == rTime){
                            self.render();
                        }
                    }else{
                        self.render();
                    }
                }
            }, 500);
        },
        /**
         * 关闭标签/浏览器 或 刷新浏览器的回调函数
         */
        onBeforeUnload : function(){
            var self = this,
                newArr = [],
                existPoll = parseInt(cookie.get(existCName)) || 0;

            //console.log('close')

            if(ExistPoll){
                existPoll = 0;
                ExistPoll = false;
            }
            for(var i=0; i<pollArrWindow.arrWindow.length; i++){
                if(pollArrWindow.arrWindow[i] != rTime){
                    newArr.push(pollArrWindow.arrWindow[i]);
                }
            }
            pollArrWindow.arrWindow = newArr;
            cookie.set(cookieWindow, JSON.stringify(pollArrWindow));
            cookie.set(existCName, existPoll, new Date(rTime + timeExpires));
        },
        /**
         * 关闭标签/浏览器 或 刷新浏览器
         */
        closeTab : function(){
            var self = this;
            window.onBeforeUnload = self.onBeforeUnload;
            if($.browser.msie){
                window.document.body.onbeforeunload = function(){
                    var newArr = [],
                        existPoll = parseInt(cookie.get(existCName)) || 0;

                    //console.log('close')

                    if(ExistPoll){
                        existPoll = 0;
                        ExistPoll = false;
                    }
                    for(var i=0; i<pollArrWindow.arrWindow.length; i++){
                        if(pollArrWindow.arrWindow[i] != rTime){
                            newArr.push(pollArrWindow.arrWindow[i]);
                        }
                    }
                    pollArrWindow.arrWindow = newArr;
                    cookie.set(cookieWindow, JSON.stringify(pollArrWindow));
                    cookie.set(existCName, existPoll, new Date(rTime + timeExpires));
                };
            }else{
                $('body').attr('onbeforeunload', 'return onBeforeUnload();')
            }
        },
        /**
         * 关闭消息提示框
         */
        close : function(){
            $('.header-msg').delegate('.tips-close', 'click', function(e){
                isCloseMessage = true;
                $('.header-msg').remove();
                e.preventDefault();
            });
        },
        /**
         * 更新消息提示框
         */
        update : function(consumeOnly){
            var self = this;
            $('.header-msg').delegate('.tips-delete', 'click', function(e){
                util.setAjax(inter.getApiUrl().updateMessageUrl, {consumeOnly: consumeOnly} , function(json){
                    if(json.status === '200'){
                        $('.header-msg').remove();
                    }else{
                        $.alert(json.message);
                    }
                }, function(){
                    $.alert('服务器繁忙，请稍后再试。');
                },"GET");
                e.preventDefault();
            });
        },
        /**
         * 长连接ajax请求
         */
        _poll : function(call){
            var self = this,
                url = inter.getApiUrl().getMessageCountUrl;

            if(!err){
                cookie.set(existCName, 1, new Date(new Date().getTime() + timeExpires));
                $.ajax({
                    url: url,
                    type: 'GET',
                    dataType: "json",
                    complete: function(){
                        times = 0;
                        self._poll(call);
                    },
                    timeout: timeExpires,
                    data: {times: times, r: new Date().getTime()},
                    success: function(json){
                        if(json.notLogin && json.notLogin === 'true'){
                            self._stopPoll();
                        }else{
                            if(call)call(json);
                        }
                    },
                    error: function(){
                        self._stopPoll();
                    }
                });
            }
        },
        _stopPoll : function(){
            err = true;
            clearInterval(timer);
            this._cleanTitle();
            cookie.set(cookieMessage, JSON.stringify(pollMessageCookie));
            cookie.set(cookieConsult, JSON.stringify(pollConsultCookie));
            cookie.set(cookieWindow, JSON.stringify(pollArrWindow));
            cookie.set(existCName, 0, new Date(rTime + timeExpires));
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

            self._setTitle(newTitle);
            winStatus = !winStatus;
            existTitle = true;
            clearTimeout(statusTimer);

            statusTimer = setTimeout(function(){
                self._title(tips);
            }, 1000);
        },
        /**
         * 设置title辅助方法
         */
        _setTitle : function(v){
            try{
                document.title = v;
            }catch (e){
                $('title').text(v);
            }
        },
        /**
         * 清除新消息title提醒
         */
        _cleanTitle : function(t){
            var self = this,
                nowTitle = $('title').text();

            if(nowTitle !== oldTitle){
                if(t){
                    if( nowTitle.indexOf(t) > -1 ){
                        self._setTitle(oldTitle);
                        winStatus = false;
                        existTitle = false;
                        clearTimeout(statusTimer);
                    }
                }else{
                    self._setTitle(oldTitle);
                    winStatus = false;
                    existTitle = false;
                    clearTimeout(statusTimer);
                }
            }
        },
        /**
         * 移除长连接相关cookie
         */
        remove : function(){
            cookie.set(existCName, '');
            cookie.set(cookieMessage, '{}');
            cookie.set(cookieConsult, '{}');
            cookie.set(cookieWindow, '{}');
            cookie.del(existCName);
            cookie.del(cookieMessage);
            cookie.del(cookieConsult);
            cookie.del(cookieWindow);
            return this;
        }
    }
});