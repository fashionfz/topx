/**
 * @description: 单页面js
 * @author: Young Foo(young.foo@helome.com)
 * @update: Young Foo(young.foo@helome.com)
 */

require([
    'module/pageCommon'
],function(common){
    common.initLogin();

    /**
     * 关于我们页面招聘信息展示效果
     */
    $('.J-job-title').on('click', function(){
        var self = $(this),
            dep = self.siblings('.J-job-dep');
        dep.toggle('fast');
    });
    $(document).scrollTop($(document).scrollTop()-60);
});