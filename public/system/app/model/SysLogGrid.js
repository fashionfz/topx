Ext.define('Topx.model.SysLogGrid', {
	extend: 'Ext.data.Model',
	fields: [
	 	    {name: 'id', type: 'int'},
	 	    {name: 'filename', type: 'string'},
	 	    {name: 'fileType',type: 'string'},
	 	    {name: 'fileSize', type: 'string'},
	 	    {name: 'status', type: 'int'},
	 	    {name: 'files', type: 'json'},
	 	    {name: 'createTime', type: 'string'},
	 	    {name: 'content',type: 'string'}
	 	]
});