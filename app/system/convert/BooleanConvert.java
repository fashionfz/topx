package system.convert;

import utils.ExcelConvert;

/**
 * 
 * @ClassName: BooleanConvert 
 * @Description: 常见booelan型转换
 * @author bin.deng 
 * @date 2014年9月17日 下午1:19:23 
 *
 */
public class BooleanConvert implements ExcelConvert{

	@Override
	public Object convert(Object obj) {
		if("true".equals(String.valueOf(obj))||"1".equals(String.valueOf(obj))){
			return "是";
		}else{
			return "否";
		}
	}

}
