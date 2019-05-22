package org.hanframework.beans.beanfactory.convert;

/**
 * @author liuxin
 * @version Id: PropertyEditor.java, v 0.1 2018-12-06 14:09
 */
public interface PropertyConvertEditor {
    /**
     * 将Object对象,转换成指定的类型 #{requiredType}
     *
     * @param value        转换前数据
     * @param requiredType 转换后数据
     * @param <T>          转换后的数据类型
     * @return
     */
    default <T> T doConvertIfNecessary(Object value, Class<T> requiredType) {
        if (requiredType == String.class) {
            return (T) value;
        }
        return null;
    }
}
