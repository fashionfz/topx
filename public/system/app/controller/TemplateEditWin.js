Ext.define('Topx.controller.TemplateEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'templateEditWin',
		selector : 'templateeditwin'
	}, {
		ref : 'templateGrid',
		selector : 'templategrid'
	} ],
	
	stores : [ 'TemplateGrid' ],

	init : function() {
		this.control({
			'#templateeditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		this.getTemplateEditWin().queryById('templateEditForm').submit(
		{
			clientValidation : true,
			submitEmptyText : false,
			url : '/system/save_template',
			method : 'POST',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getTemplateGridStore().load();
				this.getTemplateEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			failure : function(form, action) {
				this.getTemplateEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			scope : this
		});
	}

});
