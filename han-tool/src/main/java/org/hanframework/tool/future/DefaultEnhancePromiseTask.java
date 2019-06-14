package org.hanframework.tool.future;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;

/**
 * 增强了JDK自带Future,必须重新实现线程执行器。
 * @author liuxin
 * @version Id: DefaultEnhancePromiseTask.java, v 0.1 2019-06-12 11:06
 */
public class DefaultEnhancePromiseTask<V> extends DefaultEnhancePromise<V> implements RunnableFuture<V> {

    protected final Callable<V> task;

    public DefaultEnhancePromiseTask(Callable<V> task) {
        this.task = task;
    }

    public DefaultEnhancePromiseTask(Runnable task, V result) {
        this.task = Executors.callable(task, result);
    }

    /**
     * 判断是否被撤销,如果撤销就不执行
     */
    @Override
    public void run() {
        try {
            if (!isCancelled()) {
                V result = task.call();
                setSuccess(result);
            }
        } catch (Throwable e) {
            setFailure(e);
        }
    }
}
