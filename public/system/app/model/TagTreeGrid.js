/**
 * 
 */
Ext.define('Topx.model.TagTreeGrid', {
	extend : 'Ext.data.Model',
	idProperty : 'id',
	fields : [ {
		name : "id",
		type : 'int',
		convert : undefined
	}, {
		name : "tagName",
		convert : undefined
	}, {
		name : "tagEnName",
		convert : undefined
	}, {
		name : "parentTagName",
		type : 'string',
		convert : undefined
	}, {
		name : "parentId",
		type : 'int',
		convert : undefined
	}, {
		name : "hits",
		type : 'int',
		convert : undefined
	}, {
		name : "seq",
		type : 'int',
		convert : undefined
	}, {
		name : 'expandable',
		type : 'bool',
		defaultValue : true,
		persist : false
	}, {
		name : "tagType",
		type : 'string',
		convert : undefined
	}, {
		name : "industryName",
		type : 'string',
		convert : undefined
	}, {
		name : "industryId",
		type : 'int',
		convert : undefined
	}]
});
