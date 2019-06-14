package org.hanframework.web.http;

import org.hanframework.tool.common.Default;
import org.hanframework.tool.reflection.ReflectionTools;

import java.lang.reflect.Field;

/**
 * @author liuxin
 * @version Id: AbstractSessionManager.java, v 0.1 2019-06-13 10:16
 */
public abstract class AbstractSessionManager implements SessionManager {
    @Override
    public void reply(Request request) {
        HttpSession httpSession = Default.defaultValue(doReply(request), newSession(request.getId()), HttpSession.class);
        reply(request, httpSession);
    }

    private void reply(Request request, HttpSession httpSession) {
        try {
            Field session = Request.class.getDeclaredField("session");
            ReflectionTools.makeAccessible(session);
            session.set(request, httpSession);
        } catch (Exception e) {
            throw new SessionException(e);
        }
    }

    public abstract HttpSession doReply(Request request);

    @Override
    public abstract HttpSession newSession(String id);
}
