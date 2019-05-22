package org.hanframework.boot;

import org.hanframework.context.AnnotationConfigApplicationContext;
import org.hanframework.context.ApplicationContext;

/**
 * @author liuxin
 * @version Id: HanApplication.java, v 0.1 2019-03-21 16:15
 */
public class HanApplication {

    public static ApplicationContext run(Class cls, String... args) {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(cls);
        app.run(args);
        return app;
    }

}
