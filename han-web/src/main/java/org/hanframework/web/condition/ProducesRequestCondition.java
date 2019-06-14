package org.hanframework.web.condition;

import lombok.Getter;
import org.hanframework.web.handler.HttpRequestAnnotationMetaData;
import org.hanframework.web.http.HttpHeaders;

/**
 * 服务端到客户端的请求条件验证
 *
 * @author liuxin
 * @version Id: ProducesRequestCondition.java, v 0.1 2019-03-26 15:01
 */
public class ProducesRequestCondition implements RequestCondition {
    private HttpRequestAnnotationMetaData httpRequestAnnotationMetaData;

    @Getter
    private HttpHeaders httpHeaders;

    public ProducesRequestCondition(HttpRequestAnnotationMetaData httpRequestAnnotationMetaData) {
        this.httpRequestAnnotationMetaData = httpRequestAnnotationMetaData;
    }

    @Override
    public boolean condition() {
        return false;
    }
}
