package org.hanframework.web.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuxin
 * @version Id: HttpHeaders.java, v 0.1 2019-03-27 10:39
 */
public final class HttpHeaders {
    private final Map<String, Object> heads = new HashMap<>();

    public void set(String head, Object value) {
        this.heads.put(head, value);
    }

    public Object getHead(String head) {
        return heads.get(head);
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "heads=" + heads +
                '}';
    }
}
