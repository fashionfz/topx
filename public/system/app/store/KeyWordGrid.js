Ext.define("Topx.store.KeyWordGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.KeyWordGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/keyword',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});