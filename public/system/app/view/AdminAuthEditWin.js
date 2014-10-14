Ext.define('AdminAuth', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',     type: 'int'},
        {name: 'name',      type: 'string'},
        {name: 'remark',    type: 'string'},
        {name: 'auth',   type: 'int'}
    ]
});
/**
 * 后台用户管理-----授权
 */
Ext.define('Topx.view.AdminAuthEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.adminautheditwin',
	height : 340,
	width : 500,
	iconCls : 'helome-app-edit',
	title : '用户授权编辑',
	modal : true,
	//构造函数，接受需要查询的用户名称参数
	constructor: function (config) {
        this.userName = config.option;
        this.callParent();
    },
	initComponent : function() {
		var me = this;
		
		var selModel = Ext.create('Ext.selection.CheckboxModel', {
            handleMouseDown : Ext.emptyFn,  
            singleSelect : false  
	    });
		var grid2 = Ext.create('Ext.grid.Panel', {
		    id: 'grid2',
		    store: new Ext.data.Store({
				autoDestroy : true,
				autoLoad: false,
				model : 'AdminAuth',
				proxy : {
					type : 'ajax',
					reader : 'json',
					url : '/system/get_role_by_user?username='+me.userName
				}
			}),
			selModel: selModel,
		    selType: 'checkboxmodel',
		    columns: [
		        {text: "角色id", width: 100, dataIndex: 'id'},
		        {text: "角色名称", width: 150, dataIndex: 'name'},
		        {text: "角色备注", width: 150, dataIndex: 'remark'}
		    ],
		    columnLines: true,
		    width: 450,
		    height: 250,
		    frame: false,
		    title: false,
		    iconCls: 'icon-grid'
		});
        var store = Ext.getStore(grid2.store);
        var sm = grid2.selModel;
        store.load({
            callback: function(records, operation, success) {
        	var arr=[];
        	for(var i=0;i<records.length;i++){
    	      var record = records[i];
    	      if(record.data.auth == 1){
    	    	  arr.push(record);
    	      }
    	    }
        	sm.select(arr);
            },
            scope: this
        });
        //store.load();
        
        
		Ext.applyIf(me, {
			items : [ {
				id : 'adminAuthEditForm',
				xtype : 'form',
				border : false,
				bodyPadding : 10,
				header : false,
				items : [grid2,
				         {
							id : 'userName',
							name : 'userName',
							xtype : 'hiddenfield'
				         }],
				buttons : [ {
					id: 'adminautheditwin-submit',
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