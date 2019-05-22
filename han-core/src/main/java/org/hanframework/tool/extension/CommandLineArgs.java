package org.hanframework.tool.extension;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

/**
 * @Package: org.smileframework.ioc.bean.context
 * @Description: 命令行参数
 * @author: liuxin
 * @date: 2017/12/7 下午6:37
 */
public class CommandLineArgs {

    private final Properties properties = new Properties();


    public CommandLineArgs() {
    }

    public void addOptionArg(String optionName, String optionValue) {
        if (!this.properties.containsKey(optionName)) {
            this.properties.put(optionName, optionValue);
        }

        if (optionValue == null) {
            this.properties.put(optionName, "");
        }

    }

    public Properties getProperties() {
        return properties;
    }

    public Set<Object> getOptionNames() {
        return Collections.unmodifiableSet(this.properties.keySet());
    }

    public boolean containsOption(String optionName) {
        return this.properties.containsKey(optionName);
    }

    public Object getOptionValue(String optionName) {
        return this.properties.get(optionName);
    }

}
