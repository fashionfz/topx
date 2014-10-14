/**
 * @description: 用户中心--》添加群组成员
 * @author: zhiqiang.zhou@helome.com
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/pinyin',
    'module/cookie',
    'module/imTips'
], function (inter, util, common, pinyin, cookie, imTips) {
    common.initLogin();

    var leftBox = $('.user-box').eq(0),
        rightBox = $('.user-box').eq(1),
        btnOk = $('#addMemberBtn'),
        btnChat = $('.J-open-chat'),
        gid = util.location('gid') || 0,
        check = [],
        userTemplate = ['<li class="user-common" data-uid="#{userId}">',
                            '<div class="fl check"><span class="checkbox"></span></div>',
                            '<div class="user-head fl">',
                            '<img src="#{headUrl}">',
                            '<span class="chat-icon user-head-i"></span>',
                            '</div>',
                            '<div class="user-name fl" title="#{userName}">#{userName}</div>',
                       '</li>'],

        /**
         * .user-list里添加人的方法
         * target ：添加的容器,
         * option :user 相关信息
         * onclick: li的onclick
         */
        pushUser = function(target, option, onclick){
            var isExists = false;
            if($.isArray(option)){
                getGroupMember(function(list){
                    $.each(option, function(i, n){
                        isExists = false;
                        if(list && list.length){
                            $.each(list, function(x, y){
                                if(y.userId == n.userId){
                                    isExists = true;
                                    return false;
                                }
                            });
                            if(isExists){
                                n.checked = true;
                                n.disabled = true;
//                                pushUser(rightBox.find('.user-list'), n, function(obj, t){});
//                                setCheckedNum();
                            }
                        }
                        pushUser(target, n, onclick);
                    });
                });
                return;
            }
            var $tpl = $(util.template(userTemplate, option));
            if(option.checked){
                $tpl.find(".checkbox").addClass('checked');
            }
            if(option.disabled){
                $tpl.find(".checkbox").addClass('disabled none');
            }
            if(onclick){
                $tpl.on('click',function(){
                    onclick(option, $tpl);
                })
            }
            target.append($tpl);
        },

        /**
         * 删除选中的人
         * 用户id
         */
        getUserById = function(id, target){
            var target = target || leftBox,
                item = target.find('li.user-common'),
                node = null;
            item.each(function(i, n){

                if($(n).attr('data-uid') == id ){
                    node =  $(n);
                    return false;
                }
            });
            return node;
        },

        /**
         * 获取选中的人
         * target : 目标
         * type : "checked" 选中的 , "unchecked"为选中的， other parameter  will reture all data
         * callBack : 回调，在获取每个user后 触发
         */
        getUser = function(target, type, callBack){
            var list =[],
                $userHtml = $(target).hasClass('user-common') ? $(target) : $(target).find('li.user-common');

            $userHtml.each(function(i, n){
                var that = $(n),
                    checked = that.find('.check .checkbox').hasClass('checked'),
                    disabled = that.find('.check .checkbox').hasClass('disabled');
                if(type == 'checked' && !checked) return true;
                if(type == 'unchecked' && checked) return true;
                list.push({
                    userId: that.attr('data-uid'),
                    userName: that.find('.user-name').attr('title'),
                    headUrl: that.find('img').attr('src'),
                    checked: checked,
                    disabled: disabled
                });
                if(callBack){
                    callBack(that);
                }
            });
            return list;
        },
        matchHandler = function (key, item) {
            if (key.length == 0 || item.indexOf(key) != -1) {
                return true;
            }

            var qp = pinyin.getQP(item), //全拼
                jp = pinyin.getJP(item),//简拼,
                hp = pinyin.getHP(item); //

            if (key.length == 1) {
                if (jp.indexOf(key) != -1) {
                    return true;
                }
            } else {
                if (qp.indexOf(key) != -1) {
                    return true;
                }
                if (jp.indexOf(key) != -1) {
                    return true;
                }
                if (hp.indexOf(key) != -1) {
                    return true;
                }
            }
            return false;
        },
        search = function (key, list) {
            var listBox = leftBox.find('.user-list'),
                listTemp = [];

            listBox.find('li').each(function(i, n){
                if(matchHandler(key, $(n).find('.user-name').prop('title'))){
                    $(n).removeClass('none');
                }else{
                    $(n).addClass('none');
                }
            });
        },
        /**
         * 设置已选联系人数量
         */
        setCheckedNum = function(){
            rightBox.find('.user-box-top .color-blue').text(rightBox.find('.user-common').length);
        },
        /**
         * 获取群成员
         */
        getGroupMember = function(sucCall, errCall){
            var url = util.strFormat(inter.getApiUrl().getGroupMember, [gid]),
                param = {};

            util.setAjax(url, param, function (json) {
                if (json.error) {
                    errCall && errCall(json);
                } else {
                    sucCall && sucCall(json);
                }
            }, function () {
                errCall && errCall();
            }, 'GET');
        },
        init = function(){
            //初始化 好友列表
            util.setAjax(
                inter.getApiUrl().queryFriends,
                {'page': 0, 'pageSize': 100},
                function (json) {
                    if (json.error) {
                        $.alert(json.error);
                    } else {
                        leftBox.find('.searchAt').on('keyup', function(){
                            search($(this).val(), json.list);
                        });
                        //将圈中好友添加到左边
                        pushUser(leftBox.find('.user-list'), json.list, function(o, that){
                            //当 左边 用户被点击后
                            var userList = getUser(that, 'all-check', function(){});
                            if(!userList[0].disabled){
                                if(userList[0].checked){
                                    getUserById(userList[0].userId, rightBox).remove();
                                }else{
                                    userList[0].checked = true;
                                    pushUser(rightBox.find('.user-list'), userList[0], function(obj, t){
                                        //当 右边 用户被点击后
                                        var leftUser = getUserById(obj.userId, leftBox);
                                        leftUser.find('.check .checkbox').removeClass('checked');
                                        t.remove();
                                        setCheckedNum();
                                    });
                                }
                                that.find('.check .checkbox').toggleClass('checked');
                                setCheckedNum();
                            }
                        });
                    }
                },
                function () {
                    $.alert('服务器繁忙，请稍后再试。');
                },
                'GET'
            );


            // 提交按钮
            btnOk.on('click',function(){
                if(!gid){
                    $.alert('参数错误，请重新进入此页面邀请。');
                    return;
                }
                var userList = getUser(rightBox,'checked',function(){}),
                    userIds = [];

                if(userList.length == 0){
                    $.alert('还有没选中任何好友!');
                    return;
                }

                $.each(userList,function(i,n){
                    userIds.push(n.userId);
                });
                var tips = $.tips();
                util.setAjax(
                    inter.getApiUrl().inviteJoinGroup,
                    {groupId: gid, userIds: userIds, content: ''},
                    function (json) {
                        if (json.status) {
                            window.location.href = '/user/group/addMemberSuccess?gid='+ gid +'&num=' + userIds.length;
                        } else {
                            $.alert(json.error);
                        }
                        tips.close();
                    },
                    function () {
                        tips.close();
                        $.alert('服务器繁忙，请稍后再试。');
                    }
                );

            });

            btnChat.on('click', function(){
                var chatId = util.location().gid,
                    groupData = {
                        groupId: chatId,
                        groupName: $('.group-name').text(),
                        groupType: 'normal',
                        groupAvatar: ''
                    };

                imTips.fireChat(groupData);
            })
        };


    init();

});
