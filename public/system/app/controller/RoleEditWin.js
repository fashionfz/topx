Ext.define('Topx.controller.RoleEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'roleEditWin',
		selector : 'roleeditwin'
	}, {
		ref : 'roleGrid',
		selector : 'rolegrid'
	} ],
	
	stores : [ 'RoleGrid' ],

	init : function() {
		this.control({
			'#roleeditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		this.getRoleEditWin().queryById('roleEditForm').submit(
		{
			clientValidation : true,
			submitEmptyText : false,
			url:'/system/add_role',
			method : 'POST',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getRoleGridStore().load();
				this.getRoleEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			failure : function(form, action) {
				this.getRoleEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			scope : this
		});
	}

});
