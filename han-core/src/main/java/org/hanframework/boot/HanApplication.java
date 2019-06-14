package org.hanframework.boot;

import org.hanframework.context.AnnotationConfigApplicationContext;
import org.hanframework.context.ApplicationContext;
import org.hanframework.env.Environment;
import org.hanframework.tool.app.Banner;
import org.hanframework.tool.text.Color;
import org.hanframework.tool.text.UnixColor;

import java.io.PrintStream;

/**
 * @author liuxin
 * @version Id: HanApplication.java, v 0.1 2019-03-21 16:15
 */
public class HanApplication {

    private Class<?> primarySources;

    public HanApplication(Class<?> primarySources) {
        this.primarySources = primarySources;
    }

    public ApplicationContext run(String... args) {
        new PrintedBanner(this.primarySources).printBanner(null, null, Banner.banner);
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(this.primarySources);
        app.run(args);
        return app;
    }

    public static ApplicationContext run(Class<?> primarySource, String... args) {
        new PrintedBanner(primarySource).printBanner(null, null, Banner.banner);
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(primarySource);
        app.run(args);
        return app;
    }


    private static class PrintedBanner implements Banner {
        private final Class<?> sourceClass;
        private final Color color = new UnixColor();

        PrintedBanner(Class<?> sourceClass) {
            this.sourceClass = sourceClass;
        }

        @Override
        public void printBanner(Environment environment, Class<?> sourceClass, String banner) {
            color.yellow(banner);
            color.yellow("[Welcome to the Han framework]");
        }
    }
}
