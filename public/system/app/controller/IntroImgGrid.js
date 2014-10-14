Ext.define('Topx.controller.IntroImgGrid', {
    extend : 'Ext.app.Controller',
    views : [ 'IntroImgGrid', 'IntroImgEditWin' ],
    stores : [ 'IntroImgGrid' ],
    models : [ 'IntroImgGrid' ],

    init : function() {
        this.control({
            'introimggrid combobox[name=device]' : {
                change : this.queryByDevice
            },
            'introimggrid button' : {
                click : this.createIntroImg
            },
            'introimggrid actioncolumn' : {
                itemclick : this.actionBtnEvent
            },
            'introimgeditwin button' : {
                click : this.clickEditWinBtn
            }
        });
    },

    queryByDevice : function(combobox) {
        this.getStore('IntroImgGrid').reload({
            params : {
                device : combobox.getValue()
            }
        });
    },

    createIntroImg : function(btn) {
        var win = Ext.widget("introimgeditwin", {
            title : '创建轮播图  '
        });
        win.show();
    },

    actionBtnEvent : function(column, grid, rowIndex, colIndex, node, e,
            record, rowEl) {
        if (node.action == 'edit') {
            var win = Ext.widget("introimgeditwin", {
                title : '编辑轮播图  '
            });
            var formData = {};
            formData.id = record.data.id;
            formData.device = record.data.device;
            formData.uri = record.data.uri;
            formData.seq = record.data.seq;
            formData.imgUrl = record.data.imgUrl;
            win.down('form').getForm().setValues(formData);
            win.show();
        } else if (node.action == 'delete') {
            Ext.Msg.confirm('删除记录', '确认删除该条记录？', function(btn) {
                if (btn == 'yes') {
                    Ext.Ajax.request({
                        url : '/system/introimg/delete/' + record.data.id,
                        method : 'POST',
                        success : function(response) {
                            this.getStore('IntroImgGrid').reload();
                        },
                        failure : function(form, action) {
                            Ext.Msg.alert('消息', action.result.msg);
                        },
                        scope : this
                    });
                }
            }, this);
        }
    },

    clickEditWinBtn : function(btn) {
        if (btn.action == 'cancel') {
            btn.up('window').close();
        } else if (btn.action == 'submit') {
            var form = btn.up('window').down('form').getForm();
            form.submit({
                clientValidation : true,
                submitEmptyText : false,
                url : '/system/introimg/saveorupdate',
                method : 'POST',
                waitTitle : "提示",
                waitMsg : '正在提交数据，请稍后 ……',
                success : function(form, action) {
                    this.getStore('IntroImgGrid').reload();
                    btn.up('window').close();
                },
                failure : function(form, action) {
                    Ext.Msg.alert('消息', action.result.msg);
                },
                scope : this
            });
        }
    }
});