/**
 * @description: english resume
 * @author: zhiqiang.zhou@helome.com
 */
define('module/enResume', ['common/util', 'common/interface', 'module/prettifyForm'], function(util, inter, prettifyForm){

    var isForbidAddResumeTask = $('#isForbidAddResumeTask').val(),
        htmlTpl =[
            '<div class="fr en-resume-box">',
                '<div class="btn-default btn-xs btn-green btn-en-resume" >',
                    '<span class="icon icon-pen-white fl"></span><span class="fl">发布简历到海外</span>',
                '</div>',
            '</div>'
        ].join(''),
        progressTpl = [
            '<div class="fr en-resume-box">',
                '<div class="btn-default btn-xs btn-white" >',
                    '<span class="fl">海外简历翻译中...</span>',
                '</div>',
            '</div>'
        ].join('');

    return{
        init: function(target){
            var $resume = $(htmlTpl);
            isForbidAddResumeTask = isForbidAddResumeTask == 'true' || isForbidAddResumeTask == '1';
            if(isForbidAddResumeTask){
                $resume = $(progressTpl);
                $resume.on('click',function(){
                    $.alert(['<h4>您已授权嗨啰翻译中心填写英文海外简历，预计<br>1-3个工作日内完成。</h4>',
                        '<div class="f14 pt20">',
                        '发布海外简历：个人简历将发布到helome国际<br>版，方便国外用户找到您。',
                        '<a href="/questions#question_translate" target="_blank" title="查看帮助"><i class="icon icon-tips pr" title=""></i></a>',
                        '</div>'].join(''));
                })
            }else{
                $resume.on('click',function(){
                    var api = $.dialog({
                        id: 'resume',
                        fixed: true,
                        lock: true,
                        content: [
                            '<div class="dialog-panel">',
                                '<div class="dialog-tit">',
                                    '<div class="span-left">发布简历到海外</div>',
                                '</div>',
                                '<table class="dialog-table">',
                                    '<tr>',
                                        '<td class="f14">',
                                            '发布海外简历：个人简历将发布到helome国际版，方便<br>国外用户找到您。',
                                            '<a href="/questions#question_translate" target="_blank" title="查看帮助"><i class="icon icon-tips pr" title=""></i></a>',
                                        '</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td class="pt20"><h4>请选择翻译语言(暂时只开放英文)</h4></td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td class="resume-content pt20 pb20">',
                                        '<div style="overflow: hidden"><label value="1"><input checked="checked" type="checkbox"><span>英文</span></label></div>',
                                        '<div class="en-resume-error none mt10">请选择语言</div></td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td><p>有什么特殊要求留给翻译中心</p><textarea id="remark" class="textarea w350 mt10 mb10" maxlength="60"></textarea>',
                                        '<div class="tips">内容限制60个字符，还可以输入<b>60</b>个字符</div></td></tr>',
                                '</table>',
                            '</div>'
                        ].join(''),
                        okValue:'授权嗨啰翻译中心',
                        ok: function(){
                            var remark = $('#remark').val(),
                                checkbox = $('.resume-content input[type=checkbox]'),
                                lang =[];

                            checkbox.each(function(i,n){
                                if($(n).prop('checked')){
                                    lang.push($(n).parent('label').attr('value'));
                                }
                            });
                            if(lang.length ==0 ){
                                $('.en-resume-error').removeClass('none');
                            }else{
                                var tips = $.tips('loading');
                                //发布英文简历
                                util.setAjax(
                                    inter.getApiUrl().addEnResume,
                                    {remark: remark},
                                    function(json){
                                        tips.close();
                                        if(json.status == 1){
                                            api.close();
                                            $.alert('<h4>已授权嗨啰翻译中心为您填写英文海外简历，</h4><h4>预计1-3个工作日内完成。</h4>');
                                            progressTpl = $(progressTpl);
                                            $resume.replaceWith(progressTpl);
                                            progressTpl.on('click',function(){
                                                $.alert('<h4>您已授权嗨啰翻译中心填写英文海外简历，<br>预计1-3个工作日内完成。</h4>');
                                            })
                                        }else{
                                            $.alert('<h4>发布失败！请联系客服，或稍后重试!</h4>');
                                        }
                                    },
                                    function(){
                                        tips.close();
                                        $.alert('<h4>发布失败！请联系客服，或稍后重试!</h4>');
                                    },
                                    'GET'
                                );
                            }
                            return false;
                        },
                        cancelValue:'取消',
                        cancel: true,
                        initialize: function(){
                            var self = this;

                            util.wordsCount('#remark', '.dialog-table .tips b', 60);
                            $('.d-buttons .btn-default').css({'width':'145px','padding':'8px 0px'});

                            //初始化事件 选择
                            var check = prettifyForm.initCheckbox({target:$('.dialog-panel')}),
                                checkList = check.get();
                            $.each(checkList,function(i,n){
                                $(n).on('click',function(){
                                    $('.en-resume-error').addClass('none');
                                })
                            })
                        }
                    });
                })
            }
            $(target).append($resume);
        }
    }

});