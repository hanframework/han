package org.hanframework.web.core;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.beans.postprocessor.impl.MethodParameter;
import org.hanframework.context.ApplicationContext;
import org.hanframework.context.aware.ApplicationContextAware;
import org.hanframework.web.handler.HandlerMethod;
import org.hanframework.web.handler.URL;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author liuxin
 * @version Id: MappingRegistry.java, v 0.1 2019-04-16 19:51
 */
@Slf4j
public class MappingRegistry implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<URL, HandlerMethod> mappingLookup = new LinkedHashMap<>();

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public Set<URL> lookup() {
        return mappingLookup.keySet();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void register(URL url, Object handler, Method method) {
        this.readWriteLock.writeLock().lock();
        try {
            HandlerMethod handlerMethod = createHandlerMethod(handler, method);

            if (log.isInfoEnabled()) {
                log.info("Mapped \"" + url + "\" onto " + handlerMethod);
            }
            this.mappingLookup.put(url, handlerMethod);
        } finally {
            this.readWriteLock.writeLock().unlock();
        }
    }

    /**
     * ws协议和http协议都通过url查询
     *
     * @param url
     * @return
     */
    public HandlerMethod selector(URL url) {
        HandlerMethod handlerMethod;
        for (URL u : lookup()) {
            if (u.matcher(url.getPath())) {
                return mappingLookup.get(u);
            }
        }
        //TODO 没有匹配到的自动给他路由到一个方法，可用做路由异常统一处理类
        return null;
    }

    public Map<URL, HandlerMethod> getMappings() {
        return this.mappingLookup;
    }

    private HandlerMethod createHandlerMethod(Object handler, Method method) {
        MethodParameter[] methodParameters = new MethodParameter[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter methodParameter = MethodParameter.forMethodOrConstructor(method, i);
            methodParameters[i] = methodParameter;
        }
        return new HandlerMethod(handler, (BeanFactory) applicationContext.getBeanFactory(), handler.getClass(), method, methodParameters);
    }

}

