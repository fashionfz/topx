/**
 * 系统用户编辑
 */
Ext.define('Topx.view.ConfigKeyWordEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.configkeywordeditwin',
	height : 150,
	width : 500,
	iconCls : 'helome-app-edit',
	title : '关键字统计配置',
	modal : true,
	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'configKeyWordEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '关键字统计配置',
					items : [ {
						id : 'configId',
						name : 'id',
						xtype : 'hiddenfield'
					}, {
						id : 'configValue',
						name : 'value',
						xtype : 'field',
						labelWidth : 40,
						fieldLabel: '内容'
					}]
				} ],
				buttons : [ {
					id: 'configkeywordeditwin-submit',
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