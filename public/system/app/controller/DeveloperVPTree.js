Ext.define('Topx.controller.DeveloperVPTree', {

	extend : 'Ext.app.Controller',

	refs : [ {
		ref : 'developervptree',
		selector : 'DeveloperVPTree'
	}, {
		ref : 'VPTabPanel',
		selector : '#vptabpanel_vptabpanel'
	} ],

	stores : [ 'DeveloperVPTree' ],

	init : function() {
		this.control({
			'DeveloperVPTree' : {
				itemclick : this.onTreeNodeClick
			}
		});
	},

	onTreeNodeClick : function(self, store_record, html_element, node_index,
			event) {

		if(this.getVPTabPanel().queryById(store_record.data.id+"-tab") == null && store_record.data.id != 'bc_developer'){
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
