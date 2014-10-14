package utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @className: ProxyUtil excel处理注解
 * @author bin.deng
 * @Description 注解需要导出的字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ExcelField {

	/**excel字段名称 */
	String lableName() default "";
	/**转换类，需实现ExcelConvert接口*/
	Class<? extends ExcelConvert> covertClass() default ExcelConvert.class;
}
