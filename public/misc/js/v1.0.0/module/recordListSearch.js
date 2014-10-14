/**
 * @description: 会话记录--查找模块
 * @author: zhiqiang(zhiqiang.zhou@helome.com)
 * @update:
 */
define('module/recordListSearch', [
    'common/interface',
    'common/util',
    'module/prettifyForm',
    'module/datePickerB',
    'module/pinyin'
], function (inter, util, prettifyForm, datePicker, pinyin) {
    var $slt = $('.search-left-top'),
        $slb = $('.search-left-body'),
        $advanced = $slt.find('.advanced'),
        $searchDelete = $slb.find('.search-delete'),
        $nl = $('.name-list'),
        $searchName = $('#searchName'),
        $searchBtn = $('#searchBtn'),
        isAdvance = false,
        $key1 = $('#keyWord'),
        $key2 = $('#keyWord2');
//    /*暂时屏蔽的*/
//    $('.search-box').hide();
//    $('.user-info-map').hide();
//    /*end 屏蔽*/
    var selectTime = prettifyForm.initSelect({
            width: 220,
            target: $('#searchTime')
        }),
        selectType = prettifyForm.initSelect({
            width: 220,
            target: $('#searchType')
        }),

        closeAdvanced = function () {
            isAdvance = false;
            $slb.addClass('none');
            $key1.focus();
            $key1.prop('disabled',false);
            $slt.css({'border-radius': '5px'});
        },
        openAdvanced = function () {
            $key1.val('');
            isAdvance = true;
            $slb.removeClass('none');
            $searchName.focus();
            $key1.prop('disabled',true);
            $slt.css({'border-radius': '5px 5px 0 0'});
        },


        bindEvent = function (callback) {

            $advanced.on('click', function () {
                if($slb.hasClass('none')){
                    openAdvanced();
                }else{
                    closeAdvanced();
                }
            });
            $searchDelete.on('click', function () {
                closeAdvanced();
            })

            $searchBtn.on('click', function () {
                var userName = $searchName.val(),
                    key1 = $key1.val(),
                    key2 = $key2.val(),
                    time = $('#searchTime').val(),

                    type = $('#searchType').val(),

                    date = new Date(),
                    startTime = '',
                    endTime = util.dateFormat(new Date().setDate(date.getDate() + 1), 'yyyy-MM-dd');


                switch (time) {
                    case '-1d':
                        startTime = date.setDate(date.getDate() - 1);
                        break;
                    case '-3d':
                        startTime = date.setDate(date.getDate() - 3);
                        break;
                    case '-7d':
                        startTime = date.setDate(date.getDate() - 7);
                        break;
                    case '-1m':
                        startTime = date.setMonth(date.getMonth() - 1);
                        break;
                    case '-3m':
                        startTime = date.setMonth(date.getMonth() - 3);
                        break;
                    case '-6m':
                        startTime = date.setMonth(date.getMonth() - 6);
                        break;
                    case '-1y':
                        startTime = date.setFullYear(date.getFullYear() - 1);
                        break;
                    default:
                        startTime = '';
                        endTime = '';
                        break;
                }

                if (callback) {
                    if(isAdvance){
                        if(key2 == '' && userName == ''){
                            $.alert('内容和姓名不能都为空!');
                            return;
                        }
                        var json = {
                            'startDate': startTime == '' ? '': util.dateFormat(startTime, 'yyyy-MM-dd'),
                            'endDate': endTime,
                            'time': time,
                            'keyWords': key2,
                            'type': type

                        };

                        if(window.chatType == 2){
                            json.groupName = userName;
                        }else{
                            json.userName = userName;
                        }

                        if(userName == ''){
                            $.confirm(
                                '建议您填上联系人姓名，否者搜索会持续较长时间且匹配成功几率较低!',
                                function(){
                                    callback(json);
                                },
                                function(){},
                                {
                                    okValue : '返回填写',
                                    cancelValue : '继续搜索'
                                }
                            )
                        }else{
                            callback(json)
                        }

                    }else{
                        if(key1 == ''){
                            return;
                        }
                        callback({
                            'keyWords': key1,
                            'binding': 1
                        })
                    }

                }
            })

            /* name list*/
            $('.J-name-show').on('click', function () {
                $nl.toggleClass('none');
            });
            $('body').on('click', function (event) {
                var t = event.target;
                if (!$(t).closest('.J-name').length) {
                    $nl.addClass('none');
                }
            })

        },
        _move = function (direction) {
            var selected = $nl.find('li.selected');
            if (selected.length) {
                var nextSelect = direction === 'up' ? selected.prev('li') : selected.next('li');
            } else {
                var nextSelect = direction === 'up' ? $nl.find('li').first() : $nl.find('li').first().next('li');
            }
            if (nextSelect.size()) {

                selected.removeClass('selected');
                nextSelect.addClass('selected');

                var outerHeight = nextSelect.outerHeight();
                var listHeight = $nl.height();
                var top = nextSelect.position().top;
                if (top + outerHeight > listHeight) {
                    $nl.scrollTop(top + outerHeight + $nl.scrollTop() - listHeight);
                } else if (top < 0) {
                    $nl.scrollTop($nl.scrollTop() + top);
                }
            }
        },
        init = function (callback) {

            bindEvent(callback);
            util.setAjax(
                inter.getApiUrl().getChatUser,
                '',
                function (data) {
                    var newData = [];
                    var nameList = '';
                    for (var i = 0; i < data.length; i++) {
                        if (window.chatType == 1 && data[i].type == 'person') {
                            newData.push({name: data[i].userName});
                            nameList += '<li class="ellipsis db' + (i == 0 ? 'selected' : '') + '" title="'+ data[i].userName +'">' + data[i].userName + '</li>';
                        } else if (window.chatType == 2 && data[i].type == 'group') {
                            newData.push({name: data[i].groupName});
                            nameList += '<li class="ellipsis db' + (i == 0 ? 'selected' : '') + '" title="'+ data[i].groupName +'">' + data[i].groupName + '</li>';
                        }

                    }
                    $nl.append(nameList);
                    $('.name-list .ellipsis').poshytip({
                        showAniDuration: 0,
                        alignTo: 'target',
                        alignX: 'inner-left',
                        offsetY: -2,
                        offsetX: 0
                    });
                    $searchName.on('keyup', function (event) {
                        switch (event.keyCode) {
                            case 13: // enter
                                var ls = $nl.find('li.selected');
                                if (ls.length) {
                                    if (!$nl.hasClass('none')) {
                                        $searchName.val(ls.text());
                                        $nl.addClass('none');
                                    }
                                }
                                break;
                            case 16: // shift
                            case 17: // ctrl
                            case 37: // left
                            case 39: // right
                                break;
                            case 38: //up
                                _move('up');
                                break;
                            case 40: //down
                                _move('down');
                                break;
                            default :
                                if ($(this).val().length > 0){
                                    $nl.addClass('none');
                                }

                                break;
                        }
                    });

                    $.each($nl.find('li'), function (index, element) {
                        var that = $(element)
                        that.on('click', function () {
                            $searchName.val(that.text());
                            $nl.addClass('none');
                        });
                        that.on('mouseenter', function () {
                            $nl.find('li.selected').removeClass('selected');
                            that.addClass('selected');
                        })
                    })
                    var bb = $('#searchName').AutoComplete({
                        'minChars': 0,
                        'follow': true,
                        'width': 238,
                        'maxHeight': 160,
                        'itemHeight': 27,
                        'listStyle': 'custom',
                        'createItemHandler': function (i, n) {
                            return '<span style="display: table-cell;vertical-align: middle;padding-left: 6px">' + n.name + '</span>';
                        },
                        'afterSelectedHandler': function (data) {
                            $('#searchName').val(data.name);
                        },
                        'matchHandler': function (key, item) {
                            var value = item.name,
                                qp = pinyin.getQP(value), //全拼
                                jp = pinyin.getJP(value),//简拼,
                                hp = pinyin.getHP(value); //
                            if (value.indexOf(key) != -1) {
                                return true;
                            }
                            if (key.length = 1) {
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
                        'data': newData
                    });
                },
                function (error) {
                    util.trace('err: ' + error);
                },
                "GET"
            )
        },
        setData = function(json){
            var json = json || {};
            if(json.binding == 1){
                if(json.keyWords){
                    $key1.val(json.keyWords);
                }
            }else{
                if(json.keyWords){
                    $key2.val(json.keyWords);
                }
                if(json.userName){
                    $searchName.val(json.userName);
                }
                if(json.groupName){
                    $searchName.val(json.groupName);
                }
                if(json.type){
                    console.log('type = '+ json.type);
                    var str = '文本';
                    switch(parseInt(json.type)){
                        case 1:
                            str = '图片';
                            break;
                        case 2:
                            str = '文件';
                            break;
                        case 3:
                            str = '文本';
                            break;
                        case 4:
                            str = '语音';
                            break;
                        default:
                            str = '文本';
                            break;
                    }
                    selectType.setSelectVal($('#searchType').next('.select'),str);
                }
                if(json.time){
                    console.log('start time = '+ json.startDate);

                    var str= '全部';
                    switch(json.time){
                        case '-1d':
                            str = '最近1天';
                            break;
                        case '-3d':
                            str = '最近3天';
                            break;
                        case '-7d':
                            str = '最近7天';
                            break;
                        case '-1m':
                            str = '最近1月';
                            break;
                        case '-3m':
                            str = '最近3月';
                            break;
                        case '-6m':
                            str = '最近半年';
                            break;
                        case '-1y':
                            str = '最近1年';
                            break;
                        case 'all':
                        default:
                            str = '全部';
                            break;
                    }
                    selectType.setSelectVal($('#searchTime').next('.select'), str);
                }
            }
        }
    return {
        init: init,
        setData: setData
    }
});