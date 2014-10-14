/**
 * @description: 时间日历选择模块
 * @author: Zhang Guanglin
 * @update: 2013-11-13
 * @update: by zhiqiang for record 移除了相关事件
 */
define('module/datePickerB', [
    'common/interface',
    'common/util',
    'module/prettifyForm'
], function(inter, util, prettifyForm){
    if(window.bookings === undefined){
        window.bookings = [];
    };
    var    sDateWarp = [
            '<div id="dateWarp">',
                '<div class="day">',
                    '<div class="day-select">',
                        '<i class="lr lr-l" title="上一月"></i>',
                        '<div class="select-date">',
                            '<div class="stop" id="cy"></div>',
                            '<div class="sbox">',
                                '<ul id="yearAll">',
                                    '<li>2013</li>',
                                '</ul>',
                            '</div>',
                        '</div>',
                        '<div class="select-date" id="sm">',
                            '<div class="stop" id="cm"></div>',
                            '<div class="sbox" id="mm">',
                                '<ul id="dateAll">',
                                    '<li>01</li>',
                                '</ul>',
                            '</div>',
                        '</div>',
                        '<i class="lr lr-r" title="下一月"></i>',
                        '<span class="now btn-three">今天</span>',
                    '</div>',
                    '<div id="dayAll"></div>',
                '</div>',
            '</div>'
        ].join(''),
        offset  = '<em id="dpOffset" style="position: absolute;height: 0px;width: 0px ;line-height: 0px;padding: 0;margin: 0;"></em>',
        sSelectTimeTpl = [
            '<div class="select-time">',
                '<div class="select-title">#{selectTime}</div>',
                '<p class="pr z5">',
                    '<label>开始时间</label>',
                    '<span class="select-box">',
                    '<select class="order-time-hour-start" name="order-time-hour-start" id="order-time-hour-start">',
                    '#{hoursList}',
                    '</select>',
                    '</span>',
                '</p>',
                '<p class="pr z4">',
                    '<label>结束时间</label>',
                    '<span class="select-box">',
                    '<select class="order-time-hour-end" name="order-time-hour-end" id="order-time-hour-end">',
                    '#{endHoursList}',
                    '</select>',
                    '</span>',
                '</p>',
                '#{active}',
                '<div class="bottom-btn">',
                    '<input type="button" class="btn-default btn-xs btn-blue" id="getDate" value="确定"/>',
                    '<input type="button" class="btn-default btn-xs btn-white close-select-time" value="关闭"/>',
                '</div><em class="time-bg"></em>',
            '</div>'
        ].join(''),
        sSelectTimeTplOption = '<option value="#{time}">#{text}</option>',
        week = '<table><tr><th>日</th><th>一</th><th>二</th><th>三</th><th>四</th><th>五</th><th>六</th></tr><tr>',
        nowDate = new Date(),
        sYear = nowDate.getFullYear(),
        sMonth = nowDate.getMonth()+1,
        sDate = nowDate.getDate(),
        cy = null,
        sSelectDate = null,
        cm = null,
        bEdit = '';

    /**
     * 定义DatePicker构造函数
     */
    function DatePicker(){

    }

    DatePicker.prototype = {
        /**
         * 绑定日历上点击事件
         */
        _bindEvent : function(){
            var self = this;
            $('.day-warp:not(.previous)').each(function(){
                var $this = $(this);
                $this.on('click',function(){
                    $('.select-time').remove();
                    var sOrderYear = $('#cy').text(),
                        sOrderMonth = $('#cm').text(),
                        datePickerWarp = $('#datePickerWarp'),
                        sOrderDay = $this.find('.day-num').text(),
                        sOrderDate = new Date(sOrderYear+'/'+sOrderMonth+'/'+sOrderDay),
                        sOrderYMD = util.dateFormat(sOrderDate, 'yyyy-MM-dd'),
                        sSelectYMD = util.dateFormat(sOrderDate, 'yyyy年MM月dd日');
                    if(bEdit){
                        //if($this.hasClass('current')) return;
                        var sHoursList = [],
                            eHoursList = [],
                            nowHours = nowDate.getHours(),
                            nowMinutes = nowDate.getMinutes(),
                            sHoursStart = 0,
                            sMinutesStart = 0;

                        if(util.dateFormat(nowDate, 'yyyy-MM-dd') == sOrderYMD){
                            if(nowMinutes < 30){
                                sMinutesStart = 1;
                            }else{
                                sMinutesStart = 2;
                            }
                            sHoursStart = nowHours;
                        }
                        for (var i = sHoursStart; i <= 24; i++){
                            for (var j = (nowHours === i ? sMinutesStart : 0); j < (i===24 ? 1 : 2); j++){
                                var t = ( i < 10 ? ('0' + i) : (i) ) + ( j === 1 ? ':30' : ':00');
                                sHoursList.push(util.template(sSelectTimeTplOption, {
                                    time : i === 24 ? '23:59' : t,
                                    text : t
                                }));
                            }
                        }

                        for (var i = sHoursStart; i <= 24; i++){
                            for (var j = (nowHours === i ? sMinutesStart : 0); j < (i===24 ? 1 : 2); j++){
                                var t = ( i < 10 ? ('0' + i) : (i) ) + ( j === 1 ? ':30' : ':00'),
                                    m = t;
                                if(i === 24){
                                    m = '23:59';
                                }
                                eHoursList.push(util.template(sSelectTimeTplOption, {
                                    time : m,
                                    text : t
                                }));
                            }
                        }

                        var selectBox = $(util.template(sSelectTimeTpl, {
                            selectTime : sSelectYMD,
                            hoursList : sHoursList.join(''),
                            endHoursList : eHoursList.join('')
                        }));
                        datePickerWarp.append(selectBox);
                        selectBox.css({
                            top: $this.offset().top - datePickerWarp.offset().top - selectBox.height()/2,
                            left: $this.offset().left - datePickerWarp.offset().left + $this.width() + 6
                        });
                        var select = prettifyForm.initSelect(),
                            startSelectTime = select.get('#order-time-hour-start'),
                            endSelectTime = select.get('#order-time-hour-end');
                        // 选择开始时间联动结束时间
                        select.selectChange(startSelectTime, function(sel){
                            var selValue = sel.selected.attr('data-value'),
                                endValue = $('#order-time-hour-end').val(),
                                endSelection = endSelectTime.find('li[data-value="'+ selValue +'"]');

                            if(endValue <= selValue){
                                endSelectTime.find('li').show();
                                if(endSelection.next().length){
                                    endSelection = endSelection.next();
                                }
                                var endSelectValue = endSelection.attr('data-value'),
                                    endSelectText = endSelection.text();

                                $('#order-time-hour-end').val(endSelectValue);
                                endSelection.prevAll('li').hide();
                                endSelection.addClass('highlight').siblings().removeClass('highlight');
                                endSelectTime.find('.select-head-title').attr('data-value', endSelectValue).text(endSelectText);
                            }else{
                                endSelection.nextAll('li').show();
                            }
                        });

                        self._bindBookings({
                            sOrderYear: sOrderYear,
                            sOrderMonth: sOrderMonth,
                            sOrderDay: sOrderDay,
                            sOrderYMD: sOrderYMD,
                            timeSelect: $this
                        });
                    }else{
                        if($this.hasClass('current')){
                            var expertId = $('#expertId').val();
                            location.href = '/expert/reserve/'+ expertId +'#' + sOrderYMD;
                        }
                    }
                    $('.close-select-time').on('click',function(){
                        $(this).parents('.select-time').remove();
                    });
                });
            });

            $('.select-date').each(function(){
                var $this = $(this),
                    $stop = $this.find('.stop'),
                    $sbox = $this.find('.sbox');
                $this.hover(
                    function(){
                        $stop.addClass('hover');
                        $sbox.show();
                    },
                    function(){
                        $stop.removeClass('hover');
                        $sbox.hide();
                    }
                );
            });
        },

        /**
         * 年份列表
         */
        _yearAll : function(year){
            var sYear = "";
            for (var y = year - 10; y <= year + 10; y++) {
                sYear += "<li class='select-year'>"+ y +"</li>";
            }
            return sYear;
        },
        /**
         * 月份列表
         */
        _dateAll : function(year,month){
            var self = this;
            var sMonth = "",Mnum = self._getDaysInmonth(year,month);
            for (var m = 1; m <= 12; m++) {
                sMonth += "<li class='select-month'>"+ (m < 10 ? "0" + m : m) +"</li>";
            }
            return sMonth;
        },
        /**
         * 同步年月到日历
         */
        _getym : function(o,s){
            var self = this;
            $('#'+s).html(parseInt(o.innerHTML));
            $('.sbox').hide();
            self._getDynamicTable(parseInt(cy.html()),parseInt(cm.html()),sSelectDate);
        },
        /**
         * 同步月到日历
         */
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
         * 同步当前日期到日历
         */
        _now : function (){
            cy.html(sYear);
            cm.html(sMonth);
            $(this).blur();
        },
        /**
         * 初始化/同步日历
         */
        _getDynamicTable : function(year, month, sSelectDate){
            var self = this;
            var temp,i,j;
            var firstDate,monthDate,cirNum,ertNum; // 当月第一天为星期几,当月的总天数,表格的单元格数及循环数,表格第一排空格数与当月天数之和
            firstDate = self._getWeekdaymonthStartsOn(year, month);// '得到该月的第一天是星期几  0-6
            monthDate = self._getDaysInmonth(year, month);// 得到该月的总天数 30
            ertNum = firstDate + monthDate;// -1
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
                    temp.push("<td></td>");
                }else{
                    var cls = nowDate > new Date(year +'/'+ month +'/'+ j + ' 23:59:59') ? " previous" : self._isSelectDate(year, month, j);
                    /*temp.push("<td>" + (sYear == year && sMonth == month && sDate == j ? "<em class='icon now'></em>":"") +
                            "<div class='day-warp"+_isSelectDate(year,month,j)+"'><span class='day-num'>" +
                            j +"</span></div></td>");*/                                                     //如果过期则不显示预约时间和已经预约时间
                    temp.push("<td class='"+(sYear == year && sMonth == month && sDate == j ? "today":"")+"'><div class='day-warp" + cls + "' "+ (cls.indexOf('current')>-1 ? "data-date='"+ util.dateFormat(new Date(year+'/'+month+'/'+j),'yyyy-MM-dd') +"'" : "") +"><em class='icon now' title='已有预约'></em><span class='day-num'>" + j +"</span></div></td>");
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
         * 日历中指定日期显示预约样式
         */
        _isSelectDate : function(year,month,j){
            var _className = '';
            if(sSelectDate){
                for(var k in sSelectDate){
                    var _orderDate = new Date(sSelectDate[k].date.replace(/-/g, '/')),
                        _year = _orderDate.getFullYear(),
                        _month = _orderDate.getMonth() + 1,
                        _day = _orderDate.getDate();
                    if(_year==year && month==_month && j==_day){
                        _className = ' current';
                        if(sSelectDate[k].used===1){         //判断该日期是否为已预约时间
                            _className += ' hasOrder';
                            break;
                        }
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
         * 用户预约执行方法
         */
        _order : function(){

        },
        /**
         * 专家保存开放预约时间执行方法
         */
        _saveOrderTime : function(){

        }
    };

    return {
        /**
         * 加载日历
         */
        loadCalendar : function(obj,opt){
            var self = this,
                datePicker = new DatePicker();
            opt.x = opt.x || 0;
            opt.y = opt.y || 0;
            if(obj){

                obj.on('click',function(){
                    var $sDateWarp = $(sDateWarp),
                        $offset = $(offset);
                    obj.after($offset);
                    $('body').append($sDateWarp);
                    $sDateWarp.offset({
                        left: $offset.offset().left+opt.x,
                        top: $offset.offset().top+opt.y
                    })
                    bEdit = false;
                })

            }else{
                return false;
            }

            cy = $("#cy");
            cm = $("#cm");
            $('.lr-l').on('click',function(){
                datePicker._month('l');
            });
            $('.lr-r').on('click',function(){
                datePicker._month('r');
            });
            $('.now').on('click',function(){
                datePicker._getDynamicTable(sYear,sMonth,sSelectDate);
                datePicker._now();
            });
            $("#yearAll").html(datePicker._yearAll(sYear));
            $("#dateAll").html(datePicker._dateAll(sYear,sMonth));
            $('.select-year').on('click',function(){
                datePicker._getym(this,'cy');
            });
            $('.select-month').on('click',function(){
                datePicker._getym(this,'cm');
            });
            datePicker._now();
        }
    }
});