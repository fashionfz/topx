define('module/canvas', [], function(){
	return {
		create: function(id, width, height, cla){
            var el = document.createElement('canvas');
            el.setAttribute('id', id);
            el.setAttribute('width', width);
            el.setAttribute('height', height);
            el.setAttribute('class', cla);
            // 在这里做IE的判断，IE6、IE7、IE8（jQuery判断写法）
            if ($.browser.msie && /^[678]./.test($.browser.version)) {
                G_vmlCanvasManager.initElement(el);
            }
            return el;
    	},

    	drawLine: function(obj, dashedObj){
            var c = document.getElementById(obj.id);
            var cxt = c.getContext("2d"),
                //开始和结束的坐标值
                id = obj.id,
                //增长的间隔时间
                dur = obj.duration,
                //每次增长的像素
                pg = obj.pixelGrowth || 1,
                //画线的颜色
                lineColor = obj.lineColor || "#000",
                //线的宽度
                lineWidth = obj.lineWidth || 1;

            //虚线空白长度
            var dl = dashedObj.len || 2,
                skipColor = dashedObj.skipColor || "transparent";

            var isOldIE = !$.support.leadingWhitespace;

            if(skipColor === "transparent"){
                if(isOldIE){
                    skipColor = "#fff";
                }
            }

            var drawSolidLine = function(context, x1, y1, x2, y2){
                //context.beginPath();
                context.moveTo(x1,y1);
                context.lineTo(x2,y2);
                context.stroke();
                //context.closePath();
            }

            var drawDashLine = function (context, x1, y1, x2, y2, dashLength) {
                dashLength = (dashLength === undefined ? 5 : dashLength);
                var deltaX = x2 - x1,
                    deltaY = y2 - y1,
                    i = 0,
                    numbDashes = Math.floor(Math.sqrt(deltaX * deltaX + deltaY * deltaY) / dashLength);

                for (i; i < numbDashes; ++i) {
                    //context.beginPath();
                    context[i % 2 === 0 ? 'moveTo' : "lineTo"](x1 + (deltaX / numbDashes) * i, y1 + (deltaY / numbDashes) * i);
                }
                context.stroke();
            }

            //判断是否画线完成
            var isStop = function(minX, minY, absDeltaX, absDeltaY, m, loop){
                //最新所画的长度
                var currentXLen = Math.abs(minX * m);
                var currentYLen = Math.abs(minY * m);
                if ( currentXLen > absDeltaX && currentYLen > absDeltaY ) { 
                    clearInterval(loop);
                    //util.trace("has stop");
                };
            }

            var draw = function(o, cbIE, cb){
                //初始化数据
                var sx = o.startX,
                    sy = o.startY,
                    ex = o.endX,
                    ey = o.endY;

                cxt.strokeStyle = lineColor;
                cxt.lineWidth = lineWidth;
                if (isOldIE) {
                    //若浏览器是IE
                    if (!!cbIE) { cbIE() };
                } else{
                    var m = 0;
                    var start = setInterval(function(){
                        //X 和 Y轴方向长度
                        var deltaX = ex - sx;
                        var deltaY = ey - sy;

                        //长度绝对值
                        var absDeltaX = Math.abs(deltaX);
                        var absDeltaY = Math.abs(deltaY);

                        //三角形第三条边的长度
                        var deltaZ = Math.floor(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));

                        //第三条边被分为N段
                        var numbDashes =  deltaZ / dl;

                        //X和Y边被分为N段的长度
                        var minX = deltaX / numbDashes;
                        var minY = deltaY / numbDashes;

                        isStop(minX, minY, absDeltaX, absDeltaY, m, start);

                        //绘制点的位置
                        var posX = sx + (deltaX / numbDashes) * m;
                        var posY = sy + (deltaY / numbDashes) * m;
                        //开始绘制
                        cxt.beginPath();

                        if (cb) { cb(posX, posY, minX, minY, m) };
                        
                        cxt.stroke();
                        cxt.closePath();

                        m++;

                    }, dur);
                }
            }

            return {

                playSolidLine: function(obj){
                    var sx = obj.startX,
                        sy = obj.startY,
                        ex = obj.endX,
                        ey = obj.endY;

                    draw(obj, function(){

                        drawSolidLine(cxt, sx, sy, ex, ey);

                    }, function(posX, posY, minX, minY){

                        cxt.moveTo(posX, posY);
                        cxt.lineTo(posX + minX, posY + minY);  //使用lineTo的时候，前面必须有moveTo

                    });
                },

                playDashLine: function(obj){
                    //初始化数据
                    var sx = obj.startX,
                        sy = obj.startY,
                        ex = obj.endX,
                        ey = obj.endY;

                    draw(obj, function(){

                        drawDashLine(cxt, sx, sy, ex, ey, 3);

                    }, function(posX, posY, minX, minY, m){
                        if (m % 2 === 0) {
                            cxt.moveTo(posX, posY);
                        }else{
                            cxt.moveTo(posX - minX , posY - minY);
                            cxt.lineTo(posX, posY);  //使用lineTo的时候，前面必须有moveTo
                        };
                    });
                }
            }
    	}

    	
	}
});