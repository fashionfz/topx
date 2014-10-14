Ext.define('Topx.model.ConfigKeyWordGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'key', type: 'string'},
	    {name: 'value', type: 'string'},
	    {name: 'remark', type: 'string'}
	]
});