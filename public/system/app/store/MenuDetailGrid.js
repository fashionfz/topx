
Ext.define('Topx.store.MenuDetailGrid', {
	extend : 'Ext.data.Store',
	autoDestroy : true,
	autoLoad: false,
	model : 'Topx.model.MenuGrid',
	proxy : {
		type : 'ajax',
		url : '/system/get_child',
		reader: {
			type:'json',
			root:'data'
		}
	}
});
