package org.hanframework.web.condition;

import org.hanframework.web.handler.HttpRequestAnnotationMetaData;

/**
 * 客户端到服务端的请求参数条件验证
 * @author liuxin
 * @version Id: ParamsRequestCondition.java, v 0.1 2019-03-26 15:00
 */
public class ParamsRequestCondition implements RequestCondition {

    private HttpRequestAnnotationMetaData httpRequestAnnotationMetaData;

    public ParamsRequestCondition(HttpRequestAnnotationMetaData httpRequestAnnotationMetaData) {
        this.httpRequestAnnotationMetaData = httpRequestAnnotationMetaData;
    }

    @Override
    public boolean condition() {
        return false;
    }
}
