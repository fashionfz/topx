Ext.define("Topx.store.UserGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.UserGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/user',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});