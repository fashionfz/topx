package utils;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * EXCEL处理工具类
 * @author bin.deng
 *该类主要包括excel的导出
 */
public class ExcelUtil {

	/**
	 * 后台数据导出excel
	  * downloadExcel
	 */
	public static void export(Class<?> clazz,List<?> list,OutputStream os){
		try{
			WritableWorkbook book = Workbook.createWorkbook(os);
			WritableSheet sheet = book.createSheet("第一页", 0);
			int i=0;
			Field[] fields = clazz.getDeclaredFields();
			List<Object[]> excelField = new LinkedList<Object[]>();
			int m=0;
			for(int j=0;j<fields.length;j++){
				ExcelField annotation = fields[j].getAnnotation(ExcelField.class);
				if(annotation!=null){
					Object[] objs = new Object[2];
					Label label = new Label(m++, i, String.valueOf(annotation.lableName()));
					sheet.addCell(label);
					objs[0] = fields[j];
					Class<? extends ExcelConvert> convert = annotation.covertClass();
					objs[1] = convert;
					excelField.add(objs);
				}
			}
			i++;
			Object[] objs;
			for(Object info : list) {
				for(int j=0;j<excelField.size();j++){
					try{
						objs = excelField.get(j);
						Label label = null;
						if(((Field)objs[0]).getType() == Boolean.class || ((Field)objs[0]).getType() == boolean.class){
							String value = String.valueOf(ProxyUtil.isser(info, ((Field)objs[0]).getName()));
							if(!((Class<?>)objs[1]).isInterface()){//不是接口就是实例化
								ExcelConvert convert = (ExcelConvert) ((Class<?>) objs[1]).newInstance();
								value = (String) convert.convert(value);
							}
							label = new Label(j, i, value);
						}else{
							String value = String.valueOf(ProxyUtil.getter(info, ((Field)objs[0]).getName()));
							if(!((Class<?>)objs[1]).isInterface()){//不是接口就是实例化
								ExcelConvert convert = (ExcelConvert) ((Class<?>) objs[1]).newInstance();
								value = (String) convert.convert(value);
							}
							label = new Label(j, i, value);
						}
						sheet.addCell(label);
					}catch(Exception e){
						Label label = new Label(j, i, "/");
						sheet.addCell(label);
					}
				}
				i++;
			}
			 book.write();
	         book.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    

}
