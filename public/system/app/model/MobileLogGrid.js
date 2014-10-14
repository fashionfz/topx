Ext.define('Topx.model.MobileLogGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'device', type: 'string'}, 
	    {name: 'createTime', type: 'date', convert:function(v,r){return new Date(v);}},
	    {name: 'description',type: 'string'},
	    {name: 'logFileUrl',type: 'string'}
	]
});