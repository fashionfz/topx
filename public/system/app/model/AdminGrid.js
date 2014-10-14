Ext.define('Topx.model.AdminGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'userName', type: 'string'}, 
	    {name: 'password', type: 'string'},
	    {name: 'email',type: 'string'},
	    {name: 'phoneNumber',type: 'string'},
	    {name: 'loginDate',type: 'date'},
	    {name: 'loginIp',type: 'string'},
	    {name: 'remark',type: 'string'},
	    {name: 'encryptUUID',type: 'string'},
	    {name: 'roleType',type: 'int'}
	]
});