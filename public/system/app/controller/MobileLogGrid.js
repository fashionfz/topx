Ext.define('Topx.controller.MobileLogGrid', {
    extend : 'Ext.app.Controller',
    views : [ 'MobileLogGrid' ],
    stores : [ 'MobileLogGrid' ],
    models : [ 'MobileLogGrid' ],

    init : function() {
        this.control({
            'mobileloggrid combobox[name=device]' : {
                change : this.queryByDevice
            }
        });
    },

    queryByDevice : function(combobox) {
        this.getStore('MobileLogGrid').reload({
            params : {
                device : combobox.getValue()
            }
        });
    }
});