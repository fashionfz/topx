/**
 * 
 */
Ext.define('Topx.model.TagGrid', {
	extend : 'Ext.data.Model',
	idProperty : 'id',
	fields : [ {
		name : "id",
		type : 'int'
	}, {
		name : "tagName",
		type : 'string'
	}, {
		name : "tagNameEn",
		type : 'string'
	}, {
		name : "hits",
		type : 'int'
	}, {
		name : "seq",
		type : 'string'
	}, {
		name : "tagType",
		type : 'string'
	}, {
		name : "industryName",
		type : 'string'
	}, {
		name : "industryId",
		type : 'int'
	}]
});
