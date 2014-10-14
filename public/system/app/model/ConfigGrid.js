Ext.define('Topx.model.ConfigGrid', {
	extend: 'Ext.data.Model',
	fields: [
	 	{name: 'id', type: 'int'},
	    {name: 'property', type: 'string'}, 
	    {name: 'value', type: 'string'},
	    {name: 'introduce', type: 'string'}
	]
});