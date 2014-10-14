package system.convert;

import utils.ExcelConvert;

/**
 * 
 * @ClassName: StatusConvert 
 * @Description: 用户状态转换 
 * @author bin.deng 
 * @date 2014年9月17日 下午1:18:15 
 *
 */
public class StatusConvert implements ExcelConvert{

	@Override
	public Object convert(Object obj) {
		if("true".equals(String.valueOf(obj))||"1".equals(String.valueOf(obj))){
			return "正常";
		}else{
			return "禁用";
		}
	}
}
