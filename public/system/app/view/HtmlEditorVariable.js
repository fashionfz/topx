Ext.define('KeyValue', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'key',     type: 'string'},
        {name: 'value',      type: 'string'}
    ]
});
/**
*/
Ext.define('Topx.view.HtmlEditorVariable', {
    extend: 'Ext.util.Observable',
    alias: 'widget.html_HtmlEditorVariable',
    langTitle: '插入变量',
    langIconCls: 'helome-app-edit',
    init: function (view) {
        var scope = this;
        view.on('render', function () {
            scope.onRender(view);
        });
    },
    
	constructor: function (config) {
        this.templateId = config.option;
        this.callParent();
    },
 
    /**
    * 添加"插入图片"按钮
    * */
    onRender: function (view) {
        var scope = this;
        view.getToolbar().add({
            iconCls: scope.langIconCls,
            tooltip: {
                title: scope.langTitle,
                width: 60
            },
            handler: function () {
                scope.showImgWindow(view);
            }
        });
    },
 
    /**
    * 显示"插入图片"窗体
    * */
    showImgWindow: function (view) {
        var scope = this;
		var grid2 = Ext.create('Ext.grid.Panel', {
		    id: 'grid2',
		    store: new Ext.data.Store({
				autoDestroy : true,
				autoLoad: false,
				model : 'KeyValue',
				proxy : {
					type : 'ajax',
					reader : 'json',
					url : '/system/get_variable?templateId='+scope.templateId
				}
			}),
		    columns: [
		        {text: "变量名称", width: 170, dataIndex: 'key'},
		        {text: "变量描述", width: 200, dataIndex: 'value'},
		    ],
		    columnLines: true,
		    width: 450,
		    height: 250,
		    frame: false,
		    title: false,
		    iconCls: 'icon-grid'
		});
		
		grid2.on('itemdblclick', function( grid, record, item, index, e, eOpts ){
			scope.insertVariable(view,record.data.key);
			grid.up('window').close();
		});
        Ext.create('Ext.window.Window', {
            width: 400,
            height: 305,
            title: scope.langTitle,
            layout: 'fit',
            autoShow: true,
            modal: true,
            resizable: false,
            maximizable: false,
            constrain: true,
            plain: true,
            enableTabScroll: true,
            border: false,
            items: grid2
        });
        grid2.store.load();
    },
 
    /**
    * 插入变量
    * */
    insertVariable: function (view, data) {
        var str = '@'+data;
        view.insertAtCursor(str);
    }
});