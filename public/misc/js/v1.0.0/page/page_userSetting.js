/**
 * @description: 用户设置
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/interface',
    'module/pageCommon',
    'common/util',
    'module/checkForm',
    'module/strength',
    'module/prettifyForm'
],function(inter, common, util, check, strength, prettifyForm){
    common.initLogin();
    prettifyForm.initRadio();

    var oldTipsData = getTipsData();

    //按钮计时器
    var btnTimer = new check.GetPhoneCode();
    var newBtnTimer = new check.GetPhoneCode();

    //邮箱提示
    check.emailTips('#oldEmail');
    check.emailTips('#newEmail');

    //旧密码验证
    $('.J-edit-pwd').find(".btn-save").on('click', function(){
        var oPwd = $('#needOldPwd'),
            nPwd = $('#needNewPwd'),
            oPwdVal = oPwd.val(),
            nPwdVal = nPwd.val(),
            tips = oPwd.parent().siblings('.tips'),
            cTips = nPwd.parent().siblings('.tips');
        if(oPwdVal.length<1){
            check.changeClass(false, oPwd, tips, '请输入旧密码');
        }else{
            check.changeClass(true, oPwd, tips, '');
            if(nPwdVal.length > 5){
                if(nPwdVal === oPwd){
                    check.changeClass(false, nPwd, cTips, '新密码不能与旧密码相同');
                }else{
                    check.changeClass(true, nPwd, cTips, '');
                }
            }
        }
    });
    /**
      * 获取设置复选按钮值
      */
    function getTipsData(){
        var tips = {};
        tips.changePhoneNum = [];
        tips.changePassword = [];
        tips.differentPlaceLogin = [];
        tips.bookingTips = [];

        if(util.getByName('changePhoneNum')){
            util.getByName('changePhoneNum').each(function(i, n){
                tips.changePhoneNum.push($(n).val());
            });
        }
        if(util.getByName('changePassword')){
            util.getByName('changePassword').each(function(i, n){
                tips.changePassword.push($(n).val());
            });
        }
        if(util.getByName('differentPlaceLogin')){
            util.getByName('differentPlaceLogin').each(function(i, n){
                tips.differentPlaceLogin.push($(n).val());
            });
        }
        if(util.getByName('bookingTips')){
            util.getByName('bookingTips').each(function(i, n){
                tips.bookingTips.push($(n).val());
            });
        }

        return tips;
    }

    //新密码验证
    $('#needNewPwd').on('blur', function(){
        var pwd = $(this).val(),
            oldPwd = $('#needOldPwd').val(),
            cPwd = $('#confirmPwd'),
            cPwdVal = cPwd.val(),
            tips = $(this).parent().siblings('.tips'),
            cTips = cPwd.parent().siblings('.tips');
        if (pwd.length >= 1) {
            if (pwd.length < 6 || pwd.length > 18) {
                check.changeClass(false, $(this), tips, '密码长度为6-18位字符');
            } else {
                if(oldPwd === pwd){
                    check.changeClass(false, $(this), tips, '新密码不能与旧密码相同');
                }else{
                    check.changeClass(true, $(this), tips, '');
                    if (pwd !== cPwdVal) {
                        check.changeClass(false, cPwd, cTips, '两次输入的新密码不一致');
                    } else {
                        check.changeClass(true, cPwd, cTips, '');
                    }
                }
            }
        } else {
            check.changeClass(false, $(this), tips, '请输入新密码');
        }
    }).on('keyup', function(){
        var pwd = $(this).val(),
            oldPwd = $('#needOldPwd').val(),
            tips = $(this).parent().siblings('.tips');
        if(pwd.length<1){
            check.changeClass(false, $(this), tips, '请输入新密码');
        }else{
            if(pwd.length<6 || pwd.length>18){
                check.changeClass(false, $(this), tips, '密码长度为6-18位字符');
            }else{
                if(oldPwd === pwd){
                    check.changeClass(false, $(this), tips, '新密码不能与旧密码相同');
                }else{
                    check.changeClass(true, $(this), tips, '');
                }
            }
        }
    });

    strength.init('#needNewPwd', {min: 6, max: 18, arg: 'edit'});


    //确认密码验证
    $('#confirmPwd').on('blur keyup', function(){
        var pwd = $(this).val(),
            cPwd = $('#needNewPwd').val(),
            tips = $(this).parent().siblings('.tips');
        if(pwd.length<1){
            check.changeClass(false, $(this), tips, '请再次输入新密码');
        }else{
            if(pwd !== cPwd){
                check.changeClass(false, $(this), tips, '两次输入的新密码不一致');
            }else{
                check.changeClass(true, $(this), tips, '');
            }
        }
    });

    prettifyForm.initCheckbox({
        horizontalPos: "middle"
    });
    
    /**
     * 修改/编辑/绑定/设置 按钮操作
     */

    $('.setting-table').delegate('.btn-edit', 'click', function(e){
        var parent = $(this).closest('tr');
        if(parent.next().find('.J-edit-pwd').length>0){
            parent.find('.password-status').attr('class', 'password-hide');
        }
        parent.hide().next().show('slow').find('input').each(function(i, n){
            var curInput = $(n),
                curType = curInput.attr('type');
            if(curType === 'text' || curType === 'password'){
                curInput.focus();
                return false;
            }
        });
        e.preventDefault();
    });

    /**
     * 修改手机号码panel
     */

    $('.btn-phone-panel').on('click', function(e){
        var panel = $(".phone-panel");

        e.stopPropagation();
        panel.slideToggle(200);

        $(document).off("click");
        $(document).one("click", function(e){
            panel.hide();
        });

    });

    //点击原手机可以接收按钮
    $(".btn-phone-modify").on("click", function(){

        var panel = $(".J-modify-phone").closest("tr"),
            selfPanel = $(this).closest("tr"),
            tips = panel.find(".phone-modify-state");

        tips.html("");
        panel.show("slow");
        selfPanel.hide();

    });
    // 点击原手机不可接收按钮
    $(".J-sendMail-btn").on("click", function(){
        var method = "GET",
            url = inter.getApiUrl(),
            sucCall = function(d){
               if(d.status === 1){
                   window.location.href=(url.phoneMailSuccess);
               }else{
                   $.alert("邮件发送失败，请重试！");
               }
            },
            errCall = function(){
                $.alert("服务器繁忙请稍后重试")
            };
        util.setAjax(url.sendEmail,'', sucCall, errCall,method,false);
    });

    /**
     * 新添加手机的时候，发送手机短信验证码
     */
    $('.setting-table').delegate('.btn-sendCode', 'click', function(){
        var self = $(this),
            phone = $("#editPhoneInput"),
            phoneVal = phone.val();

        phone.blur();
        if(phone.siblings('.error-tip').length<1){
            var tips = $.tips('loading');

            newBtnTimer.startBtnTimer({
                url: inter.getApiUrl().sendPhoneSMSUrl,
                alert: tips,
                button: self,
                data: {"phoneNum": phoneVal}
            });
        }
    });

    //修改手机号发送验证码
    $(".btn-modify-sendCode").on("click", function(){
        var self = $(this),
            tipsModule = $(".J-modify-phone").find(".phone-modify-state");

        var tips = $.tips('loading');

        btnTimer.startBtnTimer({
            url: inter.getApiUrl().sendSMSByPhoneUrl,
            alert: tips,
            button: self,
            callback: function(o){
                tipsModule.html("已向当前绑定的手机号码" + o.phoneNum + "发送短信");
            }
        });

    });


    $(".btn-phone-modify-cancel").on("click", function(){
        var phoneBlock = $(".phone-block"),
            panel = $(this).closest("tr");

        btnTimer.resetBtn();
        phoneBlock.show();
        panel.hide();
    });
    /**
     * 保存操作
     */
    $('.setting-table').delegate('.btn-save', 'click', function(e){
        var self = $(this),
            parent = $(this).closest('tr'),
            edit = parent.prev().find('.btn-edit'),
            editType = $(this).closest('td');

        editType.find('input[type="text"], input[type="password"]').blur();
        if(editType.find('.error-tip').length<1){
            var tips = $.tips('loading');
            switch(editType.attr('class')){
                /*case 'edit-email':
                    var oldEmail = util.getByName('oldEmail').val(),
                        newEmail = util.getByName('newEmail').val(),
                        loginPwd = util.getByName('loginEmailPwd').val();

                    util.setAjax(inter.getApiUrl().saveEmailUrl, {"old": oldEmail, "new": newEmail, "psw": loginPwd}, function(json){
                        tips.close();
                        if(json.status){
                            parent.prev().find('span').html( util.getMaskEmail(newEmail) );
                            parent.hide().prev().show();
                            editType.find('input[type="text"], input[type="password"]').val('');
                            editType.find('.right-tip').remove();
                            editType.find('.error-tip').remove();
                            //$.alert('保存成功！');
                        }else{
                            $.alert(json.error);
                        }
                    },function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试！');
                    });
                    break;*/
                case 'J-edit-phone':
                    var phoneNum = $('#editPhoneInput').val(),
                        phoneCode = $('#editPhoneCode').val();

                    util.setAjax(inter.getApiUrl().savePhoneUrl, {"phoneNum": phoneNum, "code": phoneCode}, function(json){
                        tips.close();
                        if(json.status){
                            phoneNum = phoneNum.replace(phoneNum.substring(3,7), '****');
                            parent.prev().find('span.phone-num').html( phoneNum );
                            parent.hide().prev().show();
                            editType.find('input[type="text"], input[type="password"]').val('');
                            editType.find('.right-tip').remove();
                            editType.find('.error-tip').remove();
                            if(edit.text() === '绑定'){
                                edit.text('修改');
                                edit.attr("class", "blue btn-phone-panel");

                                $('.btn-phone-panel').off("click");
                                $('.btn-phone-panel').on('click', function(e){
                                    var panel = $(".phone-panel");

                                    e.stopPropagation();
                                    panel.slideToggle(200);

                                    $(document).off("click");
                                    $(document).one("click", function(e){
                                        panel.hide();
                                    });

                                });
                            }
                            editType.find('.btn-sendCode').css('color','#707070').attr('value','发送验证码').removeAttr('disabled');
                            $('.checkbox-btn[data-value="sms"]').removeClass('disabled');
                            //$.alert('保存成功！');
                        }else{
                            $.alert(json.error);
                        }
                    },function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试！');
                    });

                    break;
                //修改手机号码
                case 'J-modify-phone':
                    var phoneNum = $("#modifyPhoneInput").val(),
                        phoneCode = $('#modifyPhoneCode').val(),
                        phoneBlock = $('.phone-block'),
                        changeToken = $('#changeToken').val(),
                        phoneModifyBlock = editType.closest("tr");

                    util.setAjax(inter.getApiUrl().saveModifyPhoneUrl, {"phoneNum": phoneNum, "code": phoneCode, key: changeToken}, function(json){
                        tips.close();
                        if(json.status){
                            phoneNum = phoneNum.replace(phoneNum.substring(3,7), '****');
                            phoneBlock.find('.phone-num').html( phoneNum );
                            phoneModifyBlock.hide();
                            phoneBlock.show();
                            editType.find('input[type="text"], input[type="password"]').val('');
                            editType.find('.right-tip').remove();
                            editType.find('.error-tip').remove();
                            if(edit.text() === '绑定'){
                                edit.text('修改');
                            }
                            editType.find('.btn-sendCode').css('color','#707070').attr('value','发送验证码').removeAttr('disabled');
                            //$.alert('保存成功！');
                        }else{
                            $.alert(json.error);
                        }
                    },function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试！');
                    });

                    break;
                case 'J-edit-pwd':
                    var oldPwd = util.getByName('needOldPwd').val(),
                        newPwd = util.getByName('needNewPwd').val();

                    util.setAjax(inter.getApiUrl().saveNewPwdUrl, {"old": oldPwd,"new": newPwd}, function(json){
                        tips.close();
                        if(json.status){
                            parent.hide().prev().show();
                            editType.find('input[type="text"], input[type="password"]').val('');
                            editType.find('.right-tip').remove();
                            editType.find('.error-tip').remove();
                            var strength = parent.find('.password-strength').attr('class').replace('password-strength ',''),
                                strengthText = parent.find('.strength-text').text();
                            parent.prev().find('.password-hide').attr('class', 'password-status')
                                .find('.password-strength').attr('class', 'password-strength ' + strength);
                            parent.prev().find('.password-status').find('.strength-text').text(strengthText);
                            parent.find('.password-status').remove();
                            //$.alert('保存成功！');
                        }else{
                            $.alert(json.error);
                        }
                    },function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试！');
                    });
                    break;
                case 'J-edit-tips':
                    var tipsData = getTipsData();

                    util.setAjax(inter.getApiUrl().saveTipsUrl, {
                        "changePhoneNum": tipsData.changePhoneNum,
                        "changePassword": tipsData.changePassword,
                        "differentPlaceLogin": tipsData.differentPlaceLogin
                    }, function(json){
                        tips.close();
                        if(json.status){
                            parent.hide().prev().show();
                            if(tipsData.changePhoneNum.length || tipsData.changePassword.length || tipsData.differentPlaceLogin.length){
                                parent.prev().find('span').text('已设置');
                                if(edit.text() === '设置'){
                                    edit.text('修改');
                                }
                            }else{
                                parent.prev().find('span').text('未设置');
                                edit.text('设置');
                            }
                        }else{
                            $.alert(json.error);
                        }
                    },function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试！');
                    });
                    break;
                case 'J-edit-booking':
                    var bookingData = getTipsData();
                    util.setAjax(inter.getApiUrl().saveBookingTipsUrl,{bookingRemind : bookingData.bookingTips},
                    function(json){
                        tips.close();
                        if(json.status){
                            parent.hide().prev().show();
                            if(bookingData.bookingTips.length){
                                parent.prev().find('span').text('已设置');
                                if(edit.text() === '设置'){
                                    edit.text('修改');
                                }
                            }else{
                                parent.prev().find('span').text('未设置');
                                edit.text('设置');
                            }
                            //$.alert('保存成功！');
                        }else{
                            $.alert(json.error);
                        }
                    },function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试！');
                    });
                    break;
                case 'J-third-login':
                    var email = util.getByName('newEmail').val(),
                        pwd = util.getByName('needNewPwd').val();

                    util.setAjax(inter.getApiUrl().saveAppLoginUrl, {
                        "email": email,
                        "pwd": pwd
                    }, function(json){
                        tips.close();
                        if(json.status){
                            $.alert('保存成功！', function(){
                                window.location.href = decodeURIComponent(util.location().referer);
                            });
                        }else{
                            $.alert(json.error);
                        }
                    },function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试！');
                    });
                    break;
                default:
                    break;
            }
        }
        e.preventDefault();
    });

    /**
     * 取消操作
     */
    $('.setting-table').delegate('.btn-cancel', 'click', function(e){
        var parent = $(this).parents('tr').eq(0);
        if(parent.next().find('.J-edit-pwd').length>0){
            parent.find('.password-hide').attr('class', 'password-status');
        }

        newBtnTimer.resetBtn();

        parent.hide().prev().show();
        e.preventDefault();
    });

    /**
     * 取消绑定
     */
    $('.btn-unbind').on('click', function(e){
        var parent = $(this).closest('li'),
            href = $(this).prop('href'),
            tips = '';
        switch (parent.prop('class')){
            case 'bind-sina-weibo':
                tips = '您确定要解除新浪微博的账号绑定吗？';
                break;
            case 'bind-tencent-weibo':
                tips = '您确定要解除腾讯微博的账号绑定吗？';
                break;
            default:
                tips = '您确定要解除该账号的绑定吗？';
                break;
        }
        $.confirm(tips, function(){
            location.href = href;
        });
        e.preventDefault();
    });

    util.onBeforeBomUnload(function(){
        var nowTipsData = getTipsData(),
            isShowMsg = false,
            showMsg = '';
        if($('.J-edit-tips').closest('tr').css('display') != 'none' && (nowTipsData.changePhoneNum.join('') != oldTipsData.changePhoneNum.join('') || nowTipsData.changePassword.join('') != oldTipsData.changePassword.join('') || nowTipsData.differentPlaceLogin.join('') != oldTipsData.differentPlaceLogin.join(''))){
            isShowMsg = true;
            showMsg = '您的安全提示设置操作还未保存。';
        }
        if($('.J-edit-booking').closest('tr').css('display') != 'none' && nowTipsData.bookingTips.join('') != oldTipsData.bookingTips.join('')){
            isShowMsg = true;
            showMsg = showMsg ? '您的安全提示和预约提示设置操作还未保存。' : '您的预约提示设置操作还未保存。';
        }
        if(isShowMsg){
            return showMsg;
        }
    });
});