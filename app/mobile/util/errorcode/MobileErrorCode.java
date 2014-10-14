package mobile.util.errorcode;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 移动端错误码工具类
 * Created by ShenTeng on 2014/9/23.
 */
public class MobileErrorCode {

    private static Properties properties;

    static {
        init();
    }

    public static void init() {
        try (InputStream is = MobileErrorCode.class.getResourceAsStream("/mobileErrorCode.properties");
             InputStreamReader isr = new InputStreamReader(is, "utf-8")) {
            properties = new Properties();
            properties.load(isr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getErrorContent(String errorKey) {
        String property = properties.getProperty(errorKey);
        if (StringUtils.isBlank(property)) {
            return "";
        } else {
            return property.substring(property.indexOf(' ')).trim();
        }
    }

    public static String getErrorContent(String errorKey, String extraErrorContent) {
        String property = properties.getProperty(errorKey);
        if (StringUtils.isBlank(property)) {
            return "";
        } else {
            return property.substring(property.indexOf(' ')).trim() + "。" + extraErrorContent;
        }
    }

    public static String getErrorCode(String errorKey) {
        String property = properties.getProperty(errorKey);
        if (StringUtils.isBlank(property)) {
            return "";
        } else {
            return property.substring(0, property.indexOf(' ')).trim();
        }
    }

}
