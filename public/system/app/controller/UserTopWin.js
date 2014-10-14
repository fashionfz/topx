Ext.define('Topx.controller.UserTopWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'userTopWin',
		selector : 'usertopwin'
	}, {
		ref : 'userGrid',
		selector : 'usergrid'
	} ],
	
	stores : [ 'UserGrid' ],

	init : function() {
		this.control({
			'#usertopwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		var win = this.getUserTopWin();
		var form = win.queryById('userTopForm');
		var userIndustry = form.getForm().findField("userIndustry").value;
		if (userIndustry == null||userIndustry.length==0) {
			Ext.Msg.alert('消息', '行业不能为空！');
			return;
		}
		if (form.isValid()) {
			form.submit(
			{
//				clientValidation : true,
				submitEmptyText : false,
				url : '/system/user/top',
				method : 'POST',
				waitTitle : "提示",
				waitMsg : '正在提交数据，请稍后 ……',
				success : function(form, action) {
					this.getUserGridStore().load();
					win.close();
				},
				failure : function(form, action) {
//					Ext.Msg.alert('failure', '保存失败');
					Ext.Msg.alert('消息', action.result.errorMsg);
				},
				scope : this
			});
		}
	}

});
