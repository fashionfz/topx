Ext.define('Topx.model.MenuGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'url', type: 'string'}, 
	    {name: 'name', type: 'string'},
	    {name: 'parentId',type: 'int'},
	    {name: 'sort',type: 'int'},
	    {name: 'path',type: 'string'},
	    {name: 'remark',type: 'string'},
	    {name: 'extId',type: 'string'},
	    {name: 'tabxtype',type: 'string'},
	    {name: 'leaf',type: 'boolean'},
	    {name: 'errorCode',type: 'string'}
	]
});