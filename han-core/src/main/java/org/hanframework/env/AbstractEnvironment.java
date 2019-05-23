package org.hanframework.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hanframework.env.resolver.MultiConfigurablePropertyResolver;
import org.hanframework.env.resolver.PropertySourcesPropertyResolver;
import org.hanframework.env.resolver.source.MutablePropertySources;
import org.hanframework.tool.asserts.Assert;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author liuxin
 * @version Id: AbstractEnvironment.java, v 0.1 2018/10/16 7:29 PM
 */
public abstract class AbstractEnvironment implements ConfigurableEnvironment {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static final String RESERVED_DEFAULT_PROFILE_NAME = "default";

    /**
     * @see ConfigurableEnvironment#setActiveProfiles
     */
    public static final String ACTIVE_PROFILES_PROPERTY_NAME = "smile.profiles.active";

    /**
     * Name of property to set to specify profiles active by default: {@value}. Value may
     *
     * @see ConfigurableEnvironment#setDefaultProfiles
     */
    public static final String DEFAULT_PROFILES_PROPERTY_NAME = "smile.profiles.default";

    /**
     * 激活的配置文件信息
     */
    private Set<String> activeProfiles = new LinkedHashSet(3);

    /**
     * 默认的配置文件信息
     */
    private Set<String> defaultProfiles = new LinkedHashSet<String>(Collections.singleton(RESERVED_DEFAULT_PROFILE_NAME));

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
    public AbstractEnvironment() {
        customizePropertySources(this.propertySources);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(String.format(
                    "Initialized %s with PropertySources %s", getClass().getSimpleName(), this.propertySources));
        }
    }

    /**
     * 留给子类来实现
     *
     * @param propertySources
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
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSystemProperties() {
        return (Map) System.getProperties();

    }

    /**
     * 系统PATH信息
     *
     * @return
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
        for (String profile : profiles) {
            this.activeProfiles.add(profile);
        }
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
        return (String[]) activeProfiles.toArray();
    }


    protected Set<String> doGetActiveProfiles() {
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
        for (String profile : profiles) {
            this.defaultProfiles.add(profile);
        }
    }


    @Override
    public String[] getDefaultProfiles() {
        return (String[]) defaultProfiles.toArray();
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        return multiConfigurablePropertyResolver.getRequiredProperty(key);
    }

    @Override
    public boolean acceptsProfiles(String... profiles) {
        return false;
    }


    @Override
    public void merge(ConfigurableEnvironment parent) {

    }

    /**
     * 获取激活的
     * @param text
     * @return
     */
    @Override
    public String resolvePlaceholders(String text) {
        String result = null;
        String activeProfile = multiConfigurablePropertyResolver.getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
        if (null == activeProfile){
            activeProfile = RESERVED_DEFAULT_PROFILE_NAME;
        }
        boolean contains = activeProfiles.contains(activeProfile);
        if (!contains){
            activeProfiles.add(activeProfile);
        }
        for (String profile : activeProfiles){
            result = multiConfigurablePropertyResolver.resolvePlaceholders(text,profile);
            if (null!=result){
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
        if (!contains){
            activeProfiles.add(activeProfile);
        }
        for (String profile : activeProfiles){
            result = multiConfigurablePropertyResolver.resolveRequiredPlaceholders(text,profile);
            if (null!=result){
                return result;
            }
        }
        return result;
    }


    @Override
    public String resolvePlaceholders(String text, String profile) {
        return multiConfigurablePropertyResolver.resolvePlaceholders(text,profile);
    }

    @Override
    public String resolveRequiredPlaceholders(String text, String profile) throws IllegalArgumentException {
        return multiConfigurablePropertyResolver.resolveRequiredPlaceholders(text,profile);
    }
}
