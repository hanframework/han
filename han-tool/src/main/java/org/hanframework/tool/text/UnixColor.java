package org.hanframework.tool.text;

import static org.hanframework.tool.text.Ansi.*;
import static org.hanframework.tool.text.Ansi.Color.*;

/**
 * @author liuxin
 * @version Id: UnixColor.java, v 0.1 2019-06-13 23:53
 */
public class UnixColor implements Color {

    /**
     * 红色
     *
     * @param text 文本
     */
    @Override
    public void red(String text) {
        print(text, RED);
    }

    /**
     * 黄色
     *
     * @param text 文本
     */
    @Override
    public void yellow(String text) {
        print(text, YELLOW);
    }

    /**
     * 蓝色
     *
     * @param text 文本
     */
    @Override
    public void blue(String text) {
        print(text, BLUE);
    }

    /**
     * 绿色
     *
     * @param text 文本
     */
    @Override
    public void green(String text) {
        print(text, GREEN);
    }

    /**
     * 青色
     *
     * @param text 文本
     */
    @Override
    public void cyan(String text) {
        print(text, CYAN);
    }

    /**
     * 品红色
     *
     * @param text 文本
     */
    @Override
    public void magenta(String text) {
        print(text, MAGENTA);
    }

    private void print(String text, Ansi.Color color) {
        System.out.println(ansi().eraseScreen().fg(color).a(text).reset());
    }


    public static void main(String[] args) {
        new UnixColor().blue("blue");
        new UnixColor().yellow("yellow");
        new UnixColor().green("green");
        new UnixColor().magenta("magenta");
        new UnixColor().cyan("cyan");
        new UnixColor().red("red");
    }
}
