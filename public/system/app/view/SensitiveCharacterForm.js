/**
 * 敏感字符 Form
 */
Ext.define('Topx.view.SensitiveCharacterForm', {
	id : 'sensitiveCharacterForm',
	extend : 'Ext.form.Panel',
	bodyPadding : 10,
	alias : 'widget.sensitiveCharacterForm',
	title : '敏感字符',
	buttonAlign : 'center',

	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			items : [ {
				xtype : 'fieldset',
				title : '按照英文逗号或者空格间隔填入',
				items : [ {
					name : 'id',
					xtype : 'hiddenfield'
				}, {
					name : 'words',
					xtype : 'textareafield',
					anchor : '100%',
					height : 250,
					width : 378,
					fieldLabel : '敏感字符'
				} ]
			} ],
			dockedItems : [ me.createTopToolbar() ]
		});
		me.callParent(arguments);
		me.getForm().load({
		    url: '/system/sensitiveWords/get',
		    method : 'GET',
		    failure: function(form, action) {
		    	if(action.result.success==false){
		    		Ext.Msg.alert("数据加载失败", action.result.msg);
		    	}
		    }
		});
	},

	createTopToolbar : function() {
		var thiz = this;
		this.toolbar = Ext.create('widget.toolbar', {
			items : [{
				id : 'sensitiveCharacter-save',
				iconCls : 'helome-save',
				text : '保存敏感字符',
				scope : this,
				handler : function() {
					var form = thiz.form;
					form.submit({
						submitEmptyText : false,
						url : '/system/sensitiveWords/update',
						method : 'POST',
						waitTitle : "提示",
						waitMsg : '正在提交数据，请稍后 ……',
						success : function(form, action) {
							form.setValues(action.result);
						},
						failure : function(form, action) {
							Ext.Msg.alert('保存失败', action.result.msg);
						},
						scope : this
					});
				}
			}]
		});
		return this.toolbar;
	}

});