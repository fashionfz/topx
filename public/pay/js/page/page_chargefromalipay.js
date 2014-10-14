$(function(){
    var confirmHTML = [
        '<div class="share-block clearfix">',
            '<div class="dialog-tit clearfix">',
                '<span class="span-left">充值</span>',
            '</div>',
			'<table class="dialog-table">',
				'<tbody>',
					'<tr>',
						'<td><div class="icon-alert"></div></td>',
						'<td class="d-alert-content w300">',
							'<h3>请在新打开的页面上完成充值！</h3>',
							'<p class="mt5">充值完成前请不要关闭此窗口！完成充值后请根据您的情况点击下面按钮。</p>',
						'</td>',
					'</tr>',
				'</tbody>',
			'</table>',
        '</div>'].join("");

    var chargeBtn = $("#btn-charge");

	chargeBtn.on("click", function(){
        $.dialog({
            content : confirmHTML,
            lock : true,
            okValue : '已完成充值',
            ok : function(){
            	//todo
            },
            cancelValue : '充值失败',
            cancel : function(){
            	//todo
            },
            initialize:function(){
            	//todo
            }

        });
	});

});