Ext.define('Topx.controller.MenuEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'menuEditWin',
		selector : 'menueditwin'
	}, {
		ref : 'menuGrid',
		selector : 'menugrid'
	} ],
	
	stores : [ 'MenuGrid' ],

	init : function() {
		this.control({
			'#menueditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		var childStore = this.getMenuEditWin().childStore;
		this.getMenuEditWin().queryById('childMenuEditForm').submit(
		{
			clientValidation : true,
			submitEmptyText : false,
			url:'/system/add_menu',
			method : 'POST',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getMenuGridStore().load();
				childStore.load();
				this.getMenuEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			failure : function(form, action) {
				this.getMenuEditWin().close();
				Ext.Msg.alert('消息', action.result.msg);
			},
			scope : this
		});
	}

});
