package org.hanframework.web.handler;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hanframework.web.condition.*;

/**
 * 请求的信息封装对象
 *
 * @author liuxin
 * @version Id: RequestMappingInfo.java, v 0.1 2019-03-26 14:59
 */
@Setter
@Builder
public class RequestMappingInfo {

    private final RequestMethodsRequestCondition requestMethodsRequestCondition;

    private final ParamsRequestCondition paramsCondition;

    private final HeadersRequestCondition headersCondition;

    private final ConsumesRequestCondition consumesCondition;
    @Getter
    private final ProducesRequestCondition producesCondition;

    public RequestMappingInfo(RequestMethodsRequestCondition requestMethodsRequestCondition, ParamsRequestCondition paramsCondition, HeadersRequestCondition headersCondition, ConsumesRequestCondition consumesCondition, ProducesRequestCondition producesCondition) {
        this.requestMethodsRequestCondition = requestMethodsRequestCondition;
        this.paramsCondition = paramsCondition;
        this.headersCondition = headersCondition;
        this.consumesCondition = consumesCondition;
        this.producesCondition = producesCondition;
    }

}
