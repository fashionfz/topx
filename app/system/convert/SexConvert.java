package system.convert;

import utils.ExcelConvert;

/**
 * 
 * @ClassName: SexConvert 
 * @Description: 用户性别转换
 * @author bin.deng 
 * @date 2014年9月17日 下午1:19:47 
 *
 */
public class SexConvert implements ExcelConvert{

	@Override
	public Object convert(Object obj) {
		if("0".equals(String.valueOf(obj))){
			return "男";
		}else{
			return "女";
		}
	}

}
