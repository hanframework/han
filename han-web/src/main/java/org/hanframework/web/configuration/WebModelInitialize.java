package org.hanframework.web.configuration;

import org.hanframework.context.aware.ConfigurationAware;
import org.hanframework.env.Configuration;
import org.hanframework.env.ModuleConfiguration;

import java.util.Map;

/**
 * @author liuxin
 * @version Id: WebModelInitialize.java, v 0.1 2019-06-10 23:07
 */
public class WebModelInitialize implements ConfigurationAware {

    private Configuration configuration;

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void modelInitialize() {
        configuration.addModuleConfiguration(new WebModule("han-web", "1.0.0"));
    }


    class WebModule extends ModuleConfiguration {

        public WebModule(String moduleName, String version) {
            super(moduleName, version);
        }

        public WebModule(String moduleName, String version, Map source) {
            super(moduleName, version, source);
        }
    }
}
