Ext.define("Topx.store.SysLogGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.SysLogGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/sysLog/list',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});