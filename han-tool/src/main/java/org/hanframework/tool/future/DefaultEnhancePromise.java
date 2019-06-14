package org.hanframework.tool.future;

import io.netty.util.internal.ThrowableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * 凡是需要异步的操作都用通过该工具来实现
 * 1. setSuccess
 * 2. setFailure
 * 当以上两个方法调用,则说明状态从未决定到完成,则调用通知。
 *
 * @author liuxin
 * @version Id: DefaultHanFuture.java, v 0.1 2019-06-11 22:49
 */
public class DefaultEnhancePromise<V> extends AbstractEnhanceFuture<V> implements Promise<V> {

    private static Logger logger = LoggerFactory.getLogger(DefaultEnhancePromise.class);

    /**
     * 当前异步任务结果
     */
    private volatile Object result;

    /**
     * 是否已通知监听器
     */
    private boolean notifyingListeners;

    /**
     * 当异步操作完成,但结果为null,则使用默认值占位。标识已完成的状态
     */
    private static final Object SUCCESS = new Object();

    /**
     * 当操作被撤销时,使用当前默认值
     */
    private static final Object UNCANCELLABLE = new Object();

    /**
     * 原子更新volatile result
     */
    private static final AtomicReferenceFieldUpdater<DefaultEnhancePromise, Object> RESULT_UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(DefaultEnhancePromise.class, Object.class, "result");

    /**
     * 撤销对象
     */
    private static final CauseHolder CANCELLATION_CAUSE_HOLDER = new CauseHolder(ThrowableUtil.unknownStackTrace(
            new CancellationException(), DefaultEnhancePromise.class, "cancel(...)"));
    /**
     * 等待的任务数量
     */
    private short waiters;

    /**
     * 因为有多种监听器所以用Object,通过判断类型,找到对应的方法执行。
     * 因为监听器属于实例属性,每个任务都有自己的监听器。
     *
     * @see EnhanceFutureListener
     * @see DefaultEnhanceFutureListeners
     */
    private Object listeners;

    /**
     * 通知所有的监听器执行
     */
    private void notifyListeners() {
        notifyListenersNow();
    }

    /**
     * 当存在监听器则调用,否则不执行。
     * 因为监听器是实例属性,为保证线程安全使用同步代码块,以此来保证其他线程同时进来,导致重复通知
     */
    private void notifyListenersNow() {
        Object listeners;
        synchronized (this) {
            // Only proceed if there are listeners to notify and we are not already notifying listeners.
            if (notifyingListeners || this.listeners == null) {
                return;
            }
            notifyingListeners = true;
            listeners = this.listeners;
            this.listeners = null;
        }
        for (; ; ) {
            if (listeners instanceof DefaultEnhanceFutureListeners) {
                notifyListeners0((DefaultEnhanceFutureListeners) listeners);
            } else {
                notifyListener0(this, (EnhanceFutureListener<?>) listeners);
            }
            //执行完成手动清空监听器
            synchronized (this) {
                if (this.listeners == null) {
                    notifyingListeners = false;
                    return;
                }
                listeners = this.listeners;
                this.listeners = null;
            }
        }
    }

