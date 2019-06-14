package org.hanframework.tool.future;

import java.util.List;
import java.util.concurrent.*;

/**
 * 如何重新定义线程执行器,其实在JDK抽象执行器中已经给出了实例代码。
 * 只需要实现{RunnableFuture}接口。重写{newTaskFor}方法,即可。
 * 需要注意一点的是,Runnable会被适配成Callable一样,这样做的目的
 * 是为了是他们能享用同一套代码逻辑
 * <pre> {@code
 * public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
 *
 *     static class CustomTask<V> implements RunnableFuture<V> {...}
 *
 *     protected <V> RunnableFuture<V> newTaskFor(Callable<V> c) {
 *         return new CustomTask<V>(c);
 *     }
 *     protected <V> RunnableFuture<V> newTaskFor(Runnable r, V v) {
 *         return new CustomTask<V>(r, v);
 *     }
 *     // ... add constructors, etc.
 * }}</pre>
 *
 * @author liuxin
 * @version Id: EnhanceExecutorService.java, v 0.1 2019-06-12 10:55
 * @see AbstractExecutorService
 * @see DefaultEnhancePromiseTask
 */
public class EnhanceExecutorService extends AbstractExecutorService {

    private ExecutorService executorService;

    public EnhanceExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new DefaultEnhancePromiseTask<>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new DefaultEnhancePromiseTask<>(callable);
    }

    @Override
    public <T> EnhanceFuture<T> submit(Callable<T> task) {
        return (EnhanceFuture<T>) super.submit(task);
    }


    @Override
    public EnhanceFuture<?> submit(Runnable task) {
        return (EnhanceFuture<?>) super.submit(task);
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    /**
     * 如何解决线程池ThreadLocal的问题
     * 一般使用ThreadLocal
     * 父子线程使用InheritableThreadLocal
     *
     * @param command 运行命令
     * @see ThreadLocal
     * @see InheritableThreadLocal
     */
    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }
}
