Ext.define('Topx.controller.AdminAuthEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'adminAuthEditWin',
		selector : 'adminautheditwin'
	}, {
		ref : 'adminGrid',
		selector : 'admingrid'
	} ],
	
	stores : [ 'AdminGrid' ],

	init : function() {
		this.control({
			'#adminautheditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		var xx = this.getAdminAuthEditWin().queryById('adminAuthEditForm').getForm();
		var roleIds="";
		var store = this.getAdminAuthEditWin().queryById("grid2").store;
		var sm = this.getAdminAuthEditWin().queryById("grid2").selModel;
		for(var i=0;i<store.getCount();i++){
			if(sm.isSelected(store.getAt(i))){
				roleIds+=store.getAt(i).data.id+",";
			}
		}
		var paras = xx.getValues("userName")+"&roleIds="+roleIds;
		
		this.getAdminAuthEditWin().queryById('adminAuthEditForm').submit(
		{
			clientValidation : true,
			submitEmptyText : false,
			url : '/system/auth_user?'+paras,
			method : 'GET',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getAdminAuthEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			failure : function(form, action) {
				this.getAdminAuthEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			scope : this
		});
	}

});
