/**
 * @description: 用户中心--》群组 --》填写详细
 * @author: zhiqiang.zhou@helome.com
 * @update:
 */
require([
    'common/interface',
    'common/util',
    'module/pageCommon',
    'module/prettifyForm',
    'module/textBoxList',
    'module/swfUploadGroups',
    'module/userDetail'
],function(inter, util, common, prettifyForm, textBox, swfUpload, DetailAPI){
    common.initLogin();

    var btnOk = $('.btn-group-info-save'),
        btnClean = $('.btn-group-info-clean'),
        $name = $('#groupName'),
        $headUrl = $('#groupHeadUrl'),
        $industry = $('#industry'),
        $classes = $('#classes'),
        $instruction = $('#instruction'),
        $groupBack = $('#groupBackUrl'),
        $groupBackRelation = $('#groupBackRelationUrl'),
        groupId = $('#groupId').val(),
        groupBgTpl = [
            '<div class="image-item" data-originName="#{originName}" data-imageUrl="#{imageUrl}">',
                '<a class="image-delete" title="删除"><em class="icon icon-upload-close"></em></a>',
                '<table><tr><td><img class="db" src="#{showUrl}"></td></tr></table>',
            '</div>'
        ].join(''),
        select = prettifyForm.initSelect({
            width: 150
        }),
        radio = prettifyForm.initRadio({ }),
        checkbox = prettifyForm.initCheckbox({ }),
        tagModule = textBox.initTextBox({
            target:'#tags-input',
            dropDownData: inter.getApiUrl().getTagsUrl,
            width:'auto',
            maxHeight:'189'
        }),
        groupBack = $('#groupBackUp').fileUploader({
            limit: 1,
            maxSize: 2*1024*1024,
            onSizeErr: function(){
                $.alert('请上传小于2MB的图片!');
            },
            limitErr: function(){
                $.alert('请先删除已上传背景!');
            },
            autoUpload: true,
            buttonUpload: '#px-submit',
            buttonClass: 'btn-default btn-lg btn-white btn-user-info-save',
            selectFileLabel: '上传图片',
            allowedExtension: 'jpg|jpeg|png',
            onUpErr : function(err){
                $.alert(err);
            },
            afterEachUpload: function(data, status, form){
                if(status == 1){
                    var originName = groupBack.originName,
                        imgPath = data.headbackgroud_1920,
                        $img = $('#px-form-1 .image-item');
                    if($img.length){
                        $img.attr({'data-originName': originName, 'data-imageUrl': imgPath});
                        $img.find('img').prop('src', imgPath);
                    }else{
                        $img = $(util.template(groupBgTpl, {
                            originName: originName,
                            imageUrl: imgPath,
                            showUrl: imgPath
                        }));
                        form.after($img);
                        $img.find('.image-delete').on('click',function(){
                            $img.remove();
                            groupBack.removeFile();
                            $groupBack.val('');
                            $groupBackRelation.val('');
                        });
                    }
                    $groupBackRelation.val(data.headbackgroud_1920_source);
                    form.remove();
                }
            } //每个文件上传完成后回调

        }),
        //用来获得表单数据，或者设置表单数据
        inputVal = function(){
            tagModule.addItem();
            return {
                name : $name.val(),
                headUrl : $headUrl.val(),
                industry : $industry.val(),
                classes : $classes.val(),
                instruction : $instruction.val(),
                groupBack : $groupBackRelation.val(),
                authority : $('input[name="authority"]:checked').val(),
                skills  : tagModule.getItemsVal(),
                agreement : $('input.agreement').prop('checked')
            }
        };

    DetailAPI.initVerify();
    $name.on('blur',function(){
        DetailAPI.verifyInput([{
            target: $name,
            maxLen: 16,
            outTips: "群名称最长16个中文字符或32个英文、数字",
            nullTips: "群名称不能为空"
        }]);
    })


    //判断个人说明的字符串长度
    var initIntroduction = function(){

        $instruction.on("blur keyup", function(){
            var textVal = $instruction.val(),
                textLen = textVal.length,
                maxLen = 500,
                errBox = $instruction.siblings(".tips"),
                hasIntroTips = errBox.hasClass("introduction-tips"),
                less = maxLen - textLen,
                more = textLen - maxLen;

            if (textLen < maxLen && textLen !== 0) {
                if (!hasIntroTips) {
                    errBox.addClass("introduction-tips");
                }
                errBox.html('还可以输入<span class="num">' + less + '</span>字符');
            }else if (textLen > maxLen) {
                if (hasIntroTips) {
                    errBox.removeClass("introduction-tips");
                }
                errBox.html('群说明最多' + maxLen + '字符，现已经超过<span class="num">' + more + '</span>字符');
            }else{
                errBox.html('');
            }
        });
    };
    //根据行业分类设置背景
    var selectGroupBg = function(selTxt){
        var defaultBg = 'art',
            $img = $('#px-form-1 .image-item');

        if(selTxt.indexOf('it')>-1){//IT/互联网
            defaultBg = 'it';
        }else if(selTxt.indexOf('金融')>-1){//会计/金融
            defaultBg = 'finance';
        }else if(selTxt.indexOf('教育')>-1){//教育/培训
            defaultBg = 'education';
        }else if(selTxt.indexOf('健康')>-1){//健康/医疗
            defaultBg = 'medical';
        }else if(selTxt.indexOf('艺术')>-1){//艺术/媒体
            defaultBg = 'art';
        }else if(selTxt.indexOf('时尚')>-1){//时尚/美容
            defaultBg = 'fashion';
        }else if(selTxt.indexOf('餐饮')>-1){//餐饮
            defaultBg = 'cook';
        }else if(selTxt.indexOf('建筑')>-1){//建筑/房地产
            defaultBg = 'estate';
        }else if(selTxt.indexOf('心理')>-1){//心理咨询
            defaultBg = 'art';
        }
        defaultBg = ued_conf.root + 'images/groupBg/' + defaultBg + '.jpg';
        if($img.length){
            if($img.find('img').prop('src').indexOf(ued_conf.root) > -1){
                $img.attr({'data-originName': defaultBg, 'data-imageUrl': defaultBg});
                $img.find('img').prop('src', defaultBg);
            }
        }else{
            $img = $(util.template(groupBgTpl, {
                originName: defaultBg,
                imageUrl: defaultBg,
                showUrl: defaultBg
            }));
            $('#px-form-1').html($img);
            $img.find('.image-delete').on('click',function(){
                $img.remove();
                $groupBack.val('');
                $groupBackRelation.val();
            });
        }
    };
    initIntroduction();
    select.selectChange(select.get('#industry'), function(sel){
        var selTxt = sel.selectedText.toLowerCase();
        selectGroupBg(selTxt);
    });

    if(groupId ){
        var backUrl = $groupBack.val();
        if(backUrl){
            var $img = $(util.template(groupBgTpl, {
                originName: backUrl,
                imageUrl: backUrl,
                showUrl: backUrl
            }));
            $('#px-form-1').html($img);
            $img.find('.image-delete').on('click',function(){
                $img.remove();
                groupBack.removeFile();
                $groupBack.val('');
                $groupBackRelation.val('');
            })
        }else{
            selectGroupBg($industry.find('option:selected').text());
        }
    }
    btnClean.on('click',function(){
       location.href = '/user/group/groupList'
    });
    btnOk.on('click',function(){
        var data = inputVal();
        DetailAPI.verifyInput([{
            target: $name,
            maxLen: 16,
            outTips: "群名称最长16个中文字符或32个英文、数字",
            nullTips: "群名称不能为空"
        }]);
        if ($.trim($(".tips").not(".introduction-tips").text()).length > 0) {
            $.alert('您所填写的内容不正确。');
            return;
        };
        if(!data.name){
            $.alert('请填写群名称!');
            return;
        }
        if(!data.industry){
            $.alert('请选择所属分类!');
            return;
        }
        if(!data.skills.length){
            $.alert('请添加群标签!');
            return;
        }
        if($('input.agreement').length == 1){
            if(!data.agreement){
                $.alert('请先阅读《helome群服务协议》!');
                return;
            }
        }
        var objParameter = {
            groupName: data.name, // 名称
            headUrl: data.headUrl, // 头像
            industry: data.industry, //所属分类
            groupInfo: data.instruction, // 群说明
            backgroudUrl: data.groupBack, // 背景图片url
            groupPriv: data.authority, // 加入权限，0或1
            tags: data.skills //群标签
        };
        if(groupId){
            objParameter.id = groupId;
        }
        util.setAjax(
            inter.getApiUrl().addGroup,
            objParameter,
            function(json){
                if(json.status == 1){
                    if(groupId){
                        window.location.href = "groupList";
                    }else{
                        window.location.href = "addMember?gid=" + json.groupId + "&isNew=1";
                    }
                }else{
                    $.alert(json.error);
                }
            },
            function(json){
                var str = '系统繁忙，请稍后重试!';
                if(json && json.error){
                    str = json.error;
                }
                $.alert(str);
            }
        )
    });
    /**
     * 点击上传头像
     */
    $('#J-upload-gHead').on('click', function(){
        var uploadTpl = [
            '<div class="dialog-panel">',
                '<div class="dialog-tit clearfix">',
                    '<span class="span-left">上传群头像</span>',
                '</div>',
                '<table class="dialog-table">',
                    '<tr>',
                        '<td class="d-alert-content"><div id="groupHead"></div></td>',
                    '</tr>',
                '</table>',
            '</div>'
        ].join('');

        window.uploadGroupHead = $.dialog({
            width: 650,
            height: 500,
            content: uploadTpl,
            lock: true,
            initialize: function(){
                swfUpload.init(inter.getApiUrl().uploadAvatar, 'groupHead');
            }
        })
    })

});
