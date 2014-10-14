Ext.define("Topx.store.FeedbackGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.FeedbackGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/feedback',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});