/**
 * 
 */
Ext.define('Topx.view.TagEditGrid', {
	id : 'tageditgrid_1',
	extend : 'Ext.grid.Panel',
	alias : 'widget.tageditgrid',
	xtype : 'cell-editing',
	requires : [ 'Ext.selection.CellModel', 'Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.form.*' ],

	// title : 'Edit Plants',

	initComponent : function() {
		var me = this;
		this.cellEditing = new Ext.grid.plugin.CellEditing({
			clicksToEdit : 1
		});
		
		var store = new Ext.data.Store({
			autoDestroy : true,
			model : 'Topx.model.TagGrid',
			autoLoad : false,
			proxy : {
				type : 'ajax',
				url : '/system/tag/querycate',
				reader : {
					type : 'json',
					root : 'data',
					totalProperty : 'total'
				}
			},
			remoteSort : true
		});
		store.on('beforeload', function(store, options) {
			var newParams = {
				'searchText' : Ext.getCmp('tn').getValue()
			};
			Ext.apply(store.proxy.extraParams, newParams);
		});
		Ext.applyIf(me, {
			plugins : [ this.cellEditing ],
			store : store,
			columns : [ new Ext.grid.RowNumberer({
				header : "序号",
				width : 40
			}), {
				xtype : 'gridcolumn',
				header : '标签名称',
				dataIndex : 'tagName'
			}, {
				xtype : 'gridcolumn',
				header : '英文名称',
				dataIndex : 'tagNameEn'
			}, {
				xtype : 'gridcolumn',
				header : '行业',
				dataIndex : 'industryName',
				width : 200,
				editor : {
					xtype : 'combobox',
					lazyRender : true,
					store : new Ext.data.Store({
						fields : [ 'id', 'tagName' ],
						proxy : {
							type : 'ajax',
							reader : 'json',
							url : '/system/tag/queryind?type=industry'
						},
						autoLoad : true
					}),
					queryMode : 'local',
					displayField : 'tagName',
					valueField : 'id',
					listeners : {
						change : function(combo, newValue, oldValue) {
							var selection = me.getSelectionModel()
							if (selection.hasSelection()) {
								var cid = selection.getLastSelected().data.id;
								Ext.Ajax.request({
									url : '/system/tag/updatecate',
									method : 'POST',
									params : {
										id : cid,
										cateId : newValue,
										oldId : me.store.proxy.extraParams.cateId
									},
									success : function(response, opts) {
										me.store.reload();
									},
									failure : function(response, opts) {
										console.log('server-side failure with status code ' + response.status);
									},
									scope : this
								});
							}
						},
						scope : this
					}
				}
			}, {
				xtype : 'gridcolumn',
				header : '排序号',
				dataIndex : 'seq',
				width : 70,
				align : 'right'
			}, {
				xtype : "actioncolumn",
				sortable : false,
				width : 25,
				items : [ {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/paintbrush.png",
					tooltip : '编辑',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						Ext.Ajax.request({
							url : '/system/tag/get/' + rec.data.id,
							success : function(response) {
								var text = response.responseText;
								var win = Ext.create('widget.tageditwin');
								win.title = "编辑"
								var form = win.queryById('tagEditform').getForm();
								var data = Ext.decode(response.responseText).data;
								form.setValues(data);
								win.show();
							}
						});
					}
				} ]
			}, {
				xtype : "actioncolumn",
				sortable : false,
				width : 25,
				items : [ {
					id : "tagTreeGrid-delete",
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/delete.png",
					tooltip : '删除',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						Ext.Msg.confirm('删除', '确认删除此标签吗？', function(button) {
							if (button === 'yes') {
								Ext.Ajax.request({
									url : '/system/tag/delete/' + rec.data.id,
									method : 'POST',
									success : function(response) {
										if (response.responseText.substring(0,16) == "constraint error"){
//											Ext.Msg.alert("警告", "标签已经有专家使用!");
											var msgStr = response.responseText.substring(16,response.responseText.length);
											Ext.Msg.alert("警告", msgStr);
										}
										me.getStore().reload();
									},
									scope : this
								});
							}
						}, this);

					},
					scope : this
				} ]
			} ],
			selModel : {
				selType : 'cellmodel'
			},
			tbar : [ {
				text : '增加标签',
				iconCls : 'helome-add',
				scope : this,
				handler : this.onAddClick
			},{
				id : 'tn',
				name : 'tn',
				xtype : 'textfield',
				width : 300,
				emptyText : '请输入标签名',
				listeners : {
					specialKey : function(field, e) {
						if (e.getKey() == Ext.EventObject.ENTER) {
							me.search(me);
						}
					}
				}
			}]
		});

		this.bbar = [ {
			xtype : 'pagingtoolbar',
			store : me.store,
			emptyMsg : "没有记录"
		} ]
		this.callParent();

	},
	
	search : function(me) {
		var store = Ext.getStore(me.store);
		store.load();
	},

	onAddClick : function() {
		var selection = Ext.getCmp('cateGrid').getSelectionModel()
		if (selection.hasSelection()) {
			var win = Ext.create('widget.tageditwin');
			var model = selection.getLastSelected();
			var form = win.queryById('tagEditform').getForm();
			form.findField('industryId').setValue(model.raw.id);
			win.show();
		} else {
			Ext.Msg.alert("提示", "请选择行业");
		}

	},

	// rendererData:function(value,metadata,record){
	// if(isEdit){
	// var index = cbstore.find(Ext.getCmp('authors').valueField,value);
	// var record = cbstore.getAt(index);
	// return record.data.name;
	// }else{
	// return value;
	// }
	// },

	onRemoveClick : function(grid, rowIndex) {
		this.getStore().removeAt(rowIndex);
	}
})
