package org.hanframework.web.server;

import org.hanframework.web.tool.MethodHandlerWrapper;
import org.hanframework.web.websocket.WebSocketSession;

/**
 * @author liuxin
 * @version Id: DefaultWebSocketHandlerTask.java, v 0.1 2019-06-12 16:04
 */
public class DefaultWebSocketHandlerTask extends AbstractHttpHandlerTask<WebSocketSession> {

    private final WebSocketSession webSocketSession;

    public DefaultWebSocketHandlerTask(WebSocketSession webSocketSession, MethodHandlerWrapper methodHandlerWrapper) {
        super(methodHandlerWrapper);
        this.webSocketSession = webSocketSession;
    }


    @Override
    public WebSocketSession doCall(MethodHandlerWrapper methodHandlerWrapper) throws Exception {
        methodHandlerWrapper.setArgs(new Object[]{webSocketSession});
        Object invoker = methodHandlerWrapper.invoker();
        if (invoker.getClass() != WebSocketSession.class) {
            throw new IllegalArgumentException();
        }
        return webSocketSession;
    }
}
