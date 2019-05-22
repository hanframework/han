package org.hanframework.beans.beandefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * 构造参数信息
 *
 * @author liuxin
 * @version Id: ConstructorArgumentValues.java, v 0.1 2018/10/18 4:24 PM
 */
public class ConstructorArgumentValues {

    private List<ValueHolder> constructorArgumentValues = new ArrayList<>();

    public boolean addConstructorArgumentValue(ValueHolder constructorArgumentValue) {
        return this.constructorArgumentValues.add(constructorArgumentValue);
    }

    public List<ValueHolder> getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }

    @Override
    public String toString() {
        return "ConstructorArgumentValues{" +
                "constructorArgumentValues=" + constructorArgumentValues +
                '}';
    }
}
