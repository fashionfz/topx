/**
 * @description: 反馈建议
 * @author:
 */
require(
    [ 'module/pageCommon', 'common/util', 'common/interface', 'module/showPicture'],
    function (common, util, inter, showPict) {
    common.initLogin();
    var suggestionCount = 500,
        suggestionInput = $('.suggestion'),
        uploader = $('#certificate').fileUploader({
            limit: 5,
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
            selectFileLabel: '上传图片',
            allowedExtension: 'jpg|jpeg|png',
            onUpErr: function (err) {
                $.alert(err);
            },

            afterEachUpload: function (data, status, form) {
                if (status == 1) {
                    // path 用于保存到后台数据表中
                    // pathsource 页面展示使用
                    var originName = uploader.originName;
                    var imgPath = data.pathsource;
                    var $img = $([
                        '<div class="image-item" data-id="' + data.attachId + '">',
                        '<a class="image-delete" title="删除"><em class="icon icon-upload-close"></em></a>',
                        '<table>',
                        '<tr>',
                        '<td><img class="db" src="' + imgPath + '"></td>',
                        '</tr>',
                        '</table>',
                        '</div>'
                    ].join(''));
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

        });



    util.wordsCount(suggestionInput, '.fb-textarea .fb-count', suggestionCount);

    $('.right-block-body .weixin').hover(
        function () {
            $('.right-block-body .qr-code').removeClass('none');
        },
        function () {
            $('.right-block-body .qr-code').addClass('none');
        }
    );

    /**
     * 验证投诉/反馈提交的内容
     */
    var _checkFeedback = function () {
        var returnData = {},
            uploading = $('.fb-complaints .uploading'),
            pending = $('.fb-complaints .pending'),
            error = $('.fb-complaints .error'),
            status = $('.fb-complaints .status').text();

        returnData.param = {};
        if (uploading.length > 0 || pending.length > 0) {
            $('.back-left .item').eq(2).after('<div class="error txt-red">还有文件正在上传或者等待上传中，请稍后再试。</div>');
        } else if (error.length > 0) {
            $('.back-left .item').eq(2).after('<div class="error txt-red">文件上传发生错误，请先删除后再重新上传。(' + status + ')</div>');
        } else {
            var suggestion = suggestionInput.val(),
                url = $('#url').val(),
                name = $('#name').val(),
                tel = $('#phone').val(),
                email = $('#mail').val(),
                qq = $('#qq').val(),
                images = [],
                imageBox = $('.upload-box .image-item');
            if (imageBox.length > 0) {
                $.each(imageBox, function () {
                    images.push({
                        attachId: $(this).attr('data-id')
                    })
                });
            }
            returnData.param.href = url;
            returnData.param.userName = name;
            returnData.param.phone = tel;
            returnData.param.email = email;
            returnData.param.qq = qq;
            returnData.param.attachs = images;
            if (suggestion.length == 0) {
                $('.back-left .item').eq(0).after('<div class="error txt-red">请输入反馈/建议内容</div>');
            } else if (suggestion.length > suggestionCount) {
                $('.back-left .item').eq(0).after('<div class="error txt-red">反馈/建议内容最多' + suggestionCount + '个字</div>');
            } else {
                returnData.param.content = suggestion;
                _saveFeedback(returnData);
            }
        }
    };

    var reset = function () {
        $('.back-left .error').remove();
        $('.txt').val('');
        suggestionInput.val('');
        $('.image-item').each(function () {
            $(this).remove();
            uploader.removeFile();
        })
    };
    /**
     * 提交投诉/反馈
     */
    var _saveFeedback = function (arg) {
        var tips = $.tips('loading');
        util.setAjax(inter.getApiUrl().saveFeedbackUrl, arg.param, function (json) {
            tips.close();
            if (json.status) {
                reset();
                $.alert('提交成功！');
            } else {
                $.alert(json.error);
            }
        }, function () {
            tips.close();
            $.alert('服务器繁忙，请稍后再试。');
        });
    };

    $('#submitFeedback').off('click').on('click', function () {
        $('.back-left .error').remove();
        _checkFeedback();
    })
});
