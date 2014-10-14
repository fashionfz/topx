/*Ext.Ajax.request({
    url: '/system/user_url',
    method: 'GET',
    success: function (response, options) {
    	var jsonResult = Ext.decode(response.responseText);  
        Ext.define('Topx.store.VPTree', {
        	extend : 'Ext.data.TreeStore',
        	root : {
        		expanded : true,
        		children : [ {
        			id : 'bc',
        			text : "后台管理",
        			expanded : true,
        			children : jsonResult.data
        		} ]
        	}

        });
    },
    failure: function (response, options) {
        Ext.MessageBox.alert('失败', '请求超时或网络故障,错误编号：' + response.status);
    }
});*/

/**
 * 运维人员使用的功能
 */
Ext.define('Topx.store.VPTree', {
	extend : 'Ext.data.TreeStore',
	autoDestroy : true,
	autoLoad: true,
	fields:['id','text','tabxtype','leaf'],
	nodeParam : 'parentId',
	proxy : {
		type : 'ajax',
		reader : 'json',
		url : '/system/user_url'
	},
	root : {
	    text : '根节点',
	    id : '-1',
	    expanded : true
	}

});