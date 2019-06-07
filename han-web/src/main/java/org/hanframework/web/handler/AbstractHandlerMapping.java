package org.hanframework.web.handler;

import org.hanframework.beans.beandefinition.BeanDefinition;
import org.hanframework.beans.beanfactory.lifecycle.InitializingBean;
import org.hanframework.context.ApplicationContext;
import org.hanframework.context.aware.ApplicationContextAware;
import org.hanframework.tool.annotation.type.AnnotationMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author liuxin
 * @version Id: AbstractHandlerMapping.java, v 0.1 2019-03-26 11:27
 */
public abstract class AbstractHandlerMapping<T> implements ApplicationContextAware, InitializingBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

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


    private void initHandlerMethods() {
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
     * @return List
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

    /**
     * 根据URL获取绑定的方法
     *
     * @param url 路径地址
     * @return HandlerMethod
     */
    public abstract HandlerMethod getHandlerMethod(URL url);


    /**
     * 请求信息的封装
     *
     * @param method 创建Mapping
     * @return 泛型
     */
    protected abstract T createRequestMappingInfo(Method method);

    /**
     * 是否是控制器
     *
     * @param annotationMetadata 注解原始数据
     * @return boolean
     */
    protected abstract boolean isHandler(AnnotationMetadata annotationMetadata);

    /**
     * 是否是控制方法
     *
     * @param method 方法
     * @return boolean
     */
    protected abstract boolean isHandlerMethod(Method method);

    /**
     * 是否是websocket
     *
     * @param method 当前方法
     * @return boolean
     */
    protected abstract boolean isWebSocket(Method method);


    /**
     * 将url与处理方法绑定
     *
     * @param handler 处理类
     * @param method  当前方法
     * @param url     路径
     */
    protected abstract void registerHandlerMethod(Object handler, Method method, URL url);
}
