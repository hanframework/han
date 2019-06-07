package org.hanframework.context.listener.event;

import org.hanframework.env.ConfigurableEnvironment;

/**
 * @author liuxin
 * @version Id: EnvironmentLoadEvent.java, v 0.1 2018-12-11 14:09
 */
@Deprecated
public class EnvironmentLoadEvent extends ApplicationEvent<ConfigurableEnvironment> {

    private String[] args;

    public EnvironmentLoadEvent(ConfigurableEnvironment configurableEnvironment, String[] args) {
        super(configurableEnvironment);
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }
}
