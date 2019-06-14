package org.hanframework.web.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuxin
 * @version Id: StandardSessionManager.java, v 0.1 2019-06-13 10:22
 */
public class StandardSessionManager extends AbstractSessionManager {

    private final Object lock = new Object();

    private Map<String, HttpSession> sessionCache = new ConcurrentHashMap<>();

    @Override
    public HttpSession doReply(Request request) {
        String sessionId = request.getId();
        synchronized (lock) {
            HttpSession httpSession = sessionCache.get(sessionId);
            if (null == httpSession) {
                httpSession = newSession(sessionId);
            }
            return httpSession;
        }
    }

    @Override
    public HttpSession newSession(String id) {
        HttpSession standardSession = sessionCache.get(id);
        synchronized (lock) {
            if (null == standardSession) {
                standardSession = new StandardSession(id, System.currentTimeMillis());
                sessionCache.put(id, standardSession);
            }
        }
        return standardSession;
    }

    @Override
    public void clearSession(Request request) {
        String id = request.getId();
        sessionCache.remove(id);
    }
}
