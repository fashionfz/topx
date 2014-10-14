
--清空权限相关数据表数据

delete from r_role_url;
delete from r_admin_role;
delete from tb_role;
delete from tb_url;
--清空邮件模板
delete from tb_template;


--初始化后台功能菜单数据
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('1','main-treenode','0','嗨啰后台中心','0','/1','嗨啰后台中心','1','maingrid','/system/main/*');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('3','tag-treenode','0','服务标签','1','/1','remark','1','taghbox','/system/tag/queryind');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('4','hotkeywordform-treenode','1','热门关键字','1','/2','remark','2','hotkeywordform','/system/k/kget');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('5','sensitiveCharacterForm-treenode','1','敏感字符','1','/3','remark','3','sensitiveCharacterForm','/system/sensitiveWords/get');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('6','useraccount-treenode','0','用户帐户','1','/4','remark','4','userhbox','/system/user');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('7','userreport-treenode','1','用户建议','1','/5','remark','5','feedbackpanel','/system/suggestion');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('8','commentreply-treenode','0','评价和回复管理','1','/6','remark','6','commentreplyBox','/system/comment/queryComment');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('10','config-treenode','0','配置管理','1','/8','remark','8','configgrid','/system/config/listall');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('11','introimg-treenode','1','移动端首页轮播图','1','/9','remark','9','introimggrid','/system/introimg/list');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('12','mobileloggrid-treenode','1','移动端客户端日志','1','/10','remark','10','mobileloggrid','/system/mobileclientlog/list');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('13','log-treenode','0','系统日志','1','/11','remark3','11','sysLoggrid','/system/sysLog/list');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('14','resume-treenode','0','海外简历','1','/12','remark','12','resumehbox','/system/resume/list');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('15','menu-treenode','0','菜单功能管理','1','/13','remark','13','menugrid','/system/url_tree');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('16','role-treenode','0','后台角色管理','1','/14','remark','14','rolegrid','/system/get_role');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('17','admin-treenode','0','后台用户管理','1','/15','remark','15','admingrid','/system/get_admin');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('19','','1','授权','17',NULL,'','2','','/system/auth_user');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('20','','1','授权查询','17',NULL,'ss','3','','/system/get_role_by_user');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('21','','1','删除','17',NULL,'','4','','/system/del_admin');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('22','','1','新增','17',NULL,'','5','','/system/add_admin');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('24','','1','授权查询','16',NULL,'','2','','/system/url_tree_role');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('25','','1','删除','16',NULL,'del','3','','/system/del_role');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('26','','1','授权','16',NULL,'','4','','/system/auth_role');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('27','','1','新增','16',NULL,'','5','','/system/add_role');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('28','','1','修改','15',NULL,'','1','','/system/update_menu');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('29','','1','删除','15',NULL,'','2','','/system/del_menu');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('30','','1','新增','15',NULL,'','3','','/system/add_menu');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('31','','1','详细查看','15',NULL,'','4','','/system/get_menu_by_id');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('32','','1','子菜单查看','15',NULL,'','5','','/system/get_child');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('36','','1','新增','10',NULL,'','1','','/system/config/create');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('37','','1','保存','10',NULL,'','2','','/system/config/update');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('38','','1','删除','10',NULL,'','3','','/system/config/destroy');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('39','','1','同步','10',NULL,'','4','','/system/config/syn');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('40','','1','新增','3',NULL,'','1','','/system/tag/saveOrUpdate');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('41','','1','删除','3',NULL,'','2','','/system/tag/delete/*');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('42','','1','查询标签','3',NULL,'','3','','/system/tag/querycate');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('43','template-treenode','0','邮件模板管理','1',NULL,'邮件模板管理','16','templategrid','/system/get_template');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('44','','1','标签详情','3',NULL,'','4','','/system/tag/get/*');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('45','','1','编辑状态','6',NULL,'编辑状态','1','','/system/userState');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('46','','1','excel导出','6',NULL,'excel导出','2','','/system/downloadExcel');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('47','','1','查询行业','6',NULL,'查询行业','3','','system/tag/queryind');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('48','','1','取消置顶','6',NULL,'取消置顶','4','','/system/user/untop');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('49','','1','置顶','6',NULL,'置顶','5','','/system/user/top');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('50','','1','日志下载','13',NULL,'日志下载','1','','/system/sysLog/download/*');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('51','','1','国籍下拉列表查询','6',NULL,'国籍下拉列表查询','6','','/system/userCountry');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('52','','1','发布海外简历','6',NULL,'发布海外简历','7','','/system/resume/publishTask');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('53','configkeywordgrid','0','关键字统计配置','1',NULL,'关键字统计配置','17','configkeywordgrid','/system/config_key_word');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('54','','1','保存','53',NULL,'保存','1','','/system/saveKeyWordConfig');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('56','keyword-treenode','0','搜索关键词','1',NULL,'搜索关键词','18','keywordgrid','/system/keyword');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('57','','1','excel导出','56',NULL,'excel导出','1','','/system/keyWordExcel');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('58','','1','保存模板','43',NULL,'保存模板','1','','/system/save_template');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('59','','1','模板变量查询','43',NULL,'模板变量查询','2','','/system/get_variable');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('60','','1','废弃','14',NULL,'废弃','1','','/system/resume/toInvalid');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('61','','1','翻译完毕','14',NULL,'翻译完毕','2','','/system/resume/toCompleted');
insert into `tb_url` (`id`, `ext_id`, `leaf`, `name`, `parent_id`, `path`, `remark`, `sort`, `tabxtype`, `url`) values('62','','1','回复查询','8',NULL,'回复查询','1','','/system/comment/queryReply');

--初始化后台角色
insert into `tb_role` (`id`, `name`, `remark`) values('1','SUPER_ADMIN','超级管理员');

--初始化后台角色和功能关系
insert into r_role_url(role_id,url_id)(select 1,id from tb_url);

--初始化admin和角色的关系
insert into `r_admin_role` (`role_id`, `admin_id`)(select 1,id from tb_admin where userName='admin');


--初始化注册成功后通知邮件模板
insert into `tb_template` (`id`, `context`, `name`, `type`, `remark`) values('1','<font face=\"tahoma, arial, verdana, sans-serif\" size=\"2\" style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\">尊敬的@userName</font><div><font face=\"tahoma, arial, verdana, sans-serif\" size=\"2\" style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\"><span style=\"white-space:pre\">	</span></font><font face=\"tahoma, arial, verdana, sans-serif\" size=\"2\">欢迎用户注册本网站账号，稍后我们在线客服会与您取得联系。</font><br><div style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\"><font face=\"tahoma, arial, verdana, sans-serif\" size=\"2\"><span style=\"white-space:pre\">	</span><a href=\"http://www.helome.com\">http://www.helome.com</a></font></div><div style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\"><br></div><div style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\">此致<br></div><div style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\"><br></div><div style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\"><span style=\"white-space:pre\">							</span>helome团队敬上</div><div style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\"><br></div><div style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\"><br></div><div style=\"color: rgb(0, 0, 0); font-family: tahoma, arial, verdana, sans-serif; font-size: 12px;\">请注意，该电子邮件地址不接受回复邮件<br></div></div>','register_success_notify','2','用户注册成功系统通知');

commit;