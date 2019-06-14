package org.hanframework.tool.future;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author liuxin
 * @version Id: AbstractHanFuture.java, v 0.1 2019-06-11 22:46
 */
public abstract class AbstractEnhanceFuture<V> implements EnhanceFuture<V> {

    /**
     * 阻塞操作
     *
     * @return v
     * @throws InterruptedException 线程中断异常
     * @throws ExecutionException   线程执行异常
     */
    @Override
    public V get() throws InterruptedException, ExecutionException {
        await();
        Throwable cause = cause();
        if (cause == null) {
            return getNow();
        }
        if (cause instanceof CancellationException) {
            throw (CancellationException) cause;
        }
        throw new ExecutionException(cause);
    }

    /**
     * 带限定时间的阻塞
     *
     * @param timeout 超时时间
     * @param unit    超时时间单位
     * @return v
     * @throws InterruptedException 线程中断异常
     * @throws ExecutionException   执行异常
     * @throws TimeoutException     超时异常
     */
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (await(timeout, unit)) {
            Throwable cause = cause();
            if (cause == null) {
                return getNow();
            }
            if (cause instanceof CancellationException) {
                throw (CancellationException) cause;
            }
            throw new ExecutionException(cause);
        }
        throw new TimeoutException();
    }
}
