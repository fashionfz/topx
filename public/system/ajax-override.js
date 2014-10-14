/**
 * 该功能实现主要针对
 * Ext框架封装的对象进行重写覆盖。
 */

/**
 * ajax请求
 * author bin.deng
 * 动态封装的function导致原方法里调用的外部变量不可用
 * 所以提前单独执行方法，再独立封装一个提示的简单function作为回调函数
 * 如果不抛出，Ext.Msg.alert为单列，导致多个会覆盖问题
 * 如果抛出异常，导致方法不能正常运行，如遮罩不能隐藏等
 */
Ext.Ajax.on('requestcomplete',checkAuth, this);           
function checkAuth(conn, response, options, eOpts){  
	if(response.responseText!=''){
	    var json = Ext.decode(response.responseText);
	    if(json!=null){
	    	if(json.code == "503"){
	    		var fail = options['failure'];
	    		if(fail!=null){
		    		var str = fail.toString();
		    		var i = str.indexOf("afterAction");
		    		if(i<0){
		    			fail();//提交执行方法
		    		}
	    		}
	    		var newfn1 = "function success(response){var json = Ext.decode(response.responseText);Ext.Msg.alert('503',json.msg);}";
	    		eval(newfn1);
	    		var newfn2 = "function failure(response){var json = Ext.decode(response.responseText);Ext.Msg.alert('503',json.msg);}";
	    		eval(newfn2);
	    		options['success']=success;
	    		options['failure']=failure;
	    	}
	    }
	}
}

/**
 * JS AOP
 * author bin.deng
 * @param obj
 * @param handlers
 */
function actsAsAspect(obj,handlers) {
    if(typeof obj == 'function'){
        obj = obj.prototype;
    }
    
    for(var methodName in handlers){
        var _handlers = handlers[methodName];
        for(var handler in _handlers){
            if((handler == 'before' || handler == 'after') && typeof _handlers[handler] == 'function'){
                eval(handler)(obj,methodName,_handlers[handler]);
            }
        }
    }

    function before(obj,method,f) {
        var original = obj[method];   
        obj[method] = function() {
            f.apply(this, arguments); 
            return original.apply(this, arguments);   
        }
    }

    function after(obj,method, f) {   
        var original = obj[method];   
        obj[method] = function() {
        	original.apply(this, arguments);   
        	return f.apply(this, arguments);  
        }
    }
}
/**
 * 获取form submit提交的failure回调函数，增加权限验证逻辑代码
 * 动态封装的failure function导致原方法里调用的外部变量不可用
 * 保证failure尽量只做简单工作
 * @param obj
 */
function before(obj){
	var fn = arguments[0]['failure'];
	if(fn!=null){
		var str = fn.toString();
		var i=str.indexOf("function")+8;
		var newfn = str.substring(0,i+1)+" failure";
		var m=str.lastIndexOf('}');
		newfn += str.substring(i,m);
		newfn += "if(action.result.code == 503){Ext.Msg.alert('503',action.result.msg);throw new Error('503');}"
		newfn+=str.substring(m,str.length);
		eval(newfn);
		arguments[0]['failure']=failure;
	}else{
		var newfn = "function failure(form, action){if(action.result.code == 503){Ext.Msg.alert('503',action.result.msg);throw new Error('503');}}";
		eval(newfn);
		arguments[0]['failure']=failure;
	}
}

//ext form对象上的submit方法添加aop拦截
//actsAsAspect(Ext.form.Panel,{'submit':{before:before}});
