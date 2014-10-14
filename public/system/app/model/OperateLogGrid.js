Ext.define('Topx.model.OperateLogGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'operator', type: 'string'},
	    {name: 'operateTime', type: 'string'}, 
	    {name: 'operateIp', type: 'string'}, 
	    {name: 'menuName', type: 'string'}, 
	    {name: 'paramters',type: 'string'},
	    {name: 'result',type: 'string'},
	    {name: 'describle',type: 'string'}
	]
});