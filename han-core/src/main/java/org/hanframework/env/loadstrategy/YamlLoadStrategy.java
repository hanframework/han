package org.hanframework.env.loadstrategy;

import org.hanframework.env.resource.Resource;
import org.hanframework.tool.string.StreamTools;
import org.hanframework.tool.yaml.YamlTools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author liuxin
 * @version Id: YamlLoadStrategy.java, v 0.1 2018-12-12 14:32
 */
public class YamlLoadStrategy implements TypeLoadStrategy {
    /**
     * @param resource 文件资源
     * @param charSet  资源字符集
     * @return properties
     */
    @Override
    public Properties load(Resource resource, String charSet) {
        try {
            InputStream inputStream = resource.getInputStream();
            String context = StreamTools.convertStreamToString(inputStream, charSet);
            return YamlTools.readYaml(context);
        } catch (IOException e) {
            throw new ConfigurationLoadException("YamlLoadStrategy:" + e.getMessage());
        }
    }
}
