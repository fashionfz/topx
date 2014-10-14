Ext.define('Topx.model.TemplateGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'name', type: 'string'},
	    {name: 'type', type: 'int'},
	    {name: 'context', type: 'string'},
	    {name: 'remark', type: 'string'}
	]
});