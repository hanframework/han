package org.hanframework.beans.beandefinition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author liuxin
 * @version Id: ConstructorArgumentValue.java, v 0.1 2018/10/26 4:29 PM
 */

public class ValueHolder {

    private Parameter parameter;
    /**
     * 构造参数类型
     */
    private final Class parameterType;
    /**
     * 构造参数索引
     */
    private final int sort;
    /**
     * 构造参数变量名
     */
    private final String varName;
    /**
     * 构造参数类型名称
     */
    private final String name;
    /**
     * 构造上参数的注解
     */
    private List<Annotation> parameterAnnotations;


    public ValueHolder(Class parameterType, int sort, String varName, String name, List<Annotation> parameterAnnotations, Parameter parameters) {
        this.parameterType = parameterType;
        this.sort = sort;
        this.varName = varName;
        this.name = name;
        this.parameterAnnotations = parameterAnnotations;
        this.parameter = parameters;
    }

    public Parameter getOriginParameter() {
        return parameter;
    }

    public Class<Object> getParameterType() {
        return parameterType;
    }

    public int getSort() {
        return sort;
    }


    public String getVarName() {
        return varName;
    }


    public String getName() {
        return name;
    }

    public List<Annotation> getParameterAnnotations() {
        return parameterAnnotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.parameterAnnotations = annotations;
    }

    @Override
    public String toString() {
        return "ConstructorArgumentValue{" +
                "parameterType=" + parameterType +
                ", sort=" + sort +
                ", varName='" + varName + '\'' +
                ", name='" + name + '\'' +
                ", parameterAnnotations=" + parameterAnnotations +
                '}';
    }
}
