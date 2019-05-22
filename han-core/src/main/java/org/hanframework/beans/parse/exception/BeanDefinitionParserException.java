package org.hanframework.beans.parse.exception;

/**
 * @author liuxin
 * @version Id: BeanDefinitionParserException.java, v 0.1 2019-01-31 14:54
 */
public class BeanDefinitionParserException extends RuntimeException {

    public BeanDefinitionParserException(String message) {
        super(message);
    }

    public BeanDefinitionParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanDefinitionParserException(Throwable cause) {
        super(cause);
    }

    public BeanDefinitionParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
