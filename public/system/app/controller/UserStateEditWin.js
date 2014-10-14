Ext.define('Topx.controller.UserStateEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'userStateEditWin',
		selector : 'userstateeditwin'
	}, {
		ref : 'userGrid',
		selector : 'usergrid'
	} ],
	
	stores : [ 'UserGrid' ],

	init : function() {
		this.control({
			'#userstateeditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		this.getUserStateEditWin().queryById('userStateEditForm').submit(
		{
			clientValidation : true,
			submitEmptyText : false,
			url : '/system/userState',
			method : 'POST',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getUserGridStore().load();
				this.getUserStateEditWin().close();
			},
			failure : function(form, action) {
//				Ext.Msg.alert('failure', '保存失败');
				Ext.Msg.alert('消息', action.result.msg);
			},
			scope : this
		});
	}

});
