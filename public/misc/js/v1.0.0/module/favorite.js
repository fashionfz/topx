/**
 * @description: 收藏夹
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 */

define('module/favorite', [
    'common/util',
    'common/interface',
    'module/cookie'
], function(util, inter, cookie){

	var bindFavBtn = function($btn){

		var curId = parseInt(cookie.get('_u_id')) || 0;

	    $btn.on("click", function(){

	    	if (!curId) { 
	    		location.href = '/login?msg=1&referer=' + encodeURIComponent(location.href);
	    		return;
	    	}
	        var loading = $.tips('正在添加到收藏夹...'),
	            btn = $(this),
	            id = btn.attr("data-id"),
	            isFavorite = parseInt(btn.attr("data-isfavorite")),
	            addURL = util.strFormat(inter.getApiUrl().addFavorite, [id]),
	            deleteURL = util.strFormat(inter.getApiUrl().deleteFavorite, [id]);

	        var deleteFav = function(){
	            $.ajax({
	                url: deleteURL,
	                success: function(){
	                    
	                    btn.off("hover");
	                    btn.removeClass("btn-gray").addClass("btn-white");
	                    btn.attr("data-isfavorite", "0");
	                    btn.find(".btn-text").text("收藏");
	                },
	                error: function(){
	                    $.alert('服务器繁忙，请稍后再试。');
	                }
	            });
	        };
	        if (isFavorite) {
	        	loading.close();
	        	$.confirm("确定要取消收藏吗？", deleteFav);
	        }else{
	            $.ajax({
	                url: addURL,
	                success: function(){
	                    loading.close();
	                    btn.removeClass("btn-white").addClass("btn-gray");
	                    btn.attr("data-isfavorite", "1");
	                    btn.find(".btn-text").text("已收藏");
	                    bindHover(btn);
	                },
	                error: function(){
	                    $.alert('服务器繁忙，请稍后再试。');
	                }
	            });
	        };

	    });

		bindHover($btn);
	};

	var bindHover = function($btn){
		$btn.each(function(i, e){
			var btn = $(e);
			var isFav = btn.attr("data-isFavorite");
			if (parseInt(isFav)) {
				btn.hover(function(){
					btn.find(".btn-text").text("取消收藏");
				}, function(){
					btn.find(".btn-text").text("已收藏");
				});
			};
		});
	}

	return {
		initFavBtn: function(){
		    /*
		     * 点击收藏按钮
		     */

		     bindFavBtn($(".btn-fav"));
		},

		bindFavBtn: bindFavBtn
	}

});