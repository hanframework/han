package org.hanframework.web.http;

import org.hanframework.tool.future.EnhanceFuture;
import org.hanframework.tool.future.EnhanceFutureListener;
import org.hanframework.web.server.channel.EnhanceChannel;
import org.hanframework.web.websocket.WebSocketSession;

/**
 * 处理websocket请求结果
 *
 * @author liuxin
 * @version Id: WebSocketFutrueListener.java, v 0.1 2019-06-12 22:59
 */
public class DefaultHttpFutureListener implements EnhanceFutureListener<EnhanceFuture<Response>> {

    private final EnhanceChannel enhanceChannel;

    private final boolean close;

    public DefaultHttpFutureListener(EnhanceChannel enhanceChannel) {
        this(enhanceChannel, true);
    }

    public DefaultHttpFutureListener(EnhanceChannel enhanceChannel, boolean close) {
        this.enhanceChannel = enhanceChannel;
        this.close = close;
    }


    @Override
    public void onSuccess(EnhanceFuture<Response> future) throws Exception {
        if (close) {
            enhanceChannel.sendAndClose(future.getNow());
        } else {
            enhanceChannel.send(future.getNow());
        }
    }

    @Override
    public void onThrowable(Throwable throwable) throws Exception {
        enhanceChannel.exceptionCaught(throwable);
    }
}
