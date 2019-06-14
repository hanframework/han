package org.hanframework.tool.common;


/**
 * @author liuxin
 * @date 2017/10/11 上午11:17
 */
public class Default {

    @SuppressWarnings("unchecked")
    public static <T> T defaultValue(Object value, Object defaultValue, Class<T> genericType) {
        if (null != value) {
            return (T) value;
        } else if (null != defaultValue) {
            return (T) defaultValue;
        } else {
            throw new IllegalArgumentException("参数不能都为空");
        }
    }

    public static <T> T defaultValue(Object str, Object defaultValue, DefaultIF<T> defaultIf) {
        T value = null;
        boolean flag = true;
        if (defaultIf != null) {
            flag = defaultIf.defaultIf();
        }
        if (!flag) {
            value = ((T) defaultValue);
        } else {
            value = ((T) str);
        }
        return value;
    }

    /**
     * 如果是true就用原始数据
     * 如果是false就要用defalueValue
     */
    public interface DefaultIF<T> {
        boolean defaultIf();
    }
}
