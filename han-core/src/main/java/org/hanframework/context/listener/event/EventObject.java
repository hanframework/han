package org.hanframework.context.listener.event;

/**
 * @author liuxin
 * @version Id: EventObject.java, v 0.1 2018-12-10 19:51
 */
public class EventObject<T> {

    protected transient T source;

    public EventObject(T source) {
        if (source == null) {
            throw new IllegalArgumentException("null source");
        }
        this.source = source;
    }

    public T getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
}
