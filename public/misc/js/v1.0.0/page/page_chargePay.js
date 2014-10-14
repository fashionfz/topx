/**
 * @description: 我的钱包
 * @author: YoungFoo(young.foo@helome.com)
 * @update:
 */
require(['module/pageCommon', 'module/prettifyForm'], function(common, prettifyForm){
    common.initLogin();

    prettifyForm.initCheckbox();
    prettifyForm.initSelect();
    //prettifyForm.initRadio();

    var getURLParameter = function(param){
        var url = window.location.search;
        var reg = "/^.*[\\?|\\&]" + param + "\\=([^\\&]*)/";  
        reg = eval(reg);
        var ret = url.match(reg);
        if (ret != null) {
            return ret[1];
        } else {
            return "";
        }
    }

    var loadIframe = function(){
        var iframeUrl = getURLParameter("iframe");
        var iframe = document.getElementById("ibox");
        if (iframe === null) { return };
        //iframe.src = "http://172.16.2.82:8081" + iframeUrl;
        //iframe.src = "http://localhost:3716/secondPage.html";
        iframe.src = ued_conf.payUrl + iframeUrl;
    }

    loadIframe();

    var callback = function (data) {
        
        var id = data.id;

        switch(id){
            case "doChargeBtn":
                location.href = "/charge/center" + "?iframe=" + "info";
                break;
            case "doCashBtn":
                location.href = "/charge/center" + "?iframe=" + "info";
                break;
            case "transout":
                location.href = "/charge/center" + "?iframe=" + "info";
                break;

            //转账成功
            case "returnBtn":
                location.href = "/charge/center" + "?iframe=" + "info";
                break;

            case "changePhone":
                location.href = "/user/usersetting";
                break;

            case "loginTransfer":
                location.href = data.url;
                break;
        }

    }

    XD.receiveMessage(callback);

});