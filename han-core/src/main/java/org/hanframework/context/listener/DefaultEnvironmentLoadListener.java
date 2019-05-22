package org.hanframework.context.listener;

import org.hanframework.core.env.loadstrategy.PropertiesLoadStrategy;
import org.hanframework.core.env.loadstrategy.TypeLoadStrategy;
import org.hanframework.core.env.loadstrategy.YamlLoadStrategy;
import org.hanframework.core.env.resource.Resource;

import java.util.Properties;

/**
 * 默认只能解析Yaml yml 和Properties类型
 *
 * @author liuxin
 * @version Id: DefaultEnvironmentLoadListener.java, v 0.1 2018-12-12 14:45
 */
public class DefaultEnvironmentLoadListener extends EnvironmentLoadListener {

    private final String CHARSET = "utf8";

    @Override
    Properties loadPertiesForFileSuffixName(Resource resource) {
        return loadStrategy(resource).load(resource, CHARSET);
    }

    TypeLoadStrategy loadStrategy(Resource resource) {
        String fileSuffixName = resource.getFileSuffixName();
        if ("yaml".equalsIgnoreCase(fileSuffixName) || "yml".equalsIgnoreCase(fileSuffixName)) {
            return new YamlLoadStrategy();
        }
        return new PropertiesLoadStrategy();
    }
}
