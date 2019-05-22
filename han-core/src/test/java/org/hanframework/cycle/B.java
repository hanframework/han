package org.hanframework.cycle;

import org.hanframework.beans.annotation.Autowired;
import org.hanframework.beans.annotation.HanComponent;
import org.hanframework.beans.annotation.Lazy;

/**
 * @author liuxin
 * @version Id: B.java, v 0.1 2019-05-20 16:01
 */
@HanComponent
@Lazy(value = false)
public class B {
    private String name = "B";

    @Autowired
    private A a;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "B{" +
                "name='" + name + '\'' +
                ", a=" + a +
                '}';
    }
}
