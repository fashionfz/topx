/**
 * @description: 粘贴板模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/clipboard', ['module/cursorControl'], function(cursor){

    return {
        /**
         * 获取粘贴板内容
         */
        getData : function (callback) {
            var self = this;
            if (document.getElementById('helome_pastebin')) {
                return;
            }
            var pastebin = $('<textarea id="helome_pastebin"></textarea>');
            // Safari 要求div必须有内容，才能粘贴内容进来
            $.browser.webkit && pastebin.append(document.createTextNode(self._fillChar + self._fillChar));
            $('body').append(pastebin);
            pastebin.css({
                "position": "absolute",
                "width": "1px",
                "height": "1px",
                "overflow": "hidden",
                "left": "-1000px",
                "white-space": "nowrap",
                "top": "0px"}).focus().select(true);

            setTimeout(function () {
                if ($.browser.webkit) {
                    for (var i = 0, pastebins = document.querySelectorAll('#helome_pastebin'), pi; pi = pastebins[i++];) {
                        if (self._isEmptyNode(pi)) {
                            $(pi).remove();
                        } else {
                            pastebin = pi;
                            break;
                        }
                    }
                }
                try {
                    pastebin.parentNode.removeChild(pastebin);
                } catch (e) {
                }
                callback(pastebin);
            }, 0);
        },
        /**
         * 空白占位符
         */
        _fillChar : $.browser.ie && $.browser.version == '6' ? '\ufeff' : '\u200B',
        /**
         * 判断是否为空节点
         * @function
         * @param {Node}    node    节点
         * @return {Boolean}    是否为空节点
         */
        _isEmptyNode: function (node) {
            var self = this;
            return !node.firstChild || self._getChildCount(node, function (node) {
                return  !self._isBr(node) && !self._isWhitespace(node)
            }) == 0
        },
        /**
         * 判断节点是否为br
         * @function
         * @param {Node}    node   节点
         */
        _isBr: function (node) {
            return node.nodeType == 1 && node.tagName == 'BR';
        },
        /**
         * 检测节点node是否为空节点（包括空格、换行、占位符等字符）
         * @name  isWhitespace
         * @grammar  UM.dom.domUtils.isWhitespace(node)  => true|false
         */
        _isWhitespace: function (node) {
            return !new RegExp('[^ \t\n\r' + self._fillChar + ']').test(node.nodeValue);
        },
        /**
         * 返回子节点的数量
         * @function
         * @param {Node}    node    父节点
         * @param  {Function}    fn    过滤子节点的规则，若为空，则得到所有子节点的数量
         * @return {Number}    符合条件子节点的数量
         */
        _getChildCount: function (node, fn) {
            var count = 0, first = node.firstChild;
            fn = fn || function () {
                return 1;
            };
            while (first) {
                if (fn(first)) {
                    count++;
                }
                first = first.nextSibling;
            }
            return count;
        }
    }
});