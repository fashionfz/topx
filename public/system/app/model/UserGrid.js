Ext.define('Topx.model.UserGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'userId', type: 'int'}, 
	    {name: 'email', type: 'string'}, 
	    {name: 'userName', type: 'string'},
	    {name: 'gender',type: 'int'},
	    {name: 'country',type: 'string'},
	    {name: 'skillsTags',type: 'string'},
	    {name: 'tradeNum',type: 'int'},
	    {name: 'averageScore',type: 'string'},
	    {name: 'balance',type: 'string'},
	    {name: 'enable',type: 'boolean'},
	    {name: 'online',type: 'boolean'},
	    {name: 'complain',type: 'boolean'},
	    {name: 'top',type: 'boolean'},
	    {name: 'phoneNumber',type: 'string'},
	    {name: 'registerDate',type: 'string'},
	    {name: 'inTags',type: 'string'},
	    {name: 'topIndustryName',type: 'string'},
	    {name: 'onlineService',type: 'boolean'},
	    {name: 'onlineTranslation',type: 'boolean'},
	    {name: 'resumeStatus',type: 'string'}
	]
});