Ext.define('Topx.view.MenuGrid', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.menugrid',
	title: "功能菜单管理",
    frame: true,
    layout: 'border',
    initComponent : function() {
    	var me = this;
    	//左边功能菜单树
    	var tree = Ext.create('Ext.tree.Panel',{
    		store: 'MenuGrid',
    	    width: 250,
    	    height: 300,
    	    region:'west'
    	});  
    	
    	var submit = Ext.create('Ext.Button', {
    	    text: ' 保存  ',
    	    width: 80,
    	    renderTo: Ext.getBody(),
    	    handler: function() {
    	        input.getForm().submit({
        			clientValidation : true,
        			submitEmptyText : false,
        			url : '/system/update_menu',
        			method : 'POST',
        			waitTitle : "提示",
        			waitMsg : '正在提交数据，请稍后 ……',
        			success : function(form, action) {
        				tree.store.load();
        				Ext.Msg.alert('消息', action.result.msg);
        			},
        			failure : function(form, action) {
        				Ext.Msg.alert('消息', action.result.msg);
        			},
        			scope : this
        		});
    	    }
    	});
    	//右上边form输入
    	var input = Ext.create('Ext.form.Panel',{
			id : 'menuEditForm',
			xtype : 'form',
			border : false,
			header : false,
			region:'north',
			items : [ {
				xtype : 'fieldset',
				title : '菜单功能表单',
				width : 800,
				items : [ {
					id : 'idMenu',
					name : 'id',
					xtype : 'hiddenfield'
				}, {
					id : 'urlMenu',
					name : 'url',
					xtype : 'field',
					labelWidth : 80,
					fieldLabel: 'url'
				}, {
					id : 'nameMenu',
					name : 'name',
					xtype : 'field',
					labelWidth : 80,
					fieldLabel: '名称'
				}, {
					id : 'remarkMenu',
					name : 'remark',
					xtype : 'field',
					labelWidth : 80,
					fieldLabel: '备注'
				}, {
					id : 'extIdMenu',
					name : 'extId',
					xtype : 'field',
					labelWidth : 80,
					fieldLabel: 'extId'
				}, {
					id : 'tabxtypeMenu',
					name : 'tabxtype',
					xtype : 'field',
					labelWidth : 80,
					fieldLabel: 'tabxtype'
				},submit]
				
			}]
			
    	});
    	//有下班子功能菜单展示
    	var detail = Ext.create('widget.menudetailgrid',{option: tree.store});
    	//树select事件
    	tree.getSelectionModel().on('select', function(selModel, record) {
	  		var store = detail.store;
	  		var params = { 
	  			id:record.get('id')
	  		}; 
	  		Ext.apply(store.proxy.extraParams, params); 
	  		store.reload();
	  		//初始化form
	  		Ext.Ajax.request({
	  			url:'/system/get_menu_by_id?id='+record.get('id'),
	  			method:'GET',
	  			success: function(resp,opts) { 
	  				if(resp.responseText != null && resp.responseText!='') {
		  				var rec = Ext.decode(resp.responseText)
						var formData = {};
						formData.id = rec.id;
						formData.url = rec.url;
						formData.name = rec.name;
						formData.remark = rec.remark;
						formData.extId = rec.extId;
						formData.tabxtype = rec.tabxtype;
						input.getForm().setValues(formData);
	  				}else{
						var formData = {};
						formData.id = null;
						formData.url = null;
						formData.name = null;
						formData.remark = null;
						input.getForm().setValues(formData);
	  				}
	  			}
	  		});
        });
    	
		Ext.applyIf(me, {
			items : [tree, {
	            layout: 'border',
	            id: 'layout-browser',
	            region:'center',
	        	loadMask : true,
	        	forceFit : true, //forceFit 属性: 表示除了auto expand（自动展开）外，还会对超出的部分进行缩减，让每一列的尺寸适应GRID的宽度大小，阻止水平滚动条的出现。
	        	autoScroll:true,
	            items: [input, detail]
			}]
		});
        var store1 = tree.store;
        store1.load();
        me.callParent(arguments);
    }
});