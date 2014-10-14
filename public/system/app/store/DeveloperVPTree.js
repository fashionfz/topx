/**
 * 开发人员使用的功能
 */
Ext.define('Topx.store.DeveloperVPTree', {
	extend : 'Ext.data.TreeStore',
	root : {
		expanded : true,
		children : [ {
			id : 'bc_developer',
			text : "后台管理 (开发人员专用)",
			expanded : true,
			children : [ {
				id : "log-treenode",
				text : "系统日志",
				tabxtype: "sysLoggrid",
				leaf : true
			}, {
				id : "config-treenode",
				text : "配置管理",
				tabxtype: "configgrid",
				leaf : true
			} ]
		} ]
	}

});