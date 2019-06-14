package org.hanframework.tool.platform;

/**
 * @author liuxin
 * @version Id: EPlatform.java, v 0.1 2018/6/11 上午11:31 liuxin Exp $$
 */
public enum EPlatform {

    Linux("Linux"),

    Mac_OS("Mac OS"),

    Mac_OS_X("Mac OS X"),

    Windows("windows"),

    Others("Others");

    private String systemName;

    EPlatform(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
