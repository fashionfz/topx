Ext.define('Topx.model.ResumeGrid', {
	extend: 'Ext.data.Model',
	idProperty : 'id',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'userId', type: 'int'}, 
	    {name: 'email', type: 'string'}, 
	    {name: 'avatarUrl', type: 'string'},
	    {name: 'source',type: 'string'},
	    {name: 'sourceResume',type: 'string'},
	    {name: 'sourceResumeUrl',type: 'string'},
	    {name: 'translationResume',type: 'string'},
	    {name: 'status',type: 'string'},
	    {name: 'createDate',type: 'string'},
	    {name: 'translateDate',type: 'string'}
	]
});