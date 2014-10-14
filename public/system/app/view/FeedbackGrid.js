Ext.define('Topx.view.FeedbackGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.feedbackgrid',
    title: '用户投诉',
    store: 'FeedbackGrid',
    columnLines: true,
    enableLocking: true,
    collapsible: true,
    animCollapse: false,
    iconCls: 'icon-grid',
    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl : new Ext.XTemplate(
        	'<tpl for="proofs">',
            '<p><a href="{proofPath}" target="_blank" style="color:red;">{proofName}</a></p>',
            '</tpl>',
            '<p>内容: {content}</p>')
    }],
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	columns: [
			{
			    xtype: 'gridcolumn',
			    sortable: true,
			    dataIndex: 'createTime',
			    text: '投诉时间',
			    width: 160
			},{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'username',
                text: '投诉用户',
                width: 150,
                renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                	return '<a href="#" onClick="userInfo()">' + value + '</font>';
                }
            },{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'expertName',
                text: '被投诉用户',
                width: 150,
                renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                	return '<a href="#" onClick="userInfo()">' + value + '</font>';
                }
            },{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'number',
                text: '交易号',
                width: 160
            },{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'status',
                text: '处理状态',
                width: 100,
                renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                	if(value == 0) {
                		return '<font style="color:red">未处理</font>';
                	} else if(value == 1) {
                		return '<font style="color:green">已处理</font>';
                	}
                }
            },{
            	xtype: 'actioncolumn',
                items: [{  
					icon: cdnUrl+"/assets/system/ext/shared/extjs/images/fam/edit_task.png",  
					tooltip: '编辑',  
					handler: function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex); 
						var formData = {};
						formData.feedbackId = rec.data.id;
						formData.usernameDisplay = rec.data.username;
						formData.expertNameDisplay = rec.data.expertName;
						formData.numberDisplay = rec.data.number;
						formData.createTimeDisplay = rec.data.createTime;
						formData.handleState = +rec.data.status + "";
						
				        var win = Ext.create('widget.feedbackstateeditwin');
						var form = win.queryById('feedbackStateEditForm').getForm();
						form.setValues(formData);
				        win.show();
					}  
				}]
            }],
            bbar: {
        		xtype:'pagingtoolbar',
                store:'FeedbackGrid',
                displayInfo:true, 
                displayMsg:'显示第 {0} 条到 {1} 条记录，一共  {2} 条',
                emptyMsg: "没有记录"
            },
            tbar: [me._getSearchBar(me)]
        });
        var store = Ext.getStore(me.store);
        store.on('beforeload', function (store, options) {
            var newParams = {
        		'searchText':Ext.getCmp('searchFeedback').getValue(),
        		'searchStatus':Ext.getCmp('feedback_status').getValue()
            };
            Ext.apply(store.proxy.extraParams, newParams);
        });
        store.load();
        me.callParent(arguments);
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
            		id: 'searchFeedback',
                	xtype: 'textfield',
                    fieldLabel: '投诉用户/被投诉用户/交易号',
                    labelWidth: 160,
                    width: 400,
                    listeners :{
                        specialKey : function(field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                            	me.search(me);
                            }
                        }
                    }
                }, {
                	id: 'feedbackgrid-searchbutton',
                    xtype: 'button',
                    text: '搜索',
                    margin: '0 0 0 10',
                    handler: function () {
                    	me.search(me);
                    }
                },{
            		id: 'feedback_status',
                	xtype: 'combobox',
                	labelWidth: 60,
                	width: 160,
                	store: [
                            ['-1', '全部'],
                            ['0', '未处理'],
                            ['1', '已处理']
                        ],
                	fieldLabel: '处理状态',
                    editable: false,
                    value: '-1',
                    queryMode: 'local',
                    margin: '0 0 0 20',
                    listeners: {
                    	change: function() {me.search(me);}
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