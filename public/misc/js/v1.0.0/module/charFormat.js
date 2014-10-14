/**
 * @description: 字符与字节的转换
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 * @date: 2013/12/29
 */

define('module/charFormat', [], function(){

	//返回中文字符的长度
	var getChineseLen = function(str){
		//匹配非中文的正则表达式 
        var reg = /[^\u4E00-\u9FA5\uf900-\ufa2d]/g; 
        var temp = str.replace(reg,''); 
        return temp.length;
	}
	//返回全角字符的个数
    var getFullLen = function(str){
        //匹配全角字符的正则表达式
        var reg = /[^\uFF00-\uFFFF]/g,
            temp = str.replace(reg,'');

        return temp.length;
    }

	//获取一段字符串中ANSI的长度，一个中文字符占2个字节
	var getByteANSILen = function(str){
		var chineseLen = getChineseLen(str),
            fullLen = getFullLen(str),
            ascLen = str.length - chineseLen - fullLen;
		return (chineseLen * 2) + (fullLen * 2) + ascLen;
	}

	var getCharANSILen = function(str){
		return parseInt(getByteANSILen(str)/2 + 0.5);
	}

	//获取一段字符串中UTF8的长度，一个中文字符占2-4个字节
	var getByteUTF8Len = function(str){
		return unescape(encodeURIComponent(str)).length;
	}

	return {
		getChineseLen: getChineseLen,
		getByteANSILen: getByteANSILen,
		getCharANSILen: getCharANSILen,
		getByteUTF8Len: getByteUTF8Len
	}

});