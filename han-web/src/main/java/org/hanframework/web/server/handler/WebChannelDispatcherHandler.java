package org.hanframework.web.server.handler;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.web.core.MappingRegistry;
import org.hanframework.web.server.channel.EnhanceChannel;


/**
 * web类服务分发器
 *
 * @author liuxin
 * @version Id: HttpCompleteFutureListener.java, v 0.1 2019-05-09 16:00
 */
@Slf4j
public final class WebChannelDispatcherHandler extends AbstractExecutorChannelDispatcher implements EnhanceChannelHandler {

    private static String KEY_READ_TIMESTAMP = "READ_TIMESTAMP";

    private static String KEY_WRITE_TIMESTAMP = "WRITE_TIMESTAMP";


    public WebChannelDispatcherHandler(MappingRegistry mappingRegistry) {
        super(mappingRegistry);
    }


    private void setReadTimestamp(EnhanceChannel channel) {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
    }

    private void setWriteTimestamp(EnhanceChannel channel) {
        channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
    }

    private void clearReadTimestamp(EnhanceChannel channel) {
        channel.removeAttribute(KEY_READ_TIMESTAMP);
    }

    private void clearWriteTimestamp(EnhanceChannel channel) {
        channel.removeAttribute(KEY_WRITE_TIMESTAMP);
    }

    @Override
    public void connected(EnhanceChannel channel) throws RemotingException {
        setReadTimestamp(channel);
        setWriteTimestamp(channel);
    }

    @Override
    public void disconnected(EnhanceChannel channel) throws RemotingException {
        clearReadTimestamp(channel);
        clearWriteTimestamp(channel);
        channel.disconnected();
    }

    @Override
    public void sent(EnhanceChannel channel, Object message) throws RemotingException {
        //不重写
    }


    @Override
    public void received(EnhanceChannel channel, Object message) throws RemotingException {
        //区分http和websocket
        setReadTimestamp(channel);
        dispatch(channel, message);
    }

    @Override
    public void caught(EnhanceChannel channel, Throwable exception) throws RemotingException {
        log.error(channel.toString(), exception);
        channel.disconnected();
    }

}
