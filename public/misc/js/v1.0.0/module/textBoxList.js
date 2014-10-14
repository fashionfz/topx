/**
 * @description: 智能提示并添加到textbox
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 * @date: 2013/12/29
 */

define("module/textBoxList", ["common/util", "module/charFormat"], function(util, charFormat){

    var maxLen = 16,
        errStr = {
            addNew: "通过服务标签，别人能够更精准地搜索到您！点击此处输入服务标签，并按enter键进行添加",
            limit: "标签数量已达上限",
            add: "请添加标签",
            same: "不能添加相同的标签",
            singleErr: "单个标签最长为{0}个中文字符或{1}个英文、数字",
            nullTips: "服务标签不能为空"
        }

    /**
     * 初始化服务标签
     */
    var itemTpl = [
            '<li class="textbox-item" data-value="#{itemText}">',
            	'<span>#{itemText}</span>',
            	'<i title="删除标签"></i>',
            '</li>'
        ].join('');

 	var initTextBox = function(obj){

		var config = {
			target: obj.target, //必需
			data: obj.dropDownData, //必需
			width: obj.width || "auto", //非必须
			maxHeight: obj.maxHeight || "180" //非必需
		}

	    var tbInput = $(config.target),
	    	tbInputLi = tbInput.parent("li"),
	    	textbox = tbInputLi.parent(".textbox"),
	    	tips = textbox.next(".tips"),
	    	isShow = false;

        //输入框最大 输入16个字符
        tbInput.attr("maxlength", maxLen);

		/**
	     * 添加服务标签
	     */
	    var addItemHTML = function(itemText){

	        var oldItems = getItems(textbox.find(".textbox-item"));

			clearTips();

			tbInput.focus();
			
	        //判定标签不能重复
	        for(var i= 0; i < oldItems.length; i++){
	            if( itemText === oldItems[i] ){
	            	tips.html(errStr.same);
	                return false;
	            }
	        }

	        var itemsHtml = $(util.template(itemTpl, {itemText : util.safeHTML(itemText)}));

	        if(getItemsLen() > 19){
	        	tips.html(errStr.limit).fadeIn(1000).fadeOut(1000,function(){
		        	tips.html('');
	        	});
	        }else{
	            tbInputLi.before(itemsHtml);
	        }

	        return itemsHtml;
	    }

	    /**
	     * 获取items，并组成数组
	     */
	    var getItems = function(){
	    	var itemsArr = [];
	        $(".textbox-item").each(function(i, e){
	        	var txt = $(e).find("span").text();
	        	itemsArr.push(txt);
	        });
	        return itemsArr;
	    }

	    /**
	     * 获取items val，并组成数组
	     */
	    var getItemsVal = function(){
	    	var arr = [];
	        $('.textbox-item').each(function(i, e){
	            arr.push($(e).attr('data-value'));
	        });
	        return arr;
	    }

        /**
         * 清空并隐藏自动完成列表
         */
        var emptyTags = function(){
            $('.ac ul').empty();
            $('.ac').hide();
        }

	    /**
	     * 判断服务标签是否为空
	     */
	 	var isEmpty = function(){
	        var items = getItemsLen(),
	            isEmpty = false;

	        if (items === 0) {
	            isEmpty = true;
	        }
	        return isEmpty;
	 	}

	 	var getItemsLen = function(){
	 		return textbox.find(".textbox-item").length;
	 	}

	 	var clearTips = function(){
	 		tips.html("");
	 	}

	 	var clearInput = function(){
	 		tbInput.val("");
	 	}

	 	var getInputVal = function(){
	 		return tbInput.val();
	 	}

	 	var addItem = function(){

	 		var txt = $.trim(tbInput.val());

			clearTips();
            emptyTags();

			//不能为空标签
	        if (txt === "") {
	        	//tips.html(errStr.add);
	        	return;
	        };

			//添加标签
            var addHtml = addItemHTML(txt);
			if (!addHtml) { return };

			//删除一个标签
            addHtml.find('i').on("click", function(event){
	        	event.stopPropagation();
	            $(this).closest(".textbox-item").remove();
                if(isEmpty()){
                    tips.html(errStr.nullTips);
                }
	            if (getItemsLen() < 20 && getItemsLen() !==0 ) {
	            	clearTips();
	            }
	        });

	        //清空input值
			clearInput();
	 	};

        //清除 所有标签
        var clean = function(){
            textbox.find(".textbox-item").remove();
        }

	    //绑定删除按钮
	    $(".textbox-item i").off();
	    $(".textbox-item i").on("click", function(e){
	    	e.stopPropagation();
            $(this).closest(".textbox-item").remove();
            if(isEmpty()){
                tips.html(errStr.nullTips);
            }
	    });

	    textbox.on("click", function(e){
	    	e.stopPropagation();
	    	tbInput.focus();
	    });

		tbInput.AutoComplete({
            autoSelected: false,
			width: config.width,
			maxHeight: config.maxHeight,
			//data: ["C", "C++", "C#"],
		    data: config.data,

		    //test
		    onerror:function(e){
                util.trace(e);
		    },
		    afterSelectedHandler: function(obj){

		    	var txt = obj.value,
                    addHtml = addItemHTML(txt);

				//添加标签
				if (!addHtml) { return };

				//删除一个标签
                addHtml.find('i').on("click", function(e){
		            $(this).closest(".textbox-item").remove();
                    if(isEmpty()){
                        tips.html(errStr.nullTips);
                    }
		            if (getItemsLen() < 20 && getItemsLen() !== 0) {
		            	clearTips();
		            }
                    e.stopPropagation();
		        });

		        //清空input值
				clearInput();

		    }

		});

		tbInput.on("keydown", function(e){
			var txt = $.trim($(this).val());
			var realLen = charFormat.getByteANSILen(txt);

	        //单个标签不能超过16个字符
	        if (realLen > maxLen) {
	        	tips.html(util.strFormat(errStr.singleErr, [maxLen/2, maxLen]));
	        	return;
	        }else{
	        	clearTips();
	        };

			if (e.type === "keydown" && e.keyCode === 13) {

				addItem();

			};
		});

	    return {
	    	getItems: getItems,
	    	getItemsVal: getItemsVal,
            isEmpty: isEmpty,
            clearInput: clearInput,
            getInputVal: getInputVal,
            addItem: addItem,
            clean: clean
	    }

 	}
 	return {
 		initTextBox: initTextBox
 	}

});

