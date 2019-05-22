package org.hanframework.web.annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lx
 */
public enum HttpMethod {
    /**
     * get
     */
    GET("get"),
    /**
     * post
     */
    POST("post"),
    /**
     * put
     */
    PUT("put");

    public String value;

    private static final Map<String, HttpMethod> caches = new HashMap<>(7);

    static {
        for (HttpMethod httpMethod : values()) {
            caches.put(httpMethod.value.toLowerCase(), httpMethod);
        }
    }

    HttpMethod(String method) {
        this.value = method;
    }

    public String vlaue() {
        return this.value;

    }

    public HttpMethod valueOf0(String value) {
        return caches.get(value.toLowerCase());
    }
}
