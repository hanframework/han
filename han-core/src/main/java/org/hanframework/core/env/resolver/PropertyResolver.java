package org.hanframework.core.env.resolver;

/**
 * 属性解析器提供通用接口
 * 1. 根据key获取value
 * 2. 根据key变量表达式获取value
 *
 * @author liuxin
 * @version Id: PropertyResolver.java, v 0.1 2018/10/15 11:11 AM
 */
public interface PropertyResolver {

    /**
     * 判断当前key是否存在value
     *
     * @param key 查询key
     * @return 返回值
     */
    boolean containsProperty(String key);

    /**
     * 获取当前key
     *
     * @param key 查询key
     * @return 返回值
     */
    String getProperty(String key);

    /**
     * 获取当前key
     *
     * @param key          查询key
     * @param defaultValue 若不存在,则返回当前值
     * @return 返回值
     */
    String getProperty(String key, String defaultValue);

    /**
     * 获取返回值，返回值为空就报错
     *
     * @param key 查询key
     * @return 返回值
     * @throws IllegalStateException 非法异常
     */
    String getRequiredProperty(String key) throws IllegalStateException;

    /**
     * 表达式: ${...}
     * 从配置文件中读取
     *
     * @param key 查询key
     * @return 返回值
     */
    String resolvePlaceholders(String key);

    /**
     * 从配置文件中读取,当读取不到就报错
     *
     * @param key 查询key
     * @return 返回值
     * @throws IllegalArgumentException 非法异常
     */
    String resolveRequiredPlaceholders(String key) throws IllegalArgumentException;


}
