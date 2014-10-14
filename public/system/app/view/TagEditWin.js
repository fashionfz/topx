/**
 * 
 */
Ext.define('Topx.view.TagEditWin', {
	id : 'TagEditWin',
	extend : 'Ext.window.Window',
	modal : true,
	alias : 'widget.tageditwin',
	height : 230,
	width : 540,
	iconCls : 'helome-app-edit',
	title : '新增',

	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'tagEditform',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				fieldDefaults : {
					labelWidth : 70
				},
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '信息表单',
					items : [ {
						id : 'tagid',
						name : 'id',
						xtype : 'hiddenfield'
					}, {
						id : 'tagName',
						name : 'tagName',
						xtype : 'textfield',
						anchor : '100%',
						fieldLabel : '名称',
						allowBlank : false,
						tooltip : '请输入标签名称！',
						emptyText : '请输入标签名称！'
					}, {
						id : 'tagNameEn',
						name : 'tagNameEn',
						xtype : 'textfield',
						anchor : '100%',
						fieldLabel : '英文名称',
						allowBlank : false,
						tooltip : '请输入标签名称！',
						emptyText : '请输入标签名称！'
					}, {
						id : 'industryId',
						name : 'industryId',
						xtype : 'combobox',
						anchor : '100%',
						fieldLabel : '行业',
						store : new Ext.data.Store({
							fields : [ 'id', 'tagName' ],
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
					}, {
						id : 'tagType',
						name : 'tagType',
						xtype : 'hiddenfield',
						value : 1
					}, {
						id : 'seq',
						name : 'seq',
						xtype : 'textfield',
						anchor : '100%',
						fieldLabel : '顺序',
						emptyText : '可不填默认为最后'
					} ]
				} ],
				buttons : [ {
					id : 'tageditwin-submit',
					iconCls : 'helome-save',
					text : '保存'
				}, {
					text : '关闭',
					handler : function() {
						this.close();
					},
					scope : this
				} ]
			}]
		});

		me.callParent(arguments);
	}

});