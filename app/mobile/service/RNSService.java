/*
 * Copyright (c) 2013, Helome and/or its affiliates. All rights reserved.
 * Helome PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Created on 2014年8月28日
 */
package mobile.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.base.ObjectNodeResult;
import mobile.service.core.FileService;
import mobile.service.result.ServiceResult;
import mobile.service.result.ServiceVOResult;
import mobile.util.MobileUtil;
import mobile.vo.result.CommonVO;
import mobile.vo.result.MobilePage;
import mobile.vo.rns.RequireDetailVO;
import mobile.vo.rns.RequireVO;
import mobile.vo.rns.ServiceDetailVO;
import mobile.vo.rns.ServiceVO;
import models.*;
import models.service.RequireService;
import models.service.ServicesService;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.Json;
import vo.page.Page;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author ShenTeng
 * @ClassName: RNSService
 * @Description: 需求服务Service
 * @date 2014年8月28日 下午6:56:40
 */
public class RNSService {

    private static Object uploadServicePicLock = new Object();
    private static Object updateRequireAttachmentLock = new Object();

    /**
     * 创建需求
     *
     * @param title      标题，必须
     * @param industryId 所属行业Id，必须
     * @param info       说明信息，非必须
     * @param budget     预算，最多可包含8位整数和1位小数，必须
     * @param skillsTags 技能标签，必须
     * @param attachs    需求附件Id集合，非必须
     * @return
     */
    public static ServiceVOResult<CommonVO> createRequire(String title, Long industryId, String info, String budget,
                                                          List<String> skillsTags, List<Long> attachs) {
        if (StringUtils.isBlank(title)) {
            return ServiceVOResult.error("100005", "标题不能为空");
        }
        SkillTag tag = SkillTag.getTagById(industryId);
        if (null == tag || tag.getTagType() != SkillTag.TagType.CATEGORY) {
            return ServiceVOResult.error("100005", "未知的群组所属行业Id：" + industryId);
        }
        if (!Pattern.matches("^\\d{0,8}(\\.\\d)?$", budget) && !"-1".equals(budget)) {
            return ServiceVOResult.error("100005", "预算最多可包含8位整数和1位小数");
        }
        if (null != attachs && attachs.size() > 5) {
            return ServiceVOResult.error("1006", "需求附件数量超过限制");
        }

        ObjectNode data = Json.newObject();
        data.put("title", title);
        data.put("info", info);
        data.put("budget", budget);
        data.put("industry", industryId);
        data.set("tags", Json.toJson(skillsTags));
        if (null != attachs) {
            ArrayNode attachsNode = Json.newObject().arrayNode();
            for (Long attachId : attachs) {
                ObjectNode attachNode = Json.newObject();
                attachNode.put("attachId", attachId);
                attachsNode.add(attachNode);
            }
            data.set("attachs", attachsNode);
        }

        ObjectNodeResult objectNodeResult = new ObjectNodeResult();
        ServiceResult createResult = createOrUpdateRequire(data, objectNodeResult);
        if (!createResult.isSuccess()) {
            return ServiceVOResult.create(createResult);
        }

        CommonVO vo = CommonVO.create();
        vo.set("requireId", objectNodeResult.getObjectNode().path("requireId").asLong(-1));

        return ServiceVOResult.success(vo);
    }

