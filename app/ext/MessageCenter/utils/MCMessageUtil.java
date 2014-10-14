package ext.MessageCenter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import models.User;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import play.Logger;
import play.Logger.ALogger;
import play.cache.Cache;
import ext.MessageCenter.Message.Message;
import ext.MessageCenter.Message.base.CacheBinaryMessage;
import ext.MessageCenter.Message.base.ReportMessage;
import ext.MessageCenter.Message.base.UserOnlineMessage;
import ext.MessageCenter.Message.business.MultimediaMessage;
import ext.MessageCenter.Message.business.PictureMessage;
import ext.MessageCenter.Message.business.RemindMessage;
import ext.MessageCenter.Message.group.AddGroupMember;
import ext.MessageCenter.Message.group.DeleteGroupMessage;
import ext.MessageCenter.Message.group.FutureMessage;
import ext.MessageCenter.Message.group.GroupCacheBinaryMessage;
import ext.MessageCenter.Message.group.GroupNewMessage;
import ext.MessageCenter.Message.group.GroupTxtMessage;
import ext.MessageCenter.Message.group.NoticeMessage;
import ext.MessageCenter.Message.group.ProfileNoticeMessage;
import ext.MessageCenter.Message.group.RemoveGroupMember;
import ext.MessageCenter.core.ChannelFactory;
import ext.MessageCenter.core.MessageFactory;

public class MCMessageUtil {
    private static ALogger LOGGER = Logger.of(MCMessageUtil.class);

    /**
     * @description发送文本消息
     * @param senderId
     * @param receiverId
     * @param senderName
     * @param receiverName
     * @param context
     */
    public static void pustTxtMessage(Long senderId, Long receiverId, String senderName, String receiverName,
            String context) {
        Message msg = MessageFactory.createTxtMessage(senderId, receiverId, context, senderName, receiverName);
        try {
            // ChannelFactory.getChannel().writeAndFlush(msg);
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push txt message:" + e);
        }
    }

