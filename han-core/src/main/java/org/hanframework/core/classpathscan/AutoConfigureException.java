package org.hanframework.core.classpathscan;

/**
 * @author liuxin
 * @version Id: AutoConfigureException.java, v 0.1 2019-05-18 19:09
 */
public class AutoConfigureException extends RuntimeException {
    public AutoConfigureException() {
    }

    public AutoConfigureException(String message) {
        super(message);
    }

    public AutoConfigureException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutoConfigureException(Throwable cause) {
        super(cause);
    }

    public AutoConfigureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
