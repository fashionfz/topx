INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9114081@test.com',
		0,
		1,
		'2012-10-17 23:53:17',
		'田伟'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'30',
	'0',
	'0',
	'/assets/misc/images/head/1.jpg',
	'医学专家',
	'2012-10-17',
	'1959年生,1983年毕业于北京医学院医疗系（北京大学医学部）。1994年获日本国立弘前大学医学博士学位。1997年在北京积水潭医院创建脊柱外科。2001年赴加拿大多伦多大学、约克大学研修行政管理。田伟同时还担任全国青联常委；中国青年科技工作者协会副会长；中华医学会骨科分会常委、秘书长；中华医学会创伤分会委员；全国残联康复学会损伤委员会副会长。北京积水潭医院院长、脊柱外科主任、北京大学教授、博士生导师、北京市创伤骨科研究所所长、美国骨科学会（AAOS）会员、国务院特殊津贴专家,中央保健委员会会诊专家,全国青年科学家协会副会长,北京市九届政协委员。田伟曾被评为北京市十大杰出青年、北京市优秀共产党员、全国抗SARS工作先进个人,2003年荣获全国优秀院长称号。',
	'0.99',
	'0.01',
	'0',
	'["脊柱微创手术","腰椎管狭窄","腰椎间盘突出","骨科学","外伤骨折","韧带骨化症"]',
	'田伟',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'renyouchao@gmail.com',
		0,
		1,
		'2013-10-17 23:53:17',
		'曹军'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'10',
	'3',
	'0',
	'/assets/misc/images/head/2.jpg',
	'医学专家',
	'2013-10-17',
	'自80年代初即从事肾小球疾病免疫发病机理的研究,在肾脏病实验室建立了免疫学、细胞生物学及分子生物学等实验方法。在国内首先发表了IgA肾病的研究 论文,在继发性肾病如狼疮性肾炎、干燥综合征肾损害等方面也做了大量研究工作,先后两次获得院科研成果二等奖,并於1987年获全国第二届中青年科研论文 比赛二等奖。有关脂代谢异常对肾脏的影响、他汀类YAO物的肾保护作用、雷公藤免疫抑制机制的研究及狼疮性肾炎的临床研究多次获院一、二、三等科研奖。目前从事的研究工作 主要包括脂代谢异常对肾脏的损害机理；他汀类降脂YAO物非降脂途径的肾脏保护作用,IgA肾病的研究,狼疮性肾炎的研究,糖尿病肾病的研究及血液滤过的研究 等。其中脂代谢与肾脏病进展的研究处於国内领先水平。获得国家自然科学基金、卫生部科学研究基金、科技部科学研究基金、教育部博士点基金、北京协和医院重 点研究等多个项目资助。已发表科研论文130余篇,论著72篇,病例报告及临床病理分析33篇,讲座及综述等29篇。参与10余部专著的写作。国外发表论 文及会议摘要近30篇。',
	'1',
	'0',
	'0',
	'["肾小球疾病","肾血管疾病及急","慢性肾衰竭","肾炎","肾功能衰竭","血液净化"]',
	'曹军',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115082@test.com',
		0,
		1,
		'2012-11-02 7:53:17',
		'Juan Pablo Lorandi'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'加拿大',
	'20',
	'10',
	'0',
	'/assets/misc/images/head/3.jpg',
	'Java Developer',
	'2007-10-18',
	'Juan is an experienced web software developer with over ten years of experience in Java and SQL. He enjoys frequently tinkering with code and gadgets, and doesn\'t pass up any chance to competitively test his computer science or electronics skills.',
	'0.98',
	'0.02',
	'0',
	'["JavaScript","Java","SQL","Spring","JAXB","OpenGL"]',
	'Juan Pablo Lorandi',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115083@test.com',
		0,
		1,
		'2012-11-01 8:53:17',
		'Pablo Lalloni'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'加拿大',
	'25',
	'5',
	'0',
	'/assets/misc/images/head/4.png',
	'Hadoop Developer',
	'2008-10-18',
	'Pablo is an architect & developer with extensive experience in a wide range of techniques and technologies, a strong ability to understand & solve problems efficiently, keeping in mind the big picture, & achieving very high quality consistently. Has succesfully lead several projects of small teams.',
	'0.97',
	'0.02',
	'0.01',
	'["JavaScript","Java SE","Hadoop","Spring","Hibernate","Ext JS"]',
	'Pablo Lalloni',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115084@test.com',
		0,
		1,
		'2011-11-01 8:53:17',
		'Steven S. Morgan'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'美国',
	'40',
	'15',
	'0',
	'/assets/misc/images/head/5.png',
	'Java Developer',
	'2007-8-18',
	'Steven is an expert Java architect and developer with extensive experience in distributed architectures, scalable solutions, and flexible and maintainable designs. He is a team player with a knack for interacting well with diversity. He leads via his expertise and by the example he sets.',
	'0.99',
	'0.01',
	'0.00',
	'["SCXML","Java","Log4J","JDBC ","JavaBeans","Web Sockets","Apache Ant","Design Patterns","Multithreading"]',
	'Steven S. Morgan',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115085@test.com',
		1,
		1,
		'2010-11-01 10:53:17',
		'Anna Chiara Bellini'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'美国',
	'15',
	'2',
	'1',
	'/assets/misc/images/head/6.jpg',
	'Java Developer',
	'2009-8-18',
	'When Anna was a kid, her brother got a Commodore 64 for Christmas. He played videogames, and she started coding. Since then, her career has spanned many different projects and technologies. But regardless of the task at hand, she always brings the same enthusiasm and passion.',
	'0.97',
	'0.02',
	'0.01',
	'["JavaScript","Java","Json","Spring MVC","SQL","Taglibs","Eclipse"]',
	'Anna Chiara Bellini',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115086@test.com',
		0,
		1,
		'2010-1-01 10:00:00',
		'Mikhail Selivanov'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'日本',
	'20',
	'6',
	'0',
	'/assets/misc/images/head/7.png',
	'Scala Developer',
	'2010-1-1',
	'Mikhail has extensive experience working as a back-end programmer and has completed numerous successful projects. He has been responsible for every part of the development process, including the implementation of business logic, performance tuning, writing deployment scripts, and more.',
	'0.96',
	'0.02',
	'0.02',
	'["Scala","Circumflex","Guava","jQuery","YourKit","Taglibs","Linux"]',
	'Mikhail Selivanov',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115087@test.com',
		0,
		1,
		'2008-8-01 10:00:00',
		'白鸦'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'28',
	'5',
	'0',
	'/assets/misc/images/head/8.jpg',
	'互联网设计师',
	'2010-2-2',
	'白鸦,真名朱宁,男。UCDChina 发起人,五季咨询成员,UPA中国团队成员。专注于用户体验的产品设计顾问、博客。支付宝首席用户体验规划师；UCDChina发起人,五季咨询合伙人,UPA（可用性专业协会）中国团队成员；专注于用户体验的互联网产品设计师；Blogger。',
	'0.97',
	'0.01',
	'0.02',
	'["HTML","CSS","HTML5","jQuery","JavaScript"]',
	'白鸦',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115088@test.com',
		0,
		1,
		'2009-1-01 10:00:00',
		'冯大辉'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'32',
	'8',
	'0',
	'/assets/misc/images/head/9.jpg',
	'Oracle 专家',
	'2009-10-2',
	'冯大辉,东北人,著名Oracle专家,知名博主(Blogger)。大学的专业是生物技术,爱好计算机,毕业后从事计算机行业。曾就职于阿里巴巴集团旗下支付宝（中国）网络技术有限公司,任职数据库架构师。2010年6月10日正式离职,加盟丁香园网站出任CTO。',
	'0.98',
	'0.01',
	'0.01',
	'["Oracle","SQL","数据库","MySQL","Linux", "Web Security"]',
	'冯大辉',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115089@test.com',
		0,
		1,
		'2009-6-01 10:00:00',
		'龚蔚'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'60',
	'20',
	'0',
	'/assets/misc/images/head/10.jpg',
	'网络安全专家',
	'2008-10-10',
	'龚蔚（Goodwell）中国黑客教父,绿色兵团创始人,COG发起人。1999年,龚蔚率领黑客组织“绿色兵团”成立上海绿盟信息技术公司。计算机信息管理专业本科,注册审计师、CISP 认证讲师、ISO27001 审核员、CCIE 安全、CCNP',
	'0.99',
	'0.01',
	'0',
	'["Web","信息安全","服务器安全","linux","window", "jsp","php", "asp"]',
	'龚蔚',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115010@test.com',
		0,
		1,
		'2011-9-01 10:00:00',
		'雷军'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'15',
	'1',
	'0',
	'/assets/misc/images/head/11.jpg',
	'计算机专家',
	'2010-12-10',
	'雷军,1969年12月16日出生,湖北仙桃人,中国大陆著名天使投资人,小米科技创始人、董事长兼首席执行官,多玩游戏网董事长,金山软件公司董事长。2012年12月,荣获中国经济年度人物新锐奖。',
	'0.90',
	'0.03',
	'0.07',
	'["安卓","杀毒软件","逆向分析","安全监测"]',
	'雷军',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115011@test.com',
		0,
		1,
		'2012-10-01 10:00:00',
		'方滨兴'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'2',
	'0',
	'0',
	'/assets/misc/images/head/12.jpg',
	'网络封锁专家',
	'2012-12-10',
	'方滨兴,男,中共党员,中国工程院院士,籍贯江西省万年县,汉族,1960年7月出生于黑龙江省哈尔滨市。曾任北京邮电大学校长,国家计算机网络与信息安全管理中心名誉主任。2013年6月,方滨兴称因身体原因,不再连任北邮校长职务。',
	'0.10',
	'0.50',
	'0.40',
	'["信息安全","网络封锁","监控"]',
	'方滨兴',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115012@test.com',
		0,
		1,
		'2011-10-01 10:00:00',
		'王献冰'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'40',
	'5',
	'0',
	'/assets/misc/images/head/13.jpg',
	'网络安全专家',
	'2012-10-10',
	'曾开发出很多的优秀软件,如WinShell、PassDump、SecWiper等,还有早期的系统安全漏洞测试工具IPHacker等优秀软件。现为世界著名的网络安全公司中国分公司高级安全顾问。',
	'0.99',
	'0.01',
	'0.00',
	'["防火墙","扫描器","入侵检测", "虚拟私有网", "公共密钥", "身份认证"]',
	'王献冰',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115013@test.com',
		0,
		1,
		'2008-10-01 12:00:00',
		'谢云章'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'40',
	'10',
	'0',
	'/assets/misc/images/head/14.jpg',
	'中国管理科学研究院研究员',
	'2008-10-01',
	'曾在国家级、省级学术理论刊物发表文45篇,曾主编（含副主编）并公开出版书籍9本,曾在若干媒体发表各类文章300篇。',
	'0.99',
	'0.01',
	'0.00',
	'["管理科学"]',
	'谢云章',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115014@test.com',
		1,
		1,
		'2009-10-01 12:00:00',
		'关颖'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'30',
	'10',
	'1',
	'/assets/misc/images/head/15.jpg',
	'天津社会科学院社会学研究所研究员',
	'2009-10-01',
	'主要学术职务：中国社会学会理事、中国社会学会家庭社会学专业委员会秘书长、中国青少年研究会常务理事、天津市家庭教育研究会副会长等。主要研究领域是家庭社会学、教育社会学，以家庭教育和青少年问题研究见长。学术代表作《社会学视野中的家庭教育》、《城市未成年人犯罪与家庭》，主编多部学术著作，撰写多部家庭教育普及读物，发表论文数百篇。',
	'0.99',
	'0.01',
	'0.00',
	'["社会学","家庭社会学","青少年研究会","家庭教育","教育社会学"]',
	'谢云章',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115015@test.com',
		0,
		1,
		'2010-09-01 12:10:00',
		'刘荣跃'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'30',
	'10',
	'0',
	'/assets/misc/images/head/16.jpg',
	'中国翻译协会专家会员',
	'2009-09-01',
	'中国翻译协会专家会员，中国作家协会会员，四川省翻译文学学会理事，四川省作家协会主席团委员。1998年曾被评为“四川省青年自学成才先进个人”。\n    以“翻译经典名著，译介研究欧文，写作散文随笔”为宗旨。现已翻译出版个人译著二十五部，主编三十部（套），各近五百万字。数次再版的代表译著有《见闻札记》（华盛顿•欧文）、《无名的裘德》（托马斯•哈代）和《野性的呼唤》(杰克•伦敦)。《无名的裘德》获四川省第五屇“四川文学奖”。《征服格拉纳达》获四川省翻译工作者协会2010年度“四川省翻译学术成果论文一等奖”。另创作、发表散文随笔若干。',
	'0.99',
	'0.01',
	'0.00',
	'["翻译","翻译文学","作家","欧文"]',
	'刘荣跃',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115016@test.com',
		1,
		1,
		'2010-09-01 12:10:00',
		'张怡筠'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'30',
	'10',
	'1',
	'/assets/misc/images/head/17.jpg',
	'著名心理学者，情商研究专家',
	'2009-09-01',
	'著名心理学者，情商研究专家',
	'0.97',
	'0.03',
	'0.00',
	'["心理","情商"]',
	'张怡筠',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115017@test.com',
		0,
		1,
		'2010-06-01 12:10:00',
		'孙云晓'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'30',
	'10',
	'0',
	'/assets/misc/images/head/18.jpg',
	'中国青少年研究中心副主任、研究员孙云晓',
	'2010-06-01',
	'中国青少年研究中心研究员、副主任，中国科普作家协会副理事长，北京市家庭教育研究会副会长。作品有《夏令营中的较量》、《习惯决定孩子一生》、《孙云晓教育作品集》8卷等。',
	'0.97',
	'0.03',
	'0.00',
	'["青少年研究","科普作家","家庭教育"]',
	'孙云晓',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115018@test.com',
		1,
		1,
		'2010-06-01 12:10:00',
		'乔磊'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'30',
	'10',
	'1',
	'/assets/misc/images/head/19.jpg',
	'美国侨报副总编辑',
	'2010-06-01',
	'乔老爷，正名乔磊，美国侨报副总编辑，涉猎广泛、文章实用价值大。新浪博客十大名博、教育博客最受欢迎博主。著有《贫富游戏》、《星条旗下美国梦》、《中国妈妈启示录》、《美国教育地平线》等专著。',
	'0.96',
	'0.03',
	'0.01',
	'["乔老爷","美国侨报副总编辑","教育专家"]',
	'乔磊',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115019@test.com',
		0,
		1,
		'2010-06-01 12:10:00',
		'张化桥'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'30',
	'10',
	'1',
	'/assets/misc/images/head/20.jpg',
	'慢牛投资公司董事长',
	'2010-06-01',
	'慢牛投资公司董事长。业务:（1）上市公司重组，并购，后门上市，融资，投资者关系（2）定向增发投资',
	'0.96',
	'0.03',
	'0.01',
	'["公司重组","公司并购","后门上市","融资","投资者关系","定向增发投资"]',
	'张化桥',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115020@test.com',
		0,
		1,
		'2010-06-01 12:10:00',
		'鲍金勇'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'5',
	'10',
	'1',
	'/assets/misc/images/head/21.jpg',
	'职业规划师，华南农业大学辅导员，《原来大学可以这样读》作者',
	'2010-06-01',
	'· 华南农业大学食品学院团委书记，研究生辅导员，职业指导师、GCDF全球职业规划师。· 从事大学生就业指导和服务工作多年,精通职业生涯规划和求职指导。',
	'0.96',
	'0.03',
	'0.01',
	'["职业指导师","职业规划师","大学生就业指导","职业生涯规划","求职指导"]',
	'鲍金勇',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115021@test.com',
		1,
		1,
		'2010-06-01 12:10:00',
		'马立'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'24',
	'11',
	'1',
	'/assets/misc/images/head/22.jpg',
	'著名教育专家',
	'2010-06-01',
	'历任教育部基础教育司副外长、处长；师范司司长等职。现为全国教师教育学会会长、北京五洲之星儿童文化艺术院教育顾问。',
	'0.96',
	'0.03',
	'0.01',
	'["基础教育","教师教育","教育顾问"]',
	'马立',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115022@test.com',
		1,
		1,
		'2010-06-01 12:10:00',
		'高琛'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'24',
	'11',
	'1',
	'/assets/misc/images/head/23.jpg',
	'东北育才教育集团党委书记兼校长',
	'2010-06-01',
	'高琛，女，1963年生人，上个世纪80年代毕业于沈阳师范学院（现沈阳师范大学）中文系，获文学学士。中学高级教师，研究生学历，教育管理硕士学位，2001年任辽宁省​示范高中沈阳市第83中学校长，2005年任东北育才学校正校级常务副校长，协助校长主持日常全面工作，2008年任东北育才教育集团党委书记兼校长。',
	'1',
	'0',
	'0',
	'["文学学士","中学高级教师","教育管理"]',
	'高琛',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115023@test.com',
		1,
		1,
		'2010-06-01 12:10:00',
		'王霆'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'24',
	'11',
	'1',
	'/assets/misc/images/head/24.jpg',
	'资深教育专家',
	'2010-06-01',
	'王霆，女，教育心理学学士，教育管理硕士。资深教育专家，曾于教育部门主持教研工作，深入教育行业超过二十年',
	'1',
	'0',
	'0',
	'["教育心理学","教育管理"]',
	'王霆',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/
