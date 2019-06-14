package org.hanframework.tool.app;

import org.hanframework.env.Environment;

/**
 * @author liuxin
 * @version Id: Banner.java, v 0.1 2019-06-14 09:40
 */
@FunctionalInterface
public interface Banner {
    public String banner = "      ___           ___           ___     \n" +
            "     /\\__\\         /\\  \\         /\\__\\    \n" +
            "    /:/  /        /::\\  \\       /::|  |   \n" +
            "   /:/__/        /:/\\:\\  \\     /:|:|  |   \n" +
            "  /::\\  \\ ___   /::\\~\\:\\  \\   /:/|:|  |__ \n" +
            " /:/\\:\\  /\\__\\ /:/\\:\\ \\:\\__\\ /:/ |:| /\\__\\\n" +
            " \\/__\\:\\/:/  / \\/__\\:\\/:/  / \\/__|:|/:/  /\n" +
            "      \\::/  /       \\::/  /      |:/:/  / \n" +
            "      /:/  /        /:/  /       |::/  /  \n" +
            "     /:/  /        /:/  /        /:/  /   \n" +
            "     \\/__/         \\/__/         \\/__/    ";

    void printBanner(Environment environment, Class<?> sourceClass, String out);

    public static enum Mode {
        OFF,
        CONSOLE,
        LOG;

        private Mode() {
        }
    }
}