    private static String sendHttpRequest(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);
        HttpParams httpParams = new BasicHttpParams();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        httpost.setParams(httpParams);
        String result = "";
        HttpResponse response = null;
        InputStream in = null;
        try {
            // 添加请求参数到请求对象
            httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            // 发送请求并等待响应
            response = httpclient.execute(httpost);
            // 如果状态码为200
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    entity = new BufferedHttpEntity(entity);
                    in = entity.getContent();
                    byte[] read = new byte[1024];
                    byte[] all = new byte[0];
                    int num;
                    while ((num = in.read(read)) > 0) {
                        byte[] temp = new byte[all.length + num];
                        System.arraycopy(all, 0, temp, 0, all.length);
                        System.arraycopy(read, 0, temp, all.length, num);
                        all = temp;
                    }
                    result = new String(all, "UTF-8");
                }
            } else {
                return "";
            }
        } catch (IOException e) {
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            httpost.abort();
        }
        return result;
    }

    // 判断是否在线
    public static boolean whetherOnline(Long userId) {
        String obj = Cache.get("MC.user.id" + String.valueOf(userId)) + "";
        if (obj.equals("1")) {
            return true;
        }
        return false;
    }

    // 判断是否聊天室
    public static boolean whetherInChat(Long sendId, Long receiveId) {
        String key = "inChat" + String.valueOf(receiveId) + "to" + String.valueOf(sendId);
        Long Num = (Long) Cache.get(key);

        if (Num != null && 1 == Num) {
            return true;
        }
        return false;
    }

    // 归零未读聊天数
    public static void resetCommunicateNum(Long sendId, Long receiveId) {
        String key = "communicateNum" + String.valueOf(sendId) + "to" + String.valueOf(receiveId);
        String totalKey = "communicateTotal" + String.valueOf(receiveId);

        Long currentNum = (Long) Cache.get(key);
        if (null != currentNum) {
            Long total = (Long) Cache.get(totalKey);
            total = total == null ? 0L : total;

            Long newTotal = total - currentNum;
            newTotal = newTotal < 0 ? 0L : newTotal;

            Cache.set(key, 0L);
            Cache.set(totalKey, newTotal);
        }
    }

    // 向双方发送系统消息
    public static void pustEachTxtMessage(Long senderId, Long receiverId, String senderName, String receiverName,
            String context) {
        Message toReceiveMsg = MessageFactory.createTxtMessage(0L, receiverId, context, "system", receiverName);
        Message toSenderMsg = MessageFactory.createTxtMessage(0L, senderId, context, "system", senderName);
        try {
            /*
             * ChannelFactory.getChannel().writeAndFlush(toReceiveMsg);
             * ChannelFactory.getChannel().writeAndFlush(toSenderMsg);
             */
            ChannelFactory.sendMessage(toReceiveMsg);
            ChannelFactory.sendMessage(toSenderMsg);
        } catch (Exception e) {
            LOGGER.error("push txt message:" + e);
        }
    }

    // 向单方发送系统消息
    public static void pustAloneTxtMessage(Long receiverId, String receiverName, String context) {
        Message systemMsg = MessageFactory.createTxtMessage(0L, receiverId, context, "system", receiverName);
        try {
            ChannelFactory.sendMessage(systemMsg);
            // ChannelFactory.getChannel().writeAndFlush(systemMsg);
        } catch (Exception e) {
            LOGGER.error("push txt message:" + e);
        }
    }

    public static void pushReportMessage(long id, String token) {
        Message reportMessage = new ReportMessage(id, token, (byte) 2);
        try {
            // ChannelFactory.getChannel().writeAndFlush(reportMessage);
            ChannelFactory.sendMessage(reportMessage);
        } catch (Exception e) {
            LOGGER.error("push Report  message:" + e);
        }
    }

    /**
     * @description 推送评论消息
     */
    public static void pushCommentMessage(Long senderId, Long receiverId, String senderName, String receiverName,
            String content) {
        Message commentMessage = MessageFactory.createCommentMessage(senderId, receiverId, senderName, receiverName,
                content, new Date().getTime());
        try {
            ChannelFactory.sendMessage(commentMessage);
        } catch (Exception e) {
            LOGGER.error("push comment  message:" + e);
        }
    }

    /**
     * @description 推送回复消息
     */
    public static void pushReplyMessage(Long senderId, Long receiverId, String senderName, String receiverName,
            String content) {
        Message replayMessage = MessageFactory.createReplyMessage(senderId, receiverId, new Date().getTime(),
                senderName, receiverName, content);
        try {
            ChannelFactory.sendMessage(replayMessage);
        } catch (Exception e) {
            LOGGER.error("push replay  message:" + e);
        }
    }

    public static void pushNormalOfflineMessage(User user) {
        Message msg = MessageFactory.createNormalOfflineMessage(user.id, user.getName());
        try {
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push Normal Offline  message:" + e);
        }
    }

    /**
     * @param senderId 消息发送者id
     * @param receiverId 消息接受者id
     * @param senderName 消息发送者名字
     * @param receiverName 消息接受者名字
     * @param amount 金额
     * @param currency 货币 1人民币,2美元
     * @description 转账消息
     */
    public static void pushTransferMessage(Long senderId, Long receiverId, String senderName, String receiverName,
            double amount, int currency) {
        if (senderName == null) {
            senderName = "";
        }
        if (receiverName == null) {
            receiverName = "";
        }
        Message msg = MessageFactory.createTransferMessage(senderId, receiverId, new Date().getTime(), senderName,
                receiverName, amount, (byte) currency);
        try {
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push Transfer  message:" + e);
        }
    }

    /**
     * @param groupId 群id
     * @param masterId 创建者id
     * @param groupName 群名称
     * @description 推送创建群消息
     */
    public static void pushCreateGroupMessage(Long groupId, Long masterId, String groupName) {
        if (groupName == null) {
            groupName = "";
        }
        GroupNewMessage msg = new GroupNewMessage(groupId, masterId, groupName);
        msg.setMessageId(UUID.randomUUID().toString());
        try {
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push Create Group message:" + e);
        }
    }

    /**
     * @param groupId 群id
     * @param memberId 成员id
     * @description 推送添加群成员消息
     */
    public static void pushAddGroupMemberMessage(Long groupId, Long memberId) {
        AddGroupMember msg = new AddGroupMember(groupId, memberId);
        msg.setMessageId(UUID.randomUUID().toString());
        try {
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push Create Add GroupMember message:" + e);
        }
    }

    /**
     * @param state 0-成功，1-参数错误，2-其它错误
     * @description (翻译)群管理相关的返回结果消息
     */
    public static void pushFutureMessage(int state) {
        FutureMessage msg = new FutureMessage((short) state);
        try {
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push Future message:" + e);
        }
    }

    /**
     * @param groupId 群组id
     * @description 删除群组消息
     */
    public static void pushDeleteGroupMessage(Long groupId) {
        DeleteGroupMessage msg = new DeleteGroupMessage(groupId);
        try {
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push Delete Group Message :" + e);
        }

    }

    /**
     * 
     * @param messageId 消息id
     * @param senderId 发送者id
     * @param senderName 发送者名字
     * @param groupId 群id
     * @param type 类型 1-原文，2-译文
     * @param data 群消息
     * @description 推送群消息
     */
    public static void pushGroupTxtMessage(String messageId, Long senderId, String senderName, Long groupId, int type,
            String data) {
        GroupTxtMessage msg = new GroupTxtMessage(messageId, senderId, senderName, groupId, type, data);
        try {
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push Group Txt Message  :" + e);
        }
    }

    /**
     * @param groupId 群id
     * @param memberId 成员id
     * @description 推送删除群成员消息
     */
    public static void removeGroupMember(Long groupId, Long memberId) {
        RemoveGroupMember msg = new RemoveGroupMember(groupId, memberId);
        try {
            ChannelFactory.sendMessage(msg);
        } catch (Exception e) {
            LOGGER.error("push remove Group Member message  :" + e);
        }
    }

    /**
     * 发送缓存流消息
     */
    public static void pushCacheBinaryMessage(Long senderId, Long receiverId, String senderName, String receiverName,
            byte[] data, String fileName) {
        if (senderName == null) {
            senderName = "";
        }
        if (receiverName == null) {
            receiverName = "";
        }
        if (fileName == null) {
            fileName = "";
        }

        String messageId = UUID.randomUUID().toString();
        Long dateTime = System.currentTimeMillis();
        String size = String.valueOf(data.length);
        List<byte[]> splitByteArray = splitByteArray(data, MessageFactory.MAX_DATA_LENGTH);
        
        
        try {
            for (int i = 0; i < splitByteArray.size(); i++) {
                byte finish = (byte) (i == splitByteArray.size() - 1 ? 1 : 0);
                CacheBinaryMessage message = new CacheBinaryMessage(messageId , senderId, receiverId, 
                        splitByteArray.get(i), senderName, receiverName, fileName, finish, i, dateTime, size);
                ChannelFactory.sendMessage(message);
            }
        } catch (Exception e) {
            LOGGER.error("push CacheBinaryMessage Message error:" + e);
        }
        
    }

    /**
     * 发送图片消息
     */
    public static void pushPictureMessage(Long senderId, Long receiverId, String senderName, String receiverName,
            byte[] data, String fileName) {
        if (senderName == null) {
            senderName = "";
        }
        if (receiverName == null) {
            receiverName = "";
        }
        if (fileName == null) {
            fileName = "";
        }
        
        String messageId = UUID.randomUUID().toString();
        Long dateTime = System.currentTimeMillis();
        List<byte[]> splitByteArray = splitByteArray(data, MessageFactory.MAX_DATA_LENGTH);
        
        try {
            for (int i = 0; i < splitByteArray.size(); i++) {
                byte finish = (byte) (i == splitByteArray.size() - 1 ? 1 : 0);
                PictureMessage msg = new PictureMessage(senderId, receiverId, splitByteArray.get(i), senderName, 
                        receiverName, fileName, finish, i, dateTime, messageId);
                ChannelFactory.sendMessage(msg);
            }
        } catch (Exception e) {
            LOGGER.error("push PictureMessage error:" + e);
        }
    }
    
    /**
     * 发送音视频
     * @param multimediaLength 音视频播放长度，单位秒
     */
    public static void pushMultimediaMessage(Long senderId, Long receiverId, String senderName, String receiverName,
            byte[] data, String fileName, Long multimediaLength) {
        if (senderName == null) {
            senderName = "";
        }
        if (receiverName == null) {
            receiverName = "";
        }
        if (fileName == null) {
            fileName = "";
        }
        
        String messageId = UUID.randomUUID().toString();
        Long dateTime = System.currentTimeMillis();
        List<byte[]> splitByteArray = splitByteArray(data, MessageFactory.MAX_DATA_LENGTH);
        
        try {
            for (int i = 0; i < splitByteArray.size(); i++) {
                byte finish = (byte) (i == splitByteArray.size() - 1 ? 1 : 0);
                MultimediaMessage msg = new MultimediaMessage(senderId, receiverId, splitByteArray.get(i), senderName, 
                        receiverName, fileName, finish, i, dateTime, multimediaLength, messageId);
                
                
                ChannelFactory.sendMessage(msg);
            }
        } catch (Exception e) {
            LOGGER.error("push PictureMessage error:" + e);
        }
    }
    
    /**
     * 发送群文件消息
     */
    public static void pushGroupCacheBinaryMessage(Long senderId, Long groupId, String senderName, String groupName,
            byte[] data, String fileName) {
        if (senderName == null) {
            senderName = "";
        }
        if (groupName == null) {
            groupName = "";
        }
        if (fileName == null) {
            fileName = "";
        }

        String messageId = UUID.randomUUID().toString();
        Long dateTime = System.currentTimeMillis();
        List<byte[]> splitByteArray = splitByteArray(data, MessageFactory.MAX_DATA_LENGTH);
        
        try {
            for (int i = 0; i < splitByteArray.size(); i++) {
                byte finish = (byte) (i == splitByteArray.size() - 1 ? 1 : 0);
                GroupCacheBinaryMessage message = new GroupCacheBinaryMessage(messageId , senderId, groupId, 
                        splitByteArray.get(i), senderName, groupName, fileName, finish, i, dateTime);
                ChannelFactory.sendMessage(message);
            }
        } catch (Exception e) {
            LOGGER.error("push GroupCacheBinaryMessage Message error:" + e);
        }
        
    }

    /**
     * 
     * @param groupId
     * @param receiveId
     * @description 获取群聊聊天数目
     */
    public int getGroupChatNum(Long groupId, Long receiveId) {
        String key = "communicateNum" + "G" + String.valueOf(groupId) + "to" + String.valueOf(receiveId);
        Object obj = Cache.get(key);
        if (obj != null) {
            return (int) obj;
        } else {
            return 0;
        }
    }

    /**
     * 
     * @param groupId
     * @param receiveId
     * @description 清除群聊聊天数目
     */
    public static boolean cleanGroupChatNum(Long groupId, Long receiveId) {
        String key = "communicateNum" + "G" + String.valueOf(groupId) + "to" + String.valueOf(receiveId);
        try {
            // Object obj = Cache.get(key);
            // if(obj!=null){
            // Cache.remove(key);
            // }
            Cache.set(key, new Long(0));
        } catch (Exception e) {
            Logger.debug("清除聊天数目出错{}", e);
            return false;
        }
        return true;
    }

    /**
     * @description 清除翻译群对应messageId聊聊天数目
     */
    public static boolean cleanTranslateGroupChatNum(Long groupId, Long receiveId, String messageId) {
        String key = "communicateNum" + "G" + String.valueOf(groupId) + "to" + String.valueOf(receiveId) + messageId;
        try {
            // Object obj = Cache.get(key);
            // if(obj!=null){
            // Cache.remove(key);
            // }
            Cache.set(key, new Long(0));
        } catch (Exception e) {
            Logger.debug("清除聊天数目出错{}", e);
            return false;
        }
        return true;
    }

    /**
     * 
     * @param key
     * @description 获取聊天数目自己组装key
     */
    public static int getChatNum(String key) {
        Object obj = Cache.get(key);
        if (obj != null) {
            return (int) obj;
        } else {
            return 0;
        }
    }

    /**
     * 
     * @param key
     * @description 清除聊天数目自己组装key
     */
    public static boolean cleanChatNum(String key) {
        try {
            Object obj = Cache.get(key);
            if (obj != null) {
                Cache.remove(key);
            }
        } catch (Exception e) {
            Logger.debug("清除聊天数目出错{}", e);
            return false;
        }
        return true;
    }

    /**
     * @description 推送到单个人的提醒消息
     */
    public static void pushRemindMessage(String messageId, int type, Long senderId, Long receiverId, String senderName,
            String receiverName, String content) {
        Message messge = new RemindMessage(messageId, senderId, receiverId, senderName, receiverName, content,
                new Date().getTime(), type);
        try {
            ChannelFactory.sendMessage(messge);
        } catch (Exception e) {
            LOGGER.error("push Remind Message:" + e);
        }
    }

    /**
     * 
     * @description 推送到单个群成员的提醒消息
     */
    public static void pushGroupRemindMessage(String messageId, int type, Long senderId, Long receiverId,
            String senderName, String receiverName, String content) {
        Message messge = new RemindMessage(messageId, senderId, receiverId, senderName, receiverName, content,
                new Date().getTime(), type);
        try {
            ChannelFactory.sendMessage(messge);
        } catch (Exception e) {
            LOGGER.error("push Remind Message:" + e);
        }
    }

    /**
     * @param groupId 群组id
     * @param operationId 操作者id
     * @param groupName 群组名称
     * @param operationName 操作者名称
     * @param type 群类型(0 :普通群     1:多人会话    2:翻译群)
     * @description 推送群通知消息( //state:4 创建群 state:5 用户加入群 state:6 修改群名称、头像 state:7 用户退出群 state:8 移除用户 state:9 解散群
     * <br/>
     * 10 - 创建群组
     * 11 - 邀请成员
     * 12 - 加入群组
     * 13 - 退出群组
     * 14 - 移除成员
     * 15 - 解散群组
     *              )
     */
    public static void pushGroupNotice(String messageId, int state, Long groupId, Long operationId, String groupName,
            String operationName, String message,int type) {
        Message messge = new NoticeMessage(messageId, state, message, groupId, groupName, operationId, operationName,type);
        try {
            ChannelFactory.sendMessage(messge);
        } catch (Exception e) {
            LOGGER.error("push Remind Message:" + e);
        }
    }
    
    /**
     * @param userId 用户id
     * @param customServiceId 客服id
     * @param userName 用户名称
     * @param customServiceName 客服名称
     * @param data 消息内容
     * @description 用户注册后，向用户和客服推送的消息
     * 
     *              )
     */
    public static void pushUserOnlineMessage(Long userId, Long customServiceId, String data, String userName, String customServiceName) {
        Message messge = new UserOnlineMessage(userId,customServiceId,data,userName,customServiceName);
        try {
            ChannelFactory.sendMessage(messge);
        } catch (Exception e) {
            LOGGER.error("push ProfileNotice Message:" + e);
        }
    }
    
    
    /**
     * @param groupId 群组id
     * @param operationId 操作者id
     * @param groupNewName 群组修改后的名称
     * @param operationName 操作者名称
     * @param type 群类型(0 :普通群     1:多人会话    2:翻译群)
     * @description
     *              )
     */
    public static void pushProfileNotice(String messageId, int state, Long groupId, Long operationId, String groupNewName,
            String operationName, String message,int type,String avatarUrl) {
        Message messge = new ProfileNoticeMessage( messageId, state,  message,  groupId,
    			 groupNewName,  avatarUrl, operationId,  operationName, type);
        try {
            ChannelFactory.sendMessage(messge);
        } catch (Exception e) {
            LOGGER.error("push ProfileNotice Message:" + e);
        }
    }
    
    private static List<byte[]> splitByteArray(byte[] data, int maxDataLength) {
        List<byte[]> result = new ArrayList<byte[]>();
        
        int packNum = (int) Math.ceil(Float.valueOf(data.length) / maxDataLength);
        for (int i = 0; i < packNum; i++) {
            int subArrayEnd = (i + 1) * maxDataLength > data.length ? data.length : 
                (i + 1) * maxDataLength;
            byte[] subarray = ArrayUtils.subarray(data, i * maxDataLength, subArrayEnd);
            result.add(subarray);
        }
        
        return result;
    }

}
