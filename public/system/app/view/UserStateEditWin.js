/**
 * 
 */
Ext.define('Topx.view.UserStateEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.userstateeditwin',
	height : 280,
	width : 500,
	iconCls : 'helome-app-edit',
	title : '修改用户状态',
	modal : true,

	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'userStateEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '用户状态表单',
					items : [ {
						id : 'userId',
						name : 'userId',
						xtype : 'hiddenfield'
					}, {
						id : 'userIdDisplay',
						name : 'userIdDisplay',
						xtype : 'displayfield',
						labelWidth : 40,
						fieldLabel: '用户Id'
					}, {
						id : 'emailDisplay',
						name : 'emailDisplay',
						xtype : 'displayfield',
						labelWidth : 40,
						fieldLabel: '邮箱'
					}, {
						id : 'userNameDisplay',
						name : 'userNameDisplay',
						xtype : 'displayfield',
						labelWidth : 40,
						fieldLabel: '姓名'
					}, {
						layout : 'column',
						border : false,
						padding : '0 0 5 0',
						items : [ {//禁用状态
							id: 'onlineService',
							name: 'onlineService',
		                	xtype: 'combobox',
		                	fieldLabel: '是否嗨啰在线客服',
		                	labelWidth: 110,
		                	width: 230,
		                    store: [
		                        ['0', '否'],
		                        ['1', '是']
		                    ],
		                    editable: false,
		                    queryMode: 'local'
		                }]
					}, {
						layout : 'column',
						border : false,
						padding : '0 0 5 0',
						items : [ {//禁用状态
							id: 'onlineTranslation',
							name: 'onlineTranslation',
		                	xtype: 'combobox',
		                	fieldLabel: '是否嗨啰在线翻译',
		                	labelWidth: 110,
		                	width: 230,
		                    store: [
		                        ['0', '否'],
		                        ['1', '是']
		                    ],
		                    editable: false,
		                    queryMode: 'local'
		                }]
					}, {
						layout : 'column',
						border : false,
						padding : '0 0 5 0',
						items : [ {//禁用状态
							id: 'disableState',
							name: 'disableState',
		                	xtype: 'combobox',
		                	/*fieldLabel: '状态(禁用|在线|投诉)',*/
		                	fieldLabel: '账号状态',
		                	labelWidth: 110,
		                	width: 230,
		                    store: [
		                        ['0', '禁用'],
		                        ['1', '正常']
		                    ],
		                    editable: false,
		                    queryMode: 'local'
		                }/*, {//在线状态
		                	id: 'onlineState',
							name: 'onlineState',
		                	xtype: 'combobox',
		                	width: 100,
		                    store: [
		                        ['0', '不在线'],
		                        ['1', '在线']
		                    ],
		                    editable: false,
		                    queryMode: 'local',
		                    margin: '0 0 0 5'
		                }, {//投诉状态
		                	id: 'complainState',
							name: 'complainState',
		                	xtype: 'combobox',
		                	width: 100,
		                    store: [
		                        ['0', '未被投诉'],
		                        ['1', '被投诉']
		                    ],
		                    editable: false,
		                    queryMode: 'local',
		                    margin: '0 0 0 5'
		                }*/]
					}]
				} ],
				buttons : [ {
					id: 'userstateeditwin-submit',
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