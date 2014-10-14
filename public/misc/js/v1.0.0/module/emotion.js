/**
 * @description: 表情模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/emotion', ['common/util'], function(util){

    return {
        /**
         * 获取表情数据
         */
        getEmotionData : function(){
            return [
                {reg: 'wx', title: '微笑'},
                {reg: 'pz', title: '撇嘴'},
                {reg: 'se', title: '色'},
                {reg: 'fd', title: '发呆'},
                {reg: 'dy', title: '得意'},
                {reg: 'll', title: '流泪'},
                {reg: 'hx', title: '害羞'},
                {reg: 'bz', title: '闭嘴'},
                {reg: 'shui', title: '睡'},
                {reg: 'dk', title: '大哭'},
                {reg: 'gg', title: '尴尬'},
                {reg: 'fn', title: '发怒'},
                {reg: 'tp', title: '调皮'},
                {reg: 'cy', title: '呲牙'},
                {reg: 'jy', title: '惊讶'},
                {reg: 'ng', title: '难过'},
                {reg: 'kuk', title: '酷'},
                {reg: 'lengh', title: '冷汗'},
                {reg: 'zk', title: '抓狂'},
                {reg: 'tuu', title: '吐'},
                {reg: 'tx', title: '偷笑'},
                {reg: 'ka', title: '可爱'},
                {reg: 'baiy', title: '白眼'},
                {reg: 'am', title: '傲慢'},
                {reg: 'jie', title: '饥饿'},
                {reg: 'kun', title: '困'},
                {reg: 'jk', title: '惊恐'},
                {reg: 'lh', title: '流汗'},
                {reg: 'hanx', title: '憨笑'},
                {reg: 'db', title: '大兵'},
                {reg: 'fendou', title: '奋斗'},
                {reg: 'zhm', title: '咒骂'},
                {reg: 'yiw', title: '疑问'},
                {reg: 'xu', title: '嘘...'},
                {reg: 'yun', title: '晕'},
                {reg: 'zhem', title: '折磨'},
                {reg: 'shuai', title: '衰'},
                {reg: 'kl', title: '骷髅'},
                {reg: 'qiao', title: '敲打'},
                {reg: 'zj', title: '再见'},
                {reg: 'ch', title: '擦汗'},
                {reg: 'kb', title: '抠鼻'},
                {reg: 'gz', title: '鼓掌'},
                {reg: 'qd', title: '糗大了'},
                {reg: 'huaix', title: '坏笑'},
                {reg: 'zhh', title: '左哼哼'},
                {reg: 'yhh', title: '右哼哼'},
                {reg: 'hq', title: '哈欠'},
                {reg: 'bs', title: '鄙视'},
                {reg: 'wq', title: '委屈'},
                {reg: 'kk', title: '快哭了'},
                {reg: 'yx', title: '阴险'},
                {reg: 'qq', title: '亲亲'},
                {reg: 'xia', title: '吓'},
                {reg: 'kel', title: '可怜'},
                {reg: 'cd', title: '菜刀'},
                {reg: 'xig', title: '西瓜'},
                {reg: 'pj', title: '啤酒'},
                {reg: 'lq', title: '篮球'},
                {reg: 'pp', title: '乒乓'},
                {reg: 'kf', title: '咖啡'},
                {reg: 'fan', title: '饭'},
                {reg: 'zt', title: '猪头'},
                {reg: 'mg', title: '玫瑰'},
                {reg: 'dx', title: '凋谢'},
                {reg: 'sa', title: '示爱'},
                {reg: 'xin', title: '心'},
                {reg: 'xs', title: '心碎'},
                {reg: 'dg', title: '蛋糕'},
                {reg: 'shd', title: '闪电'},
                {reg: 'zhd', title: '炸弹'},
                {reg: 'dao', title: '刀'},
                {reg: 'zq', title: '足球'},
                {reg: 'pch', title: '爬虫'},
                {reg: 'bb', title: '便便'},
                {reg: 'yl', title: '月亮'},
                {reg: 'ty', title: '太阳'},
                {reg: 'lw', title: '礼物'},
                {reg: 'yb', title: '拥抱'},
                {reg: 'qiang', title: '强'},
                {reg: 'ruo', title: '弱'},
                {reg: 'ws', title: '握手'},
                {reg: 'shl', title: '胜利'},
                {reg: 'bq', title: '抱拳'},
                {reg: 'gy', title: '勾引'},
                {reg: 'qt', title: '拳头'},
                {reg: 'cj', title: '差劲'},
                {reg: 'aini', title: '爱你'},
                {reg: 'bu', title: 'NO'},
                {reg: 'hd', title: 'OK'},
                {reg: 'aiq', title: '爱情'},
                {reg: 'fw', title: '飞吻'},
                {reg: 'tiao', title: '跳跳'},
                {reg: 'fad', title: '发抖'},
                {reg: 'oh', title: '怄火'},
                {reg: 'zhq', title: '转圈'},
                {reg: 'kt', title: '磕头'},
                {reg: 'ht', title: '回头'},
                {reg: 'tsh', title: '跳绳'},
                {reg: 'hsh', title: '挥手'},
                {reg: 'jd', title: '激动'},
                {reg: 'jw', title: '街舞'},
                {reg: 'xw', title: '献吻'},
                {reg: 'youtj', title: '右太极'},
                {reg: 'zuotj', title: '左太极'}
            ]
        },
        /**
         * 渲染表情图片
         */
        renderEmotion : function(tpl, callback){
            var self = this,
                emotionList = [];
            $.each(self.getEmotionData(), function(i, n){
                n.src = ued_conf.root + 'images/emotion/' + n.reg + '.gif';
                emotionList.push(util.template(tpl, n));
            });
            callback && callback(emotionList);
        },
        /**
         * 替换字符串中含有特定的表情符号位表情图片
         */
        replaceEmotion : function(msg){
            msg = msg || '';
            var self = this,
                reg = /\[\![a-z]+\]/g,
                regAZ = /[a-z]/g,
                regBr = /\n/g,
                emotionPath = ued_conf.root + 'images/emotion/',
                tagList = msg.match(reg) || [],
                emotionImg = '',
                emotionTitle = '';

            msg = msg.replace(regBr, '<br>');
            msg = msg.replace(/\s/g, '&nbsp;');
            for(var i = 0; i < tagList.length; i++){
                emotionImg = tagList[i].match(regAZ).join('') || '';
                $.each(self.getEmotionData(), function(i, n){
                    if(n.reg == emotionImg){
                        emotionTitle = n.title;
                        return false;
                    }
                });
                msg = msg.replace(tagList[i], '<img class="emotion" src="' + emotionPath + emotionImg +'.gif" title="'+ emotionTitle +'">');
            }
            return self.replaceInviteMsg(msg);
        },
        /**
         * 替换消息中音视频操作相关消息
         */
        replaceInviteMsg : function(m){
            var self = this;
            m = m.replace('[/audio_invite_mc]', '发起语音通话邀请');
            m = m.replace('[/video_invite_mc]', '发起视频通话邀请');
            m = m.replace('[/audio_invite_timeout_mc]', '发起的语音通话请求超时');
            m = m.replace('[/video_invite_timeout_mc]', '发起的视频通话请求超时');
            m = m.replace('[/av_hangup_mc]', '已挂断通话');
            return m;
        }
    }
});