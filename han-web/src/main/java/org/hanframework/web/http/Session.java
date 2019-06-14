package org.hanframework.web.http;

/**
 * @author liuxin
 * @version Id: Session.java, v 0.1 2019-06-13 09:45
 */
public interface Session {

    void setCreationTime(long time);

    void setId(String id);
}
