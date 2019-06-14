package org.hanframework.web.handler;

import org.hanframework.beans.beandefinition.ValueHolder;

import java.util.List;

/**
 * @author liuxin
 * @version Id: ArgsHandler.java, v 0.1 2019-06-10 22:19
 */
public interface ArgsHandler<T> {
    Object[] args(T t, ValueHolder... valueHolders);

    Object[] args(T t, List<ValueHolder> valueHolders);
}