    private void notifyListeners0(DefaultEnhanceFutureListeners listeners) {
        EnhanceFutureListener<?>[] a = listeners.listeners();
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            notifyListener0(this, a[i]);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void notifyListener0(EnhanceFuture future, EnhanceFutureListener l) {
        try {
            if (future.isSuccess()) {
                l.onSuccess(future);
            } else {
                l.onThrowable(future.cause());
            }
        } catch (Throwable t) {
            if (logger.isWarnEnabled()) {
                logger.warn("An exception was thrown by " + l.getClass().getName() + ".operationComplete()", t);
            }
        }
    }

    @Override
    public EnhanceFuture<V> sync() throws InterruptedException {
        await();
        return this;
    }


    @Override
    public boolean isSuccess() {
        Object result = this.result;
        return result != null && result != UNCANCELLABLE && !(result instanceof CauseHolder);
    }

    @Override
    public boolean isCancellable() {
        return result == null;
    }

    @Override
    public Throwable cause() {
        Object result = this.result;
        return result instanceof CauseHolder ? ((CauseHolder) result).cause : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V getNow() {
        Object result = this.result;
        //如果是异常,或者默认值,则最终返回null
        if (result instanceof DefaultEnhancePromise.CauseHolder || result == SUCCESS || result == UNCANCELLABLE) {
            return null;
        }
        return (V) result;
    }

    @Override
    public EnhanceFuture<V> await() throws InterruptedException {
        if (isDone()) {
            return this;
        }

        if (Thread.interrupted()) {
            throw new InterruptedException(toString());
        }
        synchronized (this) {
            while (!isDone()) {
                incWaiters();
                try {
                    wait();
                } finally {
                    //线程执行到这里就,结束等待了,减少等待数量
                    decWaiters();
                }
            }
        }
        return this;
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return await0(unit.toNanos(timeout), true);
    }

    private boolean await0(long timeoutNanos, boolean interruptable) throws InterruptedException {
        if (isDone()) {
            return true;
        }

        if (timeoutNanos <= 0) {
            return isDone();
        }

        if (interruptable && Thread.interrupted()) {
            throw new InterruptedException(toString());
        }

        long startTime = System.nanoTime();
        long waitTime = timeoutNanos;
        boolean interrupted = false;
        try {
            for (; ; ) {
                synchronized (this) {
                    if (isDone()) {
                        return true;
                    }
                    incWaiters();
                    try {
                        wait(waitTime / 1000000, (int) (waitTime % 1000000));
                    } catch (InterruptedException e) {
                        if (interruptable) {
                            throw e;
                        } else {
                            interrupted = true;
                        }
                    } finally {
                        decWaiters();
                    }
                }
                if (isDone()) {
                    return true;
                } else {
                    waitTime = timeoutNanos - (System.nanoTime() - startTime);
                    if (waitTime <= 0) {
                        return isDone();
                    }
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public EnhanceFuture<V> addListener(DefaultEnhanceFutureListeners defaultEnhanceFutureListeners) {
        synchronized (this) {
            this.listeners = defaultEnhanceFutureListeners;
        }
        if (isDone()) {
            notifyListeners();
        }
        return this;
    }

    @Override
    public EnhanceFuture<V> addListener(EnhanceFutureListener hanFutureListener) {
        synchronized (this) {
            this.listeners = hanFutureListener;
        }
        if (isDone()) {
            notifyListeners();
        }
        return this;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (RESULT_UPDATER.compareAndSet(this, null, CANCELLATION_CAUSE_HOLDER)) {
            checkNotifyWaiters();
            notifyListeners();
            return true;
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled0(this.result);
    }

    @Override
    public boolean isDone() {
        return isDone0(this.result);
    }

    /**
     * 当异常为线程撤销异常则说明已撤销
     *
     * @param result 执行结果
     * @return boolean
     */
    private static boolean isCancelled0(Object result) {
        return result instanceof DefaultEnhancePromise.CauseHolder && ((DefaultEnhancePromise.CauseHolder) result).cause instanceof CancellationException;
    }


    private static boolean isDone0(Object result) {
        return result != null && result != UNCANCELLABLE;
    }

    @Override
    public Promise<V> setSuccess(V result) {
        if (setSuccess0(result)) {
            //第一次执行可能会因为还未添加监听器,导致跳过执行监听器模式。
            //当addListener时候会重新通知
            notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this);
    }

    /**
     * 设置默认值,当结果可以为null时候,默认放入SUCCESS,代表执行成功
     *
     * @param result 结果
     * @return boolean
     */
    private boolean setSuccess0(V result) {
        return setValue0(result == null ? SUCCESS : result);
    }


    /**
     * 如果结果被重新赋值,则唤醒当前等待的任务
     *
     * @param objResult 结果
     * @return 设置结果
     */
    private boolean setValue0(Object objResult) {
        if (RESULT_UPDATER.compareAndSet(this, null, objResult) ||
                RESULT_UPDATER.compareAndSet(this, UNCANCELLABLE, objResult)) {
            checkNotifyWaiters();
            return true;
        }
        return false;
    }

    private synchronized void checkNotifyWaiters() {
        if (waiters > 0) {
            notifyAll();
        }
    }

    @Override
    public Promise<V> setFailure(Throwable cause) {
        if (setFailure0(cause)) {
            notifyListeners();
            return this;
        }
        throw new IllegalStateException("complete already: " + this, cause);
    }

    private boolean setFailure0(Throwable cause) {
        return setValue0(new CauseHolder(cause));
    }

    /**
     * 每次等待时候调用,检查最大等待
     */
    private void incWaiters() {
        if (waiters == Short.MAX_VALUE) {
            throw new IllegalStateException("too many waiters: " + this);
        }
        ++waiters;
    }

    private void decWaiters() {
        --waiters;
    }

    private static final class CauseHolder {
        final Throwable cause;

        CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }

}
