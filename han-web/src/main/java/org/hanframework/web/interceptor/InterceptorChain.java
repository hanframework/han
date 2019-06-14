package org.hanframework.web.interceptor;

import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * @author liuxin
 * @version Id: InterceptorChain.java, v 0.1 2019-06-10 17:45
 */
public class InterceptorChain {

    private List<Interceptor> interceptors;

    @Setter
    private Target target;

    public void addInterceptor(Interceptor... interceptors) {
        Collections.addAll(this.interceptors, interceptors);
    }


    public void execute() throws Throwable {
        for (Interceptor interceptor : interceptors) {
            interceptor.intercept(null);
        }
        target.invoker();
    }
}
