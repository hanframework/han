package org.hanframework.beans.exception;

/**
 * @author liuxin
 * @version Id: BeanCreationException.java, v 0.1 2018/10/30 10:11 AM
 */
public class BeanCreationException extends RuntimeException {
    public BeanCreationException(String beanName, String msg) {
        super(beanName + ":" + msg);
    }

    public BeanCreationException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public BeanCreationException(String beanName, String msg, Throwable throwable) {

        super(beanName + ":" + msg, throwable);
    }

    public BeanCreationException() {
    }

    public BeanCreationException(String message) {
        super(message + ": 创建异常");
    }


    public BeanCreationException(Throwable cause) {
        super(cause);
    }

    public BeanCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
