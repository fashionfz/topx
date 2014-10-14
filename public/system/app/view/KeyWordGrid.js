Ext.define('Topx.view.KeyWordGrid', {
	extend : 'Ext.grid.Panel',
	alias : 'widget.keywordgrid',
	title : '搜索关键字',
	store : 'KeyWordGrid',
	loadMask : true,
	autoScroll:true,
	initComponent : function() {
		var me = this;
		
		var toolbar = Ext.create('Ext.toolbar.Toolbar', {
			items : [ {  
		        text:'导出EXCEL',  
		        xtype : 'button',
		        handler:function(){
					var type = Ext.getCmp('queryType').getValue();
					if(type == '2'){
						var newParams = 'type='+Ext.getCmp('queryType').getValue()
								+'&startDate='+dateFormat(Ext.getCmp('startSearch').getValue())
								+'&endDate='+dateFormat(Ext.getCmp('endSearch').getValue())
								+'&searchText='+Ext.getCmp('wordText').getValue();
						window.location ="/system/keyWordExcel?"+newParams;
					}else{
						var newParams = 'type='+Ext.getCmp('queryType').getValue()
								+'&startDate=&endDate='
								+'&searchText='+Ext.getCmp('wordText').getValue();
						window.location ="/system/keyWordExcel?"+newParams;
					}
		        }
					
		    },{
				id : 'wordText',
				xtype : 'textfield',
				emptyText : '输入关键字',
				width: 300,
				listeners : {
					specialKey : function(field, e) {
						if (e.getKey() == Ext.EventObject.ENTER) {
							me.search(me);
						}
					}
				}
			},{//性别
				id : 'queryType',
				xtype : 'combobox',
				store : [ [ '', '昨天' ], [ '0', '最近一周' ], [ '1', '最近一月' ],[ '2', '自定义日期' ] ],
				value : '',
				editable : false,
				queryMode : 'local',
				width: 200,
				listeners : {
					change : function(tbar,newValue, oldValue, eOpts) {
						var type = Ext.getCmp('queryType').getValue();
						if(newValue==''){
							if(oldValue == '2'){
								toolbar.remove(Ext.getCmp('startSearch'));
								toolbar.remove(Ext.getCmp('startReset'));
								toolbar.remove(Ext.getCmp('endSearch'));
								toolbar.remove(Ext.getCmp('endReset'));
								toolbar.remove(Ext.getCmp('querySearch'));
							}
							me.search(me);
						}
						else if(newValue=='0'){
							if(oldValue == '2'){
								toolbar.remove(Ext.getCmp('startSearch'));
								toolbar.remove(Ext.getCmp('startReset'));
								toolbar.remove(Ext.getCmp('endSearch'));
								toolbar.remove(Ext.getCmp('endReset'));
								toolbar.remove(Ext.getCmp('querySearch'));
							}
							me.search(me);
						}
						else if(newValue=='1'){
							if(oldValue == '2'){
								toolbar.remove(Ext.getCmp('startSearch'));
								toolbar.remove(Ext.getCmp('startReset'));
								toolbar.remove(Ext.getCmp('endSearch'));
								toolbar.remove(Ext.getCmp('endReset'));
								toolbar.remove(Ext.getCmp('querySearch'));
							}
							me.search(me);
						}
						else if(newValue=='2'){
							toolbar.add([{     
						        xtype:'datefield',  
						        id : 'startSearch',  
						        emptyText : '起始日期',
						        editable:false,  
						        height:20,  
						        width: 200,
						        format:'Y-m-d'
						    },{  
						        text:'清空',  
						        id : 'startReset',  
						        handler:function(){  
						                    Ext.getCmp("startSearch").setValue("");  
						                }  
						    },{     
						        xtype:'datefield',  
						        id : 'endSearch',  
						        emptyText : '结束日期',
						        editable:false,  
						        height:20,  
						        width: 200,
						        format:'Y-m-d'
						    },{  
						        text:'清空',  
						        id : 'endReset', 
						        handler:function(){  
						                    Ext.getCmp("endSearch").setValue("");  
						                }  
						    },{  
						        text:'查    询',  
						        id : 'querySearch',
						        width: 80,
						        handler:function(){  
						        			me.search(me);
						                }  
						    }]);
						}
						

					}
				}
			}]
		});
		
		
		Ext.applyIf(me, {
			columns : [ {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'word',
				text : '关键字',
				width : 600
			},{
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'searchNum',
				text : '搜索次数',
				width : 200
			}],
			bbar : {
				xtype : 'pagingtoolbar',
				store : 'KeyWordGrid',
				displayInfo : true,
				displayMsg : '显示第 {0} 条到 {1} 条记录，一共  {2} 条',
				emptyMsg : "没有记录"
			},
			tbar : toolbar
		});
        var store = Ext.getStore(me.store);
		store.on('beforeload', function(store, options) {
			var type = Ext.getCmp('queryType').getValue();
			if(type == '2'){
				var newParams = {
						'type' : Ext.getCmp('queryType').getValue(),
						'startDate' : dateFormat(Ext.getCmp('startSearch').getValue()),
						'endDate' : dateFormat(Ext.getCmp('endSearch').getValue()),
						'searchText' : Ext.getCmp('wordText').getValue()
					};
					Ext.apply(store.proxy.extraParams, newParams);
			}else{
				var newParams = {
						'type' : Ext.getCmp('queryType').getValue(),
						'searchText' : Ext.getCmp('wordText').getValue()
					};
					Ext.apply(store.proxy.extraParams, newParams);
			}

		});
        store.load({
            callback: function(records, operation, success) {
            	if(!success){
            		Ext.Msg.alert("系统信息",'权限不足，请联系管理员');
            	}
            },
            scope: this
        });
        me.callParent(arguments);
	},
	search : function(me) {
		var type = Ext.getCmp('queryType').getValue();
		if(type == '2'){
			var  bstr = Ext.getCmp('startSearch').getValue();
			var estr = Ext.getCmp('endSearch').getValue();
			
			if(bstr == null || bstr ==''){
				Ext.Msg.alert("系统信息","必须输入起始日期");
				return;
			}
			if(estr == null || estr ==''){
				Ext.Msg.alert("系统信息","必须输入结束日期");
				return;
			}
			
			var now = new Date();
			now = new Date(now.getFullYear(),now.getMonth(),now.getDate());
			var begin = new Date(Ext.getCmp('startSearch').getValue());
			if(begin.getTime()>=now.getTime()){
				Ext.Msg.alert("系统信息","查询日期必须小于今天");
				return;
			}
			var end = new Date(Ext.getCmp('endSearch').getValue());
			if(end.getTime()>=now.getTime()){
				Ext.Msg.alert("系统信息","查询日期必须小于今天");
				return;
			}
			
			if(begin.getTime()>end.getTime()){
				Ext.Msg.alert("系统信息","起始日期不能大于结束日期");
				return;
			}
		}
		var store = Ext.getStore(me.store);
		store.currentPage = 1;
		store.load();
	}
});

//格式化日期，规避日期内出现"T"字符
function dateFormat(value){ 
    if(null != value){ 
        return Ext.Date.format(new Date(value),'Y-m-d'); 
    }else{ 
        return null; 
    }
}