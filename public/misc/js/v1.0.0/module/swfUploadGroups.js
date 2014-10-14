/**
 * @description: flash上传模块
 * @author: Zhang Guanglin
 * @update: TinTao
 */
define('module/swfUploadGroups', [], function(){
	function uploadEvent(data){
        console.log('data----'+data)
        if( data == 'M01109' ){
            $.alert('摄像头已被程序占用，请先停止占用程序再试。');
        }else if( data == 'M01108' ){
            $.alert('您选择的文件过大，请选择2M以内的文件。');
        }else if( data == 'M01107' ){
            $.alert('您选择的文件格式错误，请选择格式为JPG、GIF、PNG的文件。');
        }else{
            if( $.type(data) === "string" )
                data = $.parseJSON(data);
            switch(data.status){
                case 1: // 上传头像成功
                    $('#groupHeadImage').prop('src', data.avatar_190);
                    $('#groupHeadUrl').val(data.avatar_190_source);
                    if(window.uploadGroupHead){
                        window.uploadGroupHead.close();
                    }
                    break;
                default:
                    $.alert('上传失败！');
                    break;
            }
        }

	}

	var uploadUrl = null;

    window.uploadevent = uploadEvent;

	var flashvars = {
	    "jsfunc": "uploadevent",
	    "imgUrl": "/assets/misc/images/group-default190x190.png",
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
            var self = this;
            if(swfobject.hasFlashPlayerVersion("9.0.18")){
                flashvars.uploadUrl = uploadUrl;
                var headUrl = $('#groupHeadImage').prop('src'),
                    image = null;
                if(headUrl){
                	image = new Image();
                	image.src = headUrl;
                    image.onerror = function(){
                        flashvars.imgUrl = ued_conf.root +'images/group-default190x190.png';
                        self.loadSwf(panel);
                    };
                    image.onload = function(){
                        flashvars.imgUrl = headUrl;
                        self.loadSwf(panel);
                    }
                }else{
                    flashvars.imgUrl = ued_conf.root +'images/group-default190x190.png';
                    self.loadSwf(panel);
                }
            }else{
                $('#'+panel).html('<div style="width: 650px; height:500px; text-align: center;"><div style="height:100%" class="picture"><div style="line-height:460px;"><span style="font-size:18px">您还没有安装flash播放器,请点击<a target="_blank" href="http://www.adobe.com/go/getflash">这里</a>安装</span></div></div></div>');
            }

		},
        loadSwf : function(panel){
            swfobject.embedSWF("/FaustCplus.swf", panel, "650", "500", "9.0.0", "/expressInstall.swf", flashvars, params, attributes);
        }
	}
});