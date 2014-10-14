/**
 * 系统用户编辑
 */
Ext.define('Topx.view.AdminEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.admineditwin',
	height : 250,
	width : 500,
	iconCls : 'helome-app-edit',
	title : '系统用户编辑',
	modal : true,

	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'adminEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '系统用户表单',
					items : [ {
						id : 'id',
						name : 'id',
						xtype : 'hiddenfield'
					}, {
						id : 'userName',
						name : 'userName',
						xtype : 'field',
						labelWidth : 40,
						fieldLabel: '用户名'
					}, {
						id : 'password',
						name : 'password',
						xtype : 'field',
						inputType: "password",
						labelWidth : 40,
						fieldLabel: '密码'
					}, {
						id : 'email',
						name : 'email',
						xtype : 'field',
						labelWidth : 40,
						fieldLabel: '邮箱'
					}, {
						id : 'phoneNumber',
						name : 'phoneNumber',
						xtype : 'field',
						labelWidth : 40,
						fieldLabel: '电话'
					}]
				} ],
				buttons : [ {
					id: 'admineditwin-submit',
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