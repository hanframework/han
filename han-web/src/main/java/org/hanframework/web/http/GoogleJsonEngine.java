package org.hanframework.web.http;

import com.google.gson.Gson;

/**
 * @author liuxin
 * @version Id: GoogleJsonEngine.java, v 0.1 2019-06-10 23:27
 */
public class GoogleJsonEngine implements JsonEngine {
    @Override
    public String json(Object result) {
        return new Gson().toJson(result);
    }
}
