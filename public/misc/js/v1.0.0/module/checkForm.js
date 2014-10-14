/**
 * @description: 登录注册验证模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/checkForm', [
    'common/interface',
    'common/util'
], function(inter, util){

    var errStr = {
            required: "必选字段",
            email: "请输入正确格式的电子邮件",
            url: "请输入合法的网址",
            date: "请输入合法的日期",
            digits: "只能输入整数",
            IDCard: "请输入合法的身份证号码",
            equalTo: "请再次输入相同的值",
            accept: "请输入拥有合法后缀名的字符串",
            maxLength: "请输入一个 长度最多是 {0} 的字符串",
            minLength: "请输入一个 长度最少是 {0} 的字符串",
            rangeLength: "请输入 一个长度介于 {0} 和 {1} 之间的字符串",
            range: "请输入一个介于 {0} 和 {1} 之间的值",
            max: "请输入一个最大为{0} 的值",
            min: "请输入一个最小为{0} 的值"
        },
        sendFrom = true;

    /**
     * 发送手机短信验证码
     * obj.resendTime 发送时间;  obj.data ajax请求数据; obj.alert 提示框; obj.button 按钮; obj.callback 回调
     */
    var GetPhoneCode = function(){
        
        var that = this;
        //计时器
        this.timer = 0;
        this.button;

        /**
         * 发送短信按钮倒计时效果
         * sec:倒计时时间， btn:倒计时按钮
         * 返回值: interval数值
         */
        this.btnTimer = function(sec, btn){
            var val = sec;
            var timer = setInterval(function(){
                if(val<1){
                    clearInterval(timer);
                    btn.css('color','#707070').attr('value','发送验证码').removeAttr('disabled');
                }else{
                    val = val - 1;
                    btn.css('color','#A7A7A7').attr({'value': val + '秒后可重试', 'disabled': 'disabled'});
                }
            }, 1000);

            this.timer = timer;
        };

        /*
         * 停止计时
         */
        this.stopBtnTimer = function(){
            clearInterval(this.timer);
        }

        this.resetBtn = function(){
            if (that.button) {
                that.stopBtnTimer();
                that.button.css('color','#707070').attr('value','发送验证码').removeAttr('disabled');
            };
        }

        this.startBtnTimer = function(obj){

            var d = obj,
                resendTime = d.resendTime || 60,
                codeData = d.data || {},
                codeUrl = d.url,
                codeAlert = d.alert,
                codeCB = d.callback,
                codeBtn = d.button;

            this.button = codeBtn;

            util.setAjax(codeUrl, codeData, function(json){
                codeAlert.close();
                if(json.status){

                    $.alert('发送成功，请尽快填写收到的短信验证码。');
                    if (codeCB) { codeCB(json) };
                    var timmer = that.btnTimer(resendTime, codeBtn);

                }else{
                    $.alert(json.error);
                }
            }, function(){
                codeAlert.close();
                $.alert('服务器繁忙，请稍后再试！');
            });
        }

    };

    /**
     * 根据参数设置相应表单显示效果
     */
    var changeClass = function(match, o, errBox, errMsg){
        var errTip = o.siblings('.error-tip'),
            rightTip = o.siblings('.right-tip');

        if(!match){
            o.addClass('tag-error');
            if(errTip.length<=0 && rightTip.length<=0){
                o.parent().append('<span class="error-tip"><em></em></span>');
            }else if(rightTip.length>0){
                rightTip.attr('class','error-tip');
            }
            if(errBox)errBox.html(errMsg);
            sendFrom = false;
        }else{
            if(errTip.length<=0 && rightTip.length<=0){
                o.parent().append('<span class="error-tip"><em></em></span>');
            }
            o.removeClass('tag-error').addClass('tag-right').siblings('.error-tip').attr('class','right-tip');
            if(errBox)errBox.html('&nbsp;');
            sendFrom = true;
        }
    }
    /**
     * 发送短信按钮倒计时效果
     */
    var emailReg = function(s){
        var result = {
            match : true,
            msg : '正确'
        },
            reg = new RegExp(/^[0-9a-zA-Z_][_.0-9a-zA-Z-]{0,31}@([0-9a-zA-Z][0-9a-zA-Z-]{0,30}\.){1,4}[a-zA-Z]{2,4}$/);
        if( !reg.test(s) ){
            result = {
                match : false,
                msg : errStr.email
            }
        }
        return result;
    }

    var mobileReg = function (s) {
        //严格
        //var patrn = /^(13[0-9]|15[012356789]|18[0-9]|14[57]|00852)[0-9]{8}$/;
        //宽松
        var patrn = /^(1)[0-9]{10}$/;
        return {
            match : patrn.test(s),
            msg : '输入的手机号码有误'
        };
    }

    //验证旧邮箱
    $('[data-check="email"]').on('blur keyup', function(){
        var email = $(this).val(),
            tips = $(this).parent().siblings('.tips');
        if(email.length<1){
            changeClass(false, $(this), tips, '请输入邮箱');
        }else{
            if(emailReg(email).match){
                changeClass(true, $(this), tips, '');
            }else{
                changeClass(false, $(this), tips, '邮箱格式无效');
            }
        }
    });

    //验证是否是新的邮箱（从服务器端验证）
    $('[data-check="new-email"]').on('blur keyup', function(){
        var $this = $(this),
            email = $(this).val(),
            tips = $(this).parent().siblings('.tips');

        if(email.length<1){
            changeClass(false, $this, tips, '请输入邮箱');
        }else{
            if(emailReg(email).match){
                util.setAjax(inter.getApiUrl().checkEmailUrl, {email: email} ,function(json){
                    if(json.status){
                        changeClass(true, $this, tips, '');
                    }else{
                        changeClass(false, $this, tips, '邮箱已被占用');
                    }
                },function(){
                    changeClass(false, $this, tips, '验证失败');
                });
            }else{
                changeClass(false, $this, tips, '邮箱格式无效');
            }
        }
    });

    //验证密码
    // $('[data-check="pwd"]').on('blur keyup', function(){
    //     var pwd = $(this).val(),
    //         tips = $(this).parent().siblings('.tips');
    //     if(pwd.length<1){
    //         changeClass(false, $(this), tips, '请输入密码');
    //     }else{
    //         if(pwd.length<6 || pwd.length>18){
    //             changeClass(false, $(this), tips, '密码长度为6-18位字符');
    //         }else{
    //             changeClass(true, $(this), tips, '');
    //         }
    //     }
    // });

    //验证码
    $('[data-check="code"]').on('blur keyup', function(){
        var phoneCode = $(this).val(),
            tips = $(this).parent().siblings('.tips');
        if(phoneCode.length<1){
            changeClass(false, $(this), tips, '请输入验证码');
        }else{
            changeClass(true, $(this), tips, '');
        }
    });

    //验证是否是新的手机号码（从服务端验证）
    $('[data-check="new-phone"]').on('blur keyup', function(){
        var phoneNum = $(this).val(),
            tips = $(this).parent().siblings('.tips'),
            $this = $(this);
            
        if(phoneNum.length<1){
            changeClass(false, $this, tips, '请输入手机号码');
        }else{
            if( mobileReg(phoneNum).match ){
                util.setAjax(inter.getApiUrl().checkPhoneUrl, {phoneNum: phoneNum} ,function(json){
                    if(json.status && !json.exists){
                        changeClass(true, $this, tips, '');
                    }else if(json.status && json.exists){
                        changeClass(false, $this, tips, '手机号已被使用');
                    }else{
                        changeClass(false, $this, tips, '验证失败');
                    }
                },function(){
                    changeClass(false, $this, tips, '验证失败');
                });
            }else{
                changeClass(false, $this, tips, '输入的手机号码有误');
            }
        }
    });

    //验证手机号是否正确
    $('[data-check="phone"]').on('blur keyup', function(){
        var phoneNum = $(this).val(),
            tips = $(this).parent().siblings('.tips'),
            $this = $(this);
            
        if(phoneNum.length<1){
            changeClass(false, $this, tips, '请输入手机号码');
        }else if ($.trim(phoneNum).length === 11) {
            changeClass(true, $this, tips, '');
        }else{
            changeClass(false, $this, tips, '输入的手机号码有误');
        }
    });

    return {
        /**
         * 初始化表单验证
         */
        init : function(obj,submitCall){
            var self = this;
                self.$elem = $(obj);
            var allItem = self.$elem.find('button[type="text"],input[type="text"],input[type="password"],textarea'),
                errBox = $('.error-msg');
            allItem.each(function(i,n){
                var valid = $(n).attr('data-validate'),
                    rule = $(n).attr('data-rule');
                if(valid){
                    $(n).on('blur keyup',function(){
                        self.checkData($(this), errBox);
                    });
                }
            });

            self.$elem.on('submit',function(){
                allItem.blur();
                if(obj.indexOf('reg')>-1){

                }else if(obj.indexOf('login')>-1){
                    if($(obj).find('.error-tip').length>1){
                        $('.error-msg').html('请输入用户名和密码');
                    }
                }
                if(!sendFrom){
                    return false;
                }
                if(submitCall){
                    submitCall();
                }
            });
        },

        /**
         * 邮箱账号补全
         */
        emailTips : function(obj, call){
            var self = this,
                loginObj = $(obj),
                oldData = ["qq.com","163.com","yahoo.com","yahoo.cn","126.com","sina.com","sohu.com","gmail.com","hotmail.com","foxmail.com"];

            loginObj.on('keyup', function(){
                var inpVal = $(this).val(),
                    newData = [],
                    lastData = null;
                $.each(oldData, function (i, n){
                    var newStr = '';
                    if (inpVal.indexOf("@" ) <= -1){
                        newStr = inpVal + "@" + n;
                    }else{
                        newStr = inpVal.substring(0, inpVal.indexOf( "@" ))+"@" + n;
                    }
                    if(lastData != newStr){
                        lastData =  newStr;
                        newData.push(newStr);
                    }
                });

                /**
                 * 调用自动补全插件
                 */
                loginObj.AutoComplete({
                    'follow' : true,
                    'width' : 'auto',
                    'maxHeight' : "160",
                    'afterSelectedHandler' : function(){
                        call && call();
                    },
                    'data': newData
                });
            });
        },
        /**
         * 根据伪属性规则进行验证
         */
        checkData : function(o, errBox){
            var self = this;
            var valid = o.attr('data-validate'),
                errMsg = o.attr('data-err'),
                rule = o.attr('data-rule'),
                r = self[valid](o.val(),rule);

            self.changeClass(r.match, o, errBox, errMsg);
        },

        GetPhoneCode: GetPhoneCode,

        /**
         * input状态，附带图标和提示
         * match: 显示图标正确或错误, o:input jQuery对象, errBox 错误提示的<div>jquery对象, errMsg 错误信息
         */
        changeClass : changeClass,
        unique : function(){

        },
        required : function(s){
            var result = {
                match : true,
                msg : '正确'
            };
            if( !s || s.length<1 ){
                result = {
                    match : false,
                    msg : errStr.required
                }
            }
            return result;
        },
        email : emailReg,
        url : function(s){
            var strRegex = /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i;
            var re = new RegExp(strRegex);
            return re.test(s);
        },
        date : function(){
            try{

            }catch(e){

            }
        },
        digits : function(s){
            var result = {
                match : true,
                msg : '正确'
            };
            if( !s.match(/^-?\d+$/) ){
                result = {
                    match : false,
                    msg : errStr.digits
                }
            }
            return result;
        },
        IDCard : function(s){
            var result = {
                match : true,
                msg : '正确'
            };
            if( !s.match(/^\d{15}|\d{}18$/) ){
                result = {
                    match : false,
                    msg : errStr.IDCard
                }
            }
            return result;
        },
        equalTo : function(s, ss){
            var result = {
                match : true,
                msg : '正确'
            };
            if( s !== ss ){
                result = {
                    match : false,
                    msg : errStr.equalTo
                }
            }
            return result;
        },
        accept : function(){

        },
        maxLength : function(){

        },
        minLength : function(){

        },
        rangeLength : function(s,r){
            var result = {
                    match : true,
                    msg : '正确'
                },
                rule = r.split(',');
            if( s.length < rule[0] || s.length > rule[1] ){
                result = {
                    match : false,
                    msg : util.strFormat(errStr.rangeLength, r)
                }
            }
            return result;

        },
        isMobile : mobileReg,
        isUserName: function(str){
            var reg = /^.{1,16}$/;
            return reg.test(str);
        },
        isDecimal: function(n){
            var reg = /^[0-9]+.[0-9]+$/;
            return reg.test(n);
        },
        isPositiveInt : function(n){
            var reg = /^[0-9]+$/;
            return reg.test(n);
        },
        isDecimalAndPositiveInt: function(n){
            var reg = /^[0-9]+(\.[0-9]+)?$/;
            return reg.test(n);
        },
        isLimitInt : function(n, w){
            var self = this;
            if((n+'').length <= w){
                return self.isPositiveInt(n);
            }else{
                return false;
            }
        },
        range : function(){

        },
        max : function(){

        },
        min : function(){

        }
    };
});
