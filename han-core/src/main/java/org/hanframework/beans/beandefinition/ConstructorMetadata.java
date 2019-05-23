package org.hanframework.beans.beandefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author liuxin
 * @version Id: ConstructorMetadata.java, v 0.1 2018-12-04 17:24
 */
public class ConstructorMetadata {
    /**
     * 构造的参数信息
     */
    private ConstructorArgumentValues constructorArgumentValues;
    /**
     * 构造上注解
     */
    private List<Annotation> constructorAnnotations;
    /**
     * 原始构造
     */
    private Constructor originalConstructor;


    public ConstructorArgumentValues getConstructorArguments() {
        return constructorArgumentValues;
    }


    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    public List<Annotation> getConstructorAnnotations() {
        return constructorAnnotations;
    }

    public void setConstructorAnnotations(List<Annotation> constructorAnnotations) {
        this.constructorAnnotations = constructorAnnotations;
    }

    public Constructor getOriginalConstructor() {
        return originalConstructor;
    }

    public void setOriginalConstructor(Constructor originalConstructor) {
        this.originalConstructor = originalConstructor;
    }

    @Override
    public String toString() {
        return "ConstructorMetadata{" +
                "constructorArgumentValues=" + constructorArgumentValues +
                ", constructorAnnotations=" + constructorAnnotations +
                ", originalConstructor=" + originalConstructor +
                '}';
    }
}
