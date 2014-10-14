/**
 * @description: 修改手机号码
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 * @date: 2013/12/29
 */

require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/checkForm'
],function(inter, util, common, check){
    common.initLogin();
    //输入手机号

    var panel = $(".pay-form"),
        newPhoneInput = panel.find("#new-num"),
        phoneCodeInput = panel.find("#phone-code"),
        btnSendCode = panel.find(".btn-sendCode");

    //按钮计时器
    var btnTimer = new check.GetPhoneCode();

    //重新发送验证码
    btnSendCode.on("click", function(){
        var self = $(this),
            tipsModule = panel.find(".phone-modify-state"),
            newPhoneNum = newPhoneInput.val();

        newPhoneInput.blur();

        if(newPhoneInput.siblings('.error-tip').length<1){
            var tips = $.tips('loading');

            btnTimer.startBtnTimer({
                url: inter.getApiUrl().sendSMSByEmail,
                alert: tips,
                button: self,
                data: {newPhoneNum: newPhoneNum},
                callback: function(o){
                    tipsModule.html("已向当前绑定的手机号码" + newPhoneNum + "发送短信");
                }
            });
        }

    });
    
    $(".btn-save").on("click", function(){
        var newPhoneNum = newPhoneInput.val(),
            //phoneNum = oldPhoneInput.val(),
            changeToken = $('#changeToken').val(),
            phoneCode = $.trim(phoneCodeInput.val());

        if (phoneCode.length == 0) {
            phoneCodeInput.siblings(".tips").text("请输入验证码");
            return;
        }else{
            phoneCodeInput.siblings(".tips").text("");
        };

        //修改手机号，发送两个参数
        util.setAjax("/user/usersetting/bindNewPhone", {"code": phoneCode, "newPhoneNum": newPhoneNum, key: changeToken}, function(json){
        //util.setAjax("/user/usersetting/bindNewPhone", {"phoneNum": phoneNum, "code": phoneCode, "newPhoneNum": newPhoneNum}, function(json){
            if(json.status){

                panel.find('input[type="text"], input[type="password"]').val('');
                panel.find('.right-tip').remove();
                panel.find('.error-tip').remove();

                btnSendCode.css('color','#707070').attr('value','发送验证码').removeAttr('disabled');
                $.alert('保存成功！',function(){
                    location.href = "/user/usersetting/phoneresetsuccess";
                });

            }else{
                $.alert(json.error);
            }
        },function(){
            $.alert('服务器繁忙，请稍后再试！');
        });
    });

});
