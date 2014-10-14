Ext.define('Topx.model.SuggestionGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'},
	    {name: 'userName', type: 'string'},
	    {name: 'phone', type: 'string'},
	    {name: 'email', type: 'string'},
	    {name: 'qq', type: 'string'},
	    {name: 'createTime', type: 'string'},
	    {name: 'content', type: 'string'},
	    {name: 'attachInfos', type: 'json'},
	    {name: 'href', type: 'string'}
	]
});