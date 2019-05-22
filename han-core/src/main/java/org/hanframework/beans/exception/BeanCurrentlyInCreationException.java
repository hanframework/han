package org.hanframework.beans.exception;

/**
 * @author liuxin
 * @version Id: BeanCurrentlyInCreationException.java, v 0.1 2018/10/29 3:11 PM
 */
public class BeanCurrentlyInCreationException extends RuntimeException {
    /**
     * Create a new BeanCurrentlyInCreationException,
     * with a default error message that indicates a circular reference.
     *
     * @param beanName the name of the bean requested
     */
    public BeanCurrentlyInCreationException(String beanName) {
        super("BeanName:["+beanName +
                "] 包含循环引用的问题?");
    }

    /**
     * Create a new BeanCurrentlyInCreationException.
     *
     * @param beanName the name of the bean requested
     * @param msg      the detail message
     */
    public BeanCurrentlyInCreationException(String beanName, String msg) {
        super(beanName + ":" + msg);
    }
}
