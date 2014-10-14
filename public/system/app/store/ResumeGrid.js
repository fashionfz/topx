Ext.define("Topx.store.ResumeGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.ResumeGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/resume/list',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});