package org.hanframework.tool.hook;

/**
 * @author liuxin
 * @version Id: HookTest.java, v 0.1 2019-05-18 18:21
 */
public class HookTest {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.err.println("ERROR");
            }
        }));


    }
}
