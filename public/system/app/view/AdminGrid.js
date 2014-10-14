Ext.define('Topx.view.AdminGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.admingrid',
	title : '后台用户管理',
	store : 'AdminGrid',
	loadMask : true,
	forceFit : true, //forceFit 属性: 表示除了auto expand（自动展开）外，还会对超出的部分进行缩减，让每一列的尺寸适应GRID的宽度大小，阻止水平滚动条的出现。
	autoScroll:true,
	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			columns : [ {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'id',
				text : '用户Id',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'userName',
				text : '用户名称',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'password',
				text : '密码',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'email',
				text : '邮箱',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'phoneNumber',
				text : '电话',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'loginDate',
				text : '登录时间',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'loginIp',
				text : '登录IP',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'encryptUUID',
				text : '加密',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'roleType',
				text : '用户类型',
				width : 80
			},{
				xtype : 'actioncolumn',
				width : 30,
				text : '授权',
				items : [ {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/edit_task.png",
					tooltip : '授权',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var win = Ext.create('widget.adminautheditwin',{option: rec.data.userName});
						var form = win.queryById('adminAuthEditForm').getForm();
						var formData = {};
						formData.userName = rec.data.userName;
						form.setValues(formData);
						win.show();
					}
				}]
			},{
				xtype : 'actioncolumn',
				width : 30,
				text : '编辑',
				items : [ {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/edit_task.png",
					tooltip : '编辑',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var win = Ext.create('widget.admineditwin',{option: rec.data.userName});
						var form = win.queryById('adminEditForm').getForm();
						var formData = {};
						formData.id = rec.data.id;
						formData.userName = rec.data.userName;
						formData.password = rec.data.password;
						formData.email = rec.data.email;
						formData.phoneNumber = rec.data.phoneNumber;
						form.setValues(formData);
						win.show();
					}
				}]
			},{
				xtype : 'actioncolumn',
				width : 30,
				text : '删除',
				items : [{
					id : "tagTreeGrid-delete",
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/delete.png",
					tooltip : '删除',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						Ext.Msg.confirm('删除', '确认删除此标签吗？', function(button) {
							if (button === 'yes') {
								Ext.Ajax.request({
									url : '/system/del_admin?id=' + rec.data.id,
									method : 'GET',
									success : function(response) {
										var json = Ext.decode(response.responseText);
										me.getStore().reload();
										Ext.Msg.alert("系统信息", json.msg);
									},
									failure : function(response) {
										Ext.Msg.alert("系统信息", "删除失败");
									},
									scope : this
								});
							}
						}, this);

					},
					scope : this
				}]
			}],
			tbar : me._getBar(me)
		});
        var store = Ext.getStore(me.store);
        store.load();
        me.callParent(arguments);
        
	},
	_getBar : function(me) {
		var win;
		return [ {  
	        text:' 新增  ',  
	        iconCls : 'helome-add',
	        handler:function()
	        {  
				var win = Ext.create('widget.admineditwin');
				var form = win.queryById('adminEditForm').getForm();
				win.show();
	        }  
	    }];
	}
});