    /**
     * 创建服务
     *
     * @param title      标题，必须
     * @param industryId 行业Id，必须
     * @param info       信息，非必须
     * @param price      价格，必须
     * @param skillsTags 技能标签，必须
     * @param attachs    服务案例图片附件Id集合，非必须
     * @return
     */
    public static ServiceVOResult<CommonVO> createService(String title, Long industryId, String info, String price,
                                                          List<String> skillsTags, List<Long> attachs) {
        if (StringUtils.isBlank(title)) {
            return ServiceVOResult.error("100005", "标题不能为空");
        }
        SkillTag tag = SkillTag.getTagById(industryId);
        if (null == tag || tag.getTagType() != SkillTag.TagType.CATEGORY) {
            return ServiceVOResult.error("100005", "未知的群组所属行业Id：" + industryId);
        }
        if (!Pattern.matches("^\\d{0,8}(\\.\\d)?$", price) && !"-1".equals(price)) {
            return ServiceVOResult.error("100005", "价格最多可包含8位整数和1位小数");
        }
        if (null != attachs && attachs.size() > 5) {
            return ServiceVOResult.error("1005", "服务案例图片数量超过限制");
        }

        ObjectNode data = Json.newObject();
        data.put("title", title);
        data.put("info", info);
        data.put("price", price);
        data.put("industry", industryId);
        data.set("tags", Json.toJson(skillsTags));
        if (null != attachs) {
            ArrayNode attachsNode = Json.newObject().arrayNode();
            for (Long attachId : attachs) {
                ObjectNode attachNode = Json.newObject();
                attachNode.put("attachId", attachId);
                attachsNode.add(attachNode);
            }
            data.set("attachs", attachsNode);
        }

        ObjectNodeResult objectNodeResult = new ObjectNodeResult();
        ServiceResult createResult = createOrUpdateService(data, objectNodeResult);
        if (!createResult.isSuccess()) {
            return ServiceVOResult.create(createResult);
        }

        if (!objectNodeResult.isSuccess()) {
            if ("800002".equals(objectNodeResult.getErrorCode())) {
                return ServiceVOResult.error("1002", objectNodeResult.getError());
            }
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceVOResult.error("100001", "系统错误");
        }

        CommonVO vo = CommonVO.create();
        vo.set("serviceId", objectNodeResult.getObjectNode().path("serviceId").asLong(-1));

        return ServiceVOResult.success(vo);
    }

    /**
     * 更新需求
     *
     * @param requireId  需求Id，必须
     * @param title      标题，非必须
     * @param industryId 行业Id，非必须
     * @param info       信息，非必须
     * @param budget     预算，非必须
     * @param skillsTags 技能标签，非必须
     * @return
     */
    public static ServiceResult updateRequire(Long requireId, String title, Long industryId, String info,
                                              String budget, List<String> skillsTags, List<Long> attachs) {
        if (null == requireId) {
            throw new IllegalArgumentException("requireId can not be null");
        }

        ObjectNode data = Json.newObject();
        data.put("id", requireId);

        if (null != title) {
            if (StringUtils.isBlank(title)) {
                return ServiceResult.error("100005", "标题不能为空");
            } else {
                data.put("title", title);
            }
        }
        if (null != industryId) {
            SkillTag tag = SkillTag.getTagById(industryId);
            if (null == tag || tag.getTagType() != SkillTag.TagType.CATEGORY) {
                return ServiceResult.error("100005", "未知的群组所属行业Id：" + industryId);
            } else {
                data.put("industry", industryId);
            }
        }
        if (null != info) {
            data.put("info", info);
        }
        if (null != budget) {
            if (!Pattern.matches("^\\d{0,8}(\\.\\d)?$", budget) && !"-1".equals(budget)) {
                return ServiceResult.error("100005", "预算最多可包含8位整数和1位小数");
            } else {
                data.put("budget", budget);
            }
        }
        if (null != skillsTags) {
            data.set("tags", Json.toJson(skillsTags));
        }
        if (null != attachs) {
            if (attachs.size() > 5) {
                return ServiceResult.error("1006", "需求附件数量超过限制");
            }
            ArrayNode attachsNode = Json.newObject().arrayNode();
            for (Long attachId : attachs) {
                ObjectNode attachNode = Json.newObject();
                attachNode.put("attachId", attachId);
                attachsNode.add(attachNode);
            }
            data.set("attachs", attachsNode);
        }

        ServiceResult updateResult = createOrUpdateRequire(data, null);
        if (!updateResult.isSuccess()) {
            return updateResult;
        }

        return ServiceResult.success();
    }

