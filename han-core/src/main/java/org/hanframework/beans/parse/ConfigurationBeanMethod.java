package org.hanframework.beans.parse;

import org.hanframework.beans.beandefinition.ValueHolder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author liuxin
 * @version Id: ConfigurationBeanMethod.java, v 0.1 2019-01-30 10:42
 */
public class ConfigurationBeanMethod {

    private Method beanMethod;
    private Parameter[] parameters;
    private Class parentBeanCls;
    private List<ValueHolder> valueHolders;

    public ConfigurationBeanMethod(Method beanMethod, Parameter[] parameters, Class parentBeanCls, List<ValueHolder> valueHolders) {
        this.beanMethod = beanMethod;
        this.parameters = parameters;
        this.parentBeanCls = parentBeanCls;
        this.valueHolders = valueHolders;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public Class<?> getParentBeanCls() {
        return parentBeanCls;
    }

    public Method getBeanMethod() {
        return beanMethod;
    }

    public List<ValueHolder> getValueHolders() {
        return valueHolders;
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
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return null;
    }
}
