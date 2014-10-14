Ext.define('Topx.controller.ConfigKeyWordEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'configKeyWordEditWin',
		selector : 'configkeywordeditwin'
	}, {
		ref : 'ConfigKeyWordGrid',
		selector : 'configkeywordgrid'
	} ],
	
	stores : [ 'ConfigKeyWordGrid' ],

	init : function() {
		this.control({
			'#configkeywordeditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		this.getConfigKeyWordEditWin().queryById('configKeyWordEditForm').submit(
		{
			clientValidation : true,
			submitEmptyText : false,
			url : '/system/saveKeyWordConfig',
			method : 'POST',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getConfigKeyWordGridStore().load();
				this.getConfigKeyWordEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			failure : function(form, action) {
				this.getConfigKeyWordEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			scope : this
		});
	}

});
