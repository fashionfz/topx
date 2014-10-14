Ext.define('Topx.view.MenuDetailGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.menudetailgrid',
	region: 'center',
	store : 'MenuDetailGrid',
    columnLines: true,
    frame: false,
    title: '子功能菜单',
    iconCls: 'icon-grid',
	constructor: function (config) {
        this.treeStore = config.option;
        this.callParent();
    },
    initComponent : function() {
    	var me = this;
		Ext.applyIf(me, {
			columns : [{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'url',
				text : 'url',
				width : 150
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'name',
				text : '名称',
				width : 150
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'sort',
				text : '排序',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'remark',
				text : '备注',
				width : 120
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'leaf',
				text : '是否叶子节点',
				width : 80
			},{
				xtype : 'actioncolumn',
				width : 50,
				text : '删除',
				items : [{
					id : "menu-delete",
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/delete.png",
					tooltip : '删除',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						Ext.Msg.confirm('删除', '确认删除此标签吗？', function(button) {
							if (button === 'yes') {
								Ext.Ajax.request({
									url : '/system/del_menu?id=' + rec.data.id,
									method : 'GET',
									success : function(response) {
										var json = Ext.decode(response.responseText);
										Ext.Msg.alert("系统信息", json.msg);
										me.getStore().reload();
										me.treeStore.load();
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
	        	var maxSort=1;
	        	var store = Ext.getStore(me.store);
	        	for(var i=0;i<store.data.length;i++){
	        		if(store.data.getAt(i).data.sort>=maxSort){
	        			maxSort = store.data.getAt(i).data.sort+1;
	        		}
	        	}
				var win = Ext.create('widget.menueditwin',{option: me.store});
				var form = win.queryById('childMenuEditForm').getForm();
				var formData = {};
				formData.parentId = store.proxy.extraParams.id;
				formData.sort = maxSort;
				form.setValues(formData);
				win.show();
	        }  
	    }];
	}
});