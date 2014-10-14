/**
 * Created with JetBrains WebStorm.
 * User: TinTao
 * Date: 13-10-22
 * Time: 上午9:48
 * To change this template use File | Settings | File Templates.
 */
(function (g) {

    /***************************
     private
     *************************/
    /**
     *  g.helome window 上面是否注册了helome
     * @type {*}
     * @private
     */
    var _helome = g.helome,
        global = null;
    /**
     * 如果已经存在了helome
     *     直接返回 ，否则进行初始化
     */
    if (_helome && _helome.version) {
        return
    }
    var helome = g.helome = {version: "1.0.0"};
    /**
     * 存放已经加载的模块
     * @type {{}}
     */
    var cachedModules = helome.cache = {};

    var modulesCallbackList = [];
    /**
     * 匿名模块存储
     */
    var anonymousModuleData;

    var fetchingList = {},
        fetchedList = {},
        callbackList = {},
        waitingsList = {};

    /**
     * 文件加载状态
     * @type {number}
     */
    var STATUS_FETCHING = 1,
        STATUS_SAVED = 2,
        STATUS_LOADED = 3,
        STATUS_EXECUTING = 4,
        STATUS_EXECUTED = 5;


    var READY_STATE_RE = /^(?:loaded|complete|undefined)$/;

    /**
     * 配置文件
     * @type {{charset: string, preload: Array, debug: boolean}}
     */
    var configData = {
        /**
         * 文件基础路径
         */
        base: function () {
            var ret = loaderDir;
            //ret = ret.substring(0, ret.lastIndexOf("dist"));
            ret = ued_conf.amdPath || ret;

            return ret;
        },
        charset: "utf-8",
        /**
         * 预先加载文件
         */
        preload: [],
        /**
         * 开发调试模式
         */
        debug: true
    };

    /**
     *
     * @type {*}
     * @private
     */
    var _head = document.getElementsByTagName("head")[0];

    var REQUIRE_RE = /"(?:\\"|[^"])*"|'(?:\\'|[^'])*'|\/\*[\S\s]*?\*\/|\/(?:\\\/|[^/\r\n])+\/(?=[^\/])|\/\/.*|\.\s*require|(?:^|[^$])\brequire\s*\(\s*(["'])(.+?)\1\s*\)/g;
    var SLASH_RE = /\\\\/g;

    /**
     * @method parseDependencies
     * @param {String} code 模块的字符形式
     */
    function parseDependencies(code) {
        var ret = [];
        code.replace(SLASH_RE, "").replace(REQUIRE_RE, function (m, m1, m2) {
            if (m2) {
                if (!filterDependencies(ret, m2))ret.push(m2);
            }
        });
        return ret;
    }

    /**
     * @method filterDependencies
     * @param dependencies
     * @param item
     */
    function filterDependencies(dependencies, item) {
        var _dependencies = dependencies || [];
        if (_dependencies.length == 0)return false;
        for (var i = 0, Ln = _dependencies.length; i < Ln; i++) {
            if (item == _dependencies[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * @method isType 小小扩展了javascript 判断类型的方法
     * @param type
     * @returns {Function}
     */
    function isType(type) {
        return function (obj) {
            return Object.prototype.toString.call(obj) === "[object " + type + "]"
        }
    }

    var isObject = isType("Object");
    var isString = isType("String");
    var isArray = Array.isArray || isType("Array");
    var isFunction = isType("Function");

    function isBase(id) {
        var is = false;
        if(typeof id == 'string') {
            id = id.split('.');
            if(id && id.length > 0 && id[0] == 'base') {
                is = true;
            }
        }

        return is;
    }

    /**
     * @method define 模块封装
     * @param id  模块名
     * @param factory
     */
    function define(id, deps, factory) {
        /**
         * 暂时只支持有名模块
         */
        if (arguments.length === 1) {
            factory = id;
            id = undefined;
        }

        if (!isBase(id) && isFunction(factory)) {
            var factoryString = factory.toString();
            if(!deps) {
                deps = [];
            }
            deps = deps.concat(parseDependencies(factoryString));
        }
        var data = { id: parsePaths(id), uri: parsePaths(id), deps: deps, factory: factory ,moduleName:id};
        if(data.uri) {
            saveModule(data.uri, data);
        }else {
            anonymousModuleData = data;
        }
    }

    /**
     * @method register 保存加载的module
     * @param id
     * @param meta
     */
    function regist(id, func) {
        var ids = typeof id == 'string' ? id.split('.') : [],
            win = helome;

        if(ids && ids.length > 1) {
            if(ids[0] == "base") {
                ids.shift();
            }
        }

        for(var i= 0; i<ids.length - 1; i++){
            if(win[ids[i]] == null) {
                win[ids[i]] = {};
            }

            win = win[ids[i]];
        }

        if (ids.length > 0) {
            win[ids[ids.length - 1]] = func;
        }
    }
    /**
     * @method saveModule 保存module
     * @param uri
     * @param meta
     */
    function saveModule(uri, meta) {
        var mod = getModule(uri);

        if (mod.status <= STATUS_SAVED) {
            mod.id = meta.id || uri;
            mod.dependencies = resolve(meta.deps || [], uri);
            mod.factory = meta.factory;
            if (mod.factory !== undefined) {
                mod.status = STATUS_SAVED
            }
        }
    }

    var DOT_RE = /\/\.\//g
    var URI_END_RE = /\?|\.(?:css|js)$|\/$/

    /**
     * @method parsePaths 路径转换
     * @param id
     * @returns {*}
     */
    function parsePaths(id) {
        if (!isAbsolute(id)) {
            id = getAbsolutePath(id);
        }
        return id;
    }

    function getAbsolutePath(path) {
        var basePath = configData.base();
        if (!path)return;
        if (path.indexOf("/") === -1) {
            path = path.replace(/\./g, "/");
            if (!URI_END_RE.test(path)) {
                path += ".js";
            }
            return basePath + path;
        }
        return path;

    }

    var ABSOLUTE_RE = /:\//;

    /**
     * @method isAbsolute   判断是否是绝对路径
     * @param uri
     */
    function isAbsolute(uri) {
        if(uri && ABSOLUTE_RE.test(uri)) {
            return true;
        }
        return false;
    }

    /**
     * @method Module 模块管理对象
     * @param uri
     * @constructor
     */
    function Module(uri) {
        this.uri = uri;
        this.dependencies = [];
        this.exports = null;
        this.status = 0;
    }

    /**
     * @method destroy 注销module
     *
     */
    Module.prototype.destroy = function () {
        delete cachedModules[this.id];
    };

    /**
     * @method exec 获取module
     * @param mod
     * @returns {*}
     */
    function exec(mod,id) {
        if (!mod) {
            return null;
        }
        if (mod.status >= STATUS_EXECUTING) {
            return mod.exports;
        }
        mod.status = STATUS_EXECUTING;
        function resolveInThisContext(id) {
            return resolve(id, mod.uri);
        }

        /**
         * @method require 获取module
         * @param id
         * @returns {*}
         */
        function require(id) {
            return exec(cachedModules[resolveInThisContext(id)],id)
        }

        require.resolve = resolveInThisContext;
        require.async = function (ids, callback) {
            use(resolveInThisContext(ids), callback);
            return require;
        }
        var factory = mod.factory;
        var exports = isFunction(factory) ? factory(require, mod.exports = {}, mod) : factory;
        mod.exports = exports === undefined ? mod.exports : exports;

        regist(id,mod.exports);
        mod.status = STATUS_EXECUTED;
        return mod.exports;
    }

    /**
     * @method getModule 获取需要加载的module
     * @param id
     * @returns {*|Module}
     */
    function getModule(id) {
        return cachedModules[id] || (cachedModules[id] = new Module(id));
    }

    helome.use = function (ids, callback) {
        preload(function () {
            use(resolve(ids), callback)
        });
        return helome;
    };

    /**
     * 预先加载modules
     * @param callback
     */
    function preload(callback) {
        var preloadMods = configData.preload;
        var len = preloadMods.length;
        if (len) {
            use(resolve(preloadMods), function () {
                preloadMods.splice(0, len);
                preload(callback)
            })
        } else {
            callback();
        }
    }

    function resolve(ids, refUri) {
        if (isArray(ids)) {
            var ret = [];
            for (var i = 0; i < ids.length; i++) {
                ret[i] = resolve(ids[i], refUri);
            }
            return ret;
        }
        var data = { id: ids, refUri: refUri};

        return data.uri || parsePaths(data.id)
    }

    function use(uris, callback) {
        isArray(uris) || (uris = [uris]);
        load(uris, function () {
            var exports = [];

            for (var i = 0; i < uris.length; i++) {
                exports[i] = exec(cachedModules[uris[i]])
            }

            if (callback) {
                callback.apply(global, exports)
            }
        })
    }

    var circularStack = [];

    function isCircularWaiting(mod) {
        var waitings = waitingsList[mod.uri] || [];
        if (waitings.length === 0) {
            return false;
        }
        circularStack.push(mod.uri);
//        if (isOverlap(waitings, circularStack)) {
//            cutWaitings(waitings);
//            return true;
//        }
        for (var i = 0; i < waitings.length; i++) {
            if (isCircularWaiting(cachedModules[waitings[i]])) {
                return true;
            }
        }
        circularStack.pop();
        return false;
    }

    function load(uris, callback) {
        /**
         * 检测需要导入的js文件是否在未导入的队列中
         * @type {*}
         */
        var unloadedUris = getUnloadedUris(uris);
        /**
         * 如果已经加入直接运行callback
         */
        if (unloadedUris.length === 0) {
            callback();
            return
        }
        var len = unloadedUris.length;
        var remain = len;

        for (var i = 0; i < len; i++) {
            (function (uri) {
                var mod = cachedModules[uri];
                if (mod.dependencies.length) {

                } else {
                    mod.status < STATUS_SAVED ? fetch(uri, loadWaitings) : done();
                }

                function loadWaitings(cb) {
                    cb || (cb = done);
                    var waitings = getUnloadedUris(mod.dependencies);
                    if (waitings.length === 0) {
                        cb()
                    } else if (isCircularWaiting(mod)) {
                        printCircularLog(circularStack);
                        circularStack.length = 0;
                        cb(true);
                    } else {
                        waitingsList[uri] = waitings;
                        load(waitings, cb);
                    }
                }

                function done(circular) {
                    if (!circular && mod.status < STATUS_LOADED) {
                        mod.status = STATUS_LOADED
                    }

                    if (--remain === 0) {
                        callback()
                    }
                }

            })(unloadedUris[i])
        }
    }

    function fetch(uri, callback) {
        cachedModules[uri].status = STATUS_FETCHING;
        var data = { uri: uri };
        var requestUri = data.requestUri || uri;
        data.requestUri = requestUri;

        // fix 文件重复加载报错
        // lizihan 2013-06-17 17:16
        // info 第一次请求未完成,就添加后续请求,导致报错
        if (callbackList[requestUri]) {
            callbackList[requestUri].push(callback);
        }
        else {
            callbackList[requestUri] = [callback];
        }
        if (!data.requested) {
            request(data.requestUri, onRequested, configData.charset)
        }

        function onRequested() {
            /**
             * 加载匿名模块
             */
            if (anonymousModuleData) {
                saveModule(uri, anonymousModuleData);
                anonymousModuleData = undefined;
            }
            // Call callbacks
            var fn, fns = callbackList[requestUri];
            if (!fns || fns.length < 1)
                return;
            try {
                while ((fn = fns.shift()))
                    fn();

                delete callbackList[requestUri];
            } catch (e) {
                window.console && console.log(requestUri + " " + e);
            }
        }
    }

    /**
     * @method request 动态加载js文件
     * @param url
     * @param callback
     * @param charset
     */
    function request(url, callback, charset) {
        var node = doc.createElement("script");
        node.charset = configData.charset;
        addOnload(node, callback);
        node.async = true;
        node.src = url;
        currentlyAddingScript = node;
        _head.appendChild(node);
        currentlyAddingScript = undefined;
    }

    /**
     * @method addOnload 注册加载js文件回调函数
     * @param node
     * @param callback
     * @param isCSS
     */
    function addOnload(node, callback, isCSS) {
        node.onload = node.onerror = node.onreadystatechange = function () {
            if (READY_STATE_RE.test(node.readyState)) {
                node.onload = node.onerror = node.onreadystatechange = null;
                /**
                 * 根据调试模式，是否删除script节点
                 */
                if (!isCSS && !configData.debug) {
                    _head.removeChild(node);
                }
                node = undefined;
                callback();
            }
        }
    }

    /**
     * @method getUnloadedUris 获取未导入的文件
     * @param uris
     * @returns {Array}
     */
    function getUnloadedUris(uris) {
        var ret = [];
        for (var i = 0; i < uris.length; i++) {
            var uri = uris[i];
            /**
             * uri 转换为module
             *
             */
            if (uri && getModule(uri).status < STATUS_LOADED) {
                ret.push(uri);
            }
        }

        return ret;
    }

    var DIRNAME_RE = /[^?#]*\//;
    var doc = document,
        loc = location,
        scripts = doc.getElementsByTagName("script");
    var loaderScript = doc.getElementById("helome");
    if(loaderScript){
        var dataMain = loaderScript.getAttribute("data-main");
        var loaderDir = dirname(getScriptAbsoluteSrc(loaderScript));

        function dirname(path) {
            return path.match(DIRNAME_RE)[0]
        }

        function getScriptAbsoluteSrc(node) {
            return node.hasAttribute ? node.src : node.getAttribute("src", 4);
        }
    }

    /**
     * 加载程序的运行入口
     */
    if (dataMain) {
        onDOMContentLoaded(function(){
            helome.use("base.global");
            helome.use(dataMain);
        });
    }
    /* 注册浏览器的DOMContentLoaded事件
     * @param { Function } onready [必填]在DOMContentLoaded事件触发时需要执行的函数
     * @param { Object } config [可选]配置项
     */
    function onDOMContentLoaded(onready, config) {
        //浏览器检测相关对象，在此为节省代码未实现，实际使用时需要实现。
        var _ua = navigator.userAgent.toLowerCase();

        //判断浏览器类型
        var Browser = {
            version: (_ua.match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/) || [])[1],
            ie: /msie/.test(_ua),
            moz: /gecko/.test(_ua),
            safari: /safari/.test(_ua),
            ff: /firefox/i.test(_ua),
            chrome: /chrome/i.test(_ua)
        };
        //设置是否在FF下使用DOMContentLoaded
        this.conf = {enableMozDOMReady: true};
        if (config)
            for (var p in config)
                this.conf[p] = config[p];

        var isReady = false;

        function doReady() {
            if (isReady) return;
            //确保onready只执行一次
            isReady = true;
            onready();
        }

        /*IE*/
        if (Browser.ie) {
            (function () {
                if (isReady) return;
                try {
                    document.documentElement.doScroll("left");
                } catch (error) {
                    setTimeout(arguments.callee, 0);
                    return;
                }
                doReady();
            })();
            window.attachEvent('onload', doReady);
        } else if (Browser.webkit && Browser.version < 525) { /*Webkit*/
            (function () {
                if (isReady) return;
                if (/loaded|complete/.test(document.readyState))
                    doReady();
                else
                    setTimeout(arguments.callee, 0);
            })();
            window.addEventListener('load', doReady, false);
        } else {
            /*FF Opera 高版webkit 其他*/
            if (!Browser.ff || Browser.version != 2 || this.conf.enableMozDOMReady)
                document.addEventListener("DOMContentLoaded", function () {
                    document.removeEventListener("DOMContentLoaded", arguments.callee, false);
                    doReady();
                }, false);
            window.addEventListener('load', doReady, false);
        }
    }

    /*****************************************************
     public    function
     ****************************************************/
    /**
     * 把define 挂在window上面
     */
    g.define = define;
    g.use = use;


}(this));

