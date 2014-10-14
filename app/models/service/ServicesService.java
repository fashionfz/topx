package models.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.Attach;
import models.AttachOfService;
import models.Comment;
import models.Service;
import models.ServiceComment;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import vo.ServiceDetailVO;
import vo.ServiceInfoVO;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;

import common.Constants;
import controllers.base.ObjectNodeResult;

/**
 * @ClassName: ServicesService
 * @Description: 网站的服务功能服务
 */
public class ServicesService {
	
	/**
	 * 创建或修改服务
	 * @param currentUser 当前用户
	 * @param json json字符串
	 * @return
	 */
	public static ObjectNodeResult createOrUpdateService(User currentUser, JsonNode json) {
		return Service.saveOrUpdateByJson(currentUser, json);
	}
	
	/**
	 * 分页获取服务的信息
	 * @param page
	 * @param pageSize
	 * @param user
	 * @param searchText
	 * @return
	 */
	public static Page<ServiceDetailVO> getServicePage(int page, int pageSize, User user, String searchText) {
		Page<Service> servicePage = Service.queryServiceByPage(page, pageSize, user.getId(), searchText, null, null, 
		        true, false);
		List<Service> serviceList = servicePage.getList();
		List<ServiceDetailVO> data = new ArrayList<ServiceDetailVO>();
		if (CollectionUtils.isNotEmpty(serviceList)) {
			for (Service s : serviceList) {
				ServiceDetailVO vo = new ServiceDetailVO();
				vo.convert(s);
				if (StringUtils.isNotBlank(vo.getInfo())) {
					if (vo.getInfo().length() > 30) {
						vo.setInfo(StringUtils.substring(vo.getInfo(), 0, 30) + "...");
					}
				}
				data.add(vo);
			}
		}
		return new Page<ServiceDetailVO>(Constants.SUCESS, servicePage.getTotalRowCount(), data);
	}
	
	/**
	 * 分页获取服务列表的信息
	 * @param page
	 * @param pageSize
	 * @param user
	 * @return
	 */
	public static Page<ServiceDetailVO> getServicVOListPage(int page, int pageSize, User user) {
		Page<Service> servicePage = Service.queryServiceByPage(page, pageSize, user);
		List<Service> serviceList = servicePage.getList();
		List<ServiceDetailVO> data = new ArrayList<ServiceDetailVO>();
		if (CollectionUtils.isNotEmpty(serviceList)) {
			for (Service s : serviceList) {
				ServiceDetailVO vo = new ServiceDetailVO();
				vo.convertVOList(s);
				data.add(vo);
			}
		}
		return new Page<ServiceDetailVO>(Constants.SUCESS, servicePage.getTotalRowCount(), data);
	}

	/**
	 * 根据id删除
	 * @param service
	 * @return
	 */
	public static ObjectNodeResult deleteById(Long serviceId, User user) {
		ObjectNodeResult result = new ObjectNodeResult();
		Service service = Service.queryServiceById(serviceId);
		if (service == null) {
			return result;
		}
		if (service.getOwner().getId() - user.getId() != 0) { // 判断创建者是否是指定的用户
			return result.error("你未发布该服务，不能删除！", "800003");
		}
		Set<AttachOfService> asSet = service.getCaseAttachs();
		// 删除服务对应的评论
		List<Long> commentIdList =  Comment.queryCommnetByServiceId(serviceId, ServiceComment.class);
		Comment.deleteByIds(commentIdList);
		// 删除服务
		Service.deleteById(serviceId);
		List<Long> attachIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(asSet)) {
			for (AttachOfService as : asSet) {
				attachIdList.add(as.id);
			}
		}
		// 删除附件
		Attach.deleteByIds(attachIdList, AttachOfService.class);
		return result;
	}
	
	/**
	 * 根据service的id查询service的信息
	 * @param serviceId
	 * @return
	 */
	public static ServiceInfoVO queryServiceById(Long serviceId) {
		Service service = Service.queryServiceById(serviceId);
		ServiceInfoVO serviceInfoVO = new ServiceInfoVO();
		if (service != null) {
			serviceInfoVO.convert(service);
		}
		return serviceInfoVO;
	}
	
}
