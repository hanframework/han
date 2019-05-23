package org.hanframework.env.loadstrategy;

import org.hanframework.env.resource.Resource;

import java.util.Properties;

/**
 * 配置文件类型加载器
 * 当前支持map类型及yaml格式
 * @author liuxin
 * @version Id: TypeLoadStrategy.java, v 0.1 2018-12-12 14:27
 * @see PropertiesLoadStrategy properties文件后缀解析器
 * @see YamlLoadStrategy yaml或yml文件后缀解析器
 */
public interface TypeLoadStrategy {
    /**
     * 策略接口:
     * 实现类要根据资源的文件类型,进行转换,统一返回Properties格式的数据信息
     * @param resource 文件资源
     * @param charSet  资源字符集
     * @return Properties
     */
    Properties load(Resource resource, String charSet);
}
