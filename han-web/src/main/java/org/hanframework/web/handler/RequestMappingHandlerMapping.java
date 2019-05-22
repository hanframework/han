package org.hanframework.web.handler;

import static org.hanframework.tool.annotation.AnnotationTools.*;

import org.hanframework.beans.annotation.Autowired;
import org.hanframework.beans.annotation.HanComponent;
import org.hanframework.tool.annotation.AnnotationMap;
import org.hanframework.tool.annotation.AnnotationTools;
import org.hanframework.tool.annotation.type.AnnotationMetadata;
import org.hanframework.tool.string.StringTools;
import org.hanframework.web.annotation.RequestMapping;
import org.hanframework.web.annotation.RestController;
import org.hanframework.web.annotation.WebSocket;
import org.hanframework.web.condition.*;
import org.hanframework.web.core.MappingRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 如果是Netty就将请求路径转换成URL
 * 如果是Servlet就将请求路径也专户成URL
 * 然后在RequestMappingHandlerMapping的包装类中去match(URL url);
 * - 1. 如果通过Netty读取参数或者通过Servlet读取参数
 * - 2. 根据URL获取HandlerMethod的包装类,然后执行生成结果返回。
 * <p>
 * 问题!
 * - 1. Servlet和Netty如何做统一的抽象
 *
 * @author liuxin
 * @version Id: RequestMappingHandlerMapping.java, v 0.1 2019-03-26 17:22
 */
public class RequestMappingHandlerMapping extends AbstractHandlerMapping<RequestMappingInfo> {


    MappingRegistry registry;


    public RequestMappingHandlerMapping(MappingRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected boolean isHandler(AnnotationMetadata annotationMetadata) {
        return annotationMetadata.hasAnnotation(RestController.class);
    }

    @Override
    protected boolean isHandlerMethod(Method method) {
        List<Annotation> annotationFromMethod = getAnnotationFromMethod(method);
        return isCyclicContainsAnnotation(annotationFromMethod, RequestMapping.class);
    }

    @Override
    protected List<URL> buildURL(Method method) {
        List<URL> urls = new ArrayList<>();
        List<Annotation> annotationFromMethod = getAnnotationFromMethod(method);
        for (Annotation annotation : annotationFromMethod) {
            if (isCyclicContainsAnnotation(annotation, RequestMapping.class)) {
                AnnotationMap annotationAttributeAsMap = getAnnotationAttributeAsMap(annotation);
                String name = annotationAttributeAsMap.getString("name");
                if (StringTools.isNotNullOrEmpty(name)) {
                    urls.add(new URL(name));
                }
                String[] values = (String[]) annotationAttributeAsMap.get("value");
                Arrays.stream(values).filter(StringTools::isNotNullOrEmpty).forEach(x -> urls.add(new URL(x)));
                String[] paths = (String[]) annotationAttributeAsMap.get("path");
                Arrays.stream(paths).filter(StringTools::isNotNullOrEmpty).forEach(x -> urls.add(new URL(x)));
                return urls;
            }
        }
        throw new RuntimeException("URL 构建失败");
    }

    @Override
    protected RequestMappingInfo createRequestMappingInfo(Method method) {
        HttpRequestAnnotationMetaData httpRequestAnnotationMetaData = null;
        List<Annotation> annotationFromMethod = getAnnotationFromMethod(method);
        for (Annotation annotation : annotationFromMethod) {
            if (AnnotationTools.isCyclicContainsAnnotation(annotation, RequestMapping.class)) {
                httpRequestAnnotationMetaData = new HttpRequestAnnotationMetaData(annotation);
            }
        }
        if (null == httpRequestAnnotationMetaData) {
            throw new RuntimeException("HttpRequestAnnotationMetaData FAIL");
        }
        return RequestMappingInfo.builder()
                .consumesCondition(new ConsumesRequestCondition(httpRequestAnnotationMetaData))
                .headersCondition(new HeadersRequestCondition(httpRequestAnnotationMetaData))
                .paramsCondition(new ParamsRequestCondition(httpRequestAnnotationMetaData))
                .producesCondition(new ProducesRequestCondition(httpRequestAnnotationMetaData))
                .requestMethodsRequestCondition(new RequestMethodsRequestCondition(httpRequestAnnotationMetaData)).build();

    }

    @Override
    protected boolean isWebSocket(Method method) {
        List<Annotation> annotationFromMethod = getAnnotationFromMethod(method);
        return isCyclicContainsAnnotation(annotationFromMethod, WebSocket.class);
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, URL url) {
        this.registry.register(url, handler, method);
    }

    @Override
    public HandlerMethod getHandlerMethod(URL url) {
        return registry.selector(url);
    }
}
