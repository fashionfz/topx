/**
 * @description: 登录注册模块
 * @author: Young Foo(young.foo@helome.com)
 * @update: Young Foo(young.foo@helome.com)
 */
define('module/login', [
    'common/interface',
    'common/util',
    'module/checkForm',
    'module/strength'
], function(inter, util, check, strength){

    return {

        /**
         * 获取登录状态
         */
        initLoginStatus : function(){
            var self = this;
            $.getJSON(inter.getApiUrl().getLoginUrl, function(json){
                if(json.status===1){
                    self.loginStatus = true;
                }
            });
        },

        /**
         * 初始化忘记密码窗口
         */
        initForgetPasswordBind : function(){
            var self = this,
                oEmail = $("#fEmail");

            self._changeCode();
            check.emailTips('#fEmail');

            $('.replace-code').on('click', function(e){
                self._changeCode();
                e.preventDefault();
            });

            //提交忘记密码
            $('.btn-forget').on('click', function(){
                self.findPwd();
            })

            oEmail.focus();
        },
        
        /**
         * 初始化重置密码
         */
        initResetPwdBind : function(){
            var self = this,
                $oPwd = $("#sPwd"),
                $oConfirmPwd = $('#sConfirmPwd');

            strength.init('#sPwd', {min: 6, max: 18});
            
            $oConfirmPwd.on('keydown', function(e){
                if(e.keyCode === 13){
                    self.resetPwd();
                }
            });

            $('.btn-resetPwd').on('click', function(){
                self.resetPwd();
            });

            $oPwd.focus();
        },
        /**
         * 登录操作
         * param: { block:登录的模块, loginName: 登录名, loginPwd: 登录密码, loginUrl: 登录链接, loginBtnText: 登录按钮名称 }
         */
        login : function(obj){
            var self = this;
            var loginBlock = obj.block,
                username = $.trim(obj.loginName.val()),
                password = obj.loginPwd.val(),
                loginText = obj.loginBtnText,
                userkey = obj.loginKey || false,
                remember = loginBlock.find('input[name="remember"]:checked').val()||'0',
                tips = loginBlock.find('.error-msg'),
                loginBtn = loginBlock.find(".btn-login"),
                errMsg = '';

            //用户名
            if(username.length < 1){
                errMsg = '请输入用户名';
            }else if(!check.email(username).match){
                errMsg = '邮箱格式错误';
            }

            //密码
            if(password.length < 1){
                if(errMsg && errMsg.indexOf('邮箱') == -1){
                    errMsg = '请输入用户名和密码';
                }else if(!errMsg){
                    errMsg = '请输入密码';
                }
            }

            //错误信息显示
            if(errMsg){
                tips.html('<em></em>' + errMsg).show();
            }else{
                tips.empty().hide();
            }

            //错误显示
            if( !$.trim(tips.text()).length ){
                self._toggleBtn(loginBtn, '登录中', tips, '登录请求中...');
                if (userkey) {
                    util.setAjax(obj.loginUrl, {email: username, password: password, key: userkey}, function(json){
                        if(json.status){
                            location.href = util.location().referer || '/';
                        }else{
                            self._toggleBtn(loginBtn, loginText, tips, json.error);
                        }
                    },function(){
                        self._toggleBtn(loginBtn, loginText, tips, '服务器繁忙，请稍后再试。');
                    });
                }else{
                    util.setAjax(obj.loginUrl, {email: username, password: password, remember: remember}, function(json){
                        if(json.status){
                            location.href = util.location().referer || '/';
                        }else{
                            self._toggleBtn(loginBtn, loginText, tips, json.error);
                            if(json.errorCode && json.errorCode == 200002){
                                location.href = '/loginskip';
                            }
                        }
                    },function(){
                        self._toggleBtn(loginBtn, loginText, tips, '服务器繁忙，请稍后再试。');
                    });
                }

                return false;
            }
        },

        /**
         * 注册操作
         * param: { block:登录的模块, loginName: 登录名, loginPwd: 登录密码, loginUrl: 登录链接, loginBtnText: 登录按钮名称 }
         */
        register : function(obj){
            var self = this,
                $regBlock = obj.block,
                $oEmail = obj.regName,
                $oPwd = obj.regPwd,
                $oConfirmPwd = obj.regConfirmPwd,
                $oCode = obj.regCode,
                $tips = $regBlock.find('.error-msg'),
                email = $oEmail.val(),
                password = $oPwd.val(),
                code = $oCode.val(),
                regKey = obj.regKey || false,
                errMsg = '',
                agreement = $('#regAgreement').prop("checked");
            
            var emailPass = self._checkEmail($oEmail, email, function(){

                if (!self._checkPwd($oPwd, $oConfirmPwd)) {
                    return;
                }
                
                if (!self._checkConfirmPwd($oPwd, $oConfirmPwd)) {
                    return;
                }
                
                self._checkCode($oCode, function(){

                    //验证agree
                    if(!agreement){
                        errMsg = '请阅读并接受《helome用户注册协议》';
                        $tips.html('<em></em>' + errMsg).show();

                    }else if($.trim($tips.text()).length<=0){
                        var loading = $.tips('loading');

                        self._toggleBtn($('.btn-reg'), '注册中', $tips, '注册请求中...');

                        if (regKey) {
                            util.setAjax(obj.regUrl, {
                                email : email,
                                password : password,
                                captcha : code,
                                t : self.timestamp,
                                key: regKey
                            },function(json){
                                loading.close();
                                if(json.status){
                                    location.href = '/regsuccess';
                                    self.timestamp = null;
                                }else{
                                    self._changeCode();
                                    $tips.html('<em></em>' + json.error).show();
                                }
                            },function(){
                                loading.close();
                                self._changeCode();
                                $tips.html('<em></em>服务器繁忙，请稍后再试。').show();
                            });
                        }else{
                            util.setAjax(obj.regUrl, {
                                email : email,
                                password : password,
                                captcha : code,
                                t : self.timestamp
                            },function(json){
                                loading.close();
                                if(json.status){
                                    location.href = '/regsuccess';
                                    self.timestamp = null;
                                }else{
                                    self._changeCode();
                                    $tips.html('<em></em>' + json.error).show();
                                }
                            },function(){
                                loading.close();
                                self._changeCode();
                                $tips.html('<em></em>服务器繁忙，请稍后再试。').show();
                            });

                        };
                    }
                });
            

            });

            $tips.show();

        },

        /**
         * 找回密码操作
         */
        findPwd : function(){
            var self = this,
                $forgetpwdBlock = $('.forget-password-main'),
                $oEmail = $("#fEmail"),
                $oCode = $('#fCode'),
                tips = $forgetpwdBlock.find(".error-msg");

            self._checkEmailExist($oEmail, function(){
                $forgetpwdBlock.find('input[type="text"],input[type="password"]').blur();

                self._checkCode($oCode, function(){
                    if($.trim(tips.text()).length<=0){
                        var email = $oEmail.val(),
                            code = $oCode.val();

                        self._toggleBtn($('.btn-forget'), '发送中', tips, '邮件正在发送中...');

                        self.getPwdEmail({email: email, captcha : code});
                    }
                });
            });

            tips.show();
        },

        /**
         * 找回密码发送邮箱
         */
        getPwdEmail : function(opts){
            var self = this;
            var tip = $.tips('loading');
            util.setAjax(inter.getApiUrl().forgetPwdUrl, {
                reSend : opts.reSend || 0,
                email : opts.email,
                captcha : opts.captcha,
                t : self.timestamp
            },function(json){
                tip.close();
                if(json.success){
                    location.href = '/sendsuccess?'+ $.param(opts);
                    self.timestamp = null;
                }else{
                    self._changeCode();
                    $.alert(json.error);
                }
            },function(){
                tip.close();
                self._changeCode();
                $.alert('服务器繁忙，请稍后再试。');
            });
        },
        /**
         * 重置密码操作
         */
        resetPwd : function(){
            var self = this,
                $resetBlock = $('.reset-password-main'),
                $oPwd = $("#sPwd"),
                $oConfirmPwd = $('#sConfirmPwd'),
                tips = $resetBlock.find('.error-msg');

            $resetBlock.find('input[type="text"],input[type="password"]').blur();

            if (!self._checkPwd($oPwd, $oConfirmPwd)) {
                tips.show();
                return;
            };
            
            if (!self._checkConfirmPwd($oPwd, $oConfirmPwd)) {
                tips.show();
                return;
            };

            if($.trim(tips.text()).length<=0){
                var pwd = $oPwd.val(),
                    email = $('#email').val(),
                    code = $('#code').val();

                self._toggleBtn($('.btn-resetPwd'), '修改密码', tips, '正在请求修改密码中...');

                util.setAjax(inter.getApiUrl().editPwdUrl, {
                    email : email,
                    code : code,
                    password : pwd
                },function(json){
                    if(json.error){
                        $.alert(json.error);
                    }else{
                        location.href = '/resetsuccess';
                    }
                },function(){
                    $.alert('服务器繁忙，请稍后再试。');
                });
            }

        },

        _toggleBtn : function(obj, txt, tips, msg){
            if(obj.prop('disabled')){
                tips.removeClass('postLoading').html('<em></em>' + msg).show();
                if(obj.attr('data-old') === 'btn-green'){
                    obj.addClass('btn-green');
                }
                obj.removeClass('btn-disabled').removeProp('disabled').prop({'value': txt}).removeAttr('data-old');
            }else{
                tips.html(msg).addClass('postLoading').show();
                if(obj.prop('class').indexOf('btn-green')>-1){
                    obj.removeClass('btn-green').attr('data-old', 'btn-green');
                }
                obj.addClass('btn-disabled').prop({'disabled': true, 'value': txt});
            }
        },

        _checkEmailExist: function($this, call){
            var email = $this.val(),
                tips = $this.closest("form").find(".error-msg");

            if(email.length<1){
                check.changeClass(false, $this, tips, '请输入邮箱');
            }else{
                if(check.email(email).match){
                    tips.html('验证中...');
                    util.setAjax(inter.getApiUrl().checkEmailUrl, {email: email} ,function(json){
                        if(json.status){
                            check.changeClass(false, $this, tips, '邮箱不存在');
                        }else{
                            check.changeClass(true, $this, tips, '');
                            call && call();
                        }
                    },function(){
                        check.changeClass(false, $this, tips, '验证失败');
                    });
                }else{
                    check.changeClass(false, $this, tips, '无效的邮箱');
                }
            }
        },

        /**
         * 刷新验证码
         */
        _changeCode : function(){
            this.timestamp = new Date().getTime() + "" + parseInt(Math.random()*1000);
            var rand = Math.random(),
                codeUrl = [
                    inter.getApiUrl().captchaUrl,
                    '?',
                    rand,
                    '&t=',
                    this.timestamp
                ].join('');
            $('.code').attr('src',codeUrl);
        },

        /**
         * 验证码校验
         */
        _checkCode : function($this, call){
            var self = this,
                code = $this.val(),
                tips = $this.closest("form").find(".error-msg");

            if(code.length<1){
                check.changeClass(false, $this, tips, '请输入验证码');
            }else{
                util.setAjax(inter.getApiUrl().captchaUrl, {captcha: code, t: self.timestamp} ,function(json){
                    if(json.status){
                        check.changeClass(true, $this, tips, '');
                        call && call();
                    }else{
                        check.changeClass(false, $this, tips, '验证码错误');
                    }
                },function(){
                    check.changeClass(false, $this, tips, '验证失败');
                    self._changeCode();
                });
            };
        },

        /**
         * 注册邮箱校验
         */
        _checkEmail : function($this, oldEmail, call){
            var email = $this.val(),
                tips = $this.closest("form").find(".error-msg"),
                tipsText = tips.text(),
                existTips = "存在";

            if(email.length<1){
                check.changeClass(false, $this, tips, '请输入邮箱');
            }else{
                if(check.email(email).match){
                    if(oldEmail != email && tipsText.indexOf(existTips)<=-1){
                        tips.html('验证中...');
                    }

                    util.setAjax(inter.getApiUrl().checkEmailUrl, {email: email} ,function(json){
                        if(json.status){
                            check.changeClass(true, $this, tips, '');
                            oldEmail = email;
                            call && call();
                        }else{
                            if(oldEmail == email){
                                check.changeClass(false, $this, tips, json.error + '，<a href="/forgetpwd" class="forgetHref">找回密码</a>');
                            }else{
                                oldEmail = email;
                            }
                        }

                    },function(){
                        check.changeClass(false, $this, tips, '验证失败');
                    });
                }else{
                    check.changeClass(false, $this, tips, '无效的邮箱');
                }
            }
        },

        /**
         * 注册密码校验
         */

        _checkPwd : function($this, $confirmPwd){
            var pwd = $this.val(),
                tips = $this.closest("form").find(".error-msg"),
                confirmPwd = $confirmPwd.val();
            if(pwd.length<1){
                check.changeClass(false, $this, tips, '请输入密码');
                return false;
            }else{
                if(check.rangeLength(pwd,'6,18').match){
                    check.changeClass(true, $this, tips, '');
                    if(confirmPwd.length>0){
                        if(check.equalTo(pwd, confirmPwd).match){
                            check.changeClass(true, $confirmPwd, tips, '');
                            return true;
                        }else{
                            check.changeClass(false, $confirmPwd, tips, '两次输入的密码不一致');
                            return false;
                        }
                    }
                    return true;
                }else{
                    check.changeClass(false, $this, tips, util.template('密码长度错误，正确密码长度#{min}-#{max}位',{
                        min : 6,
                        max : 18
                    }));
                    return false;
                }
            }
        },
        /**
         * 注册确认密码校验
         */
        _checkConfirmPwd : function($pwd, $confirmPwd){
            var pwd = $confirmPwd.val(),
                tips = $confirmPwd.closest("form").find(".error-msg");

            if(pwd.length<1){
                check.changeClass(false, $confirmPwd, tips, '请输入确认密码');
                return false;
            }else{
                if(check.rangeLength(pwd,'6,18').match){
                    if(check.equalTo(pwd, $pwd.val()).match){
                        check.changeClass(true, $confirmPwd, tips, '');
                        return true;
                    }else{
                        check.changeClass(false, $confirmPwd, tips, '两次输入的密码不一致');
                        return false;
                    }
                }else{
                    check.changeClass(false, $confirmPwd, tips, '密码长度6-18位');
                    return false;
                }
            }
        }
    }
});