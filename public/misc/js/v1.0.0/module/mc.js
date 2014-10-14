/**
 * 消息中心相关
 * User: Rocs Zhang
 * Date: 14-8-8
 * Time: 下午5:07
 */
ued_conf.MC = {
    mcSever: '172.16.4.200:8080', //消息中心开发
    mcRegion: '1'  //系统组编号
};
ued_conf.MC.mcApi =  ued_conf.MC.mcSever ? "http://" + ued_conf.MC.mcSever + "/getMCServer" : "";
define('module/mc', [
    'common/util'
], function(util){
    return {
        /**
         * 初始化配置
         */
        initConfig : function(){
            if(!ued_conf.mcPath) {
                if(ued_conf.MC.mcApi){
                    util.setAjax(ued_conf.MC.mcApi, {groupId : ued_conf.MC.mcRegion}, function(json){
                        if(json.error){
                            $.alert('访问消息中心[M]出错 : ' + json.error);
                        }else if(json.addrIp && json.wsPort){
                            ued_conf.mcPath = "ws://" + json.addrIp + ":" + json.wsPort;
                        }
                    },function(){
                        $.alert('消息中心[M]繁忙，请稍后再试。');
                    }, 'GET', true);
                }else{
                    $.alert('无可访问的消息中心[M]地址.');
                }
            }
            if(ued_conf.mcPath){
                console.debug("[MC]" + ued_conf.mcPath + " ...");
            }else{
                $.alert('无可访问的消息中心地址.');
            }
        }
    }
});