package org.hanframework.web.server.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import org.hanframework.tool.future.EnhanceExecutorService;
import org.hanframework.tool.future.EnhanceFuture;
import org.hanframework.tool.thread.HanThreadPoolExecutor;
import org.hanframework.tool.thread.NamedThreadFactory;
import org.hanframework.web.condition.ProducesRequestCondition;
import org.hanframework.web.core.MappingRegistry;
import org.hanframework.web.handler.RequestMappingInfo;
import org.hanframework.web.handler.URL;
import org.hanframework.web.http.*;
import org.hanframework.web.server.DefaultHttpHandlerTask;
import org.hanframework.web.server.DefaultWebSocketHandlerTask;
import org.hanframework.web.server.channel.EnhanceChannel;
import org.hanframework.web.tool.MethodHandlerWrapper;
import org.hanframework.web.view.ViewEngine;
import org.hanframework.web.view.ViewModel;
import org.hanframework.web.websocket.WebSocketConverter;
import org.hanframework.web.websocket.WebSocketFutrueListener;
import org.hanframework.web.websocket.WebSocketSession;

import java.util.Map;

/**
 * 根据请求分发逻辑
 *
 * @author liuxin
 * @version Id: AbstractChannelDispatcher.java, v 0.1 2019-06-12 23:10
 */
public abstract class AbstractExecutorChannelDispatcher implements ExecutorChannelDispatcher, WebServiceConverter {

    private EnhanceExecutorService enhanceExecutorService;

    private MappingRegistry mappingRegistry;

    private ViewEngine viewEngine;

    private SessionManager sessionManager;

    public AbstractExecutorChannelDispatcher(MappingRegistry mappingRegistry) {
        this(new EnhanceExecutorService(new HanThreadPoolExecutor(new NamedThreadFactory("han")).getExecutory()), mappingRegistry, null, new StandardSessionManager());
    }

    public AbstractExecutorChannelDispatcher(MappingRegistry mappingRegistry, ViewEngine viewEngine) {
        this(new EnhanceExecutorService(new HanThreadPoolExecutor(new NamedThreadFactory("han")).getExecutory()), mappingRegistry, viewEngine, new StandardSessionManager());
    }

    public AbstractExecutorChannelDispatcher(EnhanceExecutorService enhanceExecutorService, MappingRegistry mappingRegistry, ViewEngine viewEngine, SessionManager sessionManager) {
        this.enhanceExecutorService = enhanceExecutorService;
        this.mappingRegistry = mappingRegistry;
        this.viewEngine = viewEngine;
        this.sessionManager = sessionManager;
    }

    @Override
    public WebSocketSession buildWebSocketSession(EnhanceChannel channel, Object message) {
        return WebSocketConverter.buildWebSocketSession(message);
    }

    @Override
    public Request buildHttpRequest(EnhanceChannel channel, Object message) {
        return HttpConverter.buildHttpRequest(channel.getChannelId(), message);
    }

    private void replySession(Request request) {
        sessionManager.reply(request);
    }

    @Override
    public void dispatch(EnhanceChannel channel, Object message) {
        final FullHttpRequest req = (FullHttpRequest) message;
        //获取处理器
        MethodHandlerWrapper selector = mappingRegistry.selector(URL.valueOf(req.uri()));

        if (WebSocketConverter.match(message)) {
            WebSocketSession webSocketSession = buildWebSocketSession(channel, message);
            EnhanceFuture<WebSocketSession> enhanceFuture = enhanceExecutorService.submit(new DefaultWebSocketHandlerTask(webSocketSession, selector));
            enhanceFuture.addListener(new WebSocketFutrueListener(channel));
        } else if (HttpConverter.match(message)) {
            Request request = buildHttpRequest(channel, message);
            this.replySession(request);
            //视图渲染交给异步任务处理
            EnhanceFuture<Response> enhanceFuture = enhanceExecutorService.submit(new DefaultHttpHandlerTask(request, selector, processResult()));
            enhanceFuture.addListener(new DefaultHttpFutureListener(channel, keepAlive(request)));
        } else {
            throw new UnsupportedOperationException("Only support Response.class and  WebSocketSession.class,don't support:" + message.getClass());
        }
    }

    private boolean keepAlive(Request request) {
        HttpHeaders httpHeaders = request.getHttpHeaders();
        Object head = httpHeaders.getHead(HttpHeaders.Names.CONNECTION);
        if (head instanceof String) {
            return ((String) head).equalsIgnoreCase(HttpHeaders.Values.KEEP_ALIVE);
        }
        return false;
    }


    private ProcessResult processResult() {
        return new ProcessResult(new GoogleJsonEngine(), viewEngine);
    }


    static class ProcessResult implements ResponseHandler {

        private JsonEngine jsonHandler;

        private ViewEngine viewEngine;

        public ProcessResult(JsonEngine jsonHandler, ViewEngine viewEngine) {
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

        private String render(ViewModel viewModel) {
            return viewEngine.render(viewModel);
        }
    }
}
