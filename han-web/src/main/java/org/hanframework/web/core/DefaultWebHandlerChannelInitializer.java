package org.hanframework.web.core;

import org.hanframework.web.handler.URL;
import org.hanframework.web.server.handler.EnhanceChannelHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuxin
 * @version Id: DefaultWebHandlerChannelInitializer.java, v 0.1 2019-05-10 18:07
 */
public class DefaultWebHandlerChannelInitializer extends WebHandlerChannelInitializer {


    private MappingRegistry mappingRegistry;


    public DefaultWebHandlerChannelInitializer(EnhanceChannelHandler hanChannelHandler, MappingRegistry mappingRegistry) {
        super(hanChannelHandler);
        this.mappingRegistry = mappingRegistry;
    }

    public List<URL> webSocket() {
        return mappingRegistry.lookup().stream().filter(x -> x.getProtocol().equalsIgnoreCase("ws")).collect(Collectors.toList());
    }

    @Override
    public void configureWebSocketPath(WebSocketPathManager webSocketPath) {
        for (URL url : webSocket()) {
            webSocketPath.addWebScoketPath(url.getPath());
        }
    }

}
