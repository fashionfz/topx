Ext.define("Topx.store.ConfigKeyWordGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.ConfigKeyWordGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/config_key_word',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true
});