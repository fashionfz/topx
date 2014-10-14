/**
 * 系统用户编辑
 */
Ext.define('Topx.view.RoleEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.roleeditwin',
	height : 170,
	width : 500,
	iconCls : 'helome-app-edit',
	title : '系统角色编辑',
	modal : true,

	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'roleEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '系统角色 表单',
					items : [ {
						id : 'id',
						name : 'id',
						xtype : 'hiddenfield'
					}, {
						id : 'name',
						name : 'name',
						xtype : 'field',
						labelWidth : 40,
						fieldLabel: '名称'
					}, {
						id : 'remark',
						name : 'remark',
						xtype : 'field',
						labelWidth : 40,
						fieldLabel: '备注'
					}]
				} ],
				buttons : [ {
					id: 'roleeditwin-submit',
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