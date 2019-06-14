package org.hanframework.tool.future;

/**
 * 任务在执行过程中有哪些状态?
 * 1. 完成
 * 2. 失败
 * 3. 执行中
 * 任务状态从未决 -> 到完成。
 * 如何知道完成呢?
 * 1. setSuccess 标识成功
 * 2. setFailure 标识失败
 *
 * @author liuxin
 * @version Id: Promise.java, v 0.1 2019-06-11 22:50
 */
public interface Promise<V> {
    /**
     * 设置成功标识
     *
     * @param result 结果
     * @return this
     */
    Promise<V> setSuccess(V result);

    /**
     * 设置失败标识
     *
     * @param cause 异常
     * @return this
     */
    Promise<V> setFailure(Throwable cause);
}
