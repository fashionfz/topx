Ext.define('Topx.view.SuggestionGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.suggestiongrid',
    title: '用户建议',
    store: 'SuggestionGrid',
    loadMask : true,
	forceFit : false, //forceFit 属性: 表示除了auto expand（自动展开）外，还会对超出的部分进行缩减，让每一列的尺寸适应GRID的宽度大小，阻止水平滚动条的出现。
	autoScroll:true,
	plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl : new Ext.XTemplate(
        	'<tpl for="attachInfos">',
        	"<p>图片：<a href='"+getWebIp()+"/{attachPath}' target='_blank'>{attachFileName}</a>		&nbsp;图片路径：{attachPath}</p>",
        	'</tpl>')
    }],
	
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	columns: [
            {
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'userName',
                text: '用户姓名',
                locked: true,
                width: 120,
                renderer:function(value){
                	return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
            },{
                xtype: 'gridcolumn',
                sortable: true,
                dataIndex: 'phone',
                text: '电话',
                locked: true,
                width: 120,
                renderer:function(value){
                	return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
            },{
                xtype: 'gridcolumn',
                sortable: true,
                dataIndex: 'email',
                text: '邮箱',
                locked: true,
                width: 120,
                renderer:function(value){
                	return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
            },{
                xtype: 'gridcolumn',
                sortable: true,
                dataIndex: 'qq',
                text: 'QQ',
                locked: true,
                width: 120,
                renderer:function(value){
                	return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
            },{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'content',
                text: '建议内容',
                width:480,
                renderer:function(value){
                	return Ext.String.format('<span title="{0}">{1}</span>',value,value);
                }
            },{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'href',
                text: '超链接地址',
                width:260,
                renderer:function(value){
                	return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
            },{
                xtype: 'gridcolumn',
                sortable: true,
                dataIndex: 'createTime',
                text: '时间',
                width: 200
            }],
            bbar: {
        		xtype:'pagingtoolbar',
                store:'SuggestionGrid',
                displayInfo:true, 
                displayMsg:'显示第 {0} 条到 {1} 条记录，一共  {2} 条',
                emptyMsg: "没有记录"
            },
            tbar: [me._getSearchBar(me)]
        });
        var store = Ext.getStore(me.store);
        store.on('beforeload', function (store, options) {
            var newParams = {
        		'searchText':Ext.getCmp('searchSuggestion').getValue()
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
            		id: 'searchSuggestion',
                	xtype: 'textfield',
                    fieldLabel: '用户',
                    labelWidth: 40,
                    width: 280,
                    listeners :{
                        specialKey : function(field, e) {
                            if (e.getKey() == Ext.EventObject.ENTER) {
                            	me.search(me);
                            }
                        }
                    }
                }, {
                	id: 'suggestiongrid-searchbutton',
                    xtype: 'button',
                    text: '搜索',
                    margin: '0 0 0 5',
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