    /**
     * 修改需求
     *
     * @param serviceId  需求Id，必须
     * @param title      标题，必须
     * @param industryId 行业Id，必须
     * @param info       信息，非必须
     * @param budget     预算，必须
     * @param skillsTags 技能标签，必须
     * @return
     */
    public static ServiceResult updateService(Long serviceId, String title, Long industryId, String info, String price,
                                              List<String> skillsTags, List<Long> attachs) {
        if (null == serviceId) {
            throw new IllegalArgumentException("serviceId can not be null");
        }

        ObjectNode data = Json.newObject();
        data.put("id", serviceId);

        if (null != title) {
            if (StringUtils.isBlank(title)) {
                return ServiceResult.error("100005", "标题不能为空");
            } else {
                data.put("title", title);
            }
        }
        if (null != industryId) {
            SkillTag tag = SkillTag.getTagById(industryId);
            if (null == tag || tag.getTagType() != SkillTag.TagType.CATEGORY) {
                return ServiceResult.error("100005", "未知的群组所属行业Id：" + industryId);
            } else {
                data.put("industry", industryId);
            }
        }
        if (null != info) {
            data.put("info", info);
        }
        if (null != price) {
            if (!Pattern.matches("^\\d{0,8}(\\.\\d)?$", price) && !"-1".equals(price)) {
                return ServiceResult.error("100005", "需求最多可包含8位整数和1位小数");
            } else {
                data.put("price", price);
            }
        }
        if (null != skillsTags) {
            data.set("tags", Json.toJson(skillsTags));
        }
        if (null != attachs) {
            if (attachs.size() > 5) {
                return ServiceResult.error("1005", "服务案例图片数量超过限制");
            }
            ArrayNode attachsNode = Json.newObject().arrayNode();
            for (Long attachId : attachs) {
                ObjectNode attachNode = Json.newObject();
                attachNode.put("attachId", attachId);
                attachsNode.add(attachNode);
            }
            data.set("attachs", attachsNode);
        }

        ServiceResult updateResult = createOrUpdateService(data, null);
        if (!updateResult.isSuccess()) {
            return updateResult;
        }

        return ServiceResult.success();
    }

    /**
     * 上传服务案例图片
     *
     * @param serviceId 服务Id，非必须。服务Id为null则不关联服务
     * @param file      图片，必须，总图片数量限制5张
     * @param pos       位置，1 - 5
     * @return
     */
    public static ServiceVOResult<CommonVO> uploadServicePic(Long serviceId, File file) {
        Service service = null;
        if (null != serviceId) {
            service = Service.queryServiceById(serviceId);
            if (null == service) {
                return ServiceVOResult.error("1008", "服务不存在");
            }
            if (!service.getOwner().getId().equals(MobileUtil.getCurrentUser().getId())) {
                return ServiceVOResult.error("1004", "你不是该服务的所有者，操作失败");
            }
            if (service.getCaseAttachs().size() >= 5) {
                return ServiceVOResult.error("1005", "服务案例图片数量超过限制");
            }
        }

        ServiceVOResult<CommonVO> uploadResult = FileService.uploadAttatch(file, "mobile.jpg",
                FileService.AttatchType.SERVICE);
        if (!uploadResult.isSuccess()) {
            return ServiceVOResult.error(uploadResult.getErrorCode(), uploadResult.getErrorContent());
        }

        if (null != service) {
            synchronized (uploadServicePicLock) {
                JPA.em().refresh(service);
                Set<AttachOfService> caseAttachs = service.getCaseAttachs();
                if (caseAttachs.size() >= 5) {
                    return ServiceVOResult.error("1005", "服务案例图片数量超过限制");
                }

                // 构造保存Json
                ArrayNode attachArray = Json.newObject().arrayNode();
                for (AttachOfService attachOfService : caseAttachs) {
                    ObjectNode attachNode = Json.newObject();
                    attachNode.put("attachId", attachOfService.id);
                    attachArray.add(attachNode);
                }
                ObjectNode attachNode = Json.newObject();
                attachNode.put("attachId", uploadResult.getVo().getLong("attachId"));
                attachArray.add(attachNode);

                ObjectNode data = Json.newObject();
                data.put("id", serviceId);
                data.set("attachs", attachArray);

                ServiceResult updateResult = createOrUpdateService(data, null);
                if (!updateResult.isSuccess()) {
                    return ServiceVOResult.create(updateResult);
                }
            }
        }

        CommonVO vo = CommonVO.create();
        vo.set("url", uploadResult.getVo().getString("url"));
        vo.set("attachId", uploadResult.getVo().getLong("attachId"));

        return ServiceVOResult.success(vo);
    }

