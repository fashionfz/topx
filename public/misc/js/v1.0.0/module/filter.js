/**
 * @description: simple下拉菜单（通过hover触发）
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 */

define('module/filter', [], function(){
	return{
		init: function(){
		    $('.filter-select').hover(function(){
                var list = $(this).find(".filter-list");
		        $(this).find(".filter-name-area").addClass("on");
                list.css('min-width', $(this).width()-2);
                list.stop().show(100);
		    }, function(){
		        $(this).find(".filter-name-area").removeClass("on");
		        $(this).find(".filter-list").stop().hide(100);
		    });
		}
	}
});