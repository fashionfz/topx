/**
 * @description: 聊天消息本地存储模块
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
define('module/msgLS', [
    'module/localStorage'
], function(ls){
    var lsKey = 'lsMsg';
    return {
        /**
         * 保存单条聊天数据
         */
        save: function(json){
            var lsMsg = this.readAll(),
                list = json.groupId ? lsMsg[json.senderId + 'ToG' + json.groupId] : lsMsg[json.senderId + 'To' + json.receiverId],
                newArr = [],
                isInsert = true;

            if(list){
                newArr = list;
                newArr.sort(function(a, b){
                    return a.sendTime > b.sendTime;
                });
                if(newArr.length > 299){
                    newArr.shift();
                }
                $.each(list, function(i, n){
                    if(json.messageId && n.messageId == json.messageId){
                        isInsert = false;
                        return false;
                    }
                });
            }
            if(isInsert){
                newArr.push(json);
                if(json.groupId){
                    lsMsg[json.senderId + 'ToG' + json.groupId] = newArr;
                }else{
                    lsMsg[json.senderId + 'To' + json.receiverId] = newArr;
                }
                ls.set(lsKey, JSON.stringify(lsMsg));
            }
        },
        /**
         * 保存多条聊天数据
         */
        saveList: function(arrList){
            var lsMsg = this.readAll(),
                list = null;
            if(arrList){
                $.each(arrList, function(i, n){
                    var newArr = [],
                        isInsert = true;
                    list = n.groupId ? lsMsg[n.senderId + 'ToG' + n.groupId] : lsMsg[n.senderId + 'To' + n.receiverId];
                    if(list){
                        newArr = list;
                        newArr.sort(function(a, b){
                            return a.sendTime > b.sendTime;
                        });
                        $.each(list, function(x, y){
                            if(n.messageId && y.messageId == n.messageId){
                                isInsert = false;
                                return false;
                            }
                        });
                    }
                    if(isInsert){
                        newArr.push(n);
                        if(n.groupId){
                            lsMsg[n.senderId + 'ToG' + n.groupId] = newArr;
                        }else{
                            lsMsg[n.senderId + 'To' + n.receiverId] = newArr;
                        }
                    }
                });
                ls.set(lsKey, JSON.stringify(lsMsg));
            }
        },
        /**
         * 根据id获取聊天数据
         */
        read: function(fid, tid){
            var self = this,
                lsToKey = fid + 'To' + (group ? 'G':'') + tid,
                lsFromKey = tid + 'To' + (group ? 'G':'') + fid,
                lsValue = ls.get(lsKey),
                returnData = null;

            try{
                returnData = $.parseJSON(lsValue);
                return $.merge(returnData[lsToKey]||[], returnData[lsFromKey]||[]);
            }catch (e){
                return returnData;
            }
        },
        /**
         * 根据群组id获取群聊天数据
         */
        readGroup: function(gid){
            var self = this,
                lsValue = ls.get(lsKey),
                returnData = [];

            try{
                var lsData = $.parseJSON(lsValue);
                $.each(lsData, function(i, n){
                    if(i.match(new RegExp('G' + gid + '$'))){
                        returnData = $.merge(returnData, n || []);
                    }
                });
                return returnData;
            }catch (e){
                return returnData;
            }
        },
        /**
         * 获取所有聊天数据
         */
        readAll: function(){
            var list = ls.get(lsKey) || '';

            if($.type(list) != 'object'){
                list = $.parseJSON(list);
            }

            return list || {};
        },
        /**
         * 根据id编辑聊天数据
         */
        edit : function(mid, call){
            var lsMsg = this.readAll();
            $.each(lsMsg, function(i, n){
                $.each(n, function(x, y){
                    if(y.messageId == mid){
                        if(call){
                            lsMsg[i][x] = call(y);
                            ls.set(lsKey, JSON.stringify(lsMsg));
                        }
                        return false;
                    }
                })
            });
        },
        /**
         * 根据群组id删除群聊天数据
         */
        removeGroup : function(gid){
            var lsMsg = this.readAll(),
                newMsg = {};
            $.each(lsMsg, function(i, n){
                if(!i.match(new RegExp('G' + gid + '$'))){
                    newMsg[i] = n;
                }
            });
            ls.set(lsKey, JSON.stringify(newMsg));
        },
        /**
         * 格式化聊天数据
         */
        formatChat : function(){
            var lsMsg = this.readAll(),
                newMsg = {};
            $.each(lsMsg, function(i, n){
                var newArr = [],
                    oldMsgId = null,
                    oldTime = null;
                if(n && n.length){
                    $.each(n, function(x, y){
                        if(oldMsgId != y.messageId && oldTime != (y.sendTime || y.dateTime)){
                            newArr.push(y);
                        }
                        oldMsgId = y.messageId;
                        oldTime = y.sendTime || y.dateTime;
                    });
                }
                newMsg[i] = newArr;
            });
            ls.set(lsKey, JSON.stringify(newMsg));
        }
    }
});