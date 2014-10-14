/**
 * @description: 用户预约模块
 * @author: Zhang Guanglin(zhangguang.lin@163.com)
 * @update: 2013-12-2
 */
define('module/userOrder', [
    'common/interface',
    'common/util',
    'module/prettifyForm'
], function(inter, util, prettifyForm){
    var $order = $('.order a'),
        sDateWarp = [
            '<div id="dateWarp">',
                '<div class="day" style="user-select:none;" onselectstart="return false;">',
                    '<div class="day-select clearfix">',
                        '<button type="button" class="btn-default btn-white btn-xs lr lr-l fl"><i class="icon"></i></button>',
                        '<button type="button" class="btn-default btn-white btn-xs lr lr-r fl"><i class="icon"></i></button>',
                        '<button class="btn-default btn-white btn-xs now fl">今天</button>',
                        '<div class="select-date fl">',
                            '<span id="cy"></span> 年&nbsp;',
                            '<span id="cm"></span> 月',
                        '</div>',
                        /*'<span class="icon icon-view-day fr"></span>',*/
                        '<span class="icon icon-view-month fr none" title="切换到月"></span>',
                    '</div>',
                    '<div id="dayAll"></div>',
                    '<div id="timeAll" style="-moz-user-select:none;" onselectstart="return false;" class="none"></div>',
                '</div>',
            '</div>'
        ].join(''),
        sSelectTimeTpl = [
            '<div class="select-time">',
                '<div class="select-title">#{selectTime}</div>',
                '<p class="pr z5">',
                    '<label>开始</label>',
                    '<span class="select-box">',
                        '<select class="order-time-hour-start" name="order-time-hour-start" id="order-time-hour-start">',
                            '#{startHoursList}',
                        '</select> 时 ',
                    '</span>',
                    '<span class="select-box">',
                        '<select class="order-time-minutes-start" name="order-time-minutes-start" id="order-time-minutes-start">',
                            '#{startMinutesList}',
                        '</select> 分',
                    '</span>',
                '</p>',
                '<p class="pr z4">',
                    '<label>结束</label>',
                    '<span class="select-box">',
                        '<select class="order-time-hour-end" name="order-time-hour-end" id="order-time-hour-end">',
                            '#{endHoursList}',
                        '</select> 时 ',
                    '</span>',
                    '<span class="select-box">',
                        '<select class="order-time-minutes-end" name="order-time-minutes-end" id="order-time-minutes-end">',
                            '#{endMinutesList}',
                        '</select> 分 ',
                    '</span>',
                '</p>',
                '<p class="selectTips totalTime clearfix"><em></em>共计<span>0</span>分钟</p>',
                '<p class="selectTips totalCost clearfix"><em></em>预付<span>0</span>元</p>',
                '<p class="selectTips existTime clearfix"><em></em>已被占用的时间</p>',
                '<ul class="existTimeList">',
                    '<li>00:00 - 00:00</li>',
                '</ul>',
                '<div class="bottom-btn">',
                    '<input type="button" class="btn-default btn-xs btn-green" id="getDate" value="确定"/>',
                    '<input type="button" class="btn-default btn-xs btn-white close-select-time" value="关闭"/>',
                '</div><em class="time-bg"></em>',
            '</div>'
        ].join(''),
        sOrderTimeTpl = [
            '<span class="area-tit">新的预约</span>',
            '<div class="select-time">',
                '<div class="select-title">#{selectTime}</div>',
                '<p>',
                    '<label>开始</label>',
                    '<span>#{startHour} 时 </span>',
                    '<span>#{startMin} 分 </span>',
                '</p>',
                '<p>',
                    '<label>结束</label>',
                    '<span>#{endHour} 时 </span>',
                    '<span>#{endMin} 分</span>',
                '</p>',
                '<p class="selectTips totalTime clearfix"><em></em>共计<span>#{totalMin}</span>分钟</p>',
                '<p class="selectTips totalCost clearfix"><em></em>预付<span>#{totalCost}</span>元</p>',
                '<div class="bottom-btn">',
                    '<input type="button" class="btn-default btn-xs btn-green" id="getDate" value="确定"/>',
                    '<input type="button" class="btn-default btn-xs btn-white close-select-time" value="关闭"/>',
                '</div><em class="time-bg"></em>',
            '</div>'
        ].join(''),
        sSelectTimeTplOption = '<option>#{time}</option>',
        sBtnOrderTime = '<button type="button" class="btn-default btn-yellow btn-xs btn-order-time"><i class="icon icon-order"></i><em>预约</em></button>',
        week = '<table><tr><th>日</th><th>一</th><th>二</th><th>三</th><th>四</th><th>五</th><th>六</th></tr><tr>',
        sHourAll = [
            '<table class="minute-table">',
            '#{minuteList}',
            '</table>'
        ].join(''),
        sMinuteTpl = '<tr data-date="#{currenMinRange}" class="#{isOpen}  #{hasOrderMinTr}"><td width="82">#{minute}</td><td width="763" class="#{isOpenArea}"></td></tr>',
        //设置到 8px 可兼容到ie7；
        maskHourLine = '<div style="position: absolute;width:100%;height: 8px;margin-top:-8px;background-color: #48ab23;z-index:1"></div>',
        dNow = new Date(new Date(serviceTime).getTime() + new Date().getTime() - clientTime.getTime()),
        sYear = dNow.getFullYear(),
        sMonth = dNow.getMonth()+1,
        sDate = dNow.getDate(),
        cy = null,
        sSelectDate = [],
        cm = null,
        record = [],
        bNotToday = false;

    /**
     * 定义DatePicker构造函数
     */
    function DatePicker(){

    };

    DatePicker.prototype = {
        _bindEvent : function(){
            var self = this;
            //绑定日历上的事件
            $('.current:not(.previous)').each(function(){
                var $this = $(this), $btnOrderTime;
                $this.hover(function(){
                    $btnOrderTime = $this.find('.btn-order-time');
                    if(!$btnOrderTime.length){
                        $btnOrderTime = $(sBtnOrderTime);
                        $btnOrderTime.on('click',function(e){
                            $('.btn-order-time').addClass('none');
                            $(this).removeClass('none');
                            $('.select-time').remove();
                            var sOrderYear = $('#cy').text(),
                                sOrderMonth = $('#cm').text(),
                                sOrderDay = $this.find('.day-num').text(),
                                sOrderYMD = util.dateFormat(new Date(sOrderYear+'/'+sOrderMonth+'/'+sOrderDay),'yyyy/MM/dd'),
                                sSelectYMD = util.dateFormat(new Date(sOrderYear+'/'+sOrderMonth+'/'+sOrderDay),'yyyy年MM月dd日'),
                                aBeginHoursListTpl = [],
                                aBeginMinuteListTpl = [],
                                aEndHoursListTpl,aEndMinuteListTpl,
                                //专家开放时间
                                aCurrentOpenTime = [],
                                //已被占用时间(包括自己和别人)
                                aCurrentHasOrderTime = [],
                                //可以预约的时间
                                aCurrentOrderTime = [],
                                //请求已被占用时间段的url
                                sUrl = util.strFormat(inter.getApiUrl().getOrderUsedUrl, [$('#expertId').val(), sOrderYMD.replace(/\//g, '-')]);

                            $.each(sSelectDate, function(i, n){
                                if(n.date == sOrderYMD){
                                    if(n.end.indexOf('59')!=-1){
                                        n.end = n.end.replace('59','60');
                                    }
                                   aCurrentOpenTime.push({'begin':n.begin,'end':n.end});
                                }
                            });

                            util.setAjax(sUrl, {}, function(json){
                               getOrder(json);
                            },function(){
                                $('.existTimeList').html('<li>加载失败，<a href="javascript:" class="reGet">点击重试</a></li>');
                                $('.reGet').on('click', function(e){
                                    getRecordList();
                                    e.preventDefault();
                                });    
                            },'GET');

                            function getOrder(json){
                                if(json.record&&json.record.length){
                                    var records = json.record;
                                    $.each(records,function(i,n){
                                        if(n.date == sOrderYMD.replace(/\//g, '-')){
                                            aCurrentHasOrderTime.push({'begin':n.begin,'end':n.end});
                                        }
                                    }); 
                                }

                                var aTotalOpenTime = self._getTotalTime(aCurrentOpenTime),
                                    //所有已经被占用的时间段
                                    aTotalHasOrderTime = self._getTotalTime(aCurrentHasOrderTime);
                                //可预约的时间段
                                var nowHourMin = self._appendZero(self._getServiceDate(new Date()).getHours()) + ':' + self._appendZero(self._getServiceDate(new Date()).getMinutes());
                                for(var i in aTotalOpenTime){
                                    var startTime = aTotalOpenTime[i].begin,
                                        bFlag = false;
                                    for(var j in aTotalHasOrderTime){
                                        if(aTotalHasOrderTime[j].begin.getTime()==startTime.getTime()){
                                            bFlag = true;
                                            break;
                                        }
                                    } 
                                    var currentHourMin = self._appendZero(aTotalOpenTime[i].begin.getHours())+':'+self._appendZero(aTotalOpenTime[i].begin.getMinutes());
                                    //判断是否过期(以服务器端返回时间为准)
                                    var bGtDay,bEqDay;
                                    if(self._getServiceDate(new Date()).getFullYear()==sOrderYear&&(self._getServiceDate(new Date()).getMonth()+1)==sOrderMonth){
                                        bGtDay = self._getServiceDate(new Date()).getDate()<sOrderDay,
                                        bEqDay = self._getServiceDate(new Date()).getDate()==sOrderDay&&nowHourMin<currentHourMin;
                                    }else{
                                        bGtDay = true;
                                        bEqDay = true;
                                    }    
                                    if((!bFlag)&&(bGtDay||bEqDay)){
                                        aCurrentOrderTime.push(aTotalOpenTime[i]);
                                    }
                                }
                                //可预约的时间段排序
                                aCurrentOrderTime.sort(function(prev,next){
                                    return prev.begin.getTime()-next.begin.getTime();
                                });

                                //如果被预约完了
                                if(!aCurrentOrderTime.length){
                                    $.alert('当天开放时间已被预约完!');
                                    return;
                                }

                                //初始化开始时间段
                                var aBeginHoursList = [],
                                    aBeginMinuteList = [],
                                    firstBeginHour = aCurrentOrderTime[0].begin.getHours();
                                $.each(aCurrentOrderTime,function(i,n){
                                    var hour = n.begin.getHours();
                                    if($.inArray(hour,aBeginHoursList)==-1){
                                        aBeginHoursList.push(hour);
                                        aBeginHoursListTpl.push(util.template(sSelectTimeTplOption,{
                                            time : hour<10?('0'+hour):hour
                                        }));
                                    }
                                    if(firstBeginHour==hour){
                                        var minute = n.begin.getMinutes();
                                        if($.inArray(minute,aBeginMinuteList)==-1){
                                            aBeginMinuteList.push(minute);
                                            aBeginMinuteListTpl.push(util.template(sSelectTimeTplOption,{
                                                time : minute<10?('0'+minute):minute
                                            })); 
                                        }
                                    }
                                });
                                var nEndTime = 0,
                                    aCurrentOrderEndTime = [];

                                //递归计算出结束时间数组
                                function getEndTime(nEndTime,hour,min){
                                    var bFlag;
                                    if(hour!=undefined&&min!=undefined){
                                        bFlag = (aCurrentOrderTime[nEndTime].begin.getHours()==hour&&aCurrentOrderTime[nEndTime].begin.getMinutes()==min)||
                                        aCurrentOrderTime[nEndTime].end.getTime() == aCurrentOrderTime[nEndTime+1].begin.getTime();
                                    }else{
                                        if(aCurrentOrderTime[nEndTime]){
                                            bFlag = aCurrentOrderTime[nEndTime-1].end.getTime() == aCurrentOrderTime[nEndTime].begin.getTime();
                                        }
                                    }
                                    if(bFlag){
                                        aCurrentOrderEndTime.push(aCurrentOrderTime[nEndTime]);
                                        nEndTime++;
                                        getEndTime(nEndTime);
                                    }else{
                                        return false;
                                    }
                                };
                                getEndTime(0,firstBeginHour,aBeginMinuteList[0]);

                                //加载结束时间段
                                var aEndHoursList,aEndMinuteList;
                                function loadEndTime(firstEndHour){
                                    aEndHoursList = [],
                                    aEndMinuteList = [],
                                    aEndHoursListTpl = [],
                                    aEndMinuteListTpl = [];
                                    $.each(aCurrentOrderEndTime,function(i,n){
                                        var hour = n.begin.getHours();
                                        if($.inArray(hour,aEndHoursList)==-1){
                                            aEndHoursList.push(hour);
                                            aEndHoursListTpl.push(util.template(sSelectTimeTplOption,{
                                                time : hour<10?('0'+hour):hour
                                            }));
                                        }
                                        if(firstEndHour==hour){
                                            var minute = n.end.getMinutes();
                                            if($.inArray(minute,aEndMinuteList)==-1){
                                                minute==0?minute=60:minute;
                                                aEndMinuteList.push(minute);
                                                aEndMinuteListTpl.push(util.template(sSelectTimeTplOption,{
                                                    time : minute<10?('0'+minute):minute
                                                })); 
                                            }
                                        }
                                    });
                                };

                                //初始化
                                loadEndTime(aCurrentOrderTime[0].begin.getHours());
                                var selectTime = $(util.template(sSelectTimeTpl, {
                                    selectTime : sSelectYMD,
                                    startHoursList : aBeginHoursListTpl.join(''),
                                    startMinutesList : aBeginMinuteListTpl.join(''),
                                    endHoursList : aEndHoursListTpl.join(''),
                                    endMinutesList : aEndMinuteListTpl.join('')
                                }));
                                $this.before(selectTime);

                                selectTime.css({
                                    'margin-top': -(selectTime.height()+12-135)
                                });

                                var select = prettifyForm.initSelect(/*{
                                        change : function(){
                                            self._resetMinute(sOrderYMD);
                                        }
                                    }*/);
                                var startHourSelect = select.get('#order-time-hour-start'),
                                    startMinuteSelect = select.get('#order-time-minutes-start'),
                                    endHourSelect = select.get('#order-time-hour-end'),
                                    endMinuteSelect = select.get('#order-time-minutes-end');

                                //加载已被占用时间
                                $('.existTimeList').html('<li>加载中...</li>');
                                if(json.status){
                                    var usedRecord = [];
                                    json.record.sort(function(a, b){
                                        return a.begin > b.begin ? 1 : -1;
                                    });
                                    var dNowDate = self._getServiceDate(new Date());
                                    $.each(json.record, function(i, n){
                                        if(((dNowDate.getFullYear()==n.date.split('-')[0]&&(dNowDate.getMonth()+1)==n.date.split('-')[1]&&dNowDate.getDate()==n.date.split('-')[2])&&
                                            n.begin>(self._appendZero(dNowDate.getHours())+':'+self._appendZero(dNowDate.getMinutes())))||
                                            dNowDate.getFullYear()==n.date.split('-')[0]&&(dNowDate.getMonth()+1)==n.date.split('-')[1]&&dNowDate.getDate()<n.date.split('-')[2]||
                                            dNowDate.getFullYear()==n.date.split('-')[0]&&(dNowDate.getMonth()+1)<n.date.split('-')[1]||dNowDate.getFullYear()<n.date.split('-')[0]){
                                            var endHour = n.end.substring(0,2),
                                                endMin  = n.end.substring(3,5);
                                            if(endMin==59){
                                                endHour = (parseInt(endHour)+1);
                                                endHour = self._appendZero(endHour);
                                                n.end = endHour+':00';
                                            }
                                            usedRecord.push('<li>'+ n.begin +' - '+ n.end +'</li>');
                                        }
                                    });
                                    $('.existTimeList').html(usedRecord.length>0 ? usedRecord.join('') : '<li>还没有被预约</li>');
                                    selectTime.css({
                                        'margin-top': -(selectTime.height() + 6-135)
                                    });
                                }else{
                                    $.alert(json.error);
                                }

                                //加载伪select的下拉框
                                function getTpl(list){
                                    var str = '';
                                    $.each(list,function(i,n){
                                        n = n<10?('0'+n):n;
                                        str += '<li data-value="'+n+'">'+n+'</li>'
                                    });
                                    return str;
                                };

                                //开始小时的change事件
                                select.selectChange(startHourSelect, function(){
                                    var aBeginMinuteList = [],
                                        sCurrentHour = $('#order-time-hour-start').val();
                                        sCurrentHour = self._appendZero(sCurrentHour),
                                        sCurrentBeginMintes = $('[data-name="order-time-minutes-start"]').find('.select-head-title').text();
                                    var aBeginMinuteListTpl = [];
                                    $.each(aCurrentOrderTime,function(i,n){
                                        var hour = n.begin.getHours();
                                        if(sCurrentHour==hour){
                                            var minute = n.begin.getMinutes();
                                            if($.inArray(minute,aBeginMinuteList)==-1){
                                                minute = self._appendZero(minute);
                                                aBeginMinuteList.push(minute);
                                                aBeginMinuteListTpl.push('<li data-value="'+minute+'">'+minute+'</li>'); 
                                            }
                                        }
                                    });
                                    //选择后的小时是否包含之前选择的分钟
                                    var firstMin = self._appendZero($.inArray(self._appendZero(sCurrentBeginMintes),aBeginMinuteList)==-1?aBeginMinuteList[0]:sCurrentBeginMintes);
                                    startMinuteSelect.find('.select-head-title').text(firstMin);
                                    startMinuteSelect.find('ul').empty().append(aBeginMinuteListTpl.join(''));
                                    var num;
                                    $.each(aCurrentOrderTime,function(i,n){
                                        if(n.begin.getHours()==sCurrentHour&&n.begin.getMinutes()==firstMin){
                                            num = i;
                                            return;
                                        }
                                    });
                                    
                                    aCurrentOrderEndTime = [];
                                    getEndTime(num,sCurrentHour,firstMin);
                                    loadEndTime(sCurrentHour);
                                    endHourSelect.find('ul').empty().append(getTpl(aEndHoursList));
                                    endMinuteSelect.find('ul').empty().append(getTpl(aEndMinuteList));
                                    if(parseInt(endHourSelect.find('.select-head-title').text())<=aEndHoursList[0]){
                                        endHourSelect.find('.select-head-title').text(self._appendZero(aEndHoursList[0]));
                                        endMinuteSelect.find('.select-head-title').text(self._appendZero(aEndMinuteList[0]));
                                    }
                                    self._resetMinute(sOrderYMD);
                                });

                                //开始分钟的change事件
                                select.selectChange(startMinuteSelect, function(){
                                    var aBeginMinuteList = [],
                                        sCurrentHour = $('#order-time-hour-start').val(),
                                        sCurrentMinute = startMinuteSelect.find('.highlight').eq(0).text();
                                        sCurrentHour<10?('0'+sCurrentHour):sCurrentHour;
                                        sCurrentMinute<10?('0'+sCurrentMinute):sCurrentMinute;
                                    var num;
                                    $.each(aCurrentOrderTime,function(i,n){
                                        if(n.begin.getHours()==sCurrentHour&&n.begin.getMinutes()==sCurrentMinute){
                                            num = i;
                                            return;
                                        }
                                    });
                                    aCurrentOrderEndTime = [];
                                    getEndTime(num,sCurrentHour,sCurrentMinute);
                                    loadEndTime(sCurrentHour);
                                    endHourSelect.find('ul').empty().append(getTpl(aEndHoursList));
                                    endMinuteSelect.find('ul').empty().append(getTpl(aEndMinuteList));
                                    if(endHourSelect.find('.select-head-title').text()+':'+endMinuteSelect.find('.select-head-title').text()<=self._appendZero(sCurrentHour)+':'+self._appendZero(aEndMinuteList[0])){
                                        endHourSelect.find('.select-head-title').text(self._appendZero(aEndHoursList[0]));
                                        endMinuteSelect.find('.select-head-title').text(self._appendZero(aEndMinuteList[0]));
                                    }
                                    self._resetMinute(sOrderYMD);
                                });

                                //结束小时的change事件
                                select.selectChange(endHourSelect, function(){
                                    var aEndMinuteList = [],
                                        sCurrentHour = endHourSelect.find('.highlight').eq(0).text();
                                        sCurrentHour = self._appendZero(sCurrentHour),
                                        sCurrentStartHour = $('[data-name="order-time-hour-start"]').find('.select-head-title').text(),
                                        sCurrentStartMintes = $('[data-name="order-time-minutes-start"]').find('.select-head-title').text(),
                                        sCurrentEndMintes = $('[data-name="order-time-minutes-end"]').find('.select-head-title').text(),
                                        sCurrentStartHour = self._appendZero(sCurrentStartHour),
                                        sCurrentStartMintes = self._appendZero(sCurrentStartMintes);
                                    var aEndMinuteListTpl = [];
                                    $.each(aCurrentOrderTime,function(i,n){
                                        var hour = n.begin.getHours(),
                                            minute = n.begin.getMinutes();
                                        //确保如果结束小时与开始小时相同情况下，结束分钟大于开始分钟
                                        if(sCurrentHour==hour&&(sCurrentHour>sCurrentStartHour||(sCurrentStartHour==sCurrentHour&&minute>sCurrentStartMintes))){
                                            if($.inArray(minute,aEndMinuteList)==-1){
                                                minute = self._appendZero(minute);
                                                aEndMinuteList.push(minute);
                                                aEndMinuteListTpl.push('<li data-value="'+minute+'">'+minute+'</li>'); 
                                            }
                                        }
                                    });
                                    var len = aCurrentOrderTime.length - 1;
                                    if(len>0){
                                        var min = aCurrentOrderTime[len].begin.getMinutes();
                                        min = self._appendZero(parseInt(min) + 5);
                                        aEndMinuteListTpl.push('<li data-value="'+min+'">'+min+'</li>'); 
                                    }
                                    //选择后的小时是否包含之前选择的分钟
                                    var firstMin = self._appendZero($.inArray(self._appendZero(sCurrentEndMintes),aEndMinuteList)==-1?aEndMinuteList[0]:sCurrentEndMintes);
                                    endMinuteSelect.find('.select-head-title').text(firstMin);
                                    endMinuteSelect.find('ul').empty().append(aEndMinuteListTpl.join(''));
                                    self._resetMinute(sOrderYMD);
                                });

                                //结束分钟的change事件
                                select.selectChange(endMinuteSelect, function(){
                                    self._resetMinute(sOrderYMD);
                                });

                                self._resetMinute(sOrderYMD);
                                
                                //绑定预约事件
                                $('#getDate').on('click',function(){
                                    var $selectTime = $(this).parents('.select-time'),
                                        sOrderHourStart = $('a[data-id="order-time-hour-start"]').find('.select-head-title').text(),
                                        sOrderMinutesStart = $('a[data-id="order-time-minutes-start"]').find('.select-head-title').text(),
                                        sOrderHourEnd = $('a[data-id="order-time-hour-end"]').find('.select-head-title').text(),
                                        sOrderMinutesEnd = $('a[data-id="order-time-minutes-end"]').find('.select-head-title').text(),
                                        timeStart = new Date(sOrderYMD.replace(/-/g, '/') + ' ' + sOrderHourStart + ':' + sOrderMinutesStart),
                                        timeEnd = new Date(sOrderYMD.replace(/-/g, '/') + ' ' + sOrderHourEnd + ':' + (sOrderMinutesEnd==60?'59':sOrderMinutesEnd));

                                    if( timeStart >= timeEnd ) {
                                        $.alert('结束时间必须大于开始时间!');
                                    }else{
                                        if(timeStart < self._getServiceDate(new Date())){
                                            $.alert('预约的时间不能小于当前的时间!');
                                        }else{
                                            var conflict = false;
                                            $.each(record, function(i, n){
                                                n.date = n.date.replace(/-/g, '/');
                                                var begin = new Date(n.date + ' ' + n.begin).getTime(),
                                                    end = new Date(n.date + ' ' + (n.end=='24:00'?'23:59':n.end)).getTime(),
                                                    nowBegin = timeStart.getTime(),
                                                    nowEnd = timeEnd.getTime();

                                                if(!(nowEnd <= begin || nowBegin >= end)){
                                                    conflict = true;
                                                    return false;
                                                }
                                            });
                                            if(conflict){
                                                $.alert('您预约的时间和其他人的预约有冲突，请对比“已被占用的时间”或刷新页面重试。');
                                            }else{
                                                var expertId = $('#expertId').val(),
                                                    tBegin = util.dateFormat(timeStart, 'hh:mm'),
                                                    tEnd = util.dateFormat(timeEnd, 'hh:mm'),
                                                    loading = $.tips('loading');
                                                if(tEnd.split(":")[1]==59&&tEnd.split(":")[0]!=23){
                                                    tEnd = self._appendZero(parseInt(tEnd.split(":")[0])+1) + ":00";
                                                }
                                                util.setAjax(inter.getApiUrl().saveOrderDateUrl, {
                                                    'expert': expertId,
                                                    'records': [
                                                        {
                                                            date: sOrderYMD.replace(/\//g, '-'),
                                                            begin: tBegin,
                                                            end: tEnd
                                                        }
                                                    ]
                                                },function(json){
                                                    loading.close();
                                                    if(json.status){
                                                        //更新自己已预约记录
                                                        record.push({date:sOrderYMD,begin:tBegin,end:tEnd,summary:''});
                                                        $.confirm('您的预约提交成功！是否马上进入“我预约的”列表查看？', function(){
                                                            location.href = '/user/usertradelist';
                                                        });
                                                        if($this.find('.reserve-list li').length<3){
                                                            /*if(tEnd.split(":")[1]==59){
                                                                tEnd = self._appendZero(parseInt(tEnd.split(":")[0])+1) + ":00";
                                                            }*/
                                                            $this.find('.reserve-list').append('<li><em class="icon now"></em>'+ tBegin +' - '+ tEnd +'</li>');
                                                        }
                                                        $selectTime.remove();
                                                    }else{
                                                        $.alert(json.error);
                                                    }
                                                },function(){
                                                     $.alert('系统错误！');
                                                     loading.close();
                                                });
                                            }
                                        }
                                    }
                                });
                                $('.close-select-time').on('click',function(){
                                    $(this).parents('.select-time').siblings('.current').find('.btn-order-time').addClass('none');
                                    $(this).parents('.select-time').remove();
                                });
                            }
                            //阻止向上冒泡
                            self._stopBubble(e); 
                        });
                        $this.append($btnOrderTime);
                    }else{
                        $btnOrderTime.removeClass('none');
                    }
                },function(){
                    $btnOrderTime = $this.find('.btn-order-time');
                    if($this.siblings('.select-time').length==0){
                        $btnOrderTime.addClass('none');
                    }
                }).on('click',function(){
                    //点击某一天切换入具体这天的预约
                    self._dayOrder(this);
                });
            });
        },
        /**
         * 进入一天的预约
         */
        _dayOrder : function(obj){
            var $timeAll = $('#timeAll'),
                $dayAll = $('#dayAll'),
                $iconViewMonth = $('.icon-view-month'),
                sNone = 'none',
                sCurrentDate = $(obj).attr('data-date'),
                nCurrentDay = $(obj).find('.day-num').text(),
                //当天开放的预约时段
                aCurrentTime = [],
                //当天当天所有的被占用的时段
                aCurrentHasOrderTime = [],
                //当天自己已经预约的时段
                aHasOrder = [],
                //请求已被占用时间段的url
                sUrl = util.strFormat(
                    inter.getApiUrl().getOrderUsedUrl,
                    [$('#expertId').val(), sCurrentDate.replace(/\//g, '-')]
                ),
                sTimeAll = [
                    '<table class="hour-table">',
                    '#{hoursList}',
                    '</table>'
                ].join(''),
                y1,startHour,startMin,
                sHourTpl = '<tr class="#{isOpen}">#{firstTd}' +
                    '<td width="7%" valign="top">#{hour}</td>' +
                    '<td width="88%" class="day-hour">#{isHasOrder}</td>' +
                    '</tr>',
                aHourList = [],
                self = this;

            $dayAll.addClass(sNone);
            $iconViewMonth.removeClass(sNone)
                .on('click',function(){
                    $timeAll.addClass(sNone);
                    $dayAll.removeClass(sNone);
                    $(this).addClass(sNone);
                    $('.lr,.now').removeClass(sNone);
                });
            $('.lr,.now').addClass(sNone);

            //计算当天开放的预约时段
            for(var j in sSelectDate){
                if(sSelectDate[j].date==sCurrentDate){
                    aCurrentTime.push({'begin':sSelectDate[j].begin,'end':sSelectDate[j].end});
                }
            }

            //计算自己已经预约的时段
            for(var m in record){
                if(record[m].date==sCurrentDate){
                    var hour = record[m].end.split(':')[0];
                    if(hour!=23){
                        var endMin = record[m].end.split(':')[1];
                        if(endMin==59){
                            parseInt(hour)+1<10?'0'+(parseInt(hour)+1):parseInt(hour)+1
                            record[m].end = parseInt(hour)+1+':00';
                        }
                    }
                    aHasOrder.push({
                        'begin':record[m].begin,
                        'end':self._appendZero(record[m].end.split(':')[0])+':'+self._appendZero(record[m].end.split(':')[1])
                    });
                }
            }

            //计算出当天所有被占用的时间
            util.setAjax(sUrl, {}, function(json){
                if(json.record&&json.record.length){
                    var records = json.record;
                    $.each(records,function(i,n){
                        if(n.date == sCurrentDate.replace(/\//g, '-')){
                            aCurrentHasOrderTime.push({'begin':n.begin,'end':n.end});
                        }
                    }); 
                }

                //当天所有开放的时间段(每5分钟段的)
                var aTotalOpenTime = self._getTotalTime(aCurrentTime),

                //当天所有已经被占用的时间段(每5分钟段的)
                    aTotalHasOrderTime = self._getTotalTime(aCurrentHasOrderTime),

                //当天自己已经预约的时间段(每5分钟段的)
                    aTotalSelfHasOrderTime = self._getTotalTime(aHasOrder),

                //当天非自己预约的时间段(每5分钟段的)
                    aTotalOtherHasOrderTime = aTotalHasOrderTime;

                //当天所有开放的时间段除开非自己预约的(每5分钟段的)
                    aTotalCanOrderTime = aTotalOpenTime;

                $.each(aTotalSelfHasOrderTime,function(i,arr1){
                    var num = $.inArray(arr1.end.getTime(),changeDateToLong(aTotalHasOrderTime));
                    aTotalOtherHasOrderTime.splice(num,1);
                });

                $.each(aTotalOtherHasOrderTime,function(i,arr2){
                    var num = $.inArray(arr2.end.getTime(),changeDateToLong(aTotalOpenTime));
                    aTotalCanOrderTime.splice(num,1);
                });
                
                aTotalOpenTime = aTotalCanOrderTime;

                function changeDateToLong(arr){
                    var newArr = [];
                    $.each(arr,function(i,n){
                        newArr.push(n.end.getTime());
                    });
                    return newArr;
                }

                //定义当前的时间与现在的时间
                var dCurrentYMD = new Date(sCurrentDate);

                //生成24个小时段
                for (var i = 0; i < 24; i++){
                    var isOpenTime = '',
                        sCurrentHour = i<10 ? ('0'+i+':00') : (i+':00'),
                        sCurrentHourNext = i<9 ? ('0'+(i+1)+':00') : ((i+1)+':00'),
                        sHasOrder = '<div class="hasOrderArea">#{hasOrderTimeList}</div>',
                        sHasOrderHtml = '',
                        aHasOrderHour = [];

                    //是否为开放预约的时间
                    for(var k in aTotalOpenTime){
                        var end = self._appendZero(aTotalOpenTime[k].end.getHours())
                                +':'+self._appendZero(aTotalOpenTime[k].end.getMinutes()),
                            begin = self._appendZero(aTotalOpenTime[k].begin.getHours())
                                +':'+self._appendZero(aTotalOpenTime[k].begin.getMinutes());
                        if(!(sCurrentHour>=end || sCurrentHourNext<=begin) && (
                            dCurrentYMD>self._getServiceDate(new Date()) || (
                                dCurrentYMD.getFullYear()==self._getServiceDate(new Date()).getFullYear()
                                && dCurrentYMD.getMonth()==self._getServiceDate(new Date()).getMonth()
                                && dCurrentYMD.getDate()==self._getServiceDate(new Date()).getDate()
                                && self._getServiceDate(new Date()).getHours()<=sCurrentHour.split(':')[0])
                        )){
                            isOpenTime = 'tr-open';
                            break;
                        }
                    }

                    //自己已预约时间排序
                    aHasOrder.sort(function(a, b){
                        return a.begin > b.begin ? 1 : -1;
                    });

                    //是否为自己已经预约的时间段
                    for(var n in aHasOrder){

                        if(!(sCurrentHour>=aHasOrder[n].end||sCurrentHourNext<=aHasOrder[n].begin)){
                            if(parseInt(sCurrentHour) === parseInt(aHasOrder[n].begin)){
                                aHasOrderHour.push('<span>'+aHasOrder[n].begin+'-'+(aHasOrder[n].end=='23:59'?'24:00':aHasOrder[n].end)+'</span>');
                            }else{
                                //用一个div遮罩分割线
                                aHasOrderHour.push(maskHourLine+'<span style="color: #48ab23;">'+aHasOrder[n].begin+'-'+(aHasOrder[n].end=='23:59'?'24:00':aHasOrder[n].end)+'</span>');
                            }
                        }
                    }
                    if(aHasOrderHour.length){
                        sHasOrderHtml = util.template(sHasOrder, {
                            hasOrderTimeList : aHasOrderHour.join('')
                        });
                    }

                    aHourList.push(util.template(sHourTpl, {
                        isOpen : isOpenTime,
                        firstTd : i==0?'<td width="5%" rowspan="24" valign="top" class="day-date">'+nCurrentDay+'</td>':'',
                        hour : sCurrentHour,
                        isHasOrder : sHasOrderHtml
                    }));


                }

                $timeAll.empty().append(util.template(sTimeAll,{
                    hoursList : aHourList.join('')
                })).removeClass(sNone);


                $('.hour-table').find('tr:last').find('td').css('borderBottom','none');
                //$("#timeAll").jscroll();
                //生成12个分钟段并绑定事件
                function operateMin(obj,isDrag){
                    var $this = $(obj);
                    if($this.find('.minute-table').length||!$this.parents('.tr-open').length) return;
                    var aMinuteList = [],
                        sThisHour = $this.prev('td').text(),
                        aHasOrderMin = [],
                        $hasOrderArea = $this.find('.hasOrderArea');

                    if($hasOrderArea.length){
                        var $span = $hasOrderArea.find('span');
                        $span.each(function(){
                            var arr = $(this).text().split('-');
                            aHasOrderMin.push({'start':arr[0],'end':arr[1]});
                        });
                    }

                    for (var i = 1; i <= 12; i++){
                        var isOpenMin = '',
                            isOpenArea = '',
                            hasOrderMinTr = 'notHasOrderMinTr',
                            currenMinRange = '',
                            sThisHourMin = sThisHour.split(':')[0] + ':' + (self._appendZero(i*5));
                        if(sThisHourMin=='23:60'){
                            sThisHourMin='24:00';
                        }
                        for(var j=0;j<aTotalOpenTime.length;j++){
                            var end = self._appendZero(aTotalOpenTime[j].end.getHours())+':'+self._appendZero(aTotalOpenTime[j].end.getMinutes()),
                                begin = self._appendZero(aTotalOpenTime[j].begin.getHours())+':'+self._appendZero(aTotalOpenTime[j].begin.getMinutes()),
                                dCurrentDate = new Date(sCurrentDate+' '+begin),
                                //过期的不开放
                                bDue = self._getServiceDate(new Date()).getTime() < dCurrentDate.getTime();
                            if(sThisHourMin<=(end=='00:00'||end=='23:59'||end=='23:60'?'24:00':end)&&sThisHourMin>begin&&bDue){
                                isOpenMin = 'tr-open';
                                isOpenArea = 'open-area';
                                break;
                            }
                        }

                        for(var k in aHasOrderMin){
                            if(aHasOrderMin[k].start<sThisHourMin&&aHasOrderMin[k].end>=sThisHourMin){
                                hasOrderMinTr = 'hasOrderMinTr';
                                currenMinRange = aHasOrderMin[k].start + '-' + aHasOrderMin[k].end;
                                break;
                            }
                        }

                         aMinuteList.push(util.template(sMinuteTpl, {
                            minute : self._appendZero(i*5),
                            isOpen : isOpenMin,
                            isOpenArea : isOpenArea,
                            hasOrderMinTr : hasOrderMinTr,
                            currenMinRange : currenMinRange
                        }));
                    }

                    //如果是拖动状态下不删除已展开分钟
                    if(!isDrag){
                        $this.parents('tr').eq(0).siblings('tr').each(function(){
                            if($(this).hasClass('tr-open-temp')){
                                $(this).removeClass('tr-open-temp');
                            }
                            $(this).find('.minute-table').remove();
                            $(this).find('.hasOrderArea').removeClass(sNone);
                        });
                    }
                    $this.parents('.tr-open').addClass('tr-open-temp');

                    //加载所有分钟
                    $this.append(util.template(sHourAll,{
                        minuteList : aMinuteList.join('')
                    })).find('.hasOrderArea').addClass(sNone);

                    //加载自己预约时间段
                    var $hasOrderMinTr = $this.find('.hasOrderMinTr');


                    var hourSpan = $this.children('.hasOrderArea').children("span"),
                        ishide ='';
                    if(hourSpan ){
                        ishide = hourSpan.attr("style") || '';
                    }
                    $hasOrderMinTr.each(function(){
                        if(!$(this).prev('tr').hasClass('hasOrderMinTr')){
                            //连续的分钟预约时段
                            var $nextAll = $(this).nextUntil('.notHasOrderMinTr'),
                                num = $nextAll.length,
                                nHeight = num*30+num+31,
                                timeRange = $(this).attr("data-date");
                            if(num>0){
                                timeRange = timeRange.split('-')[0] + '-' + $nextAll.eq(num-1).attr('data-date').split('-')[1];
                            }

                            $(this).find('.open-area').append(
                                '<div class="has-order-area-outer">' +
                                    '<div class="hasOrderMinArea" style="height:'+nHeight+'px;"><span style="'+ishide+'">'+timeRange+'</span></div>' +
                                '</div>');
                            if(ishide !=  ''){
                                $(this).find('.open-area').find(".has-order-area-outer").find(".hasOrderMinArea").prepend($(maskHourLine));
                            }

                        }
                    });

                    $('.minute-table').find('tr:last').find('td').css('borderBottom','none');
                    var bFlag = false,endHour,endMin,dragStatus = false;

                    //鼠标按下
                    $('.notHasOrderMinTr .open-area').on('mousedown',function(e){
                        var $this = $(this);
                        startHour = $this.parents('.day-hour').prev('td').text().split(':')[0];
                        startMin = $this.prev('td').text();
                        y1 = e.pageY;
                        bFlag = true;
                        $('.order-area-outer').remove();
                        $(this).html('<div class="order-area-outer"><div class="order-area" style="height:0;"></div></div>');
                        $('#timeAll').on('mousewheel',function(evt){
                            evt = evt || window.event;
                            if(evt.preventDefault) {
                              evt.preventDefault();
                              evt.stopPropagation();
                            } else {
                              evt.cancelBubble=true;
                              evt.returnValue = false;
                            }
                            return false;
                        });
                    });

                    bFlag = isDrag;

                    //向上拖
                    var bTragUp = false;

                    //鼠标按下并移动
                    $('.notHasOrderMinTr .open-area').on('mousemove',function(even){
                        //确保是keydown状态
                        self._clearSelection();
                        even = even||window.event;
                        if(bFlag&&even.which==1){
                            dragStatus = true;
                            /*if(!$('.order-area-outer').length){
                                $(this).append('<div class="order-area-outer"><div class="order-area"></div></div>');
                            }*/
                            var nNum;
                            var y2 = even.pageY,
                                nHeight = y2 - y1;
                            //util.trace("y2:",y2,"y1",y1,"y2-y1:",y2-y1);
                            if(nHeight>0){
                                nHeight = nHeight - nHeight%30 + 30 + nHeight/30;   
                                bTragUp = false;
                                nNum = $('.order-area-outer')
                                    .parent('td.open-area')
                                    .parent('tr.tr-open')
                                    .nextUntil('tr.notHasOrderMinTr:not(.tr-open)')
                                    .length+1;
                            }
                            //向上拖
                            if(nHeight<0){
                                nHeight = -nHeight;
                                //加上边框总高度
                                nHeight = nHeight - nHeight%30 + 30 + nHeight/30;   
                                bTragUp = true;
                                nNum = $('.order-area-outer').parent('td.open-area').parent('tr.tr-open').prevUntil('tr.notHasOrderMinTr:not(.tr-open)').length+1;
                            }
                            
                            //防止超出范围
                            if(nHeight>nNum*31-1){
                                nHeight = nNum*31;
                            }
                            $('#timeAll').find('.select-time').remove();
                            if(dragStatus){
                                if(!bTragUp){
                                    $('.order-area').css({'height':nHeight,'top':0,'bottom':'auto'});
                                //向上拖
                                }else{
                                    $('.order-area').css({'height':nHeight,'top':'auto','bottom':0});
                                }
                            }

                            //鼠标拖动状态下并移上的时候自动展开下个时段
                            /*$('.tr-open-temp').siblings('.tr-open:not(.tr-open-temp)').find('.day-hour').on('mouseover',function(e){
                                if(bFlag&&dragStatus&&e.which==1){
                                    operateMin(this,true);
                                }
                            });*/
                        }
                    });

                    //鼠标松开
                    $(document).on('mouseup',function(){
                        //冒泡
                        if($('#timeAll').find('.select-time').length) return;
                        $('#timeAll').off('mousewheel');
                        if(bFlag&&dragStatus){
                            var totalMin = ($('.order-area').height() - $('.order-area').height()%30)/30*5;
                            if(totalMin==0) return;
                            $('.order-area').find('.area-tit,.select-time').remove();
                            if(!bTragUp){
                                endMin = parseInt(startMin-5) + totalMin;
                                endHour = parseInt(startHour);
                                if(endMin>=60){
                                    endHour = endHour + (endMin - endMin%60)/60;
                                    endMin = endMin%60;
                                }
                            //endHour = $(this).parents('.day-hour').prev('td').text().split(':')[0];
                            //endMin = $(this).prev('td').text();
                            //var totalMin  = Math.abs(parseInt((endHour-startHour)*60 + (endMin - startMin + 5)));
                                startMin = startMin==5?'00':startMin-5;
                            }
                            if(bTragUp){
                                if(startMin==60&&startHour!=23){
                                    startMin = 0;
                                    startHour = parseInt(startHour) + 1
                                }
                                endMin = parseInt(startMin) - totalMin;
                                endHour = parseInt(startHour);
                                if(endMin<0){
                                    nMin1 = parseInt(startHour)*60 + parseInt(startMin);
                                    nMIn2 = nMin1 - totalMin;
                                    endMin = nMIn2%60;
                                    endHour = (nMIn2 - nMIn2%60)/60
                                }
                                var startMinTmp = startMin,
                                startHourTmp = startHour;
                                startMin = endMin;
                                startHour = endHour;
                                endMin = startMinTmp;
                                endHour = startHourTmp;
                            }
                            

                            //弹出预约框
                            $('.order-area').append(
                                util.template(sOrderTimeTpl, {
                                    selectTime : util.dateFormat(sCurrentDate,'yyyy年MM月dd日'),
                                    startHour : self._appendZero(startHour),
                                    startMin : self._appendZero(startMin),
                                    endHour : self._appendZero(endHour),
                                    endMin : self._appendZero(endMin),
                                    totalMin : totalMin,
                                    totalCost : (totalMin*parseInt($('#expenses').val().replace('￥',''))*0.05)||'0'
                                })
                            );
                            $('.select-time').on('mousedown mouseup mousemove',function(e){
                                bFlag = false;
                                dragStatus = false;
                                self._stopBubble(e);
                            });
                            $('.close-select-time').off('mousedown mouseup mousemove click').on('click',function(e){
                                $(this).parents('.select-time').remove();
                                $('.order-area-outer').remove();
                                 bFlag = false;
                                 dragStatus = false;
                                 self._stopBubble(e);
                            });

                            //提交预约时间
                            $('#timeAll').find('#getDate').off('mousedown mouseup mousemove click').on('click',function(){
                                var expertId = $('#expertId').val(),
                                    loading = $.tips('loading');
                                if(endHour==24&&endMin==00){
                                    endHour = 23;
                                    endMin = 59;
                                }
                                util.setAjax(inter.getApiUrl().saveOrderDateUrl, {
                                    'expert': expertId,
                                    'records': [
                                        {
                                            date: sCurrentDate.replace(/\//g, '-'),
                                            begin: startHour+':'+startMin,
                                            end: endHour+':'+(endMin==60?59:endMin)
                                        }
                                    ]
                                },function(json){
                                    loading.close();
                                    if(json.status){
                                        $.confirm('您的预约提交成功！是否马上进入“我预约的”列表查看？', function(){
                                            location.href = '/user/usertradelist';
                                        },function(){
                                            var $minuteTable = $('.minute-table');
                                            if(!$minuteTable.prev('.hasOrderArea').length){
                                                $minuteTable.before('<div class="hasOrderArea"></div>')
                                            }
                                            $minuteTable.prev('.hasOrderArea').append('<span>'+self._appendZero(startHour)+':'+self._appendZero(startMin)+'-'+self._appendZero(endHour)+':'+self._appendZero(endMin)+'</span>');
                                            $minuteTable.prev('.hasOrderArea').removeClass('none');
                                            $minuteTable.parents('.tr-open').eq(0).removeClass('tr-open-temp');
                                            $minuteTable.remove();
                                        });
                                        /*if($this.find('.reserve-list li').length<3){
                                            $this.find('.reserve-list').append('<li><em class="icon now"></em>'+ tBegin +' - '+ tEnd +'</li>');
                                        }*/
                                        $('.select-time').remove();
                                    }else{
                                        $.alert(json.error);
                                    }
                                },function(json){
                                    loading.close();
                                    $.alert('系统错误！');
                                });
                            });
                        }
                        bFlag = false;
                        dragStatus = false;
                    });
                    //$('#timeAll').jscroll();
                };
                
                //绑定拖动事件
                $('.day-hour').on('click',function(){ 
                    operateMin(this);
                });
                //$('body').jscroll();
            },function(){
                $.alert('服务器错误!');
            },'GET');

        },
        /**
         * 返回最新的服务端时间
         */
        _getServiceDate : function(d){
            return new Date(new Date(serviceTime).getTime() + d.getTime() - clientTime.getTime());
        },
        /**
         * 将时间段分为每5分钟的时间段
         */
        _getTotalTime : function(aTime){
            var ymd = '2000/01/01 ',
                aTotalTime = [];
            $.each(aTime,function(i,n){
                //防止分钟为60时报错
                if(n.end.substring(3,4)=='6'||n.end.substring(4,5)=='9'){
                    var hour = parseInt(n.end.substring(0,2));
                    n.end = hour+1+':00';
                    if(hour+1==24){
                        n.end = '00:00';
                    }
                }
                var nSeconds = 5*60*1000,
                    nBegin = new Date(ymd + n.begin).getTime(),
                    nEnd = new Date(ymd + n.end).getTime();
                //当n.end为00:00时
                if(n.end=='00:00'){
                    nEnd = new Date('2000/01/02 ' + n.end).getTime();
                }
                //计算出有多少个5分钟
                var nRate = (nEnd - nBegin)/nSeconds;
                //当n.end为00:00时
                if(nRate==-276){
                    nRate = 12;
                }
                for(var i=0;i<nRate;i++){
                    var tmpDate = new Date(new Date(ymd + n.begin).getTime() + (5+i*5)*60*1000);
                    if(nBegin<=tmpDate&&nEnd>=tmpDate){
                        aTotalTime.push({'begin':new Date(tmpDate.getTime()-nSeconds),'end':tmpDate})
                    }
                }
            });
            return aTotalTime;
        },
        /**
         * 补零
         */
        _appendZero : function(str){
            var str = parseInt(str);
            return str<10?('0'+str):str;
        },
        /**
         * 阻止事件冒泡
         */
        _stopBubble : function(e){
            if (e && e.stopPropagation){
                e.stopPropagation();
            }
            else{
                window.event.cancelBubble=true;
            }
        },
        _getRecordList : function (selectTime,json){
            var self = this;
            $('.existTimeList').html('<li>加载中...</li>');
            if(json!='error'){
                if(json.status){
                    var usedRecord = [];
                    json.record.sort(function(a, b){
                        return a.begin > b.begin ? 1 : -1;
                    });
                    $.each(json.record, function(i, n){
                        usedRecord.push('<li>'+ n.begin +' - '+ n.end +'</li>');
                    });
                    $('.existTimeList').html(usedRecord.length>0 ? usedRecord.join('') : '<li>还没有被预约</li>');
                    selectTime.css({
                        'margin-top': -(selectTime.height() + 6-135)
                    });
                }else{
                    $.alert(json.error);
                }
            }
            else{
                $('.existTimeList').html('<li>加载失败，<a href="javascript:" class="reGet">点击重试</a></li>');
                $('.reGet').on('click', function(e){
                    self._getRecordList();
                    e.preventDefault();
                });
            }
        },
        _resetMinute : function(){
            var self = this,
                startHour = self._appendZero($('a[data-id="order-time-hour-start"]').find('.select-head-title').text()),
                startMin = self._appendZero($('a[data-id="order-time-minutes-start"]').find('.select-head-title').text()),
                endHour = self._appendZero($('a[data-id="order-time-hour-end"]').find('.select-head-title').text()),
                endMin = self._appendZero($('a[data-id="order-time-minutes-end"]').find('.select-head-title').text());
            if(endMin == 60 && endHour != 23){
                endMin = 0;
                endHour = self._appendZero(parseInt(endHour) + 1);
            }
            var startTime = new Date(arguments[0].replace(/-/g, '/') + ' ' + startHour + ':' + startMin),
                endTime = null;

            if(endMin == 60 && endHour == 23){
                endTime = new Date(arguments[0].replace(/-/g, '/') + ' 23:59');
                endTime = new Date(endTime.getTime()+(1000*60));
            }else{
                endTime = new Date(arguments[0].replace(/-/g, '/') + ' ' + endHour + ':' + endMin);
            }
            var result = (endTime.getTime() - startTime.getTime())/60/1000,
                expenses = parseInt($('#expenses').val().replace('￥',''))||0;

            if(result>0){
                $('.totalTime span').text(result);
                $('.totalCost span').text(Math.round(expenses*result*0.05*100)/100);
            }else{
                $('.totalTime span').text(0);
                $('.totalCost span').text(0);
            }
        },
        /**
         * 绑定删除已设定日期事件
         */
        _deleteOrderBind : function (){
            $('.delete-order').on('click',function(){
                var $this = $(this),
                    sDate = $this.prev('span').text(),
                    sYear = sDate.substring(0,4),
                    sMonth = sDate.substring(5,7),
                    sDate = sDate.substring(8,10);
                $this.parents('.order-time').remove();
            });
        },
        _yearAll : function(year){
            var sYear = "";
            for (var y = year - 10; y <= year + 10; y++) {
                sYear += "<li class='select-year'>"+ y +"</li>";
            }
            return sYear;
        },
        _dateAll : function(year,month){
            var self = this;
            var sMonth = "",Mnum = self._getDaysInmonth(year,month);
            for (var m = 1; m <= 12; m++) {
                sMonth += "<li class='select-month'>"+ (m < 10 ? "0" + m : m) +"</li>";
            }
            return sMonth;
        },
        _getym : function(o,s){
            var self = this;
            $('#'+s).html(parseInt(o.innerHTML));
            $('.sbox').hide();
            self._getDynamicTable(parseInt(cy.html()),parseInt(cm.html()),sSelectDate);
        },
        _month : function(s){
            var self = this;
            var y = parseInt(cy.html()),m = parseInt(cm.html());
            if (s == "l") {
                if (m <= 1) {
                    m = 12;
                    y --;
                } else {
                    m --;
                }
            } else {
                if (m >= 12) {
                    m = 1;
                    y ++;
                } else {
                    m ++;
                }
            }
            cy.html(y);
            cm.html(m);
            self._getDynamicTable(y,m,sSelectDate);
        },
        /**
         * 返回今天
         */
        _now : function (){
            cy.html(sYear);
            cm.html(sMonth);
            $(this).blur();
        },
        /**
         * 得到日历
         */
        _getDynamicTable : function(year, month, sSelectDate){
            var self = this;
            var temp, i, j;
            // 当月第一天为星期几,当月的总天数,表格的单元格数及循环数,表格第一排空格数与当月天数之和
            var firstDate, monthDate, cirNum, ertNum; 
            // '得到该月的第一天是星期几  0-6
            firstDate = self._getWeekdaymonthStartsOn(year, month);
            // 得到该月的总天数 30
            monthDate = self._getDaysInmonth(year, month);
            ertNum = firstDate + monthDate;
            temp = [];
            if (ertNum > 35){
                cirNum = 42;
            }else if (ertNum == 28){
                cirNum = 28;
            }else{
                cirNum = 35;
            }
            j=1;
            for (i = 1; i <= cirNum; i++){
                if (i == 1){
                    temp.push(week);
                }
                if (i < firstDate + 1 || i > ertNum){
                    temp.push("<td>&nbsp;</td>");
                }else{
                    /*temp.push("<td>" + (sYear == year && sMonth == month && sDate == j ? "<em class='icon now'></em>":"") +
                            "<div class='day-warp"+_isSelectDate(year,month,j)+"'><span class='day-num'>" +
                            j +"</span></div></td>");*/                                                     //如果过期则不显示预约时间和已经预约时间
                    var cls = self._getServiceDate(new Date()) > new Date(year +'/'+ month +'/'+ j + ' 23:59:59') ? " previous" :self._isSelectDate(year, month, j);
                    temp.push("<td class='"+(sYear == year && sMonth == month && sDate == j ? "today":"")+"'><div class='day-warp"+ cls +"' "
                        + (cls.indexOf('current')>-1 ? "data-date='"+ util.dateFormat(new Date(year+'/'+month+'/'+j),'yyyy/MM/dd') +"'" : "")
                        +"><span class='day-num'>" + j
                        +"</span><ul class='reserve-list'></ul></div></td>");
                    j = j + 1;
                }
                if (i % 7 == 0 && i < ertNum){
                    temp.push("</tr><tr>");
                }
                if (i == cirNum){
                    temp.push("</tr></table>");
                }
            }
            $("#dayAll").html(temp.join(''));
            self._bindEvent();
        },
        /**
         * 初始化日历时判断该日期是否为已设定开放预约时间
         */
        _isSelectDate : function(year, month, j){
            var _className = '';
            if(sSelectDate){
                for(var k in sSelectDate){
                    var _orderDate = new Date(sSelectDate[k].date.replace(/-/g, '/')),
                        _year = _orderDate.getFullYear(),
                        _month = _orderDate.getMonth() + 1,
                        _day = _orderDate.getDate();
                    if(_year==year&&month==_month&&j==_day){
                        _className = ' current';
                        break;
                    }
                }
            }
            return _className;
        },
        /**
         * 得到该月的总天数
         */
        _getDaysInmonth : function (year,month){
            if (month==1||month==3||month==5||month==7||month==8||month==10||month==12)
                return 31;
            else if (month==4||month==6||month==9||month==11)
                return 30;
            else if (month==2)
                if((year%4==0 && year%100!=0)||(year%100==0 && year%400==0))
                    return 29;
                else
                    return 28;
            else
                return 28;
        },
        /**
         * 得到该月的第一天是星期几
         */
        _getWeekdaymonthStartsOn : function(year,month){
            var date = new Date(year,month-1,1);
            return date.getDay();
        },
        /**
         * 禁止被选中
         */
        _clearSelection : function(){
            if(document.selection && document.selection.empty) {
                if(!$.browser.msie){
                    document.selection.empty();
                }
            }
            else if(window.getSelection) {
                var sel = window.getSelection();
                sel.removeAllRanges();
            }
        }
    };

    return {
        /**
         * 加载日历
         */
        init : function(){
            var self = this,
                dataPicker = new DatePicker(),
                arg = arguments[0],
                container = arg.container,
                $container = $(container);

            $container.append(sDateWarp);
            //dataPicker._getDynamicTable(sYear,sMonth,sSelectDate);
            bNotToday = true;
            var getData = function(){
                util.setAjax(
                    util.strFormat( inter.getApiUrl().getOrderDateUrl,[$('#expertId').val()]),
                    {},
                    self.getOrderDate,
                    null,
                    'GET');
            }
            getData();
            cy = $("#cy");
            cm = $("#cm");
            $('.lr-l').on('click',function(){
                dataPicker._month('l');
                bNotToday = false;
                getData();
            });
            $('.lr-r').on('click',function(){
                dataPicker._month('r');
                bNotToday = false;
                getData();
            });
            $('.now').on('click',function(){
                bNotToday = true;
                dataPicker._getDynamicTable(sYear,sMonth,sSelectDate);
                dataPicker._now();
                getData();
            });
            $("#yearAll").html(dataPicker._yearAll(sYear));
            $("#dateAll").html(dataPicker._dateAll(sYear,sMonth));
            $('.select-year').on('click',function(){
                dataPicker._getym(this,'cy');
            });
            $('.select-month').on('click',function(){
                dataPicker._getym(this,'cm');
            });
            dataPicker._now();
            $(document).on('dblclick click',function(e){
                dataPicker._clearSelection();
            });
        },
        
        /**
         * 得到已设定时间日期后的回调函数
         */
        getOrderDate : function (data) {
            var dataPicker = new DatePicker();
            if(data.status){
                record = data.record || [];
                sSelectDate = data.booking;
                if(bNotToday){
                    dataPicker._getDynamicTable(sYear, sMonth, sSelectDate);
                }
                if(sSelectDate){                 //初始化已设定预约时间
                    var sSelectDateTime = [];
                    $('.order-tit').after(sSelectDateTime.join(''));
                    $.each(record, function(i, n){
                        n.date = n.date.replace(/-/g, '/');
                        var len = $('.day-warp[data-date="'+ n.date +'"] .reserve-list li').length;
                        if(len<3){
                            var endHour = n.end.substring(0,2),
                                endMin  = n.end.substring(3,5);
                            if(endMin==59){
                                endHour = (parseInt(endHour)+1);
                                endHour = dataPicker._appendZero(endHour);
                                n.end = endHour+':00';
                            }
                            $('.day-warp[data-date="'+ n.date +'"] .reserve-list')
                                .append('<li><em class="icon now"></em>'+ n.begin +' - '+ n.end +'</li>');
                        }
                    });
                    $.each(sSelectDate, function(i, n){
                        n.date = n.date.replace(/-/g, '/');
                        $('.day-warp[data-date="'+ n.date+'"]').attr('data-bid', n.id);
                    });
                    dataPicker._deleteOrderBind();
                }
            }
        }
    }
});