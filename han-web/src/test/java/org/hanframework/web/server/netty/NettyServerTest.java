package org.hanframework.web.server.netty;

import org.hanframework.autoconfigure.EnableAutoConfiguration;
import org.hanframework.context.AnnotationConfigApplicationContext;
import org.hanframework.context.annotation.ComponentScan;
import org.hanframework.web.core.MappingRegistry;
import org.hanframework.web.handler.HandlerMethod;
import org.hanframework.web.handler.RequestMappingHandlerMapping;
import org.hanframework.web.handler.URL;
import org.hanframework.web.server.handler.HanHttpCompletableHandler;
import org.junit.Test;


/**
 * @author liuxin
 * @version Id: NettyServerTest.java, v 0.1 2019-03-27 11:10
 */
@ComponentScan
@EnableAutoConfiguration
public class NettyServerTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext();
        app.register(RequestMappingHandlerMapping.class, HttpControllerTest.class, NettyServer.class);
        app.run(args);
        RequestMappingHandlerMapping bean = app.getBean(RequestMappingHandlerMapping.class);
        URL matcher = bean.matcher("/zoo/api/liu/animal/");
        System.out.println(matcher);
        System.out.println(matcher.getUrlVariable("/zoo/api/liu/animal/"));
        System.out.println(matcher.getUrlVariable("/zoo/api/liu/animal/"));
        if (matcher.matcher("/zoo/api/liu/animal/")) {
            HandlerMethod handlerMethod = bean.getHandlerMethod(matcher);
        }
        app.getBean(NettyServer.class).afterPropertiesSet();
        while (true) ;
    }


    @Test
    public void test() {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(NettyServerTest.class);
        app.run();
        while (true) ;
    }
}
