/*
 * @description: 线段绘制模块
 * @author: TinTao(tintao.li@maesinfo.com)
 * @update:
 */
define(function(){

    return {
        init : function(opts){
            // 在坐标（10,50）创建宽320，高200的画布
            var paper = Raphael(10, 50, 320, 200);

            // 在坐标（x = 50, y = 40）绘制半径为 10 的圆
            var circle = paper.circle(50, 40, 10);

            // 给绘制的圆圈填充红色 (#f00)
            circle.attr("fill", "#f00");

            // 设置画笔（stroke）的颜色为白色
            circle.attr("stroke", "#fff");
        }
    }
});
