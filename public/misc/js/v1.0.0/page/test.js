var newObj = generateItemObj({
    year: $eduYear,
    month: $eduMonth,
    degree: $eduDegree,
    school: $schoolInput,
    prof: $profInput
});

//检查是否有错误
if(DetailAPI.checkEditErr($eduCollapse)) { return };

var newHtml = $(util.template(eduExpTpl, newObj));

if(newObj.professional == '' ){
    newHtml.find('.no-degree').remove();
}

var eduInfo = $eduInfoBox.find(".user-exp:not(.user-delete)");

//添加教育信息
if(eduInfo.length < 1){
    $eduInfoBox.find(".user-exp-box").html(newHtml);
}else{
    var rightIndex = DetailAPI.getIndex(startTimeArr,newObj.eduYear+','+newObj.eduMonth);
    startTimeArr.push(newObj.eduYear+','+newObj.eduMonth);
    if(rightIndex == 0 ){
        $eduInfoBox.find(".user-exp-box").prepend(newHtml);
    }else{
        eduInfo.eq(rightIndex-1).after(newHtml);
    }
}

saveUserInfo();

//为新数据绑定删除和编辑功能
eduInfo = $eduInfoBox.find(".user-exp:not(.user-delete)");
var btnInfoDelete = eduInfo.find(".btn-delete"),
	eduInfoEdit = eduInfo.find(".btn-edit");

btnInfoDelete.off("click");
btnInfoDelete.on('click', function(){
    delEduItem($(this));
});

eduInfoEdit.off("click");
eduInfoEdit.on("click", function(){
    var itemsPanel = $(this).closest(".user-exp").siblings(".user-exp");

    itemsPanel.each(function(i, n){
        if (!$(n).find(".collapse-pannel").is(":hidden")) {
            collapse.hidePannel($(n));
        };
    });
	editItem($(this));
});

resetItem($schoolInput, $profInput, $eduCollapse);

showBanner(newHtml.find("span"));