/**
 * GG *
 * 
 * @description: rtc
 * @author: 罗皓文
 * @update: zhouzhiqiang 2014 -5 -21
 */
define('module/webRtc',[ 'common/interface', 'common/util', 'module/uuid','module/mc'],function(inter, util, uuid, mc) {

			var PeerConnection = (window.RTCPeerConnection || window.webkitRTCPeerConnection || window.mozRTCPeerConnection),
			URL = (window.URL || window.webkitURL || window.msURL || window.oURL),
			getUserMedia = (navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia);
			nativeRTCIceCandidate = (window.mozRTCIceCandidate || window.RTCIceCandidate);
			nativeRTCSessionDescription = (window.mozRTCSessionDescription || window.RTCSessionDescription);
			window.WebSocket = window.WebSocket || window.MozWebSocket;

			window.sdpConstraints = {
                'optional': [],
				'mandatory' : {
					'OfferToReceiveAudio' : true,
					'OfferToReceiveVideo' : true
				}
			};
            if (window.mozRTCPeerConnection) {
                sdpConstraints.mandatory.MozDontOfferDataChannel = true;
            }

			var notWebRTC = [
					'<div id="consultMsgBox">',
					'<div class="cmb-tit clearfix">',
					'<span class="span-left">提示</span>',
					'</div>',
					'<div class="cmb-table">',
					'<table border="0" width="100%">',
					'<tr>',
					'<td class="cmb-content pl0" align="center">',
					'<p>对不起您的浏览器不支持视频和语音通讯若希望获得更好的浏览体验请下载最新版',
					'<a href="http://www.google.cn/intl/zh-CN/chrome/browser/" target="_blank">Google Chrome浏览器</a></p>',
					'</td>',
					'</tr>',
					'<tr>',
					'<td class="cmb-button">',
					'<button class="btn-default btn-xs btn-green cmb-start" id="downloadChrome">立即下载</button>',
					'<button class="btn-default btn-xs btn-gray cmb-stop" id="closeMsg">以后再说</button>',
					'</td>', '</tr>', '</table>', '</div>', '</div>' ].join('');

			// 浏览器不支持WebRTC的提示
			var notWebRTCF = function() {
				util.trace('not support webrtc');
				var api = $(notWebRTC);
				$('#init').find('#consultMsgBox').remove();
				$('#init').append(api);
				api 	.find('#downloadChrome')
						.bind(
								'click',
								function() {
									api.remove();
									window.open('http://www.google.cn/intl/zh-CN/chrome/browser/');
								});
				api.find('#closeMsg').bind('click', function() {
					api.remove();
				});
				setTimeout(function() {
					$('#video-status').removeAttr('class').addClass(
							'status-init');
					$('.init-bg').removeClass('none').show();
					$('.video-me').remove();
				}, 500)
			};

			var rtc = window.rtc = {};

            // 当 me ，you 都接通socket后 改为  true
            rtc.onLine = false;

			// Holds a connection to the server.
			rtc._socket = null;

			// Holds callbacks for certain events.
			rtc._events = {};

            //记录 是否为音频;
            rtc.isAudio = false;
			rtc.connectionTimeout = 10000

			rtc.on = function(eventName, callback) {
				rtc._events[eventName] = rtc._events[eventName] || [];
				rtc._events[eventName].push(callback);
			};

			rtc.streamCreated = false;

			rtc.fire = function(eventName, _) {
				var events = rtc._events[eventName];
				var args = Array.prototype.slice.call(arguments, 1);
				if (!events) {
					return;
				}
				for (var i = 0, len = events.length; i < len; i++) {
					events[i].apply(null, args);
				}
			};

			// Holds the STUN/ICE server to use for PeerConnections.
			rtc.SERVER = function() {

                var arrMozServer=[
                    {
                        "iceServers": [
                            {
                                "url": "stun:stun.1.google.com:19302"
                            },
                            {
                                "url": "turn:119.254.119.3:3478?transport=tcp",
                                "username": "class2",
                                "credential": "class2"
                            }
                        ]
                    },
                    {
                        "iceServers": [
                            {
                                "url": "stun:stun.1.google.com:19302"
                            },
                            {
                                "url": "turn:119.254.119.4:3478?transport=tcp",
                                "username": "class2",
                                "credential": "class2"
                            }
                        ]
                    }

                    ],
                    arrNotMozServer = [
                        {
                            "iceServers" : [
                                {
                                    "url" : "stun:stun.1.google.com:19302"
                                },
                                {
                                    "url" : "turn:class2@119.254.119.3:3478?transport=tcp",
                                    "credential" : "class2"
                                }
                            ]
                        },
                        {
                            "iceServers" : [
                                {
                                    "url" : "stun:stun.1.google.com:19302"
                                },
                                {
                                    "url" : "turn:class2@119.254.119.4:3478?transport=tcp",
                                    "credential" : "class2"
                                }
                            ]
                        }


                    ]

                var serverArray = [];

                serverArray = navigator.mozGetUserMedia ?   arrMozServer : arrNotMozServer;

                if(serverArray.length){
                    var index = parseInt(Math.random()*serverArray.length)
                    console.log("index " + index)
                    return serverArray[index];
                }else{
                    return {};
                }
			};
			// Reference to the lone PeerConnection instance.
			rtc.peerConnections = {};
			// Array of known peer socket ids
			rtc.connections = [];
			// Stream-related variables.
			rtc.streams = [];

			rtc.pc_constraints = {
				"optional" : [ {
					"DtlsSrtpKeyAgreement" : true
				} ]
			};
			rtc.sendOffers = function() {
				for (var i = 0, len = rtc.connections.length; i < len; i++) {
					var socketId = rtc.connections[i];
					rtc.sendOffer(socketId);
				}
			};

			rtc.onClose = function(data) {
				rtc.on('close_stream', function() {
					rtc.fire('close_stream', data);
				});
			};

			rtc.createPeerConnections = function() {
				for (var i = 0; i < rtc.connections.length; i++) {
					rtc.createPeerConnection(rtc.connections[i]);
				}
			};

			rtc.createPeerConnection = function(id) {
                util.trace("createPeerConnection --  id="+ id);
				var config = rtc.pc_constraints;
				var pc = rtc.peerConnections[id] = new PeerConnection(rtc.SERVER(), config);

                //检测到媒体流连接到本地，将其绑定到一个video标签上输出
                pc.onaddstream = function(event) {
                    util.trace('检测到媒体流连接到本地 onaddstream');
                    rtc.fire('add remote stream', event.stream, id);
                };

				// 发送ICE候选到其他客户端
				pc.onicecandidate = function(event) {
                    util.trace('pc onicecandidate')
					if (event.candidate) {
						rtc._socket.send(JSON.stringify({
							"code" : 232,
                            "messageId" : uuid.get(),
							"senderId" : me.id,
							"receiverId" : you.id,
							"candidate" : event.candidate,
							'sessionId' : 0
						}));
					}

//					rtc.fire('ice candidate', event.candidate);
				};

				pc.onopen = function() {
                    util.trace('pc open')
//					rtc.fire('peer connection opened');
				};

				return pc;
			};

			/**
			 * Connects to the websocket server. paramete:server (websocket
			 * server url)
			 */
			rtc.connect = function(server) {
				util.trace("start to connect " + server);
				try {
					if (!window.WebSocket) {
						util.trace('WebSocket not support');
						return;
					}
					rtc._socket = new WebSocket(server);
					rtc._socket.onopen = function() {
						util.trace('soket open');
						// 注册
						rtc._socket.send(JSON.stringify({
							"code" : 1,
							"id" : me.id,
                            "messageId" : uuid.get(),
							"token" : me.token,
							"type" : 1
						}));
						rtc._socket.send(JSON.stringify({
							"code" : 105,
                            "messageId" : uuid.get(),
							"date" : new Date().getTime(),
							"senderId" : me.id,
							"senderName" : me.name,
							"receiverId" : you.id,
							"receiverName" : you.name
						}));
						rtc._socket.onmessage = function(msg) {
							util.trace("on message " + msg.data)
							var data = JSON.parse(msg.data);
							rtc.fire(data.code + "", data);
						};

						rtc._socket.onerror = function(err) {
							util.trace('socket error' + err);
						};
						rtc._socket.onclose = function(data) {
							util.trace('soket onclose');
							var id = you.id;
							rtc.fire('disconnect stream', id);
							if (typeof (rtc.peerConnections[id]) !== 'undefined')
								rtc.peerConnections[id].close();
							delete rtc.peerConnections[id];
							// delete rtc.dataChannels[id];
							delete rtc.connections[id];
						};

                        rtc.on( 'remove_peer_connected', function (data) {
                                var id = you.id;
                                rtc.fire('disconnect stream', id);
                                if (typeof (rtc.peerConnections[id]) !== 'undefined')
                                    rtc.peerConnections[id].close();
                                delete rtc.peerConnections[id];
                                delete rtc.connections[id];
                            });
					}
				} catch (e) {
                    util.trace('error :' + e);
				}
			};

			rtc.sendOffer = function(id) {
				var pc = rtc.peerConnections[id];

                util.trace('sdpConstraints  :' + JSON.stringify(sdpConstraints));
                pc.createOffer(
                    function (session_description) {
                        session_description.sdp = preferOpus(session_description.sdp);
                        pc.setLocalDescription(session_description);
                        rtc._socket.send(JSON.stringify({
                            "code": 230,
                            "messageId" : uuid.get(),
                            "senderId": me.id,
                            "receiverId": you.id,
                            "sessionId": 0,
                            "agent": "agent",
                            "sdp": session_description
                        }));
                    }, function(){
                        util.trace('RTCPeerConnectionErrorCallback failureCallback');
                    }, sdpConstraints);
			};

			rtc.receiveOffer = function(id, sdp) {
				var pc = rtc.peerConnections[id];
				if (!pc) {
                    util.trace('receiveOffer and pc  is false');
					rtc.connections.push(id);
					rtc.createPeerConnections();
					pc = rtc.peerConnections[id];
					for (var i = 0; i < rtc.streams.length; i++) {
						var stream = rtc.streams[i];
						pc.addStream(stream);
					}
				}else{
                    util.trace('receiveOffer and pc  is true');
                    rtc.addStreams();
                }

				rtc.sendAnswer(id, sdp);
			};

			rtc.sendAnswer = function(id, sdp) {
				var pc = rtc.peerConnections[id];
				pc.setRemoteDescription(new nativeRTCSessionDescription(sdp));

                //如果是webkit 用sdpConstraints, moz 用 data.sdp
                if (navigator.webkitGetUserMedia) {
                    sdp = sdpConstraints;
                }

				pc.createAnswer(function(session_description) {
					pc.setLocalDescription(session_description);
					rtc._socket.send(JSON.stringify({
						"code" : 231,
                        "messageId" : uuid.get(),
						"senderId" : me.id,
						"receiverId" : you.id,
						"sessionId" : 0,
						"agent" : "agent",
						"sdp" : session_description
					}));
				}, function(){
                    util.trace('sendAnswer errorHandler')
                }, sdp);
			};

			rtc.receiveAnswer = function(id, sdp) {
				var pc = rtc.peerConnections[id];
				pc.setRemoteDescription(new nativeRTCSessionDescription(sdp));
			};

			rtc.createStream = function(opt, onSuccess, onFail) {
				var options;
				onSuccess = onSuccess || function() {};
				onFail = onFail || function() {};

				options = {
					"video" : !!opt.video,
					"audio" : !!opt.audio
				};
				if (getUserMedia) {
                    util.trace('getUserMedia '+ JSON.stringify(options));
                    getUserMedia.call(
                        navigator,
                        options,
                        function(stream) {

                            var data ={
                                "code" : 280,
                                "messageId" : uuid.get(),
                                "userId" : me.id,
                                "inviteeId" : you.id
                            };
                            rtc._socket.send(JSON.stringify(data));

                            rtc.streams.push(stream);
                            onSuccess(stream);
                        },
                        function(error) {
                            util.trace("Could not connect stream.");
                            onFail(error);
                        }
                    );
				} else {
					util.trace('getUserMedia is failed! ')
					notWebRTCF();
					return false;
				}
			};

			rtc.addStreams = function() {
				for (var i = 0; i < rtc.streams.length; i++) {
					var stream = rtc.streams[i];
					for ( var connection in rtc.peerConnections) {
                        util.trace('addStreams --'+ connection);
						rtc.peerConnections[connection].addStream(stream);
					}
				}
			};

			rtc.attachStream = function(stream, element) {
				if (typeof (element) === "string")
					element = document.getElementById(element);
				if (navigator.mozGetUserMedia) {
					element.mozSrcObject = stream;
				} else {
					element.src = URL.createObjectURL(stream) || stream ;
				}
                element.play();
			};

			rtc.on('ready', function() {
                util.trace('ready fired');
				rtc.createPeerConnections();
				rtc.addStreams();
				rtc.sendOffers();
			});

			function preferOpus(sdp) {
				var sdpLines = sdp.split('\r\n');
				var mLineIndex = null;
				// Search for m line.
				for (var i = 0; i < sdpLines.length; i++) {
					if (sdpLines[i].search('m=audio') !== -1) {
						mLineIndex = i;
						break;
					}
				}
				if (mLineIndex === null)
					return sdp;

				// If Opus is available, set it as the default in m line.
				for (var j = 0; j < sdpLines.length; j++) {
					if (sdpLines[j].search('opus/48000') !== -1) {
						var opusPayload = extractSdp(sdpLines[j], /:(\d+) opus\/48000/i);
						if (opusPayload)
							sdpLines[mLineIndex] = setDefaultCodec(	sdpLines[mLineIndex], opusPayload);
						break;
					}
				}

				// Remove CN in m line and sdp.
				sdpLines = removeCN(sdpLines, mLineIndex);

				sdp = sdpLines.join('\r\n');
				return sdp;
			}

			function extractSdp(sdpLine, pattern) {
				var result = sdpLine.match(pattern);
				return (result && result.length == 2) ? result[1] : null;
			}

			function setDefaultCodec(mLine, payload) {
				var elements = mLine.split(' ');
				var newLine = [];
				var index = 0;
				for (var i = 0; i < elements.length; i++) {
					if (index === 3)
					// Format of media starts from the fourth.
						newLine[index++] = payload;
					if (elements[i] !== payload)
						newLine[index++] = elements[i];
				}
				return newLine.join(' ');
			}

			function removeCN(sdpLines, mLineIndex) {
				var mLineElements = sdpLines[mLineIndex].split(' ');
				// Scan from end for the convenience of removing an item.
				for (var i = sdpLines.length - 1; i >= 0; i--) {
					var payload = extractSdp(sdpLines[i], /a=rtpmap:(\d+) CN\/\d+/i);
					if (payload) {
						var cnPos = mLineElements.indexOf(payload);
						if (cnPos !== -1) {
							// Remove CN payload from m line.
							mLineElements.splice(cnPos, 1);
						}
						// Remove CN line in sdp
						sdpLines.splice(i, 1);
					}
				}

				sdpLines[mLineIndex] = mLineElements.join(' ');
				return sdpLines;
			}

			// 合并约束
			function mergeConstraints(cons1, cons2) {
				var merged = cons1;
				for ( var name in cons2.mandatory) {
					merged.mandatory[name] = cons2.mandatory[name];
				}
				merged.optional.concat(cons2.optional);
				return merged;
			}
        if(!ued_conf.mcPath){
            mc.initConfig();
        }
        rtc.connect(ued_conf.mcPath);
    });