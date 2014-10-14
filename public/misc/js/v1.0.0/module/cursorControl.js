/**
 * @description: 光标操作模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/cursorControl', [
    'common/util',
    'module/browser'
], function(util, browser){

    function _( s ) {
        for (var k in s) {
            s[k.toUpperCase()] = s[k];
        }
        return s;
    }

    return {
        /**
         * 初始化模块
         */
        init : function(obj){
            var self = this;
            self.range = !1;
            self.target = obj;
            self.element = obj[0];
            self.body = self.element;
            self.document = document;
            self.getRange = null;
            self.clearDoc();
            self.start = 0;
            self.bindEvent();
            return self;
        },
        /**
         * 绑定事件
         */
        bindEvent: function(){
            var self = this;
            self.target.on('keydown keyup mouseup', function(e){
                this.focus();
                if(!window.getSelection){
                    self.range = document.selection.createRange();
                }else{
                    self.start = self.getStart();
                }
                var val = $.trim($(this).html());
                if(!val){
                    self.clearDoc();
                }
            });
        },
        /**
         * 获取当前元素类型
         */
        getType : function(){
            return Object.prototype.toString.call(this.element).match(/^\[object\s(.*)\]$/)[1];
        },
        /**
         * 获取光标位置
         */
        getStart : function(){
            this.target.focus();
            var selection = window.getSelection ? window.getSelection() : document.selection,
                range = selection.createRange ? selection.createRange() : selection.getRangeAt(0);

            this.getRange = range;
            if (this.element.selectionStart || this.element.selectionStart == '0'){
                return this.element.selectionStart;
            }else if (window.getSelection){
                var rng = window.getSelection().getRangeAt(0).cloneRange();
                rng.setStart(this.element.firstChild, 0);
                return rng.toString().length;
            }
        },
        /**
         * 在光标处插入内容
         */
        insertText : function(text){
            var self = this;
            self.target.focus();
            if(!window.getSelection){
                if(!self.range)self.range = document.selection.createRange();
                document.selection.empty();
                self.range.pasteHTML(text);
                self.range.collapse(false);
                self.range.select();
            }else{
                if(self.getType()=='HTMLDivElement'){
                    if(!self.getRange)self.start = self.getStart();
                    var sel = window.getSelection ? window.getSelection() : document.selection,
                        eleId = self.target.prop('id'),
                        rang = self.getRange;

                    if(self.target.html() == '<p><br></p>'){
                        self.target.html('<p></p>');
                    }

                    if(rang.startContainer.nodeName == self.body.nodeName){
                        rang.setStart(self.body.firstChild, 0);
                    }
                    //self.element.focus();
                    rang.collapse(false);

                    var hasR = rang.createContextualFragment(text),
                        hasR_lastChild = hasR.lastChild;
                    while (hasR_lastChild && hasR_lastChild.nodeName.toLowerCase() == "br" && hasR_lastChild.previousSibling && hasR_lastChild.previousSibling.nodeName.toLowerCase() == "br") {
                        var e = hasR_lastChild;
                        hasR_lastChild = hasR_lastChild.previousSibling;
                        hasR.removeChild(e)
                    }
                    rang.insertNode(hasR);
                    if (hasR_lastChild) {
                        rang.setEndAfter(hasR_lastChild);
                        rang.setStartAfter(hasR_lastChild)
                    }
                    sel.removeAllRanges();
                    sel.addRange(rang);
                }else{
                    self.target.val(self.target.val().substr(0,self.start) + text + self.target.val().substr(self.start));
                }
            }
        },
        /**
         * 清空内容
         */
        clearDoc : function() {
            this.target.html('<p>' + (browser.ie ? '' : '<br>') + '</p>');
            this.start = 0;
        },
        /**
         * 获取选中内容
         */
        getText : function(){
            if (document.all){
                var r = document.selection.createRange();
                document.selection.empty();
                return r.text;
            }else{
                if (this.element.selectionStart || this.element.selectionStart == '0'){
                    var text = this.getType() == 'HTMLDivElement' ? this.element.innerHTML : this.element.value;
                    return text.substring(this.element.selectionStart,this.element.selectionEnd);
                }else if (window.getSelection){
                    return window.getSelection().toString()
                }
            }
        },
        /**
         * 设置光标位置
         */
        setPos : function(n){
            this.target.focus();
            if (this.target[0].selectionStart || this.target[0].selectionStart == '0'){
                this.target[0].setSelectionRange(n || 0, n || (this.getType() == 'HTMLDivElement' ? this.target.html().length : this.target.val().length));
            }else if(this.target[0].createTextRange){
                var textRange = this.target[0].createTextRange();
                textRange.moveStart("character", n || (this.getType() == 'HTMLDivElement' ? this.target.html().length : this.target.val().length));
                textRange.moveEnd("character", 0);
                textRange.select();
            }
        },
        /**
         * 清空光标
         */
        emptyRange : function(){
            this.range = null;
        }
    }
});