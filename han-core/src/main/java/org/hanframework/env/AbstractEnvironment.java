package org.hanframework.env;

import org.hanframework.env.resolver.source.PropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hanframework.env.resolver.MultiConfigurablePropertyResolver;
import org.hanframework.env.resolver.PropertySourcesPropertyResolver;
import org.hanframework.env.resolver.source.MutablePropertySources;
import org.hanframework.tool.asserts.Assert;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author liuxin
 * @version Id: AbstractEnvironment.java, v 0.1 2018/10/16 7:29 PM
 */
public abstract class AbstractEnvironment implements ConfigurableEnvironment {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String RESERVED_DEFAULT_PROFILE_NAME = "default";

    /**
     * @see ConfigurableEnvironment#setActiveProfiles
     */
    private static final String ACTIVE_PROFILES_PROPERTY_NAME = "han.profiles.active";

    /**
     * Name of property to set to specify profiles active by default: {@value}. Value may
     *
     * @see ConfigurableEnvironment#setDefaultProfiles
     */
    private static final String DEFAULT_PROFILES_PROPERTY_NAME = "han.profiles.default";

    /**
     * 激活的配置文件信息
     */
    private Set<String> activeProfiles = new HashSet<>(3);

    /**
     * 默认的配置文件信息
     */
    private Set<String> defaultProfiles = new HashSet<>(Collections.singleton(RESERVED_DEFAULT_PROFILE_NAME));

    /**
     * 环境信息
     */
    private final MutablePropertySources propertySources = new MutablePropertySources(this.logger);

    /**
     * 参数解析器
     */
    private final MultiConfigurablePropertyResolver multiConfigurablePropertyResolver =
            new PropertySourcesPropertyResolver(this.propertySources);


    /**
     * 所有的配置信息由customizePropertySources注入
     */
    protected AbstractEnvironment() {
        customizePropertySources(this.propertySources);
        if (this.logger.isDebugEnabled()) {
            List<String> names = new ArrayList<>();
            Iterator<PropertySource<?>> iterator = propertySources.iterator();
            while (iterator.hasNext()) {
                PropertySource<?> next = iterator.next();
                names.add(next.getName() + ":" + next.getProfile());
            }
            this.logger.debug(String.format(
                    "Initialized %s with PropertySources %s", getClass().getSimpleName(), names));
        }
    }

    /**
     * 留给子类来实现
     *
     * @param propertySources 多属性源
     */
    protected void customizePropertySources(MutablePropertySources propertySources) {
    }

    @Override
    public MutablePropertySources getPropertySources() {
        return propertySources;
    }

    /**
     * Java运行环境信息
     *
     * @return map
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getJvmProperties() {
        return (Map) System.getProperties();

    }

    /**
     * 系统PATH信息
     *
     * @return map
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSystemEnvironment() {
        return (Map) System.getenv();
    }


    /**
     * 是否包含某个环境信息
     * JVM环境信息
     * 系统PATH环境信息
     *
     * @see StandardEnvironment
     * public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";
     * String SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME = "systemProperties";
     */
    @Override
    public boolean containsProperty(String key) {
        return multiConfigurablePropertyResolver.containsProperty(key);
    }

    @Override
    public String getProperty(String key) {
        return multiConfigurablePropertyResolver.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return multiConfigurablePropertyResolver.getProperty(key, defaultValue);
    }

    @Override
    public void setActiveProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        this.activeProfiles.clear();
        Collections.addAll(activeProfiles, profiles);
    }

    @Override
    public void addActiveProfile(String profile) {
        doGetActiveProfiles();
        this.activeProfiles.add(profile);
    }

    @Override
    public String[] getActiveProfiles() {
        if (this.activeProfiles.isEmpty()) {
            String profiles = getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
            this.activeProfiles.add(profiles);
        }
        return activeProfiles.toArray(new String[0]);
    }


    private Set<String> doGetActiveProfiles() {
        if (this.activeProfiles.isEmpty()) {
            String profiles = getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
            this.activeProfiles.add(profiles);
        }
        return this.activeProfiles;
    }

    protected Set<String> doGetDefaultProfiles() {
        if (this.defaultProfiles.equals(Collections.singleton(RESERVED_DEFAULT_PROFILE_NAME))) {
            String profiles = getProperty(DEFAULT_PROFILES_PROPERTY_NAME);
            setDefaultProfiles(profiles);
        }
        return this.defaultProfiles;
    }


    @Override
    public void setDefaultProfiles(String... profiles) {
        Assert.notNull(profiles, "Profile array must not be null");
        this.defaultProfiles.clear();
        Collections.addAll(defaultProfiles, profiles);
    }


    @Override
    public String[] getDefaultProfiles() {
        return defaultProfiles.toArray(new String[0]);
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        return multiConfigurablePropertyResolver.getRequiredProperty(key);
    }

    @Override
    public boolean acceptsProfiles(String... profiles) {
        for (String profile : profiles) {
            if (!activeProfiles.contains(profile)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取激活的
     *
     * @param text 表达式
     * @return string
     */
    @Override
    public String resolvePlaceholders(String text) {
        String result = null;
        String activeProfile = multiConfigurablePropertyResolver.getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
        if (null == activeProfile) {
            activeProfile = RESERVED_DEFAULT_PROFILE_NAME;
        }
        boolean contains = activeProfiles.contains(activeProfile);
        if (!contains) {
            activeProfiles.add(activeProfile);
        }
        for (String profile : activeProfiles) {
            result = multiConfigurablePropertyResolver.resolvePlaceholders(text, profile);
            if (null != result) {
                return result;
            }
        }
        return result;
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        String result = null;
        String activeProfile = multiConfigurablePropertyResolver.getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
        boolean contains = activeProfiles.contains(activeProfile);
        if (!contains) {
            activeProfiles.add(activeProfile);
        }
        for (String profile : activeProfiles) {
            result = multiConfigurablePropertyResolver.resolveRequiredPlaceholders(text, profile);
            if (null != result) {
                return result;
            }
        }
        return result;
    }


    @Override
    public String resolvePlaceholders(String text, String profile) {
        return multiConfigurablePropertyResolver.resolvePlaceholders(text, profile);
    }

    @Override
    public String resolveRequiredPlaceholders(String text, String profile) throws IllegalArgumentException {
        return multiConfigurablePropertyResolver.resolveRequiredPlaceholders(text, profile);
    }
}
