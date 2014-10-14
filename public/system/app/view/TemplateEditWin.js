/**
 * 系统用户编辑
 */
Ext.define('Topx.view.TemplateEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.templateeditwin',
	height : 500,
	width : 800,
	iconCls : 'helome-app-edit',
	title : '邮件模板编辑',
	modal : true,
	
	constructor: function (config) {
		this.templateId = config.templateId;
        this.content = config.content;
        this.callParent();
    },


	initComponent : function() {
		var me = this;
		Ext.tip.QuickTipManager.init();
		var editor = Ext.create('Ext.panel.Panel',{
		    title: false,
		    width: 770,
		    height: 410,
		    frame: false,
		    layout: 'fit',
		    items: {
		    	id : 'content',
		    	name : 'content',
		        xtype: 'htmleditor',
		        enableColors: true,
		        enableAlignments: true,
		        value : me.content,
		        plugins: [
		                  Ext.create('widget.html_HtmlEditorVariable',{option: me.templateId})
		                   //Ext.create('Topx.override.HtmlEditorImage')
		               ]
		    }
		});
		
		
		Ext.applyIf(me, {
			items : [ {
				id : 'templateEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [{
					id : 'templateId',
					name : 'templateId',
					xtype : 'hiddenfield',
					value : me.templateId
		         },editor],
				buttons : [ {
					id: 'templateeditwin-submit',
					text : '保存'
				}, {
					text : '关闭',
					handler : function() {
						this.close();
					},
					scope : this
				}]
			}]
		});

		me.callParent(arguments);
	}

});