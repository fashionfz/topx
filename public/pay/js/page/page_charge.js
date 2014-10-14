require(['../js/common/util'],function(util){

    $(function(){

        util.tab($(".tab-menu-btn"), "current");

		var initDeleteTrade = function(){
			var deleteBtn = $(".btn-delete");
			deleteBtn.on("click", function(){
		        $.confirm('你确定要删除此条交易信息吗？', function(){

	            }, function(){

	            });
			});
		}

		initDeleteTrade();
	});
});
