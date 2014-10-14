/**
 * @description: 登录页面
 * @author: Young Foo(young.foo@helome.com)
 * @update: Young Foo(young.foo@helome.com)
 */
require([
	'common/interface',
    'module/login',
    'module/pageCommon',
    'module/prettifyForm',
    'module/checkForm',
    'module/strength'
], function(inter, login, common, prettify, check, strength){

	common.initLogin();
	
    /**
     * 绑定login按钮
     */

    window.thirdloginType = "";

    var initLoginBind = function(){

        var $loginName = $('#loginName'),
            $loginPwd = $('#loginPwd'),
            $block = $(".login-main");

        var checkbox = prettify.initCheckbox();

        check.emailTips('#loginName');

        $block.find('.btn-login').on('click', function(){
            login.login({
            	block: $block,
            	loginName: $loginName,
            	loginPwd: $loginPwd,
            	loginUrl: inter.getApiUrl().toLoginUrl,
            	loginBtnText: "登录"
            });
        });

        $loginName.focus();

        $loginPwd.on('keydown', function(e){
            if(e.keyCode === 13){
		        login.login({
		        	block: $block,
		        	loginName: $loginName,
		        	loginPwd: $loginPwd,
		        	loginUrl: inter.getApiUrl().toLoginUrl,
		        	loginBtnText: "登录"
		        });
            }
        });

        // test the checkbox
        // $(".main-pic").click(function(){
        // 	checkbox.setCheckboxVal(checkbox.get("#remember-me"));
        // });
        // $(".iphone").click(function(e){
        // 	checkbox.cancelCheckboxVal(checkbox.get("#remember-me"));
        // 	e.stopPropagation();
        // });

		// $(".main-pic").click(function(){
  //       	checkbox.toggleCheckboxVal(checkbox.get("#remember-me"));
  //       });
    }

    /**
     * 绑定第三方登录按钮
     */
    var initThirdLoginBind = function(){
    	$(".login-third-btn").on("click", "a", function(){
    		var $this = $(this),
    			url = $this.attr("url"),
    			btn = $this.attr("class"),
    			width = "0",
    			height = "0";

    		switch(btn){
    			case "sina-weibo":
    				width = "600";
    				height = "400";
    				window.thirdloginType = "新浪微博";
    				break;
    			case "qq-zone":
    				width = "700";
    				height = "500";
    				window.thirdloginType = "腾讯QQ";
    				break;
    			case "douban":
    				width = "350";
    				height = "430";
    				window.thirdloginType = "豆瓣";
    				break;
    			default:
    				width = "700";
    				height = "600";
    				window.thirdloginType = "新浪微博";
    		}

    		window.open(url, "", "height="+ height +", width="+ width +", top=100, left=200,toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no");
    	});
    }

    /**
     * 将第三方账号绑定到自己账号
     */
    var initThirdLoginBtnBind = function(){

        var $loginName = $('#thirdLoginName'),
            $loginPwd = $('#thirdLoginPwd'),
            $block = $(".J-third-login");

        check.emailTips('#thirdLoginName');

        $block.find('.btn-login').on('click', function(){
            login.login({
            	block: $block,
            	loginName: $loginName,
            	loginPwd: $loginPwd,
            	loginKey: window.thirdLoginKey,
            	loginUrl: inter.getApiUrl().toThirdLoginUrl,
            	loginBtnText: "登录并绑定"
            });
        });

        $loginName.focus();

        $loginPwd.on('keydown', function(e){
            if(e.keyCode === 13){
		        login.login({
		        	block: $block,
		        	loginName: $loginName,
		        	loginPwd: $loginPwd,
		        	loginKey: window.thirdLoginKey,
		        	loginUrl: inter.getApiUrl().toThirdLoginUrl,
		        	loginBtnText: "登录并绑定"
		        });
            }
        });

    }

    /**
     * 将第三方账号绑定到自己账号
     */
    var initThirdFinishBind = function(){

        var $block = $('.J-third-reg'),
            $regEmail = $('#thirdRegEmail');

        prettify.initCheckbox();

        strength.init('#thirdRegPwd', {min: 6, max: 18});

        check.emailTips('#thirdRegEmail');

        login._changeCode();

        $block.find('.replace-code').on('click', function(e){
            login._changeCode();
            e.preventDefault();
        });

        $block.find('.btn-reg').on('click', function(e){
            login.register({
                block:          $block,
                regName:        $regEmail,
                regPwd:         $('#thirdRegPwd'),
                regConfirmPwd:  $('#thirdRegConfirmPwd'),
                regCode:        $('#thirdRegCode'),
                regKey: 		window.thirdLoginKey,
                regUrl:         inter.getApiUrl().toThirdRegisterUrl
            });
            e.preventDefault();
        });

        $regEmail.focus();
    }

    /**
     * 登录跳转
     * key: 用户ID, imgURL: 图像URL, name: 用户名
     */
	window.loginSkip = function(isLogin, key, imgURL, name){

		var isLoginStage = parseInt(isLogin);
		if (isLoginStage) {
			location.href = "/";
			return;
		};

		loginSkipDialog = $.dialog({
		    content: [
		    '<div class="dialog-tit clearfix"><span class="span-left">helome欢迎您</span></div>',
	        '<div class="main tl-skip">',
	            '<div class="tl-info clearfix">',
	                '<img class="tl-head" src="">',
	                '<div class="tl-content">',
	                    '<div class="title">您的' + window.thirdloginType + '帐号</div>',
	                    '<div class="name"></div>',
	                '</div>',
	            '</div>',
	            '<div class="main-block">',
	                '<div class="main-block-text">还差一步，即可完成登录设置</div>',
	            '</div>',

	            '<div class="main-block">',
	                '<input type="button" class="btn-default btn-lg btn-green" id="btn-finish" value="完善账号" />',
	            '</div>',
	            '<div class="main-block">',
	                '<input type="button" class="btn-default btn-lg btn-white" id="btn-bind" value="绑定已有账号" />',
	            '</div>',
	            '<div class="main-block">',
	                '<input type="button" class="btn-default btn-lg btn-white" id="btn-thirdLogin" value="立即登录" />',
	            '</div>',
	        '</div>'].join(''),
		    lock: true,
		    initialize: function(){
		    	//设置dialog 显示头像和名称
		    	var dialog = $(".tl-skip");
		    	dialog.find(".tl-head").attr("src", imgURL);
		    	dialog.find(".tl-content .name").text(name);

		    	//将用户名, KEY, 头像接入全局
		    	window.thirdLoginImgURL = imgURL;
		    	window.thirdLoginName = name;
		    	window.thirdLoginKey = key;

		    	//绑定完善账号按钮
		    	$("#btn-finish").on("click", function(){
		    		loginSkipDialog.close();
		    		loginFinish();
		    	});

		    	//绑定已有账号按钮
		    	$("#btn-bind").on("click", function(){
		    		loginSkipDialog.close();
		    		loginBind();
		    	});

		    	$("#btn-thirdLogin").on("click", function(){
		    		loginSkipDialog.close();
		    		loginThirdLogin();
		    	});
		    }
		});
	}

	var loginFinish = function(){
		finishDialog = $.dialog({
		    content: [
		    '<div class="dialog-tit clearfix"><span class="span-left">helome欢迎您</span></div>',
	        '<div class="main tl-reg tl-third J-third-reg clearfix">',

	            '<div class="tl-third-left fl">',
	            	'<form method="post">',
		                '<div class="main-block-text">完成账号创建后，即可直接登录helome！</div>',

		                '<div class="main-block">',
		                    '<div class="tag-container">',
		                        '<input type="text" class="txt" name="thirdRegEmail" id="thirdRegEmail" maxlength="200" placeholder="您的邮箱账号" />',
		                    '</div>',
		                '</div>',

		                '<div class="main-block">',
		                    '<div class="tag-container">',
		                        '<input type="password" class="txt" name="thirdRegPwd" id="thirdRegPwd" maxlength="18" placeholder="密码" />',
		                    '</div>',
		                '</div>',

		                '<div class="main-block">',
		                    '<div class="tag-container">',
		                        '<input type="password" class="txt" name="thirdRegConfirmPwd" id="thirdRegConfirmPwd" maxlength="18" placeholder="确认密码" />',
		                    '</div>',
		                '</div>',

		                '<div class="main-block main-block-code">',
		                    '<div class="tag-container">',
		                        '<input type="text" class="txt txt-short" name="thirdRegCode" id="thirdRegCode" maxlength="4" placeholder="验证码" />',
		                    '</div>',
		                    '<img src="" class="code" height="34" width="70" /><a href="javascript:" class="replace-code">换一个</a>',
		                '</div>',

		                '<div class="main-block">',
		                    '<label for="regAgreement">',
		                        '<input id="regAgreement" type="checkbox" name="regAgreement" value="1" />',
		                        '<span>我已阅并接受</span>',
		                    '</label>',
		                    '<a href="/regagreement" target="_blank" class="f14">《helome用户注册协议》</a>',
		                    '<div class="error-msg hide"></div>',
		                '</div>',
		                
		                '<div class="main-block clearfix">',
		                    '<input type="button" value="注册并绑定" class="btn-default btn-lg btn-green btn-left btn-reg" />',
		                    '<input type="button" value="取 消" class="btn-default btn-lg btn-white btn-cancel" />',
		                '</div>',
		            '</form>',
	            '</div>',

	            '<div class="tl-third-right">',
	                '<div class="tl-info clearfix">',
	                    '<img class="tl-head" src="' + window.thirdLoginImgURL + '">',
	                    '<div class="tl-content">',
	                        '<div class="title">您的' + window.thirdloginType + '帐号</div>',
	                        '<div class="name">' + window.thirdLoginName + '</div>',
	                    '</div>',
	                '</div>',
	                '<div class="main-block tl-third-benefit">',
	                    '完善帐号的好处<br>',
	                    '1.提升帐号安全等级<br>',
	                    '2.使用邮箱登录helome<br>',
	                    '3.便于快速找回密码<br>',
	                    '4.享受更多服务<br>',
	                '</div>',
	            '</div>',
	        '</div>'].join(''),
		    lock: true,
		    initialize: function(){
		    	initThirdFinishBind();
		    	$(".btn-cancel").on("click", function(){
		    		finishDialog.close();
		    	});
		    }
		});

	}

    var loginBind = function(){
		bindDialog = $.dialog({
		    content: [
		    '<div class="dialog-tit clearfix"><span class="span-left">helome欢迎您</span></div>',
	        '<div class="main tl-login tl-third J-third-login clearfix">',

	            '<div class="tl-third-left fl">',
	            	'<form method="post">',
		                '<div class="main-block-text">已有helome帐号，直接绑定！</div>',

		                '<div class="main-block">',
		                    '<div class="tag-container">',
		                        '<input placeholder="用户名" type="text" class="txt" name="thirdLoginName" id="thirdLoginName" maxlength="200" data-validate="required" data-err="请输入用户名"/>',
		                    '</div>',
		                '</div>',

		                '<div class="main-block">',
		                    '<div class="tag-container">',
		                        '<input placeholder="密码" type="password" class="txt" name="thirdLoginPwd" id="thirdLoginPwd" maxlength="18" data-validate="required" data-err="请输入密码" data-rule="[6,18]"/>',
		                    '</div>',
		                '</div>',
		                '<div class="main-block">',
		                    '<div class="clearfix">',
		                        '<a href="@controllers.routes.Application.forgetpwd()" class="forget-password fr">忘记密码？</a>',
		                    '</div>',
		                    '<div class="error-msg hide"></div>',
		                '</div>',
		                
		                '<div class="main-block clearfix">',
		                    '<input type="button" value="登录并绑定" class="btn-default btn-lg btn-green btn-left btn-login" />',
		                    '<input type="button" value="取 消" class="btn-default btn-lg btn-white btn-cancel" />',
		                '</div>',
		            '</form>',
	            '</div>',

	            '<div class="tl-third-right">',
	                '<div class="tl-info clearfix">',
	                    '<img class="tl-head" src="' + window.thirdLoginImgURL + '">',
	                    '<div class="tl-content">',
	                        '<div class="title">您的' + window.thirdloginType + '帐号</div>',
	                        '<div class="name">' + window.thirdLoginName + '</div>',
	                    '</div>',
	                '</div>',
	                '<div class="main-block tl-third-benefit">',
	                    '完善帐号的好处<br>',
	                    '1.提升帐号安全等级<br>',
	                    '2.使用邮箱登录helome<br>',
	                    '3.便于快速找回密码<br>',
	                    '4.享受更多服务<br>',
	                '</div>',
	            '</div>',
	        '</div>'].join(''),
		    lock: true,
		    initialize: function(){
		    	initThirdLoginBtnBind();
		    	$(".btn-cancel").on("click", function(){
		    		bindDialog.close();
		    	});
		    }
		});
    }

    var loginThirdLogin = function(){
    	var keyData = window.thirdLoginKey;
        $.ajax({
        	url: inter.getApiUrl().toThirdLoginNoAccountUrl,
        	data: JSON.stringify({ "key" : keyData }),
        	dataType: 'json',
        	type: 'POST',
        	contentType : 'application/json; charset=utf-8',
        	success: function(json){
        		if (json.status) {
        			location.href = "/";
        		};
        	},
        	error: function(json){
        		loginSkipDialog.close();
        		$.alert(json.error);
        	}
        });
    }

    initLoginBind();
    initThirdLoginBind();

});
