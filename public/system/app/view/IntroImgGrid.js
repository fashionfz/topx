Ext.define('Topx.view.IntroImgGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.introimggrid',
    title: '移动端首页轮播图',
    forceFit: false, 
    store: 'IntroImgGrid',
    sortableColumns: false,
    disableSelection: true,
    initComponent: function() {
    	var me = this;
        this.columns = [
            {header: 'ID', dataIndex: 'id', width: 60},
            {header: '图片', xtype:'templatecolumn', tpl: '<img style="max-height:120px;width:100%" src="{imgUrl}"/>', flex: 4},
            {header: '图片URL', xtype:'templatecolumn', tpl: '<div style="word-break:break-word;white-space:normal;">{imgUrl}</div>', flex: 5},
            {
                header: '图片尺寸',
                dataIndex: 'imgUrl',
                width: 65,
                renderer: function(val, meta, rec, r, c, store, view) {
                    var　img = new Image();   
                    img.src = val;
                    if(img.complete){
                        return img.width + 'x' + img.height;
                    } else {
                        img.onload = function() {
                            var row = view.getNode(rec);
                            var el = Ext.fly(Ext.fly(row).query('.x-grid-cell')[c]).down('div');
                            el.setHTML(img.width + 'x' + img.height);
                        }
                    }
                    return '?';
                }
            },
            {header: 'URI', dataIndex: 'uri', flex: 5},
            {header: '顺序', dataIndex: 'seq', width: 60},
            {header: '设备', dataIndex: 'device', width: 60},
            {
                xtype: "actioncolumn",
                width: 50,
                items: [ {
                    action: 'edit', 
                    icon: cdnUrl + "/assets/system/ext/shared/extjs/images/fam/paintbrush.png",
                    tooltip: '编辑',
                    handler: function (grid, rowIndex, colIndex, node, e, record, rowEl) {     
                        this.fireEvent('itemclick', this, grid, rowIndex, colIndex, node, e, record, rowEl);  
                    } 
                }, {
                    action: 'delete',
                    icon: cdnUrl + "/assets/system/ext/shared/extjs/images/fam/delete.png",
                    tooltip: '删除',
                    handler: function (grid, rowIndex, colIndex, node, e, record, rowEl) {     
                        this.fireEvent('itemclick', this, grid, rowIndex, colIndex, node, e, record, rowEl);  
                    } 
                }]
            }
        ];
        
        this.tbar = [
            {
                name: 'device',
                fieldLabel: '设备', 
                labelWidth: '40px',
                xtype: 'combobox',
                width: 180,
                store: [['android', '安卓'], [ 'iphone', 'iphone']],
                value: 'android',
                queryMode: 'local'
            },
            {
                text: '创建轮播图',
                iconCls: 'helome-add',
                action: 'create'
            }
        ];
        var store = Ext.getStore(me.store);
        store.load({
				params : {
					device : 'android'
				}
        });
        this.callParent(arguments);
    }
});
