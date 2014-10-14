/**
 * @description: 表单替换
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 * @data: 2013/10/26
 */

define('module/prettifyForm', ['common/util'], function(util){
	//config配置传参
	/*
	config = {
	    change:function(){
	        //todo       
	    },
		list = {
			//页面中的每一个select对象
		},
		horizontalPos = "left",  //横向位置，只能在固定宽度的元素当值使用，参数:left(默认)  //mid  //right
		//待定义 varticalPos = "mid"  //纵向位置，参数:top  //mid(默认)  //bottom
		//待定义 disabled = "true"  //表单的可用性
 	}
	*/
    window.selectIndexId = 0;

	var selectTemp = '<a class="select" data-id="#{eleID}" data-name="#{eleName}" href="javascript:;">'+
					'<div class="select-head">'+
						'<span class="select-head-title" data-value="#{eleValue}">#{eleTitle}</span>'+
						'<em></em>'+
					'</div>'+
					'<ul class="select-bar fl" data-act="hide" tabindex="1">#{eleOption}</ul>'+
					'</a>';
	
	var radioTemp = '<a class="radio-btn" data-id="#{eleID}" data-value="#{eleValue}" data-name="#{eleName}" href="javascript:;">'+
						'<span class="radio #{isChecked}"></span>'+
						'<span class="radio-title">#{eleTitle}</span>'+
					'</a>';

	var checkboxTemp = '<a class="checkbox-btn #{isDisabled} clearfix" data-id="#{eleID}" data-value="#{eleValue}" data-name="#{eleName}" href="javascript:;">'+
	                    '<span class="checkbox #{isChecked}"></span>'+
	                    '<span class="checkbox-title">#{eleTitle}</span>'+
	                   '</a>';

    /**
     * @param config{
     *      target:美化目标,
     *      width:,宽度
     *      list:,
     *      change:回调函数}
     * @returns {{change: Function, get: Function, selectChange: Function, setSelectVal: Function}}
     */
	var initSelect = function (config) {
		config = config || {};
		config.list = {};

		var selectAll,
		    eventList = {},
		    preventState = false,
		    globalChangeCallback;

		if (!!config.target) {
			var target = $(config.target);
			if (target[0].tagName.toLowerCase() == "select") {
				selectAll = $(config.target);
			}else{
				selectAll = target.find("select");
			};
		}else{
			selectAll = $(document).find("select");
		};

	    //配置config.change
		if (config.change) {
		    globalChangeCallback = config.change;
		}

		//生成select需要的数据
		//参数 that:原来的select元素 n:构成的第n个select
		var composeSelectData = function(that, n){

			//构成新的select
			//准备构成新的select的数据
			var select = that,
				firstOption = select.find('option:selected').val(),
	            firstText = select.find('option:selected').text(),
				options = select.find("option"),
				optionStr = "",
				id = select.attr("id"),
				isDisabled = select.prop("disabled");

			if (!id) {
				select.attr("id", "select" + n);
				id = "select"+n;
			};

			var sharpId = "#" + id;

	        //构成新的select option
			options.each(function(i,e){
	            var optHtml = $(this).html(),
	                isSelect = $(this).is("option:selected"),
	                optText = $(this).text(),
	                optValue = $(this).attr('value'),
	                isImage = optValue.indexOf('<img')>-1;

				if(isSelect) {
	                if(isImage){
	                    optionStr += '<li class="highlight image clearfix" data-value="' + (optValue||optText) + '">' + (optValue||optHtml) + '</li>';
	                }else{
	                    optionStr += '<li class="highlight" data-value="' + (optValue||optText) + '">' + optText + '</li>';
	                }
				}else{
	                if(isImage){
	                    optionStr += '<li class="image clearfix" data-value="' + (optValue||optText) + '">' + (optValue||optHtml) + '</li>';
	                }else{
	                    optionStr += '<li data-value="' + (optValue||optText) + '">' + optText + '</li>';
	                }
				}
			});

			//返回组成新的select的所有数据
			var obj = {
				eleID: id || "",
				eleName: select.attr("name") || "",
	            eleValue: firstOption || "",
				eleTitle: firstText || "",
				eleOption: optionStr,
				sharpId: sharpId,
				isDisabled: isDisabled
			};
			return obj;

		}

		//处理下拉菜单的enter事件
		//参数 select：该下拉菜单
		var handleUpDownKey = function(select){

			//准备拉菜单数据
			var selectItem = select.find(".select-bar li"),
				selectBar = select.children(".select-bar"),
				selectTitleText = $.trim(select.find(".select-head-title").text()),
				barLen = selectItem.length - 1,
				index = 0,
				highlightEle,
				i,
				scrollHeight = 0;

			for (i = barLen; i >= 0; i-=1) {
				var he = selectItem.eq(i),
					itemText = $.trim(he.text());
				if (selectTitleText === itemText) {
					highlightEle = he;
					if (!highlightEle.hasClass("highlight")) {
						highlightEle.addClass("highlight").siblings().removeClass("highlight");
					};
					index = i;
					break;
				}else{
					highlightEle = selectItem.eq(0);
					highlightEle.addClass("highlight").siblings().removeClass("highlight");
				};
			};

			var itemHeight = highlightEle ? highlightEle.outerHeight() : null ,
				barHeight = selectItem.length * itemHeight;
			//绑定上下按键事件
			selectBar.off("keydown");
		    selectBar.on("keydown", function(e){

		    	//向上
				if (e.keyCode === 38) {
					e.preventDefault();
					highlightEle.removeClass("highlight");
					index--;
					if (index < 0) {
						index = barLen;
						scrollHeight = barHeight - itemHeight;
					}else{
						scrollHeight = scrollHeight - itemHeight;
					};
					selectItem.eq(index).addClass("highlight");

					//scroll position
					selectBar.scrollTop(scrollHeight);

					//set highlight
					highlightEle = selectItem.eq(index);
					setSelectTitle(highlightEle);
					return;
				};

				//向下
				if (e.keyCode === 40) {
					e.preventDefault();
					highlightEle.removeClass("highlight");
					index++;
					if (index > barLen) {
						index = 0;
						scrollHeight = 0;
					}else{
						scrollHeight = scrollHeight + itemHeight;
					};
					selectItem.eq(index).addClass("highlight");

					//scroll position
					selectBar.scrollTop(scrollHeight);

					//set highlight
					highlightEle = selectItem.eq(index);
					setSelectTitle(highlightEle);
					return;
				};

				if (e.keyCode === 13) {
					//阻止冒泡到顶层select
					e.stopPropagation();
					var item,
						j,
						itemLen = selectItem.length - 1;
					for (var j = itemLen; j >= 0; j-=1) {
						if (selectItem.eq(j).hasClass("highlight")) {
							item = selectItem.eq(j);
						};
					};

					//点击按钮触发change事件
					if (item) {
						selectUIChange(item);
						preventState = true;
				    };
				};
		    });
		}

		//处理下拉菜单的显示和隐藏
		//参数 that: select元素
		var selectBarAct = function(sel){

			var	select = sel,
				selectBar = select.children(".select-bar"),
	            isShow = !(selectBar.attr("data-act") === "hide");

			if (!isShow) {
			    selectBar.slideDown(200);
			    selectBar.attr("data-act", "show");
			    selectBar.focus();
			    handleUpDownKey(select);
			} else {
			    selectBar.slideUp(200);
			    selectBar.attr("data-act", "hide");
			}

		};

		//触发下拉菜单事件，通过点击或者enter键触发
		//参数 that:select元素
		var selectAct = function(sel){

			$(".select-bar").slideUp(200);

			//准备元素数据
			var select = sel,
				selectHead = select.find(".select-head"),
				selectBar = selectHead.siblings(".select-bar"),
				selectItem = selectBar.find("li"),
				selectTitle = select.find(".select-head-title"),
				selectId = select.attr("data-id");

			//select获取焦点
			select.focus();

			//绑定下拉菜单的事件，控制下拉菜单收放
			selectBarAct(select);

			//解除绑定
			selectItem.off();

			selectItem.on("click", function (e) {
				e.stopPropagation();
			    //点击按钮触发change事件
			    selectUIChange($(this));
			});
		}

		var setSelectTitle = function(item){
			//准备元素数据
			var select = item.closest(".select"),
				id = select.attr("data-id"),
				title = select.find(".select-head-title");
	        //同步旧的select
	        $('#' + id).val(item.attr('data-value') || item.text());
	        //同步新的select
		    title.attr('data-value', item.attr('data-value')).text(item.text());
		}

        //elem: 美化的对象, str 设置的值

		var setSelectVal = function(elem, str){
            if(!elem || !elem.length)return;
			var select = elem,
				id = select.attr("data-id"),
				title = select.find(".select-head-title");
            //当str为空是默认显示第一个
            str = str||  $('#' + id).find("option").eq(0).text();
	        //同步旧的select
	        $('#' + id).val(str);
	        //同步新的select
		    title.attr('data-value', str).text(str);
		}
		//点击下拉菜单其中的一项后，触发UI改变
		//参数 item: 下拉菜单其中的一项
	    var selectUIChange = function(item){
			//准备元素数据
			var selectBar = item.closest(".select-bar"),
				select = selectBar.parent(".select"),
				selectId = select.attr("data-id");

	        item.addClass('highlight').siblings().removeClass('highlight');

	        setSelectTitle(item);
		    //select change 查看是否定义单个change事件，如果定义
		    //则运行定制的单个change时间的callback
	        if (!!globalChangeCallback) { globalChangeCallback(item.attr('data-value'), item.text()) };

	        for (var thisEventId in eventList){
	        	if (selectId == thisEventId) {
	        		var o = {};
	        		o.selected = item;
	        		o.selectedText = item.text();
	        		eventList[selectId](o);
	        	};
	        }

		    //将select bar收回
		    selectBar.slideUp(200);
		    selectBar.attr("data-act", "hide");
		    select.focus();
		   	//解除select item的绑定，避免select item被重复绑定
			//item.off();
	    }

	    //替换所有的select
		selectAll.each(function (i, e) {
			//去除重复项


			var nextNew = $(e).next(".select");

			if (nextNew.length !== 0) {
				return true;
			};

			//获取生成select的数据
			var selectData = composeSelectData($(e), window.selectIndexId);
            window.selectIndexId ++;
			//将select添加到页面上
	        nextNew = $(util.template(selectTemp, selectData));
			//添加禁用UI
			if (selectData.isDisabled) {
				nextNew.addClass("disabled");
				nextNew.find("em").remove();
			};

			//添加到原select后面
			$(e).after(nextNew);

			//配置li宽度
	        if(config.width){
	            nextNew.width(config.width);
	            nextNew.find('li').each(function(m, n){
	                if($(n).width() < config.width){
	                    $(n).width(config.width-42);
	                }
	            });
	        }

			//将原来的select隐藏
			$(e).hide();

	        //生成list
			config.list[selectData.sharpId] = $("[data-id='" + selectData.eleID + "']");
		});

		//获取新的select
		var newSelect = $(document).find(".select"),
			newSelectBar = newSelect.find(".select-bar");

		//set horizontal position
		if (config.horizontalPos) {
			switch(config.horizontalPos){
				case "left":
					newSelect.css({ "float":"left" });
					break;
				case "middle":
					newSelect.css({ "float":"none" });
					break;
				case "right":
					newSelect.css({ "float":"right" });
					break;
				default:
					newSelect.css({ "float":"left" });
			}
		};
        if(config.width){
            newSelect.css({ "":"left" });
        }

		//点击select按钮触发事件
		newSelect.off();

		//绑定所有下拉菜单的展开事件，包括点击展开和使用enter按键展开
		newSelect.on("click keydown", function (e) {
			//阻止select的click默认事件，这个很重要
			e.stopPropagation();
			//如果被禁用则不绑定事件
			if ($(this).hasClass("disabled")) {
				return;
			};

			//click事件
			if (e.type === "click") {
				if (preventState === true) {
					preventState = false;
					return;
				};
				selectAct($(this));
			//键盘keydown
			}else if(e.type === "keydown" && e.keyCode === 13){
				e.preventDefault();
				preventState = true;
				//键盘Enter键按下后触发
				selectAct($(this));
			};
		});

		//将a标签拖动禁用
		newSelect.on("mousedown", function(e){
			e.preventDefault();
		});
		//将a标签拖动禁用
		newSelectBar.on("mousedown", function(e){
			e.preventDefault();
		});

		//点击select表单之外触发
	    //$(document).off("click"); 可能会取消其他组件的$(document).on("click")
	    $(document).on("click", function (e) {
	    	//将slide收起
	        newSelectBar.slideUp(200);
	        newSelectBar.attr("data-act", "hide");
	        //解除document的绑定
	    });

		return {
			/*
	        * getSelectItemData 获取点击select的值
	        * 参数：elem 获取值的元素
	        * 返回：string 获取元素Radio的字符串
	        */
			// getClickItemData: function (elem) {
			//     return $(elem).text();
			// },

		    //设置全局change事件, 当select-head里面的值改变时触发
		    //.change( handler(eventObject) )
			change: function (callback) {
			    globalChangeCallback = callback;
			},

	        //通过原始id获取美化后的select
			get: function (id) {
			    return id === undefined ? config.list : config.list[id];
			},

			//设置一个select所触发的事件
			selectChange: function(select, callback){
				var id = select.attr("data-id");
				eventList[id] = callback;
			},
			setSelectVal: setSelectVal
		}
	}

	var initRadio = function(config){

		config = config || {};
		config.list = {};

		var eventList = {},
			globalChangeCallback,
			radioAll;

		if (!!config.target) {
			var target = $(config.target);
			if (target[0].tagName.toLowerCase() == "input") {
				radioAll = $(config.target);
			}else{
				radioAll = target.find("label input[type='radio']");
			};
		}else{
			radioAll = $(document).find("label input[type='radio']");
		};

	    //配置config.change
		if (config.change) {
		    globalChangeCallback = config.change;
		}

		//compose radio btn的数据
		var composeRadioData = function(that, n){

			var isCheck = $(that).prop("checked") ? "checked":"",
				oriValue = $(that).attr("value"),
				id = $(that).attr("id");

			oriValue = (oriValue === "on") ? "" : oriValue;

			if (!id) {
				$(that).attr("id","radio"+n);
				id = "radio"+n;
			};

			var sharpId = "#" + id,
				obj = {
					eleID: id,
					eleValue: oriValue || "",
					eleName: $(that).attr("name") || "",
					isChecked: isCheck,
					eleTitle: $(that).next("span").text() || "",
					sharpId: sharpId
				};

			return obj;
		}

		//radio btn改变的时候触发
		var radioChange = function(radioBtn){

			var radio = radioBtn.find(".radio"),
				id = radioBtn.attr("data-id"),
				name = radioBtn.attr("data-name"),
				oriRadio = radioBtn.siblings("label").find("input[id='"+ id +"']"),
				expRadioBtn = $(".radio-btn[data-name='" + name + "']"),
				expRadio = expRadioBtn.find(".radio");

			expRadio.removeClass("checked");
			radio.addClass("checked");
			//original radio 同步
			oriRadio.prop("checked", true);
			oriRadio.parent().siblings("label").children("input[type='radio']").prop("checked", false);

			radioBtn.focus();

			if (!!globalChangeCallback) { globalChangeCallback(oriRadio) };

			for (var thisEventId in eventList){
	        	if (id == thisEventId) {
	        		eventList[id]();
	        	};
	        }

		}
		var radioAct = function(radioBtn){

			var radio = radioBtn.find(".radio");

			//radio btn 被点击后，如果改变，就触发change
			if (!radio.hasClass("checked")) {
				radioChange(radioBtn);
			};

		}
		/*
        * 设置指定的radio为选中状态
        * 参数：elem: 美化后的jquery对象
        */
		var setRadioVal = function(elem){
            if(!elem || !elem.length)return;
            radioChange(elem);
		}

		//替换所有的radio btn
		radioAll.each(function(i,e){
			//去除重复项
			var nextNew = $(this).parent("label").next(".radio-btn");

			if (nextNew.length !== 0) {
				return true;
			};
			//获取组成新的radio的数据
			var radioData = composeRadioData(this, i);

			$(this).parent("label").after(util.template(radioTemp, radioData));
			$(this).parent().hide();

			config.list[radioData.sharpId] = $("[data-id='" + radioData.eleID + "']");
		});

		//为所有的radio btn添加事件
		var radioBtn = $(document).find(".radio-btn");

    	//set horizontal position
    	if (config.horizontalPos) {
    		switch(config.horizontalPos){
    			case "left":
    				radioBtn.css({ "float":"left" });
    				break;
    			case "middle":
    				radioBtn.css({ "float":"none" });
    				break;
    			case "right":
    				radioBtn.css({ "float":"right" });
    				break;
    			default:
    				radioBtn.css({ "float":"left" });
    		}
    	};

		radioBtn.on("click keydown",function(e){

			//e.stopPropagation();
			if (e.type === "click") {
                e.preventDefault();
				radioAct($(this));
			}else if(e.type === "keydown" && e.keyCode === 13){
				e.preventDefault();
				radioAct($(this));
			};

		});

		return {
			/*
	         * getRadioBtnData 获取点击radio button的值
	         * 参数：elem 获取值的元素
	         * 返回：string 获取元素Radio的字符串
	         */
			// getClickBtnData:function(elem){
			// 	return $(elem).attr("data-value");
			// },
			change:function(callback){
				globalChangeCallback = callback
			},
			get: function (id) {
			    return id === undefined ? config.list : config.list[id];
			},
			//可对某个radio改变时定制
			radioChange: function(elem, callback){
				var id = elem.attr("data-id");
				eventList[id] = callback;
			},
			setRadioVal: setRadioVal
		}
	}

	var initCheckbox = function(config){

	    config = config || {};
	    config.list = {};
	    config.change = config.change || {};

		var eventList = {},
			globalChangeCallback = {},
			checkboxAll;

		if (!!config.target) {
			var target = $(config.target);
			if (target[0].tagName.toLowerCase() == "input") {
				checkboxAll = $(config.target);
			}else{
				checkboxAll = target.find("label input[type='checkbox']");
			};
		}else{
			checkboxAll = $(document).find("label input[type='checkbox']");
		};

	    //配置config.change
		if (config.change.ok) {
		    globalChangeCallback.ok = config.change.ok;
		    globalChangeCallback.cancel = config.change.cancel;
		}

		var composeCheckboxData = function(that, n){
    		var isCheck = $(that).prop("checked") ? "checked":"",
    			oriValue = $(that).attr("value"),
				id = $(that).attr("id"),
				isDisabled = $(that).attr('disabled');

			oriValue = (oriValue === "on") ? "" : oriValue;

			if (!id) {
				$(that).attr("id","checkbox"+n);
				id = "checkbox"+n;
			};

			var sharpId = "#"+id,

				obj = {
	    			eleID: id,
	    			eleName: $(that).attr("name") || "",
	    			eleValue: oriValue || "",
	    			eleTitle: $(that).next("span").text() || "",
	    			sharpId: sharpId,
	    			isChecked: isCheck,
	    			isDisabled : isDisabled === undefined?'':'disabled'
	    		};

    		return obj;
		}

		//checkbox改变的时候

		var checkboxChange = function(checkboxBtn){
    		var checkbox = checkboxBtn.find(".checkbox"),
    			id = checkboxBtn.attr("data-id"),
    			oriCheckbox = checkboxBtn.siblings("label").find("input[id='"+id+"']"),
    			eventAct = {};
				//触发单独的事件
				for (var thisEventId in eventList){
		        	if (id == thisEventId) {
		        		eventAct = eventList[id];
		        	};
		        };

			if (!checkbox.hasClass("checked")) {
				checkbox.addClass("checked");
				//同步
				oriCheckbox.prop("checked",true);
				if (eventAct.ok) { eventAct.ok(); };
				if (globalChangeCallback.ok) { globalChangeCallback.ok(); };
			}else{
				checkbox.removeClass("checked");
				//同步
				oriCheckbox.prop("checked",false);
				if (eventAct.cancel) { eventAct.cancel(); };
				if (globalChangeCallback.cancel) { globalChangeCallback.cancel(); };
			};

			checkboxBtn.focus();

		}

		var checkboxAct = function(elem){
			if(elem.hasClass('disabled')) return;
			var checkbox = elem.find(".checkbox");

			//radio btn 被点击后，如果改变，就触发change
			//if (!checkbox.hasClass("checked")) {
				checkboxChange(elem);
			//};
		}

		/*
        * 设置指定的Checkbox为选中状态
        * 参数：elem: 美化后的jquery对象
        */
		var setCheckboxVal = function(elem){
            if(!elem || !elem.length)return;
            var checkbox = elem.find(".checkbox");

            if (!checkbox.hasClass("checked")) {
            	checkboxChange(elem);
            }
		}

		/*
        * 取消指定的Checkbox的选中状态
        * 参数：elem: 美化后的jquery对象
        */
		var cancelCheckboxVal = function(elem){
            if(!elem || !elem.length)return;
            var checkbox = elem.find(".checkbox");
            
            if (checkbox.hasClass("checked")) {
            	checkboxChange(elem);
            }
		}

		/*
        * 切换指定的Checkbox的check状态
        * 参数：elem: 美化后的jquery对象
        */
		var toggleCheckboxVal = function(elem){
            if(!elem || !elem.length)return;
            checkboxAct(elem);
		}

    	checkboxAll.each(function(i, e){
			//去除重复项
			var checkboxBtn = $(this).parent("label"),
				nextNew = checkboxBtn.next(".checkbox-btn");

			if (nextNew.length !== 0) {
				return true;
			};

			//初始化

    		var checkboxData = composeCheckboxData(this, i);

    		checkboxBtn.after(util.template(checkboxTemp, checkboxData));
    		checkboxBtn.hide();

    		config.list[checkboxData.sharpId] = $("[data-id='" + checkboxData.eleID + "']");
    	});

    	var checkboxBtns = $(document).find(".checkbox-btn");

    	//set horizontal position
    	if (config.horizontalPos) {
    		switch(config.horizontalPos){
    			case "left":
    				checkboxBtns.css({ "float":"left" });
    				break;
    			case "middle":
    				checkboxBtns.css({ "float":"none" });
    				break;
    			case "right":
    				checkboxBtns.css({ "float":"right" });
    				break;
    			default:
    				checkboxBtns.css({ "float":"left" });
    		}
    	};

    	checkboxBtns.on("click keydown", function(e){
            var $this = $(this);
			if (e.type === "click") {
                e.preventDefault();
				checkboxAct($this);
			}else if(e.type === "keydown" && (e.keyCode === 13 || e.keyCode === 32)){
                e.preventDefault();
				checkboxAct($this);
			}
    	});

    	return {
			// getClickBtnData:function(elem){
			// 	return $(elem).attr("data-value");
			// },

			//checkbox改变时触发
			change:{
				ok:function(callback){
					globalChangeCallback = callback;
				},
				cancel:function(callback){

				}
			},
			get: function (id) {
			    return id === undefined ? config.list : config.list[id];
			},
			//可对某个radio改变时定制
			checkboxChange: function(elem, okCallback, cancelCallback){
				var id = elem.attr("data-id");
				eventList[id] = {};
				eventList[id].ok = okCallback;
				eventList[id].cancel = cancelCallback;
			},
			setCheckboxVal: setCheckboxVal,
			cancelCheckboxVal: cancelCheckboxVal,
			toggleCheckboxVal: toggleCheckboxVal
    	}
	}

	return {
		/*
         * select
         */
		initSelect:function(config){
			return initSelect(config);
		},

		/*
         * radio button
         */
		initRadio:function(config){
			return initRadio(config);
		},

		/*
         * checkbox
         */
        initCheckbox:function(config){
        	return initCheckbox(config);
        }
	}
});