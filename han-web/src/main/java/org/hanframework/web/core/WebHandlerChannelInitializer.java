package org.hanframework.web.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.hanframework.web.server.handler.HanChannelHandler;
import org.hanframework.web.server.handler.NettyWebServerHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author liuxin
 * @version Id: WebHandlerChannelInitializer.java, v 0.1 2019-04-16 20:38
 */
public abstract class WebHandlerChannelInitializer extends ChannelInitializer<Channel> {


    private final WebSocketPathManager webSocketPathManager = new WebSocketPathManager();

    private final HanChannelHandler hanChannelHandler;

    public WebHandlerChannelInitializer(HanChannelHandler hanChannelHandler) {
        this.hanChannelHandler = hanChannelHandler;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        processWebHttpHandler(pipeline);
        processWebSocketHandler(pipeline);
    }

    /**
     * 默认的http配置
     *
     * @param cp Channel执行通道
     */
    private void processWebHttpHandler(ChannelPipeline cp) {
        cp.addLast("idleStateHandler", new IdleStateHandler(5, 5, 5, TimeUnit.SECONDS));
        cp.addLast("http", new HttpServerCodec());
        cp.addLast("aggregator", new HttpObjectAggregator(1048576));
        cp.addLast("compress", new HttpContentCompressor());
        cp.addLast("http-chunked", new ChunkedWriteHandler());
        cp.addLast(new WebSocketServerProtocolHandler("/ws"));
        cp.addLast("handler", new NettyWebServerHandler(hanChannelHandler));
    }


    private void processWebSocketHandler(ChannelPipeline pipeline) {
        configureWebSocketPath(webSocketPathManager);
        webSocketPathManager.installWebSocketHandlers(pipeline);
    }

    /**
     * 配置websocket路径
     *
     * @param webSocketPath 配置
     */
    public abstract void configureWebSocketPath(final WebSocketPathManager webSocketPath);


    class WebSocketPathManager {

        private List<String> webSocketPathList = new ArrayList<>();

        private Object lock = new Object();


        public void addWebScoketPath(String path) {
            synchronized (lock) {
                webSocketPathList.add(path);
            }
        }

        private void installWebSocketHandlers(ChannelPipeline pipeline) {
            for (String webSocketPath : webSocketPathList) {
                pipeline.addLast(new WebSocketServerProtocolHandler(webSocketPath));
            }
        }
    }

}
