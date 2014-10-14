/**
 * 回复列表
 */
Ext.define('Topx.view.ReplyGrid', {
	id : 'replygrid_1',
	extend : 'Ext.grid.Panel',
	alias : 'widget.replygrid',
	xtype : 'cell-editing',
	requires : [ 'Ext.selection.CellModel', 'Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.form.*' ],
	loadMask: true,
    forceFit: true,

	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			store : new Ext.data.Store({
				autoDestroy : true,
				model : 'Topx.model.CommentGrid',
				autoLoad : false,
				proxy : {
					type : 'ajax',
					url : '/system/comment/queryReply',
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'total'
					}
				},
				remoteSort : true
			}),
			columns : [ new Ext.grid.RowNumberer({
				header : "序号",
				width : 40
			}), {
				xtype : 'gridcolumn',
				header : '评价回复者id',
				sortable: false,
				width : 50,
				dataIndex : 'commentUserId'
			}, {
				xtype : 'gridcolumn',
				header : '评价回复者',
				sortable: false,
				dataIndex : 'commentUserName',
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				header : '评价回复时间',
				sortable: false,
				dataIndex : 'commentTime'
			}, {
				xtype : 'gridcolumn',
				header : '评论回复内容',
				sortable: false,
				dataIndex : 'content',
				width : 200,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : "actioncolumn",
				sortable : false,
				header : '操作',
				width : 40,
				items : [ {
					id : "replayGrid-delete",
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/delete.png",
					tooltip : '删除',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						Ext.Msg.confirm('删除', '确认删除此回复吗？', function(button) {
							if (button === 'yes') {
								Ext.Ajax.request({
									url : '/system/comment/delete/' + rec.data.id,
									method : 'POST',
									success : function(response) {
										if (response.responseText.substring(0,16) == "constraint error"){
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
			}
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
	}
})
