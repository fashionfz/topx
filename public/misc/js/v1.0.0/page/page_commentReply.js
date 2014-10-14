/**
 * @description: 回复评价
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon'
],function(inter, util, common){
    common.initLogin();

    var initComment = function(){
        var replyBtn = $(".comment-reply .comment-reply-btn");
        // 评价内容计数
        util.wordsCount('.comment-reply-input', '.comment-reply .tips b', 135);

        replyBtn.on("click", function(){
            var replyInput = $.trim($(".comment-reply .comment-reply-input").val()),
                id = $('#parentId').val(),
                tpl = [
                    '<div class="finish">',
                        '<a href="/expert/detail/#{userId}" target="_blank"><img src="#{headUrl}" alt="#{userName}" title="#{userName}"></a>',
                        '<div class="finish-main">',
                            '<span class="finish-content">#{content}<span class="finish-date">#{time}</span></span>',
                        '</div>',
                    '</div>'
                ].join('');
            if (!replyInput || replyInput.length<1) {
                $.alert('请输入回复内容！');
            }else{
                if ( replyInput.length > 135 ) {
                    $.alert('回复的内容限制135个字符！');
                }else{
                    var tips = $.tips('loading');
                    util.setAjax(inter.getApiUrl().addReplyUrl, {
                        'parentId': id,
                        'type': 'expert',
                        'content': replyInput
                    }, function(json){
                        tips.close();
                        if(json.error){
                            $.alert(json.error);
                        }else{
                            $('.comment-reply .start').before(util.template(tpl, {
                                expertId : json.comment.expertId,
                                userId : json.comment.userId,
                                userName : json.comment.username,
                                content: util.safeHTML(replyInput),
                                headUrl : json.comment.headUrl,
                                time : json.comment.commentTime
                            }));
                            $(".comment-reply .comment-reply-input").val('');
                            $('.comment-reply .tips b').text(135);
                        }
                    }, function(){
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试。');
                    });
                }
            }
        });
    }

    initComment();
});