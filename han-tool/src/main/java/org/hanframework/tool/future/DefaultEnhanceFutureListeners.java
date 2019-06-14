package org.hanframework.tool.future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author liuxin
 * @version Id: HanFutureListeners.java, v 0.1 2019-06-11 22:57
 */
public class DefaultEnhanceFutureListeners {

    private List<EnhanceFutureListener> listeners;

    @SuppressWarnings("all")
    public DefaultEnhanceFutureListeners() {
        this.listeners = new ArrayList<>(4);
    }

    @SuppressWarnings("unused")
    public DefaultEnhanceFutureListeners(EnhanceFutureListener[] listeners) {
        this.listeners = Arrays.asList(listeners);
    }

    @SuppressWarnings("all")
    public void add(EnhanceFutureListener<? extends EnhanceFuture<?>> l) {
        listeners.add(l);
    }

    @SuppressWarnings("unused")
    public void remove(EnhanceFutureListener<? extends EnhanceFuture<?>> l) {
        listeners.remove(l);
    }

    @SuppressWarnings("all")
    public EnhanceFutureListener<? extends EnhanceFuture<?>>[] listeners() {
        return listeners.toArray(new EnhanceFutureListener[]{});
    }

    @SuppressWarnings("all")
    public int size() {
        return listeners.size();
    }
}
