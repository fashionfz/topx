Ext.define('Topx.model.CateGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'},
	    {name: 'tagName', type: 'string'},
	    {name: 'tagNameEn', type: 'string'},
	    {name: 'attachInfos', type: 'json'}
	]
});