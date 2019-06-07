package org.hanframework.env;
import org.hanframework.env.resolver.source.CommonProperty;
import org.hanframework.env.resolver.source.MutablePropertySources;
import org.hanframework.env.resolver.source.PropertySource;

import java.util.Map;

/**
 * 在IOC生成BeanDefinition后,开始加载标准环境
 * @author liuxin
 * @version Id: StandardEnvironment.java, v 0.1 2018/10/17 4:16 PM
 */
public class StandardEnvironment extends AbstractEnvironment {
    /**
     * System environment property source name: {@value}
     */
    private static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";

    /**
     * JVM system properties property source name: {@value}
     */
    private static final String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemJvmProperties";

    @Override
    protected void customizePropertySources(MutablePropertySources propertySources) {
        propertySources.add(new CommonProperty(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, getJvmProperties()));
        propertySources.add(new CommonProperty(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, getSystemEnvironment()));
    }

}
