package org.hanframework.web.server;

import org.hanframework.web.tool.MethodHandlerWrapper;

import java.util.concurrent.Callable;

/**
 * @author liuxin
 * @version Id: AbstractHttpHandlerTask.java, v 0.1 2019-06-12 15:55
 */
public abstract class AbstractHttpHandlerTask<V> implements Callable<V> {

    private final MethodHandlerWrapper methodHandlerWrapper;

    public AbstractHttpHandlerTask(MethodHandlerWrapper methodHandlerWrapper) {
        this.methodHandlerWrapper = methodHandlerWrapper;
    }

    @Override
    public V call() throws Exception {
        //TODO 处理拦截器逻辑
        return doCall(methodHandlerWrapper);
    }


    public abstract V doCall(MethodHandlerWrapper methodHandlerWrapper) throws Exception;
}
