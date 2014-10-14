Ext.define('Topx.model.IntroImgGrid', {
	extend: 'Ext.data.Model',
	fields: [
	    {name: 'id', type: 'int'}, 
	    {name: 'imgUrl', type: 'string'}, 
	    {name: 'uri', type: 'string'},
	    {name: 'seq',type: 'int'},
	    {name: 'device',type: 'string'}
	]
});