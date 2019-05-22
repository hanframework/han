package org.hanframework.tool.extension;

/**
 * @Package: org.smileframework.ioc.util
 * @Description: 命令行输出参数解析类
 * 可以将
 * java Main --server.active.profile=dev
 * 分解成
 * @author: liuxin
 * @date: 2017/12/7 下午6:34
 */
public class CommandLineArgsParser {

    /**
     * -- 开头的会保存起来并根据= 拆分
     * 没有--开头的
     * @param args
     * @return
     */
    public CommandLineArgs parse(String... args) {
        CommandLineArgs commandLineArgs = new CommandLineArgs();
        String[] var3 = args;
        int var4 = args.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String arg = var3[var5];
            if (arg.startsWith("--")) {
                String optionText = arg.substring(2, arg.length());
                String optionValue = null;
                String optionName;
                if (optionText.contains("=")) {
                    optionName = optionText.substring(0, optionText.indexOf("="));
                    optionValue = optionText.substring(optionText.indexOf("=") + 1, optionText.length());
                } else {
                    optionName = optionText;
                }

                if (optionName.isEmpty() || optionValue != null && optionValue.isEmpty()) {
                    throw new IllegalArgumentException("Invalid argument syntax: " + arg);
                }

                commandLineArgs.addOptionArg(optionName, optionValue);
            }
        }

        return commandLineArgs;
    }

}
