package org.hanframework.core.env.loadstrategy;

import org.hanframework.core.env.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author liuxin
 * @version Id: PropertiesLoadStrategy.java, v 0.1 2018-12-12 14:29
 */
public class PropertiesLoadStrategy implements TypeLoadStrategy {
    /**
     * @param resource 文件资源
     * @param charSet  资源字符集
     * @return properties
     */
    @Override
    public Properties load(Resource resource, String charSet) {
        Properties properties = null;
        try {
            InputStream inputStream = resource.getInputStream();
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, charSet));
            return properties;
        } catch (IOException e) {
            throw new ConfigurationLoadException("PropertiesLoadStrategy:" + e.getMessage());
        }

    }
}
