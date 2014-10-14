/**
 * @description:  resume -- 个人教育经历
 * @author: Young Foo (Young.foo@helome.com)
 * @data: 2014/4/21
 */

require([
    'common/interface',
	'common/util',
	'module/checkForm',
    'module/pageCommon',
    'module/prettifyForm',
    'module/collapsePanel',
    'module/charFormat',
    'module/userDetail'
], function(inter, util, check, common, prettifyForm, collapse, charFormat, DetailAPI){
	//定义模板
    var eduExpTpl = [
        '<div class="user-exp clearfix">',

            '<div class="user-exp-content">',
            '<div class="user-exp-index"><span class="icon">#{index}</span></div>',
            '<div class="user-edu-main"',
                ' data-eduYear="#{eduYear}"',
                ' data-eduYearEnd="#{eduYearEnd}"',
                ' data-eduInfo="#{eduInfo}"',
                ' data-eduUnit="#{eduUnit}"',
                ' data-professional="#{professional}"',
                ' data-degree="#{degree}">',
                    '<div class="school exp-in">#{eduUnit}</div>',
                    '<div class="user-exp-text clearfix">',
                        '<span class="prof">#{professional}</span>',
                        '<span class="degree">#{degree}</span>',
                    '</div>',
                    '<div class="edu-time">#{eduYear}年-#{eduYearEnd}年</div>',
                    '<div class="user-exp-info">',
                        '<span class="info">#{eduInfo}</span>',
                    '</div>',
                '</div>',
                '<ul>',
                	'<li><a href="javascript:;" class="btn-edit"><span class="icon icon-edit"></span></a></li>',
                	'<li><a href="javascript:;" class="btn-delete"><span class="icon icon-delete"></span></a></li>',
                '</ul>',
				'<div class="collapse-pannel hide">',
					'<div class="user-info-block clearfix pr z5">',
						'<div class="info-key">',
							'<label><em class="red">*</em>在校时间：</label>',
						'</div>',
						'<div class="info-value">',
							'<select class="select-year year-start"></select>',
							'<span class="year">年&nbsp;-</span>',
							'<select class="select-year year-end"></select>',
                            '<span class="year">年</span>',
                            '<span class="tips tips-right edu-date-tips"></span>',
						'</div>',
					'</div>',
					'<div class="user-info-block clearfix">',
						'<div class="info-key">',
							'<label><em class="red">*</em>学校名称：</label>',
						'</div>',
						'<div class="info-value">',
							'<input type="text" class="txt txt-long txt-verify input-school" maxlength="64" placeholder="学校或单位名称" >',
							'<span class="tips tips-right"></span>',
						'</div>',
					'</div>',
                    '<div class="user-info-block clearfix">',
                        '<div class="info-key">',
                            '<label>所学专业：</label>',
                        '</div>',
                        '<div class="info-value">',
                            '<input type="text" class="txt txt-long txt-mid txt-verify input-prof" maxlength="64"  placeholder="所学专业">',
                            '<span class="tips tips-right"></span>',
                        '</div>',
                    '</div>',
                    '<div class="user-info-block clearfix">',
                       '<div class="info-key">',
                           '<label>获得学位：</label>',
                       '</div>',
                       '<div class="info-value">',
                           '<select class="edu-degree">',
                               '<option value="">请选择</option>',
                               '<option value="初中">初中</option>',
                               '<option value="高中">高中</option>',
                               '<option value="中技">中技</option>',
                               '<option value="中专">中专</option>',
                               '<option value="大专">大专</option>',
                               '<option value="本科">本科</option>',
                               '<option value="MBA">MBA</option>',
                               '<option value="硕士">硕士</option>',
                               '<option value="博士">博士</option>',
                               '<option value="其他">其他</option>',
                           '</select>',
                           '<span class="tips tips-right"></span>',
                       '</div>',
                    '</div>',
                    '<div class="user-info-block clearfix">',
                        '<div class="info-key">',
                            '<label>教育经历：</label>',
                        '</div>',
                        '<div class="info-value">',
                            '<textarea class="textarea text-eduInfo" maxlength="500" ',
                                'placeholder="请提供教育经历相关的细节，以便用户纤细了解您的背景。"></textarea>',
                            '<span class="tips tips-right"></span>',
                        '</div>',
                    '</div>',
					'<div class="user-info-block clearfix">',
						'<div class="info-key">&nbsp;</div>',
						'<div class="info-value">',
							'<input type="button" class="btn-default btn-green btn-lg btn-save" value="完成编辑">&nbsp',
							'<input type="button" class="btn-default btn-white btn-lg collapse-close-btn" value="取消">',
						'</div>',
					'</div>',
				'</div>',
            '</div>',
        '</div>'
        ].join(''),

        eduMainTpl = [
            '<div class="user-edu-main"',
            ' data-eduYear="#{eduYear}"',
            ' data-eduYearEnd="#{eduYearEnd}"',
            ' data-eduInfo="#{eduInfo}"',
            ' data-eduUnit="#{eduUnit}"',
            ' data-professional="#{professional}"',
            ' data-degree="#{degree}">',
                '<div class="school exp-in">#{eduUnit}</div>',
                '<div class="user-exp-text clearfix">',
                    '<span class="prof">#{professional}</span>',
                    '<span class="degree">#{degree}</span>',
                '</div>',
                '<div class="edu-time">#{eduYear}年-#{eduYearEnd}年</div>',
                '<div class="user-exp-info">',
                    '<span class="info">#{eduInfo}</span>',
                '</div>',
            '</div>'
        ].join('');

    //动态取得所有时间，以便排序
    var getedutime = function(){
        var tempArr = [],
            tempdata = $('#userEduExp .user-edu-main'),
            templeng = tempdata.length;
        for(var i=0; i < templeng; i++){
            var tempi = tempdata.eq(i)
            tempArr.push(tempi.attr('data-eduyear') + ',01,' + tempi.attr('data-eduYearEnd')+',01');
        }
        return tempArr;
    }

    //保存数据
    var saveUserInfo = function(successInfo){

        //生成数据
        var userInfoData = {
            'userId': util.location('userId'),
            'educationExp': getEduExp($('#userEduExp'))
        }

        //tips
        var api = $.tips('loading');

        //提交数据
        util.setAjax(inter.getApiUrl().saveResume, userInfoData, function(json){
            api.close();
            if(json.status){
                
                $('.collapse-pannel').hide();
                $('.collapse-btn-box').show();
                $('.user-delete').remove();
                $('.collapse-pannel input[type="text"]').val('');
                DetailAPI.showSaveSuccess(successInfo);
                DetailAPI.refreshIndex();
            }else{
                $.alert(json.error);
                $('.save-ok').addClass('none');
            }
        },function(){
            api.close();
            $.alert('服务器繁忙，请稍后再试！');
        });

    }

    var getEduExp = function(exp){
        if(exp && exp !== ''){
            exp = exp.clone();
            var jobExp = exp.find('.user-exp:not(.user-delete)'),
                arrExp = [];
            jobExp.each(function(i, n){
                var $main = $(n).find('.user-edu-main');
                arrExp.push({
                    year: $main.attr('data-eduYear'),
                    yearEnd: $main.attr('data-eduYearEnd'),
                    school: $main.attr('data-eduUnit'),
                    major: $main.attr('data-professional'),
                    academicDegree: $main.attr('data-degree'),
                    eduInfo:$main.attr('data-eduInfo')
                });
            });
            exp = arrExp;
        }else{
            exp = [];
        }
        return exp;
    }

    var init = function(){

    	//初始化登陆和搜索
	    common.initLogin();

		//初始化折叠列表
		collapse.initCollapse(".edu-collapse");

         //教育信息
        initEduInfo();

        DetailAPI.openAddPanel();

        DetailAPI.closeTab();

    }

    //初始化教育经历说明输入验证事件
    var initIntroduction = function(){

        var introduction = $('.text-eduInfo');

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

            if (textLen < maxLen && textLen !== 0) {
                if (!hasIntroTips) {
                    errBox.addClass("introduction-tips");
                }
                errBox.html('还可以输入<span class="num">' + less + '</span>字符');
            }else if (textLen > maxLen) {
                if (hasIntroTips) {
                    errBox.removeClass("introduction-tips");
                }
                errBox.html('经历说明最多' + maxLen + '字符，现已经超过<span class="num">' + more + '</span>字符');
            }else{
                errBox.html('');
            }
        });
    }

    initIntroduction();

    var generateItemObj = function(obj){
        var $year = obj.year,
            $yserEnd = obj.yearEnd,
            $school = obj.school,
            $prof = obj.prof,
            $oriDegree = obj.degree,
            $eduDetail = obj.eduInfo;

        var year = $.trim($year.val()),
            yearEnd = $.trim($yserEnd.val()),
            eduInfo = $.trim($eduDetail.val()),
            school = $.trim(util.safeHTML($school.val())),
            prof = $.trim(util.safeHTML($prof.val())),
            degree = $.trim($oriDegree.val());


        if (school === "") {
            $school.val("");
            $school.trigger("keyup");
        };

        if (prof === "") {
            $prof.val("");
        };

        var newObj = {
            index : $('.user-exp-index').length + 1,
            eduYear: year,
            eduYearEnd: yearEnd,
            eduInfo: eduInfo,
            eduUnit: school,
            degree: degree,
            professional: prof
        };

        return newObj;
    }

    //用来再次编辑模板 (添加逗号)
    var generateItemHtml = function(tmp, dataObj){
        var newHtml = $(util.template(tmp, dataObj));
        if(dataObj.professional && dataObj.professional != ''){
            newHtml.find('.prof');
        }
        if (dataObj.degree && dataObj.degree!= "") {
            newHtml.find('.degree');
        };
        if(!dataObj.eduInfo || dataObj.eduInfo ==""){
            newHtml.find('.user-exp-info').remove();
        }
        return newHtml;
    }

    //初始化添加教育信息
    var initEduInfo = function(){
        //初始化页面
        DetailAPI.initVerify();

        //初始化select年月表单
        DetailAPI.initYearSelect();

        //错误信息
        var errStr = {
            profName: "不能超过32个中文或64个英文字符",
            profNameNull: "",
            schoolName: "不能超过32个中文或64个英文字符",
            schoolNameNull: "不能留空",
            timeErr: "起始时间不能大于结束时间"
        }

        var $schoolInput = $("#eduSchool"),
            $profInput = $("#eduProfessional"),
            $eduDetailA = $("#eduInfo"),
            $eduYear = $("#eduYear"),
            $eduYearEnd = $("#eduYearEnd"),
            $eduDegree = $("#eduDegree"),
            $eduCollapse = $(".edu-collapse"), //添加面板
            $eduInfoBox = $("#userEduExp"),
            $eduInfo = $eduInfoBox.find(".user-exp:not(.user-delete)"),

            //删除按钮
            $eduInfoDelete = $eduInfo.find(".btn-delete"),
            //编辑按钮
            $eduInfoEdit = $eduInfo.find(".btn-edit"),
            //添加按钮(即新创建一项的保存按钮)
            $eduInfoAdd = $eduCollapse.find(".btn-addEduExp"),
            //取消按钮
            $eduInfoCancel = $eduCollapse.find(".collapse-close-btn");

        //初始化form表单
        var select = prettifyForm.initSelect({target:$eduCollapse});

        DetailAPI.checkDateAll({
            year: $eduYear, yearEnd: $eduYearEnd,
            editSelect: select,
            clear: true,
            err : errStr.timeErr,
            tips: $eduCollapse.find('.edu-date-tips')
        });

        var handleItemEvent = function($btnEdit, $btnDelete){
            $btnDelete.off("click");
            $btnDelete.on('click', function(){
                delEduItem($(this));
            });

            $btnEdit.off("click");
            $btnEdit.on("click", function(){
                var itemsPanel = $(this).closest(".user-exp").siblings(".user-exp");
                itemsPanel.each(function(i, n){
                    if (!$(n).find(".collapse-pannel").is(":hidden")) {
                        collapse.hidePannel($(n));
                    };
                });
                editItem($(this));
            });
        }

        var delEduItem = function(obj){
            var remindStr = '确定删除该项吗',
                //删除成功
                fnYes = function(){
                    obj.closest(".user-exp").addClass('user-delete');
                    var notDelete = $eduInfoBox.find(".user-exp:not(.user-delete)"),
                        len = notDelete.length;
                    if(len < 1){
                        $eduInfoBox.append('<div class="no-exp"><em class="icon icon-no-exp"></em><br/>请添加您的教育信息！</div>');
                    };
                    saveUserInfo("删除成功");
                },
                //删除失败
                fnNo = function(){         return;         };
            $.confirm(remindStr,fnYes,fnNo);
        };

        var editItem = function(elem){
        	var $item = elem.closest(".user-exp:not(.user-delete)"),
                $itemMain = $item.find(".user-edu-main"),
                thisIndex = $item.find('.user-exp-index').text(),

        		data = {
        			year: $itemMain.attr("data-eduYear"),
                    yearEnd : $itemMain.attr("data-eduYearEnd"),
        			eduInfo: $itemMain.attr("data-eduInfo"),
        			school: $itemMain.attr("data-eduUnit"),
        			prof: $itemMain.attr("data-professional"),
        			degree: $itemMain.attr("data-degree")
        		},
        		editModule = $item.find(".collapse-pannel"),
        		$oriYear = editModule.find(".year-start"),
        		$oriYearEnd = editModule.find(".year-end"),
        		$oriDegree = editModule.find(".edu-degree"),
                $eduDetail = editModule.find(".text-eduInfo"),
        		$school = editModule.find(".input-school"),
        		$prof = editModule.find(".input-prof"),
                $errTips = editModule.find(".edu-date-tips"),
        		
        		$btnSave = $item.find(".btn-save"),
        		$btnCancel = $item.find(".collapse-close-btn");

            /**这些工作是为了新添加内容所做的             * */
                //初始化页面
            DetailAPI.initVerify();
            //初始化select年月表单
            DetailAPI.initYearSelect();
            //美化表单
            var selectEdit = prettifyForm.initSelect({target:$item}),
                $year = $oriYear.next(".select"),
                $yearEnd = $oriYearEnd.next(".select"),
                $degree = $oriDegree.next(".select");

        	//填充数据
            selectEdit.setSelectVal($year, data.year);
            selectEdit.setSelectVal($yearEnd, data.yearEnd);
            selectEdit.setSelectVal($degree, data.degree);
            $school.val(data.school);
            $prof.val(data.prof);
            $eduDetail.val(data.eduInfo);


        	//展开pannel
        	if (!editModule.is(":hidden")) {
        		collapse.hidePannel($item);
        		return;
        	};

        	collapse.showPannel($item);
        	collapse.hidePannel($eduCollapse);

            $school.on("focus", function(){
                DetailAPI.clearInputState($(this));
            });

            $prof.on("focus", function(){
                DetailAPI.clearInputState($(this));
            });

            $school.on("blur", function(){
                DetailAPI.verifyInput([{
                    target: $school,
                    maxLen: 32,
                    outTips: errStr.schoolName,
                    nullTips: errStr.schoolNameNull
                }]);
            });

            $prof.on("blur", function(){
                DetailAPI.verifyInput([{
                    target: $prof,
                    maxLen: 32,
                    outTips: errStr.profName,
                    nullTips: errStr.profNameNull,
                    required: true
                }]);
            });

	    	$btnCancel.off("click");
            $btnCancel.on("click", function(){
                DetailAPI.resetItem([$school, $prof, $eduDetail], $item);
	        });

            /**
              * 保存修改教育数据
              */
	        $btnSave.off();
	        $btnSave.on("click", function(){
                //验证表单
                DetailAPI.verifyInput([{
                    target: $school,
                    maxLen: 32,
                    outTips: errStr.schoolName,
                    nullTips: errStr.schoolNameNull
                },{
                    target: $prof,
                    maxLen: 32,
                    outTips: errStr.profName,
                    nullTips: errStr.profNameNull,
                    required: true
                }]);

                //生成需要的数据
                var newObj = generateItemObj({
                    year: $oriYear,
                    yearEnd: $oriYearEnd,
                    degree: $oriDegree,
                    school: $school,
                    prof: $prof,
                    eduInfo:$eduDetail
                });

                //检查是否有错误
                if(DetailAPI.checkEditErr(editModule)) { return };

                //生成模板
	            var newHtml = generateItemHtml(eduMainTpl, newObj);

                $eduInfo = $eduInfoBox.find(".user-exp:not(.user-delete)");
                
                $itemMain.replaceWith(newHtml);
                var  startTimeArr = getedutime();
                startTimeArr.splice(thisIndex - 1,1);
                var currIndex = DetailAPI.getIndex(startTimeArr,newObj.eduYear+',01,' + newObj.eduYearEnd+',01');
                if(currIndex == 0 ){
                    $item.parent('.user-exp-box').prepend($item);
                }else{
                    if(thisIndex - 1 !== currIndex){
                        if(thisIndex - 1 < currIndex){
                            $eduInfo.eq(currIndex).length === 1 ? $eduInfo.eq(currIndex ).after($item) : $eduInfoBox.append($item);
                        }else{
                            $eduInfo.eq(currIndex - 1).length === 1 ? $eduInfo.eq(currIndex - 1).after($item) : $eduInfoBox.append($item);
                        }
                    }
                }
                saveUserInfo();
        		//收拢pannel
        		collapse.hidePannel($item);
        	});

            DetailAPI.checkDateAll({
                year: $oriYear,
                yearEnd: $oriYearEnd,
                editSelect: selectEdit,
                clear: false,
                err : errStr.timeErr,
                tips:$errTips
            });
        }


        //为编辑删除按钮 绑定事件
        handleItemEvent($eduInfoEdit, $eduInfoDelete);

        //取消编辑，并重置
        $eduInfoCancel.on("click", function(){
            //清除数据
            DetailAPI.resetItem([$schoolInput, $profInput, $eduDetailA], $eduCollapse);
            select.setSelectVal($eduDegree.next(".select"),'');
            select.setSelectVal($eduYear.next(".select"),'');
            select.setSelectVal($eduYearEnd.next(".select"),'');
        });

        //添加教育信息
        $eduInfoAdd.on('click', function(){

            DetailAPI.verifyInput([{
                target: $schoolInput,
                maxLen: 32,
                outTips: errStr.schoolName,
                nullTips: errStr.schoolNameNull
            },{
                target: $profInput,
                maxLen: 32,
                outTips: errStr.profName,
                nullTips: errStr.profNameNull,
                required: true
            }]);

            var newObj = generateItemObj({
                year: $eduYear,
                yearEnd: $eduYearEnd,
                eduInfo : $eduDetailA,
                degree: $eduDegree,
                school: $schoolInput,
                prof: $profInput
            });

            //检查是否有错误
            if(DetailAPI.checkEditErr($eduCollapse)) { return };

            var newHtml = generateItemHtml(eduExpTpl, newObj);


            var eduInfo = $eduInfoBox.find(".user-exp:not(.user-delete)");
            
            //添加教育信息
            if(eduInfo.length < 1){
                $eduInfoBox.find(".user-exp-box").html(newHtml);
                $(".no-exp").remove();
            }else{
                var  startTimeArr = getedutime();
                var rightIndex = DetailAPI.getIndex(startTimeArr,newObj.eduYear+',01,'+newObj.eduYearEnd + ',01');
                if(rightIndex == 0 ){
                    $eduInfoBox.find(".user-exp-box").prepend(newHtml);
                }else{
                    eduInfo.eq(rightIndex-1).after(newHtml);
                }
            }

            saveUserInfo();

            //为新数据绑定删除和编辑功能
            eduInfo = $eduInfoBox.find(".user-exp:not(.user-delete)");
            var $btnInfoDelete = eduInfo.find(".btn-delete"),
            	$btnInfoEdit = eduInfo.find(".btn-edit");

            handleItemEvent($btnInfoEdit, $btnInfoDelete);

            //清除数据
            DetailAPI.resetItem([$schoolInput, $profInput, $eduDetailA], $eduCollapse);
            select.setSelectVal($eduDegree.next(".select"),'');
            select.setSelectVal($eduYear.next(".select"),'');
            select.setSelectVal($eduYearEnd.next(".select"),'');
        });

        $schoolInput.on("focus", function(){
            DetailAPI.clearInputState($(this));
        });

        $profInput.on("focus", function(){
            DetailAPI.clearInputState($(this));
        });

        $schoolInput.on("blur", function(){
            DetailAPI.verifyInput([{
                target: $schoolInput,
                maxLen: 32,
                outTips: errStr.schoolName,
                nullTips: errStr.schoolNameNull
            }]);
        });

        $profInput.on("blur", function(){
            DetailAPI.verifyInput([{
                target: $profInput,
                maxLen: 32,
                outTips: errStr.profName,
                nullTips: errStr.profNameNull,
                required: true
            }]);
        });
    }

	init();
});