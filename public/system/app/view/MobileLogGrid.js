Ext.define('Topx.view.MobileLogGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.mobileloggrid',
    title: '移动端客户端日志',
    forceFit: false, 
    store: 'MobileLogGrid',
    sortableColumns: false,
    disableSelection: true,
    initComponent: function() {
    	var me = this;
        this.columns = [
            {header: 'ID', dataIndex: 'id', width: 60},
            {header: '创建时间', dataIndex: 'createTime', renderer:Ext.util.Format.dateRenderer('Y-m-d H:i:s'), width: 140},
            {header: '设备', dataIndex: 'device', width: 60},
            {header: '日志', xtype:'templatecolumn', tpl: '<a href="javascript:void(0);" onclick="window.location.href=\'{logFileUrl}\'" target="_blank" style="color:red">[下载]</a>', width: 60},
            {header: '备注', dataIndex: 'description', flex: 5}
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
            }
        ];
        
        this.bbar = 
        {
            xtype:'pagingtoolbar',
            store:'MobileLogGrid',
            displayInfo:true, 
            displayMsg:'显示第 {0} 条到 {1} 条记录，一共  {2} 条',
            emptyMsg: "没有记录"
        };
        var store = Ext.getStore(me.store);
        store.load({
			params : {
				device : 'android'
			}
        });
        this.callParent(arguments);
    }
});
