/**
 * @description: 用户中心--》需求 --》填写详细
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
    'module/userDetail',
    'module/showPicture',
    'module/checkForm'
],function(inter, util, common, prettifyForm, textBox, swfUpload, DetailAPI, showPict, check){
    common.initLogin();

    var btnOk = $('.btn-group-info-save'),
        btnClean = $('.btn-group-info-clean'),
        $industry = $('#industry'),
        $name = $('#name'),
        $instruction = $('#instruction'),
        $price = $('#price'),
        id = util.location('rid'),

        imgTpl = [
            '<div class="image-item" data-id="#{id}">',
            '<a class="image-delete" title="删除"><em class="icon icon-upload-close"></em></a>',
            '<table>',
            '<tr>',
                '<td>#{source}</td>',
            '</tr>',
            '</table>',
            '</div>'
        ].join(''),
        select = prettifyForm.initSelect({
            width: 210
        }),
        radio = prettifyForm.initRadio({ }),
        checkbox = prettifyForm.initCheckbox({ }),
        tagModule = textBox.initTextBox({
            target:'#tags-input',
            dropDownData: inter.getApiUrl().getTagsUrl,
            width:'auto',
            maxHeight:'189'
        }),
        uploader = $('#requireImg').fileUploader({
            limit: 5-requireImg.length,
            maxSize: 2 * 1024 * 1024,
            onSizeErr: function () {
                $.alert('请上传小于2MB的图片!');
            },
            limitErr: function () {
                $.alert('最多上传5张图片!');
            },
            autoUpload: true,
            buttonUpload: '#px-submit',
            buttonClass: 'btn-default btn-lg btn-white btn-user-info-save',
            selectFileLabel: '上传附件',
            allowedExtension: 'jpg|jpeg|png|doc|docx|xlsx',
            onUpErr: function (err) {
                $.alert(err);
            },

            afterEachUpload: function (data, status, form) {
                if (status == 1) {
                    // path 用于保存到后台数据表中
                    // pathsource 页面展示使用

                    var path = data.path;
                    var source = '' ;
                    var fileName = data.fileName;
                    if(/.+\.(jpg|jpeg|png)/.test(path)){
                        source = '<img class="vm dib" src="'+data.pathsource+'" alt="">';
                    }else{
                        source = '<a title="'+ fileName +'" href="http://'+ data.pathsource +'" target="_blank">' +
                            '<span class="file-icon"></span><span title="'+ fileName +'">'+ fileName +'</span></a>'
                    }

                    var $img = $(util.template(imgTpl,{id:data.attachId, source: source}));
                    $img.find('img').on('click', function () {
                        showPict.init(imgPath);
                    });
                    $img.find('.image-delete').on('click', function () {
                        $img.remove();
                        uploader.removeFile();
                    });
                    if (status == 1)
                        form.after($img);
                    form.remove();
                }
            } //每个文件上传完成后回调

        }),
    //用来获得表单数据，或者设置表单数据
        inputVal = function(){
            var attachs =[];
            $('.image-item').each(function(){
                attachs.push({
                    attachId:$(this).attr('data-id')
                })
            })
            return {

                title: $name.val(),//标题
                info: $instruction.val(),// 说明
                price: $price.val(),//价格
                industry: $industry.val(),//行业id
                tags: tagModule.getItemsVal(),//标签
                attachs: attachs //里面是attachId，类似于反馈上传那个地方
//                agreement : $('input.agreement').prop('checked')
            }
        };

    DetailAPI.initVerify();
    $name.on('blur',function(){
        DetailAPI.verifyInput([{
            target: $name,
            maxLen: 28,
            outTips: "最长28个中文字符或56个英文、数字",
            nullTips: "标题不能为空"
        }]);
    });

    $price.on('blur',function(){
        var that = $(this),
            $tips = that.closest('.info-value').find('.tips'),
            $placeHolder = that.closest('.info-value').find('.color-blue-light'),
            value = that.val(),
            typeErrTips = '请填写正确的金额!(最多可包含8位整数和1位小数)',
            nullTips = '价格不能为空';
        if(value != ''){
            if(/^0(\.\d)?$|^[1-9]\d{0,7}(\.\d)?$/.test(value)){
                $tips.html('')
                check.changeClass(true, that, $tips, '');
                $placeHolder.show();

            }else{
                check.changeClass(false, that, $tips, typeErrTips);
                $placeHolder.hide()
            }

        }/*else{
            check.changeClass(false, that, $tips, nullTips);
            $placeHolder.hide();
        }*/
    });

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
                errBox.html('还可以输入<span class="num">' + less + '</span>个字符');
            }else if (textLen > maxLen) {
                if (hasIntroTips) {
                    errBox.removeClass("introduction-tips");
                }
                errBox.html('需求说明最多' + maxLen + '个字符，现已经超过<span class="num">' + more + '</span>个字符');
            }else{
                errBox.html('');
            }
        });
    }
    initIntroduction();

    if(requireImg.length ){
        $.each(requireImg,function(i,n){
            var source ='';
            var path = n.path;
            var fileName = n.fileName;
            if(/.+\.(jpg|jpeg|png)/.test(path)){
                source = '<img class="vm dib" src="'+n.pathsource+'" alt="">';
            }else{
                source = '<a title="'+ fileName +'" href="http://'+ n.pathsource +'" target="_blank">' +
                    '<span class="file-icon"></span><span title="'+ fileName +'">'+ fileName +'</span></a>'
            }
            var $img = $(util.template(imgTpl, {id:n.attachId, source: source}));
            $img.find('.image-delete').on('click',function(){
                $img.remove();
                uploader.removeFile();
            })
            $('#px-form-1').append($img);
        });
    }
    btnClean.on('click',function(){
//        $('textarea').val('');
//        $('input[type!=button]').val('');
//        tagModule.clean();
        window.location.href='/user/require';
    });
    btnOk.on('click',function(){
        tagModule.addItem();
        var data = inputVal();
        if(!data.industry){
            $.alert('请选择所属分类!');
            return;
        }

        if(!data.title){
            $.alert('请填写标题!');
            return;
        }

        /*if(!data.price){
            $.alert('请填写预算!');
            return;
        }*/
        if(!data.tags.length){
            $.alert('请添加需求标签!');
            return;
        }
        DetailAPI.verifyInput([{
            target: $name,
            maxLen: 28,
            outTips: "最长28个中文字符或56个英文、数字",
            nullTips: "标题不能为空"
        }]);
        if ($.trim($(".tips").not(".introduction-tips").text()).length > 0) {
            $.alert('您所填写的内容不正确。');
            return;
        }
        if($('#px-form-1').find('.uploading').length != 0){
            $.alert('还有未上传完毕的文件，请稍后！');
            return;
        }
        /*if($('input.agreement').length == 1){
            if(!data.agreement){
                $.alert('请先阅读《helome服务需求协议》!');
                return;
            }
        }*/
        var objParameter = {

            title: data.title,//标题
            info:  data.info,//说明
            budget: data.price,//价格
            industry: data.industry,//行业id
            tags: data.tags,//标签
            attachs: data.attachs//里面是attachId，类似于反馈上传那个地方

        };
        if(id){
            objParameter.id = id;
        }
        util.setAjax(
            inter.getApiUrl().addRequire,
            objParameter,
            function(json){
                if(json.status == 1){
                    window.location.href = '/user/require';
                }else{
                    $.alert(json.error);
                }
            },
            function(){
                $.alert('系统繁忙，请稍后重试!');
            }
        )
    })

});
