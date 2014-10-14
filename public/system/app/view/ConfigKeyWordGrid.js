Ext.define('Topx.view.ConfigKeyWordGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.configkeywordgrid',
	title : '邮件模板管理',
	store : 'ConfigKeyWordGrid',
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
				text : 'Id',
				width : 40
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'key',
				text : '名称',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'value',
				text : '内容',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'remark',
				text : '备注',
				width : 150
			},{
				xtype : 'actioncolumn',
				width : 30,
				text : '编辑内容',
				items : [ {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/edit_task.png",
					tooltip : '编辑内容',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var win = Ext.create('widget.configkeywordeditwin');
						var form = win.queryById('configKeyWordEditForm').getForm();
						var formData = {};
						formData.id = rec.data.id;
						formData.value = rec.data.value;
						form.setValues(formData);
						win.show();
					}
				}]
			}]
		});
        var store = Ext.getStore(me.store);
        store.load();
        me.callParent(arguments);
        
	}
});