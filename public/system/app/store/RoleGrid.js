Ext.define("Topx.store.RoleGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.RoleGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/get_role',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true
});