/**
 * @description: 个人基本信息
 * @author: Young Foo(Young.Foo@helome.com)
 * @data: 2014/4/28
 */

require([
    'common/interface',
	'common/util',
    'module/pageCommon',
    'module/prettifyForm',
    'module/textBoxList',
    'module/charFormat',
    'module/userDetail',
    'module/enResume'
], function(inter, util, common, prettifyForm, textBox, charFormat, DetailAPI, enResume){

	//定义模板
    var	tagsTpl = '<span data-value="#{tagName}">#{tagName}</span>',
	    
        oldInfoData = {},
        tagModule,
        industry,

        $userId = $("#userId"),
        $userName = $("#userName"),
        $userIntro = $("#userIntroduction"),
        $userJob = $("#userJob"),
        $userTimeZome = $("#userTimeZome"),
        $userCountryModule = $("#userCountry"),
        $userCountry = $userCountryModule.find(".user-country"),
        $userIndustry = $("[name=userIndustry]");

    var getUserId = function(){
        return $userId.val();
    }

    var getUserName = function(){
        return $.trim($userName.val());
    }

    var getUserGender = function(){
        return util.getByName('sex').val();
    }

    var getUserCountry = function(){
        return $userCountry.text();
    }

    var getTimeZome = function(){
        return $userTimeZome.val();
    }

    var getUserJob = function(){
        return $.trim($userJob.val());
    }

    var getUserIntro = function(){
        return $userIntro.val().replace(/\n+?(?=\n)/gm, "");
    }

    /**
     * 获取个人资料数据
     * return 个人资料的json数据
     */
    var getInfoData = function() {
        var data = {};
        
        data.userName =     getUserName();
        data.gender =       getUserGender();
        data.country =      getUserCountry();
        data.timeZone =     getTimeZome();
        data.job =          getUserJob();
        data.introduction = getUserIntro();
        data.industry =     industry.getIndustry();
        data.tags =         tagModule.getItemsVal();

        return data;
    }

	//初始化标签模块

    var init = function(){
        //初始化页面
        DetailAPI.initVerify();

    	//初始化登陆和搜索
	    common.initLogin();

        enResume.init($('.user-info .tab-menu'));

	    //配置标签
	    tagModule = textBox.initTextBox({
	        target:'#tags-input',
	        dropDownData: inter.getApiUrl().getTagsUrl,
	        width:'auto',
	        maxHeight:'189'
	    });

        //初始化时区
        initTimeZone();
        
	    //初始化select表单
	    var select = prettifyForm.initSelect(),
            radio = prettifyForm.initRadio(),
	    	checkbox = prettifyForm.initCheckbox();

		//初始化国家
		initCountry();

        //个人简介
        initIntroduction();

        //初始化行业选择限制
        industry = initIndustry(checkbox);

        oldInfoData = getInfoData();


        //页面保存交互
        $(".btn-user-info-save").on("click", function(){
            // 请求接口保存个人信息
            saveUserInfo();
        });

        $userName.on("blur", function(){
            DetailAPI.verifyInput([{
                target: $userName,
                maxLen: 16,
                outTips: "姓名最长16个中文字符或32个英文、数字",
                nullTips: "姓名不能为空"
            }]);
        });

        $userJob.on("blur", function(){
            DetailAPI.verifyInput([{ //初始化职业
                target: $userJob,
                maxLen: 16,
                outTips: "最长填写16个中文字符或32个英文、数字"
            }]);
        });

        //setRadioVal
        // $(".index-icon-footlogo").on("click", function(){
        //     radio.setRadioVal(radio.get("#service-discuss"));
        // });
    }

    var initIndustry = function(checkbox){
        var $industryCheckbox = $('.industry-checkbox'),
            $industryCheckboxBtn = $industryCheckbox.find('.checkbox-btn'),
            $industryCheckedLen = null;

        $industryCheckboxBtn.each(function(){
            var $this = $(this);
            checkbox.checkboxChange($this, function(){
                $industryCheckedLen = $industryCheckboxBtn.find('.checkbox.checked').length;
                if( $industryCheckedLen < 3 ){
                    $industryCheckboxBtn.removeClass('disabled');
                }else{
                    $industryCheckboxBtn.find('.checkbox:not(.checked)').parents('.checkbox-btn').addClass('disabled');
                }
            }, function(){
                $industryCheckedLen = $industryCheckboxBtn.find('.checkbox.checked').length;
                if( $industryCheckedLen === 3 ) {
                    $industryCheckboxBtn.find('.checkbox:not(.checked)').parents('.checkbox-btn').addClass('disabled');
                }else{
                    $industryCheckboxBtn.removeClass('disabled');
                }
            });
        });

        $(function(){
            if($industryCheckboxBtn.find('.checkbox.checked').length >= 3){
                $industryCheckboxBtn.find('.checkbox:not(.checked)').parents('.checkbox-btn').addClass('disabled');
            }
        });

        return {
            getIndustry: function(){
                var arr = [];
                $userIndustry.filter(":checked").each(function(i, n){
                    arr.push($(n).val());
                });

                return arr;
            }
        }
    };

    //初始化洲和国家并绑定事件
    var initCountry = function(){

        var $continent = $('.continent'),
            url = location.pathname + '?',
            currentCountry = $userCountry.text(),
            continent,countries,str = [],
            countryTpl = '<span><a title="#{countryNameFull}"#{cls}>#{countryName}</a></span>',
            countryAllTpl = [
                '<span class="continent-country #{isHide}" data-continent="#{continentName}">',
                '#{countryList}',
                '</span>'
            ].join(''),
            getCountry = function(countries){
                var arr = [];
                $.each(countries,function(i, e){
                	if(e!='全部'){
                        arr.push(util.template(countryTpl, {
                            countryName : e.length>5? e.substring(0,5)+'..':e,
                            countryNameFull : e,
                            cls : e == currentCountry ? ' class="current"' : ''
                        }))
                    }
                });
                return arr.join('');
            };

        $.each(country,function(i,e){
            continent = e.continent;
            countries = e.country;
            str.push(util.template(countryAllTpl, {
                continentName : continent,
                isHide : i==0?'':'none',
                countryList : getCountry(countries)
            }));
        });
        $('.continent-country').remove();
        $continent.append(str.join(''));
        $('.continent-name').on('mouseover',function(){
            var $this = $(this),
                $continentName = $this.siblings('.continent-name'),
                continent = $this.attr('data-continent'),
                $countries = $('.continent-country[data-continent="'+continent+'"]');
            $continentName.removeClass('current');
            $this.addClass('current');
            $countries.removeClass('none');
            $countries.siblings('.continent-country').addClass('none');
        });

        // 隐藏国家列表
        function hideArea($this){
            var $area = $this.find('.filter-name-area');

            $area.removeClass('on');
            $continent.addClass('none');
        }

        // 展开国家列表
        function showArea($this){
            var $area = $this.find('.filter-name-area');

            $area.addClass('on');
            $continent.removeClass('none');

            $continent.find('.continent-country:not(".none") a:first').focus();

            $(document).on('click',function(e){
                var target = $(e.target);
                if(target.prop('class').indexOf('filter-name') < 0 && target.closest('#userCountry').length < 1){
                    hideArea($this);
                }
                e.stopPropagation();
            });
        }

		$('.filter-name').each(function(i, n){

            $(n).on('click', function(e){
                if($(this).find('.filter-name-area').prop('class').indexOf('on')>-1){
                    hideArea($(this));
                }else{
                    showArea($(this));
                }
                e.stopPropagation();
                e.preventDefault();
	        }).on('keydown', function(e){
                    if(e.keyCode == 13){
                        if($(this).find('.filter-name-area').prop('class').indexOf('on')>-1){
                            hideArea($(this));
                        }else{
                            showArea($(this));
                        }
                        e.stopPropagation();
                        e.preventDefault();
                    }
                });

	    });
	    $('.continent-country span a').on('click',function(e){
	    	var $this = $(this);
            hideArea($this.closest('#userCountry'));
            $continent.find('.continent-country a').removeClass('current');
            $this.addClass('current');
    		$userCountry.text($this.prop('title')||$this.text());
            e.stopPropagation();
            e.preventDefault();
	    });

	}

    var saveUserInfo = function(sucCall){

        var userNameVal = $userName.val(),
            cost = $('input[name="cost"]:checked').val();

        //初始化修改姓名
        DetailAPI.verifyInput([{
            target: $userName,
            maxLen: 16,
            outTips: "姓名最长16个中文字符或32个英文、数字",
            nullTips: "姓名不能为空"
        },{ //初始化职业
            target: $userJob,
            maxLen: 16,
            outTips: "最长填写16个中文字符或32个英文、数字"
        }]);

        //姓名不能为空
        if(!$.trim(userNameVal).length){
            //去除空格
            $userName.val("");
            //重新验证
            $userName.focus().blur();
            return;
        }

        //清除textboxlist中的input的内容
        tagModule.addItem();

        //服务标签不能为空
        var $skillTag = $("#skillTags");
        if($skillTag.find(".textbox-item").length === 0){
            $skillTag.siblings(".tips").text("服务标签不能为空");
            return;
        }

        //判断页面是否有错
    	if ($.trim($(".tips").not(".introduction-tips").text()).length > 0) {
    		$.alert('您所填写的内容不正确。');
    		return;
    	};

        var userInfoData = {
            'name': getUserName(),
            'gender': getUserGender(),
            'country': getUserCountry(),
            'timeZone': getTimeZome(),
            'job': getUserJob(),
            'personalInfo': getUserIntro(),
            'industryIds': industry.getIndustry(),
            'skillsTags': tagModule.getItemsVal(),
            'payType': cost == '-1' ? '1' : '0',
            'expenses': cost == '-1' ? '0' : cost
        }

        var api = $.tips('loading');
        util.setAjax(inter.getApiUrl().saveUserInfoUrl, userInfoData, function(json){
            api.close();
            if(json.status){
                $('#user-name, .header-user-name, #J-base-top-name').text( userInfoData.name );
                if(sucCall)sucCall();
                DetailAPI.showSaveSuccess();
                oldInfoData = getInfoData();
            }else{
                $.alert(json.error);
                $('.save-ok').addClass('none');
            }
        },function(){
            api.close();
            $.alert('服务器繁忙，请稍后再试！');
        });
    }
    //初始化个人简介事件
    //判断个人说明的字符串长度
    var initIntroduction = function(){

    	var introduction = $userIntro;

        introduction.on("blur keyup", function(){
            var textVal = introduction.val(),
                textLen = textVal.length,
                maxLen = 500,
                errBox = introduction.siblings(".tips"),
                hasIntroTips = errBox.hasClass("introduction-tips"),
                realNum = charFormat.getCharANSILen(textVal),
                less = maxLen - textLen,
                more = textLen - maxLen;

            if (textLen <= maxLen && textLen !== 0) {
                if (!hasIntroTips) {
                    errBox.addClass("introduction-tips");
                }
                errBox.html('最多' + maxLen + '字符，还可以输入<span class="num">' + less + '</span>字符');
            }else if (textLen > maxLen) {
                if (hasIntroTips) {
                    errBox.removeClass("introduction-tips");
                }
                errBox.html('最多' + maxLen + '字符，现已经超过<span class="num">' + more + '</span>字符');
            }else{
                errBox.html('');
            }
        });
    }

    //初始化时区
    var initTimeZone = function(){
    	var timeZoneSelect = $userTimeZome,
	    	timeZone = new Date().getTimezoneOffset(),
	    	userZone = $('#userTimeZome');


	    if(!timeZoneSelect.attr('data-value')){
	        var timeZoneValue = timeZoneSelect.find('option[offset="'+ (-timeZone) +'"]').eq(0).prop('value');
	        timeZoneSelect.val(timeZoneValue);
            oldInfoData.timeZone = timeZoneValue;
	    }

        if(userZone.text().length<1){
            userZone.text(timeZoneSelect.find('option:selected').text());
        }
    }

	init();

    util.onBeforeBomUnload(function(){
        var nowInfoData = getInfoData();

        if( $.param(nowInfoData) != $.param(oldInfoData) ){
            return '您的个人信息有修改还未保存。';
        }
    });

    $('.cost-detail').poshytip({
        content : '按照咨询时长计费。 <a href="/questions#question_trade" class="tips-link" target="_blank">查看详细规则</a>',
        alignTo: 'target',
        alignX: 'center',
        showAniDuration: 0,
        offsetY: 7
    });

    $('.cost-discuss').poshytip({
        content : '面议：类似按次付费，具体价格由双方协商决定。 <a href="/questions#question_trade" class="tips-link" target="_blank">查看详细规则</a>',
        alignTo: 'target',
        alignX: 'center',
        showAniDuration: 0,
        offsetY: 7
    });
});