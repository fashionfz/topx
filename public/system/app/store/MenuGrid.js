
Ext.define('Topx.store.MenuGrid', {
	extend : 'Ext.data.TreeStore',
	autoDestroy : true,
	autoLoad: false,
	fields:['id','text','leaf'],
	nodeParam : 'parentId',
	proxy : {
		type : 'ajax',
		reader : 'json',
		url : '/system/url_tree'
	},
	root : {
	    text : '根节点',
	    id : '-1',
	    expanded : false
	  }
});
