package org.hanframework.core.classpathscan;

/**
 * @author liuxin
 * @version Id: TypeFilter.java, v 0.1 2018/10/12 9:20 AM
 */
public interface TypeFilter {
    /**
     * true: 说明被匹配到
     * false: 说明需要被排除
     *
     * @param cls 当前类
     * @return 是否匹配
     */
    boolean match(Class cls);
}
