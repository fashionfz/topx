/**
 * @description: 登录注册模块
 * @author: TinTao(tintao.li@maesinfo.com)
 * @update:
 */
define(function(require, exports, module){
    var dev = false; // 设置接口模式 true为开发模式 false为真实接口
    module.exports = {

        /**
         * ajax封装函数
         * 参数：url 请求连接  data 参数  sucCall 成功回调函数  errCall失败回调函数
         */
        setAjax : function(url, args, sucCall, errCall, method){
            if( method && method === 'GET' ){
                $.ajax({
                    type: 'GET',
                    dataType: 'json',
                    data: args,
                    url: url,
                    success: function(json){
                        sucCall(json);
                    },
                    error: function(){
                        if(errCall){
                            errCall();
                        }else{
                            alert('服务器繁忙，请稍后再试！');
                        }
                    }
                });
            }else{
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    data: JSON.stringify(args),
                    contentType: 'application/json; charset=utf-8',
                    url: url,
                    success: function(json){
                        sucCall(json);
                    },
                    error: function(){
                        if(errCall){
                            errCall();
                        }else{
                            alert('服务器繁忙，请稍后再试！');
                        }
                    }
                });
            }
        },
        /**
         * 判断URL格式合法性
         * 参数：str_url 需要验证的字符串
         * 返回：布尔值
         */
        isURL : function (str_url){
            var strRegex = /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i;
            var re = new RegExp(strRegex);
            return re.test(str_url);
        },
        /**
         * 字符串格式化
         * 参数：s 模板  arr 数据数组
         * 返回：格式化后的字符串
         */
        strFormat : function(s, arr){
            if( s.length == 0 ){
                s = '';
            }else{
                for(var i=0; i<s.length; i++){
                    s = s.replace(new RegExp("\\{"+i+"\\}","g"), arr[i]);
                }
            }
            return s;
        },
        /**
         * HTML模板处理
         * 参数：src 模板  options 数据JSON  ori 规则
         * 返回：HTML代码
         */
        template : function(src, options, ori) {
            var curStr;
            if($.browser.msie){
                curStr = src;
            }else{
                curStr = [];
                var len = src.length;
                var i;
                for(i=0; i<len; i++){
                    curStr.push(src[i]);
                }
                curStr = curStr.join("");
            }

            var formatReg = new RegExp("#{([a-z0-9_]+)}", "ig");
            curStr = curStr.replace(formatReg, function(match, f1, index, srcStr){
                return options[f1]?options[f1]:(ori?match:"");
            });
            return curStr;
        },
        /**
         * Tab选项卡
         * 参数：obj 为tab按钮（必需）；
                 curCls 处于激活状态的按钮样式（必需）；
                 nomCls 处于普通状态的按钮样式；（非必须）；
                 callBack：回调（非必须）；
         */
        tab : function(obj, curCls, nomCls, callBack){
            var that = this;
            $(obj).on('click',function(e){
                var self = $(this),
                    sib = self.siblings(),
                    index = self.index();

                sib.removeClass(curCls);
                self.addClass(curCls);
                if (nomCls) {
                    if(that.isNullString(nomCls)){
                        sib.addClass(nomCls);
                        self.removeClass(nomCls);
                    };
                };

                var table = self.closest(".tab").find(".tab-table");
                var currentTable = table.eq(index);
                currentTable.removeClass("hide");
                currentTable.siblings(".tab-table").addClass("hide");

                if(callBack)callBack(index);
                e.preventDefault();
            })
        },
        /**
         * 设置/获取地址栏参数
         */
        location : function(){
            var args = {},
                data = null,
                urlData = location.search,
                arrData;
            if(arguments.length == 1){
                data = arguments[0];
                if(typeof data === "string"){
                    var arr = this.location();
                    args = arr[data] || '';
                }else if(typeof data === "object"){
                    if(urlData.indexOf('?')>-1){
                        var oldLocation = this.location();
                        var newLocation = $.extend(oldLocation, data);
                        args = $.param(newLocation);
                    }else{
                        args = '?' + $.param(data);
                    }
                }
            }else if(arguments.length == 2){
                args[arguments[0]] = arguments[1];
                args = this.location(args);
            }else{
                urlData = urlData.replace('?','');
                if(urlData.indexOf('&')>-1){
                    arrData = urlData.split('&');
                    $.each(arrData,function(i,n){
                        if(n.indexOf('=')>-1){
                            n = n.split('=');
                            args[n[0]] = decodeURIComponent(n[1]+'');
                        }else{
                            args[n] = '';
                        }
                    })
                }else{
                    if(urlData.indexOf('=')>-1){
                        arrData = urlData.split('=');
                        args[arrData[0]] = arrData[1];
                    }else{
                        args[urlData] = '';
                    }
                }
            }
            return args;
        },
        /**
         * 格式化时间戳
         */
        dateFormat : function(date,format){ 
            var dateTime = new Date(date)
            var o = { 
                "M+" : dateTime.getMonth()+1, //month 
                "d+" : dateTime.getDate(), //day 
                "h+" : dateTime.getHours(), //hour 
                "m+" : dateTime.getMinutes(), //minute 
                "s+" : dateTime.getSeconds(), //second 
                "q+" : Math.floor((dateTime.getMonth()+3)/3), //quarter 
                "S" : dateTime.getMilliseconds() //millisecond 
            };
            if(/(y+)/.test(format)) {
                format = format.replace(RegExp.$1, (dateTime.getFullYear()+"").substr(4 - RegExp.$1.length)); 
            }
            for(var k in o){ 
                if(new RegExp("("+ k +")").test(format)){ 
                    format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
                } 
            } 
            return format; 
            /*var testDate = new Date( 1320336000000 );//这里必须是整数，毫秒 
            var testStr = testDate.format("yyyy年MM月dd日hh小时mm分ss秒"); 
            var testStr2 = testDate.format("yyyyMMdd hh:mm:ss"); 
            alert(testStr + " " + testStr2);*/ 
        },
        /**
         * 根据name获取表单对象
         * 参数：name 表单的name属性
         * 返回：jquery object 对象
         */
        getByName : function(name){
            var objInput = $('.main input[name="'+ name +'"]'),
                objSelect = $('.main select[name="'+ name +'"]'),
                objTextArea = $('.main textarea[name="'+ name +'"]');
            if(objInput.length>0 && (objInput.attr('type') === 'radio' || objInput.attr('type') === 'checkbox')){
                objInput = $('.main input[name="'+ name +'"]:checked');
            }
            return objInput.length>0 ? objInput : objSelect.length>0 ? objSelect : objTextArea.length>0 ? objTextArea : null;

        },

        //判断是否是空字符串 => ''
        //
        isNullString: function(s){
            return s.replace(/(^\s*)|(\s*$)/g, "").length == 0;
        },
        /**
         * 获取email,使用星号隐藏部分内容,例如:ice****@qq.com
         * @return 例如:ice****@qq.com
         */
        getMaskEmail : function(email) {
            if (this.isNullString(email)) {
                return email;
            }else{
                var split = email.split("@"),
                    prefixLength = split[0].length,
                    starCount = prefixLength < 4 ? prefixLength : 4,
                    prefix = '';

                prefix += split[0].substring(0, prefixLength - starCount);

                for (var i = 0; i < starCount; i++) {
                    prefix += "*";
                }

                return prefix + "@" + split[1];
            }
        },
        longPoll : function (call){
            var self = this,
                times = 1,
                url = self.getApiUrl().getMessageCountUrl;

            $.ajax({
                url: url,
                dataType: "json",
                timeout: 10000,
                data: {times: times, r: new Date().getTime()},
                complete: function(){
                    times = 0;
                    self.longPoll(call);
                },
                success: function(json){
                    if(call)call(json);
                }
            });
        }

    }
});