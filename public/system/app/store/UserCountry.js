Ext.define("Topx.store.UserCountry", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.UserCountry',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/userCountry',
		reader: {
			type:'json',
			root:'data'
		}
	}
});