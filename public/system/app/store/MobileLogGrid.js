Ext.define("Topx.store.MobileLogGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.MobileLogGrid',
    //autoLoad: {params:{device: 'android'}},
    autoLoad: false,
    proxy: {
        type: 'ajax',
        url: '/system/mobileclientlog/list',
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});