INSERT INTO `tb_user` (
	email,
	gender,
	isEnable,
	registerDate,
	userName
)
VALUES
	(
		'9115024@test.com',
		0,
		1,
		'2010-06-01 12:10:00',
		'李阳'
	);

INSERT INTO `tb_expert` (
	`country`,
	`dealNum`,
	`expenses`,
	`gender`,
	`headUrl`,
	`job`,
	`joinDate`,
	`personalInfo`,
	`goodComment`,
	`averageComment`,
	`poorComment`,
	`skillsTags`,
	`userName`,
	`userid`
) SELECT
	'中国',
	'33',
	'15',
	'1',
	'/assets/misc/images/head/25.jpg',
	'疯狂英语创始人',
	'2010-06-01',
	'李阳，疯狂英语创始人，全球著名英语口语教育专家，英语成功学励志导师，中国教育慈善家，全国新青年十大新锐人物，全国五百多所中学的名誉校长和英语顾问，北京奥运会志愿者英语口语培训总教练。',
	'0.99',
	'0',
	'0.01',
	'["疯狂英语创始人","英语口语教育","英语成功学","教育慈善"]',
	'李阳',
	MAX(id)
FROM
	`tb_user`;

/*--------------------------------------------------------------------------------*/

update tb_expert e set e.serveState = e.id%2;
