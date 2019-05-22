package org.hanframework.tool.yaml;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Properties;

/**
 * @Package: org.smileframework.tool.properties
 * @Description: yaml文件操作
 * @author: liuxin
 * @date: 2017/12/19 上午11:40
 */
public final class YamlTools {

    public static String readJson(String json) {
        return new Yaml().dump(json);
    }

    public static Map readMap(String yamlContent) {
        return new Yaml().loadAs(yamlContent, Map.class);
    }

    public static Properties readYaml(String yamlContent) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(yamlContent, Properties.class);
    }

}