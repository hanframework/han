package org.hanframework.tool.future;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 对远程Future进行增强测试
 *
 * @author liuxin
 * @version Id: AbstractEnhanceFutureTest.java, v 0.1 2019-06-12 10:33
 */
public class AbstractEnhanceFutureTest {


    /**
     * 通过包装
     */
    @Test
    public void enhanceFutureTest() {
        ExecutorService executorService = new EnhanceExecutorService(Executors.newSingleThreadExecutor());
        EnhanceFuture<Integer> submit = (EnhanceFuture<Integer>) executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                throw new IllegalAccessException("不服气");
            }
        });
        submit.addListener(new EnhanceFutureListener() {
            @Override
            public void onSuccess(EnhanceFuture future) throws Exception {
                System.out.println("Success:" + future.getNow());
            }

            @Override
            public void onThrowable(Throwable throwable) throws Exception {
                System.err.println("Error" + throwable);
            }
        });
    }

    /**
     * 测试使用多个监听器
     */
    @Test
    @SuppressWarnings("unchecked")
    public void enhanceFutureTest2() {
        DefaultEnhanceFutureListeners efl = new DefaultEnhanceFutureListeners();
        efl.add(new EnhanceFutureListener() {
            @Override
            public void onSuccess(EnhanceFuture future) throws Exception {
                System.out.println("Success1:" + future.getNow());
            }

            @Override
            public void onThrowable(Throwable throwable) throws Exception {
                System.err.println("Error1:" + throwable);
            }
        });

        efl.add(new EnhanceFutureListener() {
            @Override
            public void onSuccess(EnhanceFuture future) throws Exception {
                System.out.println("Success2:" + future.getNow());
            }

            @Override
            public void onThrowable(Throwable throwable) throws Exception {
                System.err.println("Error2:" + throwable);
            }
        });


        EnhanceExecutorService executorService = new EnhanceExecutorService(Executors.newSingleThreadExecutor());
        EnhanceFuture<Integer> submit = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        });
        submit.addListener(efl);
        submit.addListener(new EnhanceFutureListener() {
            @Override
            public void onSuccess(EnhanceFuture future) throws Exception {
                System.out.println("Success3:" + future.getNow());
            }

            @Override
            public void onThrowable(Throwable throwable) throws Exception {
                System.err.println("Error3:" + throwable);
            }
        });

    }


    @Test
    public void syncTest() {
        EnhanceExecutorService executorService = new EnhanceExecutorService(Executors.newSingleThreadExecutor());
        EnhanceFuture<?> enhanceFuture = executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    System.out.println("完成");
                }
            }
        });
        enhanceFuture.addListener(new EnhanceFutureListener() {
            @Override
            public void onSuccess(EnhanceFuture future) throws Exception {
                System.out.println("SUCCESS");
            }

            @Override
            public void onThrowable(Throwable throwable) throws Exception {
                System.err.println("ERROR");
            }
        });

        System.out.println("先执行");


    }


}