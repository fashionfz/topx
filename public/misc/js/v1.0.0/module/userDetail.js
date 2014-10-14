/**
 * @description: 个人信息
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 * @data: 2013/12/29
 */

define('module/userDetail', [
	'common/util',
	'module/checkForm',
    'module/prettifyForm',
    'module/collapsePanel',
    'module/charFormat'
], function(util, check, prettifyForm, collapse, charFormat){



    /**
     * 截取n个中文或n*2个英文
     * {
     * str:"需要截取的字符串",
     * len:10 //长度
     * }
     */
    var cutString2 = function(o){
        var i = 0,
            str_cont= 0,
            str = o.str || '',
            len = o.len*2 || 0,
            str_array,
            ret_str='';
        if(!str || !len)return;
        str_array = str.split('');
        for(;i < str_array.length;i++){
            str_cont +=  charFormat.getByteANSILen(str_array[i]);
            if(str_cont == len){
                str_array.length= i+1;
                ret_str = str_array.join('');
                break;
            }
            if(str_cont > len){
                str_array.length= i;
                ret_str = str_array.join('');
                break;
            }
        }
        return ret_str;
    }


    var initVerify = function(){

        var inputVerification = $(".txt-verify");

        inputVerification.each(function(i, e){
            if (!$(e).parent().hasClass("tag-container")) {
                $(e).wrap("<div class='tag-container'></div>");
            };
        });

    }

    //验证日期
    var checkDateAll = function(form){
        var $oriYear1 = form.year,
            $oriYear2 = form.yearEnd,
            $oriMonth1 = form.month,
            $oriMonth2 = form.monthEnd,
            currentYear = new Date().getFullYear(),
            currentMonth = new Date().getMonth(),
            clear = form.clear || false,
            $year1 = $oriYear1.next(".select"),
            $year2 = $oriYear2.next(".select"),
            $month1 = $oriMonth1 ? $oriMonth1.next(".select") : null,
            $month2 = $oriMonth2 ? $oriMonth2.next(".select") : null,
            $tipsYear = form.tips,
            errStr = form.err,
            errNow = form.timeNow,
            selectModule = prettifyForm.initSelect({target: form.editSelect}),
            //selectModule = form.editSelect,
            editModule = form.editModule,
            workToday = editModule ? editModule.find(".work-today") : null;
        //恢复默认时间
        if(clear){
            selectModule.setSelectVal($month1, 1);
            selectModule.setSelectVal($year1, currentYear);
            selectModule.setSelectVal($month2, 1);
            selectModule.setSelectVal($year2, currentYear);
            $year1.scrollTop(0);
            $year2.scrollTop(0);

            check.changeClass(true, $oriYear2, $tipsYear, '');
        }
        //隐藏，以后的日期；
        var hideFuture = function(checkedYear,$month){
            if(!$month || !$month.length)return;
            var $oriMonth = $month.prev("select");
            if(checkedYear && parseInt(checkedYear) == currentYear){
                $month.find("li").eq(currentMonth).nextAll().css({"display":"none"});
                if($oriMonth.val() > currentMonth+1){
                    selectModule.setSelectVal($month, 1);
                }
            }else{
                $month.find("li").css({"display":""});
            }
        }

        //验证日期
        var checkDate = function(){
            var startYear = $.trim($oriYear1.val()),
                endYear = $.trim($oriYear2.val()),
                startMonth = $.trim($oriMonth1 ? $oriMonth1.val() : '01'),
                endMonth = $.trim($oriMonth2 ? $oriMonth2.val() : '01'),
                date = new Date(),
                thisYear = date.getFullYear().toString(),
                thisMonth = (date.getMonth() + 1).toString();

            if (endYear === "至今") {
                endYear = thisYear;
                endMonth = thisMonth;
                isToday = true;
            }
            var startTime = new Date(startYear +'/'+ startMonth +'/01'),
                endTime = new Date(endYear +'/'+ endMonth +'/01');
            var nowTime = new Date();

            if(endTime > nowTime){
                check.changeClass(false, $oriYear2, $tipsYear, errNow);
                return;
            }

            //比较时间
            if(startTime > endTime){
                check.changeClass(false, $oriYear2, $tipsYear, errStr);
                return;
            }else{
                check.changeClass(true, $oriYear2, $tipsYear, '');
            }
        }

        //初始化隐藏多余的月
        hideFuture($oriYear1.val(),$month1);
        hideFuture($oriYear2.val(),$month2);
        //切换select进行表单验证
        selectModule.selectChange($year1, function(o){
            //限制 显示的时间小于 当前时间
            hideFuture($oriYear1.val(),$month1);
            checkDate();
        });
        //初始化后判断 是否 为“至今”,是 隐藏月，否显示月
        if($.trim($oriYear2.val()) === "至今"){
            workToday.addClass("hide");
        }else{
            if(workToday) workToday.removeClass("hide");
        }

        selectModule.selectChange($year2, function(o){
            hideFuture($oriYear2.val(),$month2);
            if (o.selectedText === "至今") {
                workToday.addClass("hide");
            }else{
                if(workToday) workToday.removeClass("hide");
            }
            checkDate();
        });

        if($month1){
            selectModule.selectChange($month1, function(o){
                checkDate();
            });
        }

        if($month2){
            selectModule.selectChange($month2, function(o){
                checkDate();
            });
        }

    }

    var verifyInput = function(objArr){
        for(var i = 0; i < objArr.length; i++){
            (function(n){
                var input = objArr[n].target,
                    maxLen = objArr[n].maxLen || 20,
                    outTips = objArr[n].outTips || "",
                    nullTips = objArr[n].nullTips || "",
                    //required 表单是否可以为空
                    required = objArr[n].required || false;
                    
                $(input).attr("maxlength",maxLen*2);

                var $this = $(input),
                    val = $.trim($this.val()) || '',
                    tips = $this.parent().siblings('.tips'),
                    isNullTips = (nullTips === ""),
                    len = charFormat.getCharANSILen(val),
                    chrLen = maxLen - len;
                if(val.length < 1 && !required){
                    check.changeClass(isNullTips, $this, tips, nullTips);
                }else if(chrLen < 0){
                    check.changeClass(false, $this, tips, outTips);
                    //$this.val(cutString2({str:$this.val(),len:maxLen}));
                }else{
                    check.changeClass(true, $this, tips, '');
                }

            })(i);
        }
    }

    var showSaveSuccess = function(successInfo){
    	var successTips = $('.save-ok');
        successInfo = successInfo || "保存成功";
        successTips.find('span').text(successInfo);
        successTips.removeClass('none');
        setTimeout(function(){
        	successTips.addClass('none');
        }, 2000);
        $('.collapse-show-btn').on('click',function(){
            successTips.addClass('none');
        })
    }

    //初始化下拉菜单，年
    var initYearSelect = function(target){
        var selectYears = (target && $(target).length) ?  ($(target).hasClass('select-year')? $(target): $(target).find(".select-year")) : $(".select-year"),
            date = new Date(),
            today = parseInt(date.getFullYear()),
            oldYear = 1950,
            HTML = "";

        for (var i = today; i >= oldYear; i--) {
        	if (i !== today) {
        		HTML += "<option value='"+ i +"'>" + i + "</option>";
        	}else{
        		HTML += "<option value='"+ i +"' selected>" + i + "</option>";
        	};
        };

        selectYears.each(function(i, e){
        	if ($(e).find("option").length === 1 || $(e).find("option").length < 1) {
        		$(e).append(HTML);
        	};
        });
    }
    //初始化下拉菜单，月
    var initMonthSelect = function(target){
        var selectMonths = (target && $(target).length) ?  ($(target).hasClass('select-month')? $(target): $(target).find(".select-month")) : $(".select-month"),
            monthNum = 12,
            HTML = "";
            for (var i = 1; i <= monthNum; i++) {
                HTML += "<option value='"+ i +"'>" + i + "</option>"
            };
        selectMonths.each(function(i, e){
        	if ($(e).find("option").length < 1) {
        		$(e).append(HTML);
        	};
        });
    }

    //页面保存交互
    var closeTab = function(){

        var onBeforeUnload = function(){

            var editState = false

            $(".collapse-pannel").each(function(i, n){
                if (!($(n).is(":hidden"))) {
                    editState = true;
                    return false;
                };
            });

            if (editState) {
                return '你的数据还没有保存';
            };
        }

        if ($.browser.msie) {
            window.document.body.onbeforeunload = onBeforeUnload;
        }else{
            window.onBeforeUnload = onBeforeUnload;
            $('body').attr('onbeforeunload', 'return onBeforeUnload();')
        };

    }

    //更新列表号码
	var refreshIndex = function(){
		var $index = $(".user-exp").find(".user-exp-index span");
		$index.each(function(i, n){
			var index = i + 1;
			$(n).text(index);
		});
	}

    //打开添加面板
    var openAddPanel = function(){
        var openBtn = $(".icon-add-info");
        openBtn.on("click", function(){
            var itemsPanel = $(".user-exp");
            itemsPanel.each(function(i, n){
                collapse.hidePannel($(n));
            });
        });
    }

    //计算 str 在数组 arr中的位置 (添加workinfo 后用来计算位置)
    var getIndex = function(arr,str){
        var formatA = function(stri){
            if(!stri)return;
            var strTemp = stri.replace(/至今/,'2999,12').replace(/\s/,'').replace(/[年月]/g,',').replace(/,{2,}/g,',').replace(/\b(\d{1})\b/g,'0$1'),
                strTempB = strTemp.substring(0,7),
                strTempE = '';
            if(strTemp.length>8){
                strTempE = strTemp.substring(8,15);
                return strTempE>strTempB ? strTempE+','+strTempB : strTempB+','+strTempE;
            }else{
                return strTempB;
            }
        }
        arr = arr|| [];
        var index = arr.length;
        str = formatA(str);
        for(var i =0; i< index;i++){
            arr[i] = formatA(arr[i]);
        }
        for(var i =0; i< index;i++){
            if( arr[i] <= str ){
                index = i;
                break;
            }
        }
        return  index;
    }

    //检查编辑错误
    var checkEditErr = function(obj){
        if ($.trim(obj.find(".tips:not(.introduction-tips)").text()).length > 0) {
            $.alert('您所填写的内容不正确。');
            return true;
        }else{
            return false;
        }
    }


    var resetItem = function($inputArr, $item){
        $.each($inputArr, function(i, e){
            var $this = $(e),
                inputTips = $this.parent().siblings(".tips");
            check.changeClass(true, $this, inputTips, '');
            $this.parent().find(".right-tip").remove();
            $this.val("");
        });

        if ($item) {
            collapse.hidePannel($item);
        };
    }


    var clearInputState = function($input){
        if (!$.isArray($input)) {
            $input = [$input];
        };
        $.each($input, function(i, e){
            var $this = e,
                inputTips = $this.parent().siblings(".tips");
            check.changeClass(true, $this, inputTips, '');
            $this.parent().find(".right-tip").remove();
        });
    }

    //初始化 教育经历说明 输入验证事件
    var initIntroduction = function($intro){

        var introduction = $intro;

        introduction.off("blur keyup");
        introduction.on("blur keyup", function(){
            var $this = $(this),
                textVal = $this.val(),
                textLen = textVal.length,
                maxLen = 500,
                errBox = $this.siblings(".tips"),
                hasIntroTips = errBox.hasClass("introduction-tips"),
                realNum = charFormat.getCharANSILen(textVal),
                less = maxLen - textLen,
                more = textLen - maxLen;

            if (textLen <= maxLen && textLen !== 0) {
                if (!hasIntroTips) {
                    errBox.addClass("introduction-tips");
                }
                errBox.html('最多输入' + maxLen + '个字符，还可以输入<span class="num">' + less + '</span>字符');
            }else if (textLen > maxLen) {
                if (hasIntroTips) {
                    errBox.removeClass("introduction-tips");
                }
                errBox.html('最多输入' + maxLen + '个字符，现已经超过<span class="num">' + more + '</span>字符');
            }else{
                errBox.html('');
            }
        });
    }
    //测试radio，请不要删除
    //测试radio，请不要删除
    //测试radio，请不要删除
	// var radio = prettifyForm.initRadio({
	// 	change:function(){
	// 		alert("radio all");
	// 	},
	// 	horizontalPos: "middle"
	// });

	// var manRadio = radio.get("#man");
	// var womanRadio = radio.get("#woman");

	// radio.radioChange(manRadio, function(){
	// 	alert("chose man");
	// });

	// radio.radioChange(womanRadio, function(){
	// 	alert("chose woman");
	// });

	// 测试select，请不要删除
	// 测试select，请不要删除
	// 测试select，请不要删除
	// 定义全局change的方法
	// var select = prettifyForm.initSelect({
	//    	change:function(){
	//    		alert("select all");
	//    	},
	//    	horizontalPos: "middle"
	// });
	// var countrySelect = select.get("#info-country-select");

	// select.selectChange(countrySelect, function(o){
	// 	alert("hi");
	// });

	// var time = select.get("#info-time-select");
	// select.selectChange(time, function(o){
	// 	alert("time");
	// });

	
    // var checkbox = prettifyForm.initCheckbox({
    // 	change:{
    // 		ok:function(){
    // 			alert("check");
    // 		},
    // 		cancel:function(){
    // 			alert("uncheck");
    // 		}
    // 	}
    // });
	
	return {
		verifyInput: verifyInput,
		closeTab: closeTab,
        showSaveSuccess: showSaveSuccess,
        initYearSelect: initYearSelect,
        initMonthSelect: initMonthSelect,
        openAddPanel: openAddPanel,
        refreshIndex: refreshIndex,
        getIndex: getIndex,
        initVerify: initVerify,
        checkEditErr: checkEditErr,
        resetItem: resetItem,
        clearInputState: clearInputState,
        checkDateAll:checkDateAll,
        initIntroduction: initIntroduction
	}
});