package org.hanframework.beans.beanfactory;

/**
 * @author liuxin
 * @version Id: NoSuchBeanException.java, v 0.1 2019-05-20 21:45
 */
public class NoSuchBeanException extends RuntimeException {
    public NoSuchBeanException() {
    }

    public NoSuchBeanException(String message) {
        super(message);
    }

    public NoSuchBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBeanException(Throwable cause) {
        super(cause);
    }

    public NoSuchBeanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
