package org.hanframework.web.condition;

import org.hanframework.web.annotation.HttpMethod;
import org.hanframework.web.handler.HttpRequestAnnotationMetaData;

/**
 * 请求方法的校验
 *
 * @author liuxin
 * @version Id: RequestMethodsRequestCondition.java, v 0.1 2019-03-26 16:23
 */
public class RequestMethodsRequestCondition implements RequestCondition {


    private HttpMethod httpMethod;

    private HttpRequestAnnotationMetaData httpRequestAnnotationMetaData;

    public RequestMethodsRequestCondition(HttpRequestAnnotationMetaData httpRequestAnnotationMetaData) {
        this.httpRequestAnnotationMetaData = httpRequestAnnotationMetaData;
    }

    @Override
    public boolean condition() {
        return false;
    }
}
