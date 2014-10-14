/**
 * 
 */
Ext.define('Topx.view.RoleAuthEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.roleautheditwin',
	height : 450,
	width : 500,
	iconCls : 'helome-app-edit',
	title : '角色授权编辑',
	modal : true,
	constructor: function (config) {
        this.roleId = config.option;
        this.callParent();
    },

	initComponent : function() {
		var me = this;
		var tree = Ext.create('Ext.tree.Panel', {
			id : 'menuTree',
			alias : 'widget.roleauthgrid',
			xtype: 'check-tree',
			title: false,
//		    store: new Ext.data.TreeStore({
//				autoDestroy : true,
//				autoLoad: false,
//				fields:['id','text','checked','leaf'],
//				nodeParam : 'parentId',
//				proxy : {
//					type : 'ajax',
//					reader : 'json',
//					url : '/system/url_tree_by_role?roleId='+me.roleId
//				},
//				root : {
//				    text : '根节点',
//				    id : '-1',
//				    expanded : true
//				  }
//			}),
		    store: new Ext.data.TreeStore({
				autoDestroy : true,
				autoLoad: false,
				fields:['id','text','checked','leaf'],
				proxy : {
					type : 'ajax',
					reader : 'json',
					url : '/system/url_tree_role?roleId='+me.roleId
				}
		    }),			
			viewConfig : {  
	            onCheckboxChange : function(e, t) {  
	                var item = e.getTarget(this.getItemSelector(), this.getTargetEl()), record;  
	                if (item) {  
	                    record = this.getRecord(item);  
	                    var check = !record.get('checked');  
	                    record.set('checked', check);  
	  
	                    if (check) {  
	                        record.bubble(function(parentNode) {  
	                                    parentNode.set('checked', true);  
	                                });  
	                        record.cascadeBy(function(node) {  
	                                    node.set('checked', true);  
	                                });  
	                    } else {  
	                        record.cascadeBy(function(node) {  
	                                    node.set('checked', false);  
	                                });  
	                    }  
	                }  
	            }  
	        },
		    rootVisible: false,
		    useArrows: true,
		    frame: false,
		    height : 350,
		    width : 470
		});
		Ext.applyIf(me, {
			items : [ {
				id : 'roleAuthEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [tree,{
					id : 'idRoleAuth',
					name : 'id',
					xtype : 'hiddenfield'
						}],
				buttons : [ {
					id: 'roleautheditwin-submit',
					text : '保存'
				}, {
					text : '关闭',
					handler : function() {
						this.close();
					},
					scope : this
				}]
			}]
		});

		me.callParent(arguments);
	}
});