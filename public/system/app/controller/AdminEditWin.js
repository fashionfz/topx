Ext.define('Topx.controller.AdminEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'adminEditWin',
		selector : 'admineditwin'
	}, {
		ref : 'adminGrid',
		selector : 'admingrid'
	} ],
	
	stores : [ 'AdminGrid' ],

	init : function() {
		this.control({
			'#admineditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		this.getAdminEditWin().queryById('adminEditForm').submit(
		{
			
			clientValidation : true,
			submitEmptyText : false,
			url : '/system/add_admin',
			method : 'POST',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getAdminGridStore().load();
				this.getAdminEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			failure : function(form, action) {
				this.getAdminEditWin().close();
				Ext.Msg.alert('消息', '操作失败');
			},
			scope : this
		});
	}

});
