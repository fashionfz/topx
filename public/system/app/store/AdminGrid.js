Ext.define("Topx.store.AdminGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.AdminGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/get_admin',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true
});