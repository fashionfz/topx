Ext.define("Topx.store.OperateModule", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.OperateModule',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/operateModule',
		reader: {
			type:'json',
			root:'data'
		}
	}
});