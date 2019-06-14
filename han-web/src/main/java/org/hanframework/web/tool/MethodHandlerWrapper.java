package org.hanframework.web.tool;

import lombok.Getter;
import lombok.Setter;
import org.hanframework.beans.beandefinition.ValueHolder;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.web.handler.MethodHandler;
import org.hanframework.web.handler.RequestMappingInfo;
import org.hanframework.web.interceptor.Target;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author liuxin
 * @version Id: HandlerMethodTools.java, v 0.1 2019-06-10 16:26
 */
public class MethodHandlerWrapper implements Target {

    @Getter
    private RequestMappingInfo requestMappingInfo;

    private MethodHandler handlerMethod;

    @Setter
    private Object[] args;


    public boolean checkHandlerMethod() {
        return handlerMethod == null;
    }

    public MethodHandlerWrapper(MethodHandler handlerMethod, RequestMappingInfo requestMappingInfo) {
        this.handlerMethod = handlerMethod;
        this.requestMappingInfo = requestMappingInfo;
    }

    public MethodHandlerWrapper(RequestMappingInfo requestMappingInfo, MethodHandler handlerMethod, Object[] args) {
        this.requestMappingInfo = requestMappingInfo;
        this.handlerMethod = handlerMethod;
        this.args = args;
    }


    public List<ValueHolder> getValueHolders() {
        return handlerMethod.getValueHolders();
    }

    @Override
    public Object invoker() throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Method method = handlerMethod.getMethod();
        return method.invoke(handlerMethod.getBean(), args);
    }

    public BeanFactory getBeanFactory(){
        return handlerMethod.getBeanFactory();
    }

    public Class<?> getReturnType() {
        return handlerMethod.getReturnType();
    }
}
