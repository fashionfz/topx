/**
 * 系统用户编辑
 */
Ext.define('Topx.view.MenuEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.menueditwin',
	height : 250,
	width : 500,
	iconCls : 'helome-app-edit',
	title : '功能菜单编辑',
	modal : true,
	constructor: function (config) {
        this.childStore = config.option;
        this.callParent();
    },
	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'childMenuEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '系统用户表单',
					items : [ {
						id : 'idMenuAdd',
						name : 'parentId',
						xtype : 'hiddenfield'
					}, {
						id : 'urlMenuAdd',
						name : 'url',
						xtype : 'field',
						labelWidth : 80,
						fieldLabel: 'url'
					}, {
						id : 'nameMenuAdd',
						name : 'name',
						xtype : 'field',
						labelWidth : 80,
						fieldLabel: '名称'
					}, {
						id : 'remarkMenuAdd',
						name : 'remark',
						xtype : 'field',
						labelWidth : 80,
						fieldLabel: '备注'
					}, {
						id : 'extIdMenuAdd',
						name : 'extId',
						xtype : 'field',
						labelWidth : 80,
						fieldLabel: 'extId'
					}, {
						id : 'tabxtypeMenuAdd',
						name : 'tabxtype',
						xtype : 'field',
						labelWidth : 80,
						fieldLabel: 'tabxtype'
					},{
						id : 'sortMenuAdd',
						name : 'sort',
						xtype : 'hiddenfield',
						labelWidth : 80,
						fieldLabel: '排序'
					}]
				} ],
				buttons : [ {
					id: 'menueditwin-submit',
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