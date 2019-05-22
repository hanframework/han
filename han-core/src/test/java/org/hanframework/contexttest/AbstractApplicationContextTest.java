package org.hanframework.contexttest;

import org.hanframework.context.AnnotationConfigApplicationContext;
import org.hanframework.context.annotation.ComponentScan;
import org.junit.Test;


/**
 * @author liuxin
 * @version Id: AbstractApplicationContextTest.java, v 0.1 2019-05-20 13:55
 */
@ComponentScan
public class AbstractApplicationContextTest {

    @Test
    public void applicationTest() {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(AbstractApplicationContextTest.class);
        app.run();
        while (true) ;
    }


}



