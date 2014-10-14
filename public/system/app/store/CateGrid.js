Ext.define("Topx.store.CateGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.CateGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/tag/queryind',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});