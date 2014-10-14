/**
 * @description: 网页IM初始模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 **/
define('module/imTips',[
    'common/interface',
    'common/util',
    'module/cookie'
],function(inter, util, cookie){
    var chatPromptTpl =[
            '<p class="chat-warn">#{content}</p>'
        ].join('');

    return {
        init : function(){
            var self = this,
                $chatBox = $('<div id="chatBox">'),
                curUid = parseInt(cookie.get('_u_id')) || 0,
                isOpenChat = $.parseJSON(cookie.get('isOpenChat'+curUid)) || {};
            if(!$('#chatbox').length){
                $('body').append($chatBox);
            }
            if(!$.isEmptyObject(isOpenChat) && !isOpenChat.closeChatWindow){
                self.loadJs();
            }
            //信息提示
            self.prompt();
        },
        /**
         * 加载webIM所需Js
         */
        loadJs : function(){
            var $chatBox = $('#chatBox'),
                chatWarn = $chatBox.find('.chat-warn');
            chatWarn.prepend('<div class="chat-load-js"></div>');
            ued_import('chatJs', 'js', null, 1);
        },
        /**
         * 添加右下角的 消息提示框
         */
        prompt : function(){
            var self = this,
                content = '最近联系人',
                box = $('#chatBox'),
                warn = box.find('.chat-warn'),
                tpl = $(util.template(chatPromptTpl,{content: content}));

            if( !warn.length ){
                box.append(tpl);
            }
            tpl.on('click',function(){
                if(!window.isLoadChat){
                    self.loadJs();
                }
            })
        },
        /**
         * 打开IM并定位会话
         */
        fireChat : function(chatData){
            var self = this,
                curId = parseInt(cookie.get('_u_id')) || 0,
                isOpenChat = $.parseJSON(cookie.get('isOpenChat'+curId)) || {};

            if(curId){
                if(!chatData.groupId && curId == chatData.userId){
                    $.alert('不能和自己聊天哟！');
                }else{
                    if(!window.isLoadChat){
                        chatData.closeChatWindow = false;
                        cookie.set('isOpenChat'+curId, JSON.stringify(chatData));
                        self.loadJs();
                    }else{
                        if(!$.isEmptyObject(isOpenChat) && isOpenChat.closeChatWindow){
                            chatAct.openChat();
                        }
                        chatAct.setCurChat(chatData);
                    }
                }
            }else{
                location.href = '/login?msg=1&referer=' + encodeURIComponent(location.href);
            }
        }
    }

})