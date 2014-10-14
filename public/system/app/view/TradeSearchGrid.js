Ext.define('Topx.view.TradeSearchGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.tradesearchgrid',
    title: '交易查询',
    store: 'TradeGrid',
    loadMask: true,
    forceFit: true,
    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    sortable: true,
                    dataIndex: 'createTime',
                    text: '发起时间',
                    width: 100
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'tradeStatus',
                    text: '交易状态',
                    width: 100
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'number',
                    text: '交易号',
                    width: 100
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'expertName',
                    text: '咨询专家',
                    width: 100
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'userName',
                    text: '咨询用户',
                    width: 100
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'serviceTime',
                    text: '预约服务时间',
                    width: 160
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'duration',
                    text: '预约时长',
                    width: 100
                },{
                    xtype: 'gridcolumn',
                    sortable: false,
                    dataIndex: 'talkTime',
                    text: '通话时长',
                    width: 100
                }
            ],
            bbar: {
        		xtype:'pagingtoolbar',
                store:'TradeGrid',
                displayInfo:true, 
                displayMsg:'显示第 {0} 条到 {1} 条记录，一共  {2} 条',
                emptyMsg: "没有记录"
            },
	        tbar: [me._getSearchBar(me)]
        });
        
        var store = Ext.getStore(me.store);
        
        store.on('beforeload', function (store, options) {
            var newParams = {
        		'searchText':Ext.getCmp('tradeSearch').getValue()
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
            		id: 'tradeSearch',
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
                }, {
                	id: 'tradesearchgrid-searchbutton',
                    xtype: 'button',
                    text: '搜索',
                    margin: '0 0 0 8',
                    handler: function () {
                    	me.search(me);
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