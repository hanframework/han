package org.hanframework.context.aware;

import org.hanframework.env.Configuration;

/**
 * @author liuxin
 * @version Id: ConfigurationAware.java, v 0.1 2019-06-10 23:11
 */
public interface ConfigurationAware {
    void setConfiguration(Configuration configuration);
}
