package org.hanframework.core.env.loadstrategy;

/**
 * @author liuxin
 * @version Id: ConfigureLoadException.java, v 0.1 2019-05-18 19:48
 */
public class ConfigurationLoadException extends RuntimeException {
    public ConfigurationLoadException() {
    }

    public ConfigurationLoadException(String message) {
        super(message);
    }

    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationLoadException(Throwable cause) {
        super(cause);
    }

    public ConfigurationLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}