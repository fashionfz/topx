Ext.define("Topx.store.OperateLogGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.OperateLogGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/operatelog',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});