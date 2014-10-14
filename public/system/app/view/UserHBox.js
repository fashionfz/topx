/**
 * 
 */
Ext.define('Topx.view.UserHBox', {
    extend: 'Ext.Panel',
	alias : 'widget.userhbox',
    width: 500,
    height: 300,
    title: "用户账号管理",


	initComponent : function() {
		var me = this;
		
		Ext.applyIf(me, {
		    border : false,
			layout: {
		        type: 'hbox',
		        align: 'stretch'
		    },
			items :[{
		        xtype: 'usergrid',
		        title: '用户账号',
		        flex: 1
		    }]
		});
		
	    this.callParent();
	}
});