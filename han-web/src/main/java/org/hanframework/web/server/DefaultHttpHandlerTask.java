package org.hanframework.web.server;

import org.hanframework.beans.beanfactory.BeanFactory;
import org.hanframework.web.handler.*;
import org.hanframework.web.http.*;
import org.hanframework.web.tool.MethodHandlerWrapper;

/**
 * 处理http请求
 *
 * @author liuxin
 * @version Id: HttpHandlerTask.java, v 0.1 2019-05-09 16:03
 */
public class DefaultHttpHandlerTask extends AbstractHttpHandlerTask<Response> {

    private final Request request;

    private final ResponseHandler responseHandler;


    public DefaultHttpHandlerTask(Request request, MethodHandlerWrapper methodHandlerWrapper, ResponseHandler responseHandler) {
        super(methodHandlerWrapper);
        this.request = request;
        this.responseHandler = responseHandler;
    }

    @Override
    public Response doCall(MethodHandlerWrapper handlerMethodWrapper) throws Exception {
        Response response;
        RequestMappingInfo requestMappingInfo = handlerMethodWrapper.getRequestMappingInfo();
        //TODO 请求信息校验
        if (handlerMethodWrapper.checkHandlerMethod()) {
            response = new Response(HttpResponseStatus.NOT_FOUND);
            return response;
        }
        BeanFactory beanFactory = handlerMethodWrapper.getBeanFactory();
        if (null == beanFactory) {
            throw new IllegalAccessException("MethodHandler 缺失创建类 BeanFactory.class");
        }
        //参数处理器,只会有一个
        AbstractArgsHandler argsHandler = beanFactory.getBean(AbstractArgsHandler.class);

        if (null == argsHandler) {
            throw new IllegalArgumentException("AbstractArgsHandler 转换器不存在");
        }
        //依赖接口不依赖实现,接口一般框架提供完整的实现
        Object[] args = argsHandler.args(request, handlerMethodWrapper.getValueHolders());

        handlerMethodWrapper.setArgs(args);
        Object result = handlerMethodWrapper.invoker();

        //返回值依赖接口不依赖实现
        return responseHandler.response(result, requestMappingInfo);
    }
}
