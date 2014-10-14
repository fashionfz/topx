/**
 * @description: flash上传模块
 * @author: Zhang Guanglin
 * @update: TinTao
 */
define('module/swfUpload', [
    'module/swfUpload',
    'module/userDetail'
], function(swfUpload, DetailAPI){
	function uploadEvent(data){
        console.log('data----'+data)
        if( data == 'M01109' ){
            $.alert('摄像头已被程序占用，请先停止占用程序再试。');
        }else if( data == 'M01108' ){
            $.alert('您选择的文件过大，请选择2M以内的文件。');
        }else if( data == 'M01107' ){
            $.alert('您选择的文件格式错误，请选择格式为JPG、GIF、PNG的文件。');
        }else if( $.type(data) === "string" ){
            data = $.parseJSON(data);
            switch(data.status){
                case 1: // 上传头像成功
                    $('#userHead,#user-head').attr('src', data.avatar_190 + '?' + Math.random());
                    $('.header-head,.img-full').attr('src', data.avatar_70 + '?' + Math.random());
                    DetailAPI.showSaveSuccess();
                    //$.dialog.get('uploadHead').close();
                    break;
                case 2:
                    break;
                case -1:
                    break;
                case -2: // 上传头像失败
                    $.alert('上传失败！');
                    break;
                case 3:
                    $('#userHead').attr('src', ued_conf.root + 'images/dev-head-default2.png');
                    //$.dialog.get('uploadHead').close();
                    break;
                default:
                    //$.alert(typeof(data.status) + ' ' + data.status);
                    //$.dialog.get('uploadHead').close();
                    break;
            }
        }

	}

	var uploadUrl = null;

    window.uploadevent = uploadEvent;

	var flashvars = {
	    "jsfunc": "uploadevent",
	    "imgUrl": "/no190x190.jpg",
	    "pid": "75642723",
	    "uploadSrc": true,
	    "showBrow": true,
	    "showCame": true,
	    "uploadUrl": uploadUrl
	};

	var params = {
		menu: "true",
		scale: "noScale",
		allowFullscreen: "true",
		allowScriptAccess: "always",
		wmode:"transparent",
		bgcolor: "#FFFFFF"
	};

	var attributes = {
		id:"FaustCplus"
	};

	return {
		init : function(uploadUrl, panel){
            if(swfobject.hasFlashPlayerVersion("9.0.18")){
                flashvars.uploadUrl = uploadUrl;
                var headUrl = $('.img-full').attr('src');
                if(headUrl.indexOf('dev-head-default')==-1){
                	var image = new Image();
                	image.src = headUrl;
					/*$(image).load(function(){*/
						flashvars.imgUrl = headUrl.replace(/avatar_70/,'avatar_190');
					/*});*/
                }
                swfobject.embedSWF("/FaustCplus.swf", panel, "650", "500", "9.0.0", "/expressInstall.swf", flashvars, params, attributes);
            }else{
                $('#'+panel).html('<div style="width: 650px; height:500px; text-align: center;"><div style="height:100%" class="picture"><div style="line-height:460px;"><span style="font-size:18px">您还没有安装flash播放器,请点击<a target="_blank" href="http://www.adobe.com/go/getflash">这里</a>安装</span></div></div></div>');
            }

		}
	}
});