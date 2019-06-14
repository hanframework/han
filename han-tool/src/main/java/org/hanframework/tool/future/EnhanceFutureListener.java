package org.hanframework.tool.future;

/**
 *
 * @author liuxin
 * @version Id: HanFutureListener.java, v 0.1 2019-06-11 18:18
 */
public interface EnhanceFutureListener<F extends EnhanceFuture<?>> {

    /**
     * 成功执行器
     *
     * @param future 增强异步对象
     * @throws Exception 未知
     */
    void onSuccess(F future) throws Exception;

    /**
     * 执行失败处理方法
     *
     * @param throwable 错误信息
     * @throws Exception 未知异常
     */
    void onThrowable(Throwable throwable) throws Exception;
}
