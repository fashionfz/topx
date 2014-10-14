/**
 * @description: 预约
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'module/pageCommon',
    'module/userOrder'
],function(common, order){
    common.initLogin();
    order.init({
        container : '#orderContainer'
    });
});