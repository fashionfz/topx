Ext.define('Topx.controller.TagEditWin', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'tagEditWin',
		selector : 'tageditwin'
	}, {
		ref : 'cateEditWin',
		selector : 'cateeditwin'
	} ],

	init : function() {
		this.control({
			'#tageditwin-submit' : {
				click : this.onSubmitForm
			},
			'#cateeditwin-submit' : {
				click : this.onSubmitCateForm
			}
		});
	},

	onSubmitForm : function(selModel, selection) {
		var win = this.getTagEditWin();
		var form = win.queryById('tagEditform');
		var tagName = form.getForm().findField("tagName").value;
		var trimTagName = Ext.util.Format.trim(tagName);
		if( trimTagName == ''){
			Ext.Msg.alert('消息', '标签名称不能为空！');
			return;
		}
		if (form.isValid()) {
			form.submit({
				submitEmptyText : false,
				url : '/system/tag/saveOrUpdate',
				method : 'POST',
				waitTitle : "提示",
				waitMsg : '正在提交数据，请稍后 ……',
				success : function(form, action) {
					form.setValues(action.result.data);
					Ext.getCmp('tageditgrid_1').store.reload();
					win.close();
				},
				failure : function(form, action) {
					Ext.Msg.alert('消息', action.result.msg);
				},
				scope : this
			});
		}
	},
	
	onSubmitCateForm : function(selModel, selection) {
		var win = this.getCateEditWin();
		var form = win.queryById('cateEditform');
		var tagName = form.getForm().findField("tagName").value;
		var trimTagName = Ext.util.Format.trim(tagName);
		if( trimTagName == ''){
			Ext.Msg.alert('消息', '行业名称不能为空！');
			return;
		}
		if (form.isValid()) {
			form.submit({
				submitEmptyText : false,
				url : '/system/tag/saveOrUpdate',
				method : 'POST',
				waitTitle : "提示",
				waitMsg : '正在提交数据，请稍后 ……',
				success : function(form, action) {
					form.setValues(action.result.data);
					Ext.getCmp('cateGrid').store.reload();
					win.close();
				},
				failure : function(form, action) {
					Ext.Msg.alert('消息', action.result.msg);
				},
				scope : this
			});
		}
	}

});
