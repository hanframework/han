package org.hanframework.web.tool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/**
 * @author liuxin
 * @version Id: Futures.java, v 0.1 2019-05-09 20:45
 */
public class Futures {


  public static <V> void addCallback(Future<V> future, FutureCallBack<V> callback, Executor executor) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        V value;
        try {
          //block wait
          value = future.get();
        } catch (ExecutionException ee) {
          callback.onFailure(ee);
          return;
        } catch (InterruptedException ie) {
          callback.onFailure(ie);
          return;
        } catch (RuntimeException re) {
          callback.onFailure(re);
          return;
        } catch (Error err) {
          callback.onFailure(err);
          return;
        }
        callback.onSuccess(value);
      }
    });
  }




}
