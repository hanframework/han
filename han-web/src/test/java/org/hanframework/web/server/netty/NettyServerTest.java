package org.hanframework.web.server.netty;

import org.hanframework.boot.HanApplication;
import org.hanframework.boot.HanBootstrapClass;
import org.hanframework.context.AnnotationConfigApplicationContext;
import org.hanframework.context.ApplicationContext;
import org.junit.Test;


/**
 * @author liuxin
 * @version Id: NettyServerTest.java, v 0.1 2019-03-27 11:10
 */
@HanBootstrapClass
public class NettyServerTest {
    public static void main(String[] args) {
        ApplicationContext app = HanApplication.run(NettyServerTest.class, args);
        while (true) ;
    }


    @Test
    public void test() {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(NettyServerTest.class);
        app.run();
        while (true) ;
    }
}
