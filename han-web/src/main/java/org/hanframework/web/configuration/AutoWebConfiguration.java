package org.hanframework.web.configuration;

import org.hanframework.autoconfigure.Configuration;
import org.hanframework.beans.annotation.Autowired;
import org.hanframework.beans.annotation.HanBean;
import org.hanframework.web.core.DefaultWebHandlerChannelInitializer;
import org.hanframework.web.core.MappingRegistry;
import org.hanframework.web.handler.HttpArgsHandler;
import org.hanframework.web.handler.RequestMappingHandlerMapping;
import org.hanframework.web.server.handler.EnhanceChannelHandler;
import org.hanframework.web.server.handler.WebChannelDispatcherHandler;
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
    public WebChannelDispatcherHandler hanHttpCompletableHandler(@Autowired MappingRegistry mappingRegistry) {
        return new WebChannelDispatcherHandler(mappingRegistry);
    }

    @HanBean
    public RequestMappingHandlerMapping mappingHandlerMapping(@Autowired MappingRegistry mappingRegistry) {
        return new RequestMappingHandlerMapping(mappingRegistry);
    }

    @HanBean
    public EnhanceChannelHandler hanChannelHandler(@Autowired MappingRegistry mappingRegistry) {
        return new WebChannelDispatcherHandler(mappingRegistry);
    }

    @HanBean
    public NettyServer nettyServer(@Autowired EnhanceChannelHandler hanChannelHandler, @Autowired MappingRegistry mappingRegistry) {
        DefaultWebHandlerChannelInitializer defaultWebHandlerChannelInitializer = new DefaultWebHandlerChannelInitializer(hanChannelHandler, mappingRegistry);
        return new NettyServer(defaultWebHandlerChannelInitializer, 6969);
    }

    @HanBean
    public WebModelInitialize webModelInitialize() {
        return new WebModelInitialize();
    }

    @HanBean
    public HttpArgsHandler httpArgsHandler() {
        return new HttpArgsHandler();
    }

}


