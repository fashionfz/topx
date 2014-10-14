Ext.define('Topx.controller.RoleAuthEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'roleAuthEditWin',
		selector : 'roleautheditwin'
	}, {
		ref : 'roleGrid',
		selector : 'rolegrid'
	} ],
	
	stores : [ 'RoleGrid' ],

	init : function() {
		this.control({
			'#roleautheditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		var form = this.getRoleAuthEditWin().queryById('roleAuthEditForm').getForm();
		
		var checkedNodes = this.getRoleAuthEditWin().queryById('menuTree').getChecked();//tree必须事先创建好.
		var menuIds = "";
		for(var i=0;i<checkedNodes.length;i++){
			menuIds+=checkedNodes[i].data.id+","
		}
		var paras = form.getValues("id")+"&menuIds="+menuIds;
		
		this.getRoleAuthEditWin().queryById('roleAuthEditForm').submit(
		{
			clientValidation : true,
			submitEmptyText : false,
			url : '/system/auth_role?'+paras,
			method : 'GET',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getRoleGridStore().load();
				this.getRoleAuthEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			failure : function(form, action) {
				this.getRoleAuthEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			scope : this
		});
	}

});
