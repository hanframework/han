package org.hanframework.web.websocket;

import org.hanframework.tool.future.EnhanceFuture;
import org.hanframework.tool.future.EnhanceFutureListener;
import org.hanframework.web.server.channel.EnhanceChannel;

/**
 * 处理websocket请求结果
 *
 * @author liuxin
 * @version Id: WebSocketFutrueListener.java, v 0.1 2019-06-12 22:59
 */
public class WebSocketFutrueListener implements EnhanceFutureListener<EnhanceFuture<WebSocketSession>> {

    private final EnhanceChannel enhanceChannel;

    public WebSocketFutrueListener(final EnhanceChannel enhanceChannel) {
        this.enhanceChannel = enhanceChannel;
    }

    @Override
    public void onSuccess(EnhanceFuture<WebSocketSession> future) throws Exception {
        enhanceChannel.send(future.getNow());
    }

    @Override
    public void onThrowable(Throwable throwable) throws Exception {
        enhanceChannel.exceptionCaught(throwable);
    }
}
