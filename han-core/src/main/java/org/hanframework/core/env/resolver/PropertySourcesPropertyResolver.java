package org.hanframework.core.env.resolver;

import org.hanframework.core.env.resolver.source.PropertySource;
import org.hanframework.core.env.resolver.source.PropertySources;
import org.hanframework.tool.string.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 主要是实现@Value
 *
 * @author liuxin
 * @version Id: PropertySourcesPropertyResolver.java, v 0.1 2018/10/16 7:55 PM
 */
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {
    Logger logger = LoggerFactory.getLogger(PropertySourcesPropertyResolver.class);

    private final PropertySources propertySources;

    private final List<String> requiredPropertiesList = new ArrayList<>();

    public PropertySourcesPropertyResolver(PropertySources propertySources) {
        this.propertySources = propertySources;
    }

    @Override
    public boolean containsProperty(String key) {
        if (this.propertySources != null) {
            for (PropertySource<?> propertySource : this.propertySources) {
                if (propertySource.containsProperty(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getProperty(String key) {
        return getProperty(key, String.class, true);
    }


    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        return getProperty(key, String.class, false);
    }

    @Override
    public void validateRequiredProperties() {
        for (String string : requiredPropertiesList) {
            if (containsProperty(string)) {
                throw new RuntimeException("必须参数:[" + string + "]校验不通过");
            }
        }
    }


    @Override
    protected String getPropertyAsRawString(String key) {
        return getProperty(key, String.class, false);
    }

    @Override
    protected String getPropertyAsRawString(String key, String profile) {
        return getProperty(key, profile, String.class, false);
    }


    protected <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
        return getProperty(key, "default", targetValueType, resolveNestedPlaceholders);
    }

    protected <T> T getProperty(String key, String profile, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
        boolean debugEnabled = logger.isDebugEnabled();
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("getProperty(\"%s\", %s)", key, targetValueType.getSimpleName()));
        }
        if (this.propertySources != null) {
            for (PropertySource<?> propertySource : this.propertySources) {
                if (!StringTools.equals(propertySource.getProfile(), profile)) {
                    continue;
                }
                if (debugEnabled) {
                    logger.debug(String.format("Searching for key '%s' in [%s]", key, propertySource.getName()));
                }
                Object value;
                if ((value = propertySource.getProperty(key)) != null) {
                    Class<?> valueType = value.getClass();
                    if (resolveNestedPlaceholders && value instanceof String) {
                        value = resolveNestedPlaceholders((String) value);
                    }
                    if (debugEnabled) {
                        logger.debug(String.format("Found key '%s' in [%s] with type [%s] and value '%s'",
                                key, propertySource.getName(), valueType.getSimpleName(), value));
                    }
                    return (T) value;
                }
            }
        }
        if (debugEnabled) {
            logger.debug(String.format("Could not find key '%s' in any property source. Returning [null]", key));
        }
        return null;
    }

    @Override
    public void setRequiredProperties(String... requiredProperties) {
        for (String requiredString : requiredProperties) {
            this.requiredPropertiesList.add(requiredString);
        }
    }
}
