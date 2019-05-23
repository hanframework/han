package org.hanframework.env.resolver.source;

import java.util.Properties;

/**
 * @author liuxin
 * @version Id: PropertiesPropertySource.java, v 0.1 2018-12-07 17:30
 */
public class PropertiesPropertySource extends PropertySource<Properties> {

    Properties properties;

    public PropertiesPropertySource(String profile, String name, Properties source) {
        super(profile, name, source);
        this.properties = source;
    }

    public PropertiesPropertySource(String name, Properties source) {
        super("default", name, source);
        this.properties = source;
    }
    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }
}
