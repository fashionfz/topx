package mobile.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.vo.result.CommonVO;
import models.Group;
import models.User;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import play.mvc.Http.Context;
import ext.MessageCenter.utils.MCMessageUtil;
import ext.translate.Translate;
import ext.translate.TranslateVO;

/**
 * 
 * @ClassName: ChatService
 * @Description: 聊天相关的服务
 * @date 2014-6-25 上午10:37:15
 * @author ShenTeng
 * 
 */
public class ChatService {

    /**
     * 发送聊天文件文件
     * 
     * @param uploadFile 上传文件。必须
     * @param filename 文件名。必须
     * @param receiverId 接收者用户Id或群组Id。必须
     * @param receiverType 接收者类型。group-群组，user-用户。必须
     * @param type 文件类别。file - 文件，picture - 图片，multimedia - 音视频。必须
     * @param multimediaLength 音视频播放长度，单位秒，只用于类型为音视频的文件。音视频时必须
     */
    public static ServiceResult sendChatFile(File uploadFile, String filename, Long receiverId, String receiverType,
            String type, Long multimediaLength) {
        // 参数校验
        if (StringUtils.isBlank(receiverType) || (!"group".equals(receiverType) && !"user".equals(receiverType))) {
            throw new IllegalArgumentException("非法的receiverType。receiverType = " + receiverType);
        }
        if (!"file".equals(type) && !"picture".equals(type) && !"multimedia".equals(type)) {
            return ServiceResult.error("100005", "未知的文件类别");
        }
        int fileNameSuffixIndex = filename.lastIndexOf('.');
        if (fileNameSuffixIndex < 0) {
            return ServiceResult.error("100005", "非法的文件名");
        }
        if ("group".equals(receiverType) && !"file".equals(type)) {
            return ServiceResult.error("100005", "群组聊天上传文件不支持该类型");
        }

        User me = User.getFromSession(Context.current().session());

        try (FileInputStream fis = new FileInputStream(uploadFile)) {
            ByteArrayOutputStream os = new ByteArrayOutputStream(8192);

            byte[] read = new byte[8192];
            int size = -1;
            while ((size = fis.read(read)) != -1) {
                os.write(read, 0, size);
            }

            if ("user".equals(receiverType)) {// 对用户上传
                User receiver = User.findById(receiverId);

                if (receiver == null) {
                    return ServiceResult.error("4003", "接收用户不存在");
                }
                if ("file".equals(type)) {
                    MCMessageUtil.pushCacheBinaryMessage(me.getId(), receiver.getId(), me.getName(),
                            receiver.getName(), os.toByteArray(), filename);
                } else if ("picture".equals(type)) {
                    MCMessageUtil.pushPictureMessage(me.getId(), receiver.getId(), me.getName(), receiver.getName(),
                            os.toByteArray(), filename);
                } else if ("multimedia".equals(type)) {
                    MCMessageUtil.pushMultimediaMessage(me.getId(), receiver.getId(), me.getName(), receiver.getName(),
                            os.toByteArray(), filename, multimediaLength);
                }
            } else {// 对群组上传
                Group group = Group.queryGroupById(receiverId);
                if (group == null) {
                    return ServiceResult.error("4002", "接收群组不存在");
                }
                List<Long> groupIdList = Arrays.asList(receiverId);
                Map<Long, Boolean> checkJoinGroup = models.GroupMember.checkJoinGroup(me.getId(), groupIdList);
                if (checkJoinGroup.get(receiverId) == false) {
                    return ServiceResult.error("4001", "你还没有加入或已被踢出群组，不能发送群组聊天文件");
                }

                if ("file".equals(type)) {
                    MCMessageUtil.pushGroupCacheBinaryMessage(me.getId(), group.getId(), me.getName(),
                            group.getGroupName(), os.toByteArray(), filename);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ServiceResult.success();
    }

    public static ServiceVOResult<CommonVO> translate(String content) {

        String translatedContent = null;
        if (StringUtils.isNotBlank(content)) {
            Translate tran = new Translate(content);
            TranslateVO tv = tran.translateAdapt();

            if (StringUtils.isNotBlank(tv.errorCode) || CollectionUtils.isEmpty(tv.results)) {
                return ServiceVOResult.error("283001", "翻译失败." + tv.errorMsg);
            }

            translatedContent = tv.results.get(0).dst;
        } else {
            translatedContent = content;
        }

        ServiceVOResult<CommonVO> result = ServiceVOResult.success();
        CommonVO vo = CommonVO.create();
        vo.set("content", translatedContent);
        result.setVo(vo);

        return result;
    }
}
