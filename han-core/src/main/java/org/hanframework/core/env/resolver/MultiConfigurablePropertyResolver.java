package org.hanframework.core.env.resolver;


/**
 * 配置文件引用接口
 * <p>
 * 主要实现@value功能接口
 *
 * @author liuxin
 * @version Id: ConfigurablePropertyResolver.java, v 0.1 2018/10/16 7:53 PM
 */
public interface MultiConfigurablePropertyResolver extends MultiPropertyResolver {

    void setPlaceholderPrefix(String placeholderPrefix);

    void setPlaceholderSuffix(String placeholderSuffix);

    void setValueSeparator(String valueSeparator);

    void setRequiredProperties(String... requiredProperties);

    void validateRequiredProperties();

    void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);
}
