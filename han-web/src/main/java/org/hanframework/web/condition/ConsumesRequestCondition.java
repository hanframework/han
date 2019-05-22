package org.hanframework.web.condition;

import org.hanframework.web.handler.HttpRequestAnnotationMetaData;

/**
 * 客户端到服务端的请求条件验证
 *
 * @author liuxin
 * @version Id: ConsumesRequestCondition.java, v 0.1 2019-03-26 15:01
 */
public class ConsumesRequestCondition implements RequestCondition {

    private HttpRequestAnnotationMetaData httpRequestAnnotationMetaData;

    public ConsumesRequestCondition(HttpRequestAnnotationMetaData httpRequestAnnotationMetaData) {
        this.httpRequestAnnotationMetaData = httpRequestAnnotationMetaData;
    }

    @Override
    public boolean condition() {
        return false;
    }
}
