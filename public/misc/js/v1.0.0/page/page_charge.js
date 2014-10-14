/**
 * @description: 我的钱包
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 * @date: 2013/12/29
 */

require(['common/util', 'module/pageCommon'],function(util, common){
    common.initLogin();

    util.tab($(".tab-menu-btn"), "current");

    var callback = function (data) {

        var url = data.url,
            id = data.id,
            isBindPhone = data.bindStatus === "true" ? true : false,
            errStr = "";

        switch(id){
            case "chargeBtn":
                errStr = "充值";
                break;
            case "withdrawBtn":
                errStr = "提现";
                break;
            case "transferBtn":
                errStr = "转账";
                break;
        }

        if (!isBindPhone) {
            $.dialog({
                content : [
                    '<div class="dialog-panel">',
                        '<div class="dialog-tit clearfix">',
                        '<span class="span-left">确认信息</span>',
                        '</div>',
                        '<table class="dialog-table">',
                            '<tr>',
                                '<td><div class="icon-alert"></div></td>',
                                '<td class="d-alert-content">为了保障你的账号安全，请绑定手机号码后再' + errStr + '。</td>',
                            '</tr>',
                        '</table>',
                    '</div>'].join(""),
                lock:true,
                initialize:function(){
                    //to do
                },
                ok:function(){
                    location.href = "/user/usersetting";
                },
                okValue:"现在绑定",
                cancel:function(){
                    //to do
                },
                cancelValue:"取消"
            });

            return;
        };

        switch(id){
            case "chargeBtn":
                location.href = "/charge/alipay" + "?iframe=" + url;
                break;
            case "withdrawBtn":
                location.href = "/charge/withdraw/alipay" + "?iframe=" + url;
                break;
            case "transferBtn":
                location.href = "/charge/transfer" + "?iframe=" + url;
                break;
            case "deleteItemBtn":
                location.href = url;
                break;
        }
    }

    XD.receiveMessage(callback);

});
