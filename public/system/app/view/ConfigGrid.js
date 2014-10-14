/**
 * 配置管理的grid
 */
Ext.define('Topx.view.ConfigGrid', {

	id : 'configGrid',
	
	title : '配置管理',

	extend : 'Ext.grid.Panel',

	alias : 'widget.configgrid',

	border : false,

	initComponent : function() {
		var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
			saveBtnText: '保存', 
			cancelBtnText: "取消", 
			clicksToMoveEditor: 1,
			autoCancel: false
		});
		var me = this;
		
		Ext.apply(this, {
			
			store : new Ext.data.Store({
				autoLoad: true,
				autoSync: true,
				model: 'Topx.model.ConfigGrid',
				proxy: {
					type: 'ajax',
					api: {
						read: '/system/config/listall',//查询
						create: '/system/config/create',//创建
						update: '/system/config/update',//更新
						destroy: '/system/config/destroy'//删除
					},
					reader: {
						type: 'json'
					},
					writer: {
						type: 'json'
					}
				}

			}),
			columns : [ {
				header : '键',
				dataIndex : 'property',
				flex : 0.5,
				editor : {
					allowBlank : true
				}
			}, {
				header : '值',
				dataIndex : 'value',
				flex : 1,
				editor : {
					allowBlank : true
				}
			}, {
				header : '描述',
				dataIndex : 'introduce',
				flex : 0.3,
				editor : {
					allowBlank : true
				},
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}],
			tbar: [{
				text: '增加配置',
				iconCls: 'helome-add',
				handler : function() {
					rowEditing.cancelEdit();
					var r = Ext.create('Topx.model.ConfigGrid', {
						property: 'key',
						value: 'value'
					});
					this.store.insert(0, r);
					rowEditing.startEdit(0, 0);
				},
				scope : this
			}, {
				itemId: 'removeEmployee',
				text: '删除配置',
				iconCls: 'helome-delete',
				handler: function() {
					var sm = this.getSelectionModel();
					rowEditing.cancelEdit();
					this.store.remove(sm.getSelection());
					if (this.store.getCount() > 0) {
						sm.select(0);
					}
				},
				scope : this,
				disabled: true
			}, {
				itemId: 'synConfig',
				text: '同步配置',
				iconCls: 'helome-app-edit',
				handler: function() {
				     var myMask = new Ext.LoadMask(me, { msg: "正在同步配置信息，请稍后..." });
                     myMask.show();
                	 Ext.Ajax.timeout = 60000;
                	 Ext.Ajax.request({
                		    url: '/system/config/syn',
                		    success: function(response){
                		    	myMask.hide();
                		    	//var json = Ext.decode(response.responseText);
                		        Ext.Msg.alert("提示", "同步成功!");
                		    },
                		    failure: function (response, opts) {
                                myMask.hide();
                                Ext.Msg.alert("提示", "同步失败!");
                            }
                	});
                	
				},
				scope : this
			}],
			plugins: [rowEditing],
			listeners: {
				'selectionchange': function(view, records) {
					this.down('#removeEmployee').setDisabled(!records.length);
				}
			},
			scope : this
		});
		this.callParent();
	}
})
