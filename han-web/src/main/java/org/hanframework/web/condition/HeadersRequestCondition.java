package org.hanframework.web.condition;

import org.hanframework.web.handler.HttpRequestAnnotationMetaData;

/**
 * 客户端到服务端的请求头条件验证
 * @author liuxin
 * @version Id: HeadersRequestCondition.java, v 0.1 2019-03-26 15:00
 */
public class HeadersRequestCondition implements RequestCondition {
    private HttpRequestAnnotationMetaData httpRequestAnnotationMetaData;

    public HeadersRequestCondition(HttpRequestAnnotationMetaData httpRequestAnnotationMetaData) {
        this.httpRequestAnnotationMetaData = httpRequestAnnotationMetaData;
    }

    @Override
    public boolean condition() {
        return false;
    }
}
