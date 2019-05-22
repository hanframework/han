package org.hanframework.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beanfactory.lifecycle.InitializingBean;
import org.hanframework.context.ApplicationContext;
import org.hanframework.context.aware.ApplicationContextAware;
import org.hanframework.tool.annotation.type.AnnotationMetadata;
import org.hanframework.web.core.MappingRegistry;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author liuxin
 * @version Id: AbstractHandlerMapping.java, v 0.1 2019-03-26 11:27
 */
@Slf4j
public abstract class AbstractHandlerMapping<T> implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;


    private Map<URL, T> urlAndRequestMapping = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        initHandlerMethods();
    }


    protected void initHandlerMethods() {
        Map<String, BeanDefinition> beanDefinition = applicationContext.getBeanFactory().getBeanDefinition();
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : beanDefinition.entrySet()) {
            String beanKey = beanDefinitionEntry.getKey();
            BeanDefinition definitionEntryValue = beanDefinitionEntry.getValue();
            AnnotationMetadata annotationMetadata = definitionEntryValue.getAnnotationMetadata();
            if (isHandler(annotationMetadata)) {
                Class originClass = definitionEntryValue.getOriginClass();
                Method[] declaredMethods = originClass.getDeclaredMethods();
                for (Method method : declaredMethods) {
                    if (isHandlerMethod(method) | isWebSocket(method)) {
                        Object handler = applicationContext.getBean(beanKey);
                        Object requestMappingInfo = createRequestMappingInfo(method);
                        List<URL> urls = buildURL(method);
                        for (URL url : urls) {
                            registerHandlerMethod(handler, method, url);
                            urlAndRequestMapping.put(url, (T) requestMappingInfo);
                        }
                    }
                }
            }
        }
    }


    /**
     * 解析方法上路径生成URL
     *
     * @param method 被标记为HTTP处理方法的方法
     * @return
     */
    protected abstract List<URL> buildURL(Method method);


    public URL matcher(String path) {
        for (Map.Entry<URL, T> entries : urlAndRequestMapping.entrySet()) {
            boolean matcher = entries.getKey().matcher(path);
            if (matcher) {
                return entries.getKey();
            }
        }
        return null;
    }

    public abstract HandlerMethod getHandlerMethod(URL url);


    /**
     * 请求信息的封装
     *
     * @param method
     * @param <T>
     * @return
     */
    protected abstract <T> T createRequestMappingInfo(Method method);

    /**
     * 是否是控制器
     *
     * @param annotationMetadata
     * @return
     */
    protected abstract boolean isHandler(AnnotationMetadata annotationMetadata);

    /**
     * 是否是控制方法
     *
     * @param method
     * @return
     */
    protected abstract boolean isHandlerMethod(Method method);

    /**
     * 是否是websocket
     *
     * @param method
     * @return
     */
    protected abstract boolean isWebSocket(Method method);



    protected abstract void registerHandlerMethod(Object handler, Method method, URL url);
}
