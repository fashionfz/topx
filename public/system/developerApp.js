//var cdnUrl = "http://192.168.0.100/topx"
//var cdnUrl = "http://172.16.4.96/topx";
var cdnUrl = "http://115.28.44.61/topx";
Ext.application({

	requires : [ 'Ext.container.Viewport' ],

	name: 'Topx',
	
	appFolder : cdnUrl+'/assets/system/app',
	
	models	: ['SysLogGrid','ConfigGrid'],

	stores	: ['DeveloperVPTree', 'SysLogGrid'],

	views	: ['DeveloperVPTree', 'SysLogGrid','ConfigGrid'],

	controllers : [ 'DeveloperVPTree', 'SysLogGrid'],

	launch : function() {
    	Ext.override(Ext.ToolTip, {
    		maxWidth :9999
    	});
    	
		Ext.create('Ext.container.Viewport', {
			layout : {
				type : 'border',
				padding : '0 5 5 5' // pad the layout from the window edges
			},
			items : [ {
				id : 'app-header',
				xtype : 'box',
				region : 'north',
				height : 40,
				html : '嗨啰应用管理平台'
			}, {
				xtype : 'container',
				region : 'center',
				layout : 'border',
				items : [ {
					xtype : 'DeveloperVPTree',
					region : 'west',
					animCollapse : true,
					width : 200,
					minWidth : 150,
					maxWidth : 400,
					split : true,
					collapsible : true
				}, {
					id : 'vptabpanel_vptabpanel',
					xtype : 'tabpanel',
					region : 'center',
					enableTabScroll : true,
					defaults : {
						autoScroll : true
					},
					items : []
				}]
			}]
		});
	}
});