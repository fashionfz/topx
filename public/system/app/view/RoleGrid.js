Ext.define('Topx.view.RoleGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.rolegrid',
	title : '后台角色管理',
	store : 'RoleGrid',
	loadMask : true,
	autoScroll:true,
	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			columns : [ {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'id',
				text : 'Id',
				width : 120
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'name',
				text : '角色名称',
				width : 120
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'remark',
				text : '备注',
				width : 200
			},{
				xtype : 'actioncolumn',
				text : '授权',
				items : [ {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/edit_task.png",
					tooltip : '授权',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var win = Ext.create('widget.roleautheditwin',{option: rec.data.id});
						var form = win.queryById('roleAuthEditForm').getForm();
						var formData = {};
						formData.id = rec.data.id;
						form.setValues(formData);
						win.show();
					}
				}]
			},{
				xtype : 'actioncolumn',
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
									url : '/system/del_role?id=' + rec.data.id,
									method : 'POST',
									success : function(response) {
										var json = Ext.decode(response.responseText);
										Ext.Msg.alert("系统信息", json.msg);
										me.getStore().reload();
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
				var win = Ext.create('widget.roleeditwin');
				var form = win.queryById('roleEditForm').getForm();
				win.show();
	        }  
	    }];
	}
});