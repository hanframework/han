package org.hanframework.beans.exception;

/**
 * @author liuxin
 * @version Id: BeanInjectException.java, v 0.1 2019-05-23 11:11
 */
public class BeanInjectException extends RuntimeException{
    public BeanInjectException() {
    }

    public BeanInjectException(String message) {
        super(message);
    }

    public BeanInjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanInjectException(Throwable cause) {
        super(cause);
    }

    public BeanInjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
