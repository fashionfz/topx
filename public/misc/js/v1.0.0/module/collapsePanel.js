/*
 * @description: 折叠组件
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 * @data: 2013/11/26
 */

define('module/collapsePanel', ["common/util"], function(util){

	return {

		initCollapse:function(o){
			
			var init = function(elem){

				var collapse = elem,
                    //添加按钮
					showBtn = collapse.find(".collapse-show-btn"),
                    //取消按钮
					closeBtn = collapse.find(".collapse-close-btn"),
                    //新增面板
					pannel = collapse.find(".collapse-pannel"),
                    //添加按钮的容器
					btnBox = collapse.find(".collapse-btn-box"),

					//非必须
					collapseBox = collapse.closest(".collapse-box"),
					collapseOutInfo = collapseBox.find(".out");

				if (collapseOutInfo.length > 3) {
					showBtn.hide();
				};

				showBtn.on("click", function(){
                    //显示 新增面板
					pannel.slideDown(200);
                    //隐藏 添加按钮
					btnBox.slideUp(200);
				});

				closeBtn.on("click",function(){
                    //隐藏 新增面板
					pannel.slideUp(200);
                    //显示 添加按钮
					btnBox.slideDown(200);
				});
			}

			var tra = function(o){
				var elem = $(o);
				if (elem.length > 0) {
                    $.each(elem, function(){
                        init($(this));
                    });
				};
			}

			if(typeof o === "string"){
				tra(o);
				return;
			}

			if (util.isArray(o)) {
				for (var i = o.length - 1; i >= 0; i--) {
					tra(o[i]);
				};
				return;
			};

		},

		showPannel: function(obj){
			var collapse = obj,
				pannel = collapse.find(".collapse-pannel"),
				btnBox = collapse.find(".collapse-btn-box");
			
			if (pannel.is(":hidden")) {
				pannel.slideDown(200);
				btnBox.slideUp(200);
			};
		},

		hidePannel: function(obj){
			var collapse = obj,
				pannel = collapse.find(".collapse-pannel"),
				btnBox = collapse.find(".collapse-btn-box");

			if (!pannel.is(":hidden")) {
				pannel.slideUp(200);
				btnBox.slideDown(200);
			};
		}

	}

});