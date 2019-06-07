package org.hanframework.context;

import org.hanframework.env.ConfigurableEnvironment;
import org.hanframework.env.loadstrategy.TypeLoadStrategy;
import org.hanframework.env.loadstrategy.TypeLoadStrategyRegister;
import org.hanframework.env.resolver.source.MutablePropertySources;
import org.hanframework.env.resolver.source.PropertiesPropertySource;
import org.hanframework.env.resource.Resource;
import org.hanframework.env.resource.impl.PathMatchingResourcePatternResolver;
import org.hanframework.tool.extension.CommandLineArgsParser;

import java.io.IOException;
import java.util.Properties;

/**
 * @author liuxin
 * @version Id: LoadEnvParseContext.java, v 0.1 2019-05-24 11:01
 */
public final class LoadEnvParseContext {

  private static final String COMMAND_LINE_ARGS = "commandLineArgs";
  /**
   * 默认扫描文件的路径
   */
  private static final String DEFAULT_SEARCH_LOCATIONS = "classpath:/,classpath:/config/";
  /**
   * 默认的配置名
   */
  private static final String DEFAULT_CONFIG_LOCATION = "application";

  private static final String PROFILE_NAME = "default";

  private final EnvParseContext envParseContext;

  private final ConfigurableEnvironment configurableEnvironment;

  public LoadEnvParseContext(EnvParseContext envParseContext, ConfigurableEnvironment configurableEnvironment) {
    this.envParseContext = envParseContext;
    this.configurableEnvironment = configurableEnvironment;
  }


  public void load() {
    CommandLineArgsParser commandLineArgsParser = new CommandLineArgsParser();
    Properties commandLineArgsProperties = commandLineArgsParser.parse(envParseContext.getArgs()).getProperties();
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
      profile = filename.substring(lastIndexOf + 1, resource.getFilename().lastIndexOf("."));
    }
    //根据后缀名获取解析器
    return new PropertiesPropertySource(profile, resource.getFilename(), loadPertiesForFileSuffixName(resource));
  }


  private Properties loadPertiesForFileSuffixName(Resource resource) {
    TypeLoadStrategyRegister typeLoadStrategyRegistory = envParseContext.getTypeLoadStrategyRegistory();
    TypeLoadStrategy typeLoadStrategy = typeLoadStrategyRegistory.getLoadStrategy(resource.getFileSuffixName());
    return typeLoadStrategy.load(resource, envParseContext.getCharset());
  }

}
