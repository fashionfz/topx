/**
 * 
 */
Ext.define('Topx.view.TagHBox', {
    extend: 'Ext.Panel',
	alias : 'widget.taghbox',
    width: 500,
    height: 300,
    title: "服务标签管理",


	initComponent : function() {
		var me = this;
		
		Ext.applyIf(me, {
		    border : false,
			layout: {
		        type: 'hbox',
		        align: 'stretch'
		    },
			items :[{
		        xtype: 'categrid',
		        title: '行业管理',
		        flex: 0.8
		    },{
		        xtype: 'tageditgrid',

		        title: '行业标签管理',
		        flex: 2
		    }]
		});
		
	    this.callParent();
	}
});