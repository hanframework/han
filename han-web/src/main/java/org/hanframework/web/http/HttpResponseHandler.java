package org.hanframework.web.http;

import org.hanframework.web.condition.ProducesRequestCondition;
import org.hanframework.web.handler.RequestMappingInfo;
import org.hanframework.web.view.ViewEngine;
import org.hanframework.web.view.ViewModel;

import java.util.Map;

/**
 * @author liuxin
 * @version Id: HttpResponseHandler.java, v 0.1 2019-06-10 22:54
 */
public class HttpResponseHandler implements ResponseHandler {

    private JsonEngine jsonHandler;

    private ViewEngine viewEngine;

    public HttpResponseHandler(JsonEngine jsonHandler, ViewEngine viewEngine) {
        this.jsonHandler = jsonHandler;
        this.viewEngine = viewEngine;
    }

    /**
     * 响应结果处理,当需要渲染视图也从中处理
     *
     * @param result 业务处理结果
     * @return 响应
     */
    @Override
    public Response response(Object result, RequestMappingInfo requestMappingInfo) {
        Response response;
        Class resultType = result.getClass();
        ProducesRequestCondition producesCondition = requestMappingInfo.getProducesCondition();
        HttpHeaders httpHeaders = producesCondition.getHttpHeaders();
        //判断返回结果
        if (resultType == String.class) {
            response = new Response(result, httpHeaders);
        } else if (resultType == Void.class) {
            response = new Response("");
        } else if (resultType == ViewModel.class) {
            response = new Response(render((ViewModel) result), httpHeaders);
        } else if (resultType == Map.class) {
            response = new Response(jsonHandler.json(result), httpHeaders);
        } else {
            response = new Response(result, httpHeaders);
        }
        return response;
    }

    public String render(ViewModel viewModel) {
       return viewEngine.render(viewModel);
    }
}
