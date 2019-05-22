package org.hanframework.web.handler;

import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.postprocessor.impl.MethodParameter;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author liuxin
 * @version Id: HandlerMethod.java, v 0.1 2019-03-26 16:28
 */
public class HandlerMethod {

    private final Object bean;

    private final BeanFactory beanFactory;

    private final Class<?> beanType;

    private final Method method;

    private final MethodParameter[] parameters;

    public HandlerMethod(Object bean, BeanFactory beanFactory, Class<?> beanType, Method method, MethodParameter[] parameters) {
        this.bean = bean;
        this.beanFactory = beanFactory;
        this.beanType = beanType;
        this.method = method;
        this.parameters = parameters;
    }

    public Object getBean() {
        return bean;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public Method getMethod() {
        return method;
    }

    public MethodParameter[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "HandlerMethod{" +
                "bean=" + bean +
                ", beanType=" + beanType +
                ", method=" + method +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
