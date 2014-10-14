Ext.define('Topx.model.TradeGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'createTime', type: 'string'}, 
	    {name: 'tradeStatus', type: 'string'},
	    {name: 'number',type: 'string'},
	    {name: 'expertName',type: 'string'},
	    {name: 'expertUserId',type: 'int'},
	    {name: 'userName',type: 'string'},
	    {name: 'userId',type: 'int'},
	    {name: 'serviceTime',type: 'string'},
	    {name: 'duration',type: 'string'},
	    {name: 'talkTime',type: 'string'}
	]
});