package org.hanframework.web.core;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.context.ApplicationContext;
import org.hanframework.context.ConfigurableApplicationContext;
import org.hanframework.context.aware.ApplicationContextAware;
import org.hanframework.web.handler.MethodHandler;
import org.hanframework.web.handler.RequestMappingInfo;
import org.hanframework.web.handler.URL;
import org.hanframework.web.tool.MethodHandlerWrapper;
import org.omg.PortableInterceptor.RequestInfo;

import javax.print.attribute.standard.RequestingUserName;
import java.lang.reflect.Method;
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

    /**
     * 执行方法
     */
    private final Map<URL, MethodHandlerWrapper> mappingLookup = new LinkedHashMap<>();

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public Set<URL> lookup() {
        return mappingLookup.keySet();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void register(URL url, Object handler, Method method, boolean view, RequestMappingInfo requestInfo) {
        this.readWriteLock.writeLock().lock();
        try {
            MethodHandler handlerMethod = createHandlerMethod(handler, method, view);

            if (log.isInfoEnabled()) {
                log.info("Mapped \"" + url + "\" onto " + handlerMethod);
            }
            this.mappingLookup.put(url, new MethodHandlerWrapper(handlerMethod, requestInfo));
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
    public MethodHandlerWrapper selector(URL url) {
        for (URL u : lookup()) {
            if (u.matcher(url.getPath())) {
                return mappingLookup.get(u);
            }
        }
        //TODO 没有匹配到的自动给他路由到一个方法，可用做路由异常统一处理类
        return null;
    }

    public Map<URL, MethodHandlerWrapper> getMappings() {
        return this.mappingLookup;
    }

    private MethodHandler createHandlerMethod(Object handler, Method method, boolean view) {
        return new MethodHandler(handler, (BeanFactory) ((ConfigurableApplicationContext) applicationContext).getBeanFactory(), method, view);
    }

}

