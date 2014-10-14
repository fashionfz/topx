/**
 * 
 */
Ext.define('Topx.model.CommentGrid', {
	extend : 'Ext.data.Model',
	idProperty : 'id',
	fields : [ {
		name : "id",
		type : 'int'
	},{
		name : "level",
		type : 'int'
	}, {
		name : "commentTime",
		type : 'string'
	}, {
		name : "commentUserId",
		type : 'long'
	}, {
		name : "commentUserName",
		type : 'string'
	}, {
		name : "toCommentUserId",
		type : 'long'
	}, {
		name : "toCommentUserName",
		type : 'string'
	}, {
		name : "content",
		type : 'string'
	}, {
		name : "stateFlag",
		type : 'int'
	}]
});
