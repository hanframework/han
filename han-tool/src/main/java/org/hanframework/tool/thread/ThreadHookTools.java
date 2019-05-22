package org.hanframework.tool.thread;

/**
 * @author liuxin
 * @version Id: ThreadHookTools.java, v 0.1 2019-05-18 18:19
 */
public final class ThreadHookTools {

    public static void addHook(Thread hook) {
        Runtime.getRuntime().addShutdownHook(hook);
    }
}
