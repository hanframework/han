package org.hanframework.cycle;

import org.hanframework.beans.annotation.Autowired;
import org.hanframework.beans.annotation.HanComponent;
import org.hanframework.beans.annotation.Lazy;

/**
 * @author liuxin
 * @version Id: A.java, v 0.1 2019-05-20 16:00
 */
@HanComponent
@Lazy(value = false)
public class A {
    private String name = "A";

    @Autowired
    private B b;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "A{" +
                "name='" + name + '\'' +
                ", b=" + b +
                '}';
    }
}
