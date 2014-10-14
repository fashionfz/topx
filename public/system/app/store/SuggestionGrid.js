Ext.define("Topx.store.SuggestionGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.SuggestionGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/suggestion',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});