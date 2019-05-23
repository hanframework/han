package org.hanframework.env.postprocessor;

import org.hanframework.env.ConfigurableEnvironment;

/**
 * 配置文件读取策略
 * @author liuxin
 * @version Id: EnvironmentPostProcessor.java, v 0.1 2018-12-11 14:06
 */
public interface EnvironmentPostProcessor {
  void postProcessEnvironment(final ConfigurableEnvironment environment);
}
