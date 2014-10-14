Ext.define('Topx.view.TemplateGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.templategrid',
	title : '邮件模板管理',
	store : 'TemplateGrid',
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
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'name',
				text : '名称',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'type',
				renderer: function (value, meta, record) { 
					if(value == 1){
						return '文本'; 
					}else{
						return '网页'; 
					}
				},
				text : '类型',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'context',
				renderer: function (value, meta, record) { 
					var type = record.get("type"); 
					if(type == 1){
						return value; 
					}else{
						return '请点击"编辑模板"查看'; 
					}
				},
				text : '内容',
				width : 80
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'remark',
				text : '备注',
				width : 80
			},{
				xtype : 'actioncolumn',
				width : 30,
				text : '编辑模板',
				items : [ {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/edit_task.png",
					tooltip : '编辑模板',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var win = Ext.create('widget.templateeditwin',{content: rec.data.context,templateId:rec.data.id});
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