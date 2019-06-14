package org.hanframework.web.interceptor;

/**
 * @author liuxin
 * @version Id: Target.java, v 0.1 2019-06-10 17:59
 */
public interface Target {
    Object invoker() throws Throwable;
}
