/**
 * @description: session存储模块
 * @author: zhiqiang.zhou@helome.com
 * @update:
 */
define('module/sessionStorage', ['module/cookie'], function(cookie){

    return {
        isSessionStorage: false,  //!!window.sessionStorage, // 是否支持sessionStorage

        // 设置数据
        set: function(key, value){
            if(this.isSessionStorage){
                window.sessionStorage.setItem(key, value);
            }else{
                cookie.set(key,value);
            }
        },
        // 获取数据
        get: function(key){
            var data = null;
            if(this.isSessionStorage){
                data = window.sessionStorage.getItem(key);
            }else{
                data = cookie.get(key);
            }
            return data;
        },
        // 删除数据
        remove: function(key){
            if(this.isSessionStorage){
                window.sessionStorage.removeItem(key);
            }else{
                cookie.del(key);
            }
        },
        //清除所有数据,请慎用
        clear: function(){
            if(this.isSessionStorage){
                window.sessionStorage.clear();
            }else{
                cookie.clearAllCookie();
            }
        }
    }
});