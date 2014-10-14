/**
 * 
 */
Ext.define('Topx.view.FeedbackStateEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.feedbackstateeditwin',
	height : 270,
	width : 400,
	iconCls : 'helome-app-edit',
	title : '编辑',
	modal : true,

	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'feedbackStateEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '投诉详情',
					items : [ {
						id : 'feedbackId',
						name : 'feedbackId',
						xtype : 'hiddenfield'
					}, {
						id : 'usernameDisplay',
						name : 'usernameDisplay',
						xtype : 'displayfield',
						labelWidth : 80,
						fieldLabel: '投诉者'
					}, {
						id : 'expertNameDisplay',
						name : 'expertNameDisplay',
						xtype : 'displayfield',
						labelWidth : 80,
						fieldLabel: '被投诉专家'
					}, {
						id : 'numberDisplay',
						name : 'numberDisplay',
						xtype : 'displayfield',
						labelWidth : 80,
						fieldLabel: '交易号'
					}, {
						id : 'createTimeDisplay',
						name : 'createTimeDisplay',
						xtype : 'displayfield',
						labelWidth : 80,
						fieldLabel: '投诉时间'
					}, {//处理状态
		                	id: 'handleState',
							name: 'handleState',
		                	xtype: 'combobox',
		                	labelWidth: 80,
		                	fieldLabel: '处理状态',
		                	width: 160,
		                    store: [
		                        ['0', '未处理'],
		                        ['1', '已处理']
		                    ],
		                    editable: false,
		                    queryMode: 'local'
		                }]
					}]
				} ],
				buttons : [ {
					id: 'feedbackstateeditwin-submit',
					text : '保存'
				}, {
					text : '关闭',
					handler : function() {
						this.close();
					},
					scope : this
				}]
		});

		me.callParent(arguments);
	}

});