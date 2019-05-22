package org.hanframework.web.tool;


/**
 * @author liuxin
 * @version Id: FutureCallBack.java, v 0.1 2019-05-09 20:40
 */
public interface FutureCallBack<V> {


  void onSuccess(V result);


  void onFailure(Throwable throwable);

}