    /**
     * 上传需求附件
     *
     * @param requireId 需求Id，必须
     * @param file      文件，必须
     * @param filename  文件名，必须
     * @return
     */
    public static ServiceVOResult<CommonVO> updateRequireAttachment(Long requireId, File file, String filename) {
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("filename can not be blank.");
        }

        Require require = null;
        if (null != requireId) {
            require = Require.queryRequireById(requireId);
            if (null == require) {
                return ServiceVOResult.error("1007", "需求不存在");
            }
            if (!require.getOwner().getId().equals(MobileUtil.getCurrentUser().getId())) {
                return ServiceVOResult.error("1003", "你不是该需求的所有者，操作失败");
            }
            if (require.getCaseAttachs().size() >= 5) {
                return ServiceVOResult.error("1006", "需求附件数量超过限制");
            }
        }

        ServiceVOResult<CommonVO> uploadResult = FileService.uploadAttatch(file, filename,
                FileService.AttatchType.REQUIRE);
        if (!uploadResult.isSuccess()) {
            return ServiceVOResult.error(uploadResult.getErrorCode(), uploadResult.getErrorContent());
        }

        if (null != require) {
            synchronized (updateRequireAttachmentLock) {
                JPA.em().refresh(require);

                Set<AttachOfRequire> caseAttachs = require.getCaseAttachs();
                if (caseAttachs.size() >= 5) {
                    return ServiceVOResult.error("1006", "需求附件数量超过限制");
                }

                // 构造保存Json
                ArrayNode attachArray = Json.newObject().arrayNode();
                for (AttachOfRequire attachOfRequire : caseAttachs) {
                    ObjectNode attachNode = Json.newObject();
                    attachNode.put("attachId", attachOfRequire.id);
                    attachArray.add(attachNode);
                }
                ObjectNode attachNode = Json.newObject();
                attachNode.put("attachId", uploadResult.getVo().getLong("attachId"));
                attachArray.add(attachNode);

                ObjectNode data = Json.newObject();
                data.put("id", requireId);
                data.set("attachs", attachArray);

                ServiceResult updateResult = createOrUpdateRequire(data, null);
                if (!updateResult.isSuccess()) {
                    return ServiceVOResult.create(updateResult);
                }

            }
        }

        CommonVO vo = CommonVO.create();
        vo.set("url", uploadResult.getVo().getString("url"));
        vo.set("attachId", uploadResult.getVo().getLong("attachId"));

