/**
 * 行业管理
 */
Ext.define('Topx.view.CateGrid', {
	id: 'cateGrid',
	extend : 'Ext.grid.Panel',
	alias : 'widget.categrid',
	title:'行业管理',
	store: 'CateGrid',
	plugins: [{
        ptype: 'rowexpander',
        witdh: 10,
        rowBodyTpl : new Ext.XTemplate(
        	'<tpl for="attachInfos">',
        	"<p>图片：<a href='{attachPath}' target='_blank'>{attachFileName}</a>		&nbsp;<span style='word-break:break-word;'>图片路径：{attachPath}</span><input type='button' value='删除' onclick='deleteAttach({attachId},{industryId});'/> </p>",
        	'</tpl>')
    }],
	
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			columns : [ {
				header : '行业',
				dataIndex : 'tagName',
				flex : 1,
				editor : {
					allowBlank : false
				}
			} , {
				header : '行业英文',
				dataIndex : 'tagNameEn',
				flex : 1
			} , {
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
								var win = Ext.create('widget.cateeditwin');
								win.title = "编辑";
								var form = win.queryById('cateEditform').getForm();
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
						Ext.Msg.confirm('删除', '<font color=red>确认删除此行业吗？删除行业将导致行业标签删除,以及对专家从属的此行业也将删除！</font>', function(button) {
							if (button === 'yes') {
								Ext.Ajax.request({
									url : '/system/tag/delete/' + rec.data.id,
									method : 'POST',
									success : function(response) {
										Ext.getCmp('tageditgrid_1').store.removeAll();
										me.search(me);
									},
									scope : this
								});
							}
						}, this);

					},
					scope : this
				} ]
			}],
			tbar : [ {
				text : '增加行业',
				iconCls : 'helome-add',
				scope : this,
				handler : this.onAddClick
			} ]
		});
		this.bbar = [ {
			xtype : 'pagingtoolbar',
			store : me.store,
			emptyMsg : "没有记录"
		} ];
		this.callParent();
		this.on('afterlayout', this.loadStore, this, {
			delay : 1,
			single : true
		});
		this.on('itemclick', function(grid, r, columnIndex, e) {
			Ext.getCmp('tageditgrid_1').store.on("beforeload",function(){  
			    Ext.apply(Ext.getCmp('tageditgrid_1').store.proxy.extraParams, {cateId : r.data.id});  
			});
			Ext.getCmp('tageditgrid_1').store.load({
				params : {
					cateId : r.data.id
				}
			});
		}, this);
	},

	loadStore : function() {
		this.getStore().load();
	},

	onAddClick : function() {
		Ext.create('widget.cateeditwin').show();
	},
	search : function(me) {
		var store = Ext.getStore(me.store);
		store.currentPage = 1;
		store.load();
	}
})

/**
 * 删除图片附件
 */
function deleteAttach(attachId,industryId){
	var me = Ext.getCmp('cateGrid');
	Ext.Msg.confirm('Name', '确认删除该图片？', function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : '/system/tag/deleteAttach?attachId=' + attachId + "&industryId=" + industryId,
				method : 'GET',
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.status=="1") {
						var store = Ext.getStore(me.store);
						store.load();
					} else {
						Ext.Msg.alert('操作失败', obj.error);
					}
				},
				failure : function() {
					Ext.Msg.alert('failure', '操作失败');
				}
			});
		}
	});
}
