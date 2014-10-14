Ext.define('Topx.view.ResumeGrid', {
	id:'resumeGrid',
	extend : 'Ext.grid.Panel',
	alias : 'widget.resumegrid',
	title : '用户帐号',
	store : 'ResumeGrid',
	selType: 'rowmodel',
    plugins: [
              Ext.create('Ext.grid.plugin.RowEditing', {
                  clicksToEdit: 1
              })
          ],
//	loadMask : true,
//	forceFit : false, //forceFit 属性: 表示除了auto expand（自动展开）外，还会对超出的部分进行缩减，让每一列的尺寸适应GRID的宽度大小，阻止水平滚动条的出现。
//	autoScroll:true,
	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			columns : [ new Ext.grid.RowNumberer({
				header : "序号",
				width : 40
			}),  {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'userId',
				text : '简历人ID',
//				locked: true,
				width : 160,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'email',
				text : '邮箱',
//				locked: true,
				width : 160,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'source',
				text : '来源',
//				locked: true,
				width : 120,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'sourceResume',
				text : '英文简历',
//				locked: true,
				width : 150,
				renderer:function(value,metaData, record){
					var sourceResumeUrl = record.get('sourceResumeUrl');
					if(sourceResumeUrl!=""){
						return Ext.String.format('<a href="{0}" title="点击进入查看页面" target="_blank">{1}</span>',sourceResumeUrl,value);
					} else {
						return Ext.String.format('<a target="_blank">{0}</span>',value);
					}
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'translationResume',
				text : '翻译简历',
				width : 150,
				renderer:function(value,metaData, record){
					var userId = record.get('userId');
					return Ext.String.format('<a href="/resume/base?userId={0}" title="点击进入页面" target="_blank">{1}</span>',userId,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'status',
				text : '状态',
				width : 100,
				renderer:function(value){
					if(value=="untranslated"){
						return Ext.String.format('<span title="{0}">{1}</span>',"未翻译","未翻译");
					}else if(value=="inprogress"){
						return Ext.String.format('<span title="{0}">{1}</span>',"处理中","处理中");
					}else if(value=="translated"){
						return Ext.String.format('<span title="{0}">{1}</span>',"已翻译","已翻译");
					}else if(value=="invalidate"){
						return Ext.String.format('<span title="{0}">{1}</span>',"已作废","已作废");
					}
				}
			},  {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'createDate',
				text : '接收时间',
				width : 160,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			},  {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'translateDate',
				text : '翻译时间',
				width : 160,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			},  {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'userId',
				text : '操作',
				width : 200,
				renderer:function(value, metaData, record, rowIndex, colIndex, store, view) {
					//return Ext.String.format('<a href="/resume/base?userId={0}" target="_blank" title="点击进入处理页面">{1}</a>',value,"开始处理");
					var text = '';
					var status = record.get('status');
					var userId = record.get('userId');
					var id = record.get('id');
					if (status == "untranslated" || status == "inprogress") {
						text = text + '<a href="/resume/base?userId='+userId+'" target="_blank" title="点击进入处理页面">进行处理</a>';
						text = text + '&nbsp;&nbsp;<a href="javascript:void(0)" onclick="changeToComplate('+id+');">翻译完毕</a>';
						text = text + '&nbsp;&nbsp;<a href="javascript:void(0)" onclick="changeToInvalidate('+id+');">废弃</a>';
					} 
					return text;
				}
			}],
			bbar : {
				xtype : 'pagingtoolbar',
				store : 'ResumeGrid',
				displayInfo : true,
				displayMsg : '显示第 {0} 条到 {1} 条记录，一共  {2} 条',
				emptyMsg : "没有记录"
			},
			tbar : me._getSearchBar(me)
		});

		me.callParent(arguments);
		var store = Ext.getStore(me.store);
		store.on('beforeload', function(store, options) {
			var newParams = {
				'searchText' : Ext.getCmp('resumeSearchText').getValue(),
				'status' : Ext.getCmp('status').getValue()
			};
			Ext.apply(store.proxy.extraParams, newParams);
		});
		store.load();
	},

	_getSearchBar : function(me) {
		return [ {
			id : 'resumeSearchText',
			xtype : 'textfield',
			emptyText : '用户ID/Email/源简历名称',
			width: 150,
			listeners : {
				specialKey : function(field, e) {
					if (e.getKey() == Ext.EventObject.ENTER) {
						me.search(me);
					}
				}
			}
		}, {// 状态
			id : 'status',
			xtype : 'combobox',
			store : [ [ '', '全部简历' ], [ '0', '未翻译' ], [ '1', '处理中' ], [ '2', '已翻译' ], [ '3', '作废' ] ],
			value : '',
			editable : false,
			queryMode : 'local',
			width: 100,
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		}];
	},
	
	search : function(me) {
		var store = Ext.getStore(me.store);
		store.currentPage = 1;
		store.load();
	}
	
});


function changeToComplate(id){
	var me = Ext.getCmp('resumeGrid');
	Ext.Msg.confirm('Name', '确认翻译完成？', function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : '/system/resume/toCompleted?id='+id,
				method : 'GET',
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.status=="1") {
						var store = Ext.getStore(me.store);
						store.currentPage = 1;
						store.load();
					} else {
						Ext.Msg.alert('操作失败', obj.error);
					}
				},
				failure : function() {
					Ext.Msg.alert('failure', '操作失败');
				}
			});
		}
	});
}

function changeToInvalidate(id){
	var me = Ext.getCmp('resumeGrid');
	Ext.Msg.confirm('Name', '确认废弃？', function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : '/system/resume/toInvalid?id='+id,
				method : 'GET',
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.status=="1") {
						var store = Ext.getStore(me.store);
						store.currentPage = 1;
						store.load();
					} else {
						Ext.Msg.alert('操作失败', obj.error);
					}
				},
				failure : function() {
					Ext.Msg.alert('failure', '操作失败');
				}
			});
		}
	});
}

/**
 * 刷新页面
 */
function refreshResumeGrid(){
	var me = Ext.getCmp('resumeGrid');
//	console.info(me);
	var store = Ext.getStore(me.store);
	store.load();
}
