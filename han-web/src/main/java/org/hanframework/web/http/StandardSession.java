package org.hanframework.web.http;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author liuxin
 * @version Id: StandardSession.java, v 0.1 2019-06-13 09:44
 */
public class StandardSession implements HttpSession {
    /**
     * The collection of user data attributes associated with this Session.
     */
    protected ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    private String id;

    protected long creationTime = 0L;

    protected volatile long lastAccessedTime = creationTime;

    protected volatile long thisAccessedTime = creationTime;


    public StandardSession(String id,long time) {
        this.id = id;
        setCreationTime(time);
    }

    @Override
    public void setCreationTime(long time) {
        this.creationTime = time;
        this.lastAccessedTime = time;
        this.thisAccessedTime = time;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public String[] getAttributeNames() {
        return attributes.keySet().toArray(new String[]{});
    }
}
