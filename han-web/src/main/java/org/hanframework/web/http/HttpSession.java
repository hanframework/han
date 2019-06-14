package org.hanframework.web.http;


/**
 * @author liuxin
 * @version Id: HttpSession.java, v 0.1 2019-06-13 09:41
 */
public interface HttpSession extends Session {
    long getCreationTime();

    String getId();

    long getLastAccessedTime();

    Object getAttribute(String name);

    void setAttribute(String name, Object value);

    void removeAttribute(String name);

    String[] getAttributeNames();
}
