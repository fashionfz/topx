Ext.define('Topx.view.IntroImgEditWin', {
	extend : 'Ext.window.Window',
	alias : 'widget.introimgeditwin',
	iconCls : 'helome-app-edit',
	layout: 'fit',
	modal: true,
	initComponent : function() {
	    this.items = [
	        {
	            xtype: 'form',  
	            bodyPadding: 5,
	            border: false,
                items: [
                    {
                        name : 'id',
                        xtype : 'hiddenfield'
                    }, {
                        name: 'device',
                        fieldLabel: '设备', 
                        xtype: 'combobox',
                        store: [['android', '安卓'], [ 'iphone', 'iphone']],
                        value: 'android',
                        queryMode: 'local'
                    }, {
                        name: 'uri',
        				xtype: 'textareafield',
        				width: 310,
        				grow: true,
        				fieldLabel: 'URI'
        			}, {
        			    name: 'seq',
                        xtype : 'numberfield',
                        fieldLabel: '顺序(越大越靠前)',
                        allowBlank: false,
                        value: 0
                    }, {
                        xtype: 'displayfield',
                        fieldLabel: '原图片',
                        name: 'imgUrl',
                        width: 310,
                        height: 120,
                        renderer: function(val, obj) {
                            if (val == "") {
                                obj.hide();
                                return '无';
                            } else {
                                return '<img style="max-height:120px;width:100%" src="' + val + '"/>';
                            }
                        }
                    }, {
                        xtype: 'filefield',
                        emptyText: '请选择图片',
                        fieldLabel: '上传图片',
                        name: 'imgFile',
                        buttonText: '选择'
                    }
                ]
	        }
	    ];
	    
	    this.buttons = [
            {
    	        text: '取消',
    	        action: 'cancel'
            }, 
            {
			    text: '提交',
			    action: 'submit'
	        }
        ];

		this.callParent(arguments);
	}

});