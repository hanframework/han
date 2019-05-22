package org.hanframework.beans.condition;

/**
 * @author liuxin
 * @version Id: ConditionException.java, v 0.1 2019-01-14 14:51
 */
public class BeanCreateConditionException extends RuntimeException{
    public BeanCreateConditionException(String message) {
        super(message);
    }
    public BeanCreateConditionException(String message, Throwable cause) {
        super(message, cause);
    }
}
