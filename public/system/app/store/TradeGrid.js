Ext.define("Topx.store.TradeGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.TradeGrid',
	autoLoad: false,
	proxy:{
		type:'ajax',
		url:'/system/trade/search',
		reader: {
			type:'json',
			root:'data'
		}
	},
    remoteSort:true,
	pageSize:20
});