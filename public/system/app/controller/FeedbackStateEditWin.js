Ext.define('Topx.controller.FeedbackStateEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'feedbackStateEditWin',
		selector : 'feedbackstateeditwin'
	}, {
		ref : 'FeedbackGrid',
		selector : 'feedbackgrid'
	} ],
	
	stores : [ 'FeedbackGrid' ],

	init : function() {
		this.control({
			'#feedbackstateeditwin-submit' : {
				click : this.onSubmitForm
			}
		});

	},
	
	onSubmitForm : function(selModel, selection) {
		this.getFeedbackStateEditWin().queryById('feedbackStateEditForm').submit(
		{
			clientValidation : true,
			submitEmptyText : false,
			url : '/system/feedbackstatus',
			method : 'POST',
			waitTitle : "提示",
			waitMsg : '正在提交数据，请稍后 ……',
			success : function(form, action) {
				this.getFeedbackGridStore().load();
				this.getFeedbackStateEditWin().close();
			},
			failure : function(form, action) {
				Ext.Msg.alert('failure', '保存失败');
			},
			scope : this
		});
	}

});
