package org.hanframework.beans.parse;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

/**
 * @author liuxin
 * @version Id: ConfigurationBeanMethod.java, v 0.1 2019-01-30 10:42
 */
public class ConfigurationBeanMethod {

    Method beanMethod;
    Parameter[] parameters;
    Class parentBeanCls;

    public ConfigurationBeanMethod(Method beanMethod, Parameter[] parameters, Class parentBeanCls) {
        this.beanMethod = beanMethod;
        this.parameters = parameters;
        this.parentBeanCls = parentBeanCls;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public Class getParentBeanCls() {
        return parentBeanCls;
    }

    public Method getBeanMethod() {
        return beanMethod;
    }

    /**
     * @param parentBean 本体实例对象
     * @param args       方法参数
     * @return
     */
    public Object invoke(Object parentBean, Object[] args) {
        int modifiers = beanMethod.getModifiers();
        boolean aPublic = Modifier.isPublic(modifiers);
        if (!aPublic) {
            beanMethod.setAccessible(true);
        }
        try {
            return beanMethod.invoke(parentBean, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
