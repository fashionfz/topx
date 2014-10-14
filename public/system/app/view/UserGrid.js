Ext.define('Topx.view.UserGrid', {
	id:'userGrid',
	extend : 'Ext.grid.Panel',
	alias : 'widget.usergrid',
	title : '用户帐号',
	store : 'UserGrid',
	loadMask : true,
	forceFit : false, //forceFit 属性: 表示除了auto expand（自动展开）外，还会对超出的部分进行缩减，让每一列的尺寸适应GRID的宽度大小，阻止水平滚动条的出现。
	autoScroll:true,
	selType: 'rowmodel',
    plugins: [
              Ext.create('Ext.grid.plugin.RowEditing', {
                  clicksToEdit: 1
              })
          ],
	initComponent : function() {
		var me = this;
		Ext.applyIf(me, {
			columns : [ {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'userId',
				text : '用户Id',
				locked: true,
				width : 80,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'email',
				text : '邮箱',
				locked: true,
				width : 180,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'userName',
				text : '姓名',
				locked: true,
				width : 180,
				renderer:function(value,metaData, record){
					var userId = record.get('userId');
					return Ext.String.format('<a href="/expert/detail/{0}" target="_blank" title="{1}，点击查看个人资料">{2}</a>',userId,value,value);
				}
			}, {
				xtype : 'gridcolumn',
				renderer : function(value, metaData, record, rowIndex, colIndex, store, view) {
					if (value == 0) {
						return "男";
					} else if (value == 1) {
						return "女";
					} else {
						return "";
					}
				},
				sortable : false,
				dataIndex : 'gender',
				text : '性别',
				width : 50
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'phoneNumber',
				text : '手机号码',
				width : 120,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'country',
				text : '国籍',
				width : 100,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'registerDate',
				text : '注册时间',
				width : 150,
				renderer:function(value){
					return Ext.String.format('<span title="{0}">{1}</span>',value,value);
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'skillsTags',
				text : '标签',
				width : 50,
				renderer : function(value, metadata) {
					/*var tagJson = JSON.parse(value);
					metadata.tdAttr = 'data-qtip="' + tagJson + '"'

					var length = tagJson.length;
					if (length <= 0) {
						return '<font>&nbsp;' + tagJson.length + '&nbsp;</font>';
					} else {
						return '<font style="text-decoration:underline;cursor:pointer;">&nbsp;' + tagJson.length + '&nbsp;</font>';
					}*/
					// 处理IE显示问题
					var tagJson = new Array();
					if(value!=null&&value.length>0){
//						alert(value);
						tagJson = value.split("@");
					}
					metadata.tdAttr = 'data-qtip="' + tagJson + '"';
					var length = tagJson.length;
					if (length <= 0) {
						return '&nbsp;' + tagJson.length + '&nbsp;';
					} else {
						return '<font style="text-decoration:underline;cursor:pointer;">&nbsp;' + tagJson.length + '&nbsp;</font>';
					}
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'inTags',
				text : '行业',
				width : 50,
				renderer : function(value, metadata) {
					// 处理IE显示问题
					var tagJson = new Array();
					if(value!=null&&value.length>0){
						tagJson = value.split("@");
					}
					metadata.tdAttr = 'data-qtip="' + tagJson + '"';
					var length = tagJson.length;
					if (length <= 0) {
						return '&nbsp;' + tagJson.length + '&nbsp;';
					} else {
						return '<font style="text-decoration:underline;cursor:pointer;">&nbsp;' + tagJson.length + '&nbsp;</font>';
					}
				}
			}, {
				xtype : 'gridcolumn',
				dataIndex : 'tradeNum',
				text : '交易次数',
				width : 80
			}, {
				xtype : 'gridcolumn',
				dataIndex : 'averageScore',
				text : '评分',
				width : 50
			}, {
				xtype : 'gridcolumn',
				/*width : 200,*/
				width : 80,
				sortable : false,
				/*text : '状态(禁用|在线|投诉)',*/
				text : '账号状态',
				renderer : function(value, metaData, record, rowIndex, colIndex, store, view) {
					var text = '';
					var isEnable = record.get('enable');
					/*var isOnline = record.get('online');
					var isComplain = record.get('complain');*/

					if (isEnable) {
						text = text + '<font style="color:green">正常</font>';
					} else {
						text = text + '<font style="color:red">禁用</font>';
					}
					/*if (isOnline) {
						text = text + ' | <font style="color:green">在线</font>';
					} else {
						text = text + ' | <font style="color:gray">下线</font>';
					}
					if (isComplain) {
						text = text + ' | <font style="color:orange">被投诉</font>';
					} else {
						text = text + ' | <font style="color:green">未被投诉</font>';
					}*/
					return text;
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'balance',
				text : '余额',
				width : 100
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'onlineService',
				text : '嗨啰在线客服',
				width : 90,
				renderer:function(value){
					if(value) {
						return Ext.String.format('<span title="{0}" style="color:green">{1}</span>',"是嗨啰在线客服","是");
					} else {
						return Ext.String.format('<span title="{0}">{1}</span>',"不是嗨啰在线客服","否");
					}
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'onlineTranslation',
				text : '嗨啰在线翻译',
				width : 90,
				renderer:function(value){
					if(value) {
						return Ext.String.format('<span title="{0}" style="color:green">{1}</span>',"是嗨啰在线翻译","是");
					} else {
						return Ext.String.format('<span title="{0}">{1}</span>',"不是嗨啰在线翻译","否");
					}
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				dataIndex : 'resumeStatus',
				text : '海外简历发布情况',
				width : 150,
				renderer:function(value, metaData, record, rowIndex, colIndex, store, view) {
					var userId = record.get('userId');
					if(userId-1000000>=0){
						return Ext.String.format('<span title="{0}" style="color:green">{1}</span>',"国际版用户，不用发布简历","国际版用户");
					}
					if(value=="published") {
						return Ext.String.format('<span title="{0}" style="color:green">{1}</span>',"已发布海外简历（翻译中）","已发布");
					} else if(value=="translated") {
						return Ext.String.format('<span title="{0}">{1}</span>&nbsp;<input type="button" style="width:100px;height:20px" onclick="publishResumeTask('+userId+')" value="发布海外简历"',"海外简历已翻译","已翻译");
					} else {
						return Ext.String.format('<span title="{0}">{1}</span>&nbsp;<input type="button" style="width:100px;height:20px" onclick="publishResumeTask('+userId+')" value="发布海外简历"',"未发布海外简历","未发布");
					}
				}
			}, {
				xtype : 'gridcolumn',
				sortable : false,
				text : '置顶',
				width : 70,
				renderer : function(value, metaData, record, rowIndex, colIndex, store, view) {
					var text = '';
					var isTop = record.get('top');
					var topIndustryName = record.get('topIndustryName');
					if (isTop) {
						if (topIndustryName!=null && topIndustryName.length>0) {
							text = text + '<span style="color:green" title="置顶行业：'+topIndustryName+'">置顶</span>';
						} else {
							text = text + '<font style="color:green">置顶</font>';
						}
					} else {
						text = text + '<font style="color:gray">正常</font>';
					}

					return text;
				}
			}, {
				xtype : 'actioncolumn',
				items : [ {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/edit_task.png",
					tooltip : '编辑',
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var formData = {};
						formData.userId = rec.data.userId;
						formData.userIdDisplay = formData.userId;
						formData.emailDisplay = rec.data.email;
						formData.userNameDisplay = rec.data.userName;
						formData.disableState = +rec.data.enable + "";
						formData.onlineState = +rec.data.online + "";
						formData.complainState = +rec.data.complain + "";
						formData.onlineService = +rec.data.onlineService + "";
						formData.onlineTranslation = +rec.data.onlineTranslation + "";

						var win = Ext.create('widget.userstateeditwin');
						var form = win.queryById('userStateEditForm').getForm();
						form.setValues(formData);
						win.show();
					}
				}, {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/go_top.png",
					tooltip : '置顶【至少选择了一个行业的用户才能被置顶】',
					getClass : function(v, metadata, r) {
//						alert(r.data.inTags);
						if (r.data.inTags==null||r.data.inTags.length==0) {
							return 'hidden';
						}
						return r.data.top ? 'hidden' : '';
					},
					/*handler : function(view, rowIndex, colIndex, item, e, record) {
						Ext.Msg.confirm('提示', '确认置顶该用户？', function(btn) {
							if (btn == 'yes') {
								Ext.Ajax.request({
									url : '/system/user/top',
									params : {
										userId : record.data.userId
									},
									method : 'POST',
									success : function(response) {
										var obj = Ext.decode(response.responseText);
										if (!obj.success) {
											if (obj.errorCode == undefined) {
												Ext.Msg.alert('failure', '置顶失败');
											} else {
												Ext.Msg.alert('failure', obj.errorMsg);
											}
										} else {
											me.search(me);
										}
									},
									failure : function() {
										Ext.Msg.alert('failure', '置顶失败');
									}
								});
							}
						});
					}*/
					handler : function(grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var formData = {};
						formData.userId = rec.data.userId;
						formData.userIdDisplay = formData.userId;
						formData.emailDisplay = rec.data.email;
						formData.userNameDisplay = rec.data.userName;
						/*formData.disableState = +rec.data.enable + "";
						formData.onlineState = +rec.data.online + "";
						formData.complainState = +rec.data.complain + "";*/
						var re = /@/gi;
						var inTagsData = rec.data.inTags;
						formData.inTagsDisplay = inTagsData.replace(re,",");

						var win = Ext.create('widget.usertopwin');
						var form = win.queryById('userTopForm').getForm();
						form.setValues(formData);
						win.show();
					}
				}, {
					icon : cdnUrl + "/assets/system/ext/shared/extjs/images/fam/go_jump.png",
					tooltip : '取消置顶',
					getClass : function(v, metadata, r) {
						return r.data.top ? '' : 'hidden';
					},
					handler : function(view, rowIndex, colIndex, item, e, record) {
						Ext.Msg.confirm('Name', '确认取消置顶该用户？', function(btn) {
							if (btn == 'yes') {
								Ext.Ajax.request({
									url : '/system/user/untop',
									params : {
										userId : record.data.userId
									},
									method : 'POST',
									success : function(response) {
										var obj = Ext.decode(response.responseText);
										if (!obj.success) {
											Ext.Msg.alert('failure', '置顶失败');
										} else {
											me.search(me);
										}
									},
									failure : function() {
										Ext.Msg.alert('failure', '置顶失败');
									}
								});
							}
						});
					}
				} ]
			} ],
			bbar : {
				xtype : 'pagingtoolbar',
				store : 'UserGrid',
				displayInfo : true,
				displayMsg : '显示第 {0} 条到 {1} 条记录，一共  {2} 条',
				emptyMsg : "没有记录"
			},
			tbar : me._getSearchBar(me)
		});

		me.callParent(arguments);
		var store = Ext.getStore(me.store);
		
		var ountry = Ext.getStore(Ext.getCmp('country').store);
		ountry.load();
		store.on('beforeload', function(store, options) {
			var newParams = {
				'searchText' : Ext.getCmp('searchText').getValue(),
				'isEnable' : Ext.getCmp('isEnable').getValue(),
				/*'isComplain' : Ext.getCmp('isComplain').getValue(),*/
				/*'isOnline' : Ext.getCmp('isOnline').getValue(),*/
				'gender' : Ext.getCmp('gender').getValue(),
				'country' : Ext.getCmp('country').getValue(),
				'isTop' : Ext.getCmp('isTop').getValue(),
				'inId' : Ext.getCmp('inId').getValue(),
				'userType' : Ext.getCmp('userType').getValue(),
				'startDate' : dateFormat(Ext.getCmp('startDate').getValue()),
				'endDate' : dateFormat(Ext.getCmp('endDate').getValue())
			};
			Ext.apply(store.proxy.extraParams, newParams);
		});
		store.load();
	},
	

	_getSearchBar : function(me) {
		return [ {     
	        xtype:'datefield',  
	        id : 'startDate',  
	        emptyText : '注册起始日期',
	        editable:false,  
	        height:20,  
	        width: 100,
	        format:'Y-m-d',
			listeners : {
				change : function() {
					me.search(me);
				}
			}
	    },{  
	        text:'清空',  
	        handler:function(){  
	                    Ext.getCmp("startDate").setValue("");  
	                }  
	    },{     
	        xtype:'datefield',  
	        id : 'endDate',  
	        emptyText : '注册结束日期',
	        editable:false,  
	        height:20,  
	        width: 100,
	        format:'Y-m-d',
	        listeners : {
				change : function() {
					me.search(me);
				}
			}
	    },{  
	        text:'清空',  
	        handler:function(){  
	                    Ext.getCmp("endDate").setValue("");  
	                }  
	    },{
			id : 'searchText',
			xtype : 'textfield',
			emptyText : 'ID/姓名/Email/手机号码',
			width: 150,
			listeners : {
				specialKey : function(field, e) {
					if (e.getKey() == Ext.EventObject.ENTER) {
						me.search(me);
					}
				}
			}
		}, {
			id : 'inId',
			xtype : 'combobox',
			name : 'inId',
			width: 100,
			store : new Ext.data.Store({
				fields : [ 'id', 'tagName' ],
				proxy : {
					type : 'ajax',
					reader : 'json',
					url : '/system/tag/queryind?type=all'
				},
				autoLoad : true
			}),
			value : '全部行业',
			queryMode : 'local',
			displayField : 'tagName',
			valueField : 'id',
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		} ,{
			id : 'country',
			xtype : 'combobox',
			store : 'UserCountry',
			displayField : 'name',
			valueField : 'value',
			editable : false,
			value : '',
			width: 120,
			queryMode : 'local',
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		}, {//性别
			id : 'gender',
			xtype : 'combobox',
			store : [ [ '', '全部性别' ], [ '0', '男' ], [ '1', '女' ] ],
			value : '',
			editable : false,
			queryMode : 'local',
			width: 100,
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		}, {//禁用状态
			id : 'isEnable',
			xtype : 'combobox',
			width: 100,
			store : [ [ '', '账号状态' ], [ '0', '禁用' ], [ '1', '正常' ] ],
			value : '',
			editable : false,
			queryMode : 'local',
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		}
		/*, {//在线状态
			id : 'isOnline',
			xtype : 'combobox',
			store : [ [ '', '在线状态' ], [ '0', '不在线' ], [ '1', '在线' ] ],
			value : '',
			editable : false,
			queryMode : 'local',
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		}, {//投诉状态
			id : 'isComplain',
			xtype : 'combobox',
			store : [ [ '', '投诉状态' ], [ '0', '未被投诉' ], [ '1', '被投诉' ] ],
			value : '',
			editable : false,
			queryMode : 'local',
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		}*/, {//置顶
			id : 'isTop',
			xtype : 'combobox',
			width: 100,
			store : [ [ '', '置顶状态' ], [ '0', '正常' ], [ '1', '置顶' ] ],
			value : '',
			editable : false,
			queryMode : 'local',
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		}, {// 在线客户与在线翻译
			id : 'userType',
			xtype : 'combobox',
			width: 130,
			store : [ [ '', '全部用户' ], [ '0', '嗨啰在线客服' ], [ '1', '嗨啰在线翻译' ], [ '2', '海外简历未发布' ], [ '3', '海外简历已发布(翻译中)' ], [ '4', '海外简历已翻译' ], [ '5', '中文版用户' ], [ '6', '国际版用户' ] ],
			value : '',
			editable : false,
			queryMode : 'local',
			listeners : {
				change : function() {
					me.search(me);
				}
			}
		},{  
	        text:'导出EXCEL',  
	        xtype : 'button',
	        handler:function(){
				var newParams = 'searchText='+Ext.getCmp('searchText').getValue()
						+'&isEnable='+Ext.getCmp('isEnable').getValue()
						+'&gender='+Ext.getCmp('gender').getValue()
						+'&country='+Ext.getCmp('country').getValue()
						+'&isTop='+Ext.getCmp('isTop').getValue()
						+'&inId='+Ext.getCmp('inId').getValue()
						+'&userType='+Ext.getCmp('userType').getValue()
						+'&startDate='+dateFormat(Ext.getCmp('startDate').getValue())
						+'&endDate='+dateFormat(Ext.getCmp('endDate').getValue());
	                window.location ="/system/downloadExcel?"+newParams;
	          }  
	    } ];
	},

	search : function(me) {
		var store = Ext.getStore(me.store);
		store.currentPage = 1;
		store.load();
	}

});
//格式化日期，规避日期内出现"T"字符
function dateFormat(value){ 
    if(null != value){ 
        return Ext.Date.format(new Date(value),'Y-m-d'); 
    }else{ 
        return null; 
    }
}

/**
 * 发布海外简历任务
 */
function publishResumeTask(userId){
	var me = Ext.getCmp('userGrid');
	Ext.Msg.confirm('Name', '确认发布该用户的海外简历？', function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : '/system/resume/publishTask?userId='+userId,
				method : 'GET',
				success : function(response) {
					var obj = Ext.decode(response.responseText);
					if (obj.status=="1") {
						var store = Ext.getStore(me.store);
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