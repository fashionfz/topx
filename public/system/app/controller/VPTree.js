Ext.define('Topx.controller.VPTree', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'vptree',
		selector : 'VPTree'
	}, {
		ref : 'VPTabPanel',
		selector : '#vptabpanel'
	} ],

	stores : [ 'VPTree' ],

	init : function() {
		this.control({
			'VPTree' : {
				itemclick : this.onTreeNodeClick
			}
		});
	},

	onTreeNodeClick : function(self, store_record, html_element, node_index,
			event) {

		if(this.getVPTabPanel().queryById(store_record.data.id+"-tab") == null && store_record.data.id != 'bc'){
			if(store_record.raw.tabxtype)
				this.getVPTabPanel().add({
					id : store_record.data.id+"-tab",
		            closable: true,
		           	xtype: store_record.raw.tabxtype
		        }).show();
			else
				this.getVPTabPanel().add({
					id : store_record.data.id+"-tab",
		            closable: true,
		            title: store_record.data.text
		        }).show();
		}else{
			this.getVPTabPanel().setActiveTab(store_record.data.id+"-tab");
		}
	}
});
