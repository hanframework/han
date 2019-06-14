package org.hanframework.web.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hanframework.beans.beandefinition.ValueHolder;
import org.hanframework.beans.beandefinition.ValueHolderFactory;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.postprocessor.impl.MethodParameter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * web服务和rpc服务都通过该类实现
 * <p>
 * RPC: 根据URL生成代理实例
 *
 * @author liuxin
 * @version Id: HandlerMethod.java, v 0.1 2019-03-26 16:28
 */
@ToString
public class MethodHandler {

    @Getter
    private final Object bean;

    @Getter
    private final BeanFactory beanFactory;

    @Getter
    private final Class<?> beanType;

    @Getter
    private final Method method;

    @Getter
    private final MethodParameter[] parameters;

    @Getter
    private final List<ValueHolder> valueHolders;

    @Setter
    @Getter
    private boolean view;

    @Setter
    @Getter
    private boolean remote;


    public MethodHandler(Object bean, BeanFactory beanFactory, Method method) {
        this(bean, beanFactory, bean.getClass(), method, createMethodParameter(method), false, false);
    }

    public MethodHandler(Object bean, BeanFactory beanFactory, Method method, boolean view) {
        this(bean, beanFactory, bean.getClass(), method, createMethodParameter(method), view, false);
    }

    public MethodHandler(Object bean, BeanFactory beanFactory, Class<?> beanType, Method method, MethodParameter[] parameters, boolean view, boolean remote) {
        this.bean = bean;
        this.beanFactory = beanFactory;
        this.beanType = beanType;
        this.method = method;
        this.parameters = parameters;
        this.valueHolders = ValueHolderFactory.valueHolder(method);
        this.view = view;
        this.remote = remote;
    }

    private static MethodParameter[] createMethodParameter(Method method) {
        MethodParameter[] methodParameters = new MethodParameter[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter methodParameter = MethodParameter.forMethodOrConstructor(method, i);
            methodParameters[i] = methodParameter;
        }
        return methodParameters;
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    @Override
    public String toString() {
        return "HandlerMethod{" +
                "bean=" + bean +
                ", method=" + method.getName() +
                '}';
    }
}
