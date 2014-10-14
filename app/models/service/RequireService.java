package models.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import models.Attach;
import models.AttachOfRequire;
import models.Require;
import models.User;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import vo.RequireDetailVO;
import vo.RequireInfoVO;
import vo.page.Page;

import com.fasterxml.jackson.databind.JsonNode;

import common.Constants;
import controllers.base.ObjectNodeResult;

/**
 * @ClassName: RequireService
 * @Description: 网站的需求功能服务
 */
public class RequireService {
	
	/**
	 * 创建或修改需求
	 * @param currentUser 当前用户
	 * @param json json字符串
	 * @return
	 */
	public static ObjectNodeResult createOrUpdateService(User currentUser, JsonNode json) {
		return Require.saveOrUpdateByJson(currentUser, json);
	}
	
	/**
	 * 分页获取需求的信息
	 * @param page
	 * @param pageSize
	 * @param user
	 * @param searchText
	 * @return
	 */
	public static Page<RequireDetailVO> getRequirePage(int page, int pageSize, User user, String searchText) {
		Page<Require> requirePage = Require.queryRequireByPage(page, pageSize, user.getId(), searchText, null, null, 
		        true, false);
		List<Require> requireList = requirePage.getList();
		List<RequireDetailVO> data = new ArrayList<RequireDetailVO>();
		if (CollectionUtils.isNotEmpty(requireList)) {
			for (Require r : requireList) {
				RequireDetailVO vo = new RequireDetailVO();
				vo.convert(r);
				if (StringUtils.isNotBlank(vo.getInfo())) {
					if (vo.getInfo().length() > 30) {
						vo.setInfo(StringUtils.substring(vo.getInfo(), 0, 30) + "...");
					}
				}
				data.add(vo);
			}
		}
		return new Page<RequireDetailVO>(Constants.SUCESS, requirePage.getTotalRowCount(), data);
	}
	
	/**
	 * 分页获取个人对应的需求列表的信息
	 * @param page
	 * @param pageSize
	 * @param user
	 * @param searchText
	 * @return
	 */
	public static Page<RequireDetailVO> getRequirePage(int page, int pageSize, User user) {
		Page<Require> requirePage = Require.queryRequireByPage(page, pageSize, user);
		List<Require> requireList = requirePage.getList();
		List<RequireDetailVO> data = new ArrayList<RequireDetailVO>();
		if (CollectionUtils.isNotEmpty(requireList)) {
			for (int i = 0; i < requireList.size(); i++) {
				Require r = requireList.get(i);
				RequireDetailVO vo = new RequireDetailVO();
				vo.convertVOList(r);
				vo.setIndex(page * pageSize + (i + 1));
				data.add(vo);
			}
		}
		return new Page<RequireDetailVO>(Constants.SUCESS, requirePage.getTotalRowCount(), data);
	}
	
	/**
	 * 根据id删除
	 * @param requireId
	 * @param user
	 * @return
	 */
	public static ObjectNodeResult deleteById(Long requireId, User user) {
		ObjectNodeResult result = new ObjectNodeResult();
		Require require = Require.queryRequireById(requireId);
		if (require == null) {
			return result;
		}
		if (require.getOwner().getId() - user.getId() != 0) { // 判断创建者是否是指定的用户
			return result.error("你未发布该服务，不能删除！", "700003");
		}
		Set<AttachOfRequire> arSet = require.getCaseAttachs();
		// 删除需求
		Require.deleteById(requireId);
		List<Long> attachIdList = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(arSet)) {
			for (AttachOfRequire ar : arSet) {
				attachIdList.add(ar.id);
			}
		}
		// 删除附件
		Attach.deleteByIds(attachIdList, AttachOfRequire.class);
		return result;
	}
	
	/**
	 * 根据require的id查询require的信息
	 * @param requireId
	 * @return
	 */
	public static RequireInfoVO queryRequireById(Long requireId) {
		Require require = Require.queryRequireById(requireId);
		RequireInfoVO requireInfoVO = new RequireInfoVO();
		if (require != null) {
			requireInfoVO.convert(require);
		}
		return requireInfoVO;
	}
	
	
	
}
