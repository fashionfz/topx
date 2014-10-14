/**
 * 
 */
Ext.define('Topx.view.TagTreeGrid', {

	extend : 'Ext.tree.Panel',
	alias : 'widget.tagtreegrid',
	title : '服务标签',
	store : 'TagTreeGrid',
	loadMask : true,
	useArrows : true,
	rootVisible : false,

	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			columns : [  {
				xtype : 'treecolumn',
				sortable : false,
				dataIndex : 'tagName',
				width : 350,
				text : '名称'
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'industryName',
				width : 350,
				text : '行业'
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'tagType',
				width : 100,
				text : '类型',
				renderer: function (value, meta, record) { 
					if(value == 'CATEGORY')
						return '行业分类';
					else
						return '服务标签';
				} 
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'seq',
				text : '排序号'
			},{
				xtype : "actioncolumn",  
				sortable : false,  
				dataIndex : "enabled", 
				width: 25,
				items: [{  
					icon: cdnUrl+"/assets/system/ext/shared/extjs/images/fam/add.png",  
					tooltip: '新增',
					handler: function(grid, rowIndex, colIndex) {
						var win = Ext.create('widget.tageditwin');
	                	var selection = grid.getSelectionModel()
	                	selection.select(rowIndex);
	                	if (selection.hasSelection()){
	                		var model = selection.getLastSelected();
	                		var form = win.queryById('tagEditform').getForm();
	                		if(model.raw.tagType == "CATEGORY"){
	                			form.findField('industryId').setValue(model.raw.id);
	                		}else{
	                			form.findField('industryId').setValue(model.raw.industryId);
	                		}
	                	}
	                	win.show();
					}  
				}] 
			},{
				xtype : "actioncolumn",
				sortable : false,  
				width: 25,
				items: [{  
					icon: cdnUrl+"/assets/system/ext/shared/extjs/images/fam/edit_task.png",  
					tooltip: '编辑',  
					getClass: 'helome-app-edit',  
					handler: function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex); 
						Ext.Ajax.request({
						    url: 'tag/get/'+rec.data.id,
						    success: function(response){
						        var text = response.responseText;
						        var win = Ext.create('widget.tageditwin');
								var form = win.queryById('tagEditform').getForm();
								var data = Ext.decode(response.responseText).data;
								form.setValues(data);
						        win.show();
						    }
						});
					}  
				}] 
			},{
				xtype : "actioncolumn",
				sortable : false,  
				width: 25,
				items: [{
					id : "tagTreeGrid-delete",
					icon: cdnUrl+"/assets/system/ext/shared/extjs/images/fam/delete.png",  
					tooltip: '删除',  
					getClass: 'helome-app-delete',
					handler: function(grid, rowIndex, colIndex) {
					   	var rec = grid.getStore().getAt(rowIndex);
					   	if(rec.data.tagType == "CATEGORY"){
					   		Ext.Msg.alert("警告","行业分类不能删除!");
					   		return;
					   	}
					   	
						Ext.Msg.confirm('删除', '确认删除此标签及下属标签吗？', function(button) {
						    if (button === 'yes') {
								Ext.Ajax.request({
								    url: 'tag/delete/'+rec.data.id,
								    method : 'POST',
								    success: function(response){
										if (response.responseText == "root") {
											this.getStore().load();
										} else if (response.responseText == "constraint error") {
											Ext.Msg.alert("警告","标签已经有专家使用!");
										} else {
											var parent = this.getStore().getNodeById(response.responseText);
											this.getStore().load({
												node : parent,
												scope : this
											});
										}
								    },
								    scope : this
								
								});
						    }
						},this);
						
					},
					scope : this
				}] 
			}],

			dockedItems : [ this.createTopToolbar() ]
		});

		me.callParent(arguments);
	},

	createTopToolbar : function() {
		this.toolbar = Ext.create('widget.toolbar', {
			items : [ {
				id : 'tagTreeGrid-add',
				iconCls : 'helome-add',
				text : '新建',
				scope : this
			}]
		});
		return this.toolbar;
	}

});