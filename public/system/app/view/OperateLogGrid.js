Ext.define('Topx.view.OperateLogGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.operateloggrid',
    title: '操作记录',
    store: 'OperateLogGrid',
    loadMask: true,
    forceFit: true,
    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    sortable: true,
                    dataIndex: 'operateTime',
                    text: '操作时间',
                    width: 100
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'operator',
                    text: '操作人',
                    width: 70
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'operateIp',
                    text: '用户ip',
                    width: 70
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'menuName',
                    text: '操作模块',
                    width: 70
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'result',
                    text: '操作结果',
                    width: 50,
    				renderer:function(value){
    					if(value=="true"){
    						return "成功";
    					}else{
    						return "失败";
    					}
    				}
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'paramters',
                    text: '请求参数',
                    width: 200
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'describle',
                    text: '操作描述',
                    width: 300
                }
            ],
            bbar: {
        		xtype:'pagingtoolbar',
                store:'OperateLogGrid',
                displayInfo:true, 
                displayMsg:'显示第 {0} 条到 {1} 条记录，一共  {2} 条',
                emptyMsg: "没有记录"
            },
	        tbar: [me._getSearchBar(me)]
        });
        
        var store = Ext.getStore(me.store);
        
        store.on('beforeload', function (store, options) {
            var newParams = {
        		'searchText':Ext.getCmp('operateLogSearch').getValue(),
        		'searchModule':Ext.getCmp('operateModule').getValue(),
        		'result':Ext.getCmp('result').getValue()
            };
            Ext.apply(store.proxy.extraParams, newParams);
        });
        
        store.load();
        me.callParent(arguments);
    },
    
    _getSearchBar: function(me) {
    	return Ext.create('Ext.form.Panel', {
    		height: 36,
    		padding: '5 0 5 10',
            layout: 'vbox',
            items: [{
            	layout: 'column',
            	flex: 1,
            	items:[{
            		id: 'operateLogSearch',
                	xtype: 'textfield',
                    fieldLabel: '',
                    labelWidth: 5,
                    width: 300,
                    listeners :{
                        specialKey : function(field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                            	me.search(me);
                            }
                        }
                    }
                },{
                	id: 'operateLogSearch-searchbutton',
                    xtype: 'button',
                    text: '搜索',
                    margin: '0 0 0 10',
                    handler: function () {
                    	me.search(me);
                    }
                },{//操作模块
            		id: 'operateModule',
                	xtype: 'combobox',
                	labelWidth: 60,
                	width: 180,
                	store: 'OperateModule',
                	fieldLabel: '操作模块',
                	displayField: 'name',
                    valueField: 'value',
                    editable: false,
                    value: '',
                    queryMode: 'local',
                    margin: '0 0 0 15',
                    listeners: {
                    	change: function() {me.search(me);}
                    }
                },{// 在线客户与在线翻译
        			id : 'result',
        			xtype : 'combobox',
        			labelWidth: 60,
        			fieldLabel: '操作结果',
        			width: 200,
        			store : [ [ '', '全部' ], [ 'true', '成功' ], [ 'false', '失败' ], ],
        			value : '',
        			editable : false,
        			queryMode : 'local',
        			margin: '0 0 0 15',
        			listeners : {
        				change : function() {
        					me.search(me);
        				}
        			}
        		}]	
            }]
        });
    },
    
    search: function(me) {
    	var store = Ext.getStore(me.store);
    	store.currentPage = 1;
    	store.load();
    }
});