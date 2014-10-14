Ext.define('Topx.view.FeedbackPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.feedbackpanel',
    title: '用户建议',
    activeTab: 0,
    
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
            items: [
            /*{
               xtype: 'feedbackgrid'     	
            },*/
            {
               xtype: 'suggestiongrid'     	
            }
        ]
        });
        me.callParent(arguments);
    }
});