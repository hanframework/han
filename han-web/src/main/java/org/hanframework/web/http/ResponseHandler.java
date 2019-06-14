package org.hanframework.web.http;

import org.hanframework.web.handler.RequestMappingInfo;

/**
 * @author liuxin
 * @version Id: ResponseHandler.java, v 0.1 2019-06-10 22:49
 */
public interface ResponseHandler {
    /**
     * 响应结果处理,当需要渲染视图也从中处理
     *
     * @param result             业务处理结果
     * @param requestMappingInfo 请求信息
     * @return 响应
     */
    Response response(Object result, RequestMappingInfo requestMappingInfo);

}
