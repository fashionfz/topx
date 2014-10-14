Ext.define("Topx.store.IntroImgGrid", {
	extend: 'Ext.data.Store',
	model: 'Topx.model.IntroImgGrid',
	//autoLoad: {params:{device: 'android'}},
	autoLoad: false,
    proxy: {
        type: 'ajax',
        url: '/system/introimg/list',
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});