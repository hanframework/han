package org.hanframework.tool.future;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 在继承原由能力基础之上,对jdk原生异步接口进行增强处理
 *
 * @author liuxin
 * @version Id: HanFuture.java, v 0.1 2019-06-11 18:16
 */
public interface EnhanceFuture<V> extends Future<V> {

    /**
     * 成功标识
     *
     * @return boolean
     */
    boolean isSuccess();

    /**
     * 是否可删除
     *
     * @return boolean
     */
    boolean isCancellable();

    /**
     * 异常信息
     *
     * @return 异常
     */
    Throwable cause();

    /**
     * 结果
     *
     * @return v
     */
    V getNow();

    /**
     * 线程等待
     *
     * @return 增强的Future
     * @throws InterruptedException 线程中断异常
     */
    EnhanceFuture<V> await() throws InterruptedException;

    /**
     * 限制等待时间
     *
     * @param timeout 时间
     * @param unit    单位
     * @return 增强的Future
     * @throws InterruptedException 线程中断异常
     */
    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * 阻塞到执行完毕
     *
     * @return 增强的Future
     * @throws InterruptedException 线程中断异常
     */
    EnhanceFuture<V> sync() throws InterruptedException;


    /*-------------------以下两种方法可同时使用-------------------*/

    /**
     * 添加异步监听器
     * 当任务执行完成会调用已添加的监听器
     *
     * @param enhanceFutureListener 增强监听器
     * @return 增强的Future
     */
    EnhanceFuture<V> addListener(EnhanceFutureListener enhanceFutureListener);

    /**
     * 添加异步监听器
     * 当任务执行完成会调用已添加的监听器
     *
     * @param enhanceFutureListeners 监听器集合
     * @return 增强的Future
     */
    EnhanceFuture<V> addListener(DefaultEnhanceFutureListeners enhanceFutureListeners);
}
