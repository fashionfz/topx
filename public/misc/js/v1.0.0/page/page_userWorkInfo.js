/**
 * @description: 个人职业生涯
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
    'module/userDetail',
    'module/enResume'
], function(inter, util, check, common, prettifyForm, collapse, charFormat, DetailAPI, enResume){

	//定义模板
    // 模板在添加前先判断是否存在；存在则删除
    var workExpTpl = [
            '<div  class="user-exp clearfix">',

				'<div class="user-exp-content">',
                    '<div class="clearfix">',
                        '<div class="user-exp-index"><span class="icon">#{index}</span></div>',
    	                '<div class="user-exp-main"',
                            'data-company = "#{company}"',
                            'data-workYear = "#{startYear}"',
                            'data-workMonth = "#{startMonth}"',
                            'data-workYearEnd = "#{endYear}"',
                            'data-workMonthEnd = "#{endMonth}"',
                            'data-duty = "#{duty}">',
                            '<div class="duty">#{duty}</div>',
                            '<div class="company">#{company}</div>',
                            '<div class="job-time">',
                                '#{fromTime} - #{toTime}',
                            '</div>',
                            '<div class="user-exp-info">',
                                '<span class="info">#{workInfo}</span>',
                            '</div>',
    	                '</div>',
    	                '<ul class="user-exp-btnbox">',
    	                	'<li><a href="javascript:;" class="btn-edit"><em class="icon icon-edit"></em></a></li>',
    	                	'<li><a href="javascript:;" class="btn-delete"><em class="icon icon-delete"></em></a></li>',
    	                '</ul>',
                    '</div>',
					'<div class="collapse-pannel hide">',
						'<div class="user-info-block clearfix pr z5">',
							'<div class="info-key">',
								'<label><span class="red">*</span>在职时间：</label>',
							'</div>',
							'<div class="info-value">',
								'<select class="select-year year-start" id="#{index}">',
								'</select>',
								'<span>&nbsp;年&nbsp;</span>',
	                            '<select class="select-month month-start"></select>',
	                            '<span>&nbsp;月&nbsp;-&nbsp;</span>',
								'<select class="select-year year-end" >',
									'<option>至今</option>',
								'</select>',
								'<span class="work-today">',
									'<span>&nbsp;年&nbsp;</span>',
	                                '<select class="select-month month-end"></select>',
	                                '<span>&nbsp;月</span>',
	                        	'</span>',
								'<span class="tips tips-right work-date-tips"></span>',
							'</div>',
						'</div>',
						'<div class="user-info-block clearfix">',
							'<div class="info-key">',
								'<label><span class="red">*</span>公司名称：</label>',
							'</div>',
							'<div class="info-value">',
								'<input type="text" class="txt txt-long txt-verify input-company" maxlength="64" placeholder="所在的公司或单位名称">',
								'<span class="tips tips-right"></span>',
							'</div>',
						'</div>',
						'<div class="user-info-block clearfix">',
							'<div class="info-key">',
								'<label><span class="red">*</span>职位名称：</label>',
							'</div>',
							'<div class="info-value">',
								'<input type="text" class="txt txt-long txt-verify input-work" maxlength="64" placeholder="从事的职业">',
								'<span class="tips tips-right"></span>',
							'</div>',
						'</div>',
                        '<div class="user-info-block clearfix">',
                            '<div class="info-key">',
                                '<label>职位描述：</label>',
                            '</div>',
                            '<div class="info-value">',
                                '<textarea class="textarea text-workInfo" maxlength="500" ',
                                    'placeholder="请提供与职位相关的细节，以便用户快速了解职位信息。"></textarea>',
                                '<span class="tips tips-right"></span>',
                            '</div>',
                        '</div>',
						'<div class="user-info-block clearfix">',
							'<div class="info-key">&nbsp;</div>',
							'<div class="info-value">',
								'<input type="button" class="btn-default btn-green btn-lg btn-save" value="完成编辑"/>&nbsp',
								'<input type="button" class="btn-default btn-white btn-lg collapse-close-btn" value="取消"/>',
							'</div>',
						'</div>',
					'</div>',
				'</div>',
            '</div>'
        ].join(''),

        workMainTpl = [
            '<div class="user-exp-main"',
                'data-company = "#{company}"',
                'data-workYear = "#{startYear}"',
                'data-workMonth = "#{startMonth}"',
                'data-workYearEnd = "#{endYear}"',
                'data-workMonthEnd = "#{endMonth}"',
                'data-duty = "#{duty}">',
                '<div class="duty">#{duty}</div>',
                '<div class="company">#{company}</div>',
                '<div class="job-time">',
                    '#{fromTime} - #{toTime}',
                '</div>',
                '<div class="user-exp-info">',
                    '<span class="info">#{workInfo}</span>',
                '</div>',
            '</div>'
        ].join('');


    //获取工作数据
    var getJobExp = function(exp){
        if(exp && exp !== ''){
            var jobExp = exp.find('.user-exp:not(.user-delete)'),
                arrExp = [];
            jobExp.each(function(i, n){
                var userExp = $(n).find('.user-exp-main'),
                    workInfo = userExp.find('.info').text();
                arrExp.push({
                    beginYear : userExp.attr('data-workYear'),
                    endYear : userExp.attr('data-workYearEnd'),
                    beginMonth : userExp.attr('data-workMonth'),
                    endMonth : userExp.attr('data-workMonthEnd'),
                    company : userExp.attr('data-company'),
                    duty : userExp.attr('data-duty'),
                    workInfo : $.trim(workInfo)
                })
            });
            exp = arrExp;
        }else{
            exp = [];
        }
        return exp;
    }

    //保存信息
	var saveUserInfo = function(successInfo){

	    //生成数据
	    var userInfoData = {
	        'jobExp': getJobExp($('#userWorkExp'))
	    }
	    //tips
	    var api = $.tips('loading');

	    //提交数据
	    util.setAjax(inter.getApiUrl().saveUserInfoUrl, userInfoData, function(json){
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

    //生成单条数据
    var generateItemObj = function(obj){

        var $year1 = obj.year1,
            $year2 = obj.year2,
            $month1 = obj.month1,
            $month2 = obj.month2,
            $company = obj.company,
            $detail = obj.workInfo,
            $work = obj.duty;

        var startYear = $.trim($year1.val()),
            endYear = $.trim($year2.val()),
            startMonth = $.trim($month1.val()),
            endMonth = $.trim($month2.val()),
            company = $.trim(util.safeHTML($company.val())),
            workIn = $.trim(util.safeHTML($work.val())),
            startTime = new Date(startYear +'/'+ startMonth +'/01'),
            endTime = new Date(endYear +'/'+ endMonth +'/01'),
            isToday = false,
            workDetail = $.trim(util.safeHTML($detail.val()));

        //没有填写内容的时候验证！
        if (workIn === "") {
            $work.val("");
            $work.trigger("keyup");
        };

        if (company === "") {
            $company.val("");
            $company.trigger("keyup");
        };

        if (endYear === "至今") {
            isToday = true;
        };

        var newObj = {
            index: $('.user-exp-index').length + 1,
            fromTime: util.dateFormat(startTime, 'yyyy年M月'),
            toTime: isToday ? "至今" : util.dateFormat(endTime, 'yyyy年M月'),
            company: company,
            duty: workIn,
            startYear: startYear,
            startMonth: parseInt(startMonth, 10) || 1,
            endYear: isToday ? "至今" : endYear,
            endMonth: isToday ? "" : (parseInt(endMonth, 10) || 1),
            workInfo:workDetail
        }
        return newObj;
    }

    //根据数据编辑模板
    var generateItemHtml = function(tmplate, dataObj){
        var newHtml = $(util.template(tmplate, dataObj));
        if(!dataObj.workInfo || dataObj.workInfo ==""){
            newHtml.find('.user-exp-info').remove();
        }
        return newHtml;
    }
    //获取时间 以便排序
    var getStartTime = function(){
        var tempArr = [],
            $temp = $('#userWorkExp .user-exp-main');
        $temp.each(function(i,n){
            var $n = $(n),
                time = [
                    $n.attr('data-workYear'),
                    $n.attr('data-workMonth'),
                    $n.attr('data-workYearEnd'),
                    $n.attr('data-workMonthEnd')
                ].join(",") ;
            tempArr.push(time);
        })
        return tempArr;
    };



    //初始化添加职业信息事件
    var initWorkInfo = function(){

        //初始化页面
        DetailAPI.initVerify();

        //初始化select年月表单
        DetailAPI.initYearSelect();
        DetailAPI.initMonthSelect();

        var errStr = {
            companyName: "公司名称不能超过32个中文字符或64个英文字符",
            companyNameNull: "公司/单位名称不能留空",
            adressName: "职位名称不得超过32个中文字符或64个英文字符",
            adressNameNull: "职位名称不能留空",
            timeErr: "起始时间不能大于结束时间！"
        }

        var workCollapse = $('.work-collapse'),

            tipsYear = workCollapse.find('.work-date-tips'),

            selectYear1 = workCollapse.find("#work-year-select1"),
            selectYear2 = workCollapse.find("#work-year-select2"),
            selectMonth1 = workCollapse.find("#work-month-select1"),
            selectMonth2 = workCollapse.find("#work-month-select2"),

            companyInput = $('#workCompany'),
            workInInput = $('#workIn'),
            $detailInput = $('#workInfo'),

            workInfoBox = $('#userWorkExp'),
            workInfo = workInfoBox.find(".user-exp"),
            workInfoDelete = workInfo.find(".btn-delete"),
            workInfoEdit = workInfo.find(".btn-edit"),

            $btnSaveNew = workCollapse.find(".btn-addWorkExp"),
            $btnAdd =  workCollapse.find(".icon-add-info"),
            $btnCancel = workCollapse.find(".collapse-close-btn"),

            $dutyShow = $(".user-exp .user-exp-main").find(".duty:not(.banner-box)"),
            select = prettifyForm.initSelect({ target: workCollapse});


        $(".icon-add-info").on("click", function(){
            DetailAPI.clearInputState(companyInput);
            DetailAPI.clearInputState(workInInput);
            DetailAPI.checkDateAll({
                year: selectYear1, yearEnd: selectYear2,
                month: selectMonth1, monthEnd:selectMonth2,
                tips: tipsYear,
                err: errStr.timeErr,
                clear: true,
                editSelect: $(".work-collapse"),
                editModule: workCollapse
            });
        });


        //删除该条数据
        var delWorkItem = function(obj){
            var remindStr = '确定删除该项吗',

                fnYes = function(){
                    obj.closest(".user-exp").addClass('user-delete');
                    var notDelete = workInfoBox.find(".user-exp:not(.user-delete)"),
                        len = notDelete.length;
                    if(len < 1){
                        workInfoBox.append('<div class="no-exp"><em class="icon icon-no-exp"></em><br/>请添加您的职业生涯！</div>');
                    }
                    saveUserInfo("删除成功");
                },
                fnNo = function(){
                    return;
                };

            $.confirm(remindStr,fnYes,fnNo);
        }

        //编辑该条数据
        var editItem = function(elem){

        	var item = elem.closest(".user-exp"),
        		itemMainBox = item.find(".user-exp-content"),
        		itemMain = item.find(".user-exp-main"),
                thisIndex = parseInt(item.find('.user-exp-index').text(),10),
        		data = {
        			startYear: itemMain.attr('data-workYear'),
        			startMonth: itemMain.attr('data-workMonth'),
        			endYear: itemMain.attr('data-workYearEnd'),
        			endMonth: itemMain.attr('data-workMonthEnd'),
        			company: itemMain.attr('data-company'),
        			work: itemMain.attr('data-duty'),
                    workInfo: $.trim(itemMain.find('.info').text())
        		},
        		
        		editModule = item.find(".collapse-pannel"),

        		$oriYear1 = editModule.find(".year-start"),
        		$oriMonth1 = editModule.find(".month-start"),
        		$oriYear2 = editModule.find(".year-end"),
        		$oriMonth2 = editModule.find(".month-end"),

        		//准备数据
        		$company = editModule.find(".input-company"),
        		$work = editModule.find(".input-work"),
                $detail = editModule.find(".text-workInfo"),

        		tipsYear = editModule.find(".work-date-tips"),
        		btnSave = item.find(".btn-save"),
        		btnCancel = item.find(".collapse-close-btn");
            //初始化页面
            DetailAPI.initVerify();
        	//初始化年月select
        	DetailAPI.initYearSelect();
        	DetailAPI.initMonthSelect();

        	//初始化select表单
        	var editSelect = prettifyForm.initSelect({target:editModule}),
                $year1 = $oriYear1.next(".select"),
                $month1 = $oriMonth1.next(".select"),
                $year2 = $oriYear2.next(".select"),
                $month2 = $oriMonth2.next(".select");

        	//回填数据
        	editSelect.setSelectVal($year1, data.startYear);
        	editSelect.setSelectVal($year2, data.endYear);
        	editSelect.setSelectVal($month1, data.startMonth);
        	editSelect.setSelectVal($month2, data.endMonth);

            //清除错误提示
            DetailAPI.clearInputState($company);
            DetailAPI.clearInputState($work);
            check.changeClass(true, $oriYear2, tipsYear, '');

            if(data.endYear === '至今'){
                editModule.find('.work-today').addClass('hide');
            }

        	$company.val(data.company);
        	$work.val(data.work);
            $detail.val(data.workInfo);

        	//展开pannel，如果pannel已经展开，将pannel关闭
        	if (!editModule.is(":hidden")) {
        		collapse.hidePannel(item);
        		return;
        	}

        	//展开pannel
        	collapse.showPannel(item);
        	//隐藏添加pannel
        	collapse.hidePannel(workCollapse);

            //日期验证
            DetailAPI.checkDateAll({
                year: $oriYear1, yearEnd: $oriYear2,
                month: $oriMonth1, monthEnd: $oriMonth2,
                tips: tipsYear,
                err: errStr.timeErr,
                editSelect: editModule,
                editModule: editModule
            });

            DetailAPI.clearInputState([$company, $work]);
            
            $company.on("blur", function(){
                DetailAPI.verifyInput([{
                    target: $company,
                    maxLen: 32,
                    outTips: errStr.companyName,
                    nullTips: errStr.companyNameNull
                }]);
            });

            $work.on("blur", function(){
                DetailAPI.verifyInput([{
                    target: $work,
                    maxLen: 32,
                    outTips: errStr.adressName,
                    nullTips: errStr.adressNameNull
                }]);
            });

        	btnCancel.off("click");
	        btnCancel.on("click", function(){
	        	tipsYear.html("");
                DetailAPI.resetItem([$company, $work, $detail], item);
	        });

	        btnSave.off("click");
	        btnSave.on("click", function(){

                DetailAPI.verifyInput([{
                    target: $company,
                    maxLen: 32,
                    outTips: errStr.companyName,
                    nullTips: errStr.companyNameNull
                }, {
                    target: $work,
                    maxLen: 32,
                    outTips: errStr.adressName,
                    nullTips: errStr.adressNameNull
                }]);

                var newObj = generateItemObj({
                    year1: $oriYear1,
                    year2: $oriYear2,
                    month1: $oriMonth1,
                    month2: $oriMonth2,
                    company: $company,
                    duty: $work,
                    workInfo:$detail
                });

                if(DetailAPI.checkEditErr(editModule)) { return };

                var newHtml = generateItemHtml(workMainTpl, newObj),
                    workInfo = workInfoBox.find(".user-exp:not(.user-delete)");

	            itemMain.replaceWith(newHtml);

                var startTimeArr = getStartTime();
                startTimeArr.splice(thisIndex-1,1);
                var currIndex = DetailAPI.getIndex(startTimeArr,newObj.fromTime+','+newObj.toTime);
                if(currIndex == 0 ){
                    workInfoBox.prepend(item);
                }else{
                    if(thisIndex-1 !== currIndex){
                        if(thisIndex-1 < currIndex){
                            workInfo.eq(currIndex).length ===1 ? workInfo.eq(currIndex).after(item) : workInfoBox.append(item) ;
                        }else{
                            workInfo.eq(currIndex-1).length ===1 ? workInfo.eq(currIndex-1).after(item) : workInfoBox.append(item) ;
                        }
                    }
                }
	            saveUserInfo();
        		//收拢pannel
        		collapse.hidePannel(item);
        	});
        }

        //删除一项
        workInfoDelete.on('click', function(){
            delWorkItem($(this));
        });

        //编辑一项
        workInfoEdit.on('click', function(e){

            var that = this;
        	var itemsPanel = $(that).closest(".user-exp").siblings(".user-exp");

            itemsPanel.each(function(i, n){
                if (!$(n).find(".collapse-pannel").is(":hidden")) {
                    collapse.hidePannel($(n));
                };
            });

            editItem($(that));
        });

        //取消编辑
        $btnCancel.on("click", function(){
            DetailAPI.resetItem([companyInput, workInInput, $detailInput]);
        });

        //添加一项
        $btnSaveNew.on('click', function(){

            //验证表单
            DetailAPI.verifyInput([{
                target: companyInput,
                maxLen: 32,
                outTips: errStr.companyName,
                nullTips: errStr.companyNameNull
            },{
                target: workInInput,
                maxLen: 32,
                outTips: errStr.adressName,
                nullTips: errStr.adressNameNull
            }]);

            var newObj = generateItemObj({
                year1: selectYear1,
                year2: selectYear2,
                month1: selectMonth1,
                month2: selectMonth2,
                company: companyInput,
                duty: workInInput,
                workInfo:$detailInput
            });


            if(DetailAPI.checkEditErr(workCollapse)) { return };

            var newHtml = generateItemHtml(workExpTpl, newObj),
                workInfo = workInfoBox.find(".user-exp:not(.user-delete)");

            //添加工作信息
            if(workInfo.length<1){
                workInfoBox.html(newHtml);
            }else{
                var startTimeArr = getStartTime();
                var rightIndex = DetailAPI.getIndex(startTimeArr,newObj.fromTime+','+newObj.toTime);
                if(rightIndex == 0 ){
                    workInfoBox.prepend(newHtml);
                }else{
                    workInfo.eq(rightIndex-1).after(newHtml);
                }

            }

            saveUserInfo();

            workInfo = workInfoBox.find(".user-exp:not(.user-delete)");
            var workInfoDelete = workInfo.find(".btn-delete"),
            	workInfoEdit = workInfo.find(".btn-edit");

            workInfoDelete.off("click");
            workInfoDelete.on('click', function(){
                delWorkItem($(this));
            });

            workInfoEdit.off("click");
            workInfoEdit.on("click", function(){
            	var itemsPanel = $(this).closest(".user-exp").siblings(".user-exp");
                itemsPanel.each(function(i, n){
                    if (!$(n).find(".collapse-pannel").is(":hidden")) {
                        collapse.hidePannel($(n));
                    };
                });
            	editItem($(this));
            });
            //清除数据
            DetailAPI.resetItem([companyInput, workInInput, $detailInput], workCollapse);
            select.setSelectVal(selectYear1.next(".select"), '');
            select.setSelectVal(selectYear2.next(".select"), (new Date().getFullYear()));
            select.setSelectVal(selectMonth1.next(".select"), '');
            select.setSelectVal(selectMonth2.next(".select"), '');
            $(".work-today").removeClass("hide");

            //为textarea添加事件
            DetailAPI.initIntroduction($('.text-workInfo'));
        });

        companyInput.on("focus", function(){
            DetailAPI.clearInputState($(this));
        });

        workInInput.on("focus", function(){
            DetailAPI.clearInputState($(this));
        });

        companyInput.on("blur", function(){
            DetailAPI.verifyInput([{
                target: companyInput,
                maxLen: 32,
                outTips: errStr.companyName,
                nullTips: errStr.companyNameNull
            }]);
        });

        workInInput.on("blur", function(){
            DetailAPI.verifyInput([{
                target: workInInput,
                maxLen: 32,
                outTips: errStr.adressName,
                nullTips: errStr.adressNameNull
            }]);
        })

    };
    //初始化
    var init = function(){

        //初始化登陆和搜索
        common.initLogin();

        //发布海外简历
        enResume.init($('.user-info .tab-menu'));

        //初始化折叠列表
        collapse.initCollapse(".work-collapse");

        //textarea 验证
        DetailAPI.initIntroduction($('.text-workInfo'));

        //打开添加面板
        DetailAPI.openAddPanel();

        //页面保存交互
        DetailAPI.closeTab();

        //职业信息
        initWorkInfo();
    }
	init();
});