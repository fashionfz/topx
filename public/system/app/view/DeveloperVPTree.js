Ext.define("Topx.view.DeveloperVPTree", {
    extend: 'Ext.tree.Panel',
    alias: 'widget.DeveloperVPTree',
    id: 'menuTreePanel',
    title: "功能导航",
    margins: '0 0 0 3',
    animate: true,
    store: 'DeveloperVPTree',
    autoScroll: true,
    rootVisible: false,
    loadMsg: true,
    collapsible: true,
    useArrows:true,
    split: true,
    tools: [{
        type: 'expand',
        handler: function () { Ext.getCmp("menuTreePanel").expandAll(); },
    	scope : this
    }, {
        type: 'collapse',
        handler: function () { Ext.getCmp("menuTreePanel").collapseAll(); }
    }],　　//这里不要忘记
//    mixins: {
//        treeFilter: 'WMS.view.TreeFilter'
//    },
    tbar: [{
        xtype: 'trigger',
        triggerCls: 'x-form-clear-trigger',
        onTriggerClick: function () {
            this.setValue('');
            Ext.getCmp("menuTreePanel").clearFilter();
        },
        width:'100%',
        emptyText:'快速检索功能',
        enableKeyEvents: true,
        listeners: {
            keyup: {
                fn: function (field, e) {
                    if (Ext.EventObject.ESC == e.getKey()) {
                        field.onTriggerClick();
                    } else {
//                        Ext.getCmp("menuTreePanel").filterByText(this.getRawValue());
                    }
                }
            }
        }
    }]
});