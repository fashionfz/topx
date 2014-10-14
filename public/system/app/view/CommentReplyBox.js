/**
 * 
 */
Ext.define('Topx.view.CommentReplyBox', {
    extend: 'Ext.Panel',
	alias : 'widget.commentreplyBox',
    width: 500,
    height: 300,
    title: "评论和回复管理",


	initComponent : function() {
		var me = this;
		
		Ext.applyIf(me, {
		    border : false,
			layout: {
		        type: 'vbox',
		        align: 'stretch'
		    },
			items :[{
		        xtype: 'commentgrid',
		        title: '评价管理 — 点击某行查看对应的回复',
		        flex: 2
		    },{
		        xtype: 'replygrid',
		        title: '评价对应的回复列表',
		        flex: 2
		    }]
		});
		
	    this.callParent();
	}
});