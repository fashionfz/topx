/**
 * 评价管理
 */
Ext.define('Topx.view.CommentGrid', {
	id : 'commentGrid',
	extend : 'Ext.grid.Panel',
	alias : 'widget.commentgrid',
	requires : [ 'Ext.selection.CellModel', 'Ext.grid.*', 'Ext.data.*', 'Ext.util.*', 'Ext.form.*' ],
	loadMask: true,
    forceFit: true,
    columnLines: true,
    collapsible: true,
    animCollapse: false,

	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			store : new Ext.data.Store({
				autoDestroy : true,
				model : 'Topx.model.CommentGrid',
				autoLoad : false,
				proxy : {
					type : 'ajax',
					url : '/system/comment/queryComment',
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
				header : "评论的id",
				sortable: false,
//				hidden: false,
				dataIndex : 'id',
				width : 50,
				renderer:function(value){
					return Ext.String.format('<a href="/comment/detail/{0}" title="点击进入该评价的页面" style="text-align:center;color:red;text-decoration:none;" target="_blank">{1}</a>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				header : '被评价用户',
				sortable: false,
				dataIndex : 'toCommentUserName',
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				header : '评价用户',
				sortable: false,
				dataIndex : 'commentUserName',
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				header : '评价时间',
				sortable: false,
				dataIndex : 'commentTime'
			}, {
				xtype : 'gridcolumn',
				header : '评价内容',
				sortable: false,
				dataIndex : 'content',
				width : 200,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				header : '评价星级',
				sortable: true,
				dataIndex : 'level',
				width : 60,
				align : 'right',
				renderer:function(value){
					if(value==5){
						return Ext.String.format('<span title="{0}" style="color:red">★★★★★</span>',value);
					} else if(value==4){
						return Ext.String.format('<span title="{0}" style="color:red">★★★★<span style="color:black">☆</span></span>',value);
					} else if(value==3){
						return Ext.String.format('<span title="{0}" style="color:red">★★★<span style="color:black">☆☆</span></span>',value);
					} else if(value==2){
						return Ext.String.format('<span title="{0}" style="color:red">★★<span style="color:black">☆☆☆</span></span>',value);
					} else if(value==1){
						return Ext.String.format('<span title="{0}" style="color:red">★<span style="color:black">☆☆☆☆</span></span>',value);
					} else {
						return Ext.String.format('<span title="{0}" style="color:black">☆☆☆☆☆</span>',value);
					}
				}
			}, {
				xtype : "actioncolumn",
				sortable : false,
				width : 40,
				header : '操作',
				items : [ {
					id : "commentTreeGrid-delete",
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/delete.png",
					tooltip : '删除',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						Ext.Msg.confirm('删除', '<font color=red>确认删除此评论吗？删除该评论将同时删除其对应的所有的回复！</font>', function(button) {
							if (button === 'yes') {
								Ext.Ajax.request({
									url : '/system/comment/delete/' + rec.data.id,
									method : 'POST',
									success : function(response) {
										if (response.responseText.length>0){
											Ext.Msg.alert("警告", response.responseText);
										}
										me.getStore().reload();
										Ext.getCmp('replygrid_1').store.load();
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
			tbar: [me._getSearchBar(me)]
		});

		this.bbar = [ {
			xtype : 'pagingtoolbar',
			store : me.store,
            displayInfo:true, 
            displayMsg:'显示第 {0} 条到 {1} 条记录，一共  {2} 条',
            emptyMsg: "没有记录"
		} ]
		this.callParent();
		this.on('afterlayout', this.loadStore, this, {
			delay : 1,
			single : true
		});
		this.on('itemclick', function(grid, r, columnIndex, e) {
//alert(r.data.id);
			Ext.getCmp('replygrid_1').store.on("beforeload",function(){  
			    Ext.apply(Ext.getCmp('replygrid_1').store.proxy.extraParams, {commentId : r.data.id});  
			});
			Ext.getCmp('replygrid_1').store.load({
				params : {
					commentId : r.data.id
				}
			});
		}, this);
		
		var store = Ext.getStore(me.store);
        store.on('beforeload', function (store, options) {
            var newParams = {
        		'searchText':Ext.getCmp('searchComment').getValue(),
        		'searchStatus':Ext.getCmp('commentLevel').getValue()
            };
            Ext.apply(store.proxy.extraParams, newParams);
        });
        store.load();  // 页面加载时load数据

	},
	
	_getSearchBar: function(me) {
    	return Ext.create('Ext.form.Panel', {
    		height: 30,
    		padding: '3 0 0 0',
            layout: 'vbox',
            items: [{
            	layout: 'column',
            	flex: 1,
            	items:[{
            		id: 'searchComment',
                	xtype: 'textfield',
                    fieldLabel: '被评价用户/评价用户/评价内容',
                    labelWidth: 180,
                    width: 400,
                    listeners :{
                        specialKey : function(field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                            	me.search(me);
                            }
                        }
                    }
                }, {
                	id: 'comment-searchbutton',
                    xtype: 'button',
                    text: '搜索',
                    margin: '0 0 0 10',
                    handler: function () {
                    	me.search(me);
                    }
                },{
            		id: 'commentLevel',
                	xtype: 'combobox',
                	labelWidth: 60,
                	width: 160,
                	store: [
                	        ['-1', '全部'],
                            ['5', '★★★★★'],
                            ['4', '★★★★☆'],
                            ['3', '★★★☆☆'],
                            ['2', '★★☆☆☆'],
                            ['1', '★☆☆☆☆']
                	       /* ,
                            ['0', '☆☆☆☆☆']*/
                        ],
                	fieldLabel: '评价星级',
                    editable: false,
//                    value: '-1',
                    queryMode: 'local',
                    margin: '0 0 0 20',
                    listeners: {
                    	change: function() {me.search(me);}
                    }
                }]	
            }]
        });
    },
	
	search : function(me) {
		var store = Ext.getStore(me.store);
		store.load();
	}
})
