package org.hanframework.web.interceptor;

import org.hanframework.web.handler.Invocation;

/**
 * 拦截器拦截的是方法的执行
 * <p>
 * 过滤器是拦截Request的执行
 * method拦截器和全局拦截器
 * 全局拦截器等于方法拦截器
 * 方法拦截器单独
 *
 * @author liuxin
 * @version Id: Interceptor.java, v 0.1 2019-06-10 17:25
 */
public interface Interceptor {
    void intercept(Invocation invocation) throws Throwable;
}
