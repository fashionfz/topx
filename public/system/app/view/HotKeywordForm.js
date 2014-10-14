/**
 * 
 */
Ext.define('Topx.view.HotKeywordForm', {
	id : 'hotKeywordForm',
	extend : 'Ext.form.Panel',
	bodyPadding : 10,
	alias : 'widget.hotkeywordform',
	title : '热门关键词',
	buttonAlign : 'center',

	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			items : [ {
				xtype : 'fieldset',
				title : '按照英文逗号或者空格间隔填入',
				items : [ {
					id : 'id',
					name : 'id',
					xtype : 'hiddenfield'
				}, {
					id : 'words',
					name : 'words',
					xtype : 'textareafield',
					anchor : '100%',
					height : 250,
					width : 378,
					fieldLabel : '热门关键词'
				} ]
			} ],
			dockedItems : [ me.createTopToolbar() ]
		});
		me.callParent(arguments);
		me.getForm().load({
		    url: '/system/k/kget',
		    method : 'GET',
		    failure: function(form, action) {
		        Ext.Msg.alert("Load failed", action.result.errorMessage);
		    }
		});
	},

	createTopToolbar : function() {
		var thiz = this;
		this.toolbar = Ext.create('widget.toolbar', {
			items : [{
				id : 'keyword-save',
				iconCls : 'helome-save',
				text : '保存关键词',
				scope : this,
				handler : function() {
					var form = thiz.form;
					form.submit({
						submitEmptyText : false,
						url : '/system/k/ku',
						method : 'POST',
						waitTitle : "提示",
						waitMsg : '正在提交数据，请稍后 ……',
						success : function(form, action) {
							form.setValues(action.result);
						},
						failure : function(form, action) {
							Ext.Msg.alert('消息', action.result.msg);
						},
						scope : this
					});
				}
			}]
		});
		return this.toolbar;
	}

});