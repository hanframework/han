package org.hanframework.beans.exception;

/**
 * @author liuxin
 * @version Id: BeanInitDestroyException.java, v 0.1 2019-05-23 10:38
 */
public class BeanInitDestroyException extends RuntimeException {
    public BeanInitDestroyException() {
    }

    public BeanInitDestroyException(String message) {
        super(message);
    }

    public BeanInitDestroyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanInitDestroyException(Throwable cause) {
        super(cause);
    }

    public BeanInitDestroyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
