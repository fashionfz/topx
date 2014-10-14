/**
 * 用户置顶表单
 */
Ext.define('Topx.view.UserTopWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.usertopwin',
	height : 250,
	width : 500,
	iconCls : 'helome-app-edit',
	title : '置顶用户',
	modal : true,

	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'userTopForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '用户置顶表单',
					items : [ {
						id : 'userId',
						name : 'userId',
						xtype : 'hiddenfield'
					}, {
						id : 'userIdDisplay',
						name : 'userIdDisplay',
						xtype : 'displayfield',
						labelWidth : 35,
						fieldLabel: '用户Id',
						hidden:true
					}, {
						id : 'emailDisplay',
						name : 'emailDisplay',
						xtype : 'displayfield',
						labelWidth : 35,
						fieldLabel: '邮箱'
					}, {
						id : 'userNameDisplay',
						name : 'userNameDisplay',
						xtype : 'displayfield',
						labelWidth : 35,
						fieldLabel: '姓名'
					}, {
						id : 'inTagsDisplay',
						name : 'inTagsDisplay',
						xtype : 'displayfield',
						labelWidth : 95,
						fieldLabel: '用户选择的行业'
					}, {
						id : 'userIndustry',
						name : 'industryId',
						xtype : 'combobox',
						layout : 'column',
						border : false,
						padding : '0 0 5 0',
						fieldLabel : '置顶到的行业',
						labelWidth : 85,
						store : new Ext.data.Store({
							fields : [ 'id', 'tagName' ],
							width: 230,
							proxy : {
								type : 'ajax',
								reader : 'json',
								url : '/system/tag/queryind?type=industry'
							},
							autoLoad : true
						}),
						queryMode : 'local',
						displayField : 'tagName',
						valueField : 'id'
					}]
				} ],
				buttons : [ {
					id: 'usertopwin-submit',
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