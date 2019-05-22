package org.hanframework.web.server.handler;

import lombok.extern.slf4j.Slf4j;
import org.hanframework.tool.thread.HanThreadPoolExecutor;
import org.hanframework.tool.thread.NamedThreadFactory;
import org.hanframework.web.core.MappingRegistry;
import org.hanframework.web.http.HttpConverter;
import org.hanframework.web.http.Request;
import org.hanframework.web.http.Response;
import org.hanframework.web.server.HttpHandlerTask;
import org.hanframework.web.server.channel.Channel;
import org.hanframework.web.tool.FutureCallBack;
import org.hanframework.web.tool.Futures;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * @author liuxin
 * @version Id: HttpCompleteFutureListener.java, v 0.1 2019-05-09 16:00
 */
@Slf4j
public final class HanHttpCompletableHandler implements HanChannelHandler {

    public static String KEY_READ_TIMESTAMP = "READ_TIMESTAMP";

    public static String KEY_WRITE_TIMESTAMP = "WRITE_TIMESTAMP";

    private ExecutorService executorService;

    private MappingRegistry mappingRegistry;


    public HanHttpCompletableHandler(MappingRegistry mappingRegistry) {
        this.executorService = new HanThreadPoolExecutor(new NamedThreadFactory("han-web")).getExecutory();
        this.mappingRegistry = mappingRegistry;
    }

    public HanHttpCompletableHandler(ExecutorService executorService, MappingRegistry mappingRegistry) {
        this.executorService = executorService;
        this.mappingRegistry = mappingRegistry;
    }

    private void setReadTimestamp(Channel channel) {
        channel.setAttribute(KEY_READ_TIMESTAMP, System.currentTimeMillis());
    }

    private void setWriteTimestamp(Channel channel) {
        channel.setAttribute(KEY_WRITE_TIMESTAMP, System.currentTimeMillis());
    }

    private void clearReadTimestamp(Channel channel) {
        channel.removeAttribute(KEY_READ_TIMESTAMP);
    }

    private void clearWriteTimestamp(Channel channel) {
        channel.removeAttribute(KEY_WRITE_TIMESTAMP);
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        setReadTimestamp(channel);
        setWriteTimestamp(channel);
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        clearReadTimestamp(channel);
        clearWriteTimestamp(channel);
        channel.disconnected();
    }

    @Override
    public void sent(Channel channel, Object message) throws RemotingException {
        //不重写
    }


    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        //区分http和websocket
        setReadTimestamp(channel);
        Request request = HttpConverter.buildHttpRequest(message);
        Future<Response> responseFuture = executorService.submit(new HttpHandlerTask(request, mappingRegistry));
        Futures.addCallback(responseFuture, new FutureCallBack<Response>() {
            @Override
            public void onSuccess(Response response) {
                setWriteTimestamp(channel);
                channel.send(response);
            }

            @Override
            public void onFailure(Throwable throwable) {
                channel.exceptionCaught(throwable);
                log.error(throwable.getMessage(), channel);
            }
        }, executorService);

    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {
        log.error(channel.toString(), exception);
        channel.disconnected();
    }

}
