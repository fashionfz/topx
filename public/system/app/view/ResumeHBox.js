/**
 * 
 */
Ext.define('Topx.view.ResumeHBox', {
    extend: 'Ext.Panel',
	alias : 'widget.resumehbox',
    width: 500,
    height: 300,
    title: "海外简历管理",


	initComponent : function() {
		var me = this;
		
		Ext.applyIf(me, {
		    border : false,
			layout: {
		        type: 'hbox',
		        align: 'stretch'
		    },
			items :[{
		        xtype: 'resumegrid',
		        title: '海外简历',
		        flex: 1
		    }]
		});
		
	    this.callParent();
	}
});