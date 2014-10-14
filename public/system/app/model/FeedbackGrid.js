Ext.define('Topx.model.FeedbackGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'},
	    {name: 'username', type: 'string'},
	    {name: 'expertName',type: 'string'},
	    {name: 'number', type: 'string'},
	    {name: 'status', type: 'int'},
	    {name: 'proofs', type: 'json'},
	    {name: 'createTime', type: 'string'},
	    {name: 'content',type: 'string'}
	]
});