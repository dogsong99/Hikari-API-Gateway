package com.dogsong.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 配置类工具类
 *
 * @author <a href="mailto:dogsong99@gmail.com">dogsong</a>
 * @since 2023/6/14
 */
public class PropertiesUtil {

    public static void properties2Object(final Properties p, final Object object, String prefix) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            String mn = method.getName();

            if (!StringUtils.startsWith(mn, "set")) {
                continue;
            }

            try {
                // set
                String tmp = mn.substring(4);
                String first = mn.substring(3, 4);
                String key = prefix + first.toLowerCase() + tmp;
                String property = p.getProperty(key);

                if (property != null) {
                    Class<?>[] pt = method.getParameterTypes();
                    if (pt.length > 0) {
                        String cn = pt[0].getSimpleName();
                        Object arg = null;
                        switch (cn) {
                            case "int":
                            case "Integer":
                                arg = Integer.parseInt(property);
                                break;
                            case "long":
                            case "Long":
                                arg = Long.parseLong(property);
                                break;
                            case "double":
                            case "Double":
                                arg = Double.parseDouble(property);
                                break;
                            case "boolean":
                            case "Boolean":
                                arg = Boolean.parseBoolean(property);
                                break;
                            case "float":
                            case "Float":
                                arg = Float.parseFloat(property);
                                break;
                            case "String":
                                arg = property;
                                break;
                            default:
                                continue;
                        }
                        method.invoke(object, arg);
                    }
                }

            } catch (Throwable ignored) {

            }
        }
    }

    public static void properties2Object(final Properties p, final Object object) {
        properties2Object(p, object, "");
    }
}
