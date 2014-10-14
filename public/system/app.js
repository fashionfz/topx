var scripts=document.getElementsByTagName("script");
var curJS=scripts[scripts.length-1].src.split('?')[1].split('&');;   //curJS就是我们当前的js文件
//var cdnUrl = "http://192.168.0.100/topx"
var cdnUrl = "";
//var cdnUrl = "http://115.28.44.61/topx";



function getWebIp(){
	if(cdnUrl!=null&&cdnUrl!=""){
		return cdnUrl.substring(0,cdnUrl.lastIndexOf("/"));
	}
	return "";
}

Ext.application({

	requires : [ 'Ext.container.Viewport' ],

	name: 'Topx',
	
	appFolder : cdnUrl+'/assets/system/app',
	
	models	: ['TagGrid','CommentGrid', 'UserGrid', 'ResumeGrid', 'UserCountry', 'FeedbackGrid', 'SuggestionGrid', 'TradeGrid', 'OperateLogGrid', 'OperateModule', 'SysLogGrid','ConfigGrid','MenuGrid','RoleGrid','AdminGrid','TemplateGrid','KeyWordGrid','CateGrid','ConfigKeyWordGrid'],

	stores	: ['VPTree',  'UserGrid', 'ResumeGrid', 'UserCountry', 'FeedbackGrid', 'SuggestionGrid', 'TradeGrid', 'OperateLogGrid', 'OperateModule', 'SysLogGrid','MenuGrid','RoleGrid','AdminGrid','MenuDetailGrid','TemplateGrid','KeyWordGrid','CateGrid','ConfigKeyWordGrid'],

	views	: ['VPTree', 'TagEditWin', 'UserGrid', 'ResumeGrid', 'UserStateEditWin', 'UserTopWin','HotKeywordForm', 'SensitiveCharacterForm','TagEditGrid','CateGrid','CommentGrid','ReplyGrid','TagHBox','CommentReplyBox','ResumeHBox', 'CateEditWin', 'FeedbackPanel', 'SuggestionGrid', 'FeedbackGrid', 'FeedbackStateEditWin', 'TradeSearchGrid', 'OperateLogGrid', 'SysLogGrid','ConfigGrid','MenuGrid','RoleGrid','AdminGrid','RoleAuthEditWin','RoleEditWin','AdminEditWin','AdminAuthEditWin','MenuDetailGrid','MenuEditWin','TemplateGrid','TemplateEditWin','KeyWordGrid','UserHBox','HtmlEditorVariable','ConfigKeyWordGrid','ConfigKeyWordEditWin'],

	controllers : [ 'VPTree', 'TagEditWin', 'UserGrid', 'ResumeGrid', 'UserStateEditWin', 'UserTopWin', 'FeedbackGrid', 'SuggestionGrid', 'FeedbackStateEditWin', 'TradeSearchGrid', 'OperateLogGrid', 'SysLogGrid', 'IntroImgGrid', 'MobileLogGrid','MenuGrid','RoleGrid','AdminGrid','RoleAuthEditWin','RoleEditWin','AdminEditWin','AdminAuthEditWin','MenuEditWin','TemplateGrid','TemplateEditWin','KeyWordGrid','CateGrid','ConfigKeyWordEditWin'],

	launch : function() {
    	Ext.override(Ext.ToolTip, {
    		maxWidth :9999
    	});
    	
		Ext.create('Ext.container.Viewport', {
			layout : {
				type : 'border',
				padding : '0 5 5 5' // pad the layout from the window edges
			},
			items : [ {
				id : 'app-header',
				xtype : 'box',
				region : 'north',
				height : 70,
				html : '<table width="100%" height="100%"><tr><td>嗨啰应用管理平台</td></tr>'
					+'<tr><td align="right"><font size="2">您好 '+curJS[0].split('=')[1]+' ['+decodeURIComponent(curJS[1].split('=')[1])+']&nbsp;&nbsp;&nbsp;&nbsp;'
					+'<a href="/system/logout">注  销 </a></font></td></tr></table>'
			}, {
				xtype : 'container',
				region : 'center',
				layout : 'border',
				items : [ {
					xtype : 'VPTree',
					region : 'west',
					animCollapse : true,
					width : 200,
					minWidth : 150,
					maxWidth : 400,
					split : true,
					collapsible : true
				}, {
					id : 'vptabpanel',
					xtype : 'tabpanel',
					region : 'center',
					enableTabScroll : true,
					defaults : {
						autoScroll : true
					},
					items : []
				}]
			}]
		});
	}
});