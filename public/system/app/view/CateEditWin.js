/**
 * 
 */
Ext.define('Topx.view.CateEditWin', {
	id : 'CateEditWin',
	extend : 'Ext.window.Window',
	modal : true,
	alias : 'widget.cateeditwin',
	height : 210,
	width : 540,
	iconCls : 'helome-app-edit',
	title : '新增',

	initComponent : function() {
		var me = this;

		Ext.applyIf(me, {
			items : [ {
				id : 'cateEditform',
				xtype : 'form',
				border : false,
				bodyPadding : 5,
				fieldDefaults : {
					labelWidth : 70
				},
				header : false,
				items : [ {
					xtype : 'fieldset',
					title : '信息表单',
					items : [{
						id : 'cateid',
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
						id : 'tagType',
						name : 'tagType',
						xtype : 'hiddenfield',
						value : 0
					}, {
						id : 'seq',
						name : 'seq',
						xtype : 'textfield',
						anchor : '100%',
						fieldLabel : '顺序',
						emptyText : '可不填默认为最后'
					}, {
                        xtype: 'filefield',
                        emptyText: '请选择图片',
                        fieldLabel: '上传图片',
                        name: 'certificate',
                        buttonText: '选择'
                    } ]
				} ],
				buttons : [ {
					id : 'cateeditwin-submit',
					iconCls : 'helome-save',
					text : '保存'
				}, {
					text : '关闭',
					handler : function() {
						this.close();
					},
					scope : this
				} ]
			} ]
		});

		me.callParent(arguments);
	}

});