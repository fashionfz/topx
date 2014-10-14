Ext.define('Topx.view.SysLogGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.sysLoggrid',
    title: '系统日志',
    store: 'SysLogGrid',
    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl : new Ext.XTemplate(
        	'<tpl for="files">',
            "<table border='1' cellpadding='1' cellspacing='1' bordercolor='#2edaff' width='100%'><tr><td><img src='"+cdnUrl+"/assets/system/ext/shared/extjs/images/fam/txtFile.png'/>{childFileName}<a href='javascript:void(0);' onclick='window.location.href=\"/system/sysLog/download/{parentName}/{childFileName}\"' target='_blank' style='color:red'>[下载]</a></td><td style='width:155px'>{childCreateTime}</td><td style='width:155px;'>{childFileType}</td><td style='width:155px;'>{childFileSize}</td></tr></table>",
            '</tpl>')
    }],
    initComponent: function() {
        var me = this;
        Ext.applyIf(me, {
        	columns: [
			{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'filename',
                text: '名称',
                width: 300,
                renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
                	return "<img src='"+ cdnUrl + "/assets/system/ext/shared/extjs/images/fam/folder-open.gif'/>" + '&nbsp;' + value;
                }
            },{
			    xtype: 'gridcolumn',
			    sortable: false,
			    dataIndex: 'createTime',
			    text: '修改日期',
			    width: 160
			},{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'fileType',
                text: '类型',
                width: 160
            },{
                xtype: 'gridcolumn',
                sortable: false,
                dataIndex: 'fileSize',
                text: '大小',
                width: 160
            }],
            bbar: {
        		xtype:'pagingtoolbar',
                store:'SysLogGrid',
                displayInfo:true, 
                displayMsg:'显示第 {0} 条到 {1} 条记录，一共  {2} 条',
                emptyMsg: "没有记录"
            }
        });
        var store = Ext.getStore(me.store);
        store.load();
        me.callParent(arguments);
    },
    
    search: function(me) {
    	var store = Ext.getStore(me.store);
    	store.currentPage = 1;
    	store.load();
    }
});