package org.hanframework.web.http;

/**
 * @author liuxin
 * @version Id: SessionManager.java, v 0.1 2019-06-13 10:08
 */
public interface SessionManager {

    void reply(Request request);

    HttpSession newSession(String id);

    void clearSession(Request request);
}
