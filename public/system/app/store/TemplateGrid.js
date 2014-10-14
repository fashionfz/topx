Ext.define("Topx.store.TemplateGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.TemplateGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/get_template',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true
});