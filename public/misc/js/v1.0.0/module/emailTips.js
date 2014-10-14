/**
 * @description: Email自动补全模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/emailTips', [
    'common/util',
    'module/checkForm'
], function(util, check){
    var nowId,
        totalId,
        can1press = false,
        emailAfter,
        emailBefore,
        template = [
            '<div id="myEmail" style="width:250px; height:auto; background:#fff; color:#6B6B6B; position:absolute; z-index:99; left:0px; top:#{top}px; border:1px solid #ccc; line-height:18px;">',
            '</div>'
        ].join(''),
        liTpl = [
            '<div class="newEmail" style="width:244px; color:#6B6B6B; overflow:hidden; padding:3px; cursor:pointer;"><span style=" color:#666666">#{press}</span>#{emailVar}</div>'
        ].join('');

    return {
        init : function(elem, callBack){
            var self = this;
            self.$elem = $(elem);
            var elemPos = self.$elem.offsetTop + self.$elem.height() + 6;
            self.$elem.focus(function(){ //文本框获得焦点，插入Email提示层
                var email = $("#myEmail");
                if( email.length <= 0 ){
                    email = $(util.template(template, {
                        top: elemPos
                    }));
                    $(this).after(email);
                }
                if(email.html()){
                    email.css("display", "block");
                    email.find(".newEmail").css("width", email.width());
                    can1press = true;
                } else {
                    email.css("display","none");
                    can1press = false;
                }
            }).keyup(function(){ //文本框输入文字时，显示Email提示层和常用Email
                    var press = $(this).val(),
                        email = $("#myEmail");
                    if ( press != "" || press != null ){
                        var emailTxt = "";
                        var emailVar = ["@qq.com","@163.com","@126.com","@sina.com","@yahoo.com","@gmail.com","@hotmail.com","@foxmail.com"];
                        totalId = emailVar.length;
                        var emailMy = util.template(liTpl,{
                            press : press,
                            emailVar : ''
                        });
                        if(!check.email(press).match){
                            for(var i=0; i < emailVar.length; i++) {
                                emailTxt = emailTxt + util.template(liTpl,{
                                    press : press,
                                    emailVar : emailVar[i]
                                })
                            }
                        } else {
                            emailBefore = press.split("@")[0];
                            emailAfter = "@" + press.split("@")[1];
                            for(var i=0; i<emailVar.length; i++) {
                                var theEmail = emailVar[i];
                                if(theEmail.indexOf(emailAfter) == 0)
                                {
                                    emailTxt = emailTxt + util.template(liTpl,{
                                        press : emailBefore,
                                        emailVar : emailVar[i]
                                    })
                                }
                            }
                        }
                        email.html(emailMy + emailTxt);
                        if(email.html()){
                            email.css("display","block");
                            email.css("width",email.width());
                            can1press = true;
                        } else {
                            email.css("display","none");
                            can1press = false;
                        }
                    }
                    if (press=="" || press==null){
                        email.html("");
                        email.css("display","none");
                    }
                });

            //文本框失焦时删除层
            $(document).click(function(){
                if(can1press){
                    $("#myEmail").remove();
                    can1press = false;
                    if(self.$elem.focus()){
                        can1press = false;
                    }
                }
            });

            $(".newEmail").live("mouseover",function(){ //鼠标经过提示Email时，高亮该条Email
                $(".newEmail").css("background","#FFF");
                $(this).css("background","#CACACA");
                $(this).focus();
                nowId = $(this).index();
            }).live("click",function(){ //鼠标点击Email时，文本框内容替换成该条Email，并删除提示层
                    var newHtml = $(this).html();
                    newHtml = newHtml.replace(/<.*?>/g,"");
                    self.$elem.val(newHtml);
                    $("#myEmail").remove();
                    if(callBack)callBack(newHtml);
                });

            $(document).bind("keydown",function(e){
                var newEmail = $(".newEmail");
                if(can1press){
                    switch(e.which){
                        case 38:
                            if (nowId > 0){
                                newEmail.css("background","#FFF");
                                newEmail.eq(nowId).prev().css("background","#CACACA").focus();
                                nowId = nowId-1;
                            }
                            if(!nowId){
                                nowId = 0;
                                newEmail.css("background","#FFF");
                                newEmail.eq(nowId).css("background","#CACACA");
                                newEmail.eq(nowId).focus();
                            }
                            break;

                        case 40:
                            if (nowId < totalId){
                                newEmail.css("background","#FFF");
                                newEmail.eq(nowId).next().css("background","#CACACA").focus();
                                nowId = nowId+1;
                            }
                            if(!nowId){
                                nowId = 0;
                                newEmail.css("background","#FFF");
                                newEmail.eq(nowId).css("background","#CACACA");
                                newEmail.eq(nowId).focus();
                            }
                            break;

                        case 13:
                            var newHtml = newEmail.eq(nowId).html();
                            newHtml = newHtml.replace(/<.*?>/g,"");
                            self.$elem.val(newHtml);
                            $("#myEmail").remove();
                    }
                }
            })
        }
    }

});
