package org.hanframework.context.listener;

import org.hanframework.context.listener.event.EnvironmentLoadEvent;
import org.hanframework.env.ConfigurableEnvironment;
import org.hanframework.env.postprocessor.EnvironmentPostProcessor;
import org.hanframework.env.resolver.source.MutablePropertySources;
import org.hanframework.env.resolver.source.PropertiesPropertySource;
import org.hanframework.env.resource.Resource;
import org.hanframework.env.resource.impl.PathMatchingResourcePatternResolver;
import org.hanframework.tool.extension.CommandLineArgsParser;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * 为什么使用了事件通知,有实现了Post后置处理
 * <p>
 * 以为先Bean加载前,要使用事件来获取先获取环境信息，这个时候post后置处理还没有执行。
 *
 * @author liuxin
 * @version Id: EnvironmentLoadListener.java, v 0.1 2018-12-11 14:26
 */
public abstract class EnvironmentLoadListener implements ApplicationListener<EnvironmentLoadEvent> {

    private static final String COMMAND_LINE_ARGS = "commandLineArgs";
    /**
     * 默认扫描文件的路径
     */
    private static final String DEFAULT_SEARCH_LOCATIONS = "classpath:/,classpath:/config/";
    /**
     * 激活的配置文件路径
     */
    public static final String ACTIVE_PROFILES_PROPERTY = "han.profiles.active";

    /**
     * 默认的配置名
     */
    private static final String DEFAULT_CONFIG_LOCATION = "application";

    private static final String PROFILE_NAME = "default";

    /**
     * location-${profile}.properties
     * location-${profile}.yaml
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(EnvironmentLoadEvent event) {
        ConfigurableEnvironment configurableEnvironment = event.getSource();
        CommandLineArgsParser commandLineArgsParser = new CommandLineArgsParser();
        Properties commandLineArgsProperties = commandLineArgsParser.parse(event.getArgs()).getProperties();
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        propertySources.add(new PropertiesPropertySource(COMMAND_LINE_ARGS, commandLineArgsProperties));
        PathMatchingResourcePatternResolver pmr = new PathMatchingResourcePatternResolver();
        try {
            for (String location : DEFAULT_SEARCH_LOCATIONS.split(",")) {
                String config = location + DEFAULT_CONFIG_LOCATION + "*";
                Resource[] resources = pmr.getResources(config);
                for (Resource resource : resources) {
                    propertySources.add(loadPropertiesPropertySource(resource));
                }
            }
        } catch (IOException e) {

        }
    }

    private PropertiesPropertySource loadPropertiesPropertySource(Resource resource) {
        String profile;
        String filename = resource.getFilename();
        int lastIndexOf = filename.lastIndexOf("-");
        if (lastIndexOf == -1) {
            profile = PROFILE_NAME;
        } else {
            String profileName = filename.substring(lastIndexOf + 1, resource.getFilename().lastIndexOf("."));
            profile = profileName;
        }
        return new PropertiesPropertySource(profile, resource.getFilename(), loadPertiesForFileSuffixName(resource));
    }

    /**
     * 根据文件后缀名来
     *
     * @return
     */
    abstract Properties loadPertiesForFileSuffixName(Resource resource);

}
