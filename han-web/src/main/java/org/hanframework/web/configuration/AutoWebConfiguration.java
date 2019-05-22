package org.hanframework.web.configuration;

import org.hanframework.autoconfigure.Configuration;
import org.hanframework.beans.annotation.HanBean;
import org.hanframework.web.core.DefaultWebHandlerChannelInitializer;
import org.hanframework.web.core.MappingRegistry;
import org.hanframework.web.handler.RequestMappingHandlerMapping;
import org.hanframework.web.server.handler.HanChannelHandler;
import org.hanframework.web.server.handler.HanHttpCompletableHandler;
import org.hanframework.web.server.netty.NettyServer;

/**
 * @author liuxin
 * @version Id: WebConfiguration.java, v 0.1 2019-05-20 20:50
 */
@Configuration
public class AutoWebConfiguration {

    @HanBean
    public MappingRegistry mappingRegistry() {
        return new MappingRegistry();
    }

    @HanBean
    public HanHttpCompletableHandler hanHttpCompletableHandler(MappingRegistry mappingRegistry) {
        return new HanHttpCompletableHandler(mappingRegistry);
    }

    @HanBean
    public RequestMappingHandlerMapping mappingHandlerMapping(MappingRegistry mappingRegistry) {
        return new RequestMappingHandlerMapping(mappingRegistry);
    }

    @HanBean
    public HanChannelHandler hanChannelHandler(MappingRegistry mappingRegistry) {
        return new HanHttpCompletableHandler(mappingRegistry);
    }


    @HanBean
    public NettyServer nettyServer(HanChannelHandler hanChannelHandler, MappingRegistry mappingRegistry) {
        DefaultWebHandlerChannelInitializer defaultWebHandlerChannelInitializer = new DefaultWebHandlerChannelInitializer(hanChannelHandler, mappingRegistry);
        return new NettyServer(defaultWebHandlerChannelInitializer);
    }
}