        return ServiceVOResult.success(vo);
    }

    /**
     * 删除服务图片
     *
     * @param serviceId 服务Id，必须
     * @param index     在附件数组中的序号，从0开始
     * @return
     */
    public static ServiceResult deleteServicePic(Long serviceId, List<Integer> index) {
        Service service = Service.queryServiceById(serviceId);
        if (null == service) {
            return ServiceResult.error("1008", "服务不存在");
        }
        if (!service.getOwner().getId().equals(MobileUtil.getCurrentUser().getId())) {
            return ServiceResult.error("1004", "你不是该服务的所有者，操作失败");
        }

        // 构造保存Json
        Set<AttachOfService> caseAttachs = service.getCaseAttachs();
        ArrayNode attachArray = Json.newObject().arrayNode();
        int counter = 0;
        List<Long> deleteAttachIdList = new ArrayList<Long>();
        for (AttachOfService attachOfService : caseAttachs) {
            if (index.contains(counter)) {
                deleteAttachIdList.add(attachOfService.id);
            } else {
                ObjectNode attachNode = Json.newObject();
                attachNode.put("attachId", attachOfService.id);
                attachArray.add(attachNode);
            }

            counter++;
        }
        Attach.deleteByIds(deleteAttachIdList, AttachOfService.class);

        ObjectNode data = Json.newObject();
        data.put("id", serviceId);
        data.set("attachs", attachArray);

        ServiceResult deleteResult = createOrUpdateService(data, null);
        if (!deleteResult.isSuccess()) {
            return deleteResult;
        }

        return ServiceResult.success();
    }

    /**
     * 删除需求附件
     *
     * @param requireId 需求Id，必须
     * @param index     在附件数组中的序号，从0开始
     * @return
     */
    public static ServiceResult deleteRequireAttachment(Long requireId, List<Integer> index) {
        Require require = Require.queryRequireById(requireId);
        if (null == require) {
            return ServiceResult.error("1007", "需求不存在");
        }
        if (!require.getOwner().getId().equals(MobileUtil.getCurrentUser().getId())) {
            return ServiceResult.error("1003", "你不是该需求的所有者，操作失败");
        }

        Set<AttachOfRequire> caseAttachs = require.getCaseAttachs();

        // 构造保存Json
        ArrayNode attachArray = Json.newObject().arrayNode();
        int counter = 0;
        List<Long> deleteAttachIdList = new ArrayList<Long>();
        for (AttachOfRequire attachOfService : caseAttachs) {
            if (index.contains(counter)) {
                deleteAttachIdList.add(attachOfService.id);
            } else {
                ObjectNode attachNode = Json.newObject();
                attachNode.put("attachId", attachOfService.id);
                attachArray.add(attachNode);
            }

            counter++;
        }
        Attach.deleteByIds(deleteAttachIdList, AttachOfRequire.class);

        ObjectNode data = Json.newObject();
        data.put("id", requireId);
        data.set("attachs", attachArray);

        ServiceResult deleteResult = createOrUpdateRequire(data, null);
        if (!deleteResult.isSuccess()) {
            return deleteResult;
        }

        return ServiceResult.success();
    }

    /**
     * 分页获取服务
     *
     * @param pageIndex   页数，从1开始，必须
     * @param pageSize    每页条数，必须
     * @param ownerUserId 过滤条件：所属用户Id，非必须
     * @param searchText  过滤条件：查询字符串，非必须
     * @param industryId  过滤条件：行业Id，非必须
     * @param skillTag    过滤条件：技能标签，非必须
     * @return
     */
    public static ServiceVOResult<MobilePage<ServiceVO>> getServicePage(int pageIndex, int pageSize, Long ownerUserId,
                                                                        String searchText, Long industryId, String skillTag) {
        Page<Service> poPage = Service.queryServiceByPage(pageIndex - 1, pageSize, ownerUserId, searchText, industryId,
                skillTag, true, true);

        List<ServiceVO> list = new ArrayList<ServiceVO>();
        for (Service po : poPage.getList()) {
            list.add(ServiceVO.create(po));
        }
        MobilePage<ServiceVO> mobilePage = new MobilePage<ServiceVO>(poPage.getTotalRowCount(), list);

        return ServiceVOResult.success(mobilePage);
    }

    /**
     * 分页获取需求
     *
     * @param pageIndex   页数，从1开始，必须
     * @param pageSize    每页条数，必须
     * @param ownerUserId 过滤条件：所属用户Id，非必须
     * @param searchText  过滤条件：查询字符串，非必须
     * @param industryId  过滤条件：行业Id，非必须
     * @param skillTag    过滤条件：技能标签，非必须
     * @return
     */
    public static ServiceVOResult<MobilePage<RequireVO>> getRequirePage(Integer pageIndex, Integer pageSize,
                                                                        Long ownerUserId, String searchText, Long industryId, String skillTag) {
        Page<Require> poPage = Require.queryRequireByPage(pageIndex - 1, pageSize, ownerUserId, searchText, industryId,
                skillTag, true, true);

        List<RequireVO> list = new ArrayList<RequireVO>();
        for (Require po : poPage.getList()) {
            list.add(RequireVO.create(po));
        }
        MobilePage<RequireVO> mobilePage = new MobilePage<RequireVO>(poPage.getTotalRowCount(), list);

        return ServiceVOResult.success(mobilePage);
    }

    /**
     * 删除我的需求
     *
     * @param requireId 需求Id
     * @return
     */
    public static ServiceResult deleteMyRequire(Long requireId) {
        Require require = Require.queryRequireById(requireId);
        if (null == require) {
            return ServiceResult.error("1007", "需求不存在");
        }
        if (!require.getOwner().getId().equals(MobileUtil.getCurrentUser().getId())) {
            return ServiceResult.error("1003", "你不是该需求的所有者，操作失败");
        }

        ObjectNodeResult objectNodeResult = RequireService.deleteById(requireId, MobileUtil.getCurrentUser());

        if (!objectNodeResult.isSuccess()) {
            if ("700003".equals(objectNodeResult.getErrorCode())) {
                return ServiceResult.error("1003", "你不是该需求的所有者，操作失败");
            }
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceResult.error("100001", "系统错误");
        }

        return ServiceResult.success();
    }

    /**
     * 删除我的服务
     *
     * @param serviceId 服务Id
     * @return
     */
    public static ServiceResult deleteMyService(Long serviceId) {
        Service service = Service.queryServiceById(serviceId);
        if (null == service) {
            return ServiceResult.error("1008", "服务不存在");
        }
        if (!service.getOwner().getId().equals(MobileUtil.getCurrentUser().getId())) {
            return ServiceResult.error("1004", "你不是该服务的所有者，操作失败");
        }

        ObjectNodeResult objectNodeResult = ServicesService.deleteById(serviceId, MobileUtil.getCurrentUser());

        if (!objectNodeResult.isSuccess()) {
            if ("700003".equals(objectNodeResult.getErrorCode())) {
                return ServiceResult.error("1004", "你不是该服务的所有者，操作失败");
            }
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceResult.error("100001", "系统错误");
        }

        return ServiceResult.success();
    }

    /**
     * 获取需求详细
     *
     * @param requireId 需求Id
     * @return
     */
    public static ServiceVOResult<RequireDetailVO> getRequireDetail(Long requireId) {
        Require po = Require.queryRequireById(requireId);
        if (null == po) {
            return ServiceVOResult.error("1007", "需求不存在");
        }

        return ServiceVOResult.success(RequireDetailVO.create(po));
    }

    /**
     * 获取服务详细
     *
     * @param serviceId 服务Id
     * @return
     */
    public static ServiceVOResult<ServiceDetailVO> getServiceDetail(Long serviceId) {
        Service po = Service.queryServiceById(serviceId);
        if (null == po) {
            return ServiceVOResult.error("1008", "服务不存在");
        }

        return ServiceVOResult.success(ServiceDetailVO.create(po));
    }

    private static ServiceResult createOrUpdateService(ObjectNode data, ObjectNodeResult returnRawResult) {
        if (data.hasNonNull("id")) {
            Long id = data.get("id").asLong();
            Service service = Service.queryServiceById(id);
            if (null == service) {
                return ServiceResult.error("1008", "服务不存在");
            }
        }

        if ("-1".equals(data.path("price").asText())) {
            data.put("price", "");
        }

        ObjectNodeResult objectNodeResult = ServicesService.createOrUpdateService(MobileUtil.getCurrentUser(), data);
        if (null != returnRawResult) {
            returnRawResult.setAll(objectNodeResult.getObjectNode());
        }

        // 处理返回结果
        if (!objectNodeResult.isSuccess()) {
            if ("800002".equals(objectNodeResult.getErrorCode())) {
                return ServiceResult.error("1001", objectNodeResult.getError());
            } else if ("-301".equals(objectNodeResult.getErrorCode())) {
                return ServiceResult.error("1004", "你不是该服务的所有者，操作失败");
            }
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceResult.error("100001", "系统错误");
        }

        return ServiceResult.success();
    }

    private static ServiceResult createOrUpdateRequire(ObjectNode data, ObjectNodeResult returnRawResult) {
        if (data.hasNonNull("id")) {
            Long id = data.get("id").asLong();
            Require require = Require.queryRequireById(id);
            if (null == require) {
                return ServiceResult.error("1007", "需求不存在");
            }
        }

        if ("-1".equals(data.path("budget").asText())) {
            data.put("budget", "");
        }

        ObjectNodeResult objectNodeResult = RequireService.createOrUpdateService(MobileUtil.getCurrentUser(), data);
        if (null != returnRawResult) {
            returnRawResult.setAll(objectNodeResult.getObjectNode());
        }

        // 处理返回结果
        if (!objectNodeResult.isSuccess()) {
            if ("700002".equals(objectNodeResult.getErrorCode())) {
                return ServiceResult.error("1001", objectNodeResult.getError());
            } else if ("-301".equals(objectNodeResult.getErrorCode())) {
                return ServiceResult.error("1003", "你不是该需求的所有者，操作失败");
            }
            Logger.error(objectNodeResult.getObjectNode().toString());
            return ServiceResult.error("100001", "系统错误");
        }

        return ServiceResult.success();
    }
}
