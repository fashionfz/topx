/**
 * @description: 常用方法、工具
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('common/util', ['module/cookie'], function(cookie){
    var countdownTimer = {};

    return {
        /**
         * ajax封装函数
         * 参数：url 请求连接  data 参数  sucCall 成功回调函数  errCall失败回调函数  sync同异步请求
         */
        setAjax : function(url, args, sucCall, errCall, method, sync){
            var self = this,
                curUid = parseInt(cookie.get('_u_id')) || 0;

            //args = self.objectToStr(args); 不对提交数据做安全处理，后台已经处理
            var ajaxOptions = {
                    type: 'POST',
                    dataType: 'json',
                    data: JSON.stringify(args),
                    cache: false,
                    url: url,
                    async: sync ? false : true,
                    success: function(json){
                        if(json && json.notLogin && json.notLogin === 'true'){
                            location.href = '/login?msg=1&referer=' + encodeURIComponent(location.href);
                        }else if(json && json.notCompInfo && json.notCompInfo === 'true'){
                            location.href = '/user/thirdaccountsetting?referer=' + encodeURIComponent(location.href);
                        }else{
                            sucCall && sucCall(json);
                        }
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown){
                        switch (textStatus){
                            case "timeout":
                                //alert('服务无反应，请稍后再试！');
                                break;
                            case "error":
                                if(XMLHttpRequest.status != 0){
                                    if(errCall){
                                        errCall();
                                    }else{
                                        alert('服务器繁忙，请稍后再试！');
                                    }
                                }
                                break;
                            case "notmodified":
                                //
                                break;
                            case "parsererror":
                                if(errCall){
                                    errCall();
                                }else{
                                    alert('服务器繁忙，请稍后再试！');
                                }
                                break;
                            default:
                                break;
                        }
                    }
                };

            if( curUid ){
                ajaxOptions.headers = {currentUid: curUid};
            }
            if( method && method === 'GET' ){
                ajaxOptions.type = 'GET';
                ajaxOptions.data = args;
            }else{
                ajaxOptions.contentType = 'application/json; charset=utf-8';
            }
            return $.ajax(ajaxOptions);
        },

        objectToStr : function(args){
            var self = this;
            if(typeof args === 'object'){
                $.each(args, function(i, n){
                    args[i] = self.safeHTML(self.objectToStr(n));
                })
            }else{
                args = self.safeHTML(args);
            }
            return args;
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
            if( !s || s.length == 0 ){
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
                //如果option[f1]返回undefined或null，将会被转为空字符串""
                //如果option[f1]返回0, 将会返回0
                //options[f1] = (options[f1] == 0 || options[f1] == "0") ? "0" : options[f1];
                if (options[f1] == 0 || options[f1] == "0") {
                    return options[f1];
                }else{
                    return options[f1] ? options[f1] : (ori?match:"");
                };
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
        //判断是否是数组
        isArray: function(s){
            return s instanceof Array;
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
        /**
         * 图片加载失败处理
         */
        imgLoadError : function(obj){
            var oImg = obj || $('img');
            oImg.each(function(i, n){
                n.onerror = function(){
                    $(n).prop('src', ued_conf.root + 'images/dev-head-default1.png');
                }
            })
        },
        /**
         * 转换为安全的HTML
         */
        safeHTML : function (str) {
            if(str && typeof str === 'string'){
                str = str.replace(/</ig, '&lt;');
                str = str.replace(/>/ig, '&gt;');
            }
            return str;
        },
        /**
         * 转换用户头像  登录后使用
         */
        getAvatar : function(uid, size){
            var curUid = parseInt(cookie.get('_u_id')) || 0,
                curAvatar = $('.J-login-mark .img-full').prop('src'),
                reStr = '';
            if(curAvatar.indexOf('assets') > -1){
                reStr = curAvatar.substring(0, curAvatar.indexOf('assets')) + 'uploadfile/avatar/'+ uid +'/avatar_190.jpg';
            }else{
                var markP = curAvatar.indexOf('?');
                reStr = curAvatar.substring(0, markP > -1 ? markP : curAvatar.length).replace('/'+ curUid +'/', '/'+ uid +'/');
            }
            if(size && ued_conf.mode === 'online'){
                reStr = reStr + '_' + size + '_' + size;
            }
            return reStr;
        },
        /**
         * 清除HTML
         */
        removeHTMLTag : function (str) {
            if(str && typeof str === 'string'){
                str = str.replace(/<\/?[^>]*>/g, ''); //去除HTML tag
                str = str.replace(/[ | ]*\n/g, '\n'); //去除行尾空白
                //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
                str = str.replace(/&nbsp;/ig, ''); //去掉&nbsp;
            }
            return str;
        },
        /**
         * 倒计时
         * 参数：obj 显示倒计时对象   t 倒计时时间 单位（秒）  callback 倒计时为0时回调函数  name 定时器名字
         */
        countdown : function(obj, t, callback, eachCall, name){
            var self = this,
                num = t - 1;
            name = name || 'default';

            if(obj){
                $(obj+':input').val(t);
                $(obj).text(t);
            }

            if(t){
                countdownTimer[name] = setTimeout(function(){
                    self.countdown(obj, num, callback, eachCall,name);
                }, 1000);
                if(eachCall)eachCall(t);
            }else{
                if(callback)callback();
            }
        },
        /**
         * 清除倒计时对象
         */
        clearCountdown : function(name){
            name = name || 'default';
            clearTimeout(countdownTimer[name]);
        },
        /**
         * 字符串计数
         */
        wordsCount : function(input, tips, num){
            $(input).on('keydown keyup blur mousecenter mouseleave mousemove',function(){
                var len = $(this).val().length || 0,
                    chrLen = num - len;
                tips && $(tips).text(chrLen > 0 ? chrLen : 0);
            }).attr('maxLength',num);
        },
        /**
        * 处理过长的字符串，截取并添加省略号
        * 注：半角长度为1，全角长度为2
        *
        * pStr:字符串
        * pLen:截取长度
        *
        * return: 截取后的字符串
        */
        autoAddEllipsis : function (pStr, pLen, pEll) {
            var self = this,
                _ret = self.cutString(pStr, pLen),
                _cutFlag = _ret.cutFlag,
                _cutStringN = _ret.cutString;

            if ("1" == _cutFlag) {
                return _cutStringN + (pEll || "...");
            } else {
                return _cutStringN;
            }
        },

        /**
        * 取得指定长度的字符串
        * 注：半角长度为1，全角长度为2
        *
        * pStr:字符串
        * pLen:截取长度
        *
        * return: 截取后的字符串
        */
        cutString :function (pStr, pLen) {
            var self = this;
            if(!pStr){
                return {
                    "cutString": '',
                    "cutFlag": ''
                };
            }
            // 原字符串长度
            var _strLen = pStr.length,
                _tmpCode,
                _cutString,
                _cutFlag = "1",// 默认情况下，返回的字符串是原字符串的一部分
                _lenCount = 0,
                _ret = false;

            if (_strLen <= pLen/2) {
                _cutString = pStr;
                _ret = true;
            }

            if (!_ret) {
                for (var i = 0; i < _strLen ; i++ ) {
                    if (self.isFull(pStr.charAt(i))) {
                        _lenCount += 2;
                    } else {
                        _lenCount += 1;
                    }

                    if (_lenCount > pLen) {
                        _cutString = pStr.substring(0, i);
                        _ret = true;
                        break;
                    } else if (_lenCount == pLen) {
                        _cutString = pStr.substring(0, i + 1);
                        _ret = true;
                        break;
                    }
                }
            }

            if (!_ret) {
                _cutString = pStr;
                _ret = true;
            }

            if (_cutString.length == _strLen) {
                _cutFlag = "0";
            }

            return {
                "cutString": _cutString,
                "cutFlag": _cutFlag
            };
        },
        /**
        * 判断是否为全角
        *
        * pChar: 长度为1的字符串
        * return: true:全角
        *         false:半角
        */
        isFull : function (pChar) {
            for (var i = 0; i < pChar.length ; i++ ) {
                return pChar.charCodeAt(i) > 128
            }
        },
        /**
         * 关闭标签/浏览器 或 刷新浏览器
         * call: 回调函数
         */
        onBeforeBomUnload : function(call){
            if ($.browser.msie) {
                window.document.body.onbeforeunload = call;
            }else{
                window.onBeforeUnload = call;
                $('body').attr('onbeforeunload', 'return onBeforeUnload();')
            }
        },
        /**
         * 保留2位或多位小数
         * pos 需要保留小数的位数，默认为2位
         */
        toFixed : function (num, pos){
            var floatNum = parseFloat(num),
                floatPos = pos || 2,
                roundNum = Math.pow(10, floatPos),
                returnNum = '0',
                indexNum = 0,
                remainNum = 0,
                i = 0;

            if(floatNum){
                returnNum = (Math.round(floatNum * roundNum)/roundNum) + '';
            }
            indexNum = returnNum.indexOf('.');
            if(indexNum == -1){
                returnNum += '.';
            }
            indexNum = returnNum.indexOf('.');
            remainNum = returnNum.substring(indexNum, returnNum.length).length - 1;
            if(remainNum >= floatPos){
                returnNum = returnNum.substring(0, indexNum + floatPos + 1);
            }else{
                for(; i < floatPos-remainNum; i++){
                    returnNum += '0';
                }
            }
            return returnNum;
        },
        /**
         * 获取IE版本
         */
        IEVersion: function(){
            var v = 3, div = document.createElement('div'), all = div.getElementsByTagName('i');
            while (
                div.innerHTML = '<!--[if gt IE ' + (++v) + ']><i></i><![endif]-->',
                all[0]
            );
            return v > 4 ? v : false ;
        },
        isIE6:function(){
            return this.IEVersion() === 6;
        },
        isIE7:function(){
            return this.IEVersion() === 7;
        },
        isIE8:function(){
            return this.IEVersion() === 8;
        },
        isIE9:function(){
            return this.IEVersion() === 9;
        },
        /**
         * 控制台输出
         */
        trace : function(msg, color){
            if(ued_conf.mode === 'dev'){
                if(window.console){
                    if(color){
                        console.log('%c'+msg, 'color:'+color);
                    }else{
                        console.log(msg);
                    }
                }
            }
        },
        /**
         * 相关转码函数
         */
        arrayBufferToString:function(arrayBuffer){
            var binarry = this.arrayBufferToBase64(arrayBuffer);
            return this.binaryToString(binarry);
        },
        binaryToString:function(binary){
            var error;
            try {
                console.info(decodeURIComponent(escape(binary)));
                return decodeURIComponent(escape(binary));
            } catch (_error) {
                error = _error;
                if (error instanceof URIError) {
                    return binary;
                } else {
                    throw error;
                }
            }
        },
        arrayBufferToBase64:function(buffer){
            var binary = '';
            var bytes = new Uint8Array(buffer);
            var len = bytes.byteLength;
            for (var i = 0; i < len; i++) {
                binary += String.fromCharCode(bytes[i]);
            }
            return binary;
        }
    }
});